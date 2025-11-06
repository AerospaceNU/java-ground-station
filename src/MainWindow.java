package src;

import java.awt.BorderLayout;
//import java.awt.Color;
import java.awt.Color;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.Border;

import com.fazecast.jSerialComm.SerialPort;

import src.testing.GraphWindow;

import javax.swing.JButton;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MainWindow {
	public JFrame frame = new JFrame();
	JTabbedPane tabbedPane = new JTabbedPane();
	private final Map<String, SerialTab> activeTabs = new HashMap<>();
	JPanel mainPanel = new JPanel();
	private	JPanel	panel1 = new JPanel();
	private	JPanel	panel2 = new JPanel();
	private	JPanel	panel3 = new JPanel();
	private	JPanel	panel4 = new JPanel();

	public MainWindow() {
		// build and show the UI
		SwingUtilities.invokeLater(() -> {
			frame.setTitle("Avionics Ground Station");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setSize(600, 400);
			frame.setLayout(new BorderLayout());
			//frame.setBackground(Color.DARK_GRAY);

			// mainPanel features tabbedPane which holds all serial tabs
			tabbedPane.setBackground(Color.GRAY);
			mainPanel.setLayout(new BorderLayout());
			//mainPanel.add(tabbedPane, BorderLayout.CENTER);
			//frame.getContentPane().add(mainPanel, BorderLayout.CENTER);

			frame.setLocationRelativeTo(null);
			frame.setVisible(true);

			// starts detecting new ports every 2 seconds
			new Timer(2000, e -> checkForNewPorts()).start();
		});
	}


	private void checkForNewPorts() {
		for (SerialPort port : SerialPort.getCommPorts()) {
            String portName = port.getSystemPortName();
			if (!activeTabs.containsKey(portName) && portName.contains("cu.usbmodem")) {
				System.out.println("New serial device detected: " + portName);
				//create the serial tab which will be used for active tabs
				SerialTab tab = new SerialTab(port);

				mainPanel.add(tabbedPane, BorderLayout.CENTER);
				frame.getContentPane().add(mainPanel, BorderLayout.CENTER);

				//tracks the tabs with activeTabs and adds a tab to the tabbedPane
				activeTabs.put(portName, tab);
				tabbedPane.addTab(portName, tab);

				//sub tab content
				/*subTab1Content.add(new JLabel("Content of Subtab 1"));
				subTab2Content.add(console);
    			subTab2Content.add(submitButton);*/

				//sub tabs added to each tab
				/*tabbedPane.add(subTabbedPane, tab);
				subTabbedPane.addTab("Debug", subTab1Content);
				subTabbedPane.addTab("Config", subTab2Content);
				*/

				// updates
				tabbedPane.revalidate();
				tabbedPane.repaint();
				frame.revalidate();
				frame.repaint();
			}
			else{
				JLabel label = new JLabel("No compatible USB devices connected. Please try again.");
				label.setHorizontalAlignment(SwingConstants.CENTER);
				mainPanel.add(label, BorderLayout.CENTER);
				mainPanel.revalidate();
			}
        }

        // removes objects and tabs by checking if they still exist or not
        activeTabs.keySet().removeIf(portName -> {
            boolean stillExists = Arrays.stream(SerialPort.getCommPorts())
                                        .anyMatch(p -> p.getSystemPortName().equals(portName));
            if (!stillExists) {
                removeTab(portName);
            }
            return !stillExists;
        });

	} 

	private void removeTab(String portName) {
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            if (tabbedPane.getTitleAt(i).equals(portName)) {
                tabbedPane.remove(i);
                System.out.println("Removed tab for disconnected device: " + portName);
                break;
            }
        }
    }

	//button gets triggered
	/* 
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == submitButton) {
			subTab2Content.add(l);
			l.setText(console.getText());
			/*cool.setHorizontalAlignment(SwingConstants.CENTER);
				subTab2Content.add(cool, BorderLayout.CENTER);
				mainPanel.revalidate();
		}
	} 
	*/

	//outdated code from previous implentation. It's still a useful reference for UI building!
	public void itemTabPanel1()
	{
		checkForNewPorts();
		 panel1 = new JPanel(); // default
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