package sa;

import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.JTextArea;
import general.Algorithm;
import general.Attribute;
import general.Product;

public class SimulatedAnnealingProblem extends SimulatedAnnealingAlgorithm {
	private int CHANGE_ATTRIBUTE_PROB = 40;
	private int productIndex = 0;
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

			solveSA();

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
				+ "\r\n" + "TEMPERATURE: " + getTEMPERATURE() + "\r\n" + "coolingRate: " + getCoolingRate() + "\r\n"
				+ "CHANGE_ATTRIBUTE_PROB: " + getCHANGE_ATTRIBUTE_PROB() + " %" + "\r\n" + "Atributos conocidos: "
				+ alg.getKNOWN_ATTRIBUTES() + " %" + "\r\n" + "Atributos especiales: " + alg.in.getSPECIAL_ATTRIBUTES()
				+ " %" + "\r\n" + "% Mutación de atributos: " + alg.in.getMUT_PROB_CUSTOMER_PROFILE() + " %" + "\r\n"
				+ "Num grupos de perfil: " + alg.getRESP_PER_GROUP() + "\r\n" + "Num productos: "
				+ alg.in.getNumber_Products() + "\r\n" + "Atributos linkados: " + alg.isAttributesLinked() + "\r\n"
				+ "Centroides: " + alg.inWeka.getIndexProfiles().toString() + "\r\n"
				+ "************* RESULTS *************" + "\r\n" + "Mean: " + meanTXT + "\r\n" + "initMean: "
				+ initMeanTXT + "\r\n" + "stdDev: " + stdDevTXT + "\r\n" + "initStdDev: " + initStdDevTXT + "\r\n"
				+ "My_priceString: " + priceTXT + "\r\n");
		if (alg.isMaximizar()) {
			jtA.append("percCust: " + percCustTXT + "\r\n" + "initPercCust: " + initPercCustTXT + "\r\n");
		} else {
			jtA.append("percCust: " + percCustTXT + "\r\n");
		}
		alg.out.output(jtA, "Algoritmo Simulated Annealing");

	}

	/**
	 * Resolviendo el problema usando SA
	 */
	@SuppressWarnings("static-access")
	private void solveSA() throws Exception {

		ArrayList<Integer> initial = new ArrayList<>();
		for (int i = 0; i < alg.Producers.get(alg.MY_PRODUCER).getProducts().size(); i++) {
			if (alg.isMaximizar())
				initial.add(computeWSCSA(alg.Producers.get(alg.MY_PRODUCER).getProducts().get(i), alg.MY_PRODUCER, i));
			else
				initial.add(
						computeBenefitsSA(alg.Producers.get(alg.MY_PRODUCER).getProducts().get(i), alg.MY_PRODUCER, i));
		}
		alg.Initial_Results.add(initial);

		for (int i = 0; i < alg.Producers.get(alg.MY_PRODUCER).getProducts().size(); i++) {
			Product BestProductFound = alg.Producers.get(alg.MY_PRODUCER).getProducts().get(i).clone();
			while (getTEMPERATURE() > 1) {

				// Log.d("Temperature", TEMPERATURE + "");
				Product originProduct = alg.Producers.get(alg.MY_PRODUCER).getProducts().get(i);
				HashMap<Attribute, Integer> AUXattributeValue = new HashMap<>();
				for (int q = 0; q < alg.TotalAttributes.size(); q++) {
					AUXattributeValue.put(alg.TotalAttributes.get(q),
							originProduct.getAttributeValue().get(alg.TotalAttributes.get(q)));
				}
				Product new_product = new Product(AUXattributeValue);
				new_product.setPrice(originProduct.getPrice());

				// CHANGE SOME ATTRIBUTES
				for (int j = 0; j < alg.TotalAttributes.size(); j++) {
					if ((Math.random() * 100) < getCHANGE_ATTRIBUTE_PROB()) {
						int new_attr_value = (int) (Math.random() * alg.TotalAttributes.get(j).getMAX());
						new_product.getAttributeValue().put(alg.TotalAttributes.get(j), new_attr_value);
					}
				}

				int old_energy;
				int new_energy;
				if (alg.isMaximizar()) {
					old_energy = computeWSCSA(alg.Producers.get(alg.MY_PRODUCER).getProducts().get(i), alg.MY_PRODUCER,
							i);
					new_energy = computeWSCSA(new_product, alg.MY_PRODUCER, i);
				} else {
					old_energy = computeBenefitsSA(alg.Producers.get(alg.MY_PRODUCER).getProducts().get(i),
							alg.MY_PRODUCER, i);
					new_energy = computeBenefitsSA(new_product, alg.MY_PRODUCER, i);
				}

				// CALCULATE THE ACCEPTED FUNCTION
				if (acceptanceProbability(old_energy, new_energy) > Math.random()) {
					alg.Producers.get(alg.MY_PRODUCER).getProducts().set(i, new_product);

					// ACTUALIZE THE BEST
					if (alg.isMaximizar()) {
						if (computeWSCSA(alg.Producers.get(alg.MY_PRODUCER).getProducts().get(i), alg.MY_PRODUCER,
								i) > computeWSCSA(BestProductFound, alg.MY_PRODUCER, i))
							BestProductFound = alg.Producers.get(alg.MY_PRODUCER).getProducts().get(i);
					} else {
						if (computeBenefitsSA(alg.Producers.get(alg.MY_PRODUCER).getProducts().get(i), alg.MY_PRODUCER,
								i) > computeBenefitsSA(BestProductFound, alg.MY_PRODUCER, i))
							BestProductFound = alg.Producers.get(alg.MY_PRODUCER).getProducts().get(i);
					}
				}

				// Cool system
				TEMPERATURE *= 1 - getCoolingRate();
			}
			alg.Producers.get(alg.MY_PRODUCER).getProducts().set(i, BestProductFound);
			TEMPERATURE = Start_TEMP;
		}

		ArrayList<Integer> result = new ArrayList<>();
		for (int i = 0; i < alg.Producers.get(alg.MY_PRODUCER).getProducts().size(); i++) {
			if (alg.isMaximizar())
				result.add(computeWSCSA(alg.Producers.get(alg.MY_PRODUCER).getProducts().get(i), alg.MY_PRODUCER, i));
			else
				result.add(
						computeBenefitsSA(alg.Producers.get(alg.MY_PRODUCER).getProducts().get(i), alg.MY_PRODUCER, i));
		}
		alg.Results.add(result);
		showWSC();

		// Set prices
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
	 * Calcular la suma del wsc de cada producto
	 * 
	 * @throws Exception
	 */

	@SuppressWarnings("static-access")
	public void showWSC() throws Exception {
		int wsc;
		alg.wscSum = 0;

		for (int i = 0; i < alg.Producers.size(); i++) {
			for (int j = 0; j < alg.Producers.get(i).getProducts().size(); j++) {
				wsc = computeWSCSA(alg.Producers.get(i).getProducts().get(j), i, j);
				alg.wscSum += wsc;
			}
		}
	}

	public int getFitness(Object origin) throws Exception {
		Product p = (Product) origin;

		if (alg.isMaximizar()) {
			return computeWSCSA(p, alg.MY_PRODUCER, productIndex);
		} else {
			return computeBenefitsSA(p, alg.MY_PRODUCER, productIndex);
		}
	}

	@SuppressWarnings("static-access")
	public Object changeObject(Object currency_product) {

		Product originProduct = (Product) currency_product;
		HashMap<Attribute, Integer> AUXattributeValue = new HashMap<>();
		for (int q = 0; q < alg.TotalAttributes.size(); q++) {
			AUXattributeValue.put(alg.TotalAttributes.get(q),
					originProduct.getAttributeValue().get(alg.TotalAttributes.get(q)));
		}
		Product new_product = new Product(AUXattributeValue);
		new_product.setPrice(originProduct.getPrice());

		// CHANGE SOME ATTRIBUTES
		for (int j = 0; j < alg.TotalAttributes.size(); j++) {
			if ((Math.random() * 100) < getCHANGE_ATTRIBUTE_PROB()) {
				int new_attr_value = (int) (Math.random() * alg.TotalAttributes.get(j).getMAX());
				new_product.getAttributeValue().put(alg.TotalAttributes.get(j), new_attr_value);
			}
		}

		return new_product;
	}

	/** Calcular los ingresos */
	public Integer computeBenefitsSA(Product product, int myProducer, int productIndex) throws Exception {
		return computeWSCSA(product, myProducer, productIndex) * product.getPrice();
	}

	/** Calcular WSC de un productor */
	@SuppressWarnings("static-access")
	public int computeWSCSA(Product product, int prodInd, int productIndex) throws Exception {
		int wsc = 0;
		boolean isTheFavourite;
		int meScore, score, k, p, numTies;

		for (int i = 0; i < alg.CustomerProfiles.size(); i++) {
			for (int j = 0; j < alg.CustomerProfiles.get(i).getSubProfiles().size(); j++) {
				isTheFavourite = true;
				numTies = 1;
				meScore = alg.scoreProduct(alg.CustomerProfiles.get(i).getSubProfiles().get(j), product);

				if (alg.isAttributesLinked())
					meScore += alg.scoreLinkedAttributes(alg.CustomerProfiles.get(i).getLinkedAttributes(), product);

				k = 0;
				while (isTheFavourite && k < alg.Producers.size()) {
					p = 0;
					while (isTheFavourite && p < alg.Producers.get(k).getProducts().size()) {
						if (k != alg.MY_PRODUCER || p != productIndex) {

							score = alg.scoreProduct(alg.CustomerProfiles.get(i).getSubProfiles().get(j),
									alg.Producers.get(k).getProducts().get(p));

							if (alg.isAttributesLinked())
								score += alg.scoreLinkedAttributes(alg.CustomerProfiles.get(i).getLinkedAttributes(),
										product);

							if (score > meScore)
								isTheFavourite = false;

							else if (score == meScore)
								numTies += 1;
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
					if ((j == alg.CustomerProfiles.get(i).getSubProfiles().size())
							&& ((alg.CustomerProfiles.get(i).getNumberCustomers() % alg.getRESP_PER_GROUP()) != 0)) {
						wsc += (alg.CustomerProfiles.get(i).getNumberCustomers() % alg.getRESP_PER_GROUP()) / numTies;
					} else {
						wsc += alg.getRESP_PER_GROUP() / numTies;
					}
				}
			}
		}

		return wsc;
	}

	/*************************************** " GETTERS Y SETTERS" ***************************************/

	public double getTEMPERATURE() {
		return TEMPERATURE;
	}

	public double getCoolingRate() {
		return coolingRate;
	}

	public double getStart_TEMP() {
		return Start_TEMP;
	}

	public void setStart_TEMP(double start) {
		Start_TEMP = start;
	}

	public int getCHANGE_ATTRIBUTE_PROB() {
		return CHANGE_ATTRIBUTE_PROB;
	}

	public void setTEMPERATURE(double tEMPERATURE) {
		TEMPERATURE = tEMPERATURE;
	}

	public void setCoolingRate(double coolingRa) {
		coolingRate = coolingRa;
	}

	public void setCHANGE_ATTRIBUTE_PROB(int cHANGE_ATTRIBUTE_PROB) {
		CHANGE_ATTRIBUTE_PROB = cHANGE_ATTRIBUTE_PROB;
	}

}
