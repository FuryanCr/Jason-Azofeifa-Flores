import dao.UsuarioDAO;
import formularios.FrmLogin;
import formularios.FrmOlvido;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Abre las ventanas de verdad y las maneja por reflexión.
 * Uso: java PruebasVentana carpeta-de-salida   (necesita pantalla, real o virtual)
 */
public class PruebasVentana {

    private static Verificador v;
    private static File carpeta;

    public static void main(String[] args) throws Exception {
        carpeta = new File(args.length > 0 ? args[0] : "evidencia");
        carpeta.mkdirs();
        PrintWriter out = new PrintWriter(new File(carpeta, "ventanas_resultados.txt"), "UTF-8");
        v = new Verificador(out);

        Path temporal = Files.createTempDirectory("ventanas");
        Path archivo = temporal.resolve("usuarios.txt");
        olvido(archivo);
        login();

        out.println(v.resumen());
        out.close();
        System.exit(v.fallos() == 0 ? 0 : 1);
    }

    private static void olvido(Path archivo) throws Exception {
        UsuarioDAO dao = new UsuarioDAO(archivo);
        FrmOlvido f = new FrmOlvido(dao);
        f.setVisible(true);
        Thread.sleep(300);
        foto(f, "olvido_inicio.png");

        Dimension pantalla = Toolkit.getDefaultToolkit().getScreenSize();
        v.comprobar("FrmOlvido es un JFrame", f instanceof JFrame);
        v.comprobar("Aparece centrada en la pantalla",
                Math.abs(f.getX() - (pantalla.width - f.getWidth()) / 2) <= 1
                        && Math.abs(f.getY() - (pantalla.height - f.getHeight()) / 2) <= 1);
        v.comprobar("Resizable = false", !f.isResizable());
        v.comprobar("Cierre = DISPOSE_ON_CLOSE", f.getDefaultCloseOperation() == WindowConstants.DISPOSE_ON_CLOSE);

        Dimension tamano = f.getSize();
        pulsar(f, "btnGuardar");
        v.comprobar("Formulario vacío: marca los tres campos a la vez",
                !texto(f, "errCorreo").isEmpty() && !texto(f, "errNueva").isEmpty() && !texto(f, "errConfirmar").isEmpty());
        v.comprobar("Mensajes de campo vacío: \"" + texto(f, "errCorreo") + "\" / \"" + texto(f, "errNueva") + "\" / \"" + texto(f, "errConfirmar") + "\"",
                texto(f, "errCorreo").contains("correo") && texto(f, "errNueva").contains("contraseña"));
        v.comprobar("La ventana no cambia de tamaño al mostrar errores", f.getSize().equals(tamano));
        foto(f, "olvido_vacio.png");

        poner(f, "txtCorreo", "    ");
        pulsar(f, "btnGuardar");
        v.comprobar("Correo solo con espacios se rechaza como vacío", texto(f, "errCorreo").contains("Escriba"));

        for (String malo : new String[]{"correo", "correo@", "@dominio.com", "a@b", "a b@c.com", "a@..b.com", "a@x..com", "a@-x.com", "..@x.com"}) {
            poner(f, "txtCorreo", malo);
            pulsar(f, "btnGuardar");
            v.comprobar("Correo con formato inválido \"" + malo + "\" -> " + texto(f, "errCorreo"), texto(f, "errCorreo").contains("formato"));
        }
        poner(f, "txtCorreo", "demo@correo");
        poner(f, "txtNueva", "abc"); poner(f, "txtConfirmar", "abd");
        pulsar(f, "btnGuardar");
        foto(f, "olvido_errores.png");

        poner(f, "txtNueva", "          "); poner(f, "txtConfirmar", "          ");
        pulsar(f, "btnGuardar");
        v.comprobar("Contraseña de solo espacios -> " + texto(f, "errNueva"), texto(f, "errNueva").contains("espacios"));
        poner(f, "txtNueva", "corta1"); poner(f, "txtConfirmar", "corta1");
        pulsar(f, "btnGuardar");
        v.comprobar("Contraseña menor a 8 caracteres -> " + texto(f, "errNueva"), texto(f, "errNueva").contains("8 caracteres"));
        poner(f, "txtCorreo", "demo@correo.com");
        poner(f, "txtNueva", "Clave12345"); poner(f, "txtConfirmar", "");
        pulsar(f, "btnGuardar");
        v.comprobar("Sin confirmación -> " + texto(f, "errConfirmar"), texto(f, "errConfirmar").contains("Repita"));
        poner(f, "txtConfirmar", "Otra12345");
        pulsar(f, "btnGuardar");
        v.comprobar("Confirmación distinta -> " + texto(f, "errConfirmar"), texto(f, "errConfirmar").contains("no coinciden"));
        v.comprobar("Con errores el hash guardado no cambia", dao.verificarContrasena("demo@correo.com", "Demo1234"));
        v.comprobar("La ventana sigue abierta mientras hay errores", f.isVisible());

        poner(f, "txtConfirmar", "Clave12345");
        v.comprobar("El error de un campo se borra al corregirlo", texto(f, "errConfirmar").isEmpty());

        // medidor de fuerza
        MedidorNivel m = new MedidorNivel(f);
        poner(f, "txtNueva", "abc");           v.comprobar("Medidor: \"abc\" nivel 1", m.nivel() == 1);
        poner(f, "txtNueva", "Demo1234");      v.comprobar("Medidor: \"Demo1234\" nivel 2", m.nivel() == 2);
        poner(f, "txtNueva", "MiClave#2026x"); v.comprobar("Medidor: \"MiClave#2026x\" nivel 4", m.nivel() == 4);
        poner(f, "txtNueva", "");              v.comprobar("Medidor: campo vacío nivel 0", m.nivel() == 0);

        // ojito
        JPasswordField nueva = (JPasswordField) campo(f, "txtNueva");
        char antes = nueva.getEchoChar();
        ((componentes.CampoClave) nueva).alternarVisibilidad();
        v.comprobar("El ojito muestra la contraseña", antes != 0 && nueva.getEchoChar() == 0);
        ((componentes.CampoClave) nueva).alternarVisibilidad();
        v.comprobar("El ojito vuelve a ocultarla", nueva.getEchoChar() == antes);

        // correo que no existe
        poner(f, "txtCorreo", "nadie@correo.com");
        poner(f, "txtNueva", "Clave12345"); poner(f, "txtConfirmar", "Clave12345");
        pulsar(f, "btnGuardar");
        v.comprobar("Correo inexistente -> " + texto(f, "errCorreo"), texto(f, "errCorreo").contains("ninguna cuenta"));
        foto(f, "olvido_noexiste.png");

        // éxito
        String registroAntes = Files.readAllLines(archivo).get(0);
        poner(f, "txtCorreo", " DEMO@correo.com ");
        poner(f, "txtNueva", "MiClave#2026x"); poner(f, "txtConfirmar", "MiClave#2026x");
        foto(f, "olvido_fuerte.png");
        pulsar(f, "btnGuardar");
        Thread.sleep(200);
        foto(f, "olvido_listo.png");
        v.comprobar("Flujo exitoso: aparece la pantalla de confirmación", pantallaConfirmacionVisible(f));
        v.comprobar("Flujo exitoso: la clave nueva entra y la anterior no", dao.verificarContrasena("demo@correo.com", "MiClave#2026x")
                && !dao.verificarContrasena("demo@correo.com", "Demo1234"));
        String registroDespues = Files.readAllLines(archivo).get(0);
        v.nota("Registro antes:   " + registroAntes);
        v.nota("Registro después: " + registroDespues);
        v.comprobar("El archivo no contiene la contraseña en texto plano", !registroDespues.contains("MiClave#2026x"));
        v.comprobar("El hash cambió", !registroAntes.equals(registroDespues));

        // cierre
        f.dispatchEvent(new WindowEvent(f, WindowEvent.WINDOW_CLOSING));
        Thread.sleep(200);
        v.comprobar("Cerrar con la X cierra solo esta ventana (la JVM sigue viva)", !f.isVisible());

        FrmOlvido g = new FrmOlvido(dao);
        g.setVisible(true);
        KeyStroke esc = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        Object clave = g.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).get(esc);
        g.getRootPane().getActionMap().get(clave).actionPerformed(null);
        Thread.sleep(150);
        v.comprobar("La tecla Esc cierra la ventana", !g.isDisplayable());
    }

    private static void login() throws Exception {
        FrmLogin l = new FrmLogin();
        l.setVisible(true);
        Thread.sleep(300);
        foto(l, "login_inicio.png");
        v.comprobar("FrmLogin se muestra centrada y sin redimensionar", l.isVisible() && !l.isResizable());

        pulsar(l, "btnIngresar");
        v.comprobar("Login vacío -> " + texto(l, "errCorreo"), texto(l, "errCorreo").contains("correo"));
        poner(l, "txtCorreo", "demo@correo.com");
        pulsar(l, "btnIngresar");
        v.comprobar("Login sin contraseña -> " + texto(l, "errClave"), texto(l, "errClave").contains("contraseña"));
        poner(l, "txtClave", "incorrecta");
        pulsar(l, "btnIngresar");
        v.comprobar("Login con contraseña incorrecta -> " + texto(l, "errClave"), texto(l, "errClave").equals("Correo o contraseña incorrectos."));
        foto(l, "login_error.png");
        poner(l, "txtClave", "Demo1234");
        pulsar(l, "btnIngresar");
        v.comprobar("Login con la cuenta demo entra -> " + texto(l, "errClave"), texto(l, "errClave").startsWith("Acceso correcto"));
        foto(l, "login_ok.png");

        // recorrido completo: cambiar la contraseña desde el enlace y volver a entrar con ella
        Method abrirOlvido = FrmLogin.class.getDeclaredMethod("abrirOlvido");
        abrirOlvido.setAccessible(true);
        abrirOlvido.invoke(l);
        FrmOlvido olvido = (FrmOlvido) get(l, "ventanaOlvido");
        poner(olvido, "txtCorreo", "demo@correo.com");
        poner(olvido, "txtNueva", "MiClave#2026x");
        poner(olvido, "txtConfirmar", "MiClave#2026x");
        pulsar(olvido, "btnGuardar");
        v.comprobar("Recorrido completo: el cambio desde FrmOlvido se aplica", pantallaConfirmacionVisible(olvido));
        poner(l, "txtClave", "Demo1234");
        pulsar(l, "btnIngresar");
        v.comprobar("Recorrido completo: la contraseña vieja ya no entra", texto(l, "errClave").equals("Correo o contraseña incorrectos."));
        poner(l, "txtClave", "MiClave#2026x");
        pulsar(l, "btnIngresar");
        v.comprobar("Recorrido completo: la contraseña nueva entra -> " + texto(l, "errClave"), texto(l, "errClave").startsWith("Acceso correcto"));
        foto(l, "login_nueva_clave.png");
        olvido.dispose();

        Method abrir = FrmLogin.class.getDeclaredMethod("abrirOlvido");
        abrir.setAccessible(true);
        abrir.invoke(l);
        abrir.invoke(l);
        abrir.invoke(l);
        int abiertas = 0;
        for (Frame fr : Frame.getFrames()) {
            if (fr instanceof FrmOlvido && fr.isVisible()) abiertas++;
        }
        v.comprobar("Pulsar el enlace varias veces abre una sola ventana FrmOlvido", abiertas == 1);
        for (Frame fr : Frame.getFrames()) {
            if (fr instanceof FrmOlvido) fr.dispose();
        }
        v.comprobar("Al cerrar FrmOlvido el login principal sigue abierto", l.isVisible());
    }

    // ---- utilidades de reflexión y captura ----

    private static Object get(Object o, String nombre) throws Exception {
        Field f = o.getClass().getDeclaredField(nombre);
        f.setAccessible(true);
        return f.get(o);
    }

    private static JTextComponent campo(Object o, String nombre) throws Exception {
        return (JTextComponent) get(o, nombre);
    }

    private static void poner(Object o, String nombre, String valor) throws Exception {
        campo(o, nombre).setText(valor);
    }

    private static String texto(Object o, String etiqueta) throws Exception {
        return ((JLabel) get(o, etiqueta)).getText().replaceAll("<[^>]*>", "").trim();
    }

    private static void pulsar(Object o, String boton) throws Exception {
        ((JButton) get(o, boton)).doClick();
        Thread.sleep(120);
    }

    private static boolean pantallaConfirmacionVisible(FrmOlvido f) throws Exception {
        JPanel lado = (JPanel) get(f, "lado");
        for (Component c : lado.getComponents()) {
            if (c.isVisible() && contiene(c, componentes.Sello.class)) return true;
        }
        return false;
    }

    private static boolean contiene(Component c, Class<?> clase) {
        if (clase.isInstance(c)) return true;
        if (c instanceof Container) {
            for (Component h : ((Container) c).getComponents()) {
                if (contiene(h, clase)) return true;
            }
        }
        return false;
    }

    private static void foto(Window w, String nombre) throws Exception {
        Thread.sleep(200);
        BufferedImage img = new BufferedImage(w.getWidth(), w.getHeight(), BufferedImage.TYPE_INT_RGB);
        w.paint(img.getGraphics());
        ImageIO.write(img, "png", new File(carpeta, nombre));
    }

    private static class MedidorNivel {
        private final componentes.MedidorClave medidor;
        MedidorNivel(FrmOlvido f) throws Exception { medidor = (componentes.MedidorClave) get(f, "medidor"); }
        int nivel() { return medidor.getNivel(); }
    }
}
