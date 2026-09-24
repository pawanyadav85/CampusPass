package com.campuspass.dto.request;

public class HodDecisionRequest {

    private String remarks;

    public HodDecisionRequest() {
    }

    public HodDecisionRequest(String remarks) {
        this.remarks = remarks;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
