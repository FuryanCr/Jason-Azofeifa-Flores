package vista;

import dao.UsuarioDAO;
import dao.UsuarioDAOImpl;
import util.Validador;
import vista.ui.Boton;
import vista.ui.CampoClave;
import vista.ui.CampoTexto;
import vista.ui.Enlace;
import vista.ui.PanelMarca;
import vista.ui.Piezas;
import vista.ui.Tema;
import vista.ui.Validable;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Dimension;

public class FrmLogin extends JFrame {

    private final UsuarioDAO usuarioDAO = new UsuarioDAOImpl();

    private final CampoTexto txtCorreo = new CampoTexto();
    private final CampoClave txtClave = new CampoClave();
    private final JLabel errCorreo = Piezas.mensaje();
    private final JLabel errClave = Piezas.mensaje();
    private final Boton btnIngresar = new Boton("Ingresar", true);

    private FrmOlvido ventanaOlvido;

    public FrmLogin() {
        setTitle("Iniciar sesión");
        setIconImage(Tema.icono());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        JPanel raiz = new JPanel(new BorderLayout());
        raiz.add(new PanelMarca("Qué bueno verle de nuevo.",
                "Ingrese con su correo y contraseña para continuar."), BorderLayout.WEST);
        raiz.add(crearFormulario(), BorderLayout.CENTER);
        raiz.setPreferredSize(new Dimension(870, 620));
        setContentPane(raiz);

        btnIngresar.addActionListener(e -> ingresar());
        getRootPane().setDefaultButton(btnIngresar);
        Piezas.alEscribir(txtCorreo, () -> limpiar(errCorreo, txtCorreo));
        Piezas.alEscribir(txtClave, () -> limpiar(errClave, txtClave));

        pack();
        setLocationRelativeTo(null);
    }

    private JPanel crearFormulario() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(Tema.PAPEL);
        p.setBorder(BorderFactory.createEmptyBorder(96, 52, 36, 52));

        p.add(Piezas.titulo("Iniciar sesión"));
        p.add(Piezas.hueco(8));
        p.add(Piezas.parrafo("Use la cuenta con la que se registró."));
        p.add(Piezas.hueco(30));

        p.add(Piezas.etiqueta("Correo electrónico"));
        p.add(Piezas.hueco(6));
        p.add(Piezas.alinear(txtCorreo));
        p.add(errCorreo);
        p.add(Piezas.hueco(10));

        p.add(Piezas.etiqueta("Contraseña"));
        p.add(Piezas.hueco(6));
        p.add(Piezas.alinear(txtClave));
        p.add(errClave);
        p.add(Piezas.hueco(18));

        p.add(Piezas.alinear(btnIngresar));
        p.add(Piezas.hueco(18));
        Enlace olvido = new Enlace("¿Olvidó su contraseña?", this::abrirOlvido);
        p.add(olvido);
        return p;
    }

    private void abrirOlvido() {
        // si ya hay una ventana abierta se trae al frente en vez de abrir otra
        if (ventanaOlvido == null || !ventanaOlvido.isDisplayable()) {
            ventanaOlvido = new FrmOlvido(usuarioDAO);
        }
        ventanaOlvido.setVisible(true);
        ventanaOlvido.toFront();
    }

    private void ingresar() {
        String correo = txtCorreo.getText().trim();
        String clave = new String(txtClave.getPassword());

        limpiar(errCorreo, txtCorreo);
        limpiar(errClave, txtClave);

        if (Validador.estaVacio(correo)) {
            marcar(errCorreo, txtCorreo, "Escriba su correo electrónico.");
            txtCorreo.requestFocusInWindow();
        } else if (clave.isEmpty()) {
            marcar(errClave, txtClave, "Escriba su contraseña.");
            txtClave.requestFocusInWindow();
        } else if (usuarioDAO.verificarContrasena(correo, clave)) {
            errClave.setForeground(Tema.EXITO);
            Piezas.decir(errClave, "Acceso correcto. Sesión iniciada.");
        } else {
            marcar(errClave, txtClave, "Correo o contraseña incorrectos.");
        }
    }

    private static void marcar(JLabel mensaje, Validable campo, String contenido) {
        mensaje.setForeground(Tema.ERROR);
        Piezas.decir(mensaje, contenido);
        campo.marcarError(true);
    }

    private static void limpiar(JLabel mensaje, Validable campo) {
        Piezas.decir(mensaje, null);
        mensaje.setForeground(Tema.ERROR);
        campo.marcarError(false);
    }
}
