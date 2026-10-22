package com.elktini.iotfleet.service;

import com.elktini.iotfleet.exception.DeviceStateConflictException;
import com.elktini.iotfleet.exception.ResourceNotFoundException;
import com.elktini.iotfleet.model.Device;
import com.elktini.iotfleet.repository.DeviceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeviceServiceTest {

    @Mock
    private DeviceRepository deviceRepository;

    private DeviceService deviceService;

    @BeforeEach
    void setUp() {
        deviceService = new DeviceService(deviceRepository);
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

    // --- getAllDevices ---

    @Test
    void getAllDevices_returnsAllDevicesFromRepository() {
        List<Device> devices = List.of(
                buildDevice(1L, "SN-AAA-111", "ONLINE"),
                buildDevice(2L, "SN-BBB-222", "OFFLINE")
        );
        when(deviceRepository.findAll()).thenReturn(devices);

        List<Device> result = deviceService.getAllDevices();

        assertThat(result).hasSize(2).isEqualTo(devices);
    }

    // --- getDeviceById ---

    @Test
    void getDeviceById_whenFound_returnsDevice() {
        Device device = buildDevice(1L, "SN-AAA-111", "ONLINE");
        when(deviceRepository.findById(1L)).thenReturn(Optional.of(device));

        Device result = deviceService.getDeviceById(1L);

        assertThat(result).isEqualTo(device);
    }

    @Test
    void getDeviceById_whenNotFound_throwsResourceNotFoundException() {
        when(deviceRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> deviceService.getDeviceById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    // --- getDeviceBySerialNumber ---

    @Test
    void getDeviceBySerialNumber_whenFound_returnsDevice() {
        Device device = buildDevice(1L, "SN-AAA-111", "ONLINE");
        when(deviceRepository.findBySerialNumber("SN-AAA-111")).thenReturn(Optional.of(device));

        Device result = deviceService.getDeviceBySerialNumber("SN-AAA-111");

        assertThat(result).isEqualTo(device);
    }

    @Test
    void getDeviceBySerialNumber_whenNotFound_throwsResourceNotFoundException() {
        when(deviceRepository.findBySerialNumber("SN-ZZZ-999")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> deviceService.getDeviceBySerialNumber("SN-ZZZ-999"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("SN-ZZZ-999");
    }

    // --- createDevice ---

    @Test
    void createDevice_whenSerialNumberAlreadyExists_throwsConflictAndDoesNotSave() {
        Device device = buildDevice(null, "SN-AAA-111", "OFFLINE");
        when(deviceRepository.existsBySerialNumber("SN-AAA-111")).thenReturn(true);

        assertThatThrownBy(() -> deviceService.createDevice(device))
                .isInstanceOf(DeviceStateConflictException.class)
                .hasMessageContaining("SN-AAA-111");

        verify(deviceRepository, never()).save(any());
    }

    @Test
    void createDevice_whenStatusIsNull_defaultsToOffline() {
        Device device = buildDevice(null, "SN-AAA-111", null);
        when(deviceRepository.existsBySerialNumber("SN-AAA-111")).thenReturn(false);
        when(deviceRepository.save(any(Device.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Device result = deviceService.createDevice(device);

        assertThat(result.getStatus()).isEqualTo("OFFLINE");
    }

    @Test
    void createDevice_whenStatusIsEmpty_defaultsToOffline() {
        Device device = buildDevice(null, "SN-AAA-111", "");
        when(deviceRepository.existsBySerialNumber("SN-AAA-111")).thenReturn(false);
        when(deviceRepository.save(any(Device.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Device result = deviceService.createDevice(device);

        assertThat(result.getStatus()).isEqualTo("OFFLINE");
    }

    @Test
    void createDevice_whenStatusProvided_keepsProvidedStatus() {
        Device device = buildDevice(null, "SN-AAA-111", "WARNING");
        when(deviceRepository.existsBySerialNumber("SN-AAA-111")).thenReturn(false);
        when(deviceRepository.save(any(Device.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Device result = deviceService.createDevice(device);

        assertThat(result.getStatus()).isEqualTo("WARNING");
        verify(deviceRepository).save(device);
    }

    // --- updateDevice ---

    @Test
    void updateDevice_whenFound_updatesNameTypeAndStatus() {
        Device existing = buildDevice(1L, "SN-AAA-111", "OFFLINE");
        Device updates = buildDevice(null, "SN-IGNORED", "ONLINE");
        updates.setName("Updated Name");
        updates.setType("GATEWAY");

        when(deviceRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(deviceRepository.save(any(Device.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Device result = deviceService.updateDevice(1L, updates);

        assertThat(result.getName()).isEqualTo("Updated Name");
        assertThat(result.getType()).isEqualTo("GATEWAY");
        assertThat(result.getStatus()).isEqualTo("ONLINE");
        // serialNumber is not part of the updatable fields
        assertThat(result.getSerialNumber()).isEqualTo("SN-AAA-111");
    }

    @Test
    void updateDevice_whenNotFound_throwsResourceNotFoundException() {
        when(deviceRepository.findById(99L)).thenReturn(Optional.empty());
        Device updates = buildDevice(null, "SN-AAA-111", "ONLINE");

        assertThatThrownBy(() -> deviceService.updateDevice(99L, updates))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(deviceRepository, never()).save(any());
    }

    // --- deleteDevice ---

    @Test
    void deleteDevice_whenOnline_throwsConflictAndDoesNotDelete() {
        Device device = buildDevice(1L, "SN-AAA-111", "ONLINE");
        when(deviceRepository.findById(1L)).thenReturn(Optional.of(device));

        assertThatThrownBy(() -> deviceService.deleteDevice(1L))
                .isInstanceOf(DeviceStateConflictException.class)
                .hasMessageContaining("SN-AAA-111");

        verify(deviceRepository, never()).deleteById(any());
    }

    @Test
    void deleteDevice_whenNotOnline_deletesSuccessfully() {
        Device device = buildDevice(1L, "SN-AAA-111", "OFFLINE");
        when(deviceRepository.findById(1L)).thenReturn(Optional.of(device));

        deviceService.deleteDevice(1L);

        verify(deviceRepository).deleteById(1L);
    }

    @Test
    void deleteDevice_whenNotFound_throwsResourceNotFoundException() {
        when(deviceRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> deviceService.deleteDevice(99L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(deviceRepository, never()).deleteById(any());
    }
}
