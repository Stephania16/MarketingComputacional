package GUI;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import Comunes.Attribute;
import Comunes.CustomerProfile;
import Comunes.Producer;
import Comunes.Product;
import genetic.SubProfile;

public class Añadir {
	 private static ArrayList<CustomerProfile> CustomerProfiles = new ArrayList<>();
	 private static ArrayList<Attribute> TotalAttributes = new ArrayList<>();
	 private static ArrayList<Producer> Producers = new ArrayList<>();
	 private static boolean generarDatosEntrada = false;
	 private static int num_attr = 70;
	 private static int num_prod = 10;
	 private static int num_prof = 16;
	 private static int num_prof_num = 200;
	 static final int RESP_PER_GROUP = 20; /* * We divide the respondents of each

	 public Añadir(){
	 }
	    /**
	     * Generating the input data
	     *
	     * @throws Exception
	     */

	 public void setnum(int size)
		{
			num_prof_num = size;
		}
		
	public int getnum()
	{
		return num_prof_num;
	}

    public int getnum_attr() {
    	return num_attr;
    }
    
    public void setnum_attr(int size) {
    	num_attr = size;
    }
	
    public int getnum_prod() {
    	return num_prod;
    }
    public void setnum_prod(int size) {
    	num_prod = size;
    }
    public int getnum_prof() {
    	return num_prof;
    }
    
    public void setnum_prof(int size) {
    	num_prof = size;
    }
 
    public void setProfiles() {
		for(int i = 0; i < CustomerProfiles.size(); i++)
		{
			CustomerProfiles.get(i).setNumberCustomers(num_prof_num);
		}
		}
	 public void setTotalAttributes(Attribute attr) {
			TotalAttributes.add(attr);			
		}
	 
	 public boolean isGenerarDatosEntrada() {
			return generarDatosEntrada;
		}
	 
	 public void setisGenerarDatosEntrada(boolean generargui){
		 generarDatosEntrada = generargui;
	 }
	 
	  /**
	     * Dividing the customer profiles into sub-profiles
	     *
	     * @throws Exception
	     */
	    public void divideCustomerProfile() throws Exception {


	        int numOfSubProfile;
	        for (int i = 0; i < CustomerProfiles.size(); i++) {
	            ArrayList<SubProfile> subProfiles = new ArrayList<>();
	            numOfSubProfile = CustomerProfiles.get(i).getNumberCustomers() / RESP_PER_GROUP;

	            if ((CustomerProfiles.get(i).getNumberCustomers() % RESP_PER_GROUP) != 0)
	                numOfSubProfile++;

	            for (int j = 0; j < numOfSubProfile; j++) { //We divide into sub-profiles
	                SubProfile subprofile = new SubProfile();
	                subprofile.setName("Subperfil " + (j + 1));// + ", Perfil " + (i+1));

	                HashMap<Attribute, Integer> valuesChosen = new HashMap<>();
	                for (int k = 0; k < TotalAttributes.size(); k++) //Each of the sub-profiles choose a value for each of the attributes
	                    valuesChosen.put(TotalAttributes.get(k), chooseValueForAttribute(CustomerProfiles.get(i).getScoreAttributes().get(k)));

	                subprofile.setValueChosen(valuesChosen);
	                subProfiles.add(subprofile);
	            }
	            CustomerProfiles.get(i).setSubProfiles(subProfiles);
	        }
	    }


	    /**
	     * Given an index of a customer profile and the index of an attribute we choose a value
	     * for that attribute of the sub-profile having into account the values of the poll
	     */
	    private Integer chooseValueForAttribute(Attribute attribute) throws Exception {

	        int total = 0;
	        int accumulated = 0;
	        boolean found = false;

	        for (int i = 0; i < attribute.getScoreValues().size(); i++)
	            total += attribute.getScoreValues().get(i);

	        int rndVal = (int) (total * Math.random());

	        int value = 0;
	        while (!found) {
	            accumulated += attribute.getScoreValues().get(value);
	            if (rndVal <= accumulated)
	                found = true;
	            else
	                value++;
	            ;

	            if (value >= attribute.getScoreValues().size())
	                throw new Exception("Error 1 in chooseValueForAttribute() method: Value not found");

	        }

	        if (value >= attribute.getScoreValues().size())
	            throw new Exception("Error 2 in chooseValueForAttribute() method: Value not found");

	        return value;
	    }
	 

	/** Add valoration desde gui**/
	 public void AñadirValorAtributo(Attribute attr, int punt, int posProducer) {
		 ArrayList<Boolean> avaiableValues = new ArrayList<Boolean>();
		 for (int j = 0; j < attr.getMAX(); j++)
			 avaiableValues.add(true);
	     attr.setAvailableValues(avaiableValues);
	     
	     Producers.get(posProducer).getAvailableAttribute().add(attr);
	     
	     int pos = getIndexOf(TotalAttributes,attr);
	     Producers.get(posProducer).getProduct().getAttributeValue().put(TotalAttributes.get(pos), punt);
	 }

	public Attribute getAttribute(ArrayList<Attribute> totalAttributes, String name) {
		for(int i = 0; i < totalAttributes.size(); i++)
		{
			if(totalAttributes.get(i).getName().equals(name))
				return totalAttributes.get(i);
		}
		return null;
	}


	/**Generating the producers desde la gui**/
	public void AñadirProducer(int posProd){
		 
		 if(Producers.size() <= posProd){
			 Producer producer = new Producer();
			 producer.setName("Productor " + posProd);
			 
			 ArrayList<Attribute> availableAttr = new ArrayList<Attribute>();
	         producer.setAvailableAttribute(availableAttr);
	            
	         HashMap<Attribute,Integer> values = new HashMap<>();
	         producer.setProduct(new Product(values));
	         Producers.add(producer);
		 }
	}
	    
	 
	/** Add valoration desde gui**/
	 public void AñadirValoracion(Attribute attr, int punt, int posCustomer) {
		 
		     int pos = getIndexOf(CustomerProfiles.get(posCustomer).getScoreAttributes(), attr);
		     CustomerProfiles.get(posCustomer).getScoreAttributes().get(pos).getScoreValues().add(punt);    
	 }
	 
	 
	 /**
	     * Generating the customerprofile desde la gui
	     */
	 public void AñadirCustomer(Attribute attr,int posCustomer){
		if(CustomerProfiles.size() > posCustomer)
		{
			ArrayList<Integer> scoreValues = new ArrayList<>();
			attr.setScoreValues(scoreValues);
			CustomerProfiles.get(posCustomer).getScoreAttributes().add(attr);
		}
		else
		{	
			ArrayList<Attribute> attrs = new ArrayList<>();
			ArrayList<Integer> scoreValues = new ArrayList<>();
			attr.setScoreValues(scoreValues);
			attrs.add(attr);
			CustomerProfiles.add(new CustomerProfile(attrs));
		}
	 }
	 
	 private int getIndexOf(ArrayList<Attribute> listaAttr, Attribute attr){
		   for (int i = 0; i < listaAttr.size(); i++){
			   if(listaAttr.get(i).equals(attr)) return i;
		   } 
		   return -1;
	 }
	 
	public ArrayList<CustomerProfile> getCustomerProfiles() {
		return CustomerProfiles;
	}

	public void setCustomerProfiles(ArrayList<CustomerProfile> customerProfiles) {
		CustomerProfiles = customerProfiles;
	}

	public ArrayList<Attribute> getTotalAttributes() {
		return TotalAttributes;
	}

	public void setTotalAttributes(ArrayList<Attribute> totalAttributes) {
		TotalAttributes = totalAttributes;
	}

	public ArrayList<Producer> getProducers() {
		return Producers;
	}

	public void setProducers(ArrayList<Producer> producers) {
		Producers = producers;
	}
	
	 public boolean isElement(ArrayList<Attribute> attr, Attribute atributo)
     {
	    	for(int i = 0; i < attr.size(); i++)
	    	{
	    		if(attr.get(i).equals(atributo)) return true;
	    	}
	    	return false;
     
     }
	 
	 public void addTxt(String archivo)throws FileNotFoundException, IOException
	 {
		 File fichero = new File(archivo);
		 FileWriter writer = new FileWriter(fichero);
		 PrintWriter salida = new PrintWriter(writer);
		 salida.println("@atributo");
		 for(int i = 0; i < TotalAttributes.size(); i++){
			 salida.println(TotalAttributes.get(i).getName() + " " + TotalAttributes.get(i).getMIN() + " " + TotalAttributes.get(i).getMAX());
		 }
		 salida.println("@productor");
		 for (int i = 0; i < Producers.size(); i++) {
	            Producer p = Producers.get(i);
	            for (int j = 0; j < p.getAvailableAttribute().size(); j++) {
	            	salida.print("productor " + i + " ");
	            	salida.println(p.getAvailableAttribute().get(j).getName() + " " + p.getProduct().getAttributeValue().get(TotalAttributes.get(j)));
	    
	            }
	     }
		 salida.println("@perfil");
		 for (int i = 0; i < CustomerProfiles.size(); i++) {
	            CustomerProfile cp = CustomerProfiles.get(i);          
	            for (int j = 0; j < cp.getScoreAttributes().size(); j++) {
	            	salida.print("perfil " + i  + " ");
	            	salida.print(cp.getScoreAttributes().get(j).getName() + " ");
	            	for (int z = 0; z < cp.getScoreAttributes().get(j).getScoreValues().size(); z++) {
	            		if(z == cp.getScoreAttributes().get(j).getScoreValues().size() - 1)
	            			salida.println(cp.getScoreAttributes().get(j).getScoreValues().get(z));
	            		else
	            			salida.print(cp.getScoreAttributes().get(j).getScoreValues().get(z) + " ");
	            	}
	            }
	    	}
		 salida.println();
		 salida.close();
	 }
	 
		public void muestraContenido(String archivo) throws FileNotFoundException, IOException {
			  Scanner in = null;
			  int max = 0;
			  try {
				  // abre el fichero
				  in = new Scanner(new FileReader(archivo));
				  // lee el fichero palabra a palabra
				  if(in.next().equals("@atributo"))
				  {
					  String atributo = in.next();
					  do{
						  int min = in.nextInt();
							  max = in.nextInt();
						  Attribute attr = new Attribute(atributo,min,max);
						  TotalAttributes.add(attr);
						  atributo = in.next();
					   }while (!(atributo.equals("@productor")));
					  
					  String productor = in.next();
					  int posProducerAux = -1;
					  do{		
						  int posProducer = in.nextInt();
						  String atrib_prod = in.next();
						  int valor = in.nextInt();
						  Attribute attr = getAttribute(TotalAttributes,atrib_prod);
						  Attribute atribute = new Attribute(attr.getName(),attr.getMIN(),attr.getMAX());
						  if(isElement(TotalAttributes,atribute)){
								AñadirProducer(posProducer);
								AñadirValorAtributo(atribute, valor, posProducer);
							  }  
							  productor = in.next();
						  posProducerAux = posProducer;
					   } while (!(productor.equals("@perfil")));

					  while (in.hasNext()) {		
						  String perfil = in.next();
						  int posCustomer = in.nextInt();
						  String atrib_perfil = in.next(); 
						  Attribute attr = getAttribute(TotalAttributes,atrib_perfil);
						  Attribute atribute = new Attribute(attr.getName(),attr.getMIN(),attr.getMAX());
						  if(isElement(TotalAttributes,atribute)){
							  int puntuacion = in.nextInt();
							  int cont = 1;
							  while (in.hasNextInt()  && attr.getMAX() > puntuacion) {
								  AñadirCustomer(atribute, posCustomer);
								  
								  while(cont < attr.getMAX()) {
									  AñadirValoracion(atribute, puntuacion, posCustomer);
								  	  puntuacion = in.nextInt();
								  	  cont++;
								  }
								  if(!in.hasNextInt()){
									  AñadirValoracion(atribute, puntuacion, posCustomer);
							      }			
							  }
						  }					  
					  }
				   }
			   } 
			  catch (FileNotFoundException e) {
				  System.out.println("Error abriendo el fichero " + archivo);
				  //JOptionPane.showMessageDialog(tab4, e.getMessage());
			   } 
			  finally {
				   if (in!=null){
				   in.close();
				   }
			  } // try
		    }
 

}
