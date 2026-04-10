package com.justiceserve.judgmentservice.repository;

import com.justiceserve.judgmentservice.entity.Judgment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JudgmentRepository extends JpaRepository<Judgment, Long> {
    List<Judgment> findByCaseId(Long caseId);
}