import javax.swing.SwingUtilities;
import java.net.URISyntaxException;

public class Main {
    public static void main(String[] args) {
        // Запускаем главное окно в Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                GUI gui = new GUI();
            }
        });
    }
}