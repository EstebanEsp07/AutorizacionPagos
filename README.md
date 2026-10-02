# Autorización de Pagos

Proyecto base para una cooperativa que recibe pagos desde una aplicación móvil.

## Arquitectura

```text
App móvil
   |
   v
REST / Spring Boot
   |
   +--> PostgreSQL
   |      + pagos
   |      + auditoria
   |      + outbox_events
   |
   +--> Banco externo
   |      timeout 4 s + circuit breaker
   |
   +--> Outbox Publisher --> RabbitMQ
                              +--> notificaciones
                              +--> conciliación
```

## Flujo síncrono

1. Recibir `Idempotency-Key`.
2. Validar formato y datos.
3. Buscar cuenta.
4. Validar estado, saldo y límites.
5. Crear pago `PENDING_EXTERNAL`.
6. Registrar auditoría.
7. Reservar fondos.
8. Llamar al banco externo con timeout.
9. Responder `200`, `422` o `202`.

## Idempotencia

La clave se almacena con `UNIQUE` en PostgreSQL. Un reintento con la misma clave no vuelve a ejecutar la autorización externa y devuelve el resultado persistido.

## Timeout

Si el banco tarda más que el timeout configurado, el pago queda `UNKNOWN_STATUS` y el cliente recibe `202 Accepted`. La conciliación consulta posteriormente el estado del banco.

## Métricas

- `payment.authorization.latency`: latencia del flujo de autorización; se publican percentiles p95/p99.
- `payment.duplicates.total`: solicitudes detectadas como duplicadas.
- `payment.errors.total`: errores por tipo.
- `payment.reconciliation.duration`: duración de conciliación.

Prometheus: `GET /actuator/prometheus`.

## Ejecutar

```bash
cp .env.example .env
docker compose up --build
```

## GitHub Codespaces

1. Abre el repositorio en GitHub y selecciona **Code > Codespaces > Create codespace on main**.
2. El Codespace incluye Java 21, Maven y Docker/Compose para ejecutar el entorno local.
3. Durante la creación del Codespace se copia `.env.example` a `.env` solo si `.env` no existe.
4. Para iniciar los servicios desde la raíz del repo:

```bash
docker compose up --build
```

Si necesitas regenerar el archivo de entorno manualmente:

```bash
cp .env.example .env
```

Swagger:
`http://localhost:8080/swagger-ui.html`

Health:
`http://localhost:8080/actuator/health`

Prometheus:
`http://localhost:8080/actuator/prometheus`

## Ejemplo

```bash
curl -X POST http://localhost:8080/api/v1/pagos/autorizar   -H "Content-Type: application/json"   -H "Idempotency-Key: 7f7f3b7d-9a4b-4c6b-a3dd-001122334455"   -d '{"cuenta":"100000001","monto":100.00,"moneda":"USD"}'
```

### Nota de producción

El esqueleto muestra el patrón arquitectónico. Antes de producción deben completarse:
- autenticación/autorización real;
- cifrado y redacción de datos sensibles;
- liberación/reverso de fondos ante rechazo o conciliación negativa;
- estrategia exacta de estados y recuperación;
- locks transaccionales para concurrencia de una misma cuenta;
- DLQ y políticas de retry de RabbitMQ;
- pruebas de integración/Testcontainers;
- controles PCI y trazabilidad conforme a normativa aplicable.
