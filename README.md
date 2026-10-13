# EnvíaGT — Laboratorio 3

Programa en Java para gestionar envíos, vehículos y despachos.

## Cómo ejecutarlo

Abre `Principal.java` dentro de `src` y presiona **Run** sobre el método `main`.

## Pruebas

Abre `Pruebas.java` y ejecuta su método `main`.

## Organización

- `Envio` es la clase abstracta.
- `Sobre`, `Paquete` y `CargaRefrigerada` son tipos de envío.
- `PaqueteFragil` hereda de `Paquete`.
- `Vehiculo` y `Despacho` gestionan el transporte.
- `SistemaEnvios` guarda los datos y genera reportes.
- `Principal` muestra el menú.