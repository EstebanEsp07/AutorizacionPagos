package com.cooperativa.pagos.autorizacion.repository;

import com.cooperativa.pagos.autorizacion.domain.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface PagoRepository extends JpaRepository<Pago, UUID> {
    Optional<Pago> findByIdempotencyKey(String key);
}
