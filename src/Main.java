import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // Запускаем главное окно в Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                MainWindow window = new MainWindow();
            }
        });
    }
}