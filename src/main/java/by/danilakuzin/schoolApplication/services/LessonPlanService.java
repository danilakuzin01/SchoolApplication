package by.danilakuzin.schoolApplication.services;

import by.danilakuzin.schoolApplication.models.LessonPlan;

import java.time.LocalDate;
import java.util.List;

public interface LessonPlanService {
    List<LessonPlan> getAll();
    LessonPlan getById(long id);
    LessonPlan getByDate(LocalDate localDate);
    LessonPlan getTomorrow();

    void save(LessonPlan lessonPlan);
    void delete(LessonPlan lessonPlan);
}
