package example.demo.util;

public class Constants {
    public static final String USER_NAME_REGEXP = "^[a-zA-Z0-9_.-]{3,20}$";
    public static final String USER_NAME_ERROR_MESSAGE =
            "Username must be 3 to 20 characters long and can only contain letters, digits, underscores, hyphens, and dots";

    public static final String PASSWORD_REGEXP =
            //"^(?=.*[0-9])(?=.*[A-Z])(?=.*[a-z])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{6,64}$";
            "^(?=.*[0-9])(?=.*[A-Z])(?=.*[a-z]).{6,64}$";
    public static final String PASSWORD_ERROR_MESSAGE =
            "Password must be 6-64 characters long, contain at least one uppercase letter, one lowercase letter, and " +
                    "one digit";

    public static final String ACCESS_TOKEN = "accessToken";

    public static final String AUTHORITIES = "authorities";
    public static final String NAME = "name";
    public static final String ADMIN = "admin";

    public static final String INTERNAL_API_KEY_HEADER = "X-INTERNAL-API-KEY";

    public static final int TOKEN_EXPIRATION_TIME_IN_DAYS = 1;
    public static final int COOKIE_EXPIRATION_TIME_IN_DAYS = 1;
}
