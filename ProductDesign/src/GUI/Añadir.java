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

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

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
	private static int num_attr = 10;//70
	private static int num_prod = 2; //10
	private static int num_prof = 16;
	private static int num_prof_num = 200;
	static int RESP_PER_GROUP = 20; /*
											 * * We divide the respondents of
											 * each
											 * 
											 * public Añadir(){ } /** Generating
											 * the input data
											 *
											 * @throws Exception
											 */

	public void setnum(int size) {
		num_prof_num = size;
	}

	public int getnum() {
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
		for (int i = 0; i < CustomerProfiles.size(); i++) {
			CustomerProfiles.get(i).setNumberCustomers(num_prof_num);
		}
	}

	public void setTotalAttributes(Attribute attr) {
		TotalAttributes.add(attr);
	}

	public boolean isGenerarDatosEntrada() {
		return generarDatosEntrada;
	}

	public void setisGenerarDatosEntrada(boolean generargui) {
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

			for (int j = 0; j < numOfSubProfile; j++) { // We divide into
														// sub-profiles
				SubProfile subprofile = new SubProfile();
				subprofile.setName("Subperfil " + (j + 1));// + ", Perfil " +
															// (i+1));

				HashMap<Attribute, Integer> valuesChosen = new HashMap<>();
				for (int k = 0; k < TotalAttributes.size(); k++) // Each of the
																	// sub-profiles
																	// choose a
																	// value for
																	// each of
																	// the
																	// attributes
					valuesChosen.put(TotalAttributes.get(k),
							chooseValueForAttribute(CustomerProfiles.get(i).getScoreAttributes().get(k)));

				subprofile.setValueChosen(valuesChosen);
				subProfiles.add(subprofile);
			}
			CustomerProfiles.get(i).setSubProfiles(subProfiles);
		}
	}

	/**
	 * Given an index of a customer profile and the index of an attribute we
	 * choose a value for that attribute of the sub-profile having into account
	 * the values of the poll
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

	/** Add valoration desde gui **/
	public void AñadirValorAtributo(Attribute attr, int punt, int posProducer, int posProduct) {
		ArrayList<Boolean> avaiableValues = new ArrayList<Boolean>();
		for (int j = 0; j < attr.getMAX(); j++)
			avaiableValues.add(true);
		attr.setAvailableValues(avaiableValues);
		
		if(!isElement(Producers.get(posProducer).getAvailableAttribute(), attr))
			Producers.get(posProducer).getAvailableAttribute().add(attr);

		int pos = getIndexOf(TotalAttributes, attr);
		Producers.get(posProducer).getProduct().getAttributeValue().put(TotalAttributes.get(pos), punt);
		Producers.get(posProducer).getProducts().get(posProduct).getAttributeValue().put(TotalAttributes.get(pos), punt);
		
	}

	public Attribute getAttribute(ArrayList<Attribute> totalAttributes, String name) {
		for (int i = 0; i < totalAttributes.size(); i++) {
			if (totalAttributes.get(i).getName().equals(name))
				return totalAttributes.get(i);
		}
		return null;
	}

	/** Generating the producers desde la gui **/
	public void AñadirProducer(int posProd, int posProduct) {

		if (Producers.size() <= posProd) {
			Producer producer = new Producer();
			producer.setName("Productor " + posProd);

			ArrayList<Attribute> availableAttr = new ArrayList<Attribute>();
			producer.setAvailableAttribute(availableAttr);

			HashMap<Attribute, Integer> values = new HashMap<>();
			Product product = new Product();
			product.setAttributeValue(values);
			product.setPrice((int)( Math.random() * 400) + 100);
			producer.setProduct(product);
			
			Producers.add(producer);
		}
	}
	
	public void products(int posProd, int posProduct){
		if (Producers.get(posProd).getProducts().size() <= posProduct) {
			HashMap<Attribute, Integer> values = new HashMap<>();
			Product product = new Product();
			product.setAttributeValue(values);
			product.setPrice((int)( Math.random() * 400) + 100);			
			ArrayList<Product> products = new ArrayList<>();
			products.add(product);
			
			Producers.get(posProd).getProducts().add(product);
		}
		
	}

	/** Add valoration desde gui **/
	public void AñadirValoracion(Attribute attr, int punt, int posCustomer) {

		int pos = getIndexOf(CustomerProfiles.get(posCustomer).getScoreAttributes(), attr);
		CustomerProfiles.get(posCustomer).getScoreAttributes().get(pos).getScoreValues().add(punt);
	}

	/**
	 * Generating the customerprofile desde la gui
	 */
	public void AñadirCustomer(Attribute attr, int posCustomer) {
		if (CustomerProfiles.size() > posCustomer) {
			ArrayList<Integer> scoreValues = new ArrayList<>();
			attr.setScoreValues(scoreValues);
			CustomerProfiles.get(posCustomer).getScoreAttributes().add(attr);
		} else {
			ArrayList<Attribute> attrs = new ArrayList<>();
			ArrayList<Integer> scoreValues = new ArrayList<>();
			attr.setScoreValues(scoreValues);
			attrs.add(attr);
			CustomerProfiles.add(new CustomerProfile(attrs));
		}
	}

	private int getIndexOf(ArrayList<Attribute> listaAttr, Attribute attr) {
		for (int i = 0; i < listaAttr.size(); i++) {
			if (listaAttr.get(i).equals(attr))
				return i;
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

	public boolean isElement(ArrayList<Attribute> attr, Attribute atributo) {
		for (int i = 0; i < attr.size(); i++) {
			if (attr.get(i).equals(atributo))
				return true;
		}
		return false;

	}

	public void addTxt(String archivo) throws FileNotFoundException, IOException {
		File fichero = new File(archivo);
		FileWriter writer = new FileWriter(fichero);
		PrintWriter salida = new PrintWriter(writer);
		salida.println("@atributo");
		for (int i = 0; i < TotalAttributes.size(); i++) {
			salida.println(TotalAttributes.get(i).getName() + " " + TotalAttributes.get(i).getMIN() + " "
					+ TotalAttributes.get(i).getMAX());
		}
		salida.println("@productor");
		for (int i = 0; i < Producers.size(); i++) {
			Producer p = Producers.get(i);
			for(int k = 0; k < p.getProducts().size(); k++){
				for (int j = 0; j < p.getAvailableAttribute().size(); j++) {
						salida.print("productor " + i + " ");
						salida.print("producto " + k + " ");
						salida.println(p.getAvailableAttribute().get(j).getName() + " "
						+ p.getProducts().get(k).getAttributeValue().get(TotalAttributes.get(j)));
			
				}
			}
		}
		salida.println("@perfil");
		for (int i = 0; i < CustomerProfiles.size(); i++) {
			CustomerProfile cp = CustomerProfiles.get(i);
			for (int j = 0; j < cp.getScoreAttributes().size(); j++) {
				salida.print("perfil " + i + " ");
				salida.print(cp.getScoreAttributes().get(j).getName() + " ");
				for (int z = 0; z < cp.getScoreAttributes().get(j).getScoreValues().size(); z++) {
					if (z == cp.getScoreAttributes().get(j).getScoreValues().size() - 1)
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
			if (in.next().equals("@atributo")) {
				String atributo = in.next();
				do {
					int min = in.nextInt();
					max = in.nextInt();
					Attribute attr = new Attribute(atributo, min, max);
					TotalAttributes.add(attr);
					atributo = in.next();
				} while (!(atributo.equals("@productor")));

				String productor = in.next();
				int posProducerAux = -1;
				do {
					int posProducer = in.nextInt();
					String producto = in.next();
					int posProduct = in.nextInt();
					String atrib_prod = in.next();
					int valor = in.nextInt();
					Attribute attr = getAttribute(TotalAttributes, atrib_prod);
					Attribute atribute = new Attribute(attr.getName(), attr.getMIN(), attr.getMAX());
					if (isElement(TotalAttributes, atribute)) {
						AñadirProducer(posProducer, posProduct);
						products(posProducer, posProduct);
						AñadirValorAtributo(atribute, valor, posProducer, posProduct);
					}
					productor = in.next();
					posProducerAux = posProducer;
				} while (!(productor.equals("@perfil")));

				while (in.hasNext()) {
					String perfil = in.next();
					int posCustomer = in.nextInt();
					String atrib_perfil = in.next();
					Attribute attr = getAttribute(TotalAttributes, atrib_perfil);
					Attribute atribute = new Attribute(attr.getName(), attr.getMIN(), attr.getMAX());
					if (isElement(TotalAttributes, atribute)) {
						int puntuacion = in.nextInt();
						int cont = 1;
						while (in.hasNextInt() && attr.getMAX() > puntuacion) {
							AñadirCustomer(atribute, posCustomer);

							while (cont < attr.getMAX()) {
								AñadirValoracion(atribute, puntuacion, posCustomer);
								puntuacion = in.nextInt();
								cont++;
							}
							if (!in.hasNextInt()) {
								AñadirValoracion(atribute, puntuacion, posCustomer);
							}
						}
					}
				}
			}
		} /*catch (FileNotFoundException e) {
			System.out.println("Error abriendo el fichero " + archivo);
		}*/ finally {
			if (in != null) {
				in.close();
			}
		} // try
	}

	public void inputXML(String archivo_xml) {

	    try {

		File fXmlFile = new File(archivo_xml); //"nuevo.xml"
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(fXmlFile);
				
		doc.getDocumentElement().normalize();

		//System.out.println("Datos :" + doc.getDocumentElement().getNodeName());
				
		NodeList nListAttr = doc.getElementsByTagName("attr");
				
		//System.out.println("----------------------------");

		for (int atributo = 0; atributo < nListAttr.getLength(); atributo++) {

			Node nAttr = nListAttr.item(atributo);
					
			//System.out.println("\nCurrent Element :" + nAttr.getNodeName());
					
			if (nAttr.getNodeType() == Node.ELEMENT_NODE) {

				Element eElement = (Element) nAttr;

				//System.out.println("attr id : " + eElement.getAttribute("id"));
				String name = eElement.getElementsByTagName("name").item(0).getTextContent();
				//System.out.println("name : " + name);
				int min = Integer.parseInt(eElement.getElementsByTagName("min").item(0).getTextContent());
				//System.out.println("min : " + min);
				int max = Integer.parseInt(eElement.getElementsByTagName("max").item(0).getTextContent());
				//System.out.println("max : " + max);
				
				Attribute attr = new Attribute(name, min,max);
				TotalAttributes.add(attr);
			}
		}
		
		NodeList nListProd = doc.getElementsByTagName("prod");
		for (int productor = 0; productor < nListProd.getLength(); productor++) {

			Node nProd = nListProd.item(productor);
			//System.out.println("\nProducers :" + nProd.getNodeName());
			
			if (nProd.getNodeType() == Node.ELEMENT_NODE) {

				Element eElementProd = (Element) nProd;
				
				//System.out.println("prod id : " + eElementProd.getAttribute("id"));
				String name = eElementProd.getElementsByTagName("name").item(0).getTextContent();
				//System.out.println("name : " + name);
				
				int indice = Integer.parseInt(eElementProd.getElementsByTagName("indice").item(0).getTextContent());
				//System.out.println("indice : " + indice);
				
				String name_product = eElementProd.getElementsByTagName("name_product").item(0).getTextContent();
				int pos_product = Integer.parseInt(eElementProd.getElementsByTagName("pos_product").item(0).getTextContent());
				
				String nameAttr = eElementProd.getElementsByTagName("nameAtributo").item(0).getTextContent();
				//System.out.println("name atributo : " + nameAttr);
				
				int valor = Integer.parseInt(eElementProd.getElementsByTagName("valor").item(0).getTextContent());
				//System.out.println("valor : " + valor);
				
				Attribute attr = getAttribute(TotalAttributes, nameAttr);
				Attribute atribute = new Attribute(attr.getName(), attr.getMIN(), attr.getMAX());
				if (isElement(TotalAttributes, atribute)) {
					AñadirProducer(indice,pos_product);
					products(indice, pos_product);
					AñadirValorAtributo(atribute, valor, indice,pos_product);
				}
			}

		}
		
		NodeList nListProf = doc.getElementsByTagName("prof");
		for (int perfil = 0; perfil < nListProf.getLength(); perfil++) {

			Node nProf = nListProf.item(perfil);
			//System.out.println("\nCustomer profile :" + nProf.getNodeName());
			
			if (nProf.getNodeType() == Node.ELEMENT_NODE) {

				Element eElementProf = (Element) nProf;
				
				//System.out.println("prof id : " + eElementProf.getAttribute("id"));
				String name = eElementProf.getElementsByTagName("name").item(0).getTextContent();
				//System.out.println("name : " + name);
				
				int indice = Integer.parseInt(eElementProf.getElementsByTagName("indice").item(0).getTextContent());
				//System.out.println("indice : " + indice);
				
				String nameAttr = eElementProf.getElementsByTagName("nameAtributo").item(0).getTextContent();
				//System.out.println("name atributo : " + nameAttr);
				
				Attribute attr = getAttribute(TotalAttributes, nameAttr);
				Attribute atribute = new Attribute(attr.getName(), attr.getMIN(), attr.getMAX());
				if (isElement(TotalAttributes, atribute)) {
					int cont = 1;
					AñadirCustomer(atribute, indice);
					while (cont < attr.getMAX()) {
						for(int i = 0; i < eElementProf.getElementsByTagName("valoracion").getLength(); i++){
							int valoracion = Integer.parseInt(eElementProf.getElementsByTagName("valoracion").item(i).getTextContent());
							//System.out.println("valoracion : " + valoracion);
							AñadirValoracion(atribute, valoracion, indice);
							cont++;
						}
					}		
				}
			}	
		}
	    } catch (SAXException ex) {
	        System.out.println("ERROR: El formato XML del fichero no es correcto\n"+ex.getMessage());
	        ex.printStackTrace();
	    } catch (IOException ex) {
	        System.out.println("ERROR: Se ha producido un error el leer el fichero\n"+ex.getMessage());
	        ex.printStackTrace();
	    } catch (ParserConfigurationException ex) {
	        System.out.println("ERROR: No se ha podido crear el generador de documentos XML\n"+ex.getMessage());
	        ex.printStackTrace();
	    }
	  }
	
	public void writeXML(String archivo){
		try {
		    DocumentBuilderFactory fábricaCreadorDocumento = DocumentBuilderFactory.newInstance();
		    DocumentBuilder creadorDocumento = fábricaCreadorDocumento.newDocumentBuilder();
		    //Crear un nuevo documento XML
		    Document documento = creadorDocumento.newDocument();
		 
		    //Crear el nodo raíz y colgarlo del documento
		    Element elementoRaiz = documento.createElement("listas");
		    documento.appendChild(elementoRaiz);
		 
		    for(int attr = 0; attr < getTotalAttributes().size(); attr++){
			    //Crear un elemento EMPLEADO colgando de EMPLEADOS
			    Element elementoAtributo = documento.createElement("attr");
			    elementoAtributo.setAttribute("id", "@atributo");
			    elementoRaiz.appendChild(elementoAtributo);
			 
			    //Crear cada uno de los textos de datos del empleado
			    Element elementoNombre = documento.createElement("name");
			    elementoAtributo.appendChild(elementoNombre);
			    Text textoNombre = documento.createTextNode(getTotalAttributes().get(attr).getName());
			    elementoNombre.appendChild(textoNombre);
			 
			    Element elementoMin = documento.createElement("min");
			    elementoAtributo.appendChild(elementoMin);
			    String min = Integer.toString(getTotalAttributes().get(attr).getMIN());
			    Text textoMin = documento.createTextNode(min);
			    elementoMin.appendChild(textoMin);
			    
			    String max = Integer.toString(getTotalAttributes().get(attr).getMAX());
			    Element elementoMax = documento.createElement("max");
			    elementoAtributo.appendChild(elementoMax);
			    Text textoMax = documento.createTextNode(max);
			    elementoMax.appendChild(textoMax);
		    }
		    
		    for(int prod = 0; prod < getProducers().size(); prod++){
			    Producer p = getProducers().get(prod);
			    for(int k = 0; k < p.getProducts().size(); k++){
				    for (int j = 0; j < p.getAvailableAttribute().size(); j++) {
				    	//Crear un elemento EMPLEADO colgando de EMPLEADOS
				    	Element elementoProd = documento.createElement("prod");
				    	elementoProd.setAttribute("id", "@productor");
				    	elementoRaiz.appendChild(elementoProd);
				    	//Crear cada uno de los textos de datos del empleado
					    Element elementoNombre = documento.createElement("name");
					    elementoProd.appendChild(elementoNombre);
					    Text textoNombre = documento.createTextNode("productor");
					    elementoNombre.appendChild(textoNombre);
					 
					    Element elementoIndice = documento.createElement("indice");
					    elementoProd.appendChild(elementoIndice);
					    String indice = Integer.toString(prod);
					    Text textoIndice = documento.createTextNode(indice);
					    elementoIndice.appendChild(textoIndice);
					    
					    Element elementoNameProd = documento.createElement("name_product");
					    elementoProd.appendChild(elementoNameProd);
					    Text textoNameProd = documento.createTextNode("producto");
					    elementoNameProd.appendChild(textoNameProd);
					    
					    Element elementoPosPro = documento.createElement("pos_product");
					    elementoProd.appendChild(elementoPosPro);
					    String posP = Integer.toString(k);
					    Text textoProd = documento.createTextNode(posP);
					    elementoPosPro.appendChild(textoProd);
					    
					    Element elementoNameAttr = documento.createElement("nameAtributo");
					    elementoProd.appendChild(elementoNameAttr);
					    Text textoNameAttr = documento.createTextNode(p.getAvailableAttribute().get(j).getName());
					    elementoNameAttr.appendChild(textoNameAttr);
					    
					    Element valor = documento.createElement("valor");
					    elementoProd.appendChild(valor);
					    String valores = Integer.toString(p.getProducts().get(k).getAttributeValue().get(getTotalAttributes().get(j)));
					    Text textvalue = documento.createTextNode(valores);
					    valor.appendChild(textvalue);
				    }
			    }
		    }
		    
		    for(int prof = 0; prof < getCustomerProfiles().size(); prof++){
			    CustomerProfile cp = getCustomerProfiles().get(prof);
			    for (int j = 0; j < cp.getScoreAttributes().size(); j++) {
			    	 //Crear un elemento EMPLEADO colgando de EMPLEADOS
			    	Element elementoProf = documento.createElement("prof");
			    	elementoProf.setAttribute("id", "@perfil");
			    	elementoRaiz.appendChild(elementoProf);
				  //Crear cada uno de los textos de datos del empleado
				    Element elementoNombre = documento.createElement("name");
				    elementoProf.appendChild(elementoNombre);
				    Text textoNombre = documento.createTextNode("perfil");
				    elementoNombre.appendChild(textoNombre);
				    
				    Element elementoIndice = documento.createElement("indice");
				    elementoProf.appendChild(elementoIndice);
				    String indice = Integer.toString(prof);
				    Text textoIndice = documento.createTextNode(indice);
				    elementoIndice.appendChild(textoIndice);
				    
				    
				    Element elementoNameAttr = documento.createElement("nameAtributo");
				    elementoProf.appendChild(elementoNameAttr);
				    Text textoNameAttr = documento.createTextNode(cp.getScoreAttributes().get(j).getName());
				    elementoNameAttr.appendChild(textoNameAttr);
				    for (int z = 0; z < cp.getScoreAttributes().get(j).getScoreValues().size(); z++) {
				    	Element valorac = documento.createElement("valoracion");
					    elementoProf.appendChild(valorac);
					    String valoracion = Integer.toString(cp.getScoreAttributes().get(j).getScoreValues().get(z));
					    Text textvalue = documento.createTextNode(valoracion);
					    valorac.appendChild(textvalue);
				    }   
			    }
		    }
		    //Generar el tranformador para obtener el documento XML en un fichero
		    TransformerFactory fábricaTransformador = TransformerFactory.newInstance();
		    Transformer transformador = fábricaTransformador.newTransformer();
		    //Insertar saltos de línea al final de cada línea
		    transformador.setOutputProperty(OutputKeys.INDENT, "yes");
		    //Añadir 3 espacios delante, en función del nivel de cada nodo
		   // transformador.setOutputProperty(OutputPropertiesFactory.S_KEY_INDENT_AMOUNT, );
		    Source origen = new DOMSource(documento);
		    Result destino = new StreamResult(); //archivo
		    destino.setSystemId(archivo);
		    transformador.transform(origen, destino);
		 
		} catch (ParserConfigurationException ex) {
		    System.out.println("ERROR: No se ha podido crear el generador de documentos XML\n"+ex.getMessage());
		    ex.printStackTrace();
		} catch (TransformerConfigurationException ex) {
		    System.out.println("ERROR: No se ha podido crear el transformador del documento XML\n"+ex.getMessage());
		    ex.printStackTrace();
		} catch (TransformerException ex) {
		    System.out.println("ERROR: No se ha podido crear la salida del documento XML\n"+ex.getMessage());
		    ex.printStackTrace();
		}
	}
	
}
