package com.niit.industrialgasalarmcorporate.domain.staff;

import java.time.LocalDateTime;
import java.util.UUID;

public class Staff {

    private final String staffUuid;
    private String userUuid;
    private String name;
    private String phone;
    private String email;
    private StaffRole role;
    private StaffStatus status;
    private final LocalDateTime createdAt;

    public Staff(String name, String phone, String email, StaffRole role, StaffStatus status) {
        this.staffUuid = UUID.randomUUID().toString();
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.role = role;
        this.status = status;
        this.createdAt = LocalDateTime.now();
    }

    public Staff(String name, String phone, String email, StaffRole role, StaffStatus status, String userUuid) {
        this(name, phone, email, role, status);
        this.userUuid = userUuid;
    }

    public Staff(String staffUuid, String userUuid, String name, String phone, String email,
                 StaffRole role, StaffStatus status, LocalDateTime createdAt) {
        this.staffUuid = staffUuid;
        this.userUuid = userUuid;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.role = role;
        this.status = status;
        this.createdAt = createdAt;
    }

    public String getStaffUuid() { return staffUuid; }
    public String getUserUuid() { return userUuid; }
    public String getName() { return name; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public StaffRole getRole() { return role; }
    public StaffStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setUserUuid(String userUuid) { this.userUuid = userUuid; }

    public boolean isAvailable() {
        return status == StaffStatus.STANDBY || status == StaffStatus.WORKING;
    }

    public void changeStatus(StaffStatus newStatus) {
        this.status = newStatus;
    }

    public void update(String name, String phone, String email, StaffRole role, StaffStatus status) {
        if (name != null && !name.isBlank()) {
            this.name = name;
        }
        if (phone != null && !phone.isBlank()) {
            this.phone = phone;
        }
        if (email != null) {
            this.email = email;
        }
        if (role != null) {
            this.role = role;
        }
        if (status != null) {
            this.status = status;
        }
    }
}
