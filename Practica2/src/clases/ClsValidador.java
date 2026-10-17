package clases;

import java.util.regex.Pattern;

public final class ClsValidador {

    public static final int MIN_CLAVE = 8;
    private static final int MAX_CORREO = 254;
    private static final String[] ETIQUETAS = {"", "Débil", "Aceptable", "Buena", "Fuerte"};

    // local@dominio.tld sin puntos dobles ni guiones al inicio o final de cada etiqueta
    private static final Pattern CORREO = Pattern.compile(
            "^[A-Za-z0-9]([A-Za-z0-9._%+-]*[A-Za-z0-9])?"
            + "@([A-Za-z0-9]([A-Za-z0-9-]*[A-Za-z0-9])?\\.)+[A-Za-z]{2,}$");

    private ClsValidador() {
    }

    public static boolean estaVacio(String texto) {
        return texto == null || texto.isBlank();
    }

    public static boolean correoValido(String correo) {
        return correo != null
                && correo.length() <= MAX_CORREO
                && !correo.contains("..")
                && CORREO.matcher(correo).matches();
    }

    /** Devuelve 0 si no hay texto y de 1 (débil) a 4 (fuerte) en los demás casos. */
    public static int fuerza(String clave) {
        if (clave == null || clave.isEmpty()) {
            return 0;
        }
        if (clave.length() < MIN_CLAVE) {
            return 1;
        }
        boolean mayus = clave.chars().anyMatch(Character::isUpperCase);
        boolean minus = clave.chars().anyMatch(Character::isLowerCase);
        boolean digito = clave.chars().anyMatch(Character::isDigit);
        boolean simbolo = clave.chars().anyMatch(c -> !Character.isLetterOrDigit(c));

        int puntos = 1;
        if (clave.length() >= 12) puntos++;
        if (mayus && minus && digito) puntos++;
        if (simbolo) puntos++;
        return Math.min(puntos, 4);
    }

    public static String etiquetaFuerza(int nivel) {
        return ETIQUETAS[Math.max(0, Math.min(nivel, ETIQUETAS.length - 1))];
    }
}
