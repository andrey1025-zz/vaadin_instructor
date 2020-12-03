package com.petrz.instructors.data.service;

import com.petrz.instructors.data.entity.Instructor;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InstructorRepository extends JpaRepository<Instructor, Integer> {

    Optional<Instructor> findByEmail(String email);
}

