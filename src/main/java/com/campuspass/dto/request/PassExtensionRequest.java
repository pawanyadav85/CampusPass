package com.campuspass.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

public class PassExtensionRequest {

    @NotNull(message = "Requested return time is required")
    @Future(message = "Requested return time must be in the future")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime requestedReturnTime;

    @NotBlank(message = "Reason for extension is required")
    private String reason;

    public PassExtensionRequest() {
    }

    public PassExtensionRequest(LocalDateTime requestedReturnTime, String reason) {
        this.requestedReturnTime = requestedReturnTime;
        this.reason = reason;
    }

    public LocalDateTime getRequestedReturnTime() {
        return requestedReturnTime;
    }

    public void setRequestedReturnTime(LocalDateTime requestedReturnTime) {
        this.requestedReturnTime = requestedReturnTime;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
