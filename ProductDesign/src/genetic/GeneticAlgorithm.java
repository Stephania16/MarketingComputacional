package genetic;

import java.util.HashMap;
import javax.swing.JTextArea;
import general.Algorithm;
import general.Attribute;
import general.Product;
import java.util.ArrayList;

public class GeneticAlgorithm extends Algorithm{//implements Algorithm{

	/* GA VARIABLES */
	private int BestWSC; /* Stores the best wsc found */
	private ArrayList<Product> Population; // Private mPopu As List(Of List(Of
											// Integer))
	private ArrayList<Integer> Fitness; /* * mFitness(i) = wsc of mPopu(i) */

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

		inputData(inputFile,archivo);

		for (int i = 0; i < getNumExecutions(); i++) {
			if (i != 0)
					 /* We reset myPP and create a new product as the first
					 * product
					 */ 
			Producers.get(MY_PRODUCER)
					.setProduct(createNearProduct(Producers.get(MY_PRODUCER).getAvailableAttribute(),
							(int) (CustomerProfiles.size() * Math.random())));
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
				+ "Centroides: " + inWeka.getIndexProfiles().toString() + "\r\n"
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
			
			for(int j = 0; j < inWeka.getIndexProfiles().size(); j++){
				createNearProductCluster(Producers.get(MY_PRODUCER).getAvailableAttribute(),inWeka.getIndexProfiles().get(j));
				
			}

			if (i % 2 == 0) /* We create a random product */
				Population.add(createRndProduct(Producers.get(MY_PRODUCER).getAvailableAttribute()));
			else /* We create a near product */
				Population.add(createNearProduct(Producers.get(MY_PRODUCER).getAvailableAttribute(),
						(int) (CustomerProfiles.size() * Math.random()))); 

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


}