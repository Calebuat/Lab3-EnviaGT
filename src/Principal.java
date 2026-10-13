import java.util.Scanner;

public class Principal {
    private SistemaEnvios sistema;
    private Scanner entrada;

    public Principal() {
        sistema = new SistemaEnvios();
        entrada = new Scanner(System.in);
    }

    public static void main(String[] args) {
        new Principal().ejecutar();
    }

    private void ejecutar() {
        int opcion;
        do {
            mostrarMenu();
            try {
                opcion = entero("Opción: ");
                switch (opcion) {
                    case 1: System.out.println(sistema.cotizar(leerEnvio(true))); break;
                    case 2:
                        Envio nuevo = leerEnvio(false);
                        sistema.registrarEnvio(nuevo);
                        System.out.printf("Registrado. Costo: Q%.2f%n", nuevo.getIngresoRegistro());
                        break;
                    case 3: registrarVehiculo(); break;
                    case 4:
                        Despacho creado = sistema.crearDespacho(texto("Placa: "));
                        System.out.println("Despacho #" + creado.getId() + " creado.");
                        break;
                    case 5:
                        despacho().asignar(sistema.buscarEnvio(texto("Código del envío: ")));
                        System.out.println("Envío asignado.");
                        break;
                    case 6:
                        despacho().retirar(sistema.buscarEnvio(texto("Código del envío: ")));
                        System.out.println("Envío retirado; volvió a bodega.");
                        break;
                    case 7:
                        despacho().salir();
                        System.out.println("Despacho en ruta.");
                        break;
                    case 8: registrarResultado(); break;
                    case 9: consultarEnvios(); break;
                    case 10: consultarVehiculos(); break;
                    case 11: System.out.println(sistema.reporteEnvios()); break;
                    case 12: System.out.println(sistema.reporteIngresos()); break;
                    case 13: System.out.println(sistema.reporteDespachos()); break;
                    case 0: System.out.println("Hasta luego."); break;
                    default: System.out.println("Seleccione una opción del menú.");
                }
            } catch (IllegalArgumentException | IllegalStateException ex) {
                if (!entrada.hasNextLine()) {
                    System.out.println("Se terminó la entrada.");
                    return;
                }
                System.out.println("Error: " + ex.getMessage() + " Puede intentarlo de nuevo.");
                opcion = -1;
            }
        } while (opcion != 0);
    }

    private void mostrarMenu() {
        System.out.println("\n=== EnvíaGT ===");
        System.out.println("1 Cotizar         2 Registrar envío      3 Registrar vehículo");
        System.out.println("4 Crear despacho  5 Asignar envío       6 Retirar envío");
        System.out.println("7 Salir a ruta    8 Registrar resultado  9 Consultar envíos");
        System.out.println("10 Ver vehículos  11 Reporte envíos     12 Reporte ingresos");
        System.out.println("13 Despachos abiertos              0 Salir");
    }

    private Envio leerEnvio(boolean cotizacion) {
        System.out.println("Categorías: 1 Sobre, 2 Paquete, 3 Paquete frágil, 4 Carga refrigerada");
        int tipo = entero("Categoría: ");
        if (tipo < 1 || tipo > 4) throw new IllegalArgumentException("Categoría inválida.");
        String codigo = cotizacion ? "" : texto("Código único: ");
        String remitente = texto("Remitente: ");
        String destinatario = texto("Destinatario: ");
        double peso = decimal("Peso real en kg: ");
        System.out.println("Destino: 1 Urbano, 2 Departamental");
        int eleccion = entero("Destino: ");
        if (eleccion < 1 || eleccion > 2) throw new IllegalArgumentException("Destino inválido.");
        Destino destino = eleccion == 1 ? Destino.URBANO : Destino.DEPARTAMENTAL;

        if (tipo == 1) return new Sobre(codigo, remitente, destinatario, peso, destino);
        if (tipo == 4) {
            double temperatura = decimal("Temperatura en °C: ");
            return new CargaRefrigerada(codigo, remitente, destinatario,
                    peso, destino, temperatura);
        }
        double largo = decimal("Largo en cm: ");
        double ancho = decimal("Ancho en cm: ");
        double alto = decimal("Alto en cm: ");
        if (tipo == 2) return new Paquete(codigo, remitente, destinatario,
                peso, destino, largo, ancho, alto);
        double valor = decimal("Valor declarado en Q: ");
        return new PaqueteFragil(codigo, remitente, destinatario,
                peso, destino, largo, ancho, alto, valor);
    }

    private void registrarVehiculo() {
        String placa = texto("Placa: ");
        double capacidad = decimal("Capacidad en kg: ");
        int frio = entero("¿Tiene refrigeración? 1 Sí, 2 No: ");
        if (frio != 1 && frio != 2) throw new IllegalArgumentException("Elija 1 o 2.");
        sistema.registrarVehiculo(new Vehiculo(placa, capacidad, frio == 1));
        System.out.println("Vehículo registrado.");
    }

    private Despacho despacho() {
        return sistema.buscarDespacho(entero("Número de despacho: "));
    }

    private void registrarResultado() {
        Despacho d = despacho();
        String codigo = texto("Código del envío: ");
        int resultado = entero("Resultado: 1 Entregado, 2 Fallido: ");
        if (resultado != 1 && resultado != 2) {
            throw new IllegalArgumentException("Elija 1 o 2.");
        }
        d.registrarResultado(codigo, resultado == 1);
        System.out.println("Resultado guardado. Despacho: " + d.getEstado());
    }

    private void consultarEnvios() {
        for (int i = 0; i < sistema.getCantidadEnvios(); i++) {
            Envio e = sistema.getEnvio(i);
            System.out.printf("%s | %s | %s -> %s | %.2f kg | %s | intentos %d%n",
                    e.getCodigo(), e.categoria(), e.getRemitente(), e.getDestinatario(),
                    e.getPesoReal(), e.getEstado(), e.getIntentos());
        }
    }

    private void consultarVehiculos() {
        for (int i = 0; i < sistema.getCantidadVehiculos(); i++) {
            Vehiculo v = sistema.getVehiculo(i);
            System.out.printf("%s | capacidad %.2f kg | refrigerado: %s%n",
                    v.getPlaca(), v.getCapacidadKg(), v.esRefrigerado() ? "sí" : "no");
        }
    }

    private String texto(String pregunta) {
        System.out.print(pregunta);
        if (!entrada.hasNextLine()) throw new IllegalStateException("Sin más datos.");
        return entrada.nextLine().trim();
    }

    private int entero(String pregunta) {
        try { return Integer.parseInt(texto(pregunta)); }
        catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Ingrese un número entero.");
        }
    }

    private double decimal(String pregunta) {
        try { return Double.parseDouble(texto(pregunta).replace(',', '.')); }
        catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Ingrese un número decimal válido.");
        }
    }

}