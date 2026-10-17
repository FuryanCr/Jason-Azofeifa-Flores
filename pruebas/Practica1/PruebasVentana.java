import formularios.FrmLogin;
import formularios.FrmOlvido;
import interfaces.IAutenticacion;

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

/** Abre las ventanas de verdad. Uso: java PruebasVentana carpeta-de-salida (necesita pantalla, real o virtual). */
public class PruebasVentana {

    private static Verificador v;
    private static File carpeta;
    private static int llamadasDAO;
    private static String ultimoUsuario, ultimoCorreo;

    public static void main(String[] args) throws Exception {
        carpeta = new File(args.length > 0 ? args[0] : "evidencia");
        carpeta.mkdirs();
        PrintWriter out = new PrintWriter(new File(carpeta, "ventanas_resultados.txt"), "UTF-8");
        v = new Verificador(out);
        olvido();
        login();
        out.println(v.resumen());
        out.close();
        System.exit(v.fallos() == 0 ? 0 : 1);
    }

    private static void olvido() throws Exception {
        IAutenticacion espia = (usuario, correo) -> { llamadasDAO++; ultimoUsuario = usuario; ultimoCorreo = correo; return true; };
        FrmOlvido f = new FrmOlvido(espia);
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
        pulsar(f, "btnRecuperar");
        v.comprobar("Formulario vacío: marca los dos campos -> \"" + texto(f, "errUsuario") + "\" / \"" + texto(f, "errCorreo") + "\"",
                texto(f, "errUsuario").contains("usuario") && texto(f, "errCorreo").contains("correo"));
        v.comprobar("La ventana no cambia de tamaño al mostrar errores", f.getSize().equals(tamano));
        v.comprobar("Con errores no se llama al DAO", llamadasDAO == 0);
        foto(f, "olvido_vacio.png");

        poner(f, "txtUsuario", "     "); poner(f, "txtCorreo", "a@b.com");
        pulsar(f, "btnRecuperar");
        v.comprobar("Usuario solo con espacios se rechaza -> " + texto(f, "errUsuario"), texto(f, "errUsuario").contains("usuario"));
        poner(f, "txtUsuario", "jazofeifa"); poner(f, "txtCorreo", "    ");
        pulsar(f, "btnRecuperar");
        v.comprobar("Correo solo con espacios se rechaza -> " + texto(f, "errCorreo"), texto(f, "errCorreo").contains("Escriba"));

        for (String malo : new String[]{"correo", "correo@", "@dominio.com", "a@b", "a b@c.com", "a@..b.com", "a@x..com", "a@-x.com"}) {
            poner(f, "txtCorreo", malo);
            pulsar(f, "btnRecuperar");
            v.comprobar("Correo con formato inválido \"" + malo + "\" -> " + texto(f, "errCorreo"), texto(f, "errCorreo").contains("formato"));
        }
        poner(f, "txtCorreo", "correo-malo");
        pulsar(f, "btnRecuperar");
        foto(f, "olvido_correo.png");
        v.comprobar("La ventana sigue abierta y no se llamó al DAO", f.isVisible() && llamadasDAO == 0);
        poner(f, "txtCorreo", "j@correo.com");
        v.comprobar("El error se borra al corregir el campo", texto(f, "errCorreo").isEmpty());

        pulsar(f, "btnRecuperar");
        Thread.sleep(200);
        foto(f, "olvido_listo.png");
        v.comprobar("Datos válidos: se llama al DAO una vez con usuario y correo", llamadasDAO == 1
                && "jazofeifa".equals(ultimoUsuario) && "j@correo.com".equals(ultimoCorreo));
        v.comprobar("Datos válidos: aparece la pantalla de confirmación", confirmacionVisible(f));

        f.dispatchEvent(new WindowEvent(f, WindowEvent.WINDOW_CLOSING));
        Thread.sleep(200);
        v.comprobar("Cerrar con la X cierra solo esta ventana (la JVM sigue viva)", !f.isVisible());

        FrmOlvido g = new FrmOlvido();
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
        v.comprobar("FrmLogin se muestra y no se puede redimensionar", l.isVisible() && !l.isResizable());
        pulsar(l, "btnIngresar");
        v.comprobar("Login vacío -> " + texto(l, "errUsuario"), texto(l, "errUsuario").contains("usuario"));
        poner(l, "txtUsuario", "jazofeifa");
        pulsar(l, "btnIngresar");
        v.comprobar("Login sin contraseña -> " + texto(l, "errClave"), texto(l, "errClave").contains("contraseña"));

        Method abrir = FrmLogin.class.getDeclaredMethod("abrirOlvido");
        abrir.setAccessible(true);
        abrir.invoke(l); abrir.invoke(l); abrir.invoke(l);
        int abiertas = 0;
        for (Frame fr : Frame.getFrames()) if (fr instanceof FrmOlvido && fr.isVisible()) abiertas++;
        v.comprobar("Pulsar el enlace varias veces abre una sola ventana FrmOlvido", abiertas == 1);
        for (Frame fr : Frame.getFrames()) if (fr instanceof FrmOlvido) fr.dispose();
        v.comprobar("Al cerrar FrmOlvido el login principal sigue abierto", l.isVisible());
    }

    private static Object get(Object o, String nombre) throws Exception {
        Field f = o.getClass().getDeclaredField(nombre);
        f.setAccessible(true);
        return f.get(o);
    }

    private static void poner(Object o, String nombre, String valor) throws Exception {
        ((JTextComponent) get(o, nombre)).setText(valor);
    }

    private static String texto(Object o, String etiqueta) throws Exception {
        return ((JLabel) get(o, etiqueta)).getText().replaceAll("<[^>]*>", "").trim();
    }

    private static void pulsar(Object o, String boton) throws Exception {
        ((JButton) get(o, boton)).doClick();
        Thread.sleep(120);
    }

    private static boolean confirmacionVisible(FrmOlvido f) throws Exception {
        for (Component c : ((JPanel) get(f, "lado")).getComponents()) {
            if (c.isVisible() && contiene(c, componentes.Sello.class)) return true;
        }
        return false;
    }

    private static boolean contiene(Component c, Class<?> clase) {
        if (clase.isInstance(c)) return true;
        if (c instanceof Container) {
            for (Component h : ((Container) c).getComponents()) if (contiene(h, clase)) return true;
        }
        return false;
    }

    private static void foto(Window w, String nombre) throws Exception {
        Thread.sleep(200);
        BufferedImage img = new BufferedImage(w.getWidth(), w.getHeight(), BufferedImage.TYPE_INT_RGB);
        w.paint(img.getGraphics());
        ImageIO.write(img, "png", new File(carpeta, nombre));
    }
}
