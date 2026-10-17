package componentes;

import javax.swing.JLabel;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/** Texto que se comporta como enlace: se subraya al pasar el mouse. */
public class Enlace extends JLabel {

    private boolean encima;

    public Enlace(String texto, Runnable alHacerClic) {
        super(texto);
        setFont(Tema.texto(Font.BOLD, 13f));
        setForeground(Tema.BOSQUE);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setHorizontalAlignment(CENTER);
        setAlignmentX(LEFT_ALIGNMENT);
        addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { alHacerClic.run(); }
            @Override public void mouseEntered(MouseEvent e) { encima = true; repaint(); }
            @Override public void mouseExited(MouseEvent e) { encima = false; repaint(); }
        });
    }

    @Override
    public Dimension getMaximumSize() {
        return new Dimension(Integer.MAX_VALUE, getPreferredSize().height + 6);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (encima) {
            FontMetrics fm = g.getFontMetrics(getFont());
            int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2 + 2;
            int ancho = fm.stringWidth(getText());
            int x = (getWidth() - ancho) / 2;
            g.setColor(getForeground());
            g.drawLine(x, y, x + ancho, y);
        }
    }
}
