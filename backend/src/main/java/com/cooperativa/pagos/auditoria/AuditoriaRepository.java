package com.cooperativa.pagos.auditoria;

import com.cooperativa.pagos.auditoria.domain.RegistroAuditoria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditoriaRepository extends JpaRepository<RegistroAuditoria, Long> {}
