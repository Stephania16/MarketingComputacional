package genetic;

import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.JTextArea;
import general.Algorithm;
import general.Attribute;
import general.Product;

public class GeneticAlgorithmVariant extends Algorithm{

	/* GA VARIABLES */
	private ArrayList<Integer> BestWSC = new ArrayList<>(); /*
															 * Stores the best
															 * wsc found
															 */
	private ArrayList<Product> Population = new ArrayList<>(); // Private mPopu
																// As List(Of
																// List(Of
																// Integer))
	private ArrayList<Integer> Fitness = new ArrayList<>(); /*
															 * * mFitness(i) =
															 * wsc of mPopu(i)
															 */

	/* STATISTICAL VARIABLES */
	private ArrayList<ArrayList<Integer>> Results = new ArrayList<>();
	private ArrayList<ArrayList<Integer>> Initial_Results = new ArrayList<>();
	private ArrayList<ArrayList<Integer>> Prices = new ArrayList<>();
	public static int number_Products = 1;


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

		ArrayList<Integer> prices = new ArrayList<>();
		for (int i = 0; i < Producers.get(MY_PRODUCER).getProducts().size(); i++) {
			int price_MyProduct = inter.calculatePrice(Producers.get(MY_PRODUCER).getProducts().get(i), getTotalAttributes(), getProducers());
			Producers.get(MY_PRODUCER).getProducts().get(i).setPrice(price_MyProduct);
			prices.add(price_MyProduct);
		}
		Prices.add(prices);
	}

	/**
	 * Generating statistics about the PD problem
	 */
	public void statisticsAlgorithm(JTextArea jtA, String archivo, boolean inputFile) throws Exception {

		ArrayList<Double> sum = new ArrayList<>();
		ArrayList<Double> initSum = new ArrayList<>();
		ArrayList<Integer> prices = new ArrayList<>();
		int sumCust = 0;

		for (int i = 0; i < getNumber_Products(); i++) {
			sum.add((double) 0);
			initSum.add((double) 0);
			prices.add(0);
		}

		Results = new ArrayList<>();
		Initial_Results = new ArrayList<>();
		Prices = new ArrayList<>();

		inputData(inputFile,archivo);

		for (int i = 0; i < getNumExecutions(); i++) {
			if (i != 0) /*
						 * We reset myPP and create a new product as the first
						 * product
						 */ {
				ArrayList<Product> products = new ArrayList<>();
				for (int k = 0; k < getNumber_Products(); k++)
					products.add(createNearProduct(Producers.get(MY_PRODUCER).getAvailableAttribute(),
							(int) (CustomerProfiles.size() * Math.random())));
				Producers.get(MY_PRODUCER).setProducts(products);
			}
			solvePD_GA();

			ArrayList<Double> auxSum = new ArrayList<>();
			ArrayList<Double> auxinitSum = new ArrayList<>();
			ArrayList<Integer> auxprice = new ArrayList<>();
			for (int k = 0; k < getNumber_Products(); k++) {
				auxSum.add(sum.get(k) + Results.get(i).get(k));
				auxinitSum.add(initSum.get(k) + Initial_Results.get(i).get(k));
				auxprice.add(prices.get(k) + Prices.get(i).get(k));
			}

			sum = auxSum;
			initSum = auxinitSum;
			prices = auxprice;

			sumCust += wscSum;
		}

		String meanTXT = "";
		String initMeanTXT = "";
		String stdDevTXT = "";
		String initStdDevTXT = "";
		String percCustTXT = "";
		String initPercCustTXT = "";
		String priceTXT = "";

		double custMean = sumCust / getNumExecutions();

		for (int i = 0; i < getNumber_Products(); i++) {

			double mean = sum.get(i) / getNumExecutions();
			double initMean = initSum.get(i) / getNumExecutions();
			double variance = computeVariance(mean);
			double initVariance = computeVariance(initMean);
			double stdDev = Math.sqrt(variance);
			double initStdDev = Math.sqrt(initVariance);
			double percCust;
			double initPercCust = -1;
			if (isMaximizar()) {
				percCust = 100 * mean / custMean;
				initPercCust = 100 * initMean / custMean;
			} else {
				percCust = 100 * mean / initMean;
			}
			double priceDoub = prices.get(i) / getNumExecutions();

			if (i == 0)
				meanTXT += String.format("%.2f", mean);
			else
				meanTXT += ", " + String.format("%.2f", mean);

			if (i == 0)
				initMeanTXT += String.format("%.2f", initMean);
			else
				initMeanTXT += ", " + String.format("%.2f", initMean);

			if (i == 0)
				stdDevTXT += String.format("%.2f", stdDev);
			else
				stdDevTXT += ", " + String.format("%.2f", stdDev);

			if (i == 0)
				initStdDevTXT += String.format("%.2f", initStdDev);
			else
				initStdDevTXT += ", " + String.format("%.2f", initStdDev);

			if (i == 0)
				percCustTXT += String.format("%.2f", percCust) + " %";
			else
				percCustTXT += ", " + String.format("%.2f", percCust) + " %";

			if (isMaximizar())
				if (i == 0)
					initPercCustTXT += String.format("%.2f", initPercCust) + " %";
				else
					initPercCustTXT += ", " + String.format("%.2f", initPercCust) + " %";

			if (i == 0)
				priceTXT += String.format("%.2f", priceDoub) + " €";
			else
				priceTXT += ", " + String.format("%.2f", priceDoub) + " €";
		}
		
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
				 + "Mean: " + meanTXT + "\r\n"
				 + "initMean: " + initMeanTXT + "\r\n"
				 + "stdDev: " + stdDevTXT + "\r\n"
				 + "initStdDev: " + initStdDevTXT + "\r\n"
				 + "percCust: " + percCustTXT + "\r\n"
				 + "initPercCust: " + initPercCustTXT + "\r\n"
				 + "My_priceString: " + priceTXT + "\r\n");
		if (isMaximizar()) { 
			jtA.append("percCust: " + percCustTXT + "\r\n"
					 + "initPercCust: " + initPercCustTXT  + "\r\n");
		} else { 
			jtA.append("percCust: " + percCustTXT + "\r\n");
		}
		out.output(jtA, "Algoritmo Genético");

	}


	/*************************************** " AUXILIARY METHODS SOLVEPD_GA()" ***************************************/

	/**
	 * Creating the initial population
	 *
	 * @throws Exception
	 */
	private void createInitPopu() throws Exception {
		Population = new ArrayList<>();
		Fitness = new ArrayList<>();
		BestWSC = new ArrayList<>();

		for (int i = 0; i < Producers.get(MY_PRODUCER).getProducts().size(); i++) {
			Population.add((Producers.get(MY_PRODUCER).getProducts().get(i)).clone());

			if (isMaximizar())
				Fitness.add(computeWSC(Population.get(i), MY_PRODUCER));
			else
				Fitness.add(computeBenefits(Population.get(i), MY_PRODUCER));
		}

		for (int t = 0; t < Fitness.size(); t++)
			BestWSC.add(Fitness.get(t));

		ArrayList<Integer> aux = new ArrayList<>();
		for (int q = 0; q < BestWSC.size(); q++)
			aux.add(BestWSC.get(q));

		Initial_Results.add(aux);

		for (int i = Producers.get(MY_PRODUCER).getProducts().size(); i < getNUM_POPULATION(); i++) {
			
			for(int j = 0; j < inWeka.getIndexProfiles().size(); j++){
				createNearProductCluster(Producers.get(MY_PRODUCER).getAvailableAttribute(),inWeka.getIndexProfiles().get(j));
				
			}

			if (i % 2 == 0) /* We create a random product */
				Population.add(createRndProduct(Producers.get(MY_PRODUCER).getAvailableAttribute()));
			else /* We create a near product */
				Population.add(createNearProduct(Producers.get(MY_PRODUCER).getAvailableAttribute(),
						(int) (CustomerProfiles.size() * Math.random()))); ///////// ??verificar//////////

			if (isMaximizar())
				Fitness.add(computeWSC(Population.get(i), MY_PRODUCER));
			else
				Fitness.add(computeBenefits(Population.get(i), MY_PRODUCER));

			int worstIndex = isBetweenBest(Fitness.get(i));
			if (worstIndex != -1) {
				BestWSC.remove(worstIndex);
				BestWSC.add(Fitness.get(i));
				Producers.get(MY_PRODUCER).getProducts().remove(worstIndex);
				Producers.get(MY_PRODUCER).getProducts().add(Population.get(i));
			}
		}
	}

	private int isBetweenBest(int fitness) {
		for (int i = 0; i < BestWSC.size(); i++) {
			if (fitness > BestWSC.get(i))
				return i;
		}
		return -1;
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
		int meScore, score, k, p, numTies;
		@SuppressWarnings("unused")
		int count = 0;

		for (int i = 0; i < CustomerProfiles.size(); i++) {
			for (int j = 0; j < CustomerProfiles.get(i).getSubProfiles().size(); j++) {
				isTheFavourite = true;
				numTies = 1;
				meScore = scoreProduct(CustomerProfiles.get(i).getSubProfiles().get(j), product);

				if (isAttributesLinked())
					meScore += scoreLinkedAttributes(CustomerProfiles.get(i).getLinkedAttributes(), product);

				k = 0;
				while (isTheFavourite && k < Producers.size()) {
					p = 0;
					while (isTheFavourite && p < Producers.get(k).getProducts().size()) {
						if (Producers.get(k).getProducts().get(p) != product) {

							score = scoreProduct(CustomerProfiles.get(i).getSubProfiles().get(j),
									Producers.get(k).getProducts().get(p));

							if (isAttributesLinked())
								score += scoreLinkedAttributes(CustomerProfiles.get(i).getLinkedAttributes(), product);

							if (score > meScore)
								isTheFavourite = false;

							else if (score == meScore)
								numTies += 1;
						} else {
							count++;
						}
						p++;
					}
					k++;
				}
				/*
				 * When there exists ties we loose some voters because of
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

			if (isMaximizar())
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
	 * cada posici�n del array eligiremos aleatoriamente si el hijo heredar� esa
	 * posici�n del padre o de la madre.
	 */
	private Product breed(int father, int mother) {
		Product son = new Product();
		/* Random value in range [0,100) */
		int crossover = (int) (100 * Math.random());
		int rndVal;

		if (crossover <= getCROSSOVER_PROB()) {// El hijo sera una mezcla de la madre
											// y el padre
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
	 * Metodo que dada la poblaci�n original y una nueva poblaci�n elige la
	 * siguente ' generaci�n de individuos. Actualizo la mejor soluci�n
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

				int worstIndex = isBetweenBest(Fitness.get(i));
				if (worstIndex != -1) {
					BestWSC.remove(worstIndex);
					BestWSC.add(Fitness.get(i));
					Producers.get(MY_PRODUCER).getProducts().remove(worstIndex);
					Producers.get(MY_PRODUCER).getProducts().add((newPopu.get(i)).clone());
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
			for (int j = 0; j < Producers.get(i).getProducts().size(); j++) {
				wsc = computeWSC(Producers.get(i).getProducts().get(j), i);
				wscSum += wsc;
			}
		}
	}

	/*************************************** " AUXILIARY METHODS STATISTICSPD()" ***************************************/

	/**
	 * Computing the variance
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

	
	public int getNumber_Products() {
		return number_Products;
	}

	public void setNumber_Products(int number) {
		number_Products = number;
	}


}
