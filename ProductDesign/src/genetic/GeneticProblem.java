package genetic;

import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.JTextArea;

import general.Algorithm;
import general.Attribute;
import general.Product;

public class GeneticProblem extends GeneticAlgorithm {

	/* GA VARIABLES */
	private ArrayList<Integer> BestWSC = new ArrayList<>(); // Almacena el mejor
															// valor de wsc
	private ArrayList<Integer> Fitness = new ArrayList<>();
	Algorithm alg = new Algorithm();

	/** Método que realiza la ejecución */
	public void start(JTextArea jtA, String archivo, boolean inputFile) throws Exception {
		long inicio = System.currentTimeMillis();
		statisticsAlgorithm(jtA, archivo, inputFile);
		long tiempo = System.currentTimeMillis() - inicio;

		double elapsedTimeSec = tiempo / 1.0E03;
		jtA.append("\nTiempo de ejecución = " + String.format("%.2f", elapsedTimeSec) + " seconds" + "\n");
	}

	/**
	 * Resolviendo el problema PD usando GA
	 */
	@SuppressWarnings("static-access")
	private void solvePD_GA() throws Exception {

		ArrayList<Object> BestResults = solveGeneticAlgorithm(alg.getNumber_Products());

		for (int i = 0; i < BestResults.size(); i++) {
			BestWSC.set(i, getFitness(BestResults.get(i)));
			alg.Producers.get(alg.MY_PRODUCER).getProducts().set(i, (Product) BestResults.get(i));
		}

		alg.Results.add(BestWSC);

		alg.showWSC();

		ArrayList<Integer> prices = new ArrayList<>();
		for (int i = 0; i < alg.Producers.get(alg.MY_PRODUCER).getProducts().size(); i++) {
			int price_MyProduct = alg.inter.calculatePrice(alg.Producers.get(alg.MY_PRODUCER).getProducts().get(i),
					alg.getTotalAttributes(), alg.getProducers());
			alg.Producers.get(alg.MY_PRODUCER).getProducts().get(i).setPrice(price_MyProduct);
			prices.add(price_MyProduct);
		}
		alg.Prices.add(prices);
	}

	/**
	 * Genera las estadísticas sobre el problema PD
	 */
	@SuppressWarnings("static-access")
	public void statisticsAlgorithm(JTextArea jtA, String archivo, boolean inputFile) throws Exception {

		ArrayList<Double> sum = new ArrayList<>();
		ArrayList<Double> initSum = new ArrayList<>();
		ArrayList<Integer> prices = new ArrayList<>();
		int sumCust = 0;

		for (int i = 0; i < alg.getNumber_Products(); i++) {
			sum.add((double) 0);
			initSum.add((double) 0);
			prices.add(0);
		}

		alg.Results = new ArrayList<>();
		alg.Initial_Results = new ArrayList<>();
		alg.Prices = new ArrayList<>();

		alg.inputData(inputFile, archivo);

		for (int i = 0; i < alg.getNumExecutions(); i++) {
			if (i != 0) /*
						 * We reset myPP and create a new product as the first
						 * product
						 */ {
				ArrayList<Product> products = new ArrayList<>();
				for (int k = 0; k < alg.getNumber_Products(); k++)
					products.add(alg.createNearProduct(alg.Producers.get(alg.MY_PRODUCER).getAvailableAttribute(),
							(int) (alg.CustomerProfiles.size() * Math.random())));
				alg.Producers.get(alg.MY_PRODUCER).setProducts(products);
			}
			solvePD_GA();

			ArrayList<Double> auxSum = new ArrayList<>();
			ArrayList<Double> auxinitSum = new ArrayList<>();
			ArrayList<Integer> auxprice = new ArrayList<>();
			for (int k = 0; k < alg.getNumber_Products(); k++) {
				auxSum.add(sum.get(k) + alg.Results.get(i).get(k));
				auxinitSum.add(initSum.get(k) + alg.Initial_Results.get(i).get(k));
				auxprice.add(prices.get(k) + alg.Prices.get(i).get(k));
			}

			sum = auxSum;
			initSum = auxinitSum;
			prices = auxprice;

			sumCust += alg.wscSum;
		}

		String meanTXT = "";
		String initMeanTXT = "";
		String stdDevTXT = "";
		String initStdDevTXT = "";
		String percCustTXT = "";
		String initPercCustTXT = "";
		String priceTXT = "";

		double custMean = sumCust / alg.getNumExecutions();

		for (int i = 0; i < alg.getNumber_Products(); i++) {

			double mean = sum.get(i) / alg.getNumExecutions();
			double initMean = initSum.get(i) / alg.getNumExecutions();
			double variance = alg.computeVariance(mean);
			double initVariance = alg.computeVariance(initMean);
			double stdDev = Math.sqrt(variance);
			double initStdDev = Math.sqrt(initVariance);
			double percCust;
			double initPercCust = -1;
			if (alg.isMaximizar()) {
				percCust = 100 * mean / custMean;
				initPercCust = 100 * initMean / custMean;
			} else {
				percCust = 100 * mean / initMean;
			}
			double priceDoub = prices.get(i) / alg.getNumExecutions();

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

			if (alg.isMaximizar())
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

		jtA.append("Num Ejecuciones: " + alg.getNumExecutions() + "\r\n" + "Num atributos: "
				+ alg.TotalAttributes.size() + "\r\n" + "Num productores: " + alg.Producers.size() + "\r\n"
				+ "Num perfiles: " + alg.CustomerProfiles.size() + "\r\n" + "Number CustProf: " + alg.inputGUI.getnum()
				+ "\r\n" + "Num Población: " + alg.getNUM_POPULATION() + "\r\n" + "Num Generaciones: "
				+ alg.getNUM_GENERATIONS() + "\r\n" + "Atributos conocidos: " + alg.getKNOWN_ATTRIBUTES() + " %"
				+ "\r\n" + "Atributos especiales: " + alg.in.getSPECIAL_ATTRIBUTES() + " %" + "\r\n"
				+ "% Mutación de atributos: " + alg.in.getMUT_PROB_CUSTOMER_PROFILE() + " %" + "\r\n" + "% Crossover: "
				+ alg.getCROSSOVER_PROB() + " %" + "\r\n" + "% Mutación: " + alg.getMUTATION_PROB() + " %" + "\r\n"
				+ "Num grupos de perfil: " + alg.getRESP_PER_GROUP() + "\r\n" + "Num perfiles cercanos: "
				+ alg.getNEAR_CUST_PROFS() + "\r\n" + "Num productos: " + alg.getNumber_Products() + "\r\n"
				+ "Atributos linkados: " + alg.isAttributesLinked() + "\r\n" + "Centroides: "
				+ alg.inWeka.getIndexProfiles().toString() + "\r\n" + "************* RESULTS *************" + "\r\n"
				+ "BestWSC: " + BestWSC + "\r\n" + "Mean: " + meanTXT + "\r\n" + "initMean: " + initMeanTXT + "\r\n"
				+ "stdDev: " + stdDevTXT + "\r\n" + "initStdDev: " + initStdDevTXT + "\r\n" + "percCust: " + percCustTXT
				+ "\r\n" + "initPercCust: " + initPercCustTXT + "\r\n" + "My_priceString: " + priceTXT + "\r\n");
		if (alg.isMaximizar()) {
			jtA.append("percCust: " + percCustTXT + "\r\n" + "initPercCust: " + initPercCustTXT + "\r\n");
		} else {
			jtA.append("percCust: " + percCustTXT + "\r\n");
		}
		alg.out.output(jtA, "Algoritmo Genético");

	}

	/*************************************** " AUXILIARY METHODS SOLVEPD_GA()" ***************************************/

	/**
	 * Crea la población inicial
	 *
	 * @throws Exception
	 */
	@SuppressWarnings("static-access")
	public ArrayList<Object> createInitPopulation() throws Exception {
		ArrayList<Object> Population = new ArrayList<>();
		Fitness = new ArrayList<>();
		BestWSC = new ArrayList<>();

		for (int i = 0; i < alg.Producers.get(alg.MY_PRODUCER).getProducts().size(); i++) {
			Population.add((alg.Producers.get(alg.MY_PRODUCER).getProducts().get(i)).clone());

			if (alg.isMaximizar())
				Fitness.add(alg.computeWSC((Product) Population.get(i), alg.MY_PRODUCER));
			else
				Fitness.add(alg.computeBenefits((Product) Population.get(i), alg.MY_PRODUCER));
		}

		for (int t = 0; t < Fitness.size(); t++)
			BestWSC.add(Fitness.get(t));

		ArrayList<Integer> aux = new ArrayList<>();
		for (int q = 0; q < BestWSC.size(); q++)
			aux.add(BestWSC.get(q));

		alg.Initial_Results.add(aux);

		for (int i = alg.Producers.get(alg.MY_PRODUCER).getProducts().size(); i < alg.getNUM_POPULATION(); i++) {

			for (int j = 0; j < alg.inWeka.getIndexProfiles().size(); j++) {
				alg.createNearProductCluster(alg.Producers.get(alg.MY_PRODUCER).getAvailableAttribute(),
						alg.inWeka.getIndexProfiles().get(j));

			}

			if (i % 2 == 0) /* We create a random product */
				Population.add(alg.createRndProduct(alg.Producers.get(alg.MY_PRODUCER).getAvailableAttribute()));
			else /* We create a near product */
				Population.add(alg.createNearProduct(alg.Producers.get(alg.MY_PRODUCER).getAvailableAttribute(),
						(int) (alg.CustomerProfiles.size() * Math.random()))); ///////// ??verificar//////////

			if (alg.isMaximizar())
				Fitness.add(alg.computeWSC((Product) Population.get(i), alg.MY_PRODUCER));
			else
				Fitness.add(alg.computeBenefits((Product) Population.get(i), alg.MY_PRODUCER));

			int worstIndex = isBetweenBest(Fitness.get(i));
			if (worstIndex != -1) {
				BestWSC.set(worstIndex, Fitness.get(i));
				alg.Producers.get(alg.MY_PRODUCER).getProducts().set(worstIndex, (Product) Population.get(i));
			}
		}

		return Population;
	}

	/**
	 * Metodo que dado un padre y una madre los cruza para obtener un hijo. Para
	 * cada posici�n del array eligiremos aleatoriamente si el hijo heredar� esa
	 * posici�n del padre o de la madre.
	 */
	@SuppressWarnings("static-access")
	protected Object breed(Object father, Object mother) {
		Product son = new Product();
		/* Random value in range [0,100) */
		int crossover = (int) (100 * Math.random());
		int rndVal;

		if (crossover <= CROSSOVER_PROB) {// El hijo sera una mezcla de la madre
											// y el padre
			HashMap<Attribute, Integer> crossover_attributeValue = new HashMap<>();
			for (int i = 0; i < alg.TotalAttributes.size(); i++) { // With son

				rndVal = (int) (2
						* Math.random()); /*
											 * Generamos aleatoriamente un 0
											 * (padre) o un 1 (madre).
											 */
				if (rndVal == 0)
					crossover_attributeValue.put(alg.TotalAttributes.get(i),
							((Product) father).getAttributeValue().get(alg.TotalAttributes.get(i)));
				else
					crossover_attributeValue.put(alg.TotalAttributes.get(i),
							((Product) mother).getAttributeValue().get(alg.TotalAttributes.get(i)));
			}
			son.setAttributeValue(crossover_attributeValue);
		} else {// El hijo seria completamente igual a la madre o al padre
			rndVal = (int) (2 * Math.random()); /*
												 * Generamos aleatoriamente un 0
												 * (padre) o un 1 (madre).
												 */

			if (rndVal == 0)
				son = ((Product) father).clone();
			else
				son = ((Product) mother).clone();
		}
		return son;
	}

	/**
	 * Metodo que crea un parametro individual pasado por la mutación
	 * individual. La mutación consiste en añadir / eliminar una solución
	 * conjunta..
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("static-access")
	protected Object mutate(Object indiv) {
		Product mutant = ((Product) indiv).clone();
		int attrVal = 0;

		for (int j = 0; j < alg.TotalAttributes.size(); j++) {
			/* Random value in range [0,100) */
			double mutation = 100 * Math.random();
			if (mutation <= alg.getMUTATION_PROB()) {
				boolean attrFound = false;
				while (!attrFound) {
					attrVal = (int) (Math.floor(alg.TotalAttributes.get(j).getMAX() * Math.random()));
					if (alg.Producers.get(alg.MY_PRODUCER).getAvailableAttribute().get(j).getAvailableValues()
							.get(attrVal))
						attrFound = true;
				}
				mutant.getAttributeValue().put(alg.TotalAttributes.get(j), attrVal);
			}
		}

		mutant.setPrice(alg.inter.calculatePrice(mutant, alg.getTotalAttributes(), alg.getProducers()));
		return mutant;
	}

	/** Obtener el fitness */
	public Integer getFitness(Object object) throws Exception {
		if (alg.isMaximizar())
			return alg.computeWSC((Product) object, alg.MY_PRODUCER);
		else
			return alg.computeBenefits((Product) object, alg.MY_PRODUCER);
	}
}
