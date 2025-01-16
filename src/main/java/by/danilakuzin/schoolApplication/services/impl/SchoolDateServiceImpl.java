package by.danilakuzin.schoolApplication.services.impl;


import by.danilakuzin.schoolApplication.models.SchoolDate;
import by.danilakuzin.schoolApplication.repositories.SchoolDateRepository;
import by.danilakuzin.schoolApplication.services.SchoolDateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class SchoolDateServiceImpl implements SchoolDateService {

    @Autowired
    private SchoolDateRepository schoolDateRepository;


    @Override
    public List<SchoolDate> getSchoolClassDates() {
        return List.of();
    }

    @Override
    public List<SchoolDate> getSchoolClassesToday() {
        return List.of();
    }

    @Override
    public List<SchoolDate> getSchoolClassesTomorrow() {
        return List.of();
    }

    @Override
    public SchoolDate getSchoolClassById(long id) {
        return null;
    }

    @Override
    public void save(SchoolDate schoolClassDate) {

    }

    @Override
    public void deleteById(long id) {

    }
}
