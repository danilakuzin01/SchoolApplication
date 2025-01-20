package by.danilakuzin.schoolApplication.repositories;

import by.danilakuzin.schoolApplication.models.LessonPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface LessonPlanRepository extends JpaRepository<LessonPlan, Long> {
    LessonPlan findBySchoolDate_Date(LocalDate date);
}
