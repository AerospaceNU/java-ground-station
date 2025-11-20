package src;

import com.fazecast.jSerialComm.SerialPort;
import javax.swing.*;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class SerialTab extends JPanel implements Runnable, java.awt.event.ActionListener{
    //public SerialPort[] ports = SerialPort.getCommPorts();
   // private SerialPort port = ports[0]; //let the user choose 
    private final SerialPort port;
    private final JTextArea textArea = new JTextArea();
    private JTabbedPane subTabbedPane = new JTabbedPane();
	private JPanel subTab1Content = new JPanel();
	private JPanel subTab2Content = new JPanel();
    public final JButton submitButton = new JButton("set"); // final ?
	public JLabel l = new JLabel();
	public final JTextField console = new JTextField(16); //final?

    //OutputStream outputStream = port.getOutputStream();

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
        try (InputStream in = port.getInputStream();) {
            byte[] buffer = new byte[1024];
            while (port.isOpen()) {
                int length = in.read(buffer);
                if (length > 0) {
                    String received = new String(buffer, 0, length);
                    SwingUtilities.invokeLater(() -> textArea.append(received));
                    /*String sent = new String(datatosend);
                    SwingUtilities.invokeLater(() -> textArea.append(sent));*/
                }
            }
        } catch (Exception e) {
            //add another try and catch here...and simplify the existing code...
            System.err.println("Error during serial communication: " + e.getMessage() + ". Let's try again!");
            try (InputStream in = port.getInputStream();){
                byte[] buffer = new byte[1024];
                // no && isDataAvailable(buffer) in this catch block
                while(port.isOpen()) {
                    int length = in.read(buffer);
                    if (length > 0) {
                        String recieved = new String(buffer, 0, length);
                        SwingUtilities.invokeLater(() -> textArea.append(recieved));
                    }
                }
            
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
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
			l.setText("Sent '" + console.getText() + "'");
			/*cool.setHorizontalAlignment(SwingConstants.CENTER);
				subTab2Content.add(cool, BorderLayout.CENTER);
				mainPanel.revalidate();*/
		}
        try (OutputStream out = port.getOutputStream();) {
            byte[] datatosend = (console.getText()).getBytes();
            //OutputStream out = port.getOutputStream();
            out.write(datatosend);
            out.flush();
            String sent = new String(datatosend);
            System.out.println("Sent data: " + sent);
        }
        catch(Exception e1){
            System.err.println("Error sending data: " + e1.getMessage());
        }
	}

    // should check if data is available to read from the serial input (not working?)
    private Boolean isDataAvailable(byte[] portData) {
		while (port.isOpen()) {
			if(portData == null){
                return false;
            }
            else{
                return true;
            }
		}
	}

    public void reset(String s){
        if (s == "--" + ) {
            //put code here
        }
    }
}

