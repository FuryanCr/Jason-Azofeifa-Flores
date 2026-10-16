import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación de UsuarioDAO. Persiste en un archivo de texto
 * (usuarios.txt) con el formato:  correo;salt;hash
 * La contraseña NUNCA se guarda en texto plano.
 */
public class UsuarioDAOImpl implements UsuarioDAO {

    private static final String SEP = ";";
    private final Path archivo;

    public UsuarioDAOImpl() {
        this(Paths.get("usuarios.txt"));
    }

    public UsuarioDAOImpl(Path archivo) {
        this.archivo = archivo;
        crearUsuarioDemoSiNoExiste();
    }

    @Override
    public synchronized boolean recuperarContrasena(String correo, String nuevaContrasena) {
        if (correo == null || nuevaContrasena == null) {
            return false;
        }
        String clave = correo.trim().toLowerCase();
        try {
            List<String> lineas = leer();
            boolean encontrado = false;
            for (int i = 0; i < lineas.size(); i++) {
                String[] p = lineas.get(i).split(SEP);
                if (p.length == 3 && p[0].equals(clave)) {
                    String salt = SeguridadClave.generarSalt(); // salt nuevo en cada cambio
                    String hash = SeguridadClave.hash(nuevaContrasena, salt);
                    lineas.set(i, clave + SEP + salt + SEP + hash);
                    encontrado = true;
                    break;
                }
            }
            if (encontrado) {
                Files.write(archivo, lineas, StandardCharsets.UTF_8);
            }
            return encontrado;
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public synchronized boolean verificarContrasena(String correo, String contrasena) {
        String clave = correo.trim().toLowerCase();
        try {
            for (String linea : leer()) {
                String[] p = linea.split(SEP);
                if (p.length == 3 && p[0].equals(clave)) {
                    return SeguridadClave.coincide(contrasena, p[1], p[2]);
                }
            }
        } catch (IOException ignorada) { }
        return false;
    }

    private List<String> leer() throws IOException {
        if (!Files.exists(archivo)) {
            return new ArrayList<>();
        }
        List<String> lineas = new ArrayList<>(Files.readAllLines(archivo, StandardCharsets.UTF_8));
        lineas.removeIf(String::isBlank);
        return lineas;
    }

    /** Cuenta de prueba para poder demostrar el flujo (clave inicial: Demo1234). */
    private void crearUsuarioDemoSiNoExiste() {
        if (Files.exists(archivo)) {
            return;
        }
        String salt = SeguridadClave.generarSalt();
        String linea = "demo@correo.com" + SEP + salt + SEP + SeguridadClave.hash("Demo1234", salt);
        try {
            Files.write(archivo, List.of(linea), StandardCharsets.UTF_8);
        } catch (IOException ignorada) { }
    }
}
