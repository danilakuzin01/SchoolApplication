package by.danilakuzin.schoolApplication.repositories;

import by.danilakuzin.schoolApplication.models.SchoolDate;
import org.springframework.cglib.core.Local;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SchoolDateRepository extends JpaRepository<SchoolDate, Long> {
    SchoolDate findByDate(LocalDate date);
    List<SchoolDate> findByDateTime(LocalDateTime date);
}
