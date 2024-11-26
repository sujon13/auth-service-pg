package example.demo.signup.enums;

import lombok.Getter;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public enum AccountType {
    REGULAR(0),
    GOOGLE(1),
    FACEBOOK(2),
    MICROSOFT(3),
    LINKEDIN(4),
    APPLE(5),
    GITHUB(6);

    private final int value;

    private static final Map<Integer, AccountType> mapByValue;

    static {
        mapByValue = Stream.of(AccountType.values())
                .collect(Collectors.toMap(e -> e.value, e -> e));
    }

    AccountType(int value) {
        this.value = value;
    }

    public static AccountType getByValue(Integer value) {
        return value == null ? null : mapByValue.get(value);
    }
}

