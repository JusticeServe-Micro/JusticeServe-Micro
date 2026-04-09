package com.justiceserve.complianceservice.repository;
import com.justiceserve.complianceservice.entity.Audit;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface AuditRepository extends JpaRepository<Audit, Long> {

}