package com.justiceserve.hearingservice.repository;

import com.justiceserve.hearingservice.entity.Hearing;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface HearingRepository extends JpaRepository<Hearing, Long> {
    List<Hearing> findByCaseId(Long caseId);
    List<Hearing> findByJudgeId(Long judgeId);
    List<Hearing> findByStatus(Hearing.HearingStatus status);
}
