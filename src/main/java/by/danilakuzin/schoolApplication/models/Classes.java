package by.danilakuzin.schoolApplication.models;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
public class Classes {
    private Integer id;
    private String name;
    private String number;
    private String cab;
    private String time;
}
