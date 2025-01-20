package by.danilakuzin.schoolApplication.services;


import by.danilakuzin.schoolApplication.models.SchoolClass;
import java.util.List;
import java.util.Optional;

public interface SchoolClassService {
    List<SchoolClass> getAll();
    SchoolClass getById(long id);
    Optional<SchoolClass> getByName(String name);
    void save(SchoolClass schoolClass);
    void deleteById(long id);
}
