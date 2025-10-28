package src.testing;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import javax.swing.JFrame;
import javax.swing.JLabel;
//import javax.swing.JButton;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

public class GraphWindow {
    public JFrame frame = new JFrame();
    public JLabel label = new JLabel("This is the test Graph Window");
    public JLabel test;

    public void readCSV() throws IOException {
        try (Reader in = new FileReader("Contacts.csv")) {
            Iterable<CSVRecord> records = CSVFormat.DEFAULT.parse(in);
            for (CSVRecord record : records) {
                String columnOne = record.get(0);
                String columnTwo = record.get(1);
            }
        }
    }

     public GraphWindow()   {
        frame.addNotify();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);
        frame.setLayout(null);
        frame.setVisible(true);
        frame.toFront();

        label.setBounds(100,160,200,40);
        frame.add(label);
        try {
            readCSV();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}