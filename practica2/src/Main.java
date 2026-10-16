import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        Estilo.ui();
        SwingUtilities.invokeLater(() -> new FrmLogin().setVisible(true));
    }
}
