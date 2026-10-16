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

    private static final int MIN_CLAVE = 8;

    private final JTextField txtCorreo = new JTextField();
    private final JPasswordField txtNueva = new JPasswordField();
    private final JPasswordField txtConfirmar = new JPasswordField();
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

        Estilo.campo(txtCorreo);
        Estilo.campo(txtNueva);
        Estilo.campo(txtConfirmar);
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
        form.add(Estilo.etiqueta("Correo electrónico"), g);
        g.gridy = 1; g.insets = new Insets(0, 0, 14, 0);
        form.add(txtCorreo, g);
        g.gridy = 2; g.insets = new Insets(0, 0, 5, 0);
        form.add(Estilo.etiqueta("Nueva contraseña"), g);
        g.gridy = 3; g.insets = new Insets(0, 0, 14, 0);
        form.add(txtNueva, g);
        g.gridy = 4; g.insets = new Insets(0, 0, 5, 0);
        form.add(Estilo.etiqueta("Confirmar contraseña"), g);
        g.gridy = 5; g.insets = new Insets(0, 0, 8, 0);
        form.add(txtConfirmar, g);
        g.gridy = 6; g.insets = new Insets(0, 0, 14, 0);
        form.add(lblMensaje, g);

        JPanel botones = new JPanel(new GridLayout(1, 2, 12, 0));
        botones.setOpaque(false);
        botones.add(btnCancelar);
        botones.add(btnRecuperar);
        g.gridy = 7; g.insets = new Insets(0, 0, 0, 0);
        form.add(botones, g);

        JPanel contenido = new JPanel(new BorderLayout());
        contenido.add(Estilo.encabezado("Recuperar contraseña",
                "Defina una nueva contraseña para su cuenta"), BorderLayout.NORTH);
        contenido.add(form, BorderLayout.CENTER);
        setContentPane(contenido);

        btnRecuperar.addActionListener(e -> recuperar());
        btnCancelar.addActionListener(e -> dispose());
        getRootPane().setDefaultButton(btnRecuperar);

        pack();
        setLocationRelativeTo(null); // centrada en la pantalla
    }

    private void recuperar() {
        String correo = txtCorreo.getText().trim();
        String nueva = new String(txtNueva.getPassword());
        String confirmar = new String(txtConfirmar.getPassword());

        if (!validarCampos(correo, nueva, confirmar)) {
            return; // impide continuar mientras existan errores
        }

        // El DAO aplica SHA-256 + salt antes de guardar
        boolean ok = usuarioDAO.recuperarContrasena(correo, nueva);
        if (ok) {
            JOptionPane.showMessageDialog(this,
                    "Su contraseña fue actualizada correctamente.",
                    "Información", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            mostrarError("No existe una cuenta registrada con ese correo.", txtCorreo);
        }
    }

    /** Valida los campos del formulario. */
    private boolean validarCampos(String correo, String nueva, String confirmar) {
        for (JTextField c : new JTextField[]{txtCorreo, txtNueva, txtConfirmar}) {
            c.setBorder(Estilo.bordeCampo(Estilo.BORDE));
        }
        if (correo.isEmpty()) {
            mostrarError("El correo electrónico es obligatorio.", txtCorreo);
            return false;
        }
        if (!EMAIL_PATTERN.matcher(correo).matches()) {
            mostrarError("El correo no tiene un formato válido (ejemplo: nombre@dominio.com).", txtCorreo);
            return false;
        }
        if (nueva.trim().isEmpty()) {
            mostrarError("La nueva contraseña es obligatoria y no puede quedar en blanco.", txtNueva);
            return false;
        }
        if (nueva.length() < MIN_CLAVE) {
            mostrarError("La contraseña debe tener al menos " + MIN_CLAVE + " caracteres.", txtNueva);
            return false;
        }
        if (confirmar.isEmpty()) {
            mostrarError("Debe confirmar la contraseña.", txtConfirmar);
            return false;
        }
        if (!nueva.equals(confirmar)) {
            mostrarError("Las contraseñas no coinciden.", txtConfirmar);
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
