public class CargaRefrigerada extends Envio {
    private double temperatura;

    public CargaRefrigerada(String codigo, String remitente, String destinatario,
                            double pesoReal, Destino destino, double temperatura) {
        super(codigo, remitente, destinatario, pesoReal, destino);
        if (destino != Destino.URBANO || !(temperatura >= -25)
                || !(temperatura <= 8))
            throw new IllegalArgumentException("Requiere destino urbano y -25 a 8 °C.");
        this.temperatura = temperatura;
    }
    @Override public double calcularCosto() {
        return 25 + 12 * getPesoReal() + 60;
    }
    @Override public int tiempoEntrega() { return 1; }
    @Override public int maxIntentos() { return 1; }
    @Override public boolean compatible(Vehiculo v) {
        return v != null && v.esRefrigerado();
    }
    @Override public String categoria() { return "Carga refrigerada"; }
}
