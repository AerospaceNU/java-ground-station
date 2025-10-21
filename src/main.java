package src;
//import java-ground-station.MainWindow;
import java.awt.EventQueue;
import java.awt.BorderLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class main {
    private	JPanel	panel1;
	private	JPanel	panel2;
	private	JPanel	panel3;
	private	JPanel	panel4;
    public JFrame frame = new JFrame();
    public static void main(String[] args){
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                MainWindow mainWindow = new MainWindow();
            }
    });
}
}