package by.danilakuzin.schoolApplication.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name="school_classes")
public class SchoolClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @JsonIgnore
    @OneToMany(mappedBy = "schoolClass") // указываем, что связь управляется полем 'schoolClass' в сущности Lesson
    private List<Lesson> lessons = new ArrayList<>();

//    @ManyToOne // каждый урок относится к одному классу
//    @JoinColumn(name = "school_class_date_id") // указываем имя столбца в таблице, которое будет хранить id SchoolClass
//    private SchoolDate date;
//
//    public void AddLesson(Lesson lesson){
//        if (lessons == null) lessons = new ArrayList<>();
//        lessons.add(lesson);
//    }
}
