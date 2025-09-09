package com.giftandgo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "logEntry")
public class LogEntry {

    @Id
    private String id;
    private String requestUri;
    private Instant date;
    private Integer responseCode;
    private String ipAddress;
    private String countryCode;
    private String ipProvider;
    private Long timeLapsed;
}
