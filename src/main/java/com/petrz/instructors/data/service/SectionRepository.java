package com.petrz.instructors.data.service;

import com.petrz.instructors.data.entity.Instructor;
import com.petrz.instructors.data.entity.Section;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SectionRepository extends JpaRepository<Section, Integer> {

    List<Section> findByInstructorId(Integer instructorId);
}
