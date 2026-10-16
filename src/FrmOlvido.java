import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.regex.Pattern;

/**
 * Ventana "Olvidó mi contraseña".
 */
public class FrmOlvido extends JFrame {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private final JTextField txtUsuario = new JTextField();
    private final JTextField txtCorreo = new JTextField();
    private final JLabel lblMensaje = new JLabel(" ");
    private final JButton btnRecuperar = Estilo.boton("Recuperar", true);
    private final JButton btnCancelar = Estilo.boton("Cancelar", false);

    private final UsuarioDAO usuarioDAO = new UsuarioDAOImpl();

    public FrmOlvido() {
        initComponents();
    }

    private void initComponents() {
        setTitle("Olvidó mi contraseña");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE); // no termina el sistema principal
        setResizable(false);                        // Resizable = false

        Estilo.campo(txtUsuario);
        Estilo.campo(txtCorreo);
        lblMensaje.setFont(Estilo.NORMAL);
        lblMensaje.setForeground(Estilo.ERROR);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Estilo.FONDO);
        form.setBorder(new EmptyBorder(22, 30, 20, 30));
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.anchor = GridBagConstraints.WEST;
        g.weightx = 1;

        g.gridy = 0; g.insets = new Insets(0, 0, 5, 0);
        form.add(Estilo.etiqueta("Usuario"), g);
        g.gridy = 1; g.insets = new Insets(0, 0, 14, 0);
        form.add(txtUsuario, g);
        g.gridy = 2; g.insets = new Insets(0, 0, 5, 0);
        form.add(Estilo.etiqueta("Correo electrónico"), g);
        g.gridy = 3; g.insets = new Insets(0, 0, 8, 0);
        form.add(txtCorreo, g);
        g.gridy = 4; g.insets = new Insets(0, 0, 14, 0);
        form.add(lblMensaje, g);

        JPanel botones = new JPanel(new GridLayout(1, 2, 12, 0));
        botones.setOpaque(false);
        botones.add(btnCancelar);
        botones.add(btnRecuperar);
        g.gridy = 5; g.insets = new Insets(0, 0, 0, 0);
        form.add(botones, g);

        JPanel contenido = new JPanel(new BorderLayout());
        contenido.add(Estilo.encabezado("Recuperar contraseña",
                "Ingrese sus datos para restablecer el acceso"), BorderLayout.NORTH);
        contenido.add(form, BorderLayout.CENTER);
        setContentPane(contenido);

        btnRecuperar.addActionListener(e -> recuperar());
        btnCancelar.addActionListener(e -> dispose());
        getRootPane().setDefaultButton(btnRecuperar);

        pack();
        setLocationRelativeTo(null); // centrada en la pantalla
    }

    private void recuperar() {
        String usuario = txtUsuario.getText().trim();
        String correo = txtCorreo.getText().trim();

        if (!validarCampos(usuario, correo)) {
            return; // impide continuar mientras existan errores
        }

        boolean ok = usuarioDAO.recuperarContrasena(usuario, correo);
        if (ok) {
            JOptionPane.showMessageDialog(this,
                    "Su solicitud de recuperación fue procesada.",
                    "Información", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            mostrarError("No se pudo procesar la solicitud.", null);
        }
    }

    /** Valida solo los campos del formulario (no consulta la base de datos). */
    private boolean validarCampos(String usuario, String correo) {
        txtUsuario.setBorder(Estilo.bordeCampo(Estilo.BORDE));
        txtCorreo.setBorder(Estilo.bordeCampo(Estilo.BORDE));

        if (usuario.isEmpty()) {
            mostrarError("El usuario es obligatorio y no puede quedar en blanco.", txtUsuario);
            return false;
        }
        if (correo.isEmpty()) {
            mostrarError("El correo electrónico es obligatorio y no puede quedar en blanco.", txtCorreo);
            return false;
        }
        if (!EMAIL_PATTERN.matcher(correo).matches()) {
            mostrarError("El correo no tiene un formato válido (ejemplo: nombre@dominio.com).", txtCorreo);
            return false;
        }
        lblMensaje.setText(" ");
        return true;
    }

    private void mostrarError(String mensaje, JTextField campo) {
        lblMensaje.setText("<html><body style='width:250px'>" + mensaje + "</body></html>");
        if (campo != null) {
            campo.setBorder(Estilo.bordeCampo(Estilo.ERROR));
            campo.requestFocusInWindow();
        }
        pack();
    }
}
