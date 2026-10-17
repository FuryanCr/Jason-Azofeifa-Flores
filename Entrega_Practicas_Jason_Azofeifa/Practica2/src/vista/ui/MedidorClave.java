package vista.ui;

import util.Validador;

import javax.swing.JComponent;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;

/** Cuatro barras y una palabra que resumen qué tan fuerte es la contraseña escrita. */
public class MedidorClave extends JComponent {

    private static final Color[] COLORES = {Tema.LINEA, Tema.ERROR, Tema.ACENTO, new Color(0x8DA34A), Tema.EXITO};
    private static final int SEGMENTOS = 4;

    private int nivel;

    public MedidorClave() {
        setFont(Tema.texto(Font.BOLD, 12f));
    }

    public void medir(String clave) {
        nivel = Validador.fuerza(clave);
        repaint();
    }

    public int getNivel() {
        return nivel;
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(100, 20);
    }

    @Override
    public Dimension getMaximumSize() {
        return new Dimension(Integer.MAX_VALUE, 20);
    }

    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = Tema.suavizar((Graphics2D) g0.create());
        g.setFont(getFont());
        FontMetrics fm = g.getFontMetrics();
        String palabra = Validador.etiquetaFuerza(nivel);
        int anchoPalabra = fm.stringWidth("Aceptable") + 6;

        int separacion = 6;
        int zona = getWidth() - anchoPalabra;
        int ancho = (zona - separacion * (SEGMENTOS - 1)) / SEGMENTOS;
        int y = (getHeight() - 6) / 2;
        for (int i = 0; i < SEGMENTOS; i++) {
            g.setColor(i < nivel ? COLORES[nivel] : COLORES[0]);
            g.fillRoundRect(i * (ancho + separacion), y, ancho, 6, 6, 6);
        }
        g.setColor(nivel == 0 ? Tema.SUAVE : COLORES[nivel]);
        g.drawString(palabra, zona + 8, (getHeight() - fm.getHeight()) / 2 + fm.getAscent());
        g.dispose();
    }
}
