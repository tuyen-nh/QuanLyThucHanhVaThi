package com.example.demo.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.Model.AiNotification;

@Repository
public interface AIRepo extends JpaRepository<AiNotification, Integer> {

}
