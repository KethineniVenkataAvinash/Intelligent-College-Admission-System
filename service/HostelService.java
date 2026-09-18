package com.college.admission.service;

import com.college.admission.enums.Gender;
import com.college.admission.jpa.HostelAllocationJpaEntity;
import com.college.admission.jpa.HostelJpaEntity;
import com.college.admission.jpa.HostelJpaRepository;
import com.college.admission.jpa.HostelRoomJpaEntity;
import com.college.admission.model.Hostel;
import com.college.admission.model.HostelAllocation;
import com.college.admission.model.HostelRoom;
import com.college.admission.model.Student;
import com.college.admission.security.ServiceAuthorization;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HostelService {

    private final List<Hostel> hostels;

    /*
     * Compatibility cache.
     *
     * The database remains the persistent source of truth.
     */
    private final Map<String, HostelAllocation> allocations;

    private final HostelJpaRepository hostelRepository;

    // ============================================================
    // CONSTRUCTOR
    // ============================================================

    public HostelService() {

        this.hostels = new ArrayList<>();

        this.allocations = new HashMap<>();

        this.hostelRepository = new HostelJpaRepository();

        initializeHostels();

        loadAllocationsFromDatabase();
    }

    // ============================================================
    // INITIALIZATION
    // ============================================================

    private void initializeHostels() {

    List<HostelJpaEntity> existingHostels =
            hostelRepository.findAllHostels();

    /*
     * No hostels exist at all.
     * Create the complete default hostel structure.
     */
    if (existingHostels == null
            || existingHostels.isEmpty()) {

        createDefaultHostels();

        return;
    }

    /*
     * Hostels already exist.
     *
     * However, a previous initialization may have created
     * the hostel records but failed before creating rooms.
     *
     * Therefore, verify every default hostel individually.
     */
    ensureHostelWithRooms(
            "BH01",
            "Boys Hostel A",
            Gender.MALE,
            "Block A",
            "A",
            101
    );

    ensureHostelWithRooms(
            "BH02",
            "Boys Hostel B",
            Gender.MALE,
            "Block B",
            "B",
            201
    );

    ensureHostelWithRooms(
            "GH01",
            "Girls Hostel A",
            Gender.FEMALE,
            "Block A",
            "A",
            101
    );

    ensureHostelWithRooms(
            "GH02",
            "Girls Hostel B",
            Gender.FEMALE,
            "Block B",
            "B",
            201
    );

    loadHostelsFromDatabase();
}

private void ensureHostelWithRooms(
        String hostelCode,
        String hostelName,
        Gender gender,
        String blockName,
        String blockPrefix,
        int startingRoomNumber
) {

    HostelJpaEntity hostelEntity =
            hostelRepository.findHostelByCode(hostelCode);

    /*
     * ------------------------------------------------------------
     * HOSTEL DOES NOT EXIST
     * ------------------------------------------------------------
     */
    if (hostelEntity == null) {

        createHostelWithRooms(
                hostelCode,
                hostelName,
                gender,
                blockName,
                blockPrefix,
                startingRoomNumber
        );

        return;
    }

    /*
     * ------------------------------------------------------------
     * HOSTEL EXISTS
     *
     * Check whether rooms were successfully created.
     * ------------------------------------------------------------
     */
    List<HostelRoomJpaEntity> rooms =
            hostelRepository.findRoomsByHostelCode(
                    hostelCode
            );

    if (rooms == null || rooms.isEmpty()) {

        System.out.println(
                "Rooms missing for hostel "
                        + hostelCode
                        + ". Creating default rooms..."
        );

        createRoomsForExistingHostel(
                hostelEntity,
                blockPrefix,
                startingRoomNumber
        );

    } else {

        System.out.println(
                "Hostel "
                        + hostelCode
                        + " already has "
                        + rooms.size()
                        + " rooms."
        );
    }
}

private void createRoomsForExistingHostel(
        HostelJpaEntity hostelEntity,
        String blockPrefix,
        int startingRoomNumber
) {

    for (int i = 0; i < 5; i++) {

        int roomNumber =
                startingRoomNumber + i;

        String roomCode =
                blockPrefix + "-" + roomNumber;

        String roomType;
        int capacity;

        if (i < 2) {

            roomType = "SINGLE";
            capacity = 1;

        } else if (i < 4) {

            roomType = "DOUBLE";
            capacity = 2;

        } else {

            roomType = "TRIPLE";
            capacity = 3;
        }

        HostelRoomJpaEntity roomEntity =
                new HostelRoomJpaEntity(
                        roomCode,
                        roomType,
                        capacity,
                        hostelEntity
                );

        hostelRepository.saveRoom(roomEntity);
    }

    System.out.println(
            "5 rooms created for hostel "
                    + hostelEntity.getHostelCode()
    );
}


    private void createDefaultHostels() {

        createHostelWithRooms(
                "BH01",
                "Boys Hostel A",
                Gender.MALE,
                "Block A",
                "A",
                101
        );

        createHostelWithRooms(
                "BH02",
                "Boys Hostel B",
                Gender.MALE,
                "Block B",
                "B",
                201
        );

        createHostelWithRooms(
                "GH01",
                "Girls Hostel A",
                Gender.FEMALE,
                "Block A",
                "A",
                101
        );

        createHostelWithRooms(
                "GH02",
                "Girls Hostel B",
                Gender.FEMALE,
                "Block B",
                "B",
                201
        );

        loadHostelsFromDatabase();
    }

    private void createHostelWithRooms(
        String hostelCode,
        String hostelName,
        Gender gender,
        String blockName,
        String blockPrefix,
        int startingRoomNumber
) {

    HostelJpaEntity hostelEntity =
            new HostelJpaEntity(
                    hostelCode,
                    hostelName,
                    gender,
                    blockName
            );

    /*
     * ------------------------------------------------------------
     * STEP 1: Save the hostel first.
     * ------------------------------------------------------------
     */
    hostelRepository.saveHostel(hostelEntity);

    /*
     * ------------------------------------------------------------
     * STEP 2: Reload the hostel from the database.
     *
     * This is important because saveHostel() uses merge()
     * in a separate EntityManager transaction.
     *
     * We must use the persistent database entity when creating
     * the rooms.
     * ------------------------------------------------------------
     */
    HostelJpaEntity savedHostel =
            hostelRepository.findHostelByCode(
                    hostelCode
            );

    if (savedHostel == null) {

        throw new IllegalStateException(
                "Failed to create hostel: "
                        + hostelCode
        );
    }

    /*
     * ------------------------------------------------------------
     * STEP 3: Create the five default rooms.
     * ------------------------------------------------------------
     */
    for (int i = 0; i < 5; i++) {

        int roomNumber =
                startingRoomNumber + i;

        String roomCode =
                blockPrefix + "-" + roomNumber;

        String roomType;
        int capacity;

        if (i < 2) {

            roomType = "SINGLE";
            capacity = 1;

        } else if (i < 4) {

            roomType = "DOUBLE";
            capacity = 2;

        } else {

            roomType = "TRIPLE";
            capacity = 3;
        }

        HostelRoomJpaEntity roomEntity =
                new HostelRoomJpaEntity(
                        roomCode,
                        roomType,
                        capacity,
                        savedHostel
                );

        hostelRepository.saveRoom(roomEntity);
    }

    System.out.println(
            "5 rooms created for hostel "
                    + hostelCode
    );
}

    // ============================================================
    // LOAD HOSTELS FROM DATABASE
    // ============================================================

    private void loadHostelsFromDatabase() {

        hostels.clear();

        List<HostelJpaEntity> hostelEntities =
                hostelRepository.findAllHostels();

        List<HostelAllocationJpaEntity> allAllocations =
                hostelRepository.findAllAllocations();

        for (HostelJpaEntity entity : hostelEntities) {

            Hostel hostel =
                    new Hostel(
                            entity.getHostelCode(),
                            entity.getHostelName(),
                            entity.getGender(),
                            entity.getBlockName()
                    );

            List<HostelRoomJpaEntity> rooms =
                    hostelRepository.findRoomsByHostelCode(
                            entity.getHostelCode()
                    );

            for (HostelRoomJpaEntity roomEntity : rooms) {

                HostelRoom room =
                        new HostelRoom(
                                roomEntity.getRoomNumber(),
                                roomEntity.getRoomType(),
                                roomEntity.getCapacity()
                        );

                /*
                 * Restore occupied beds from the database.
                 *
                 * Match using room ID instead of only room number.
                 * This avoids collisions between rooms such as
                 * A-101 in different hostels.
                 */
                for (HostelAllocationJpaEntity allocation :
                        allAllocations) {

                    if (allocation.getRoom() == null) {
                        continue;
                    }

                    if (!allocation.getRoom()
                            .getRoomId()
                            .equals(roomEntity.getRoomId())) {

                        continue;
                    }

                    room.allocateBed(
                            allocation.getApplicationNumber()
                    );
                }

                hostel.addRoom(room);
            }

            hostels.add(hostel);
        }
    }

    // ============================================================
    // LOAD ALLOCATIONS FROM DATABASE
    // ============================================================

    private void loadAllocationsFromDatabase() {

        allocations.clear();

        List<HostelAllocationJpaEntity> entities =
                hostelRepository.findAllAllocations();

        for (HostelAllocationJpaEntity entity : entities) {

            HostelAllocation allocation =
                    convertToModel(entity);

            allocations.put(
                    allocation.getApplicationNumber(),
                    allocation
            );
        }
    }

    // ============================================================
    // ENTITY -> MODEL
    // ============================================================

    private HostelAllocation convertToModel(
            HostelAllocationJpaEntity entity
    ) {

        return new HostelAllocation(
                entity.getApplicationNumber(),
                entity.getStudentName(),
                entity.getHostelCode(),
                entity.getHostelName(),
                entity.getBlockName(),
                entity.getRoomNumber(),
                entity.getRoomType(),
                entity.getBedNumber()
        );
    }

    // ============================================================
    // HOSTEL LOOKUP
    // ============================================================

    public List<Hostel> getHostels() {

        loadHostelsFromDatabase();

        return new ArrayList<>(hostels);
    }

    public Hostel findHostel(String hostelCode) {

        if (hostelCode == null
                || hostelCode.isBlank()) {

            return null;
        }

        for (Hostel hostel : hostels) {

            if (hostel.getHostelCode()
                    .equalsIgnoreCase(
                            hostelCode.trim()
                    )) {

                return hostel;
            }
        }

        /*
         * Refresh once in case the database was changed
         * after this service was created.
         */
        loadHostelsFromDatabase();

        for (Hostel hostel : hostels) {

            if (hostel.getHostelCode()
                    .equalsIgnoreCase(
                            hostelCode.trim()
                    )) {

                return hostel;
            }
        }

        return null;
    }

    public List<Hostel> getHostelsForGender(
            Gender gender
    ) {

        List<Hostel> matchingHostels =
                new ArrayList<>();

        if (gender == null) {
            return matchingHostels;
        }

        loadHostelsFromDatabase();

        for (Hostel hostel : hostels) {

            if (hostel.getGender() == gender) {

                matchingHostels.add(hostel);
            }
        }

        return matchingHostels;
    }

    // ============================================================
    // DISPLAY AVAILABLE HOSTELS
    // ============================================================

    public void displayAvailableHostels(
            Gender gender
    ) {

        List<Hostel> matchingHostels =
                getHostelsForGender(gender);

        System.out.println();

        System.out.println(
                "================================================================"
        );

        System.out.println(
                "                    HOSTEL AVAILABILITY"
        );

        System.out.println(
                "================================================================"
        );

        if (matchingHostels.isEmpty()) {

            System.out.println(
                    "No hostels available for the selected gender."
            );

            System.out.println(
                    "================================================================"
            );

            return;
        }

        System.out.printf(
                "%-10s %-22s %-12s %-12s %-15s%n",
                "Code",
                "Hostel",
                "Block",
                "Occupied",
                "Available"
        );

        System.out.println(
                "----------------------------------------------------------------"
        );

        for (Hostel hostel : matchingHostels) {

            System.out.printf(
                    "%-10s %-22s %-12s %-12d %-15d%n",
                    hostel.getHostelCode(),
                    hostel.getHostelName(),
                    hostel.getBlockName(),
                    hostel.getOccupiedCapacity(),
                    hostel.getAvailableCapacity()
            );
        }

        System.out.println(
                "================================================================"
        );
    }

    // ============================================================
    // DISPLAY ROOMS
    // ============================================================

    public void displayRooms(
            String hostelCode
    ) {

        Hostel hostel =
                findHostel(hostelCode);

        if (hostel == null) {

            System.out.println(
                    "Hostel not found."
            );

            return;
        }

        System.out.println();

        System.out.println(
                "================================================================"
        );

        System.out.println(
                "                    HOSTEL ROOM DETAILS"
        );

        System.out.println(
                "================================================================"
        );

        System.out.println(
                "Hostel : " + hostel.getHostelName()
        );

        System.out.println(
                "Block  : " + hostel.getBlockName()
        );

        System.out.println();

        for (HostelRoom room : hostel.getRooms()) {

            room.displayRoom();
        }

        System.out.println(
                "================================================================"
        );
    }

    // ============================================================
    // HOSTEL ALLOCATION
    // ============================================================

    public HostelAllocation allocateHostel(
            Student student,
            String roomType
    ) {

        ServiceAuthorization.requireStudentOrAdminOwnership(student);

        if (student == null) {

            throw new IllegalArgumentException(
                    "Student cannot be null."
            );
        }

        if (student.getApplicationNumber() == null
                || student.getApplicationNumber().isBlank()) {

            throw new IllegalArgumentException(
                    "Student application number is required."
            );
        }

        if (student.getName() == null
                || student.getName().isBlank()) {

            throw new IllegalArgumentException(
                    "Student name is required."
            );
        }

        if (student.getGender() == null) {

            throw new IllegalArgumentException(
                    "Student gender is required for hostel allocation."
            );
        }

        if (student.getRank() == null) {

            throw new IllegalStateException(
                    "Student rank must be generated before hostel allocation."
            );
        }

        if (roomType == null
                || roomType.isBlank()) {

            throw new IllegalArgumentException(
                    "Room type cannot be empty."
            );
        }

        String normalizedRoomType =
                roomType.trim().toUpperCase();

        if (!normalizedRoomType.equals("SINGLE")
                && !normalizedRoomType.equals("DOUBLE")
                && !normalizedRoomType.equals("TRIPLE")) {

            throw new IllegalArgumentException(
                    "Room type must be SINGLE, DOUBLE or TRIPLE."
            );
        }

        String applicationNumber =
                student.getApplicationNumber().trim();

        /*
         * ========================================================
         * FIRST: CHECK DATABASE
         * ========================================================
         *
         * Database is authoritative.
         */
        HostelAllocationJpaEntity existingEntity =
                hostelRepository.findAllocationByApplication(
                        applicationNumber
                );

        if (existingEntity != null) {

            HostelAllocation existing =
                    convertToModel(existingEntity);

            allocations.put(
                    applicationNumber,
                    existing
            );

            return existing;
        }

        /*
         * Compatibility cache check.
         */
        HostelAllocation cached =
                allocations.get(applicationNumber);

        if (cached != null) {

            return cached;
        }

        /*
         * ========================================================
         * CHECK GENDER HOSTEL AVAILABILITY
         * ========================================================
         */
        List<Hostel> matchingHostels =
                getHostelsForGender(
                        student.getGender()
                );

        if (matchingHostels.isEmpty()) {

            throw new IllegalStateException(
                    "No hostel is available for the student's gender."
            );
        }

        /*
         * Make sure at least one room of the requested type
         * exists before starting the transaction.
         */
        boolean roomTypeExists = false;

        for (Hostel hostel : matchingHostels) {

            for (HostelRoom room : hostel.getRooms()) {

                if (room.getRoomType()
                        .equalsIgnoreCase(
                                normalizedRoomType
                        )) {

                    roomTypeExists = true;
                    break;
                }
            }

            if (roomTypeExists) {
                break;
            }
        }

        if (!roomTypeExists) {

            throw new IllegalStateException(
                    "No "
                            + normalizedRoomType
                            + " room exists for the student's gender."
            );
        }

        /*
         * ========================================================
         * TRANSACTION-SAFE DATABASE ALLOCATION
         * ========================================================
         *
         * The repository:
         *
         * 1. starts a transaction
         * 2. checks duplicate application
         * 3. finds eligible hostel
         * 4. locks rooms
         * 5. checks capacity
         * 6. selects next available bed
         * 7. persists allocation
         * 8. commits
         *
         * This prevents duplicate beds during concurrent allocation.
         */
        HostelAllocationJpaEntity allocationEntity =
                hostelRepository.allocateBedTransactionally(
                        applicationNumber,
                        student.getName().trim(),
                        normalizedRoomType,
                        findFirstAvailableHostelCode(
                                matchingHostels,
                                normalizedRoomType
                        )
                );

        if (allocationEntity == null) {

            /*
             * The transaction-safe repository may return null
             * when no suitable bed is available.
             */
            throw new IllegalStateException(
                    "No available "
                            + normalizedRoomType
                            + " hostel room is available."
            );
        }

        /*
         * Convert persistent entity into application model.
         */
        HostelAllocation allocation =
                convertToModel(allocationEntity);

        /*
         * Update compatibility cache.
         */
        allocations.put(
                applicationNumber,
                allocation
        );

        /*
         * Refresh in-memory hostel representation so that
         * display methods immediately reflect the allocation.
         */
        loadHostelsFromDatabase();

        return allocation;
    }

    // ============================================================
    // FIND FIRST SUITABLE HOSTEL
    // ============================================================

    private String findFirstAvailableHostelCode(
            List<Hostel> matchingHostels,
            String roomType
    ) {

        for (Hostel hostel : matchingHostels) {

            for (HostelRoom room : hostel.getRooms()) {

                if (!room.getRoomType()
                        .equalsIgnoreCase(roomType)) {

                    continue;
                }

                if (room.hasAvailableBed()) {

                    return hostel.getHostelCode();
                }
            }
        }

        /*
         * Returning the first matching hostel allows the repository
         * to perform the final locked availability check.
         *
         * This is important because availability may change between
         * this read and the transactional allocation.
         */
        for (Hostel hostel : matchingHostels) {

            for (HostelRoom room : hostel.getRooms()) {

                if (room.getRoomType()
                        .equalsIgnoreCase(roomType)) {

                    return hostel.getHostelCode();
                }
            }
        }

        return null;
    }

    // ============================================================
    // RANK-BASED HOSTEL ALLOCATION
    // ============================================================

    public void allocateHostelByRank(
            List<Student> students,
            String roomType
    ) {

        ServiceAuthorization.requireAdmin();

        if (students == null
                || students.isEmpty()) {

            System.out.println(
                    "No students available for hostel allocation."
            );

            return;
        }

        if (roomType == null
                || roomType.isBlank()) {

            System.out.println(
                    "Room type cannot be empty."
            );

            return;
        }

        List<Student> rankedStudents =
                new ArrayList<>();

        for (Student student : students) {

            if (student == null) {
                continue;
            }

            if (student.getRank() == null) {
                continue;
            }

            rankedStudents.add(student);
        }

        rankedStudents.sort(
                Comparator
                        .comparingInt(
                                Student::getRank
                        )
                        .thenComparing(
                                Student::getApplicationNumber,
                                Comparator.nullsLast(
                                        String::compareTo
                                )
                        )
        );

        for (Student student : rankedStudents) {

            String applicationNumber =
                    student.getApplicationNumber();

            if (applicationNumber == null
                    || applicationNumber.isBlank()) {

                continue;
            }

            if (hasAllocation(applicationNumber)) {

                continue;
            }

            try {

                HostelAllocation allocation =
                        allocateHostel(
                                student,
                                roomType
                        );

                System.out.println();

                System.out.println(
                        "Hostel allocated successfully."
                );

                System.out.println(
                        "Student       : "
                                + student.getName()
                );

                System.out.println(
                        "Application No.: "
                                + student.getApplicationNumber()
                );

                System.out.println(
                        "Rank          : "
                                + student.getRank()
                );

                System.out.println(
                        "Hostel        : "
                                + allocation.getHostelName()
                );

                System.out.println(
                        "Room          : "
                                + allocation.getRoomNumber()
                );

                System.out.println(
                        "Room Type     : "
                                + allocation.getRoomType()
                );

                System.out.println(
                        "Bed           : "
                                + allocation.getBedNumber()
                );

            } catch (IllegalStateException e) {

                System.out.println();

                System.out.println(
                        "Hostel allocation failed."
                );

                System.out.println(
                        "Student : "
                                + student.getName()
                );

                System.out.println(
                        "Rank    : "
                                + student.getRank()
                );

                System.out.println(
                        "Reason  : "
                                + e.getMessage()
                );
            }
        }
    }

    // ============================================================
    // ALLOCATION LOOKUP
    // ============================================================

    public HostelAllocation getAllocation(
            String applicationNumber
    ) {

        ServiceAuthorization.requireApplicationOwnership(
                applicationNumber
        );

        if (applicationNumber == null
                || applicationNumber.isBlank()) {

            return null;
        }

        String normalizedApplicationNumber =
                applicationNumber.trim();

        /*
         * Always check database first.
         */
        HostelAllocationJpaEntity entity =
                hostelRepository.findAllocationByApplication(
                        normalizedApplicationNumber
                );

        if (entity != null) {

            HostelAllocation allocation =
                    convertToModel(entity);

            allocations.put(
                    normalizedApplicationNumber,
                    allocation
            );

            return allocation;
        }

        /*
         * Compatibility fallback.
         */
        return allocations.get(
                normalizedApplicationNumber
        );
    }

    public boolean hasAllocation(
            String applicationNumber
    ) {

        ServiceAuthorization.requireApplicationOwnership(
                applicationNumber
        );

        if (applicationNumber == null
                || applicationNumber.isBlank()) {

            return false;
        }

        return hostelRepository.allocationExists(
                applicationNumber.trim()
        );
    }

    // ============================================================
    // DISPLAY STUDENT ALLOCATION
    // ============================================================

    public void displayStudentAllocation(
            Student student
    ) {

        ServiceAuthorization.requireStudentOwnership(student);

        if (student == null) {

            System.out.println(
                    "Student not found."
            );

            return;
        }

        HostelAllocation allocation =
                getAllocation(
                        student.getApplicationNumber()
                );

        System.out.println();

        System.out.println(
                "=============================================================="
        );

        System.out.println(
                "                    HOSTEL DETAILS"
        );

        System.out.println(
                "=============================================================="
        );

        System.out.println(
                "Student : "
                        + student.getName()
        );

        System.out.println(
                "Application Number : "
                        + student.getApplicationNumber()
        );

        if (allocation == null) {

            System.out.println();

            System.out.println(
                    "Hostel has not been allocated."
            );

            System.out.println(
                    "=============================================================="
            );

            return;
        }

        allocation.displayAllocation();

        System.out.println(
                "=============================================================="
        );
    }

    // ============================================================
    // DISPLAY ALL ALLOCATIONS
    // ============================================================

    public void displayAllAllocations() {

        ServiceAuthorization.requireAdmin();

        loadAllocationsFromDatabase();

        System.out.println();

        System.out.println(
                "=========================================================================="
        );

        System.out.println(
                "                       HOSTEL ALLOCATIONS"
        );

        System.out.println(
                "=========================================================================="
        );

        if (allocations.isEmpty()) {

            System.out.println(
                    "No hostel allocations found."
            );

            System.out.println(
                    "=========================================================================="
            );

            return;
        }

        System.out.printf(
                "%-20s %-20s %-10s %-12s %-10s %-8s%n",
                "Application",
                "Student",
                "Hostel",
                "Block",
                "Room",
                "Bed"
        );

        System.out.println(
                "--------------------------------------------------------------------------"
        );

        List<HostelAllocation> sortedAllocations =
                new ArrayList<>(
                        allocations.values()
                );

        sortedAllocations.sort(
                Comparator.comparing(
                        HostelAllocation::getApplicationNumber,
                        Comparator.nullsLast(
                                String::compareTo
                        )
                )
        );

        for (HostelAllocation allocation :
                sortedAllocations) {

            System.out.printf(
                    "%-20s %-20s %-10s %-12s %-10s %-8d%n",
                    allocation.getApplicationNumber(),
                    allocation.getStudentName(),
                    allocation.getHostelCode(),
                    allocation.getBlockName(),
                    allocation.getRoomNumber(),
                    allocation.getBedNumber()
            );
        }

        System.out.println(
                "=========================================================================="
        );
    }

    // ============================================================
    // HOSTEL OCCUPANCY
    // ============================================================

    public void displayHostelOccupancy() {

        ServiceAuthorization.requireAdmin();

        loadHostelsFromDatabase();

        System.out.println();

        System.out.println(
                "=========================================================================="
        );

        System.out.println(
                "                        HOSTEL OCCUPANCY"
        );

        System.out.println(
                "=========================================================================="
        );

        if (hostels.isEmpty()) {

            System.out.println(
                    "No hostel data found."
            );

            System.out.println(
                    "=========================================================================="
            );

            return;
        }

        for (Hostel hostel : hostels) {

            System.out.println();

            System.out.println(
                    "Hostel Code    : "
                            + hostel.getHostelCode()
            );

            System.out.println(
                    "Hostel Name    : "
                            + hostel.getHostelName()
            );

            System.out.println(
                    "Gender         : "
                            + hostel.getGender()
            );

            System.out.println(
                    "Block          : "
                            + hostel.getBlockName()
            );

            System.out.println(
                    "Total Capacity : "
                            + hostel.getTotalCapacity()
            );

            System.out.println(
                    "Occupied Beds  : "
                            + hostel.getOccupiedCapacity()
            );

            System.out.println(
                    "Available Beds : "
                            + hostel.getAvailableCapacity()
            );

            System.out.println(
                    "------------------------------------------------------------"
            );
        }

        System.out.println(
                "=========================================================================="
        );
    }

    // ============================================================
    // COMPATIBILITY ACCESS
    // ============================================================

    public Map<String, HostelAllocation> getAllocations() {

        ServiceAuthorization.requireAdmin();

        loadAllocationsFromDatabase();

        return new HashMap<>(allocations);
    }

    public HostelJpaRepository getHostelRepository() {

        ServiceAuthorization.requireAdmin();

        return hostelRepository;
    }
}