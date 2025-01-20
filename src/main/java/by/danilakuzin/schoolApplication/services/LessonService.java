package by.danilakuzin.schoolApplication.services;


import by.danilakuzin.schoolApplication.models.Lesson;
import by.danilakuzin.schoolApplication.models.SchoolClass;

import java.util.List;

public interface LessonService {
    List<Lesson> getAllLessons();
    List<Lesson> getAllLessonsForController();
//    List<Lesson> getLessonsBySchoolClass(SchoolClass schoolClass);
    Lesson getById(long id);
    Lesson getByNameAndSchoolClassAndNumber(String name, SchoolClass schoolClass, String number);

    boolean equals(Lesson lesson1, Lesson lesson2);
    void save(Lesson lesson);
    void deleteById(long id);

}
