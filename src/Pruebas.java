public class Pruebas {
    private static int fallos = 0;
    private static int casos = 0;

    private static String monto(double valor) { return String.format("%.2f", valor); }

    private static void revisar(String caso, String esperado, String obtenido) {
        casos++;
        boolean bien = esperado.equals(obtenido);
        if (!bien) fallos++;
        System.out.println(caso + " | " + esperado + " | " + obtenido
                + " | " + (bien ? "OK" : "FALLÓ"));
    }

    private static void fallarEntrega(SistemaEnvios s, String placa, String codigo) {
        Despacho d = s.crearDespacho(placa);
        d.asignar(s.buscarEnvio(codigo));
        d.salir();
        d.registrarResultado(codigo, false);
    }

    public static void main(String[] args) {
        System.out.println("Caso | Esperado | Obtenido | Estado");
        revisar("Sobre urbano", "20.00", monto(new Sobre("", "A", "B", 0.2, Destino.URBANO).calcularCosto()));
        revisar("Sobre departamental", "35.00", monto(new Sobre("", "A", "B", 0.4, Destino.DEPARTAMENTAL).calcularCosto()));
        Paquete p = new Paquete("", "A", "B", 3.2, Destino.URBANO, 40, 30, 20);
        revisar("Peso volumétrico 4.8", "4.80", monto(p.pesoVolumetrico()));
        revisar("Paquete urbano 5 kg cobrados", "65.00", monto(p.calcularCosto()));
        revisar("Paquete departamental", "80.00", monto(new Paquete("", "A", "B", 3.2, Destino.DEPARTAMENTAL, 40, 30, 20).calcularCosto()));
        revisar("Fracción 3.2 cobra 4 kg", "57.00", monto(new Paquete("", "A", "B", 3.2, Destino.URBANO, 10, 10, 10).calcularCosto()));
        revisar("Frágil con seguro 3 % de Q1200", "116.00", monto(new PaqueteFragil("", "A", "B", 3.2, Destino.URBANO, 40, 30, 20, 1200).calcularCosto()));
        revisar("Carga refrigerada 2.5 kg", "115.00", monto(new CargaRefrigerada("", "A", "B", 2.5, Destino.URBANO, 4).calcularCosto()));

        revisar("Días sobre U/D", "1/2", new Sobre("", "A", "B", 0.2, Destino.URBANO).tiempoEntrega() + "/" + new Sobre("", "A", "B", 0.2, Destino.DEPARTAMENTAL).tiempoEntrega());
        revisar("Días paquete U/D", "2/4", p.tiempoEntrega() + "/" + new Paquete("", "A", "B", 2, Destino.DEPARTAMENTAL, 10, 10, 10).tiempoEntrega());
        revisar("Días frágil U/D", "3/5", new PaqueteFragil("", "A", "B", 2, Destino.URBANO, 10, 10, 10, 100).tiempoEntrega() + "/" + new PaqueteFragil("", "A", "B", 2, Destino.DEPARTAMENTAL, 10, 10, 10, 100).tiempoEntrega());
        revisar("Días carga fría", "1", "" + new CargaRefrigerada("", "A", "B", 2.5, Destino.URBANO, 0).tiempoEntrega());

        SistemaEnvios s = new SistemaEnvios();
        revisar("Ingreso inicial", "0.00", monto(s.buscarEnvio("P1").getIngresoRegistro()));
        double antes = s.buscarEnvio("P1").getIngresoRegistro();
        s.cotizar(new Paquete("", "A", "B", 1, Destino.URBANO, 40, 30, 25));
        revisar("Cotizar no cobra", monto(antes), monto(s.buscarEnvio("P1").getIngresoRegistro()));

        Despacho d = s.crearDespacho("GT-101");
        d.asignar(s.buscarEnvio("P1"));
        d.retirar(s.buscarEnvio("P1"));
        revisar("Retirar no cobra", "0.00", monto(s.buscarEnvio("P1").getIngresoReintentos()));
        revisar("Retirado vuelve a bodega", "BODEGA", s.buscarEnvio("P1").getEstado().toString());

        boolean rechazoFrio = false;
        try { d.asignar(s.buscarEnvio("R1")); }
        catch (IllegalStateException ex) { rechazoFrio = true; }
        revisar("Refrigeración rechazada", "true", "" + rechazoFrio);
        revisar("Rechazo no cambia ingreso", "0.00", monto(s.buscarEnvio("R1").getIngresoReintentos()));
        boolean rechazoCapacidad = false;
        try { d.asignar(new Paquete("NUEVO", "A", "B", 11, Destino.URBANO, 10, 10, 10)); }
        catch (IllegalStateException ex) { rechazoCapacidad = true; }
        revisar("Capacidad rechazada", "true", "" + rechazoCapacidad);

        // Se cierra el despacho preparado para reutilizar GT-101.
        d.asignar(s.buscarEnvio("S2"));
        d.salir();
        d.registrarResultado("S2", true);

        fallarEntrega(s, "GT-101", "S1");
        revisar("Sobre fallo 1", "BODEGA", s.buscarEnvio("S1").getEstado().toString());
        fallarEntrega(s, "GT-101", "S1");
        revisar("Sobre fallo 2", "BODEGA", s.buscarEnvio("S1").getEstado().toString());
        revisar("Sobre 1 reintento", "15.00", monto(s.buscarEnvio("S1").getIngresoReintentos()));
        fallarEntrega(s, "GT-101", "S1");
        revisar("Sobre fallo 3", "DEVUELTO", s.buscarEnvio("S1").getEstado().toString());
        revisar("Sobre 2 reintentos", "30.00", monto(s.buscarEnvio("S1").getIngresoReintentos()));

        fallarEntrega(s, "GT-101", "P1");
        fallarEntrega(s, "GT-101", "P1");
        fallarEntrega(s, "GT-101", "P1");
        revisar("Paquete fallo 3", "DEVUELTO", s.buscarEnvio("P1").getEstado().toString());

        fallarEntrega(s, "GT-101", "F1");
        revisar("Frágil fallo 1", "BODEGA", s.buscarEnvio("F1").getEstado().toString());
        fallarEntrega(s, "GT-101", "F1");
        revisar("Frágil fallo 2", "DEVUELTO", s.buscarEnvio("F1").getEstado().toString());
        revisar("Frágil reintento", "15.00", monto(s.buscarEnvio("F1").getIngresoReintentos()));

        fallarEntrega(s, "GT-202", "R1");
        revisar("Carga fría fallo 1", "DEVUELTO", s.buscarEnvio("R1").getEstado().toString());
        revisar("Carga fría sin reintento", "0.00", monto(s.buscarEnvio("R1").getIngresoReintentos()));

        boolean duplicado = false;
        try { s.registrarVehiculo(new Vehiculo("GT-101", 50, false)); }
        catch (IllegalArgumentException ex) { duplicado = true; }
        revisar("Placa duplicada rechazada", "true", "" + duplicado);
        revisar("Ingreso sigue Q0 registro", "0.00", monto(s.buscarEnvio("S1").getIngresoRegistro()));

        SistemaEnvios registro = new SistemaEnvios();
        Envio enviado = new Sobre("N1", "A", "B", 0.2, Destino.URBANO);
        registro.registrarEnvio(enviado);
        revisar("Registro nuevo cobra una vez", "20.00", monto(enviado.getIngresoRegistro()));
        boolean codigoRepetido = false;
        try { registro.registrarEnvio(new Sobre("N1", "C", "D", 0.2, Destino.URBANO)); }
        catch (IllegalArgumentException ex) { codigoRepetido = true; }
        revisar("Código duplicado rechazado", "true", "" + codigoRepetido);
        revisar("Duplicado no modifica ingreso", "20.00", monto(enviado.getIngresoRegistro()));
        boolean vehiculoOcupado = false;
        registro.crearDespacho("GT-101");
        try { registro.crearDespacho("GT-101"); }
        catch (IllegalStateException ex) { vehiculoOcupado = true; }
        revisar("Vehículo ocupado rechazado", "true", "" + vehiculoOcupado);
        boolean sobrePesado = false;
        try { new Sobre("", "A", "B", 0.6, Destino.URBANO); }
        catch (IllegalArgumentException ex) { sobrePesado = true; }
        revisar("Sobre de 0.6 kg rechazado", "true", "" + sobrePesado);
        boolean frioDepartamental = false;
        try { new CargaRefrigerada("", "A", "B", 1, Destino.DEPARTAMENTAL, 3); }
        catch (IllegalArgumentException ex) { frioDepartamental = true; }
        revisar("Frío departamental rechazado", "true", "" + frioDepartamental);

        System.out.println("TOTAL " + casos + " | FALLAS " + fallos);
        if (fallos > 0) System.exit(1);
    }
}