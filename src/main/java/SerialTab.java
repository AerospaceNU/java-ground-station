

import com.fazecast.jSerialComm.SerialPort;

import javax.imageio.ImageIO;
import javax.swing.*;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HexFormat;
import java.util.List;
import java.awt.image.BufferedImage;

public class SerialTab extends JPanel implements Runnable, java.awt.event.ActionListener{
    private final SerialPort port;
    private final JTextArea textArea = new JTextArea();
    private JTabbedPane subTabbedPane = new JTabbedPane();
    private JPanel subTab1Content = new JPanel();
    private JPanel subTab2Content = new JPanel();
    private JPanel subTab3Content = new JPanel();
    public final JButton submitButton = new JButton("set"); // final ?
    public JLabel l = new JLabel("Type your prompt here. It must have two parts seperated by a space.");
    ImageIcon icon = new ImageIcon("./src/main/java/testing/my-qrcode.png");
    public JLabel image = new JLabel(icon); // image is updated in run() to refresh the QR code display
    public final JTextField console = new JTextField(16); // final?
    private int timeoutMs = 0; // milliseconds for read timeout; 0 = wait indefinitely (semi-blocking)

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

        // Open the port and configure its read timeout
        port.openPort();
        port.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, timeoutMs, 0);

        //add content to the subtabs
        //subTab1Content.setSize();
        //subTab1Content.setBackground(Color.DARK_GRAY);

        // Debug tab: scrollable raw output area
        subTab1Content.setLayout(new BorderLayout());
        subTab1Content.add(new JScrollPane(textArea), BorderLayout.CENTER);

        // Config tab: text field + label + submit button for sending commands
        //subTab2Content.setBackground(Color.DARK_GRAY);
        subTab2Content.add(console);
        subTab2Content.add(l);
        subTab2Content.add(submitButton);

        // QR Code tab: displays the QR code image (updated dynamically in run())
        // Note: these local `icon` and `image` shadow the instance fields above.
        // The instance fields are what get updated in run(); these locals only populate the initial tab UI.
        ImageIcon icon = new ImageIcon("./src/main/java/testing/my-qrcode.png");
        JLabel image = new JLabel(icon);
        subTab3Content.add(image);

        // Start a background thread to continuously read from the serial port
        new Thread(this).start();
    }

    // Returns true if bytes were read (len > 0), false otherwise
    private Boolean isDataAvailable(int len) {
        return len > 0;
    }

    // Searches a byte array for a target byte value; returns its index or -1 if not found
    public static int findIndex(byte[] arr, int target) {
        if (arr == null) {
            return -1;
        }
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == target) {
                return i; // found the target byte (e.g. a start/end flag)
            }
        }
        return -1;
    }

    @Override
    public void run() {
        try (InputStream in = port.getInputStream();) {
            byte[] buffer = new byte[1024];
            int i = 0;
            int k = 0; // unused counter — reserved for future use
            while (port.isOpen()) {
                int length = in.read(buffer);
                if (isDataAvailable(length) == true) {
                    i++;
                    // `hex` and `hexString` kept for debugging convenience; not currently displayed
                    String hex = String.format("%02X ", buffer[i] & 0xFF);
                    String hexString = HexFormat.ofDelimiter(" ").formatHex(buffer);
                    String received = new String(buffer, 0, length); // raw string form, not currently displayed

                    //SwingUtilities.invokeLater(() -> textArea.append(received));

                    // Commented-out alternative: parse each byte as a hex int
                    // String[] parts = hexString.split(" ");
                    // List<Integer> parsedIntList = new ArrayList<>();
                    // for (int j = 0; j < length; j++) {
                    //     parsedIntList.add(Integer.parseInt(parts[j], 16)); //check if parts[j] is a str or a int?
                    // }

                    // Trim buffer to the actual number of bytes read
                    byte[] actualBytes = Arrays.copyOf(buffer, length);

                    // Combine all bytes into a single long value (big-endian)
                    long value = 0;
                    for (int j = 0; j < actualBytes.length; j++) {
                        value = (value << 8) | (actualBytes[j] & 0xFF);
                    }

                    String parsedInt = Long.toString(value); // numeric representation of received bytes
                    SwingUtilities.invokeLater(() -> textArea.append(parsedInt + "\n"));

                    // Parse the bytes as a GPS packet and generate a QR code if a valid link is returned
                    try (ByteArrayInputStream bais = new ByteArrayInputStream(actualBytes)) {
                        String websiteLink = GpsParser.parseSerial(bais);
                        if (websiteLink != null) {
                            String outputPath = "./src/main/java/testing/my-qrcode.png";
                            int qrCodeSize = 200;
                            try {
                                System.out.println(websiteLink);
                                QRGenerator.generateQRCode(websiteLink, outputPath, qrCodeSize);

                                // Reload the QR image from disk and update the label
                                icon = new ImageIcon("./src/main/java/testing/my-qrcode.png");
                                image = new JLabel(icon);

                                BufferedImage qrImage = ImageIO.read(new File(outputPath));
                                SwingUtilities.invokeLater(() -> {
                                    image.setIcon(new ImageIcon(qrImage));
                                    image.revalidate();
                                    image.repaint();
                                });
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                        }
                    } catch (Exception e2) {
                        System.err.println("Error parsing GPS data: " + e2.getMessage());
                    }
                    /*String sent = new String(datatosend);
                    SwingUtilities.invokeLater(() -> textArea.append(sent));*/
                }
            }
        } catch (Exception e) {
            // If the primary read loop throws, retry with a simplified loop (no isDataAvailable check)
            System.err.println("Error during serial communication: " + e.getMessage() + " Let's try again!");
            try (InputStream in = port.getInputStream();) {
                byte[] buffer = new byte[1024];
                while (port.isOpen()) {
                    //IntroRocketData rocketData;
                    //int startIndex = findIndex(buffer, IntroRocketData.startFlag);
                    //int endIndex = findIndex(buffer, IntroRocketData.endFlag);
                    //int length = in.read(buffer, startIndex, endIndex);
                    int length = in.read(buffer);
                    int i = 0;
                    if (length > 0) {
                        i++;
                        // `hex`, `hexString`, `received` retained for debugging; not currently displayed
                        String hex = String.format("%02X ", buffer[i] & 0xFF);
                        String hexString = HexFormat.ofDelimiter(" ").formatHex(buffer);
                        String received = new String(buffer, 0, length);
                        //SwingUtilities.invokeLater(() -> textArea.append(received));

                        // Commented-out alternative hex parse approach
                        // String[] parts = hexString.split(" ");
                        // List<Integer> parsedIntList = new ArrayList<>();
                        // for (int j = 0; j < length; j++) {
                        //     parsedIntList.add(Integer.parseInt(parts[j], 16)); //check if parts[j] is a str or a int?
                        // }

                        byte[] actualBytes = Arrays.copyOf(buffer, length);

                        // Combine bytes into a single big-endian long
                        long value = 0;
                        for (int j = 0; j < actualBytes.length; j++) {
                            value = (value << 8) | (actualBytes[j] & 0xFF);
                        }

                        String parsedInt = Long.toString(value);
                        SwingUtilities.invokeLater(() -> textArea.append(parsedInt + "\n"));

                        try (ByteArrayInputStream bais = new ByteArrayInputStream(actualBytes)) {
                            //bais.skip(3);
                            String websiteLink = GpsParser.parseSerial(bais);
                            if (websiteLink != null) {
                                String outputPath = "./src/main/java/testing/my-qrcode.png";
                                int qrCodeSize = 200;
                                try {
                                    System.out.println(websiteLink);
                                    QRGenerator.generateQRCode(websiteLink, outputPath, qrCodeSize);
                                    icon = new ImageIcon("./src/main/java/testing/my-qrcode.png");

                                    BufferedImage qrImage = ImageIO.read(new File(outputPath));
                                    SwingUtilities.invokeLater(() -> {
                                        image.setIcon(new ImageIcon(qrImage));
                                        image.revalidate();
                                        image.repaint();
                                    });
                                } catch (IOException e1) {
                                    e1.printStackTrace();
                                }
                            }
                        } catch (Exception e3) {
                            System.err.println("Error parsing GPS data: " + e3.getMessage());
                        }
                    }
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    @Override
    public void removeNotify() {
        super.removeNotify();
        port.closePort(); // close the serial port when this panel is removed from the UI
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == submitButton) {
            subTab2Content.add(l);
            int count = 0;

            // Count the number of spaces to validate the "two-part" prompt format
            for (int i = 0; i < (console.getText()).length(); i++) {
                if ((console.getText()).charAt(i) == ' ') {
                    count++;
                }
            }

            if (count == 1) {
                l.setText("Sent '" + console.getText() + "'");
                    // Send the command over serial — uses UTF_16 encoding (consider whether the receiver expects this)
                    try (OutputStream out = port.getOutputStream();) {
                        //byte[] datatosend = (console.getText()).getBytes();
                        //OutputStream out = port.getOutputStream();
                        Commands command = new Commands(console.getText());
                        //Charset charset = StandardCharsets.UTF_16;
                        byte[] byteToSend = (command.concatenatedCommand).getBytes(StandardCharsets.UTF_16); // use another charset?
                        out.write(byteToSend);
                        out.flush();
                        String sent = new String(byteToSend, StandardCharsets.UTF_16);
                        System.out.println("Sent data: " + sent);
                        //System.out.println(byteToSend);
                    } catch (Exception e1) {
                        System.err.println("Error sending data: " + e1.getMessage());
                    }
            } else {
                l.setText("'" + console.getText() + "' didn't send as it doesn't fit the prompt format. Try again.");
            }
        }
    }
}
