package com.elktini.iotfleet.controller;

import com.elktini.iotfleet.model.Device;
import com.elktini.iotfleet.service.DeviceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/devices")
public class DeviceController {

    private final DeviceService deviceService;

    //Dependency injection via the constructor
    public DeviceController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    // Endpoint for listing all devices (GET /api/v1/devices)
    @GetMapping
    public List<Device> getAllDevices() {
        return deviceService.getAllDevices();
    }

    // Endpoint for registering a new device (POST /api/v1/devices)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Device createDevice(@Valid @RequestBody Device device) {
        // Store the object in the database and return it with its generated ID.
        return deviceService.createDevice(device);
    }

    @GetMapping("/{id}")
    public Device getDeviceById(@PathVariable Long id) {
        return deviceService.getDeviceById(id);
    }

    @GetMapping("/serial/{serialNumber}")
    public Device getDeviceBySerialNumber(@PathVariable String serialNumber) {
        return deviceService.getDeviceBySerialNumber(serialNumber);
    }

    @PutMapping("/{id}")
    public Device updateDevice(@PathVariable Long id, @Valid @RequestBody Device device) {
        return deviceService.updateDevice(id, device);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT) // Code 204
    public void deleteDevice(@PathVariable Long id) {
        deviceService.deleteDevice(id);
    }
}