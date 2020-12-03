package com.petrz.instructors.data.service;

import com.petrz.instructors.data.entity.Semester;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SemesterRepository extends JpaRepository<Semester, Integer> {
}