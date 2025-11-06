package src;

import com.fazecast.jSerialComm.SerialPort;
import javax.swing.*;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.io.InputStream;

public class SerialTab extends JPanel implements Runnable, java.awt.event.ActionListener{
    private final SerialPort port;
    private final JTextArea textArea = new JTextArea();
    private JTabbedPane subTabbedPane = new JTabbedPane();
	private JPanel subTab1Content = new JPanel();
	private JPanel subTab2Content = new JPanel();
    public JButton submitButton = new JButton("set");
	public JLabel l = new JLabel();
	public static JTextField console = new JTextField(16);

    public SerialTab(SerialPort port) {
        this.port = port;
        setLayout(new BorderLayout());
        textArea.setEditable(false);
        //add(new JScrollPane(textArea), BorderLayout.CENTER);
        submitButton.addActionListener(this);
        add(subTabbedPane, BorderLayout.CENTER);
        subTabbedPane.addTab("Debug", subTab1Content);
        subTabbedPane.addTab("Config", subTab2Content);

        // Open the port
        port.openPort();
        port.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 100, 0);

        //add content to the subtabs
        //subTab1Content.setSize();
        subTab1Content.setLayout(new BorderLayout());
        subTab1Content.add(new JScrollPane(textArea), BorderLayout.CENTER);
		subTab2Content.add(console);
    	subTab2Content.add(submitButton);

        // Start a thread to read data
        new Thread(this).start();
    }

    @Override
    public void run() {
        try (InputStream in = port.getInputStream()) {
            byte[] buffer = new byte[1024];
            while (port.isOpen()) {
                int numRead = in.read(buffer);
                if (numRead > 0) {
                    String received = new String(buffer, 0, numRead);
                    SwingUtilities.invokeLater(() -> textArea.append(received));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removeNotify() {
        super.removeNotify();
        port.closePort();
    }

    public void actionPerformed(ActionEvent e) {
		if (e.getSource() == submitButton) {
			subTab2Content.add(l);
			l.setText(console.getText());
			/*cool.setHorizontalAlignment(SwingConstants.CENTER);
				subTab2Content.add(cool, BorderLayout.CENTER);
				mainPanel.revalidate();*/
		}
	}

    public void reset(String s){
        if (s == "--reset") {
            //put code here
        }
    }
}

