package com.campuspass.dto.response;

public class StudentProfileResponse {

    private Long id;
    private Long userId;
    private String username;
    private String email;
    private String rollNumber;
    private Long departmentId;
    private String departmentCode;
    private String departmentName;
    private Integer semester;
    private String mobile;
    private String parentName;
    private String parentPhone;
    private String studentPhotoUrl;
    private String idCardDocUrl;

    public StudentProfileResponse() {
    }

    public StudentProfileResponse(Long id, Long userId, String username, String email, String rollNumber,
                                  Long departmentId, String departmentCode, String departmentName,
                                  Integer semester, String mobile, String parentName, String parentPhone,
                                  String studentPhotoUrl, String idCardDocUrl) {
        this.id = id;
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.rollNumber = rollNumber;
        this.departmentId = departmentId;
        this.departmentCode = departmentCode;
        this.departmentName = departmentName;
        this.semester = semester;
        this.mobile = mobile;
        this.parentName = parentName;
        this.parentPhone = parentPhone;
        this.studentPhotoUrl = studentPhotoUrl;
        this.idCardDocUrl = idCardDocUrl;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRollNumber() {
        return rollNumber;
    }

    public void setRollNumber(String rollNumber) {
        this.rollNumber = rollNumber;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public String getDepartmentCode() {
        return departmentCode;
    }

    public void setDepartmentCode(String departmentCode) {
        this.departmentCode = departmentCode;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public Integer getSemester() {
        return semester;
    }

    public void setSemester(Integer semester) {
        this.semester = semester;
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

    public String getStudentPhotoUrl() {
        return studentPhotoUrl;
    }

    public void setStudentPhotoUrl(String studentPhotoUrl) {
        this.studentPhotoUrl = studentPhotoUrl;
    }

    public String getIdCardDocUrl() {
        return idCardDocUrl;
    }

    public void setIdCardDocUrl(String idCardDocUrl) {
        this.idCardDocUrl = idCardDocUrl;
    }
}
