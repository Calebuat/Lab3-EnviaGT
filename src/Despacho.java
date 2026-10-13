public class Despacho {
    private static final int MAX_ENVIOS = 50;
    private int id;
    private Vehiculo vehiculo;
    private EstadoDespacho estado;
    private Envio[] envios;
    private ResultadoEntrega[] resultados;
    private int cantidad;

    public Despacho(int id, Vehiculo vehiculo) {
        if (id <= 0 || vehiculo == null) throw new IllegalArgumentException("Despacho inválido.");
        this.id = id;
        this.vehiculo = vehiculo;
        estado = EstadoDespacho.PREPARACION;
        envios = new Envio[MAX_ENVIOS];
        resultados = new ResultadoEntrega[MAX_ENVIOS];
        cantidad = 0;
    }

    public double pesoCargado() {
        double total = 0;
        for (int i = 0; i < cantidad; i++) total += envios[i].getPesoReal();
        return total;
    }
    public double capacidadDisponible() { return vehiculo.getCapacidadKg() - pesoCargado(); }

    private int posicion(String codigo) {
        for (int i = 0; i < cantidad; i++)
            if (envios[i].getCodigo().equals(codigo)) return i;
        return -1;
    }

    public void asignar(Envio e) {
        if (estado != EstadoDespacho.PREPARACION || e == null
                || cantidad == MAX_ENVIOS || e.getEstado() != EstadoEnvio.BODEGA
                || !e.puedeReintentar() || posicion(e.getCodigo()) >= 0
                || !e.compatible(vehiculo) || e.getPesoReal() > capacidadDisponible())
            throw new IllegalStateException("No se puede asignar: revise estado, espacio, vehículo y capacidad.");
        e.asignar();
        envios[cantidad] = e;
        resultados[cantidad] = ResultadoEntrega.PENDIENTE;
        cantidad++;
    }

    public void retirar(Envio e) {
        if (estado != EstadoDespacho.PREPARACION || e == null)
            throw new IllegalStateException("El despacho no está en preparación.");
        int i = posicion(e.getCodigo());
        if (i < 0 || envios[i] != e)
            throw new IllegalStateException("El envío no está en este despacho.");
        e.retirar();
        for (int j = i; j < cantidad - 1; j++) {
            envios[j] = envios[j + 1];
            resultados[j] = resultados[j + 1];
        }
        cantidad--;
        envios[cantidad] = null;
        resultados[cantidad] = null;
    }

    public void salir() {
        if (estado != EstadoDespacho.PREPARACION || cantidad == 0)
            throw new IllegalStateException("El despacho debe tener envíos y estar en preparación.");
        for (int i = 0; i < cantidad; i++)
            if (envios[i].getEstado() != EstadoEnvio.ASIGNADO || !envios[i].puedeReintentar())
                throw new IllegalStateException("Un envío no puede salir.");
        for (int i = 0; i < cantidad; i++) envios[i].salir();
        estado = EstadoDespacho.EN_RUTA;
    }

    public void registrarResultado(String codigo, boolean exito) {
        int i = codigo == null ? -1 : posicion(codigo);
        if (estado != EstadoDespacho.EN_RUTA || i < 0
                || resultados[i] != ResultadoEntrega.PENDIENTE)
            throw new IllegalStateException("El resultado no está pendiente en este despacho.");
        envios[i].resolver(exito);
        resultados[i] = exito ? ResultadoEntrega.ENTREGADO : ResultadoEntrega.FALLIDO;
        boolean pendientes = false;
        for (int j = 0; j < cantidad; j++)
            if (resultados[j] == ResultadoEntrega.PENDIENTE) pendientes = true;
        if (!pendientes) estado = EstadoDespacho.CERRADO;
    }

    public int getId() { return id; }
    public Vehiculo getVehiculo() { return vehiculo; }
    public EstadoDespacho getEstado() { return estado; }
    public int getCantidad() { return cantidad; }
    public Envio getEnvio(int posicion) {
        if (posicion < 0 || posicion >= cantidad) throw new IllegalArgumentException("Posición inválida.");
        return envios[posicion];
    }
    public ResultadoEntrega getResultado(int posicion) {
        if (posicion < 0 || posicion >= cantidad) throw new IllegalArgumentException("Posición inválida.");
        return resultados[posicion];
    }
}
