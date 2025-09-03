package example.demo.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserDropdown {
    private String username;
    private String name;
    private String designation;
    private String office;
    private String company;
}