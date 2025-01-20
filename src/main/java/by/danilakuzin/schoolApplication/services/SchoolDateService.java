package by.danilakuzin.schoolApplication.services;

import by.danilakuzin.schoolApplication.models.SchoolDate;

import java.time.LocalDate;
import java.util.List;

public interface SchoolDateService {
    List<SchoolDate> getSchoolClassDates();
    SchoolDate getSchoolDateToday();
    List<SchoolDate> getSchoolDateTomorrow();
    SchoolDate getSchoolDateByDate(LocalDate localDate);

    SchoolDate getSchoolClassById(long id);
    void save(SchoolDate schoolClassDate);
    void deleteById(long id);
}
