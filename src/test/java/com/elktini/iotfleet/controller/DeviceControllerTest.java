package com.elktini.iotfleet.controller;

import com.elktini.iotfleet.exception.DeviceStateConflictException;
import com.elktini.iotfleet.exception.ResourceNotFoundException;
import com.elktini.iotfleet.model.Device;
import com.elktini.iotfleet.service.DeviceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

/**
 * Pure unit test: DeviceController is instantiated directly with a mocked
 * DeviceService, no Spring context is loaded. It only verifies delegation
 * and exception propagation between controller and service.
 *
 * NOT covered here (needs a Spring web-layer/slice test instead):
 * HTTP status codes, JSON (de)serialization, @Valid triggering validation
 * errors, and GlobalExceptionHandler mapping exceptions to HTTP responses.
 * Bean-validation constraints themselves are covered by DeviceValidationTest.
 */
@ExtendWith(MockitoExtension.class)
class DeviceControllerTest {

    @Mock
    private DeviceService deviceService;

    private DeviceController deviceController;

    @BeforeEach
    void setUp() {
        deviceController = new DeviceController(deviceService);
    }

    private Device buildDevice(Long id, String serialNumber, String status) {
        Device device = new Device();
        device.setId(id);
        device.setSerialNumber(serialNumber);
        device.setName("Test Device");
        device.setType("SENSOR");
        device.setStatus(status);
        return device;
    }

    @Test
    void getAllDevices_delegatesToServiceAndReturnsResult() {
        List<Device> devices = List.of(
                buildDevice(1L, "SN-AAA-111", "ONLINE"),
                buildDevice(2L, "SN-BBB-222", "OFFLINE")
        );
        when(deviceService.getAllDevices()).thenReturn(devices);

        List<Device> result = deviceController.getAllDevices();

        assertThat(result).isEqualTo(devices);
    }

    @Test
    void getDeviceById_whenFound_returnsDevice() {
        Device device = buildDevice(1L, "SN-AAA-111", "ONLINE");
        when(deviceService.getDeviceById(1L)).thenReturn(device);

        Device result = deviceController.getDeviceById(1L);

        assertThat(result).isEqualTo(device);
    }

    @Test
    void getDeviceById_whenNotFound_propagatesException() {
        when(deviceService.getDeviceById(99L))
                .thenThrow(new ResourceNotFoundException("Device not found with ID: 99"));

        assertThatThrownBy(() -> deviceController.getDeviceById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void getDeviceBySerialNumber_whenFound_returnsDevice() {
        Device device = buildDevice(1L, "SN-AAA-111", "ONLINE");
        when(deviceService.getDeviceBySerialNumber("SN-AAA-111")).thenReturn(device);

        Device result = deviceController.getDeviceBySerialNumber("SN-AAA-111");

        assertThat(result).isEqualTo(device);
    }

    @Test
    void getDeviceBySerialNumber_whenNotFound_propagatesException() {
        when(deviceService.getDeviceBySerialNumber("SN-ZZZ-999"))
                .thenThrow(new ResourceNotFoundException("Device not found with serialNumber: SN-ZZZ-999"));

        assertThatThrownBy(() -> deviceController.getDeviceBySerialNumber("SN-ZZZ-999"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void createDevice_delegatesToServiceAndReturnsCreatedDevice() {
        Device requestBody = buildDevice(null, "SN-AAA-111", "OFFLINE");
        Device saved = buildDevice(1L, "SN-AAA-111", "OFFLINE");
        when(deviceService.createDevice(requestBody)).thenReturn(saved);

        Device result = deviceController.createDevice(requestBody);

        assertThat(result).isEqualTo(saved);
    }

    @Test
    void createDevice_whenDuplicateSerialNumber_propagatesException() {
        Device requestBody = buildDevice(null, "SN-AAA-111", "OFFLINE");
        when(deviceService.createDevice(requestBody))
                .thenThrow(new DeviceStateConflictException("Device already exists with serialNumber: SN-AAA-111"));

        assertThatThrownBy(() -> deviceController.createDevice(requestBody))
                .isInstanceOf(DeviceStateConflictException.class)
                .hasMessageContaining("SN-AAA-111");
    }

    @Test
    void updateDevice_delegatesToServiceAndReturnsUpdatedDevice() {
        Device requestBody = buildDevice(null, "SN-AAA-111", "ONLINE");
        Device updated = buildDevice(1L, "SN-AAA-111", "ONLINE");
        when(deviceService.updateDevice(1L, requestBody)).thenReturn(updated);

        Device result = deviceController.updateDevice(1L, requestBody);

        assertThat(result).isEqualTo(updated);
    }

    @Test
    void updateDevice_whenNotFound_propagatesException() {
        Device requestBody = buildDevice(null, "SN-AAA-111", "ONLINE");
        when(deviceService.updateDevice(99L, requestBody))
                .thenThrow(new ResourceNotFoundException("Device not found with ID: 99"));

        assertThatThrownBy(() -> deviceController.updateDevice(99L, requestBody))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deleteDevice_whenAllowed_delegatesToService() {
        deviceController.deleteDevice(1L);

        verify(deviceService).deleteDevice(1L);
    }

    @Test
    void deleteDevice_whenDeviceOnline_propagatesException() {
        doThrow(new DeviceStateConflictException("Unable to delete currently ONLINE device SN-AAA-111 : Devices with ONLINE status cannot be removed"))
                .when(deviceService).deleteDevice(1L);

        assertThatThrownBy(() -> deviceController.deleteDevice(1L))
                .isInstanceOf(DeviceStateConflictException.class);
    }
}