package by.danilakuzin.schoolApplication.repositories;

import by.danilakuzin.schoolApplication.models.SchoolClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SchoolClassRepository extends JpaRepository<SchoolClass, Long> {
    Optional<SchoolClass> findByName(String name);
}
