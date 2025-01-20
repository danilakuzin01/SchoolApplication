package by.danilakuzin.schoolApplication.services.impl;


import by.danilakuzin.schoolApplication.models.SchoolClass;
import by.danilakuzin.schoolApplication.repositories.SchoolClassRepository;
import by.danilakuzin.schoolApplication.services.SchoolClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SchoolClassServiceImpl implements SchoolClassService {

    @Autowired
    private SchoolClassRepository schoolClassRepository;

    @Override
    public List<SchoolClass> getAll() {
        return schoolClassRepository.findAll();
    }

    @Override
    public void save(SchoolClass schoolClass) {
        schoolClassRepository.save(schoolClass);
    }

    @Override
    public SchoolClass getById(long id) {
        return schoolClassRepository.findById(id).orElseThrow();
    }

    @Override
    public Optional<SchoolClass> getByName(String name) {
        return schoolClassRepository.findByName(name);
    }

    @Override
    public void deleteById(long id) {
        schoolClassRepository.deleteById(id);
    }
}
