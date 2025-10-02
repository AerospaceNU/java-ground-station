import javax.swing.JFrame;
import javax.swing.JButton;
import java.awt.Window;

public class MainWindow {
    JFrame frame = new JFrame();
    Window window = new Window(frame);
    JButton button = new JButton();

     public void mainWindow()  {
        button.setBounds(100,160,200,40);
        button.setFocusable(false);
        //button.addActionListener(this);
        frame.add(button);
        
        frame.addNotify();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setSize(400, 400);
        window.setLayout(null);
        window.setVisible(true);
        window.toFront();
    }
}