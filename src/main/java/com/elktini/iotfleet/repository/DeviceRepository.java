package com.elktini.iotfleet.repository;

import com.elktini.iotfleet.model.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {
    Optional<Device> findBySerialNumber(String serialNumber);

    boolean existsBySerialNumber(String serialNumber);
}
