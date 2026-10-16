import java.nio.file.*;

/** Prueba de consola: demuestra que la clave se guarda con hash + salt. */
public class PruebaSeguridad {
    public static void main(String[] args) throws Exception {
        Path tmp = Files.createTempFile("usuarios", ".txt");
        Files.delete(tmp);
        UsuarioDAO dao = new UsuarioDAOImpl(tmp);

        System.out.println("Demo1234 valida:        " + dao.verificarContrasena("demo@correo.com", "Demo1234"));
        System.out.println("Recuperar (existe):     " + dao.recuperarContrasena("demo@correo.com", "NuevaClave99"));
        System.out.println("Clave vieja valida:     " + dao.verificarContrasena("demo@correo.com", "Demo1234"));
        System.out.println("Clave nueva valida:     " + dao.verificarContrasena("demo@correo.com", "NuevaClave99"));
        System.out.println("Recuperar (no existe):  " + dao.recuperarContrasena("otro@correo.com", "Abcdefg123"));
        String guardado = Files.readAllLines(tmp).get(0);
        System.out.println("Registro guardado:      " + guardado);
        System.out.println("Contiene texto plano:   " + guardado.contains("NuevaClave99"));
        dao.recuperarContrasena("demo@correo.com", "NuevaClave99");
        System.out.println("Mismo texto, otro salt: " + !guardado.equals(Files.readAllLines(tmp).get(0)));
        Files.delete(tmp);
    }
}
