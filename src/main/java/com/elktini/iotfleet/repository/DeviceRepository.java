package com.elktini.iotfleet.repository;

import com.elktini.iotfleet.model.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {
    // Juste en faisant ça, Spring génère automatiquement les méthodes :
    // save(), findAll(), findById(), deleteById()
}
