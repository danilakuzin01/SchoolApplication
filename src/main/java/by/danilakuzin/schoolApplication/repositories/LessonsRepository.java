package by.danilakuzin.schoolApplication.repositories;

import by.danilakuzin.schoolApplication.models.Lesson;
import by.danilakuzin.schoolApplication.models.SchoolClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LessonsRepository extends JpaRepository<Lesson, Long> {
//    List<Lesson> findBySchoolClass(SchoolClass schoolClass);
    Lesson findByNameAndSchoolClassAndAndNumber(String name, SchoolClass schoolClass, String number);

//    List<Lesson> findAllBy OrderByNumber();
}
