package src;

import java.awt.BorderLayout;
//import java.awt.Color;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import src.testing.GraphWindow;

import javax.swing.JButton;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;

public class MainWindow implements java.awt.event.ActionListener {
	public JFrame frame = new JFrame();
	public JButton button = new JButton("Hello World!");
	JTabbedPane tabbedPane = new JTabbedPane();
	JPanel mainPanel = new JPanel();
	private	JPanel	panel1 = new JPanel();
	private	JPanel	panel2 = new JPanel();
	private	JPanel	panel3 = new JPanel();
	private	JPanel	panel4 = new JPanel();

	public MainWindow() {
		/*
		// mainPanel.setLayout(new BorderLayout());
		// frame.getContentPane().add(mainPanel);
		//mainPanel.add(panel1);

		/*button.setBounds(100, 160, 200, 40);
		button.setFocusable(false);
		button.addActionListener(this);
		frame.add(button); 

		*/

		            JFrame frame = new JFrame("Avionics Ground Station");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(400, 300);

            JTabbedPane tabs = new JTabbedPane();

			itemTabPanel1();
			itemTabPanel2();
			itemTabPanel3();
			itemTabPanel4();

            tabs.addTab("SillyGoose", panel1);
            tabs.addTab("Volt", panel2);
            tabs.addTab("Third", panel3);
			tabs.addTab("Fourth", panel4);

            frame.add(tabs);
            frame.setVisible(true);
	}


	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == button) {
			frame.dispose();
            /*try {
                csv.testCSVprint();
            } catch (IOException e1) {
                e1.printStackTrace();
            }*/
			GraphWindow graphWindow = new GraphWindow();

		}
	}
	
	public void itemTabPanel1()
	{
		 panel1 = new JPanel(); // FlowLayout by default
            panel1.add(new JLabel("Tab 1 Content"));
            panel1.add(new JButton("Button 1"));
		
		JButton btn2 = new JButton("Button 2");
		btn2.setBounds(10, 45, 89, 23);
		panel1.add(btn2);

		/* String name = JOptionPane.showInputDialog("Enter your name:");

        // Prompt the user for their age
        String ageString = JOptionPane.showInputDialog("Enter your age:");
        int age = Integer.parseInt(ageString); // Convert the string input to an integer

        // Display the collected information using a message dialog
        JOptionPane.showMessageDialog(null, "Hello, " + name + "!\nYou are " + age + " years old.");*/

		
	}

	public void itemTabPanel2()
	{
		panel2 = new JPanel();
		panel2.add(new JLabel("Tab 2 Content"));
            panel2.add(new JButton("Button 3"));
		
		JButton btn4 = new JButton("Button 4");
		btn4.setBounds(10, 45, 89, 23);
		panel2.add(btn4);	
	}

	public void itemTabPanel3()
	{
		panel3.setLayout(null);;
		
		JButton btn5 = new JButton("Button 5");
		btn5.setBounds(10, 11, 89, 23);
		panel3.add(btn5);
		
		JButton btn6 = new JButton("Button 6");
		btn6.setBounds(10, 45, 89, 23);
		panel3.add(btn6);		
		
	}
	
	public void itemTabPanel4()
	{
		panel4.setLayout(null);;
		
		JButton btn7 = new JButton("Button 7");
		btn7.setBounds(10, 11, 89, 23);
		panel4.add(btn7);
		
		JButton btn8 = new JButton("Button 8");
		btn8.setBounds(10, 45, 89, 23);
		panel4.add(btn8);	
	}

}