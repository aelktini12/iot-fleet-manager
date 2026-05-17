package com.elktini.iotfleet.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Device {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Serial Number is mandatory")
    @Pattern(regexp = "^SN-[A-Z0-9\\-]{3,15}$", message = "Format must be 'SN-' followed by alphanumeric characters or hyphens (e.g., SN-1234-ABC)")
    @Column(unique = true)
    @Schema(description = "Unique Device Serial Number", example = "SN-1234-ABC)")
    private String serialNumber;

    @NotBlank(message = "Device name is required")
    @Size(min = 3, max = 50, message = "The name must contain between 3 and 50 characters")
    private String name;

    @NotBlank(message = "Type is mandatory")
    @Pattern(regexp = "^(SENSOR|ACTUATOR|GATEWAY)$", message = "The type must be SENSOR, ACTUATOR or GATEWAY")
    private String type; // ex: SENSOR, DRONE, GATEWAY

    private String status; // ex: ONLINE, OFFLINE, WARNING

    @UpdateTimestamp
    private LocalDateTime lastCommunication;

    // Used for dashboard
    private Double latitude;
    private Double longitude;
}
