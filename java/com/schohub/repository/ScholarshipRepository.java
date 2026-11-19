package com.schohub.repository;

import com.schohub.model.Scholarship;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ScholarshipRepository extends JpaRepository<Scholarship, String> {

    Optional<Scholarship> findByCodeIgnoreCase(String code);

    List<Scholarship> findAllByOrderByNameAsc();
}
