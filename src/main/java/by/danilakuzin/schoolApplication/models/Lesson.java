package by.danilakuzin.schoolApplication.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
@Entity
public class Lesson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String number;
    private String cab;
    private String time;

    @ManyToOne // каждый урок относится к одному классу
    @JoinColumn(name = "school_class_id") // указываем имя столбца в таблице, которое будет хранить id SchoolClass
    private SchoolClass schoolClass;
}
