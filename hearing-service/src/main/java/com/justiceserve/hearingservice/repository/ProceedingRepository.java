package com.justiceserve.hearingservice.repository;
import com.justiceserve.hearingservice.entity.Proceeding;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface ProceedingRepository extends JpaRepository<Proceeding, Long> {
    List<Proceeding> findByHearingHearingId(Long hearingId);
}