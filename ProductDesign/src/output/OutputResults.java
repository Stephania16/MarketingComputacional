package output;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.swing.JTextArea;

public class OutputResults {
	public OutputResults(){}
	 
	public void output(JTextArea jtA, String algoritmo) throws IOException{
		File fichero = new File("Salida.txt");
		FileWriter writer = new FileWriter(fichero, true);
		PrintWriter salida = new PrintWriter(writer);
		
		salida.println(algoritmo);
		salida.println(jtA.getText());
		salida.println();
		salida.close();
	}

}
