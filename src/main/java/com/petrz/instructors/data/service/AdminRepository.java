package com.petrz.instructors.data.service;

import com.petrz.instructors.data.entity.Admin;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Integer> {

    Optional<Admin> findByEmailAndPwd(String email,String pwd);
    Optional<Admin> findByEmail(String email);
}
