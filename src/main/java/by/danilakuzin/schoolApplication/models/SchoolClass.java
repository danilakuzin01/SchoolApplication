package by.danilakuzin.schoolApplication.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@Builder
@Entity
public class SchoolClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @OneToMany(mappedBy = "schoolClass") // указываем, что связь управляется полем 'schoolClass' в сущности Lesson
    private List<Lesson> lessons = new ArrayList<>();

    public void AddLesson(Lesson lesson){
        if (lessons == null) lessons = new ArrayList<>();
        lessons.add(lesson);
    }
}
