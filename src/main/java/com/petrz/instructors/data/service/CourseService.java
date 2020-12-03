package com.petrz.instructors.data.service;

import com.petrz.instructors.data.entity.Course;
import com.petrz.instructors.data.entity.Section;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.vaadin.artur.helpers.CrudService;

import java.util.List;

@Service
public class CourseService extends CrudService<Course, Integer> {

    private CourseRepository repository;

    public CourseService(@Autowired CourseRepository repository) {
        this.repository = repository;
    }

    @Override
    protected CourseRepository getRepository() {
        return repository;
    }

}
