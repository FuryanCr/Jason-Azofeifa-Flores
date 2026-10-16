import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Ventana principal (login) desde la que se abre FrmOlvido.
 */
public class FrmLogin extends JFrame {

    private final JTextField txtUsuario = new JTextField(20);
    private final JPasswordField txtClave = new JPasswordField(20);
    private final JButton btnIngresar = new JButton("Ingresar");
    private final JLabel lblOlvido = new JLabel("<html><u>¿Olvidó su contraseña?</u></html>");

    public FrmLogin() {
        setTitle("Inicio de sesión");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        JLabel lblTitulo = new JLabel("Inicio de sesión", SwingConstants.CENTER);
        lblTitulo.setFont(lblTitulo.getFont().deriveFont(Font.BOLD, 16f));

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0; gbc.gridy = 0; form.add(new JLabel("Usuario:"), gbc);
        gbc.gridx = 1; form.add(txtUsuario, gbc);
        gbc.gridx = 0; gbc.gridy = 1; form.add(new JLabel("Contraseña:"), gbc);
        gbc.gridx = 1; form.add(txtClave, gbc);

        lblOlvido.setForeground(Color.BLUE);
        lblOlvido.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lblOlvido.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                new FrmOlvido().setVisible(true);
            }
        });

        JPanel sur = new JPanel(new GridLayout(2, 1, 0, 6));
        JPanel fila1 = new JPanel(new FlowLayout(FlowLayout.CENTER));
        fila1.add(btnIngresar);
        JPanel fila2 = new JPanel(new FlowLayout(FlowLayout.CENTER));
        fila2.add(lblOlvido);
        sur.add(fila1);
        sur.add(fila2);

        JPanel contenido = new JPanel(new BorderLayout(10, 10));
        contenido.setBorder(BorderFactory.createEmptyBorder(15, 20, 10, 20));
        contenido.add(lblTitulo, BorderLayout.NORTH);
        contenido.add(form, BorderLayout.CENTER);
        contenido.add(sur, BorderLayout.SOUTH);
        setContentPane(contenido);

        btnIngresar.addActionListener(e -> {
            if (txtUsuario.getText().trim().isEmpty()
                    || new String(txtClave.getPassword()).trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Complete usuario y contraseña.",
                        "Validación", JOptionPane.WARNING_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Demostración: el login real no es parte de esta práctica.");
            }
        });

        pack();
        setLocationRelativeTo(null);
    }
}
