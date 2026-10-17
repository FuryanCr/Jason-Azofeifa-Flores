package vista.ui;

import javax.swing.JPasswordField;
import java.awt.BasicStroke;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;

/** Campo de contraseña con el ojito para mostrar u ocultar lo escrito. */
public class CampoClave extends JPasswordField implements Validable {

    private static final int ALTO = 46;
    private static final int ZONA_OJO = 46;
    private static final char PUNTO = '•';

    private boolean conError;
    private boolean visible;

    public CampoClave() {
        CampoTexto.configurar(this, ZONA_OJO);
        setEchoChar(PUNTO);

        MouseAdapter raton = new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (sobreOjo(e)) {
                    alternarVisibilidad();
                }
            }
            @Override public void mouseMoved(MouseEvent e) {
                setCursor(Cursor.getPredefinedCursor(sobreOjo(e) ? Cursor.HAND_CURSOR : Cursor.TEXT_CURSOR));
            }
        };
        addMouseListener(raton);
        addMouseMotionListener(raton);
    }

    @Override
    public void marcarError(boolean error) {
        conError = error;
        repaint();
    }

    public void alternarVisibilidad() {
        visible = !visible;
        setEchoChar(visible ? (char) 0 : PUNTO);
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
        CampoTexto.pintarFondo(g, this, conError);
        super.paintComponent(g);
        dibujarOjo(g);
    }

    private boolean sobreOjo(MouseEvent e) {
        return e.getX() >= getWidth() - ZONA_OJO;
    }

    private void dibujarOjo(Graphics g0) {
        Graphics2D g = Tema.suavizar((Graphics2D) g0.create());
        double cx = getWidth() - ZONA_OJO / 2.0 - 2;
        double cy = getHeight() / 2.0;
        g.setColor(visible ? Tema.BOSQUE : Tema.SUAVE);
        g.setStroke(new BasicStroke(1.7f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        Path2D ojo = new Path2D.Double();
        ojo.moveTo(cx - 9, cy);
        ojo.quadTo(cx, cy - 9, cx + 9, cy);
        ojo.quadTo(cx, cy + 9, cx - 9, cy);
        g.draw(ojo);
        g.fill(new Ellipse2D.Double(cx - 2.6, cy - 2.6, 5.2, 5.2));
        if (!visible) {
            g.draw(new Line2D.Double(cx - 8, cy + 8, cx + 8, cy - 8));
        }
        g.dispose();
    }
}
