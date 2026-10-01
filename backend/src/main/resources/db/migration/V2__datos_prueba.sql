INSERT INTO cuentas(numero, saldo, limite_diario, acumulado_diario)
VALUES ('100000001', 1000.00, 800.00, 0.00)
ON CONFLICT (numero) DO NOTHING;
