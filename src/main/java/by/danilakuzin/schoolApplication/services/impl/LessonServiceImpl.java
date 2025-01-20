package by.danilakuzin.schoolApplication.services.impl;

import by.danilakuzin.schoolApplication.models.Lesson;
import by.danilakuzin.schoolApplication.models.SchoolClass;
import by.danilakuzin.schoolApplication.repositories.LessonsRepository;
import by.danilakuzin.schoolApplication.services.LessonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LessonServiceImpl implements LessonService {

    @Autowired
    private LessonsRepository lessonRepository;


    @Override
    public List<Lesson> getAllLessons() {
        return lessonRepository.findAll();
    }

    @Override
    public List<Lesson> getAllLessonsForController() {
        return null;
    }

//    @Override
//    public List<Lesson> getLessonsBySchoolClass(SchoolClass schoolClass) {
//        return lessonRepository.findBySchoolClass(schoolClass);
//    }

    @Override
    public Lesson getById(long id) {
        return lessonRepository.findById(id).orElseThrow();
    }

    @Override
    public Lesson getByNameAndSchoolClassAndNumber(String name, SchoolClass schoolClass, String number) {
        return lessonRepository.findByNameAndSchoolClassAndAndNumber(name, schoolClass, number);
    }

    @Override
    public boolean equals(Lesson lesson1, Lesson lesson2) {
        if (lesson1.getName().equals(lesson2.getName()))
            if (lesson1.getCab().equals(lesson2.getCab()))
                if (lesson1.getNumber().equals(lesson2.getNumber()))
                    return lesson1.getSchoolClass().getName().equals(lesson2.getSchoolClass().getName());
        return false;
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
