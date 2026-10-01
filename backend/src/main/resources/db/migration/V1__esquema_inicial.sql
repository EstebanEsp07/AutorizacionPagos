CREATE TABLE cuentas (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  numero VARCHAR(30) NOT NULL UNIQUE,
  saldo NUMERIC(19,2) NOT NULL,
  limite_diario NUMERIC(19,2) NOT NULL,
  acumulado_diario NUMERIC(19,2) NOT NULL DEFAULT 0,
  activa BOOLEAN NOT NULL DEFAULT TRUE,
  version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE pagos (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  idempotency_key VARCHAR(100) NOT NULL UNIQUE,
  cuenta_id UUID NOT NULL REFERENCES cuentas(id),
  monto NUMERIC(19,2) NOT NULL,
  moneda VARCHAR(3) NOT NULL,
  estado VARCHAR(40) NOT NULL,
  respuesta_http INT,
  respuesta_cliente TEXT,
  codigo_banco VARCHAR(50),
  banco_reference VARCHAR(100),
  created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE auditoria (
  id BIGSERIAL PRIMARY KEY,
  pago_id UUID REFERENCES pagos(id),
  idempotency_key VARCHAR(100),
  cuenta_id UUID,
  monto NUMERIC(19,2),
  moneda VARCHAR(3),
  estado VARCHAR(40),
  request_payload TEXT,
  response_payload TEXT,
  codigo_respuesta_banco VARCHAR(50),
  latencia_ms BIGINT,
  ip_origen VARCHAR(64),
  timestamp_inicio TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
  timestamp_fin TIMESTAMPTZ
);

CREATE TABLE outbox_events (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  aggregate_id UUID NOT NULL,
  event_type VARCHAR(80) NOT NULL,
  payload TEXT NOT NULL,
  published BOOLEAN NOT NULL DEFAULT FALSE,
  created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
  published_at TIMESTAMPTZ
);

CREATE TABLE conciliaciones (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  pago_id UUID REFERENCES pagos(id),
  banco_reference VARCHAR(100),
  estado VARCHAR(30) NOT NULL,
  detalle TEXT,
  processed_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_pagos_estado ON pagos(estado);
CREATE INDEX idx_outbox_unpublished ON outbox_events(published, created_at);
CREATE INDEX idx_auditoria_pago ON auditoria(pago_id);
