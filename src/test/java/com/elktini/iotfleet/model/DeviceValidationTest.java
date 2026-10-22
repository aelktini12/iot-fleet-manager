package com.elktini.iotfleet.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class DeviceValidationTest {

    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    static void closeValidatorFactory() {
        validatorFactory.close();
    }

    private Device validDevice() {
        Device device = new Device();
        device.setSerialNumber("SN-1234-ABC");
        device.setName("Living Room Sensor");
        device.setType("SENSOR");
        return device;
    }

    @Test
    void validDevice_hasNoViolations() {
        Set<ConstraintViolation<Device>> violations = validator.validate(validDevice());

        assertThat(violations).isEmpty();
    }

    @Test
    void blankSerialNumber_isRejected() {
        Device device = validDevice();
        device.setSerialNumber("");

        Set<ConstraintViolation<Device>> violations = validator.validate(device);

        assertThat(violations)
                .extracting(ConstraintViolation::getPropertyPath)
                .extracting(Object::toString)
                .contains("serialNumber");
    }

    @ParameterizedTest
    @ValueSource(strings = {"1234-ABC", "sn-1234-abc", "SN_1234_ABC", "SN-A"})
    void malformedSerialNumber_isRejected(String serialNumber) {
        Device device = validDevice();
        device.setSerialNumber(serialNumber);

        Set<ConstraintViolation<Device>> violations = validator.validate(device);

        assertThat(violations)
                .extracting(ConstraintViolation::getPropertyPath)
                .extracting(Object::toString)
                .contains("serialNumber");
    }

    @Test
    void blankName_isRejected() {
        Device device = validDevice();
        device.setName(" ");

        Set<ConstraintViolation<Device>> violations = validator.validate(device);

        assertThat(violations)
                .extracting(ConstraintViolation::getPropertyPath)
                .extracting(Object::toString)
                .contains("name");
    }

    @Test
    void nameTooShort_isRejected() {
        Device device = validDevice();
        device.setName("AB");

        Set<ConstraintViolation<Device>> violations = validator.validate(device);

        assertThat(violations)
                .extracting(ConstraintViolation::getPropertyPath)
                .extracting(Object::toString)
                .contains("name");
    }

    @Test
    void nameTooLong_isRejected() {
        Device device = validDevice();
        device.setName("A".repeat(51));

        Set<ConstraintViolation<Device>> violations = validator.validate(device);

        assertThat(violations)
                .extracting(ConstraintViolation::getPropertyPath)
                .extracting(Object::toString)
                .contains("name");
    }

    @ParameterizedTest
    @ValueSource(strings = {"SENSOR", "ACTUATOR", "GATEWAY"})
    void allowedTypes_areAccepted(String type) {
        Device device = validDevice();
        device.setType(type);

        Set<ConstraintViolation<Device>> violations = validator.validate(device);

        assertThat(violations).isEmpty();
    }

    @Test
    void unknownType_isRejected() {
        Device device = validDevice();
        device.setType("DRONE");

        Set<ConstraintViolation<Device>> violations = validator.validate(device);

        assertThat(violations)
                .extracting(ConstraintViolation::getPropertyPath)
                .extracting(Object::toString)
                .contains("type");
    }

    @Test
    void statusIsOptional_hasNoConstraint() {
        Device device = validDevice();
        device.setStatus(null);

        Set<ConstraintViolation<Device>> violations = validator.validate(device);

        assertThat(violations).isEmpty();
    }
}
