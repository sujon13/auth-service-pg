package example.demo.enums;

import lombok.Getter;


@Getter
public enum RoleEnum {
    ADMIN(1),
    SCADA_SE(2),
    SCADA_XEN(3),
    SCADA_SDE(4),
    SCADA_AE(5),
    SCADA_SAE(6),
    NOD_XEN(7),
    NOD_SDE(8),
    NOD_AE(9),
    NOD_SAE(10),
    GE(11);

    private final int id;

    RoleEnum(int id) {
        this.id = id;
    }
}

