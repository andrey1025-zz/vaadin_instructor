package com.petrz.instructors.data.service;

import com.petrz.instructors.data.entity.StudentGradeList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.vaadin.artur.helpers.CrudService;


@Service
public class StudentGradeListService extends CrudService<StudentGradeList, Integer> {

    private StudentGradeListRepository repository;

    public StudentGradeListService(@Autowired StudentGradeListRepository repository) {
        this.repository = repository;
    }

    @Override
    protected StudentGradeListRepository getRepository() {
        return repository;
    }
/*
    public StudentGradeList findBySectionIdAndSemesterId(Integer sectionId, Integer semesterId) {
        return repository.findBySectionIdAndSemesterId(sectionId, semesterId);
    }*/
    public StudentGradeList findBySectionId(Integer sectionId) {
        return repository.findBySectionId(sectionId);
    }
}