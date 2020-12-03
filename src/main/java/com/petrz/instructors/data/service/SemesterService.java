package com.petrz.instructors.data.service;

import com.petrz.instructors.data.entity.Semester;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.vaadin.artur.helpers.CrudService;

@Service
public class SemesterService extends CrudService<Semester, Integer> {

    private SemesterRepository repository;

    public SemesterService(@Autowired SemesterRepository repository) {
        this.repository = repository;
    }

    @Override
    protected SemesterRepository getRepository() {
        return repository;
    }

}