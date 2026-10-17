package vista.ui;

import javax.swing.JComponent;
import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;

/** Círculo verde con una marca de verificación, para la pantalla de éxito. */
public class Sello extends JComponent {

    private static final int LADO = 72;

    public Sello() {
        setAlignmentX(LEFT_ALIGNMENT);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(LADO, LADO);
    }

    @Override
    public Dimension getMaximumSize() {
        return getPreferredSize();
    }

    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = Tema.suavizar((Graphics2D) g0.create());
        g.setColor(Tema.conAlfa(Tema.EXITO, 32));
        g.fillOval(0, 0, LADO - 1, LADO - 1);
        g.setColor(Tema.EXITO);
        g.fillOval(10, 10, LADO - 21, LADO - 21);

        Path2D marca = new Path2D.Double();
        marca.moveTo(24, 37);
        marca.lineTo(32, 45);
        marca.lineTo(48, 27);
        g.setColor(java.awt.Color.WHITE);
        g.setStroke(new BasicStroke(4.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.draw(marca);
        g.dispose();
    }
}
