

import com.fazecast.jSerialComm.SerialPort;
import javax.swing.*;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.imageio.ImageIO;
import java.nio.charset.StandardCharsets;



public class SerialTab extends JPanel implements Runnable, java.awt.event.ActionListener{
    //public SerialPort[] ports = SerialPort.getCommPorts();
   // private SerialPort port = ports[0]; //let the user choose 
    private final SerialPort port;
    private final JTextArea textArea = new JTextArea();
    private JTabbedPane subTabbedPane = new JTabbedPane();
	private JPanel subTab1Content = new JPanel();
	private JPanel subTab2Content = new JPanel();
    private JPanel subTab3Content = new JPanel();
    private static final long QR_UPDATE_INTERVAL_MS = 1000;
    private static final String QR_OUTPUT_PATH = "./src/main/java/testing/my-qrcode.png";
    private static final int QR_CODE_SIZE = 200;
    private long lastQrUpdateTimeMs = 0;
    public final JButton submitButton = new JButton("set"); // final ?
	public JLabel l = new JLabel("Type your prompt here. It must have two parts seperated by a space.");
	private final JLabel image = new JLabel(new ImageIcon(QR_OUTPUT_PATH));
	public final JTextField console = new JTextField(16); //final?
    private int timeoutMs = 0; //milliseconds for timeout, maybe make it final?

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
        subTabbedPane.addTab("QR Code", subTab3Content);
        subTabbedPane.setBackground(Color.GREEN);

        // Open the port
        port.openPort();
        port.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, timeoutMs, 0);

        //add content to the subtabs
        //subTab1Content.setSize();
        //subTab1Content.setBackground(Color.DARK_GRAY);

        subTab3Content.add(image);


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
			if (len <= 0){
                return false;
            }
            else{
                return true;
            }
	}

    public static int findIndex(byte[] arr, int target) {
        if (arr == null) {
            return -1;
        }
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == target) {
                return i; // Return the index of the byte[], startFlag
            }
        }
        return -1; // Return -1 if the element is not found
    }

    @Override
    public void run() {
        try (InputStream in = port.getInputStream()) {
            while (port.isOpen()) {
                try {
                    String websiteLink = GpsParser.parseSerial(in);
                    if (websiteLink != null) {
                        String sanitizedLink = websiteLink.replaceAll("\\s+", "");
                        System.out.println(websiteLink);
                        SwingUtilities.invokeLater(() -> textArea.append(sanitizedLink + "\n"));
                        long nowMs = System.currentTimeMillis();
                        if ((nowMs - lastQrUpdateTimeMs) >= QR_UPDATE_INTERVAL_MS) {
                            BufferedImage qrImage = QRGenerator.generateQRCodeImage(sanitizedLink, QR_CODE_SIZE);
                            ImageIO.write(qrImage, "PNG", new File(QR_OUTPUT_PATH));
                            refreshQrImage(qrImage);
                            lastQrUpdateTimeMs = nowMs;
                        }
                    }
                } catch (Exception parseError) {
                    System.err.println("Error parsing GPS data: " + parseError.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("Error during serial communication: " + e.getMessage());
        }
    }

    private void refreshQrImage(BufferedImage updatedImage) {
        SwingUtilities.invokeLater(() -> {
            image.setIcon(new ImageIcon(updatedImage));
            image.revalidate();
            image.repaint();
        });
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

