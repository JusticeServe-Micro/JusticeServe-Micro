package com.justiceserve.complianceservice.repository;
import com.justiceserve.complianceservice.entity.ComplianceRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface ComplianceRecordRepository extends JpaRepository<ComplianceRecord, Long> {

}