import dao.UsuarioDAO;
import interfaces.IAutenticacion;
import clases.ClsValidador;

import java.io.PrintWriter;

/** Pruebas sin ventanas: validación de correo y estructura del DAO. */
public class PruebasLogica {

    public static void main(String[] args) throws Exception {
        PrintWriter out = args.length > 0 ? new PrintWriter(args[0], "UTF-8") : new PrintWriter(System.out, true);
        Verificador v = new Verificador(out);

        for (String bueno : new String[]{"a@b.co", "nombre.apellido@dominio.com", "x_y+z@sub.dominio.cr", "A1@mail-2.org"}) {
            v.comprobar("Correo válido aceptado: " + bueno, ClsValidador.correoValido(bueno));
        }
        String[] malos = {"correo", "correo@", "@dominio.com", "a@b", "a b@c.com", "a@b..", "a@b.c",
                "a@..b.com", "a@x..com", "a@-x.com", "a@x-.com", "..@x.com", "a..b@x.com", ".a@x.com", "a.@x.com", ""};
        for (String malo : malos) {
            v.comprobar("Correo inválido rechazado: \"" + malo + "\"", !ClsValidador.correoValido(malo));
        }
        v.comprobar("Correo null rechazado", !ClsValidador.correoValido(null));
        v.comprobar("estaVacio: null, vacío y solo espacios", ClsValidador.estaVacio(null) && ClsValidador.estaVacio("") && ClsValidador.estaVacio("  \t "));
        v.comprobar("estaVacio: texto con contenido", !ClsValidador.estaVacio(" a "));

        UsuarioDAO dao = new UsuarioDAO();
        v.comprobar("IAutenticacion declara recuperarContrasena(String, String)",
                IAutenticacion.class.isInterface()
                        && IAutenticacion.class.getMethod("recuperarContrasena", String.class, String.class) != null);
        v.comprobar("UsuarioDAO implementa IAutenticacion", IAutenticacion.class.isAssignableFrom(UsuarioDAO.class));
        v.comprobar("DAO: recuperarContrasena(usuario, correo) devuelve true", dao.recuperarContrasena("jazofeifa", "j@correo.com"));
        v.comprobar("DAO: con null devuelve false", !dao.recuperarContrasena(null, "j@correo.com") && !dao.recuperarContrasena("j", null));

        out.println(v.resumen());
        out.flush();
        System.exit(v.fallos() == 0 ? 0 : 1);
    }
}
