package com.elktini.iotfleet.service;

import com.elktini.iotfleet.exception.ResourceNotFoundException;
import com.elktini.iotfleet.model.Device;
import com.elktini.iotfleet.repository.DeviceRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DeviceService {

    private final DeviceRepository deviceRepository;

    // Repository Injection into the Service
    public DeviceService(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    public List<Device> getAllDevices() {
        return deviceRepository.findAll();
    }

    // GET
    public Device getDeviceById(Long id) {
        return deviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Device not found with ID: " + id));
    }

    // POST
    public Device createDevice(Device device) {

        // If no status is provided, it is set to OFFLINE by default
        if (device.getStatus() == null || device.getStatus().isEmpty()) {
            device.setStatus("OFFLINE");
        }

        // We save to the database
        return deviceRepository.save(device);
    }

    // UPDATE (PUT)
    public Device updateDevice(Long id, Device updatedDevice) {
        Device existingDevice = getDeviceById(id);

        existingDevice.setName(updatedDevice.getName());
        existingDevice.setType(updatedDevice.getType());
        existingDevice.setStatus(updatedDevice.getStatus());

        return deviceRepository.save(existingDevice);
    }

    // DELETE
    public void deleteDevice(Long id) {
        getDeviceById(id);
        deviceRepository.deleteById(id);
    }
}
