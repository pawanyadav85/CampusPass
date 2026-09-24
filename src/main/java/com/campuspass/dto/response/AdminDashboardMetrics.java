package com.campuspass.dto.response;

public class AdminDashboardMetrics {

    private long totalStudents;
    private long totalHods;
    private long totalSecurityStaff;
    private long totalApplications;
    private long activePasses;
    private long currentlyOutside;
    private long totalOverdue;

    public AdminDashboardMetrics() {
    }

    public AdminDashboardMetrics(long totalStudents, long totalHods, long totalSecurityStaff,
                                 long totalApplications, long activePasses, long currentlyOutside, long totalOverdue) {
        this.totalStudents = totalStudents;
        this.totalHods = totalHods;
        this.totalSecurityStaff = totalSecurityStaff;
        this.totalApplications = totalApplications;
        this.activePasses = activePasses;
        this.currentlyOutside = currentlyOutside;
        this.totalOverdue = totalOverdue;
    }

    public long getTotalStudents() {
        return totalStudents;
    }

    public void setTotalStudents(long totalStudents) {
        this.totalStudents = totalStudents;
    }

    public long getTotalHods() {
        return totalHods;
    }

    public void setTotalHods(long totalHods) {
        this.totalHods = totalHods;
    }

    public long getTotalSecurityStaff() {
        return totalSecurityStaff;
    }

    public void setTotalSecurityStaff(long totalSecurityStaff) {
        this.totalSecurityStaff = totalSecurityStaff;
    }

    public long getTotalApplications() {
        return totalApplications;
    }

    public void setTotalApplications(long totalApplications) {
        this.totalApplications = totalApplications;
    }

    public long getActivePasses() {
        return activePasses;
    }

    public void setActivePasses(long activePasses) {
        this.activePasses = activePasses;
    }

    public long getCurrentlyOutside() {
        return currentlyOutside;
    }

    public void setCurrentlyOutside(long currentlyOutside) {
        this.currentlyOutside = currentlyOutside;
    }

    public long getTotalOverdue() {
        return totalOverdue;
    }

    public void setTotalOverdue(long totalOverdue) {
        this.totalOverdue = totalOverdue;
    }
}
