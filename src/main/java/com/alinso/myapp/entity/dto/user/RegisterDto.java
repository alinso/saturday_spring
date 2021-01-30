package com.alinso.myapp.entity.dto.user;

import com.alinso.myapp.entity.enums.Gender;
import org.springframework.stereotype.Component;

@Component
public class RegisterDto {
    private String password;
    private String confirmPassword;
    private Gender gender;
    private String approvalCode;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getApprovalCode() {
        return approvalCode;
    }

    public void setApprovalCode(String approvalCode) {
        this.approvalCode = approvalCode;
    }
}
