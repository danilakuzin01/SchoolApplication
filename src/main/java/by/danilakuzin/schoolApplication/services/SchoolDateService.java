package by.danilakuzin.schoolApplication.services;

import by.danilakuzin.schoolApplication.models.SchoolDate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface SchoolDateService {
    List<SchoolDate> getSchoolClassDates();
    List<SchoolDate> getSchoolClassesToday();
    List<SchoolDate> getSchoolClassesTomorrow();
    List<SchoolDate> getSchoolClassesByDate(LocalDate localDate);

    SchoolDate getSchoolClassById(long id);
    void save(SchoolDate schoolClassDate);
    void deleteById(long id);
}
