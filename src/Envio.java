public abstract class Envio {
    private String codigo, remitente, destinatario;
    private double pesoReal;
    private Destino destino;
    private EstadoEnvio estado;
    private int intentos;
    private double ingresoRegistro, ingresoReintentos;

    protected Envio(String codigo, String remitente, String destinatario,
                    double pesoReal, Destino destino) {
        if (remitente == null || remitente.trim().isEmpty()
                || destinatario == null || destinatario.trim().isEmpty())
            throw new IllegalArgumentException("Faltan nombres.");
        if (!(pesoReal > 0) || pesoReal > 1000000 || destino == null)
            throw new IllegalArgumentException("Peso o destino inválido.");
        this.codigo = codigo == null ? "" : codigo.trim();
        this.remitente = remitente.trim();
        this.destinatario = destinatario.trim();
        this.pesoReal = pesoReal;
        this.destino = destino;
        estado = EstadoEnvio.BODEGA;
        intentos = 0;
        ingresoRegistro = 0;
        ingresoReintentos = 0;
    }

    public abstract double calcularCosto();
    public abstract int tiempoEntrega();
    public abstract int maxIntentos();
    public abstract boolean compatible(Vehiculo v);
    public abstract String categoria();

    public boolean puedeReintentar() { return intentos < maxIntentos(); }

    void cobrarRegistro(double monto) {
        if (!(monto > 0) || codigo.isEmpty() || ingresoRegistro != 0
                || estado != EstadoEnvio.BODEGA || intentos != 0)
            throw new IllegalStateException("No se puede cobrar el registro.");
        ingresoRegistro = monto;
    }
    void asignar() {
        if (estado != EstadoEnvio.BODEGA || !puedeReintentar())
            throw new IllegalStateException("El envío no está disponible.");
        estado = EstadoEnvio.ASIGNADO;
    }
    void retirar() {
        if (estado != EstadoEnvio.ASIGNADO)
            throw new IllegalStateException("El envío no está asignado.");
        estado = EstadoEnvio.BODEGA;
    }
    void salir() {
        if (estado != EstadoEnvio.ASIGNADO || !puedeReintentar())
            throw new IllegalStateException("El envío no puede salir.");
        intentos++;
        if (intentos > 1) ingresoReintentos += 15;
        estado = EstadoEnvio.EN_RUTA;
    }
    void resolver(boolean exito) {
        if (estado != EstadoEnvio.EN_RUTA)
            throw new IllegalStateException("El envío no está en ruta.");
        if (exito) estado = EstadoEnvio.ENTREGADO;
        else if (puedeReintentar()) estado = EstadoEnvio.BODEGA;
        else estado = EstadoEnvio.DEVUELTO;
    }

    public String getCodigo() { return codigo; }
    public String getRemitente() { return remitente; }
    public String getDestinatario() { return destinatario; }
    public double getPesoReal() { return pesoReal; }
    public Destino getDestino() { return destino; }
    public EstadoEnvio getEstado() { return estado; }
    public int getIntentos() { return intentos; }
    public double getIngresoRegistro() { return ingresoRegistro; }
    public double getIngresoReintentos() { return ingresoReintentos; }
}
