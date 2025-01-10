package by.danilakuzin.schoolApplication.repositories;

import by.danilakuzin.schoolApplication.models.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LessonsRepository extends JpaRepository<Lesson, Long> {
}
