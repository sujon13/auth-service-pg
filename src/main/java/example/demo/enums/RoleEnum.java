package example.demo.enums;

import lombok.Getter;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Getter
public enum RoleEnum {
    ADMIN(1, "Admin"),
    SCADA_SE(2, "SCADA SE"),
    SMD_XEN(3, "SMD XEN"),
    SMD_SDE(4, "SMD SDE"),
    SMD_AE(5, "SMD AE"),
    SMD_SAE(6, "SMD SAE"),
    NOD_XEN(7, "NOD XEN"),
    NOD_SDE(8, "NOD SDE"),
    NOD_AE(9, "NOD AE"),
    NOD_SAE(10, "NOD SAE"),
    CONTRACTOR(11, "Contractor"),
    IMD(12, "IMD"),
    EMD(13, "EMD"),
    CNST_XEN(14, "CNST XEN"),
    CNST_SDE(15, "CNST SDE"),
    CNST_AE(16, "CNST AE"),
    SCADA_AE(17, "SCADA AE");

    private static final Map<Integer, RoleEnum> mapByValue;

    static {
        mapByValue = Stream.of(RoleEnum.values())
                .collect(Collectors.toMap(e -> e.value, e -> e));
    }

    private final int value;
    private final String displayName;

    RoleEnum(int value, String displayName) {
        this.value = value;
        this.displayName = displayName;
    }

    public static RoleEnum getByValue(Integer value) {
        return value == null ? null : mapByValue.get(value);
    }

    public String getName() {
        return this.name();
    }
}

