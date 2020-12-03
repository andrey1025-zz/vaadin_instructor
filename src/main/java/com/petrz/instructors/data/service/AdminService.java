package com.petrz.instructors.data.service;

import com.petrz.instructors.data.entity.Admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.vaadin.artur.helpers.CrudService;
import java.util.Optional;

@Service
public class AdminService extends CrudService<Admin, Integer> {

    private AdminRepository repository;

    public AdminService(@Autowired AdminRepository repository) {
        this.repository = repository;
    }

    @Override
    protected AdminRepository getRepository() {
        return repository;
    }

    public Optional<Admin> findByEmailAndPwd(String email,String pwd) {
        return this.getRepository().findByEmailAndPwd(email,pwd);
    }

    public Optional<Admin> findByEmail(String email) {
        return this.getRepository().findByEmail(email);
    }

    /**
     * insert an Admin into the database to be able to login into the app as the "first" admin
     * @param email
     * @param pwd   password
     * @param name
     */
    public void initData(String email,String pwd,String name) {
        if( !findByEmail(email).isPresent() ) {
            Admin admin = new Admin();
            admin.setEmail(email);
            admin.setPwd(pwd);
            admin.setName(name);
            this.getRepository().save(admin);
        }
    }
}
