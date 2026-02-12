package src;

import com.fazecast.jSerialComm.SerialPort;
import javax.swing.*;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;



public class SerialTab extends JPanel implements Runnable, java.awt.event.ActionListener{
    //public SerialPort[] ports = SerialPort.getCommPorts();
   // private SerialPort port = ports[0]; //let the user choose 
    private final SerialPort port;
    private final JTextArea textArea = new JTextArea();
    private JTabbedPane subTabbedPane = new JTabbedPane();
	private JPanel subTab1Content = new JPanel();
	private JPanel subTab2Content = new JPanel();
    public final JButton submitButton = new JButton("set"); // final ?
	public JLabel l = new JLabel("Type your prompt here. It must have two parts seperated by a space.");
	public final JTextField console = new JTextField(16); //final?
    private int timeoutMs = 100; //milliseconds for timeout, maybe make it final?

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
        subTabbedPane.setBackground(Color.GREEN);

        // Open the port
        port.openPort();
        port.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, timeoutMs, 0);

        //add content to the subtabs
        //subTab1Content.setSize();
        //subTab1Content.setBackground(Color.DARK_GRAY);
        subTab1Content.setLayout(new BorderLayout());
        subTab1Content.add(new JScrollPane(textArea), BorderLayout.CENTER);
        //subTab2Content.setBackground(Color.DARK_GRAY);
		subTab2Content.add(console);
        subTab2Content.add(l);
    	subTab2Content.add(submitButton);

        // Start a thread to read data
        new Thread(this).start();
    }

    // should check if data is available to read from the serial input (not working?)
    private Boolean isDataAvailable(int len) {
			if (len == 0){
                return false;
            }
            else{
                return true;
            }
	}

    @Override
    public void run() {
        try (InputStream in = port.getInputStream();) {
            byte[] buffer = new byte[1024];
            while (port.isOpen()) {
                int length = in.read(buffer);
                if (isDataAvailable(length) == true) {
                    String received = new String(buffer, 0, length);
                    SwingUtilities.invokeLater(() -> textArea.append(received));
                    /*String sent = new String(datatosend);
                    SwingUtilities.invokeLater(() -> textArea.append(sent));*/
                }
            }
        } catch (Exception e) {
            //add another try and catch here...and simplify the existing code...
            System.err.println("Error during serial communication: " + e.getMessage() + " Let's try again!");
            try (InputStream in = port.getInputStream();){
                byte[] buffer = new byte[2048];
                // no isDataAvailable(length) in this catch block
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
            int count = 0;

            for (int i = 0; i < (console.getText()).length(); i++){
                if ((console.getText()).charAt(i) == ' '){
                    count++;
                }
            }

            if (count == 1){
			    l.setText("Sent '" + console.getText() + "'");
            }
            else{
                l.setText("'" + console.getText() + "' didn't send as it doesn't fit the prompt format. Try again.");
            }
            //Commands command = new Commands(console.getText());
            //byte[] byteToSend = (command.concatenatedCommand).getBytes();
			/*cool.setHorizontalAlignment(SwingConstants.CENTER);
				subTab2Content.add(cool, BorderLayout.CENTER);
				mainPanel.revalidate();*/
           /*  try (OutputStream out = port.getOutputStream();) {
                //byte[] datatosend = (console.getText()).getBytes();
                //OutputStream out = port.getOutputStream();
                out.write(byteToSend);
                out.flush();
                String sent = new String(byteToSend);
                System.out.println("Sent data: " + sent);
            }
         catch(Exception e1){
                System.err.println("Error sending data: " + e1.getMessage());
            }*/
		}
        try (OutputStream out = port.getOutputStream();) { //the command prompt entered must be split into two parts with a space.
            //byte[] datatosend = (console.getText()).getBytes();
            //OutputStream out = port.getOutputStream();
            Commands command = new Commands(console.getText());
            //Charset charset = StandardCharsets.UTF_16;
            byte[] byteToSend = (command.concatenatedCommand).getBytes(StandardCharsets.UTF_16); //use another charset?
            out.write(byteToSend);
            out.flush();
            String sent = new String(byteToSend, StandardCharsets.UTF_16);
            System.out.println("Sent data: " + sent);
            //System.out.println(byteToSend);
        }
        catch(Exception e1){
            System.err.println("Error sending data: " + e1.getMessage());
        }
	}


    /*public void reset(String s){
        if (s == "--" + ) {
            //put code here
        }
    }*/
}

