package general;

import java.util.ArrayList;
import java.util.HashMap;
import genetic.SubProfile;
import input.InputGUI;
import input.InputRandom;
import input.InputWeka;
import output.OutputResults;

public class Algorithm {
	static double VEL_LOW = -2;
	static double VEL_HIGH = 2;
	static double KNOWN_ATTRIBUTES = 100;
	static int CROSSOVER_PROB = 80; /* % de crossover */
	static int MUTATION_PROB = 1; /* % de mutacion */
	static int NUM_GENERATIONS = 100; /* numero de generaciones */
	static int NUM_POPULATION = 20; /* numero de poblacion */
	static int RESP_PER_GROUP = 20;
	static int NEAR_CUST_PROFS = 4;
	static int NUM_EXECUTIONS = 20; /* numero de ejecuciones */
	public final int MY_PRODUCER = 0;
	public static boolean isPSO = false;
	public static boolean maximizar = false;
	public static boolean isAttributesLinked = false;
	public static ArrayList<Attribute> TotalAttributes = new ArrayList<>();
	public static ArrayList<Producer> Producers = new ArrayList<>();
	public Interpolation inter = new Interpolation();
	public static ArrayList<CustomerProfile> CustomerProfiles = new ArrayList<>();
	public int wscSum;
	public static int number_Products = 1;

	public ArrayList<ArrayList<Integer>> Results = new ArrayList<>();
	public ArrayList<ArrayList<Integer>> Initial_Results = new ArrayList<>();
	public ArrayList<ArrayList<Integer>> Prices = new ArrayList<>();
	public InputGUI inputGUI = new InputGUI();
	public InputRandom in = new InputRandom();
	public OutputResults out = new OutputResults();
	public InputWeka inWeka = new InputWeka();

	/** Generar datos */
	public void inputData(boolean inputFile, String archivo) throws Exception {
		if (inputFile) {
			int input = archivo.indexOf(".xml");
			if (input != -1)
				inputGUI.inputXML(archivo);
			else
				inputGUI.inputTxt(archivo);
			generarDatosGUI();
		} else {
			if (inputGUI.isGenerarDatosEntrada())
				generarDatosGUI();
			else if (Producers.size() == 0 || inputGUI.isNumData()) {
				in.generate();
				TotalAttributes = in.getTotalAttributes();
				Producers = in.getProducers();
				CustomerProfiles = in.getCustomerProfiles();
			}
		}
	}

	/** Generar datos de la gui */
	public void generarDatosGUI() throws Exception {
		TotalAttributes = inputGUI.getTotalAttributes();
		CustomerProfiles = inputGUI.getCustomerProfiles();
		inputGUI.setProfiles();
		inputGUI.divideCustomerProfile();
		Producers = inputGUI.getProducers();
	}

	/** Crear un producto aleatorio */
	public Product createRndProduct(ArrayList<Attribute> availableAttribute) {
		Product product = new Product(new HashMap<Attribute, Integer>());
		int limit = (int) (TotalAttributes.size() * getKNOWN_ATTRIBUTES() / 100);
		int attrVal = 0;

		for (int i = 0; i < limit; i++) {
			attrVal = (int) (TotalAttributes.get(i).getMAX() * Math.random());
			product.getAttributeValue().put(TotalAttributes.get(i), attrVal);

		}

		for (int i = limit; i < TotalAttributes.size(); i++) {
			boolean attrFound = false;
			while (!attrFound) {

				attrVal = (int) (TotalAttributes.get(i).getMAX() * Math.random());

				if (availableAttribute.get(i).getAvailableValues().get(attrVal))
					attrFound = true;

			}
			product.getAttributeValue().put(TotalAttributes.get(i), attrVal);
		}

		// IF WE NEED THE VELOCTY FOR PSO
		if (isPSO()) {
			product.setVelocity(new HashMap<Attribute, Double>());
			for (int i = 0; i < TotalAttributes.size(); i++) {
				double velocity = (((VEL_HIGH - VEL_LOW) * Math.random()) - VEL_LOW);
				product.getVelocity().put(TotalAttributes.get(i), velocity);
			}
		}

		product.setPrice(inter.calculatePrice(product, getTotalAttributes(), getProducers()));
		return product;
	}

	/** Crear un producto cercano */
	public Product createNearProduct(ArrayList<Attribute> availableAttribute, int nearCustProfs) {
		// improve having into account the sub-profiles*/
		Product product = new Product(new HashMap<Attribute, Integer>());
		int attrVal;

		ArrayList<Integer> custProfsInd = new ArrayList<>();
		for (int i = 1; i < nearCustProfs; i++)
			custProfsInd.add((int) Math.floor(CustomerProfiles.size() * Math.random()));

		for (int i = 0; i < TotalAttributes.size(); i++) {
			attrVal = chooseAttribute(i, custProfsInd, availableAttribute);
			product.getAttributeValue().put(TotalAttributes.get(i), attrVal);
		}
		// IF WE NEED THE VELOCTY FOR PSO
		if (isPSO()) {
			product.setVelocity(new HashMap<Attribute, Double>());
			for (int i = 0; i < TotalAttributes.size(); i++) {
				double velocity = (((VEL_HIGH - VEL_LOW) * Math.random()) - VEL_LOW);
				product.getVelocity().put(TotalAttributes.get(i), velocity);
			}
		}

		product.setPrice(inter.calculatePrice(product, getTotalAttributes(), getProducers()));
		return product;
	}

	/**
	 * Elegir un atributo cercano por cada perfil
	 */
	public int chooseAttribute(int attrInd, ArrayList<Integer> custProfInd, ArrayList<Attribute> availableAttrs) {
		int attrVal;

		ArrayList<Integer> possibleAttr = new ArrayList<>();

		for (int i = 0; i < TotalAttributes.get(attrInd).getMAX(); i++) {
			/*
			 * We count the valoration of each selected profile for attribute
			 * attrInd value i
			 */
			int possible = 0;
			for (int j = 0; j < custProfInd.size(); j++) {
				possible += CustomerProfiles.get(custProfInd.get(j)).getScoreAttributes().get(attrInd).getScoreValues()
						.get(i);
			}
			possibleAttr.add(possible);
		}
		attrVal = getMaxAttrVal(attrInd, possibleAttr, availableAttrs);

		return attrVal;
	}

	/**
	 * Crea un producto cercano para cada perfil obtenido como centroide
	 */
	public Product createNearProductCluster(ArrayList<Attribute> availableAttribute, int index) {

		Product product = new Product(new HashMap<Attribute, Integer>());
		int attrVal;

		for (int i = 0; i < TotalAttributes.size(); i++) {
			attrVal = chooseAttributeCluster(i, index, availableAttribute);
			product.getAttributeValue().put(TotalAttributes.get(i), attrVal);
		}
		// IF WE NEED THE VELOCTY FOR PSO
		if (isPSO()) {
			product.setVelocity(new HashMap<Attribute, Double>());
			for (int i = 0; i < TotalAttributes.size(); i++) {
				double velocity = (((VEL_HIGH - VEL_LOW) * Math.random()) - VEL_LOW);
				product.getVelocity().put(TotalAttributes.get(i), velocity);
			}
		}
		product.setPrice(inter.calculatePrice(product, getTotalAttributes(), getProducers()));
		return product;
	}

	/**
	 * Elegir un atributo cercano por cada perfil obtenido como centroide
	 */
	private int chooseAttributeCluster(int attrInd, int index, ArrayList<Attribute> availableAttrs) {
		int attrVal;

		ArrayList<Integer> possibleAttr = new ArrayList<>();

		for (int i = 0; i < TotalAttributes.get(attrInd).getMAX(); i++) {
			int possible = 0;
			possible += getCustomerProfiles().get(index).getScoreAttributes().get(attrInd).getScoreValues().get(i);
			possibleAttr.add(possible);
		}
		attrVal = getMaxAttrVal(attrInd, possibleAttr, availableAttrs);
		return attrVal;
	}

	/**
	 * Elegir atributo con la máxima puntuación de los perfiles de los clientes
	 * dados
	 */
	public int getMaxAttrVal(int attrInd, ArrayList<Integer> possibleAttr, ArrayList<Attribute> availableAttr) {
		int attrVal = -1;
		double max = -1;

		for (int i = 0; i < possibleAttr.size(); i++) {
			if (availableAttr.get(attrInd).getAvailableValues().get(i) && possibleAttr.get(i) > max) {
				max = possibleAttr.get(i);
				attrVal = i;
			}
		}
		return attrVal;
	}

	/** Calcular los beneficios */
	public Integer computeBenefits(Product product, int myProducer) throws Exception {
		return computeWSC(product, myProducer) * product.getPrice();
	}

	/** Calcular WSC */
	public int computeWSC(Product product, int prodInd) throws Exception {
		int wsc = 0;
		boolean isTheFavourite;
		int meScore, score, k, numTies;

		for (int i = 0; i < CustomerProfiles.size(); i++) {
			for (int j = 0; j < CustomerProfiles.get(i).getSubProfiles().size(); j++) {
				isTheFavourite = true;
				numTies = 1;
				meScore = scoreProduct(CustomerProfiles.get(i).getSubProfiles().get(j), product);

				if (isAttributesLinked())
					meScore += scoreLinkedAttributes(CustomerProfiles.get(i).getLinkedAttributes(), product);

				k = 0;
				while (isTheFavourite && k < Producers.size()) {
					if (k != prodInd) {

						score = scoreProduct(CustomerProfiles.get(i).getSubProfiles().get(j), Producers.get(k).product);

						if (isAttributesLinked())
							score += scoreLinkedAttributes(CustomerProfiles.get(i).getLinkedAttributes(), product);

						if (score > meScore)
							isTheFavourite = false;

						else if (score == meScore)
							numTies += 1;
					}
					k++;
				}
				/*
				 * TODO: When there exists ties we loose some voters because of
				 * decimals (undecided voters)
				 */
				if (isTheFavourite) {
					if ((j == CustomerProfiles.get(i).getSubProfiles().size())
							&& ((CustomerProfiles.get(i).getNumberCustomers() % getRESP_PER_GROUP()) != 0)) {
						wsc += (CustomerProfiles.get(i).getNumberCustomers() % getRESP_PER_GROUP()) / numTies;
					} else {
						wsc += getRESP_PER_GROUP() / numTies;
					}
				}

			}
		}

		return wsc;
	}

	/** Calcular la puntuacion de atributos linkados */
	public int scoreLinkedAttributes(ArrayList<LinkedAttribute> linkedAttributes, Product product) {
		int modifyScore = 0;
		for (int i = 0; i < linkedAttributes.size(); i++) {
			LinkedAttribute link = linkedAttributes.get(i);
			if (product.getAttributeValue().get(link.getAttribute1()) == link.getValue1()
					&& product.getAttributeValue().get(link.getAttribute2()) == link.getValue2()) {
				modifyScore += link.getScoreModification();
			}
		}
		return modifyScore;
	}

	/** Calcular la puntuacion que se le da a un producto */
	public int scoreProduct(SubProfile subprofile, Product product) throws Exception {
		int score = 0;
		for (int i = 0; i < TotalAttributes.size(); i++) {
			score += scoreAttribute(TotalAttributes.get(i).getMAX(),
					subprofile.getValueChosen().get(TotalAttributes.get(i)),
					product.getAttributeValue().get(TotalAttributes.get(i)));
		}
		return score;
	}

	/** Calcular la puntuacion de atributos */
	public int scoreAttribute(int numOfValsOfAttr, int valOfAttrCust, int valOfAttrProd) throws Exception {
		int score = 0;
		switch (numOfValsOfAttr) {
		case 2: {
			if (valOfAttrCust == valOfAttrProd)
				score = 10;
			else
				score = 0;
		}
			break;
		case 3: {
			if (valOfAttrCust == valOfAttrProd)
				score = 10;
			else if (Math.abs(valOfAttrCust - valOfAttrProd) == 1)
				score = 5;
			else
				score = 0;
		}
			break;
		case 4: {
			if (valOfAttrCust == valOfAttrProd)
				score = 10;
			else if (Math.abs(valOfAttrCust - valOfAttrProd) == 1)
				score = 6;
			else if (Math.abs(valOfAttrCust - valOfAttrProd) == 2)
				score = 2;
			else
				score = 0;
		}
			break;
		case 5:
		case 6:
		case 7:
		case 8:
		case 9:
		case 10: {
			if (valOfAttrCust == valOfAttrProd)
				score = 10;
			else if (Math.abs(valOfAttrCust - valOfAttrProd) == 1)
				score = 6;
			else if (Math.abs(valOfAttrCust - valOfAttrProd) == 2)
				score = 2;
			else if (Math.abs(valOfAttrCust - valOfAttrProd) == 3)
				score = 1;
			else
				score = 0;
		}
			break;
		case 11: {
			if (valOfAttrCust == valOfAttrProd)
				score = 10;
			else if (Math.abs(valOfAttrCust - valOfAttrProd) == 1)
				score = 8;
			else if (Math.abs(valOfAttrCust - valOfAttrProd) == 2)
				score = 6;
			else if (Math.abs(valOfAttrCust - valOfAttrProd) == 3)
				score = 4;
			else if (Math.abs(valOfAttrCust - valOfAttrProd) == 4)
				score = 2;
			else
				score = 0;
		}
			break;
		default:
			throw new Exception(
					"Error in scoreAttribute() function: " + "Number of values of the attribute unexpected");
		}
		return score;
	}

	/** Calcular suma de WSC de cada producto de un productor */
	public void showWSC() throws Exception {
		int wsc;
		wscSum = 0;

		for (int i = 0; i < Producers.size(); i++) {
			wsc = computeWSC(Producers.get(i).getProduct(), i);
			wscSum += wsc;
		}
	}

	/**
	 * Calcular la varianza
	 */
	public double computeVariance(double mean) {// TODO me fijo solo en el
												// primero
		double sqrSum = 0;
		for (int i = 0; i < getNumExecutions(); i++) {
			sqrSum += Math.pow(Results.get(i).get(0) - mean, 2);
		}
		return (sqrSum / getNumExecutions());
	}

	/*************************************** " GETTERS Y SETTERS OF ATTRIBUTES " ***************************************/

	public int getCROSSOVER_PROB() {
		return CROSSOVER_PROB;
	}

	public int getMUTATION_PROB() {
		return MUTATION_PROB;
	}

	public int getNUM_GENERATIONS() {
		return NUM_GENERATIONS;
	}

	public int getNUM_POPULATION() {
		return NUM_POPULATION;
	}

	public int getNEAR_CUST_PROFS() {
		return NEAR_CUST_PROFS;
	}

	public void setCROSSOVER_PROB(int cROSSOVER_PROB) {
		CROSSOVER_PROB = cROSSOVER_PROB;
	}

	public void setMUTATION_PROB(int mUTATION_PROB) {
		MUTATION_PROB = mUTATION_PROB;
	}

	public void setNUM_GENERATIONS(int nUM_GENERATIONS) {
		NUM_GENERATIONS = nUM_GENERATIONS;
	}

	public void setNUM_POPULATION(int nUM_POPULATION) {
		NUM_POPULATION = nUM_POPULATION;
	}

	public void setNEAR_CUST_PROFS(int nEAR_CUST_PROFS) {
		NEAR_CUST_PROFS = nEAR_CUST_PROFS;
	}

	public boolean isPSO() {
		return isPSO;
	}

	public void setPSO(boolean ispso) {
		isPSO = ispso;
	}

	public int getNumExecutions() {
		return NUM_EXECUTIONS;
	}

	public void setNumExecutions(int exec) {
		NUM_EXECUTIONS = exec;
	}

	public double getKNOWN_ATTRIBUTES() {
		return KNOWN_ATTRIBUTES;
	}

	public void setKNOWN_ATTRIBUTES(double kNOWN_ATTRIBUTES) {
		KNOWN_ATTRIBUTES = kNOWN_ATTRIBUTES;
	}

	public int getRESP_PER_GROUP() {
		return RESP_PER_GROUP;
	}

	public void setRESP_PER_GROUP(int rESP_PER_GROUP) {
		RESP_PER_GROUP = rESP_PER_GROUP;
	}

	public ArrayList<Attribute> getTotalAttributes() {
		return TotalAttributes;
	}

	public ArrayList<Producer> getProducers() {
		return Producers;
	}

	public ArrayList<CustomerProfile> getCustomerProfiles() {
		return CustomerProfiles;
	}

	public void setTotalAttributes(ArrayList<Attribute> totalAttributes) {
		TotalAttributes = totalAttributes;
	}

	public void setProducers(ArrayList<Producer> producers) {
		Producers = producers;
	}

	public void setCustomerProfiles(ArrayList<CustomerProfile> customerProfiles) {
		CustomerProfiles = customerProfiles;
	}

	public boolean isMaximizar() {
		return maximizar;
	}

	public void setMaximizar(boolean max) {
		maximizar = max;
	}

	public boolean isAttributesLinked() {
		return isAttributesLinked;
	}

	public void setAttributesLinked(boolean isAttributesLink) {
		isAttributesLinked = isAttributesLink;
	}

	public double getVEL_LOW() {
		return VEL_LOW;
	}

	public double getVEL_HIGH() {
		return VEL_HIGH;
	}

	public void setVEL_LOW(double vEL_LOW) {
		VEL_LOW = vEL_LOW;
	}

	public void setVEL_HIGH(double vEL_HIGH) {
		VEL_HIGH = vEL_HIGH;
	}

	public int getNumber_Products() {
		return number_Products;
	}

	public void setNumber_Products(int number) {
		number_Products = number;
	}
}
