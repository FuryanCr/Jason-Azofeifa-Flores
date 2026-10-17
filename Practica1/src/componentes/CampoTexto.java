package componentes;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JTextField;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class CampoTexto extends JTextField implements Validable {

    private static final int ALTO = 46;
    private static final int RADIO = 12;

    private boolean conError;

    public CampoTexto() {
        configurar(this, 14);
    }

    @Override
    public void marcarError(boolean error) {
        conError = error;
        repaint();
    }

    @Override
    public Dimension getMaximumSize() {
        return new Dimension(Integer.MAX_VALUE, ALTO);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(super.getPreferredSize().width, ALTO);
    }

    @Override
    protected void paintComponent(Graphics g) {
        pintarFondo(g, this, conError);
        super.paintComponent(g);
    }

    static void configurar(javax.swing.text.JTextComponent campo, int margenDerecho) {
        campo.setOpaque(false);
        campo.setFont(Tema.texto(Font.PLAIN, 14.5f));
        campo.setForeground(Tema.TINTA);
        campo.setCaretColor(Tema.BOSQUE);
        campo.setSelectionColor(Tema.conAlfa(Tema.ACENTO, 90));
        campo.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, margenDerecho + 2));
        campo.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) { campo.repaint(); }
            @Override public void focusLost(FocusEvent e) { campo.repaint(); }
        });
    }

    static void pintarFondo(Graphics g0, JComponent c, boolean error) {
        Graphics2D g = Tema.suavizar((Graphics2D) g0.create());
        int w = c.getWidth() - 1, h = c.getHeight() - 1;
        g.setColor(Tema.BLANCO);
        g.fillRoundRect(0, 0, w, h, RADIO, RADIO);

        Color borde = Tema.LINEA;
        float grosor = 1f;
        if (error) {
            borde = Tema.ERROR;
            grosor = 1.6f;
        } else if (c.hasFocus()) {
            borde = Tema.ACENTO;
            grosor = 2f;
        }
        g.setColor(borde);
        g.setStroke(new BasicStroke(grosor));
        float m = grosor / 2f;
        g.drawRoundRect(Math.round(m), Math.round(m), w - Math.round(grosor), h - Math.round(grosor), RADIO, RADIO);
        g.dispose();
    }
}
