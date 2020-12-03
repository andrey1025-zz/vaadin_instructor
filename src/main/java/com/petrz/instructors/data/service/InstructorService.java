package com.petrz.instructors.data.service;

import com.petrz.instructors.data.entity.Instructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.vaadin.artur.helpers.CrudService;

import java.util.Optional;

@Service
public class InstructorService extends CrudService<Instructor, Integer> {

    private InstructorRepository repository;

    public InstructorService(@Autowired InstructorRepository repository) {
        this.repository = repository;
    }

    @Override
    protected InstructorRepository getRepository() {
        return repository;
    }

    public Optional<Instructor> findByEmail(String email) {
        return this.getRepository().findByEmail(email);
    }

}
