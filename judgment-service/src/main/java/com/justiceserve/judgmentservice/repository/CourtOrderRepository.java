package com.justiceserve.judgmentservice.repository;

import com.justiceserve.judgmentservice.entity.CourtOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CourtOrderRepository extends JpaRepository<CourtOrder, Long> {
    List<CourtOrder> findByCaseId(Long caseId);
}