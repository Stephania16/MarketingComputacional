package GUI;

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

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import Comunes.CustomerProfile;
import Comunes.Producer;

public class InputXML {
	static A�adir a�adir = new A�adir();
/*	public static void main(String argv[]) {

		try {
		    DocumentBuilderFactory f�bricaCreadorDocumento = DocumentBuilderFactory.newInstance();
		    DocumentBuilder creadorDocumento = f�bricaCreadorDocumento.newDocumentBuilder();
		    //Crear un nuevo documento XML
		    Document documento = creadorDocumento.newDocument();
		 
		    //Crear el nodo ra�z y colgarlo del documento
		    Element elementoRaiz = documento.createElement("listas");
		    documento.appendChild(elementoRaiz);
		 
		    for(int attr = 0; attr < a�adir.getTotalAttributes().size(); attr++){
			    //Crear un elemento EMPLEADO colgando de EMPLEADOS
			    Element elementoEmpleado = documento.createElement("attr id = @atributo");
			    elementoRaiz.appendChild(elementoEmpleado);
			 
			    //Crear cada uno de los textos de datos del empleado
			    Element elementoNombre = documento.createElement("name");
			    elementoEmpleado.appendChild(elementoNombre);
			    Text textoNombre = documento.createTextNode(a�adir.getTotalAttributes().get(attr).getName());
			    elementoNombre.appendChild(textoNombre);
			 
			    Element elementoApellidos = documento.createElement("min");
			    elementoEmpleado.appendChild(elementoApellidos);
			    String min = Integer.toString(a�adir.getTotalAttributes().get(attr).getMIN());
			    Text textoApellidos = documento.createTextNode(min);
			    elementoApellidos.appendChild(textoApellidos);
			    
			    String max = Integer.toString(a�adir.getTotalAttributes().get(attr).getMAX());
			    Element elementoDNI = documento.createElement("max");
			    elementoEmpleado.appendChild(elementoDNI);
			    Text textoDNI = documento.createTextNode(max);
			    elementoDNI.appendChild(textoDNI);
		    }
		    
		    for(int prod = 0; prod < a�adir.getProducers().size(); prod++){
			    //Crear un elemento EMPLEADO colgando de EMPLEADOS
			    Element elementoEmpleado = documento.createElement("prod id = @productor");
			    elementoRaiz.appendChild(elementoEmpleado);
			 
			    Producer p = a�adir.getProducers().get(prod);
			    for (int j = 0; j < p.getAvailableAttribute().size(); j++) {
			    	//Crear cada uno de los textos de datos del empleado
				    Element elementoNombre = documento.createElement("name");
				    elementoEmpleado.appendChild(elementoNombre);
				    Text textoNombre = documento.createTextNode(a�adir.getProducers().get(prod).getName());
				    elementoNombre.appendChild(textoNombre);
				 
				    Element elementoApellidos = documento.createElement("indice");
				    elementoEmpleado.appendChild(elementoApellidos);
				    String indice = Integer.toString(prod);
				    Text textoApellidos = documento.createTextNode(indice);
				    elementoApellidos.appendChild(textoApellidos);
				    
				    Element elementoDNI = documento.createElement("nameAtributo");
				    elementoEmpleado.appendChild(elementoDNI);
				    Text textoDNI = documento.createTextNode(p.getAvailableAttribute().get(j).getName());
				    elementoDNI.appendChild(textoDNI);
				    
				    Element valor = documento.createElement("valor");
				    elementoEmpleado.appendChild(valor);
				    String value = Integer.toString(p.getProduct().getAttributeValue().get(a�adir.getTotalAttributes().get(j)));
				    Text textvalue = documento.createTextNode(value);
				    elementoDNI.appendChild(textvalue);
			    }
		    }
		    
		    for(int prof = 0; prof < a�adir.getCustomerProfiles().size(); prof++){
			    //Crear un elemento EMPLEADO colgando de EMPLEADOS
			    Element elementoEmpleado = documento.createElement("prof id = @perfil");
			    elementoRaiz.appendChild(elementoEmpleado);
			    CustomerProfile cp = a�adir.getCustomerProfiles().get(prof);
			    for (int j = 0; j < cp.getScoreAttributes().size(); j++) {
				  //Crear cada uno de los textos de datos del empleado
				    Element elementoNombre = documento.createElement("name");
				    elementoEmpleado.appendChild(elementoNombre);
				    Text textoNombre = documento.createTextNode("perfil");
				    elementoNombre.appendChild(textoNombre);
				    
				    Element elementoApellidos = documento.createElement("indice");
				    elementoEmpleado.appendChild(elementoApellidos);
				    String indice = Integer.toString(prof);
				    Text textoApellidos = documento.createTextNode(indice);
				    elementoApellidos.appendChild(textoApellidos);
				    
				    
				    Element elementoDNI = documento.createElement("nameAtributo");
				    elementoEmpleado.appendChild(elementoDNI);
				    Text textoDNI = documento.createTextNode(cp.getScoreAttributes().get(j).getName());
				    elementoDNI.appendChild(textoDNI);
				    for (int z = 0; z < cp.getScoreAttributes().get(j).getScoreValues().size(); z++) {
				    	Element valorac = documento.createElement("valoracion");
					    elementoEmpleado.appendChild(valorac);
					    String valoracion = Integer.toString(cp.getScoreAttributes().get(j).getScoreValues().get(z));
					    Text textvalue = documento.createTextNode(valoracion);
					    elementoDNI.appendChild(textvalue);
				    }   
			    }
		    }
		    
		    //Generar el tranformador para obtener el documento XML en un fichero
		    TransformerFactory f�bricaTransformador = TransformerFactory.newInstance();
		    Transformer transformador = f�bricaTransformador.newTransformer();
		    //Insertar saltos de l�nea al final de cada l�nea
		    transformador.setOutputProperty(OutputKeys.INDENT, "yes");
		    //A�adir 3 espacios delante, en funci�n del nivel de cada nodo
		   // transformador.setOutputProperty(OutputPropertiesFactory.S_KEY_INDENT_AMOUNT, "3");
		    Source origen = new DOMSource(documento);
		    Result destino = new StreamResult("salida.xml");
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
*/
}
