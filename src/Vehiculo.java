public class Vehiculo {
    private String placa;
    private double capacidadKg;
    private boolean refrigerado;

    public Vehiculo(String placa, double capacidadKg, boolean refrigerado) {
        if (placa == null || placa.trim().isEmpty()
                || !(capacidadKg > 0) || capacidadKg > 1000000) {
            throw new IllegalArgumentException("Placa y capacidad del vehículo inválidas.");
        }
        this.placa = placa.trim();
        this.capacidadKg = capacidadKg;
        this.refrigerado = refrigerado;
    }
    public String getPlaca() { return placa; }
    public double getCapacidadKg() { return capacidadKg; }
    public boolean esRefrigerado() { return refrigerado; }
}
