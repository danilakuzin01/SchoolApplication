package by.danilakuzin.schoolApplication.repositories;

import by.danilakuzin.schoolApplication.models.SchoolClass;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SchoolClassRepository extends JpaRepository<SchoolClass, Long> {
}
