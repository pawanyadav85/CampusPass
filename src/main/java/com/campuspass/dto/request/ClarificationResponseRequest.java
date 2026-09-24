package com.campuspass.dto.request;

import jakarta.validation.constraints.NotBlank;

public class ClarificationResponseRequest {

    @NotBlank(message = "Clarification reply text is required")
    private String responseRemarks;

    public ClarificationResponseRequest() {
    }

    public ClarificationResponseRequest(String responseRemarks) {
        this.responseRemarks = responseRemarks;
    }

    public String getResponseRemarks() {
        return responseRemarks;
    }

    public void setResponseRemarks(String responseRemarks) {
        this.responseRemarks = responseRemarks;
    }
}
