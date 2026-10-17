package vista.ui;

import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

/** Columna oscura de la izquierda: nombre de la app, una idea corta y un dibujo de arcos. */
public class PanelMarca extends JPanel {

    private static final int ANCHO = 330;
    private static final int MARGEN = 40;
    private static final int INTERLINEADO_TITULAR = 38;
    private static final int INTERLINEADO_DETALLE = 21;

    private final String titular;
    private final String detalle;

    public PanelMarca(String titular, String detalle) {
        this.titular = titular;
        this.detalle = detalle;
        setOpaque(true);
        setBackground(Tema.BOSQUE);
        setPreferredSize(new Dimension(ANCHO, 0));
    }

    @Override
    protected void paintComponent(Graphics g0) {
        super.paintComponent(g0);
        Graphics2D g = Tema.suavizar((Graphics2D) g0.create());
        int w = getWidth(), h = getHeight();
        int ancho = w - MARGEN * 2;

        dibujarArcos(g, w, h);
        dibujarMarca(g);

        Font fuenteTitular = Tema.titulo(30f);
        Font fuenteDetalle = Tema.texto(Font.PLAIN, 13.5f);
        List<String> lineasTitular = partir(titular, g.getFontMetrics(fuenteTitular), ancho);
        List<String> lineasDetalle = partir(detalle, g.getFontMetrics(fuenteDetalle), ancho);

        // se dibuja de abajo hacia arriba: primero el detalle y encima el titular
        int y = h - MARGEN - (lineasDetalle.size() - 1) * INTERLINEADO_DETALLE;
        g.setFont(fuenteDetalle);
        g.setColor(Tema.conAlfa(Color.WHITE, 190));
        for (String linea : lineasDetalle) {
            g.drawString(linea, MARGEN, y);
            y += INTERLINEADO_DETALLE;
        }

        y = h - MARGEN - lineasDetalle.size() * INTERLINEADO_DETALLE - 22
                - (lineasTitular.size() - 1) * INTERLINEADO_TITULAR;
        g.setFont(fuenteTitular);
        g.setColor(Color.WHITE);
        for (String linea : lineasTitular) {
            g.drawString(linea, MARGEN, y);
            y += INTERLINEADO_TITULAR;
        }
        g.dispose();
    }

    private void dibujarArcos(Graphics2D g, int w, int h) {
        int cx = w + 20, cy = -30;
        g.setStroke(new BasicStroke(1.3f));
        for (int i = 1; i <= 6; i++) {
            int r = i * 82;
            g.setColor(Tema.conAlfa(Color.WHITE, i % 2 == 0 ? 20 : 11));
            g.drawOval(cx - r, cy - r, r * 2, r * 2);
        }
        g.setColor(Tema.ACENTO);
        g.fillArc(cx - 118, cy - 118, 236, 236, 180, 90);
    }

    private void dibujarMarca(Graphics2D g) {
        g.setColor(Tema.ACENTO);
        g.fillRoundRect(MARGEN, MARGEN + 2, 14, 14, 4, 4);
        g.setColor(Color.WHITE);
        g.setFont(Tema.texto(Font.BOLD, 15f));
        g.drawString("Mi cuenta", MARGEN + 24, MARGEN + 15);
    }

    private static List<String> partir(String texto, FontMetrics fm, int maximo) {
        List<String> lineas = new ArrayList<>();
        StringBuilder actual = new StringBuilder();
        for (String palabra : texto.split(" ")) {
            String prueba = actual.length() == 0 ? palabra : actual + " " + palabra;
            if (fm.stringWidth(prueba) > maximo && actual.length() > 0) {
                lineas.add(actual.toString());
                actual = new StringBuilder(palabra);
            } else {
                actual = new StringBuilder(prueba);
            }
        }
        if (actual.length() > 0) {
            lineas.add(actual.toString());
        }
        return lineas;
    }
}
