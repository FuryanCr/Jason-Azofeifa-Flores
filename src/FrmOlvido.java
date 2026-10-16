import javax.swing.*;
import java.awt.*;
import java.util.regex.Pattern;

/**
 * Ventana "Olvidó mi contraseña".
 */
public class FrmOlvido extends JFrame {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private final JTextField txtUsuario = new JTextField(20);
    private final JTextField txtCorreo = new JTextField(20);
    private final JButton btnRecuperar = new JButton("Recuperar contraseña");
    private final JButton btnCancelar = new JButton("Cancelar");

    private final UsuarioDAO usuarioDAO = new UsuarioDAOImpl();

    public FrmOlvido() {
        initComponents();
    }

    private void initComponents() {
        setTitle("Olvidó mi contraseña");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE); // no termina el sistema principal
        setResizable(false);                        // Resizable = false

        JLabel lblTitulo = new JLabel("Recuperación de contraseña", SwingConstants.CENTER);
        lblTitulo.setFont(lblTitulo.getFont().deriveFont(Font.BOLD, 16f));

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        form.add(new JLabel("Usuario:"), gbc);
        gbc.gridx = 1;
        form.add(txtUsuario, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        form.add(new JLabel("Correo electrónico:"), gbc);
        gbc.gridx = 1;
        form.add(txtCorreo, gbc);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        botones.add(btnRecuperar);
        botones.add(btnCancelar);

        JPanel contenido = new JPanel(new BorderLayout(10, 10));
        contenido.setBorder(BorderFactory.createEmptyBorder(15, 20, 10, 20));
        contenido.add(lblTitulo, BorderLayout.NORTH);
        contenido.add(form, BorderLayout.CENTER);
        contenido.add(botones, BorderLayout.SOUTH);
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
            JOptionPane.showMessageDialog(this,
                    "No se pudo procesar la solicitud.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Valida solo los campos del formulario (no consulta la base de datos). */
    private boolean validarCampos(String usuario, String correo) {
        if (usuario.isEmpty()) {
            advertir("El campo Usuario es obligatorio y no puede contener solo espacios.", txtUsuario);
            return false;
        }
        if (correo.isEmpty()) {
            advertir("El campo Correo electrónico es obligatorio y no puede contener solo espacios.", txtCorreo);
            return false;
        }
        if (!EMAIL_PATTERN.matcher(correo).matches()) {
            advertir("El formato del correo electrónico no es válido (ejemplo: nombre@dominio.com).", txtCorreo);
            return false;
        }
        return true;
    }

    private void advertir(String mensaje, JComponent campo) {
        JOptionPane.showMessageDialog(this, mensaje, "Validación", JOptionPane.WARNING_MESSAGE);
        campo.requestFocusInWindow();
    }

    /** Solo para probar la ventana de forma independiente. */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FrmOlvido frm = new FrmOlvido();
            frm.setDefaultCloseOperation(EXIT_ON_CLOSE);
            frm.setVisible(true);
        });
    }
}
