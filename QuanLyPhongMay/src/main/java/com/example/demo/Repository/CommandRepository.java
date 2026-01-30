package com.example.demo.Repository;

import java.util.Optional;

import org.hibernate.type.descriptor.converter.spi.JpaAttributeConverter;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.Model.Computer;
import com.example.demo.Model.command;

public interface CommandRepository extends  JpaRepository<command,Integer> {
    Optional<command> findByMaLenh(String malenh);
}
