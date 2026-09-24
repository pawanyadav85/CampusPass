package com.campuspass.dto.request;

import jakarta.validation.constraints.NotBlank;

public class SystemSettingUpdateRequest {

    @NotBlank(message = "Setting key is required")
    private String key;

    @NotBlank(message = "Setting value is required")
    private String value;

    public SystemSettingUpdateRequest() {
    }

    public SystemSettingUpdateRequest(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
