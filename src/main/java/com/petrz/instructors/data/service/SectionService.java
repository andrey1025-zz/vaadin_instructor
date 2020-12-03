package com.petrz.instructors.data.service;

import com.petrz.instructors.data.entity.Course;
import com.petrz.instructors.data.entity.Instructor;
import com.petrz.instructors.data.entity.Section;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.vaadin.artur.helpers.CrudService;

import java.util.List;

@Service
public class SectionService extends CrudService<Section, Integer> {

    private SectionRepository repository;

    public SectionService(@Autowired SectionRepository repository) {
        this.repository = repository;
    }

    @Override
    protected SectionRepository getRepository() {
        return repository;
    }

    public List<Section> findByInstructorId(Integer instructorId) {
        return this.repository.findByInstructorId(instructorId);
    }

}
