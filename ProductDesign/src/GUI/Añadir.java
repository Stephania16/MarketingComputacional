package GUI;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import Comunes.Attribute;
import Comunes.CustomerProfile;
import Comunes.Producer;
import Comunes.Product;

public class Añadir {
	 private static ArrayList<CustomerProfile> CustomerProfiles = new ArrayList<>();
	 private static ArrayList<Attribute> TotalAttributes = new ArrayList<>();
	 private static ArrayList<Producer> Producers = new ArrayList<>();
	 private static boolean generarDatosEntrada = false;
	 private static int num_attr = 70;
	 private static int num_prod = 10;
	 private static int num_prof = 16;
	 private static int num_prof_num = 200;

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

	/** Add valoration desde gui**/
	 public void AñadirValorAtributo(Attribute attr, int punt, int posProducer) {

		 int pos = getIndexHash(TotalAttributes,attr);
		 Producers.get(posProducer).getProduct().getAttributeValue().put(TotalAttributes.get(pos), punt);
	 }

	private int getIndexHash(ArrayList<Attribute> totalAttributes, Attribute attr) {
		for(int i = 0; i < totalAttributes.size(); i++)
		{
			if(totalAttributes.get(i).equals(attr)) return i;
		}
		return -1;
	}

	/**Generating the producers desde la gui**/
	 public void AñadirProducer(Attribute attr,int punt, int posProd){
		 	if(Producers.size() > posProd)
			{
				ArrayList<Boolean> availablevalues = new ArrayList<>();
		        for (int j = 0; j < attr.getMAX(); j++) {
		                availablevalues.add(true);
		        }
		        attr.setAvailableValues(availablevalues);
		        Producers.get(posProd).getAvailableAttribute().add(attr);
		        Producers.get(posProd).getProduct().getAttributeValue().put(attr, punt);
			}
		 	else 
		 	{
			 	Producer new_producer = new Producer();
				new_producer.setName("Productor " + posProd);
				
				ArrayList<Boolean> availablevalues = new ArrayList<>();
		        for (int j = 0; j < attr.getMAX(); j++) {
		                availablevalues.add(true);
		        }
		        attr.setAvailableValues(availablevalues);
				ArrayList<Attribute> availableAttr = new ArrayList<>();
				availableAttr.add(attr);
	            new_producer.setAvailableAttribute(availableAttr);
	            
	            HashMap<Attribute,Integer> values = new HashMap<>();
	            new_producer.setProduct(new Product(values));
	            Producers.add(new_producer);
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
	 public void AñadirCustomer(Attribute attr,int punt, int posCustomer){
		if(CustomerProfiles.isEmpty())
		{
			ArrayList<Attribute> attrs = new ArrayList<>();
			ArrayList<Integer> scoreValues = new ArrayList<>();
			attr.setScoreValues(scoreValues);
			attrs.add(attr);
			CustomerProfiles.add(new CustomerProfile(attrs));
		}
		else if(CustomerProfiles.size() > posCustomer)
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
	
	 public boolean isElement(ArrayList<Attribute> attr, Attribute at)
     {
	    	for(int i = 0; i < attr.size(); i++)
	    	{
	    		if(attr.get(i).equals(at)) return true;
	    	}
	    	return false;
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
						  String atrib = in.next();
						  int min = in.nextInt();
						  max = in.nextInt();
						  Attribute attr = new Attribute(atrib,min,max);
						  if(isElement(TotalAttributes,attr)){
								  int valor = in.nextInt();
								  AñadirProducer(attr, valor, posProducer);
								  if(posProducer > posProducerAux){ 
									  AñadirValorAtributo(attr, valor, posProducer);
								  } 
								  if(!in.hasNextInt()){
									  AñadirValorAtributo(attr, valor, posProducer);
								  }
						  } 
							  productor = in.next();
						  posProducerAux = posProducer;
					   } while (!(productor.equals("@perfil")));

					  while (in.hasNext()) {		
						  String perfil = in.next();
						  int posCustomer = in.nextInt();
						  String atrib = in.next();
						  int min = in.nextInt();
						  max = in.nextInt();
						  Attribute attr = new Attribute(atrib,min,max);
						  
						  if(isElement(TotalAttributes,attr)){
							  int puntuacion = in.nextInt();
							  int cont = 1;
							  while (in.hasNextInt()  && attr.getMAX() > puntuacion) {
								  AñadirCustomer(attr, puntuacion, posCustomer);
									  
								  while(cont < attr.getMAX()) {
									  AñadirValoracion(attr, puntuacion, posCustomer);
								  	  puntuacion = in.nextInt();
								  	  cont++;
								  }
								  if(!in.hasNextInt()){
									  AñadirValoracion(attr, puntuacion, posCustomer);
							      }			
							  }
						  }					  
					  }
				   }
			   } 
			  catch (FileNotFoundException e) {
				  System.out.println("Error abriendo el fichero " + archivo);
			   } 
			  finally {
				   if (in!=null){
				   in.close();
				   }
			  } // try
		    }
 

}
