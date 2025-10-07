import javax.swing.JFrame;
import javax.swing.JLabel;
//import javax.swing.JButton;

public class GraphWindow {
    public JFrame frame = new JFrame();
    public JLabel label = new JLabel("This is the test Graph Window");

     public GraphWindow()  {
        frame.addNotify();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);
        frame.setLayout(null);
        frame.setVisible(true);
        frame.toFront();

        label.setBounds(100,160,200,40);
        frame.add(label);
    }
}