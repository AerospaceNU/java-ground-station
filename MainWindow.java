import javax.swing.JFrame;
import javax.swing.JButton;

public class MainWindow {
    public JFrame frame = new JFrame();
    public JButton button = new JButton();

     public MainWindow()  {
        frame.addNotify();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);
        frame.setLayout(null);
        frame.setVisible(true);
        frame.toFront();

        button.setBounds(100,160,200,40);
        button.setFocusable(false);
        //button.addActionListener(this);
        frame.add(button);
    }
}