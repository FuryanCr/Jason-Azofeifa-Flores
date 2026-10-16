import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

/** Colores y componentes comunes para que las ventanas tengan el mismo aspecto. */
public final class Estilo {

    public static final Color FONDO = new Color(0xF4F6F9);
    public static final Color PRIMARIO = new Color(0x1F4E79);
    public static final Color PRIMARIO_OSCURO = new Color(0x163A5C);
    public static final Color TEXTO = new Color(0x2B2F36);
    public static final Color TEXTO_SUAVE = new Color(0x6B7280);
    public static final Color BORDE = new Color(0xCBD2DA);
    public static final Color ERROR = new Color(0xB3261E);

    public static final Font NORMAL = new Font("SansSerif", Font.PLAIN, 13);
    public static final Font NEGRITA = new Font("SansSerif", Font.BOLD, 13);

    private Estilo() { }

    public static JLabel etiqueta(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(NEGRITA);
        l.setForeground(TEXTO);
        return l;
    }

    public static void campo(JTextField c) {
        c.setFont(NORMAL);
        c.setForeground(TEXTO);
        c.setPreferredSize(new Dimension(280, 34));
        c.setBorder(bordeCampo(BORDE));
        c.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override public void focusGained(java.awt.event.FocusEvent e) { c.setBorder(bordeCampo(PRIMARIO)); }
            @Override public void focusLost(java.awt.event.FocusEvent e) { c.setBorder(bordeCampo(BORDE)); }
        });
    }

    public static Border bordeCampo(Color color) {
        return new CompoundBorder(new LineBorder(color, 1), new EmptyBorder(0, 10, 0, 10));
    }

    public static JButton boton(String texto, boolean principal) {
        JButton b = new JButton(texto);
        b.setFont(NEGRITA);
        b.setFocusPainted(false);
        b.setContentAreaFilled(false);
        b.setOpaque(true);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(150, 36));
        if (principal) {
            b.setBackground(PRIMARIO);
            b.setForeground(Color.WHITE);
            b.setBorder(new LineBorder(PRIMARIO));
            b.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override public void mouseEntered(java.awt.event.MouseEvent e) { b.setBackground(PRIMARIO_OSCURO); }
                @Override public void mouseExited(java.awt.event.MouseEvent e) { b.setBackground(PRIMARIO); }
            });
        } else {
            b.setBackground(Color.WHITE);
            b.setForeground(TEXTO);
            b.setBorder(new LineBorder(BORDE));
        }
        return b;
    }

    /** Franja superior de color con título y subtítulo. */
    public static JPanel encabezado(String titulo, String subtitulo) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(PRIMARIO);
        p.setBorder(new EmptyBorder(20, 30, 18, 30));
        JLabel t = new JLabel(titulo);
        t.setFont(new Font("SansSerif", Font.BOLD, 20));
        t.setForeground(Color.WHITE);
        JLabel s = new JLabel(subtitulo);
        s.setFont(NORMAL);
        s.setForeground(new Color(0xCFDDEB));
        s.setBorder(new EmptyBorder(4, 0, 0, 0));
        p.add(t);
        p.add(s);
        return p;
    }

    public static void ui() {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignorada) { }
        UIManager.put("OptionPane.messageFont", NORMAL);
        UIManager.put("OptionPane.buttonFont", NORMAL);
    }
}
