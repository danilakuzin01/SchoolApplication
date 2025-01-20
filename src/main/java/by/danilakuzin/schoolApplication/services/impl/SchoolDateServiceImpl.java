package by.danilakuzin.schoolApplication.services.impl;


import by.danilakuzin.schoolApplication.models.SchoolDate;
import by.danilakuzin.schoolApplication.repositories.SchoolDateRepository;
import by.danilakuzin.schoolApplication.services.SchoolDateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;


@Service
public class SchoolDateServiceImpl implements SchoolDateService {

    @Autowired
    private SchoolDateRepository schoolDateRepository;


    @Override
    public List<SchoolDate> getSchoolClassDates() {
        return schoolDateRepository.findAll();
    }

    @Override
    public SchoolDate getSchoolDateToday() {
        return schoolDateRepository.findByDate(LocalDate.now());
    }

    @Override
    public List<SchoolDate> getSchoolDateTomorrow() {
        return List.of();
    }

    @Override
    public SchoolDate getSchoolDateByDate(LocalDate localDate) {
        return schoolDateRepository.findByDate(localDate);
    }

    @Override
    public SchoolDate getSchoolClassById(long id) {
        return schoolDateRepository.findById(id).orElseThrow();
    }

    @Override
    public void save(SchoolDate schoolClassDate) {
        schoolDateRepository.save(schoolClassDate);
    }

    @Override
    public void deleteById(long id) {
        schoolDateRepository.deleteById(id);
    }
}
