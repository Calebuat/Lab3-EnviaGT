public class SistemaEnvios {
    private static final int MAX = 100;
    private Envio[] envios = new Envio[MAX];
    private Vehiculo[] vehiculos = new Vehiculo[MAX];
    private Despacho[] despachos = new Despacho[MAX];
    private int cantidadEnvios = 0, cantidadVehiculos = 0, cantidadDespachos = 0;
    private int siguienteId = 1;

    public SistemaEnvios() { cargarDatosIniciales(); }

    public String cotizar(Envio e) {
        if (e == null) throw new IllegalArgumentException("Faltan datos.");
        return e.categoria() + ": Q" + dinero(e.calcularCosto())
                + ", " + e.tiempoEntrega() + " día(s) hábil(es).";
    }

    public void registrarEnvio(Envio e) {
        if (e == null || e.getCodigo().isEmpty() || cantidadEnvios == MAX
                || existeEnvio(e.getCodigo()) || e.getEstado() != EstadoEnvio.BODEGA
                || e.getIntentos() != 0 || e.getIngresoRegistro() != 0
                || e.getIngresoReintentos() != 0)
            throw new IllegalArgumentException("Código repetido, límite alcanzado o envío inválido.");
        double monto = e.calcularCosto();
        e.cobrarRegistro(monto);
        envios[cantidadEnvios++] = e;
    }

    private boolean existeEnvio(String codigo) {
        for (int i = 0; i < cantidadEnvios; i++)
            if (envios[i].getCodigo().equals(codigo)) return true;
        return false;
    }
    public void registrarVehiculo(Vehiculo v) {
        if (v == null || cantidadVehiculos == MAX)
            throw new IllegalArgumentException("No se puede registrar el vehículo.");
        for (int i = 0; i < cantidadVehiculos; i++)
            if (vehiculos[i].getPlaca().equals(v.getPlaca()))
                throw new IllegalArgumentException("La placa ya existe.");
        vehiculos[cantidadVehiculos++] = v;
    }

    public Despacho crearDespacho(String placa) {
        Vehiculo elegido = null;
        for (int i = 0; i < cantidadVehiculos; i++)
            if (vehiculos[i].getPlaca().equals(placa == null ? "" : placa.trim()))
                elegido = vehiculos[i];
        if (elegido == null) throw new IllegalArgumentException("No existe la placa.");
        if (cantidadDespachos == MAX) throw new IllegalStateException("Límite de despachos alcanzado.");
        for (int i = 0; i < cantidadDespachos; i++)
            if (despachos[i].getVehiculo() == elegido
                    && despachos[i].getEstado() != EstadoDespacho.CERRADO)
                throw new IllegalStateException("El vehículo tiene un despacho abierto.");
        Despacho d = new Despacho(siguienteId++, elegido);
        despachos[cantidadDespachos++] = d;
        return d;
    }

    public Envio buscarEnvio(String codigo) {
        for (int i = 0; i < cantidadEnvios; i++)
            if (envios[i].getCodigo().equals(codigo == null ? "" : codigo.trim()))
                return envios[i];
        throw new IllegalArgumentException("No existe el envío.");
    }
    public Despacho buscarDespacho(int id) {
        for (int i = 0; i < cantidadDespachos; i++)
            if (despachos[i].getId() == id) return despachos[i];
        throw new IllegalArgumentException("No existe el despacho.");
    }

    public int getCantidadEnvios() { return cantidadEnvios; }
    public Envio getEnvio(int i) {
        if (i < 0 || i >= cantidadEnvios) throw new IllegalArgumentException("Posición inválida.");
        return envios[i];
    }
    public int getCantidadVehiculos() { return cantidadVehiculos; }
    public Vehiculo getVehiculo(int i) {
        if (i < 0 || i >= cantidadVehiculos) throw new IllegalArgumentException("Posición inválida.");
        return vehiculos[i];
    }

    public String reporteEnvios() {
        String s = "Total: " + cantidadEnvios + "\nPor categoría:\n";
        // Se obtiene el nombre de cada categoría desde los propios objetos.
        for (int i = 0; i < cantidadEnvios; i++) {
            boolean visto = false;
            for (int j = 0; j < i; j++)
                if (envios[i].categoria().equals(envios[j].categoria())) visto = true;
            if (!visto) {
                int cuenta = 0;
                for (int j = 0; j < cantidadEnvios; j++)
                    if (envios[i].categoria().equals(envios[j].categoria())) cuenta++;
                s += "  " + envios[i].categoria() + ": " + cuenta + "\n";
            }
        }
        s += "Por estado:\n";
        for (EstadoEnvio estado : EstadoEnvio.values()) {
            int cuenta = 0;
            for (int i = 0; i < cantidadEnvios; i++)
                if (envios[i].getEstado() == estado) cuenta++;
            s += "  " + estado + ": " + cuenta + "\n";
        }
        return s;
    }

    public String reporteIngresos() {
        double registro = 0, reintentos = 0;
        for (int i = 0; i < cantidadEnvios; i++) {
            registro += envios[i].getIngresoRegistro();
            reintentos += envios[i].getIngresoReintentos();
        }
        String s = "Registro Q" + dinero(registro) + "\nReintentos Q"
                + dinero(reintentos) + "\nTotal Q" + dinero(registro + reintentos)
                + "\nPor categoría:\n";
        for (int i = 0; i < cantidadEnvios; i++) {
            boolean visto = false;
            for (int j = 0; j < i; j++)
                if (envios[i].categoria().equals(envios[j].categoria())) visto = true;
            if (!visto) {
                double total = 0;
                for (int j = 0; j < cantidadEnvios; j++)
                    if (envios[i].categoria().equals(envios[j].categoria()))
                        total += envios[j].getIngresoRegistro() + envios[j].getIngresoReintentos();
                s += "  " + envios[i].categoria() + ": Q" + dinero(total) + "\n";
            }
        }
        return s;
    }

    public String reporteDespachos() {
        String s = "Despachos abiertos:\n";
        int cuenta = 0;
        for (int i = 0; i < cantidadDespachos; i++) {
            Despacho d = despachos[i];
            if (d.getEstado() != EstadoDespacho.CERRADO) {
                cuenta++;
                s += "  #" + d.getId() + " " + d.getEstado() + " placa "
                        + d.getVehiculo().getPlaca() + " cargado " + dinero(d.pesoCargado())
                        + " kg, disponible " + dinero(d.capacidadDisponible()) + " kg\n";
            }
        }
        if (cuenta == 0) s += "  Ninguno.\n";
        return s;
    }

    private String dinero(double valor) { return String.format("%.2f", valor); }

    private void cargarDatosIniciales() {
        // No se cobra registro a los ejemplos.
        envios[cantidadEnvios++] = new Sobre("S1", "Ana", "Luis", 0.2, Destino.URBANO);
        envios[cantidadEnvios++] = new Sobre("S2", "María", "José", 0.4, Destino.DEPARTAMENTAL);
        envios[cantidadEnvios++] = new Paquete("P1", "Carlos", "Elena", 1, Destino.URBANO, 40, 30, 25);
        envios[cantidadEnvios++] = new Paquete("P2", "Dora", "Pablo", 4, Destino.DEPARTAMENTAL, 20, 20, 20);
        envios[cantidadEnvios++] = new PaqueteFragil("F1", "Sofía", "Iván", 2, Destino.URBANO, 20, 20, 20, 100);
        envios[cantidadEnvios++] = new PaqueteFragil("F2", "Raúl", "Lucía", 3, Destino.DEPARTAMENTAL, 30, 20, 20, 200);
        envios[cantidadEnvios++] = new CargaRefrigerada("R1", "Marta", "Juan", 2.5, Destino.URBANO, 4);
        envios[cantidadEnvios++] = new CargaRefrigerada("R2", "Nora", "Leo", 5, Destino.URBANO, -10);
        registrarVehiculo(new Vehiculo("GT-101", 10, false));
        registrarVehiculo(new Vehiculo("GT-202", 25, true));
        registrarVehiculo(new Vehiculo("GT-303", 50, false));
    }
}
