package com.petrz.instructors.data.service;

import com.petrz.instructors.data.entity.Course;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Integer> {

}
