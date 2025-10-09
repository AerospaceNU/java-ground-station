package src;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

import javax.swing.Icon;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

public class csv {

	public static Icon testCSVprint() throws IOException {
		String outputFileName = "Contacts.csv";
		String[] headers = new String[] {"First Name", "Last Name" };
		
		try(Writer writer = new FileWriter(outputFileName);
				CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader(headers))
						) {
			csvPrinter.printRecord("Dannielle", "Wilks");
			csvPrinter.printRecord("Harvir", "Mathews");
			csvPrinter.printRecord("Sahil", "Rojas");
			csvPrinter.printRecord("Eileen", "Pike");
			csvPrinter.printRecord("Matias", "Moreno");
		}
		return null;
	}

    public static void printCsv(List<String[]> data, String filePath) {
        try (CSVPrinter printer = new CSVPrinter(new FileWriter(filePath), CSVFormat.DEFAULT)) {
            for (String[] row : data) {
                printer.printRecord((Object[]) row);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
