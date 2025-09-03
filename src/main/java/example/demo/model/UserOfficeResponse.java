package example.demo.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserOfficeResponse {
    private int userId;

    private int designationId;
    private String designation;
    private String designationFullName;

    private int officeId;
    private String office;
    private String officeFullName;
    private String company;
}
