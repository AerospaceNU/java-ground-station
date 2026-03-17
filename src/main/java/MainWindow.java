

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

	public MainWindow() {
		// build and show the UI
		SwingUtilities.invokeLater(() -> {
			frame.setTitle("Avionics Ground Station");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setSize(600, 400);
			frame.setLayout(new BorderLayout());
			frame.setBackground(Color.BLACK);

			// mainPanel features tabbedPane which holds all serial tabs
			tabbedPane.setBackground(Color.GREEN);
			mainPanel.setBackground(Color.GRAY);
			mainPanel.setLayout(new BorderLayout());

			frame.setLocationRelativeTo(null);
			frame.setVisible(true);

			// starts detecting new ports every 2 seconds
			new Timer(2000, e -> checkForNewPorts()).start();
		});
	}


	private void checkForNewPorts() {
		for (SerialPort port : SerialPort.getCommPorts()) {
            String portName = port.getSystemPortName();
			if (!activeTabs.containsKey(portName) && (portName.contains("cu.usbmodem") || portName.contains("COM"))) {
				System.out.println("New serial device detected: " + portName);
				//create the serial tab which will be used for active tabs
				SerialTab tab = new SerialTab(port);

				mainPanel.add(tabbedPane, BorderLayout.CENTER);
				frame.getContentPane().add(mainPanel, BorderLayout.CENTER);

				//tracks the tabs with activeTabs and adds a tab to the tabbedPane
				activeTabs.put(portName, tab);
				tabbedPane.addTab(portName, tab);

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
		
	}

}