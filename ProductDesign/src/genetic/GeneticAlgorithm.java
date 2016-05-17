package genetic;

import java.util.HashMap;
import javax.swing.JTextArea;

import general.Algorithm;
import general.Attribute;
import general.CustomerProfile;
import general.Interpolation;
import general.LinkedAttribute;
import general.Producer;
import general.Product;
import input.InputGUI;
import input.InputRandom;
import input.InputWeka;
import output.OutputResults;

import java.util.ArrayList;

public class GeneticAlgorithm extends Algorithm{

	static double KNOWN_ATTRIBUTES = 100; /*
											 * 100 % of attributes known for all
											 * producers*/

	static int CROSSOVER_PROB = 80; /* % of crossover */
	static int MUTATION_PROB = 1; /* % of mutation */
	static int NUM_GENERATIONS = 100; /* number of generations */
	static int NUM_POPULATION = 20; /* number of population */
	static int RESP_PER_GROUP = 20; /*
									 * * We divide the respondents of each
									 * profile in groups of RESP_PER_GROUP
									 * respondents
									 */
	static int NEAR_CUST_PROFS = 4;
	static int NUM_EXECUTIONS = 20; /* number of executions = 20 */

	static final int MY_PRODUCER = 0; // The index of my producer

	/*
	 * public static int fit = 1; public static int customers = 0; public static
	 * int Benefits = 1;
	 */

	public boolean maximizar = false;
	public static boolean isAttributesLinked = false;

	private ArrayList<Attribute> TotalAttributes = new ArrayList<>();
	private ArrayList<Producer> Producers = new ArrayList<>();

	/* GA VARIABLES */
	private int BestWSC; /* Stores the best wsc found */
	private ArrayList<Product> Population; // Private mPopu As List(Of List(Of
											// Integer))
	private ArrayList<Integer> Fitness; /* * mFitness(i) = wsc of mPopu(i) */

	/* STATISTICAL VARIABLES */
	private ArrayList<Integer> Results = new ArrayList<>();
	private ArrayList<Integer> Initial_Results = new ArrayList<>();
	private int wscSum;

	private ArrayList<Integer> Prices = new ArrayList<>();
	private ArrayList<CustomerProfile> CustomerProfiles = new ArrayList<>();
	InputGUI inputGUI = new InputGUI();
	InputRandom in = new InputRandom();
	OutputResults out = new OutputResults();
	InputWeka inWeka = new InputWeka();
	Interpolation inter = new Interpolation();

	/***************************************
	 * " AUXILIARY EXCEL METHODS " * @throws Exception
	 ***************************************/

	public void start(JTextArea jtA, String archivo, boolean inputFile) throws Exception { // String
																								// args[]
		long inicio = System.currentTimeMillis();
		statisticsAlgorithm(jtA, archivo, inputFile);
		long tiempo = System.currentTimeMillis() - inicio;

		double elapsedTimeSec = tiempo / 1.0E03;
		jtA.append("\nTiempo de ejecución = " + String.format("%.2f", elapsedTimeSec) + " seconds" + "\n");
	}

	/***************************************
	 * " PRIVATE METHODS "
	 ***************************************/

	/**
	 * Solving the PD problem by using a GA
	 */
	private void solvePD_GA() throws Exception {

		ArrayList<Product> newPopu;
		ArrayList<Integer> newFitness = new ArrayList<>();
		createInitPopu();
		for (int generation = 0; generation < getNUM_GENERATIONS(); generation++) {
			newPopu = createNewPopu(newFitness);
			Population = tournament(newPopu, newFitness);
		}

		Results.add(BestWSC);
		showWSC();

		int price_MyProduct = inter.calculatePrice(Producers.get(MY_PRODUCER).getProduct(), getTotalAttributes(), getProducers());
		Producers.get(MY_PRODUCER).getProduct().setPrice(price_MyProduct);
		Prices.add(price_MyProduct);
	}

	/**
	 * Generating statistics about the PD problem
	 */
	public void statisticsAlgorithm(JTextArea jtA, String archivo, boolean inputFile) throws Exception {

		double mean;
		double initMean;
		double sum = 0; /* sum of customers achieved */
		double initSum = 0; /* sum of initial customers */
		int sumCust = 0; /* sum of the total number of customers */
		double custMean;
		double variance;
		double initVariance;
		double stdDev;
		double initStdDev;
		double percCust; /* % of customers achieved */
		double initPercCust; /* % of initial customers achieved */
		int price = 0;
		double My_price;
		
		
		Results = new ArrayList<>();
		Initial_Results = new ArrayList<>();
		Prices = new ArrayList<>();

		Math.random();

		if (inputFile) {
			int input = archivo.indexOf(".xml");
			if(input != -1) inputGUI.inputXML(archivo);
			else inputGUI.inputTxt(archivo);
			generarDatosGUI();
		} else {
			if (inputGUI.isGenerarDatosEntrada())
				generarDatosGUI();
			else if (Producers.size() == 0) {
				in.generate();
				TotalAttributes = in.getTotalAttributes();
				Producers = in.getProducers();
				CustomerProfiles = in.getCustomerProfiles();
			}
		}

		for (int i = 0; i < getNumExecutions(); i++) {
			if (i != 0) {
				if(inWeka.isClusters())
				{
					Producers.get(MY_PRODUCER)
					.setProduct(createNearProductCluster(Producers.get(MY_PRODUCER).getAvailableAttribute(), inWeka.getCustomerProfiles().size()));
				}
				else {/*
					 * We reset myPP and create a new product as the first
					 * product
					 */ 
			Producers.get(MY_PRODUCER)
					.setProduct(createNearProduct(Producers.get(MY_PRODUCER).getAvailableAttribute(),
							(int) (CustomerProfiles.size() * Math.random())));
				}
			}
			solvePD_GA();
			sum += Results.get(i);
			initSum += Initial_Results.get(i);
			sumCust += wscSum;
			price += Prices.get(i);
		}

		mean = sum / getNumExecutions();
		initMean = initSum / getNumExecutions();
		variance = computeVariance(mean);
		initVariance = computeVariance(initMean);
		stdDev = Math.sqrt(variance);
		initStdDev = Math.sqrt(initVariance);
		custMean = sumCust / getNumExecutions();
		percCust = 100 * mean / custMean;
		initPercCust = 100 * initMean / custMean;
		My_price = price / getNumExecutions();

		jtA.setText("");
		jtA.append("Num Ejecuciones: " + getNumExecutions() + "\r\n" 
				+ "Num atributos: " + TotalAttributes.size() + "\r\n"
				+ "Num productores: " + Producers.size() + "\r\n" 
				+ "Num perfiles: " + CustomerProfiles.size() + "\r\n"
				+ "Number CustProf: " + inputGUI.getnum() + "\r\n" 
				+ "Num Población: " + getNUM_POPULATION() + "\r\n"
				+ "Num Generaciones: " + getNUM_GENERATIONS() + "\r\n"
				+ "Atributos conocidos: " + getKNOWN_ATTRIBUTES() + " %" + "\r\n"
				+ "Atributos especiales: " + in.getSPECIAL_ATTRIBUTES() + " %" + "\r\n"
				+ "% Mutación de atributos: " + in.getMUT_PROB_CUSTOMER_PROFILE() + " %" + "\r\n"
				+ "% Crossover: " + getCROSSOVER_PROB() + " %" + "\r\n"
				+ "% Mutación: " + getMUTATION_PROB() + " %" + "\r\n"
				+ "Num grupos de perfil: " + getRESP_PER_GROUP() + "\r\n" 
				+ "Num perfiles cercanos: " + getNEAR_CUST_PROFS() + "\r\n"
				+ "Num productos: " + in.getNumber_Products() + "\r\n" 
				+ "Atributos linkados: " + isAttributesLinked() + "\r\n"
				+ "************* RESULTS *************" + "\r\n"  
				+ "BestWSC: " + BestWSC + "\r\n" 
				+ "Mean: " + String.format("%.2f", mean) + "\r\n" 
				+ "initMean: " + String.format("%.2f", initMean) + "\r\n" 
				+ "variance: " + String.format("%.2f", variance) + "\r\n"
				+ "initVariance: " + String.format("%.2f", initVariance) + "\r\n" 
				+ "stdDev: " + String.format("%.2f", stdDev) + "\r\n"
				+ "initStdDev: " + String.format("%.2f", initStdDev) + "\r\n" 
				+ "custMean: " + String.format("%.2f", custMean) + "\r\n"
				+ "Price: " + String.format("%.2f", My_price) + " €" + "\r\n");

		if (isMaximizar()) { // fit == customers
			jtA.append("percCust: " + String.format("%.2f", percCust) + " %" + "\r\n"
					 + "initPercCust: " + String.format("%.2f", initPercCust) + " %" + "\r\n");
		} else { // if (fit == Benefits)
			jtA.append("percCust: " + String.format("%.2f", ((100 * mean) / initMean)) + " %" + "\r\n");
		}
		
		out.output(jtA, "Algoritmo Genético");
		
	}

	public void generarDatosGUI() throws Exception {
		TotalAttributes = inputGUI.getTotalAttributes();
		CustomerProfiles = inputGUI.getCustomerProfiles();
		inputGUI.setProfiles();
		inputGUI.divideCustomerProfile();
		Producers = inputGUI.getProducers();
	}
	
	/*************************************** " AUXILIARY METHODS GENERATEINPUT()" ***************************************/

		/**
	 * Creating a random product
	 */
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
		product.setPrice(inter.calculatePrice(product, getTotalAttributes(), getProducers()));
		return product;
	}

	/**
	 * Creating a product near various customer profiles
	 */
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
		product.setPrice(inter.calculatePrice(product, getTotalAttributes(), getProducers()));
		return product;
	}

	/**
	 * Creating a product near various customer profiles cluster
	 */
	public Product createNearProductCluster(ArrayList<Attribute> availableAttribute, int nearCustProfs) {
		// improve having into account the sub-profiles*/
		Product product = new Product(new HashMap<Attribute, Integer>());
		int possible = 0;
		int attrVal = 0;

		ArrayList<Integer> possibleAttr = new ArrayList<>();		
		for(int z = 0; z < nearCustProfs; z++){
			for(int j = 0; j < inWeka.getCustomerProfiles().get(z).getScoreAttributes().size(); j++)
			{
				for(int k = 0; k < inWeka.getCustomerProfiles().get(z).getScoreAttributes().get(j).getScoreValues().size(); k++){
					possible += inWeka.getCustomerProfiles().get(z).getScoreAttributes().get(j).getScoreValues().get(k);
				}
				possibleAttr.add(possible);
				attrVal = getMaxAttrVal(j, possibleAttr, availableAttribute);
				product.getAttributeValue().put(inWeka.getCustomerProfiles().get(z).getScoreAttributes().get(j), attrVal);
			}
		}
		
		product.setPrice(inter.calculatePrice(product, getTotalAttributes(), getProducers()));
		return product;
	}
	
	/**
	 * Chosing an attribute near to the customer profiles given
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
	 * Chosing the attribute with the maximum score for the customer profiles
	 * given
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

	/*************************************** " AUXILIARY METHODS SOLVEPD_GA()" ***************************************/

	/**
	 * Creating the initial population
	 *
	 * @throws Exception
	 */
	public void createInitPopu() throws Exception {
		Population = new ArrayList<>();
		Fitness = new ArrayList<>();

		Population.add((Producers.get(MY_PRODUCER).getProduct()).clone());
		if (isMaximizar()) // fit == customers
			Fitness.add(computeWSC(Population.get(MY_PRODUCER), MY_PRODUCER));
		else
			Fitness.add(computeBenefits(Population.get(MY_PRODUCER), MY_PRODUCER));

		BestWSC = Fitness.get(MY_PRODUCER);
		Initial_Results.add(BestWSC);

		for (int i = 1; i < getNUM_POPULATION(); i++) {

			if (i % 2 == 0) /* We create a random product */
				Population.add(createRndProduct(Producers.get(MY_PRODUCER).getAvailableAttribute()));
			else /* We create a near product */
				Population.add(createNearProduct(Producers.get(MY_PRODUCER).getAvailableAttribute(),
						(int) (CustomerProfiles.size() * Math.random()))); ///////// ??verificar//////////

			if (isMaximizar()) // fit == customers
				Fitness.add(computeWSC(Population.get(i), MY_PRODUCER));
			else
				Fitness.add(computeBenefits(Population.get(i), MY_PRODUCER));

			if (Fitness.get(i) > BestWSC) {
				BestWSC = Fitness.get(i);
				Producers.get(MY_PRODUCER).setProduct(Population.get(i).clone());
			}
		}
	}

	public Integer computeBenefits(Product product, int myProducer) throws Exception {
		return computeWSC(product, myProducer) * product.getPrice();
	}

	/***
	 * Computing the weighted score of the producer prodInd is the index of the
	 * producer
	 *
	 * @throws Exception
	 **/
	public int computeWSC(Product product, int prodInd) throws Exception {
		int wsc = 0;
		boolean isTheFavourite;
		int meScore, score, k, numTies;

		for (int i = 0; i < CustomerProfiles.size(); i++) {
			for (int j = 0; j < CustomerProfiles.get(i).getSubProfiles().size(); j++) {
				isTheFavourite = true;
				numTies = 1;
				meScore = scoreProduct(CustomerProfiles.get(i).getSubProfiles().get(j), product);
				
				if(isAttributesLinked())
                    meScore += scoreLinkedAttributes(CustomerProfiles.get(i).getLinkedAttributes(), product);
				
				k = 0;
				while (isTheFavourite && k < Producers.size()) {
					if (k != prodInd) {

						score = scoreProduct(CustomerProfiles.get(i).getSubProfiles().get(j), Producers.get(k).product);

						if(isAttributesLinked())
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

    public int scoreLinkedAttributes(ArrayList<LinkedAttribute> linkedAttributes, Product product) {
        int modifyScore = 0;
            for(int i = 0; i < linkedAttributes.size(); i++){
                LinkedAttribute link = linkedAttributes.get(i);
                if(product.getAttributeValue().get(link.getAttribute1()) == link.getValue1() && product.getAttributeValue().get(link.getAttribute2()) == link.getValue2()){
                    modifyScore += link.getScoreModification();
                }
            }
        return modifyScore;
    }
	
	/**
	 * Computing the score of a product given the customer profile index
	 * custProfInd and the product
	 */
	public int scoreProduct(SubProfile subprofile, Product product) throws Exception {
		int score = 0;
		for (int i = 0; i < TotalAttributes.size(); i++) {
			score += scoreAttribute(TotalAttributes.get(i).getMAX(),
					subprofile.getValueChosen().get(TotalAttributes.get(i)),
					product.getAttributeValue().get(TotalAttributes.get(i)));
		}
		return score;
	}

	/**
	 * Computing the score of an attribute for a product given the ' number of
	 * values
	 */
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
		case 5: {
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

	/**
	 * Creating a new population
	 */
	private ArrayList<Product> createNewPopu(ArrayList<Integer> fitness) throws Exception {
		int fitnessSum = computeFitnessSum();
		ArrayList<Product> newPopu = new ArrayList<>();
		int father, mother;
		Product son;

		for (int i = 0; i < getNUM_POPULATION(); i++) {
			father = chooseFather(fitnessSum);
			mother = chooseFather(fitnessSum);
			son = mutate(breed(father, mother));

			newPopu.add(son);

			if (isMaximizar()) // fit == customers
				fitness.add(computeWSC(newPopu.get(i), MY_PRODUCER));
			else
				fitness.add(computeBenefits(newPopu.get(i), MY_PRODUCER));
		}

		return newPopu;
	}

	/**
	 * Computing the sum of the fitness of all the population
	 */
	private int computeFitnessSum() {
		int sum = 0;
		for (int i = 0; i < Fitness.size() - 1; i++) {
			sum += Fitness.get(i);
		}
		return sum;
	}

	/**
	 * Chosing the father in a random way taking into account the fitness
	 */
	private int chooseFather(double fitnessSum) {
		int fatherPos = 0;
		double rndVal = fitnessSum * Math.random();
		double accumulator = Fitness.get(fatherPos);
		while (rndVal > accumulator) {
			fatherPos += 1;
			accumulator += Fitness.get(fatherPos);
		}
		return fatherPos;
	}

	/**
	 * Metodo que dado un padre y una madre los cruza para obtener un hijo. Para
	 * cada posici�n del array eligiremos aleatoriamente si el hijo heredar�
	 * esa posici�n del padre o de la madre.
	 */
	private Product breed(int father, int mother) {
		Product son = new Product();
		/* Random value in range [0,100) */
		int crossover = (int) (100 * Math.random());
		int rndVal;

		if (crossover <= getCROSSOVER_PROB()) {// El hijo sera una mezcla de la
												// madre y el padre
			HashMap<Attribute, Integer> crossover_attributeValue = new HashMap<>();
			for (int i = 0; i < TotalAttributes.size(); i++) { // With son

				rndVal = (int) (2
						* Math.random()); /*
											 * Generamos aleatoriamente un 0
											 * (padre) o un 1 (madre).
											 */
				if (rndVal == 0)
					crossover_attributeValue.put(TotalAttributes.get(i),
							Population.get(father).getAttributeValue().get(TotalAttributes.get(i)));
				else
					crossover_attributeValue.put(TotalAttributes.get(i),
							Population.get(mother).getAttributeValue().get(TotalAttributes.get(i)));
			}
			son.setAttributeValue(crossover_attributeValue);
		} else {// El hijo seria completamente igual a la madre o al padre
			rndVal = (int) (2 * Math.random()); /*
												 * Generamos aleatoriamente un 0
												 * (padre) o un 1 (madre).
												 */

			if (rndVal == 0)
				son = Population.get(father).clone();
			else
				son = Population.get(mother).clone();
		}
		return son;
	}

	/**
	 * Method that creates an individual parameter passed mutating individual.
	 * The mutation is to add / remove a joint solution.
	 *
	 * @throws Exception
	 */
	private Product mutate(Product indiv) {

		Product mutant = indiv.clone();
		int attrVal = 0;

		for (int j = 0; j < TotalAttributes.size(); j++) {
			/* Random value in range [0,100) */
			double mutation = 100 * Math.random();
			if (mutation <= getMUTATION_PROB()) {
				boolean attrFound = false;
				while (!attrFound) {
					attrVal = (int) (Math.floor(TotalAttributes.get(j).getMAX() * Math.random()));
					if (Producers.get(MY_PRODUCER).getAvailableAttribute().get(j).getAvailableValues().get(attrVal))
						attrFound = true;
				}
				mutant.getAttributeValue().put(TotalAttributes.get(j), attrVal);
			}
		}
		mutant.setPrice(inter.calculatePrice(mutant, getTotalAttributes(), getProducers()));
		return mutant;
	}

	/**
	 * M�todo que dada la Población original y una nueva Población elige
	 * la siguente ' generaci�n de individuos. Actualizo la mejor soluci�n
	 * encontrada en caso de mejorarla.
	 */
	private ArrayList<Product> tournament(ArrayList<Product> newPopu, ArrayList<Integer> newFitness) {

		ArrayList<Product> nextGeneration = new ArrayList<>();
		for (int i = 0; i < getNUM_POPULATION(); i++) {

			if (Fitness.get(i) >= newFitness.get(i))
				nextGeneration.add((Population.get(i)).clone());
			else {

				nextGeneration.add((newPopu.get(i)).clone());
				Fitness.set(i, newFitness.get(i));// We update the fitness of
													// the new individual

				if (newFitness.get(i) > BestWSC) {
					BestWSC = newFitness.get(i);
					Producers.get(0).setProduct(newPopu.get(i));
				}
			}
		}
		return nextGeneration;
	}

	/**
	 * Showing the wsc of the rest of products
	 *
	 * @throws Exception
	 */

	public void showWSC() throws Exception {
		int wsc;
		wscSum = 0;

		for (int i = 0; i < Producers.size(); i++) {
			wsc = computeWSC(Producers.get(i).getProduct(), i);
			wscSum += wsc;
		}
	}

	/*************************************** " AUXILIARY METHODS STATISTICSPD()" ***************************************/

	/**
	 * Computing the variance
	 */
	public double computeVariance(double mean) {
		double sqrSum = 0;
		for (int i = 0; i < getNumExecutions(); i++) {
			sqrSum += Math.pow(Results.get(i) - mean, 2);
		}
		return (sqrSum / getNumExecutions());
	}

	/*************************************** " GETTERS Y SETTERS OF ATTRIBUTES " ***************************************/
	public int getNumExecutions() {
		return NUM_EXECUTIONS;
	}

	public void setNumExecutions(int exec) {
		NUM_EXECUTIONS = exec;
	}

	public double getKNOWN_ATTRIBUTES() {
		return KNOWN_ATTRIBUTES;
	}

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

	public int getRESP_PER_GROUP() {
		return RESP_PER_GROUP;
	}

	public int getNEAR_CUST_PROFS() {
		return NEAR_CUST_PROFS;
	}

	public void setKNOWN_ATTRIBUTES(double kNOWN_ATTRIBUTES) {
		KNOWN_ATTRIBUTES = kNOWN_ATTRIBUTES;
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

	public void setRESP_PER_GROUP(int rESP_PER_GROUP) {
		RESP_PER_GROUP = rESP_PER_GROUP;
	}

	public void setNEAR_CUST_PROFS(int nEAR_CUST_PROFS) {
		NEAR_CUST_PROFS = nEAR_CUST_PROFS;
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

	public boolean isMaximizar() {
		return maximizar;
	}

	public void setMaximizar(boolean maximizar) {
		this.maximizar = maximizar;
	}

	public boolean isAttributesLinked() {
		return isAttributesLinked;
	}

	public void setAttributesLinked(boolean isAttributesLinked) {
		GeneticAlgorithm.isAttributesLinked = isAttributesLinked;
	}

}