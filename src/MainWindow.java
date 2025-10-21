package src;

import java.awt.BorderLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;


import javax.swing.JButton;
import javax.swing.JFrame;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;

public class MainWindow implements java.awt.event.ActionListener {
	public JFrame frame = new JFrame();
	public JButton button = new JButton("Hello World!");
	JTabbedPane tabbedPane = new JTabbedPane();
	/*private	JPanel	panel1;
	private	JPanel	panel2;
	private	JPanel	panel3;
	private	JPanel	panel4;*/

	public MainWindow() {
		frame.addNotify();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(400, 400);
		frame.setLayout(null);
		frame.setVisible(true);
		frame.toFront();

		//button.setBounds(100, 160, 200, 40);
		/*button.setFocusable(false);
		button.addActionListener(this);*/
		//frame.add(button);

		JComponent panel1 = new JPanel();
		tabbedPane.addTab("Tab 1", null, panel1,
				  "Does nothing");
		tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);
		tabbedPane.setBounds(100, 160, 200 ,40);
		frame.add(tabbedPane);


		/*JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		frame.add(mainPanel);
		
		itemTabPanel1();
		itemTabPanel2();
		itemTabPanel3();
		itemTabPanel4();

		JTabbedPane tabPane = new JTabbedPane();
		tabPane.addTab( "Tab 1", panel1);
		tabPane.addTab( "Tab 2", panel2);
		tabPane.addTab( "Tab 3", panel3);
		tabPane.addTab( "Tab 4", panel4);
		mainPanel.add(tabPane);
		frame.add(tabPane);*/
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
	
	/*public void itemTabPanel1()
	{
		panel1 = new JPanel();
		panel1.setLayout(null);
		
		JButton btn1 = new JButton("Button 1");
		btn1.setBounds(10, 11, 89, 23);
		panel1.add(btn1);
		
		JButton btn2 = new JButton("Button 2");
		btn2.setBounds(10, 45, 89, 23);
		panel1.add(btn2);
	}

	public void itemTabPanel2()
	{
		panel2 = new JPanel();
		panel2.setLayout(null);
		
		JButton btn3 = new JButton("Button 3");
		btn3.setBounds(10, 11, 89, 23);
		panel2.add(btn3);
		
		JButton btn4 = new JButton("Button 4");
		btn4.setBounds(10, 45, 89, 23);
		panel2.add(btn4);	
	}

	public void itemTabPanel3()
	{
		panel3 = new JPanel();
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
		panel4 = new JPanel();
		panel4.setLayout(null);;
		
		JButton btn7 = new JButton("Button 7");
		btn7.setBounds(10, 11, 89, 23);
		panel4.add(btn7);
		
		JButton btn8 = new JButton("Button 8");
		btn8.setBounds(10, 45, 89, 23);
		panel4.add(btn8);	
	}*/


}