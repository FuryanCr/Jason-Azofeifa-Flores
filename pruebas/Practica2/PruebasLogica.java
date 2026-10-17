import dao.UsuarioDAO;
import clases.ClsSeguridad;
import clases.ClsValidador;

import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/** Pruebas sin ventanas: validación, hash con salt y DAO. Uso: java PruebasLogica [archivo-salida] */
public class PruebasLogica {

    public static void main(String[] args) throws Exception {
        PrintWriter out = args.length > 0 ? new PrintWriter(args[0], "UTF-8") : new PrintWriter(System.out, true);
        Verificador v = new Verificador(out);

        correos(v);
        fuerza(v);
        hash(v);
        dao(v);

        out.println(v.resumen());
        out.flush();
        System.exit(v.fallos() == 0 ? 0 : 1);
    }

    private static void correos(Verificador v) {
        for (String bueno : new String[]{"a@b.co", "nombre.apellido@dominio.com", "x_y+z@sub.dominio.cr", "A1@mail-2.org"}) {
            v.comprobar("Correo válido aceptado: " + bueno, ClsValidador.correoValido(bueno));
        }
        String[] malos = {"correo", "correo@", "@dominio.com", "a@b", "a b@c.com", "a@b..", "a@b.c",
                "a@..b.com", "a@x..com", "a@-x.com", "a@x-.com", "..@x.com", "a..b@x.com", ".a@x.com", "a.@x.com", "", "a@b.com."};
        for (String malo : malos) {
            v.comprobar("Correo inválido rechazado: \"" + malo + "\"", !ClsValidador.correoValido(malo));
        }
        v.comprobar("Correo null rechazado", !ClsValidador.correoValido(null));
        v.comprobar("Correo de más de 254 caracteres rechazado", !ClsValidador.correoValido("a".repeat(250) + "@b.com"));
        v.comprobar("estaVacio: null, vacío y solo espacios", ClsValidador.estaVacio(null) && ClsValidador.estaVacio("") && ClsValidador.estaVacio("  \t "));
        v.comprobar("estaVacio: texto con contenido", !ClsValidador.estaVacio(" a "));
    }

    private static void fuerza(Verificador v) {
        v.comprobar("Fuerza de vacío = 0", ClsValidador.fuerza("") == 0 && ClsValidador.fuerza(null) == 0);
        v.comprobar("Fuerza de contraseña corta = 1", ClsValidador.fuerza("abc") == 1);
        v.comprobar("Fuerza de Demo1234 = 2", ClsValidador.fuerza("Demo1234") == 2);
        v.comprobar("Fuerza de MiClave2026x = 3", ClsValidador.fuerza("MiClave2026x") == 3);
        v.comprobar("Fuerza de MiClave#2026x = 4", ClsValidador.fuerza("MiClave#2026x") == 4);
    }

    private static void hash(Verificador v) throws Exception {
        String salt = ClsSeguridad.nuevoSalt();
        String h = ClsSeguridad.hash("Clave12345", salt);
        v.comprobar("Salt de 16 bytes", Base64.getDecoder().decode(salt).length == 16);
        v.comprobar("Hash de 32 bytes (SHA-256)", Base64.getDecoder().decode(h).length == 32);

        java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
        md.update(Base64.getDecoder().decode(salt));
        String esperado = Base64.getEncoder().encodeToString(md.digest("Clave12345".getBytes(StandardCharsets.UTF_8)));
        v.comprobar("Hash == SHA-256(salt + contraseña) calculado aparte", h.equals(esperado));
        v.comprobar("Mismo salt y contraseña dan el mismo hash", h.equals(ClsSeguridad.hash("Clave12345", salt)));
        v.comprobar("Otro salt cambia el hash", !h.equals(ClsSeguridad.hash("Clave12345", ClsSeguridad.nuevoSalt())));
        v.comprobar("coincide() acepta la correcta", ClsSeguridad.coincide("Clave12345", salt, h));
        v.comprobar("coincide() rechaza la incorrecta", !ClsSeguridad.coincide("clave12345", salt, h));

        Set<String> salts = new HashSet<>();
        for (int i = 0; i < 2000; i++) salts.add(ClsSeguridad.nuevoSalt());
        v.comprobar("2000 salts generados, todos distintos", salts.size() == 2000);
    }

    private static void dao(Verificador v) throws Exception {
        Path dir = Files.createTempDirectory("dao");
        Path archivo = dir.resolve("usuarios.txt");
        UsuarioDAO dao = new UsuarioDAO(archivo);

        v.comprobar("Se crea el archivo con la cuenta demo", Files.exists(archivo));
        v.comprobar("Cuenta demo entra con Demo1234", dao.verificarContrasena("demo@correo.com", "Demo1234"));

        String antes = Files.readAllLines(archivo).get(0);
        v.comprobar("recuperarContrasena devuelve true si el correo existe", dao.recuperarContrasena("demo@correo.com", "NuevaClave99"));
        String despues = Files.readAllLines(archivo).get(0);
        v.nota("Registro antes:   " + antes);
        v.nota("Registro después: " + despues);

        String[] a = antes.split(";"), d = despues.split(";");
        v.comprobar("Formato correo;salt;hash", d.length == 3 && d[0].equals("demo@correo.com"));
        v.comprobar("El archivo no contiene la contraseña en texto plano", !despues.contains("NuevaClave99"));
        v.comprobar("Salt nuevo en cada cambio", !a[1].equals(d[1]));
        v.comprobar("Hash distinto tras el cambio", !a[2].equals(d[2]));
        v.comprobar("Clave nueva entra", dao.verificarContrasena("demo@correo.com", "NuevaClave99"));
        v.comprobar("Clave anterior ya no entra", !dao.verificarContrasena("demo@correo.com", "Demo1234"));
        v.comprobar("No queda archivo temporal tras guardar", !Files.exists(dir.resolve("usuarios.txt.tmp")));

        v.comprobar("Correo se compara sin importar mayúsculas ni espacios", dao.recuperarContrasena("  DEMO@Correo.COM ", "OtraClave88")
                && dao.verificarContrasena("demo@correo.com", "OtraClave88"));
        v.comprobar("Correo inexistente devuelve false", !dao.recuperarContrasena("nadie@correo.com", "Abcdefg123"));
        v.comprobar("Correo null devuelve false", !dao.recuperarContrasena(null, "Abcdefg123"));
        v.comprobar("Contraseña null devuelve false", !dao.recuperarContrasena("demo@correo.com", null));
        v.comprobar("Contraseña en blanco devuelve false y no cambia nada", !dao.recuperarContrasena("demo@correo.com", "     ")
                && dao.verificarContrasena("demo@correo.com", "OtraClave88"));
        v.comprobar("verificarContrasena con null devuelve false", !dao.verificarContrasena(null, "x") && !dao.verificarContrasena("demo@correo.com", null));
        v.comprobar("Contraseña con tildes y símbolos", dao.recuperarContrasena("demo@correo.com", "Ñandú#2026!")
                && dao.verificarContrasena("demo@correo.com", "Ñandú#2026!"));

        // varios usuarios: solo cambia el indicado
        Files.write(archivo, List.of(Files.readAllLines(archivo).get(0), "otro@correo.com;" + ClsSeguridad.nuevoSalt() + ";x"), StandardCharsets.UTF_8);
        String otroAntes = Files.readAllLines(archivo).get(1);
        dao.recuperarContrasena("demo@correo.com", "Cambio12345");
        v.comprobar("Con varios usuarios solo cambia el solicitado", Files.readAllLines(archivo).get(1).equals(otroAntes) && Files.readAllLines(archivo).size() == 2);

        // un archivo con líneas rotas no tumba al DAO
        Files.write(archivo, List.of("basura", "", "demo@correo.com;solo-dos"), StandardCharsets.UTF_8);
        v.comprobar("Archivo con líneas dañadas: no lanza error y devuelve false", !dao.recuperarContrasena("demo@correo.com", "Abcdefg123"));

        // acceso concurrente
        Path conc = dir.resolve("conc.txt");
        UsuarioDAO d2 = new UsuarioDAO(conc);
        ExecutorService pool = Executors.newFixedThreadPool(8);
        for (int i = 0; i < 40; i++) {
            final String clave = "Clave-Num-" + i;
            pool.submit(() -> d2.recuperarContrasena("demo@correo.com", clave));
        }
        pool.shutdown();
        pool.awaitTermination(20, TimeUnit.SECONDS);
        List<String> lineas = Files.readAllLines(conc);
        boolean alguna = false;
        for (int i = 0; i < 40; i++) alguna |= d2.verificarContrasena("demo@correo.com", "Clave-Num-" + i);
        v.comprobar("40 cambios simultáneos: el archivo queda con 1 línea íntegra", lineas.size() == 1 && lineas.get(0).split(";").length == 3 && alguna);
    }
}
