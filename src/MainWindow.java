package src;

import javax.swing.JButton;
import javax.swing.JFrame;

import java.awt.event.ActionEvent;
import java.io.IOException;

public class MainWindow implements java.awt.event.ActionListener {
	public JFrame frame = new JFrame();
	public JButton button = new JButton("Hello World!");

	public MainWindow() {
		frame.addNotify();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(400, 400);
		frame.setLayout(null);
		frame.setVisible(true);
		frame.toFront();

		button.setBounds(100, 160, 200, 40);
		button.setFocusable(false);
		button.addActionListener(this);
		frame.add(button);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == button) {
			frame.dispose();
            try {
                csv.testCSVprint();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
			GraphWindow graphWindow = new GraphWindow();
		}
	}
}