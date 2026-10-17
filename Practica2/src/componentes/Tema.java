package componentes;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public final class Tema {

    public static final Color PAPEL = new Color(0xF7F4EE);
    public static final Color BLANCO = new Color(0xFFFFFF);
    public static final Color TINTA = new Color(0x1D2B27);
    public static final Color SUAVE = new Color(0x6B736E);
    public static final Color LINEA = new Color(0xD8D2C5);
    public static final Color BOSQUE = new Color(0x1F3D36);
    public static final Color BOSQUE_OSCURO = new Color(0x15302A);
    public static final Color ACENTO = new Color(0xC8793B);
    public static final Color ERROR = new Color(0xB4432F);
    public static final Color EXITO = new Color(0x2F7D5B);

    private static final Set<String> instaladas = new HashSet<>(Arrays.asList(
            GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()));
    private static final String FAMILIA_TEXTO = primeraDisponible(Font.SANS_SERIF,
            "Segoe UI", "Helvetica Neue", "Inter", "Noto Sans", "DejaVu Sans");
    private static final String FAMILIA_TITULO = primeraDisponible(Font.SERIF,
            "Georgia", "Cambria", "Noto Serif", "DejaVu Serif");

    private Tema() {
    }

    public static Font texto(int estilo, float tamano) {
        return new Font(FAMILIA_TEXTO, estilo, 1).deriveFont(tamano);
    }

    public static Font titulo(float tamano) {
        return new Font(FAMILIA_TITULO, Font.BOLD, 1).deriveFont(tamano);
    }

    public static Graphics2D suavizar(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        return g;
    }

    public static Color conAlfa(Color c, int alfa) {
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), alfa);
    }

    public static Image icono() {
        BufferedImage img = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = suavizar(img.createGraphics());
        g.setColor(BOSQUE);
        g.fillRoundRect(2, 2, 60, 60, 18, 18);
        g.setColor(ACENTO);
        g.setStroke(new BasicStroke(6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawArc(20, 12, 24, 24, 0, 180);
        g.setColor(BLANCO);
        g.fillRoundRect(16, 30, 32, 22, 8, 8);
        g.dispose();
        return img;
    }

    private static String primeraDisponible(String respaldo, String... candidatas) {
        for (String nombre : candidatas) {
            if (instaladas.contains(nombre)) {
                return nombre;
            }
        }
        return respaldo;
    }
}
