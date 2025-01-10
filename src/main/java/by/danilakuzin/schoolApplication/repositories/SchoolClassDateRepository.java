package by.danilakuzin.schoolApplication.repositories;

import by.danilakuzin.schoolApplication.models.SchoolClassDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SchoolClassDateRepository extends JpaRepository<SchoolClassDate, Long> {
}
