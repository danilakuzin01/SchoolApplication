package by.danilakuzin.schoolApplication.services;

import by.danilakuzin.schoolApplication.models.SchoolDate;

import java.util.List;

public interface SchoolDateService {
    List<SchoolDate> getSchoolClassDates();
    List<SchoolDate> getSchoolClassesToday();
    List<SchoolDate> getSchoolClassesTomorrow();
    SchoolDate getSchoolClassById(long id);
    void save(SchoolDate schoolClassDate);
    void deleteById(long id);
}
