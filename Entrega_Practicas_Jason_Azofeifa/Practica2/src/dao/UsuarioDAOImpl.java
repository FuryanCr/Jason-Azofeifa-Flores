package dao;

import util.SeguridadClave;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

/**
 * Guarda los usuarios en un archivo de texto, una línea por usuario:
 * correo;salt;hash
 */
public class UsuarioDAOImpl implements UsuarioDAO {

    private static final String SEPARADOR = ";";
    private static final String CORREO_DEMO = "demo@correo.com";
    private static final String CLAVE_DEMO = "Demo1234";

    private final Path archivo;

    public UsuarioDAOImpl() {
        this(Paths.get("usuarios.txt"));
    }

    public UsuarioDAOImpl(Path archivo) {
        this.archivo = archivo;
        if (!Files.exists(archivo)) {
            crearUsuarioDemo();
        }
    }

    @Override
    public synchronized boolean recuperarContrasena(String correo, String nuevaContrasena) {
        if (correo == null || nuevaContrasena == null || nuevaContrasena.isBlank()) {
            return false;
        }
        String clave = normalizar(correo);
        try {
            List<String> lineas = leer();
            for (int i = 0; i < lineas.size(); i++) {
                String[] campos = lineas.get(i).split(SEPARADOR);
                if (campos.length == 3 && campos[0].equals(clave)) {
                    String salt = SeguridadClave.nuevoSalt();
                    lineas.set(i, registro(clave, salt, SeguridadClave.hash(nuevaContrasena, salt)));
                    guardar(lineas);
                    return true;
                }
            }
        } catch (IOException e) {
            System.err.println("No se pudo actualizar " + archivo + ": " + e.getMessage());
        }
        return false;
    }

    @Override
    public synchronized boolean verificarContrasena(String correo, String contrasena) {
        if (correo == null || contrasena == null) {
            return false;
        }
        String clave = normalizar(correo);
        try {
            for (String linea : leer()) {
                String[] campos = linea.split(SEPARADOR);
                if (campos.length == 3 && campos[0].equals(clave)) {
                    return SeguridadClave.coincide(contrasena, campos[1], campos[2]);
                }
            }
        } catch (IOException e) {
            System.err.println("No se pudo leer " + archivo + ": " + e.getMessage());
        }
        return false;
    }

    private static String normalizar(String correo) {
        return correo.trim().toLowerCase();
    }

    private static String registro(String correo, String salt, String hash) {
        return String.join(SEPARADOR, correo, salt, hash);
    }

    private List<String> leer() throws IOException {
        List<String> lineas = new ArrayList<>(Files.readAllLines(archivo, StandardCharsets.UTF_8));
        lineas.removeIf(String::isBlank);
        return lineas;
    }

    // Se escribe a un archivo temporal y se renombra, así un corte a mitad de
    // la escritura no deja usuarios.txt a medias.
    private void guardar(List<String> lineas) throws IOException {
        Path temporal = archivo.resolveSibling(archivo.getFileName() + ".tmp");
        Files.write(temporal, lineas, StandardCharsets.UTF_8);
        try {
            Files.move(temporal, archivo, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException e) {
            Files.move(temporal, archivo, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private void crearUsuarioDemo() {
        String salt = SeguridadClave.nuevoSalt();
        try {
            guardar(new ArrayList<>(List.of(
                    registro(CORREO_DEMO, salt, SeguridadClave.hash(CLAVE_DEMO, salt)))));
        } catch (IOException e) {
            System.err.println("No se pudo crear " + archivo + ": " + e.getMessage());
        }
    }
}
