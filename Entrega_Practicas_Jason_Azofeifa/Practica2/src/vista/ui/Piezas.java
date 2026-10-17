package vista.ui;

import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;
import java.awt.Component;
import java.awt.Font;

/** Pequeñas fábricas para que los formularios no repitan el mismo código. */
public final class Piezas {

    private Piezas() {
    }

    public static JLabel titulo(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(Tema.titulo(28f));
        l.setForeground(Tema.TINTA);
        return alinear(l);
    }

    public static JLabel parrafo(String texto) {
        JLabel l = new JLabel("<html><body style='width:380px'>" + texto + "</body></html>");
        l.setFont(Tema.texto(Font.PLAIN, 13.5f));
        l.setForeground(Tema.SUAVE);
        return alinear(l);
    }

    public static JLabel etiqueta(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(Tema.texto(Font.BOLD, 12f));
        l.setForeground(Tema.TINTA);
        return alinear(l);
    }

    /** Línea de error con la altura ya reservada, para que el formulario no salte. */
    public static JLabel mensaje() {
        JLabel l = new JLabel(" ");
        l.setFont(Tema.texto(Font.PLAIN, 12f));
        l.setForeground(Tema.ERROR);
        return alinear(l);
    }

    public static void decir(JLabel mensaje, String texto) {
        mensaje.setText(texto == null || texto.isEmpty() ? " " : texto);
    }

    public static Component hueco(int alto) {
        return Box.createVerticalStrut(alto);
    }

    public static <T extends JComponent> T alinear(T c) {
        c.setAlignmentX(Component.LEFT_ALIGNMENT);
        return c;
    }

    public static void alEscribir(JTextComponent campo, Runnable accion) {
        campo.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { accion.run(); }
            @Override public void removeUpdate(DocumentEvent e) { accion.run(); }
            @Override public void changedUpdate(DocumentEvent e) { accion.run(); }
        });
    }
}
