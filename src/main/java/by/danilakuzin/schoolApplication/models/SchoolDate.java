package by.danilakuzin.schoolApplication.models;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "school_dates")
public class SchoolDate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    public String name;
    public LocalDateTime dateTime;
    public LocalDate date;
    public String filePath;

//    @OneToMany(mappedBy = "date", fetch = FetchType.EAGER)
//    public List<SchoolClass> schoolClasses = new ArrayList<>();
}
