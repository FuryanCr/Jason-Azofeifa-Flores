import java.io.PrintWriter;

/** Registro mínimo de pruebas: imprime PASS o FAIL y lleva la cuenta. */
public class Verificador {

    private final PrintWriter salida;
    private int total;
    private int fallos;

    public Verificador(PrintWriter salida) {
        this.salida = salida;
    }

    public void comprobar(String nombre, boolean cumple) {
        total++;
        if (!cumple) fallos++;
        salida.println((cumple ? "PASS | " : "FAIL | ") + nombre);
    }

    public void nota(String texto) {
        salida.println("INFO | " + texto);
    }

    public int fallos() {
        return fallos;
    }

    public String resumen() {
        return "TOTAL: " + (total - fallos) + "/" + total + " pruebas aprobadas";
    }
}
