package com.eam_simulator.common;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.http.HttpStatus;

import java.time.Instant;

record CustomErrorResponse(HttpStatus status, String errorMessage,
                           @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX", timezone = "UTC")
                           Instant timestamp) {
}
