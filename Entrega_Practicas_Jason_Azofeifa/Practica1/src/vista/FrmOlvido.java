package vista;

import dao.UsuarioDAO;
import dao.UsuarioDAOImpl;
import util.Validador;
import vista.ui.Boton;
import vista.ui.CampoTexto;
import vista.ui.Enlace;
import vista.ui.PanelMarca;
import vista.ui.Piezas;
import vista.ui.Sello;
import vista.ui.Tema;
import vista.ui.Validable;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.event.KeyEvent;

public class FrmOlvido extends JFrame {

    private static final String PASO_FORMULARIO = "formulario";
    private static final String PASO_LISTO = "listo";

    private final UsuarioDAO usuarioDAO;

    private final CampoTexto txtUsuario = new CampoTexto();
    private final CampoTexto txtCorreo = new CampoTexto();
    private final JLabel errUsuario = Piezas.mensaje();
    private final JLabel errCorreo = Piezas.mensaje();
    private final Boton btnRecuperar = new Boton("Recuperar contraseña", true);

    private final CardLayout pasos = new CardLayout();
    private final JPanel lado = new JPanel(pasos);

    public FrmOlvido() {
        this(new UsuarioDAOImpl());
    }

    public FrmOlvido(UsuarioDAO usuarioDAO) {
        this.usuarioDAO = usuarioDAO;
        armarVentana();
        conectarEventos();
    }

    private void armarVentana() {
        setTitle("Olvidó mi contraseña");
        setIconImage(Tema.icono());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);

        lado.setBackground(Tema.PAPEL);
        lado.add(crearFormulario(), PASO_FORMULARIO);
        lado.add(crearConfirmacion(), PASO_LISTO);

        JPanel raiz = new JPanel(new BorderLayout());
        raiz.add(new PanelMarca("Recupere el acceso a su cuenta.",
                "Indique su usuario y el correo con el que se registró para iniciar la recuperación."),
                BorderLayout.WEST);
        raiz.add(lado, BorderLayout.CENTER);
        raiz.setPreferredSize(new Dimension(870, 620));
        setContentPane(raiz);

        pack();
        setLocationRelativeTo(null);
    }

    private JPanel crearFormulario() {
        JPanel p = columna();
        p.add(Piezas.hueco(62));
        p.add(Piezas.titulo("Olvidé mi contraseña"));
        p.add(Piezas.hueco(8));
        p.add(Piezas.parrafo("Complete los datos de su cuenta para continuar."));
        p.add(Piezas.hueco(30));

        p.add(Piezas.etiqueta("Usuario"));
        p.add(Piezas.hueco(6));
        p.add(Piezas.alinear(txtUsuario));
        p.add(errUsuario);
        p.add(Piezas.hueco(10));

        p.add(Piezas.etiqueta("Correo electrónico"));
        p.add(Piezas.hueco(6));
        p.add(Piezas.alinear(txtCorreo));
        p.add(errCorreo);
        p.add(Piezas.hueco(22));

        p.add(Piezas.alinear(btnRecuperar));
        p.add(Piezas.hueco(14));
        p.add(new Enlace("Cancelar", this::dispose));
        return p;
    }

    private JPanel crearConfirmacion() {
        JPanel p = columna();
        p.add(Piezas.hueco(110));
        p.add(new Sello());
        p.add(Piezas.hueco(26));
        p.add(Piezas.titulo("Solicitud procesada"));
        p.add(Piezas.hueco(10));
        p.add(Piezas.parrafo("Su solicitud de recuperación fue procesada."));
        p.add(Piezas.hueco(34));
        Boton volver = new Boton("Volver al inicio", true);
        volver.addActionListener(e -> dispose());
        p.add(Piezas.alinear(volver));
        return p;
    }

    private static JPanel columna() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(44, 52, 36, 52));
        return p;
    }

    private void conectarEventos() {
        btnRecuperar.addActionListener(e -> recuperar());
        getRootPane().setDefaultButton(btnRecuperar);
        getRootPane().registerKeyboardAction(e -> dispose(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);

        Piezas.alEscribir(txtUsuario, () -> limpiar(errUsuario, txtUsuario));
        Piezas.alEscribir(txtCorreo, () -> limpiar(errCorreo, txtCorreo));
    }

    private void recuperar() {
        String usuario = txtUsuario.getText().trim();
        String correo = txtCorreo.getText().trim();

        if (!hayErrores(usuario, correo) && usuarioDAO.recuperarContrasena(usuario, correo)) {
            pasos.show(lado, PASO_LISTO);
        }
    }

    /** Revisa los dos campos juntos y deja el foco en el primero que falle. */
    private boolean hayErrores(String usuario, String correo) {
        limpiar(errUsuario, txtUsuario);
        limpiar(errCorreo, txtCorreo);
        CampoTexto primero = null;

        if (Validador.estaVacio(usuario)) {
            marcar(errUsuario, txtUsuario, "Escriba su usuario.");
            primero = txtUsuario;
        }
        if (Validador.estaVacio(correo)) {
            marcar(errCorreo, txtCorreo, "Escriba su correo electrónico.");
            if (primero == null) primero = txtCorreo;
        } else if (!Validador.correoValido(correo)) {
            marcar(errCorreo, txtCorreo, "El correo no tiene un formato válido.");
            if (primero == null) primero = txtCorreo;
        }

        if (primero != null) {
            primero.requestFocusInWindow();
        }
        return primero != null;
    }

    private static void marcar(JLabel mensaje, Validable campo, String texto) {
        Piezas.decir(mensaje, texto);
        campo.marcarError(true);
    }

    private static void limpiar(JLabel mensaje, Validable campo) {
        Piezas.decir(mensaje, null);
        campo.marcarError(false);
    }
}
