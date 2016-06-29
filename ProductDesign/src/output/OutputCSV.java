package output;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import com.csvreader.CsvWriter;

import general.CustomerProfile;

import java.util.ArrayList;

import javax.swing.JOptionPane;

/** Clase que contiene el método que genera un archivo csv */
public class OutputCSV {

	public OutputCSV() {
	}

	/**
	 * Método que crea un fichero csv a partir de los perfiles de clientes
	 * generados
	 */
	public void WriteCSV(ArrayList<CustomerProfile> customer, String archivo)
			throws FileNotFoundException, IOException {
		boolean alreadyExists = new File(archivo).exists();

		if (alreadyExists) {
			File Profiles = new File(archivo);
			Profiles.delete();
		}
		try {

			CsvWriter csvOutput = new CsvWriter(new FileWriter(archivo, true), ',');
			csvOutput.write("indice");
			csvOutput.write("atributo");
			csvOutput.write("valoracion1");
			csvOutput.write("valoracion2");
			csvOutput.write("valoracion3");
			csvOutput.write("valoracion4");
			csvOutput.write("valoracion5");
			//csvOutput.write("valoracion6");
			//csvOutput.write("valoracion7");
			csvOutput.endRecord();

			for (int prof = 0; prof < customer.size(); prof++) {
				CustomerProfile cp = customer.get(prof);
				for (int attr = 0; attr < cp.getScoreAttributes().size(); attr++) {
					csvOutput.write(String.valueOf(prof + 1));
					csvOutput.write(cp.getScoreAttributes().get(attr).getName());
					
					/*int cont = 7;
					for (int val = 0; val < cp.getScoreAttributes().get(attr).getScoreValues().size(); val++) {
						csvOutput
								.write(String.valueOf(cp.getScoreAttributes().get(attr).getScoreValues().get(val) + 1));
						cont--;
					}
					while (cp.getScoreAttributes().get(attr).getScoreValues().size() == 6 && cont != 0) {
						csvOutput.write("0");
						cont--;
					}
					while (cp.getScoreAttributes().get(attr).getScoreValues().size() == 5 && cont != 0) {
						csvOutput.write("0");
						cont--;
					}
					while (cp.getScoreAttributes().get(attr).getScoreValues().size() == 4 && cont != 0) {
						csvOutput.write("0");
						cont--;
					}
					while (cp.getScoreAttributes().get(attr).getScoreValues().size() == 3 && cont != 0) {
						csvOutput.write("0");
						cont--;
					}*/

					
					int cont = 5;
					for (int val = 0; val < cp.getScoreAttributes().get(attr).getScoreValues().size(); val++) {
						csvOutput.write(String.valueOf(cp.getScoreAttributes().get(attr).getScoreValues().get(val) + 1));
						cont--;
					}
					while (cp.getScoreAttributes().get(attr).getScoreValues().size() == 4 && cont != 0) {
						csvOutput.write("0");
						cont--;
					}
					while (cp.getScoreAttributes().get(attr).getScoreValues().size() == 3 && cont != 0) {
						csvOutput.write("0");
						cont--;
					}
					csvOutput.endRecord();
				}
			}
			csvOutput.close();

		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "No se ha podido crear el CSV");
		}

	}

}