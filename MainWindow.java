import javax.swing.JFrame;
import javax.swing.JButton;

public class MainWindow {
    JFrame frame = new JFrame();
    JButton button = new JButton();

     public mainWindow()  {
        button.setBounds(100,160,200,40);
        button.setFocusable(false);
        button.addActionListener(this);
        frame.add(button);
        
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);
        frame.setLayout(null);
        frame.setVisible(true);
    }
}