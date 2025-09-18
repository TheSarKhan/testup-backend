package com.exam.examapp.repository.information;

import com.exam.examapp.model.information.Term;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TermRepository extends JpaRepository<Term, UUID> {
    Optional<Term> getTermByName(String termName);
}
