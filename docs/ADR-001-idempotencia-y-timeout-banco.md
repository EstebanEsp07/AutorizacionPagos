# ADR-001: Idempotencia y manejo de timeout del banco externo

## Contexto
La aplicación móvil puede repetir una solicitud por pérdida de conectividad. Además, el banco externo puede tardar o dejar un estado desconocido.

## Decisión
- Cada intento originado por una acción del usuario exige `Idempotency-Key`.
- PostgreSQL mantiene una restricción `UNIQUE` sobre la clave.
- El pago se persiste antes de invocar al banco.
- Se usa timeout estricto de 4 segundos y Circuit Breaker.
- Un timeout no se interpreta como rechazo: el pago pasa a `UNKNOWN_STATUS`.
- La conciliación posterior consulta el estado externo y puede confirmar el pago.

## Consecuencias
- Se evita ejecutar dos veces una misma operación lógica.
- Los estados desconocidos requieren procesamiento asíncrono.
- La base de datos se convierte en fuente de verdad del estado interno.
- Se requiere una estrategia explícita de reverso/liberación de fondos.
