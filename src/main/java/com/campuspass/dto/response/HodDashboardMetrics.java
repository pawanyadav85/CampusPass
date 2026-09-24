package com.campuspass.dto.response;

public class HodDashboardMetrics {

    private long pendingApplications;
    private long approvedToday;
    private long rejectedToday;
    private long clarificationRequired;
    private long currentlyOutside;
    private long overdueStudents;

    public HodDashboardMetrics() {
    }

    public HodDashboardMetrics(long pendingApplications, long approvedToday, long rejectedToday,
                               long clarificationRequired, long currentlyOutside, long overdueStudents) {
        this.pendingApplications = pendingApplications;
        this.approvedToday = approvedToday;
        this.rejectedToday = rejectedToday;
        this.clarificationRequired = clarificationRequired;
        this.currentlyOutside = currentlyOutside;
        this.overdueStudents = overdueStudents;
    }

    public long getPendingApplications() {
        return pendingApplications;
    }

    public void setPendingApplications(long pendingApplications) {
        this.pendingApplications = pendingApplications;
    }

    public long getApprovedToday() {
        return approvedToday;
    }

    public void setApprovedToday(long approvedToday) {
        this.approvedToday = approvedToday;
    }

    public long getRejectedToday() {
        return rejectedToday;
    }

    public void setRejectedToday(long rejectedToday) {
        this.rejectedToday = rejectedToday;
    }

    public long getClarificationRequired() {
        return clarificationRequired;
    }

    public void setClarificationRequired(long clarificationRequired) {
        this.clarificationRequired = clarificationRequired;
    }

    public long getCurrentlyOutside() {
        return currentlyOutside;
    }

    public void setCurrentlyOutside(long currentlyOutside) {
        this.currentlyOutside = currentlyOutside;
    }

    public long getOverdueStudents() {
        return overdueStudents;
    }

    public void setOverdueStudents(long overdueStudents) {
        this.overdueStudents = overdueStudents;
    }
}
