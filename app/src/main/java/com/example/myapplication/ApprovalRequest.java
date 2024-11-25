package com.example.myapplication;

public class ApprovalRequest {
    private String status;

    public ApprovalRequest(String status) {
        this.status = status;
    }

    // Getter and setter
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
