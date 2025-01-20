package by.danilakuzin.schoolApplication.services.impl;


import by.danilakuzin.schoolApplication.models.LessonPlan;
import by.danilakuzin.schoolApplication.repositories.LessonPlanRepository;
import by.danilakuzin.schoolApplication.services.LessonPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class LessonPlanServiceImpl implements LessonPlanService {

    @Autowired
    private LessonPlanRepository lessonPlanRepository;

    @Override
    public List<LessonPlan> getAll() {
        return lessonPlanRepository.findAll();
    }

    @Override
    public LessonPlan getById(long id) {
        return lessonPlanRepository.findById(id).orElseThrow();
    }

    @Override
    public LessonPlan getByDate(LocalDate localDate) {
        return lessonPlanRepository.findBySchoolDate_Date(localDate);
    }

    @Override
    public LessonPlan getTomorrow() {
        return lessonPlanRepository.findBySchoolDate_Date(LocalDate.now().plusDays(1));
    }

    @Override
    public void save(LessonPlan lessonPlan) {
        lessonPlanRepository.save(lessonPlan);
    }

    @Override
    public void delete(LessonPlan lessonPlan) {
        lessonPlanRepository.delete(lessonPlan);
    }


}
