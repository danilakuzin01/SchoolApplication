package by.danilakuzin.schoolApplication.services.impl;

import by.danilakuzin.schoolApplication.models.Lesson;
import by.danilakuzin.schoolApplication.models.SchoolClass;
import by.danilakuzin.schoolApplication.repositories.LessonsRepository;
import by.danilakuzin.schoolApplication.services.LessonService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class LessonServiceImpl implements LessonService {

    @Autowired
    private LessonsRepository lessonRepository;


    @Override
    public List<Lesson> getAllLessons() {
        return lessonRepository.findAll();
    }

    @Override
    public List<Lesson> getLessonsBySchoolClass(SchoolClass schoolClass) {
        return lessonRepository.findBySchoolClass(schoolClass);
    }

    @Override
    public Lesson getById(long id) {
        return lessonRepository.findById(id).orElseThrow();
    }

    @Override
    public void save(Lesson lesson) {
        lessonRepository.save(lesson);
    }

    @Override
    public void deleteById(long id) {
        lessonRepository.deleteById(id);
    }
}
