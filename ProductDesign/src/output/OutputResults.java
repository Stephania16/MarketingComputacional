package output;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.swing.JTextArea;

/**
 * Clase que contiene el método que genera un txt para almacenar las ejecuciones
 */
public class OutputResults {
	public OutputResults() {
	}

	/**
	 * Método que crea un fichero txt que guarda los datos estadísticos de cada
	 * ejecución
	 */
	public void output(JTextArea jtA, String algoritmo) throws IOException {
		File fichero = new File("Salida.txt");
		FileWriter writer = new FileWriter(fichero, true);
		PrintWriter salida = new PrintWriter(writer);

		salida.println(algoritmo);
		salida.println(jtA.getText());
		salida.println();
		salida.close();
	}

}
