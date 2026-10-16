import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Ventana principal (login) desde la que se abre FrmOlvido.
 */
public class FrmLogin extends JFrame {

    private final JTextField txtUsuario = new JTextField();
    private final JPasswordField txtClave = new JPasswordField();
    private final JButton btnIngresar = Estilo.boton("Ingresar", true);
    private final JLabel lblOlvido = new JLabel("¿Olvidó su contraseña?");

    public FrmLogin() {
        setTitle("Inicio de sesión");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        Estilo.campo(txtUsuario);
        Estilo.campo(txtClave);
        btnIngresar.setPreferredSize(new Dimension(280, 38));

        lblOlvido.setFont(Estilo.NORMAL);
        lblOlvido.setForeground(Estilo.PRIMARIO);
        lblOlvido.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lblOlvido.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { new FrmOlvido().setVisible(true); }
            @Override public void mouseEntered(MouseEvent e) { lblOlvido.setText("<html><u>¿Olvidó su contraseña?</u></html>"); }
            @Override public void mouseExited(MouseEvent e) { lblOlvido.setText("¿Olvidó su contraseña?"); }
        });

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
        form.add(Estilo.etiqueta("Contraseña"), g);
        g.gridy = 3; g.insets = new Insets(0, 0, 20, 0);
        form.add(txtClave, g);
        g.gridy = 4; g.insets = new Insets(0, 0, 14, 0);
        form.add(btnIngresar, g);
        g.gridy = 5; g.insets = new Insets(0, 0, 0, 0);
        g.anchor = GridBagConstraints.CENTER; g.fill = GridBagConstraints.NONE;
        form.add(lblOlvido, g);

        JPanel contenido = new JPanel(new BorderLayout());
        contenido.add(Estilo.encabezado("Bienvenido", "Inicie sesión para continuar"), BorderLayout.NORTH);
        contenido.add(form, BorderLayout.CENTER);
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
        getRootPane().setDefaultButton(btnIngresar);

        pack();
        setLocationRelativeTo(null);
    }
}
