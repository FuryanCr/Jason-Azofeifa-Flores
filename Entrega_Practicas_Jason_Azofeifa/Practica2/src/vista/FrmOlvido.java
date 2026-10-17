package vista;

import dao.UsuarioDAO;
import dao.UsuarioDAOImpl;
import util.Validador;
import vista.ui.Boton;
import vista.ui.CampoClave;
import vista.ui.CampoTexto;
import vista.ui.Enlace;
import vista.ui.MedidorClave;
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

    private final CampoTexto txtCorreo = new CampoTexto();
    private final CampoClave txtNueva = new CampoClave();
    private final CampoClave txtConfirmar = new CampoClave();
    private final MedidorClave medidor = new MedidorClave();
    private final JLabel errCorreo = Piezas.mensaje();
    private final JLabel errNueva = Piezas.mensaje();
    private final JLabel errConfirmar = Piezas.mensaje();
    private final Boton btnGuardar = new Boton("Guardar contraseña", true);

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
        setTitle("Recuperar contraseña");
        setIconImage(Tema.icono());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);

        lado.setBackground(Tema.PAPEL);
        lado.add(crearFormulario(), PASO_FORMULARIO);
        lado.add(crearConfirmacion(), PASO_LISTO);

        JPanel raiz = new JPanel(new BorderLayout());
        raiz.add(new PanelMarca("Recupere el acceso a su cuenta.",
                "La nueva contraseña se guarda cifrada con SHA-256 y un salt aleatorio, nunca en texto plano."),
                BorderLayout.WEST);
        raiz.add(lado, BorderLayout.CENTER);
        raiz.setPreferredSize(new Dimension(870, 620));
        setContentPane(raiz);

        pack();
        setLocationRelativeTo(null);
    }

    private JPanel crearFormulario() {
        JPanel p = columna();
        p.add(Piezas.titulo("Nueva contraseña"));
        p.add(Piezas.hueco(8));
        p.add(Piezas.parrafo("Escriba el correo de su cuenta y elija una contraseña nueva."));
        p.add(Piezas.hueco(24));

        p.add(Piezas.etiqueta("Correo electrónico"));
        p.add(Piezas.hueco(6));
        p.add(Piezas.alinear(txtCorreo));
        p.add(errCorreo);
        p.add(Piezas.hueco(10));

        p.add(Piezas.etiqueta("Nueva contraseña"));
        p.add(Piezas.hueco(6));
        p.add(Piezas.alinear(txtNueva));
        p.add(Piezas.hueco(8));
        p.add(Piezas.alinear(medidor));
        p.add(errNueva);
        p.add(Piezas.hueco(10));

        p.add(Piezas.etiqueta("Confirmar contraseña"));
        p.add(Piezas.hueco(6));
        p.add(Piezas.alinear(txtConfirmar));
        p.add(errConfirmar);
        p.add(Piezas.hueco(14));

        p.add(Piezas.alinear(btnGuardar));
        p.add(Piezas.hueco(14));
        Enlace cancelar = new Enlace("Cancelar", this::dispose);
        p.add(cancelar);
        return p;
    }

    private JPanel crearConfirmacion() {
        JPanel p = columna();
        p.add(Piezas.hueco(110));
        p.add(new Sello());
        p.add(Piezas.hueco(26));
        p.add(Piezas.titulo("Contraseña actualizada"));
        p.add(Piezas.hueco(10));
        p.add(Piezas.parrafo("Ya puede iniciar sesión con su nueva contraseña."));
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
        btnGuardar.addActionListener(e -> guardar());
        getRootPane().setDefaultButton(btnGuardar);
        getRootPane().registerKeyboardAction(e -> dispose(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);

        // el error de un campo desaparece apenas el usuario lo corrige
        Piezas.alEscribir(txtCorreo, () -> limpiar(errCorreo, txtCorreo));
        Piezas.alEscribir(txtNueva, () -> {
            limpiar(errNueva, txtNueva);
            medidor.medir(new String(txtNueva.getPassword()));
        });
        Piezas.alEscribir(txtConfirmar, () -> limpiar(errConfirmar, txtConfirmar));
    }

    private void guardar() {
        String correo = txtCorreo.getText().trim();
        String nueva = new String(txtNueva.getPassword());
        String confirmar = new String(txtConfirmar.getPassword());

        if (!hayErrores(correo, nueva, confirmar)) {
            if (usuarioDAO.recuperarContrasena(correo, nueva)) {
                pasos.show(lado, PASO_LISTO);
            } else {
                marcar(errCorreo, txtCorreo, "No hay ninguna cuenta con ese correo.");
                txtCorreo.requestFocusInWindow();
            }
        }
    }

    /** Revisa todo el formulario de una vez y deja el foco en el primer campo con error. */
    private boolean hayErrores(String correo, String nueva, String confirmar) {
        limpiar(errCorreo, txtCorreo);
        limpiar(errNueva, txtNueva);
        limpiar(errConfirmar, txtConfirmar);
        JComponent primero = null;

        if (Validador.estaVacio(correo)) {
            marcar(errCorreo, txtCorreo, "Escriba su correo electrónico.");
            primero = txtCorreo;
        } else if (!Validador.correoValido(correo)) {
            marcar(errCorreo, txtCorreo, "El correo no tiene un formato válido.");
            primero = txtCorreo;
        }

        if (nueva.isEmpty()) {
            marcar(errNueva, txtNueva, "Escriba la nueva contraseña.");
        } else if (Validador.estaVacio(nueva)) {
            marcar(errNueva, txtNueva, "La contraseña no puede ser solo espacios.");
        } else if (nueva.length() < Validador.MIN_CLAVE) {
            marcar(errNueva, txtNueva, "Use al menos " + Validador.MIN_CLAVE + " caracteres.");
        }
        if (primero == null && !errNueva.getText().isBlank()) {
            primero = txtNueva;
        }

        if (confirmar.isEmpty()) {
            marcar(errConfirmar, txtConfirmar, "Repita la contraseña.");
        } else if (!confirmar.equals(nueva)) {
            marcar(errConfirmar, txtConfirmar, "Las contraseñas no coinciden.");
        }
        if (primero == null && !errConfirmar.getText().isBlank()) {
            primero = txtConfirmar;
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
