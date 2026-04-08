package com.justiceserve.caseservice.repository;

import com.justiceserve.caseservice.entity.Case;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CaseRepository extends JpaRepository<Case, Long> {
    List<Case> findByCitizenId(Long citizenId);
    List<Case> findByLawyerId(Long lawyerId);
    List<Case> findByJudgeId(Long judgeId);
    List<Case> findByStatus(Case.CaseStatus status);
}
