package src;

import com.fazecast.jSerialComm.SerialPort;
import javax.swing.*;

import java.awt.BorderLayout;
import java.io.InputStream;

public class SerialTab extends JPanel implements Runnable {
    private final SerialPort port;
    private final JTextArea textArea = new JTextArea();

    public SerialTab(SerialPort port) {
        this.port = port;
        setLayout(new BorderLayout());
        textArea.setEditable(false);
        add(new JScrollPane(textArea), BorderLayout.CENTER);

        // Open the port
        port.openPort();
        port.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 100, 0);

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
}

