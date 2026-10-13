public class PaqueteFragil extends Paquete {
    private double valorDeclarado;

    public PaqueteFragil(String codigo, String remitente, String destinatario,
                         double pesoReal, Destino destino, double largo,
                         double ancho, double alto, double valorDeclarado) {
        super(codigo, remitente, destinatario, pesoReal, destino, largo, ancho, alto);
        if (!(valorDeclarado > 0) || valorDeclarado > 1000000)
            throw new IllegalArgumentException("Valor declarado inválido.");
        this.valorDeclarado = valorDeclarado;
    }
    @Override public double calcularCosto() {
        return super.calcularCosto() + 15 + valorDeclarado * 0.03;
    }
    @Override public int tiempoEntrega() { return super.tiempoEntrega() + 1; }
    @Override public int maxIntentos() { return 2; }
    @Override public String categoria() { return "Paquete frágil"; }
}