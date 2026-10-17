package vista.ui;

import javax.swing.JButton;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;

public class Boton extends JButton {

    private static final int ALTO = 48;
    private static final int RADIO = 12;

    private final boolean principal;

    public Boton(String texto, boolean principal) {
        super(texto);
        this.principal = principal;
        setFont(Tema.texto(Font.BOLD, 14f));
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
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
    protected void paintComponent(Graphics g0) {
        Graphics2D g = Tema.suavizar((Graphics2D) g0.create());
        int w = getWidth(), h = getHeight();

        Color fondo = principal ? Tema.BOSQUE : Tema.BLANCO;
        if (getModel().isPressed()) {
            fondo = principal ? Tema.BOSQUE_OSCURO.darker() : Tema.PAPEL;
        } else if (getModel().isRollover()) {
            fondo = principal ? Tema.BOSQUE_OSCURO : Tema.PAPEL;
        }
        g.setColor(fondo);
        g.fillRoundRect(0, 0, w - 1, h - 1, RADIO, RADIO);

        if (!principal) {
            g.setColor(Tema.LINEA);
            g.setStroke(new BasicStroke(1.2f));
            g.drawRoundRect(0, 0, w - 2, h - 2, RADIO, RADIO);
        }
        if (hasFocus()) {
            g.setColor(Tema.ACENTO);
            g.setStroke(new BasicStroke(2f));
            g.drawRoundRect(2, 2, w - 6, h - 6, RADIO - 3, RADIO - 3);
        }

        g.setFont(getFont());
        g.setColor(principal ? Color.WHITE : Tema.TINTA);
        FontMetrics fm = g.getFontMetrics();
        int x = (w - fm.stringWidth(getText())) / 2;
        int y = (h - fm.getHeight()) / 2 + fm.getAscent();
        g.drawString(getText(), x, y);
        g.dispose();
    }
}
