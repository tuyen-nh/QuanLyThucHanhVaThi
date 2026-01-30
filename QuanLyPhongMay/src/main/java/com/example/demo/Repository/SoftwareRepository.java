package com.example.demo.Repository;

import com.example.demo.Model.Software;
import com.example.demo.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SoftwareRepository extends JpaRepository<Software,Integer> {
    Optional<Software> findByNameSoftware(String nameSoftware);
}
