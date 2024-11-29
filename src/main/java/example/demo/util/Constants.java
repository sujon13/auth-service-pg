package example.demo.util;

public class Constants {
    public static final String USER_NAME_REGEXP = "^[a-zA-Z][a-zA-Z0-9._-]{1,18}[a-zA-Z0-9]$";
    public static final String USER_NAME_ERROR_MESSAGE =
            "Username must start with an alphabet, end with an alphanumeric character, and only '.', '_', and '-' are allowed";

    public static final String PASSWORD_REGEXP =
            "^(?=.*[0-9])(?=.*[A-Z])(?=.*[a-z])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{6,64}$";
    public static final String PASSWORD_ERROR_MESSAGE =
            "Password must be 6-64 characters long, contain at least one uppercase letter, one lowercase letter, " +
                    "one digit, and one special character";
}
