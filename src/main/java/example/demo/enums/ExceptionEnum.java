package example.demo.enums;

import lombok.Getter;


@Getter
public enum ExceptionEnum {
    NotVerifiedException(1);

    private final int id;

    ExceptionEnum(int id) {
        this.id = id;
    }
}