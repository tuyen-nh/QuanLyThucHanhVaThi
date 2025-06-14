package com.example.demo.Repository;

import com.example.demo.InterFace.infoDashBoard;
import com.example.demo.Model.Computer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ComputerRepository extends JpaRepository<Computer,Integer> {
    Optional<Computer> findByMacAddress(String macAddress);

}
