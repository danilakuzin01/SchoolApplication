package by.danilakuzin.schoolApplication.services;


import by.danilakuzin.schoolApplication.models.SchoolClass;
import java.util.List;

public interface SchoolClassService {
    List<SchoolClass> getAll();
    SchoolClass getById(long id);
    void save(SchoolClass schoolClass);
    void deleteById(long id);
}
