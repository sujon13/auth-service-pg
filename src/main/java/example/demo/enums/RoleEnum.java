package example.demo.enums;

import lombok.Getter;


@Getter
public enum RoleEnum {
    ADMIN(0),
    USER(1),
    QUESTIONER(2),
    EXAMINER(3);

    private final int id;

    RoleEnum(int id) {
        this.id = id;
    }
}

