package com.college.admission.service;

import com.college.admission.security.ServiceAuthorization;
import com.college.admission.jpa.TransportAllocationJpaEntity;
import com.college.admission.jpa.TransportJpaRepository;
import com.college.admission.model.BusRoute;
import com.college.admission.model.Student;
import com.college.admission.model.TransportAllocation;

public class TransportAllocationTransactionalService {

    private final TransportJpaRepository
            transportRepository;

    private final TransportService
            transportService;

    public TransportAllocationTransactionalService() {

        transportRepository =
                new TransportJpaRepository();

        transportService =
                new TransportService();
    }

    // ============================================================
    // TRANSACTIONAL ALLOCATION
    // ============================================================

    public TransportAllocation allocateTransport(
            Student student,
            String routeCode,
            String boardingPoint
    ) {

        ServiceAuthorization.requireStudentOrAdminOwnership(student);

        validateStudent(student);

        validateRouteCode(routeCode);

        validateBoardingPoint(
                boardingPoint
        );

        BusRoute route =
                transportService.findRoute(
                        routeCode
                );

        if (route == null) {

            throw new IllegalArgumentException(
                    "Invalid bus route: "
                            + routeCode
            );
        }

        // --------------------------------------------------------
        // CHECK BOARDING POINT BEFORE DATABASE TRANSACTION
        // --------------------------------------------------------

        boolean validBoardingPoint =
                route.getStops()
                        .stream()
                        .anyMatch(
                                stop ->
                                        stop.equalsIgnoreCase(
                                                boardingPoint
                                        )
                        );

        if (!validBoardingPoint) {

            throw new IllegalArgumentException(
                    "Selected boarding point does not belong to this route."
            );
        }

        // --------------------------------------------------------
        // DATABASE TRANSACTION
        // --------------------------------------------------------

        TransportAllocationJpaEntity
                allocationEntity =
                transportRepository
                        .allocateSeatTransactionally(
                                student.getApplicationNumber(),
                                student.getName(),
                                routeCode,
                                boardingPoint
                        );

        return convertToModel(
                allocationEntity
        );
    }

    // ============================================================
    // VALIDATION
    // ============================================================

    private void validateStudent(
            Student student
    ) {

        if (student == null) {

            throw new IllegalArgumentException(
                    "Student cannot be null."
            );
        }

        if (student.getApplicationNumber() == null
                || student.getApplicationNumber().isBlank()) {

            throw new IllegalArgumentException(
                    "Student application number cannot be empty."
            );
        }

        if (student.getName() == null
                || student.getName().isBlank()) {

            throw new IllegalArgumentException(
                    "Student name cannot be empty."
            );
        }
    }

    private void validateRouteCode(
            String routeCode
    ) {

        if (routeCode == null
                || routeCode.isBlank()) {

            throw new IllegalArgumentException(
                    "Route code cannot be empty."
            );
        }
    }

    private void validateBoardingPoint(
            String boardingPoint
    ) {

        if (boardingPoint == null
                || boardingPoint.isBlank()) {

            throw new IllegalArgumentException(
                    "Boarding point cannot be empty."
            );
        }
    }

    // ============================================================
    // CONVERT JPA → MODEL
    // ============================================================

    private TransportAllocation convertToModel(
            TransportAllocationJpaEntity entity
    ) {

        return new TransportAllocation(
                entity.getApplicationNumber(),
                entity.getStudentName(),
                entity.getRouteCode(),
                entity.getRouteName(),
                entity.getBusNumber(),
                entity.getBusRegistrationNumber(),
                entity.getBoardingPoint(),
                entity.getBusSeatNumber()
        );
    }

    // ============================================================
    // FIND ALLOCATION
    // ============================================================

    public TransportAllocation getAllocation(
            String applicationNumber
    ) {

        if (applicationNumber == null
                || applicationNumber.isBlank()) {

            return null;
        }

        TransportAllocationJpaEntity entity =
                transportRepository
                        .findAllocationByApplication(
                                applicationNumber
                        );

        if (entity == null) {

            return null;
        }

        return convertToModel(entity);
    }

    // ============================================================
    // CHECK ALLOCATION
    // ============================================================

    public boolean hasAllocation(
            String applicationNumber
    ) {

        if (applicationNumber == null
                || applicationNumber.isBlank()) {

            return false;
        }

        return transportRepository
                .allocationExists(
                        applicationNumber
                );
    }

    // ============================================================
    // RELEASE TRANSPORT
    // ============================================================

    public void releaseTransport(
            String applicationNumber
    ) {

        if (applicationNumber == null
                || applicationNumber.isBlank()) {

            throw new IllegalArgumentException(
                    "Application number cannot be empty."
            );
        }

        if (!hasAllocation(
                applicationNumber
        )) {

            throw new IllegalStateException(
                    "No transport allocation exists for application "
                            + applicationNumber
            );
        }

        transportRepository.deleteAllocation(
                applicationNumber
        );
    }

    // ============================================================
    // REPOSITORY ACCESS
    // ============================================================

    public TransportJpaRepository
    getTransportRepository() {

        ServiceAuthorization.requireAdmin();

        return transportRepository;
    }
}