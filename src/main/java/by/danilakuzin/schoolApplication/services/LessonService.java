package by.danilakuzin.schoolApplication.services;


import by.danilakuzin.schoolApplication.models.Lesson;
import by.danilakuzin.schoolApplication.models.SchoolClass;

import java.util.List;

public interface LessonService {
    List<Lesson> getAllLessons();
    List<Lesson> getLessonsBySchoolClass(SchoolClass schoolClass);
    Lesson getById(long id);

    void save(Lesson lesson);
    void deleteById(long id);

}
