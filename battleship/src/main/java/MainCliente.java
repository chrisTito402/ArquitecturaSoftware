import views.frames.FrmMenuPrincipal;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class MainCliente {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("No se pudo establecer el Look and Feel: " + e.getMessage());
        }

        SwingUtilities.invokeLater(() -> {
            System.out.println("=================================");
            System.out.println("   BATTLESHIP - CLIENTE");
            System.out.println("=================================");

            FrmMenuPrincipal menu = new FrmMenuPrincipal();
            menu.setVisible(true);
        });
    }
}
