package example.demo.signup.enums;

import lombok.Getter;

@Getter
public enum OtpValidation {
    MATCHED(0, "OTP matched"),
    NOT_FOUND(1, "OTP not found"),
    EXPIRED(2, "OTP expired"),
    USED(3, "Already used"),
    INVALID(4, "OTP invalid"),
    USER_NOT_FOUND(5, "User not found"),
    USER_NOT_MATCHED(6, "User not matched");

    private final int value;

    private final String message;

    OtpValidation(int value, String message) {
        this.value = value;
        this.message = message;
    }

    public boolean doesNotMatch() {
        return this != MATCHED;
    }
}

