package com.campuspass.dto.request;

import jakarta.validation.constraints.NotBlank;

public class StudentProfileUpdateRequest {

    @NotBlank(message = "Mobile number is required")
    private String mobile;

    @NotBlank(message = "Parent/Guardian name is required")
    private String parentName;

    @NotBlank(message = "Parent/Guardian phone is required")
    private String parentPhone;

    public StudentProfileUpdateRequest() {
    }

    public StudentProfileUpdateRequest(String mobile, String parentName, String parentPhone) {
        this.mobile = mobile;
        this.parentName = parentName;
        this.parentPhone = parentPhone;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public String getParentPhone() {
        return parentPhone;
    }

    public void setParentPhone(String parentPhone) {
        this.parentPhone = parentPhone;
    }
}
