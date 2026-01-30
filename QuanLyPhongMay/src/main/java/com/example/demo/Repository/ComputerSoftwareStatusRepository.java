package com.example.demo.Repository;

import com.example.demo.Model.Computer;
import com.example.demo.Model.ComputerSoftwareStatus;
import com.example.demo.Model.Software;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ComputerSoftwareStatusRepository extends JpaRepository<ComputerSoftwareStatus,Integer> {
    // Trong ComputerSoftwareStatusRepository
    // @Query("SELECT css FROM ComputerSoftwareStatus css WHERE css.computer.macAddress = :macAddress AND css.software.nameSoftware = :softwareName")
    // Optional<ComputerSoftwareStatus> findByComputerAndSoftwareName(@Param("macAddress") String macAddress, @Param("softwareName") String softwareName);
    Optional<ComputerSoftwareStatus> findByComputerAndSoftware(Computer computer, Software software);


    List<ComputerSoftwareStatus> findByComputer(Computer computer);

}
