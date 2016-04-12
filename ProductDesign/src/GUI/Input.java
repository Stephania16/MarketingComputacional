package GUI;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.csvreader.CsvReader;

import Comunes.Attribute;
import genetic.GeneticAlgorithm;
import minimax.Minimax;

public class Input {
	static GeneticAlgorithm ga = new GeneticAlgorithm();
	static Minimax minimax = new Minimax();

	public Input(){}
	
	/*public static void main(String[] args)  {
		try {
			
			CsvReader archivo_csv = new CsvReader("archivo_csv.csv", ',');
		
			archivo_csv.readHeaders();

			while (archivo_csv.readRecord())
			{
				String atributoName = archivo_csv.get("atributoName");
				String min= archivo_csv.get("min");
				String max = archivo_csv.get("max");
				String productorName = archivo_csv.get("productorName");
				String posProd = archivo_csv.get("posProd");
				String valor = archivo_csv.get("valor");
				String perfilName = archivo_csv.get("perfilName");
				String posPerfil = archivo_csv.get("posPerfil");
				String puntuacion0 = archivo_csv.get("puntuacion0");
				String puntuacion1 = archivo_csv.get("puntuacion1");
				String puntuacion2 = archivo_csv.get("puntuacion2");
				String puntuacion3 = archivo_csv.get("puntuacion3");
				
				int minimo = Integer.parseInt(min);
				int maximo = Integer.parseInt(max);
				int valorProd = Integer.parseInt(valor);
				int posProducer = Integer.parseInt(posProd);
				int posCustomer = Integer.parseInt(posPerfil);
				int punt0 = Integer.parseInt(puntuacion0);
				int punt1 = Integer.parseInt(puntuacion1);
				int punt2 = Integer.parseInt(puntuacion2);
				int punt3 = Integer.parseInt(puntuacion3);
				Attribute attr = new Attribute(atributoName, minimo,maximo);
				ga.getTotalAttributes().add(attr);
				if(ga.isElement(ga.getTotalAttributes(),attr)){
					  ga.AñadirProducer(attr, valorProd, posProducer);
					  ga.AñadirValorAtributo(attr, valorProd, posProducer);
					  ga.AñadirCustomer(attr, punt0, posCustomer);
					  ga.AñadirValoracion(attr, punt0, posCustomer);
					  ga.AñadirValoracion(attr, punt1, posCustomer);
					  ga.AñadirValoracion(attr, punt2, posCustomer);
					  ga.AñadirValoracion(attr, punt3, posCustomer);
			     }
				// perform program logic here
				System.out.println(atributoName + ":" + min + " " + max);
				System.out.println(productorName + ":" + posProd + " " + atributoName + " " + min + " " + max + " " + valor);
				System.out.println(perfilName + ":" + posPerfil + " " + atributoName + " " + min + " " + max 
						          + " " + puntuacion0 + " " + puntuacion1 + " " + puntuacion2 + " " + puntuacion3);  
			  } 			
	
			archivo_csv.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	     
	}*/
  
}
