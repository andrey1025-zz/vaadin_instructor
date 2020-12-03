package com.petrz.instructors.data.service;

import com.petrz.instructors.data.entity.StudentGradeList;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentGradeListRepository extends JpaRepository<StudentGradeList, Integer> {
    //StudentGradeList findBySectionIdAndSemesterId(Integer sectionId, Integer semesterId);
    StudentGradeList findBySectionId(Integer sectionId);
}
