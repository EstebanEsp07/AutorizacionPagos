package com.cooperativa.pagos.autorizacion.repository;

import com.cooperativa.pagos.autorizacion.domain.Cuenta;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface CuentaRepository extends JpaRepository<Cuenta, UUID> {
    Optional<Cuenta> findByNumero(String numero);
}
