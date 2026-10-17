import dao.UsuarioDAO;
import dao.UsuarioDAOImpl;
import util.Validador;

import java.io.PrintWriter;

/** Pruebas sin ventanas: validación de correo y estructura del DAO. */
public class PruebasLogica {

    public static void main(String[] args) throws Exception {
        PrintWriter out = args.length > 0 ? new PrintWriter(args[0], "UTF-8") : new PrintWriter(System.out, true);
        Verificador v = new Verificador(out);

        for (String bueno : new String[]{"a@b.co", "nombre.apellido@dominio.com", "x_y+z@sub.dominio.cr", "A1@mail-2.org"}) {
            v.comprobar("Correo válido aceptado: " + bueno, Validador.correoValido(bueno));
        }
        String[] malos = {"correo", "correo@", "@dominio.com", "a@b", "a b@c.com", "a@b..", "a@b.c",
                "a@..b.com", "a@x..com", "a@-x.com", "a@x-.com", "..@x.com", "a..b@x.com", ".a@x.com", "a.@x.com", ""};
        for (String malo : malos) {
            v.comprobar("Correo inválido rechazado: \"" + malo + "\"", !Validador.correoValido(malo));
        }
        v.comprobar("Correo null rechazado", !Validador.correoValido(null));
        v.comprobar("estaVacio: null, vacío y solo espacios", Validador.estaVacio(null) && Validador.estaVacio("") && Validador.estaVacio("  \t "));
        v.comprobar("estaVacio: texto con contenido", !Validador.estaVacio(" a "));

        UsuarioDAO dao = new UsuarioDAOImpl();
        v.comprobar("UsuarioDAO declara recuperarContrasena(String, String)",
                UsuarioDAO.class.getMethod("recuperarContrasena", String.class, String.class) != null);
        v.comprobar("UsuarioDAOImpl implementa UsuarioDAO", UsuarioDAO.class.isAssignableFrom(UsuarioDAOImpl.class));
        v.comprobar("DAO: recuperarContrasena(usuario, correo) devuelve true", dao.recuperarContrasena("jazofeifa", "j@correo.com"));
        v.comprobar("DAO: con null devuelve false", !dao.recuperarContrasena(null, "j@correo.com") && !dao.recuperarContrasena("j", null));

        out.println(v.resumen());
        out.flush();
        System.exit(v.fallos() == 0 ? 0 : 1);
    }
}
