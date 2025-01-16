package by.danilakuzin.schoolApplication.services.impl;

import by.danilakuzin.schoolApplication.models.Lesson;
import by.danilakuzin.schoolApplication.models.SchoolClass;
import by.danilakuzin.schoolApplication.repositories.LessonsRepository;
import by.danilakuzin.schoolApplication.services.LessonsService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class LessonServiceImpl implements LessonsService {

    @Autowired
    private LessonsRepository lessonsRepository;


    @Override
    public List<Lesson> getAllLessons() {
        return List.of();
    }

    @Override
    public List<Lesson> getLessonsBySchoolClass(SchoolClass schoolClass) {
        return List.of();
    }

    @Override
    public Lesson getById(long id) {
        return null;
    }

    @Override
    public void save(Lesson lesson) {

    }

    @Override
    public void deleteById(long id) {

    }
}
