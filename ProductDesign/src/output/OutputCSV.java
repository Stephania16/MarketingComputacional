package output;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import com.csvreader.CsvWriter;

import general.CustomerProfile;

import java.util.ArrayList;

public class OutputCSV {
	
	public OutputCSV(){}
	
	public void WriteCSV(ArrayList<CustomerProfile> customer, String archivo) throws FileNotFoundException, IOException  {
         
       // String outputFile = "Cluster.csv";
        boolean alreadyExists = new File(archivo).exists();
         
        if(alreadyExists){
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
            csvOutput.endRecord();
 
            for(int prof = 0; prof < customer.size(); prof++){
                CustomerProfile cp = customer.get(prof);
                for(int attr = 0; attr < cp.getScoreAttributes().size(); attr++){
                	csvOutput.write(String.valueOf(prof + 1));
                	csvOutput.write(cp.getScoreAttributes().get(attr).getName());
                	int cont = 5;
                	for(int val = 0; val < cp.getScoreAttributes().get(attr).getScoreValues().size(); val++){
                			csvOutput.write(String.valueOf(cp.getScoreAttributes().get(attr).getScoreValues().get(val) + 1));   
                			cont--;
	                }
                	while(cp.getScoreAttributes().get(attr).getScoreValues().size() == 4 && cont!= 0){
        				csvOutput.write("0"); 
                		cont--;
                	}
                	while(cp.getScoreAttributes().get(attr).getScoreValues().size() == 3 && cont!= 0){
            			csvOutput.write("0"); 
                		cont--;
                	}
                	
                	csvOutput.endRecord();  
                }
            }
            csvOutput.close();
 
        } catch (IOException e) {
            e.printStackTrace();
        }
 
	}
  
}


/*
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
		  ga.A�adirProducer(attr, valorProd, posProducer);
		  ga.A�adirValorAtributo(attr, valorProd, posProducer);
		  ga.A�adirCustomer(attr, punt0, posCustomer);
		  ga.A�adirValoracion(attr, punt0, posCustomer);
		  ga.A�adirValoracion(attr, punt1, posCustomer);
		  ga.A�adirValoracion(attr, punt2, posCustomer);
		  ga.A�adirValoracion(attr, punt3, posCustomer);
     }
	// perform program logic here
	System.out.println(atributoName + ":" + min + " " + max);
	System.out.println(productorName + ":" + posProd + " " + atributoName + " " + min + " " + max + " " + valor);
	System.out.println(perfilName + ":" + posPerfil + " " + atributoName + " " + min + " " + max 
			          + " " + puntuacion0 + " " + puntuacion1 + " " + puntuacion2 + " " + puntuacion3);  
  } 			

archivo_csv.close();
*/