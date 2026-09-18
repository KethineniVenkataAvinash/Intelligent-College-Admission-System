package com.college.admission.service;

import com.college.admission.jpa.HostelAllocationJpaEntity;
import com.college.admission.jpa.HostelJpaRepository;
import com.college.admission.model.HostelAllocation;
import com.college.admission.model.Student;
import com.college.admission.security.ServiceAuthorization;

public class HostelAllocationTransactionalService {

    private final HostelJpaRepository hostelRepository;

    public HostelAllocationTransactionalService() {

        this.hostelRepository =
                new HostelJpaRepository();
    }

    // ============================================================
    // TRANSACTION-SAFE ALLOCATION
    // ============================================================

    public HostelAllocation allocateHostel(
            Student student,
            String hostelCode,
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

        if (hostelCode == null
                || hostelCode.isBlank()) {

            throw new IllegalArgumentException(
                    "Hostel code cannot be empty."
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

        HostelAllocationJpaEntity entity =
                hostelRepository.allocateBedTransactionally(
                        student.getApplicationNumber(),
                        student.getName(),
                        normalizedRoomType,
                        hostelCode.trim()
                );

        return convertToModel(entity);
    }

    // ============================================================
    // CONVERSION
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
}