package src;
//import java-ground-station.MainWindow;
import java.awt.EventQueue;
import java.awt.BorderLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

public class main {
    public static void main(String[] args){
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                MainWindow mainWindow = new MainWindow();
                SwingUtilities.invokeLater(MainWindow::new);
            }
    });
}
}