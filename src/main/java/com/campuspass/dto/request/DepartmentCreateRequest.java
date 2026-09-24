package com.campuspass.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class DepartmentCreateRequest {

    @NotBlank(message = "Department code is required")
    @Size(max = 20, message = "Code must be under 20 characters")
    private String code;

    @NotBlank(message = "Department name is required")
    private String name;

    private String description;

    public DepartmentCreateRequest() {
    }

    public DepartmentCreateRequest(String code, String name, String description) {
        this.code = code;
        this.name = name;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
