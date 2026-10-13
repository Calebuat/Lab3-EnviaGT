public class Paquete extends Envio {
    private double largo, ancho, alto;

    public Paquete(String codigo, String remitente, String destinatario,
                   double pesoReal, Destino destino,
                   double largo, double ancho, double alto) {
        super(codigo, remitente, destinatario, pesoReal, destino);
        if (!(largo > 0) || largo > 1000 || !(ancho > 0) || ancho > 1000
                || !(alto > 0) || alto > 1000)
            throw new IllegalArgumentException("Dimensiones inválidas (máximo 1000 cm).");
        this.largo = largo;
        this.ancho = ancho;
        this.alto = alto;
    }

    public double pesoVolumetrico() { return largo * ancho * alto / 5000; }
    public double pesoFacturable() {
        if (pesoVolumetrico() > getPesoReal()) return pesoVolumetrico();
        return getPesoReal();
    }
    @Override public double calcularCosto() {
        double kilos = pesoFacturable();
        int kilosCobrados = (int) kilos;
        if (kilos > kilosCobrados) kilosCobrados++;
        double base = getDestino() == Destino.URBANO ? 25 : 40;
        return base + 8 * kilosCobrados;
    }
    @Override public int tiempoEntrega() {
        return getDestino() == Destino.URBANO ? 2 : 4;
    }
    @Override public int maxIntentos() { return 3; }
    @Override public boolean compatible(Vehiculo v) { return v != null; }
    @Override public String categoria() { return "Paquete"; }
}