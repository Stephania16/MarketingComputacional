package pso;

import java.util.ArrayList;
import javax.swing.JTextArea;

import general.Algorithm;
import general.Product;

public class ParticleSwarmOptimizationProblem extends ParticleSwarmOptimizationAlgorithm {

	private ArrayList<Product> Population = new ArrayList<>();
	private ArrayList<Integer> BestWSC = new ArrayList<>();
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
	 * Genera las estadísticas
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

			solvePSO();

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
				+ "\r\n" + "Máx Iteraciones: " + getMAX_ITERATION() + "\r\n" + "Swarm Size: " + getSWARM_SIZE() + "\r\n"
				+ "W_UPPERBOUND: " + getW_UPPERBOUND() + " %" + "\r\n" + "W_LOWERBOUND: " + getW_LOWERBOUND() + " %"
				+ "\r\n" + "C1: " + getC1() + " %" + "\r\n" + "C2: " + getC2() + " %" + "\r\n" + "Atributos conocidos: "
				+ alg.getKNOWN_ATTRIBUTES() + " %" + "\r\n" + "Atributos especiales: " + alg.in.getSPECIAL_ATTRIBUTES()
				+ " %" + "\r\n" + "% Mutación de atributos: " + alg.in.getMUT_PROB_CUSTOMER_PROFILE() + " %" + "\r\n"
				+ "Num grupos de perfil: " + alg.getRESP_PER_GROUP() + "\r\n" + "Num productos: "
				+ alg.in.getNumber_Products() + "\r\n" + "Atributos linkados: " + alg.isAttributesLinked() + "\r\n"
				+ "Centroides: " + alg.inWeka.getIndexProfiles().toString() + "\r\n"
				+ "************* RESULTS *************" + "\r\n" + "BestWSC: " + BestWSC + "\r\n" + "Mean: " + meanTXT
				+ "\r\n" + "initMean: " + initMeanTXT + "\r\n" + "stdDev: " + stdDevTXT + "\r\n" + "initStdDev: "
				+ initStdDevTXT + "\r\n" + "My_priceString: " + priceTXT + "\r\n");
		if (alg.isMaximizar()) {
			jtA.append("percCust: " + percCustTXT + "\r\n" + "initPercCust: " + initPercCustTXT + "\r\n");
		} else {
			jtA.append("percCust: " + percCustTXT + "\r\n");
		}
		alg.out.output(jtA, "Algoritmo PSO");
	}

	/**
	 * Resolviendo el problema usando PSO
	 */
	@SuppressWarnings("static-access")
	private void solvePSO() throws Exception {
		Population = new ArrayList<>();
		ArrayList<Object> finalPupolation = solvePSOAlgorithm();

		for (int i = 0; i < finalPupolation.size(); i++) {
			Population.add((Product) finalPupolation.get(i));
			Fitness.set(i, getFitness(Population.get(i)));
		}

		// STEP 2 - UPDATE GENERAL BEST
		for (int j = 0; j < Fitness.size(); j++) {
			int worstIndex = isBetweenBest(Fitness.get(j));
			if (worstIndex != -1) {
				BestWSC.set(worstIndex, Fitness.get(j));
				alg.Producers.get(alg.MY_PRODUCER).getProducts().set(worstIndex, Population.get(j));
			}
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

	@SuppressWarnings("static-access")
	/** Crear el enjambre inicial */
	public ArrayList<Object> createInitSwarm() throws Exception {
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

		for (int i = alg.Producers.get(alg.MY_PRODUCER).getProducts().size(); i < getSWARM_SIZE(); i++) {
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

	/** Calcular el valor máximo entre fitness o WSC */
	private int isBetweenBest(int fitness) {
		for (int i = 0; i < BestWSC.size(); i++) {
			if (fitness > BestWSC.get(i))
				return i;
		}
		return -1;
	}

	/*************************************** " GETTERS Y SETTERS" ***************************************/

	public int getSWARM_SIZE() {
		return SWARM_SIZE;
	}

	public double getW_UPPERBOUND() {
		return W_UPPERBOUND;
	}

	public double getW_LOWERBOUND() {
		return W_LOWERBOUND;
	}

	public double getC1() {
		return C1;
	}

	public double getC2() {
		return C2;
	}

	public void setSWARM_SIZE(int sWARM_SIZE) {
		SWARM_SIZE = sWARM_SIZE;
	}

	public void setW_UPPERBOUND(double w_UPPERBOUND) {
		W_UPPERBOUND = w_UPPERBOUND;
	}

	public void setW_LOWERBOUND(double w_LOWERBOUND) {
		W_LOWERBOUND = w_LOWERBOUND;
	}

	public void setC1(double c1) {
		C1 = c1;
	}

	public void setC2(double c2) {
		C2 = c2;
	}

	public int getMAX_ITERATION() {
		return MAX_ITERATION;
	}

	public void setMAX_ITERATION(int mAX_ITERATION) {
		MAX_ITERATION = mAX_ITERATION;
	}

	@SuppressWarnings("static-access")
	public int getDimensions() {
		return alg.TotalAttributes.size();
	}

	@SuppressWarnings("static-access")
	public Integer getLocationValue(Object obj, int dimen) {
		return ((Product) obj).getAttributeValue().get(alg.TotalAttributes.get(dimen));
	}

	@SuppressWarnings("static-access")

	public void updateLocation(Object obj, int dimen, int new_value_for_location) {
		((Product) obj).getAttributeValue().put(alg.TotalAttributes.get(dimen), new_value_for_location);
	}

	public Integer getFitness(Object object) throws Exception {
		if (alg.isMaximizar())
			return alg.computeWSC((Product) object, alg.MY_PRODUCER);
		else
			return alg.computeBenefits((Product) object, alg.MY_PRODUCER);
	}

}
