package minimax;

import java.util.ArrayList;
import javax.swing.JTextArea;

import general.Algorithm;
import general.CustomerProfile;
import general.Product;

public class MinimaxProblem extends MinimaxAlgorithm {

	private int mNAttrMod = 1; // Number of attributes the producer can modify
								// (D)
	protected ArrayList<Integer> mInitial_Results = new ArrayList<>();
	protected ArrayList<Integer> mResults = new ArrayList<>();
	protected ArrayList<Integer> mPrices = new ArrayList<>();
	Algorithm alg = new Algorithm();

	/** Método que empieza con la ejecución */
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
	public void statisticsAlgorithm(JTextArea jtA, String archivo, boolean inputFile) throws Exception {
		double mean, initMean;
		double sum = 0; /* sum of customers achieved */
		int sumCust = 0; /* sum of the total number of customers */
		double custMean;
		double variance;
		int initSum = 0;
		double stdDev;
		double percCust; /* % of customers achieved */
		int price = 0;
		double My_price;
		double initPercCust; /* % of initial customers achieved */

		mResults = new ArrayList<>();
		mInitial_Results = new ArrayList<>();
		mPrices = new ArrayList<>();

		if (inputFile) {
			int input = archivo.indexOf(".xml");
			if (input != -1)
				alg.inputGUI.inputXML(archivo);
			else
				alg.inputGUI.inputTxt(archivo);
			alg.generarDatosGUI();
		}

		for (int i = 0; i < alg.getNumExecutions(); i++) {
			playPDG(archivo, inputFile);
			sum += mResults.get(i);
			initSum = mInitial_Results.get(i);
			sumCust += countCustomers() * getmNTurns() * 2;
			price += mPrices.get(i);
		}

		mean = sum / alg.getNumExecutions();
		initMean = initSum / alg.getNumExecutions();
		variance = computeVariance(mean);
		stdDev = Math.sqrt(variance);
		custMean = sumCust / alg.getNumExecutions();
		percCust = 100 * mean / custMean;
		initPercCust = 100 * initMean / custMean;
		My_price = price / alg.getNumExecutions();

		jtA.setText("");
		jtA.append("Num Ejecuciones: " + alg.getNumExecutions() + "\r\n" + "Num atributos: "
				+ alg.getTotalAttributes().size() + "\r\n" + "Num productores: " + alg.getProducers().size() + "\r\n"
				+ "Num perfiles: " + alg.getCustomerProfiles().size() + "\r\n" + "Number CustProf: "
				+ alg.inputGUI.getnum() + "\r\n" + "Depth Prod 0: " + getMAX_DEPTH_0() + "\r\n" + "Depth Prod 1: "
				+ getMAX_DEPTH_1() + "\r\n" + "Num atributos modificables: " + getmNAttrMod() + "\r\n"
				+ "Num turnos previos: " + getmPrevTurns() + "\r\n" + "Num productos: " + alg.in.getNumber_Products()
				+ "\r\n" + "Atributos linkados: " + alg.isAttributesLinked() + "\r\n"
				+ "************* RESULTS *************" + "\r\n" + "Num turnos: " + getmNTurns() + "\r\n" + "Mean: "
				+ String.format("%.2f", mean) + "\r\n" + "stdDev: " + String.format("%.2f", stdDev) + "\r\n"
				+ "custMean: " + String.format("%.2f", custMean) + "\r\n" + "Price: " + String.format("%.2f", My_price)
				+ " €" + "\r\n");
		if (alg.isMaximizar()) {
			jtA.append("percCust: " + String.format("%.2f", percCust) + " %" + "\r\n" + "initPercCust: "
					+ String.format("%.2f", initPercCust) + " %" + "\r\n");
		} else {
			jtA.append("percCust: " + String.format("%.2f", ((100 * mean) / initMean)) + " %" + "\r\n");
		}
		alg.out.output(jtA, "Algoritmo Minimax");
	}

	/**
	 * Resolviendo el problema PDG usando Minimax
	 */
	@SuppressWarnings("static-access")
	public void playPDG(String archivo, boolean inputFile) throws Exception {
		if (!inputFile) {
			if (alg.inputGUI.isGenerarDatosEntrada())
				alg.generarDatosGUI();
			else {
				alg.in.generateInput();
				alg.TotalAttributes = alg.in.getTotalAttributes();
				alg.Producers = alg.in.getProducers();
				alg.CustomerProfiles = alg.in.getCustomerProfiles();
			}
		}
		playGame();
	}

	/** Método que empieza el juego */
	@SuppressWarnings("static-access")
	public void playGame() throws Exception {
		if (!alg.isMaximizar())
			mInitial_Results.add(computeBenefits(alg.Producers.get(alg.MY_PRODUCER).getProduct(), alg.MY_PRODUCER));
		else
			mInitial_Results.add(computeWSC(alg.Producers.get(alg.MY_PRODUCER).getProduct(), alg.MY_PRODUCER));

		playMinimaxAlgorithm();

		if (!alg.isMaximizar())
			mResults.add(alg.Producers.get(alg.MY_PRODUCER).getNumber_CustomerGathered() * alg.inter.calculatePrice(
					alg.Producers.get(alg.MY_PRODUCER).getProduct(), alg.getTotalAttributes(), alg.getProducers()));
		else
			mResults.add(alg.Producers.get(alg.MY_PRODUCER).getNumber_CustomerGathered());

		mPrices.add(alg.inter.calculatePrice(alg.Producers.get(alg.MY_PRODUCER).getProduct(), alg.getTotalAttributes(),
				alg.getProducers()));
	}

	/** Calcular ingresos */
	public Integer computeBenefits(Product product, int myProducer) throws Exception {
		return computeWSC(product, myProducer) * product.getPrice();
	}

	/***
	 * / * El cálculo de la puntuación ponderada del productor
	 * 
	 * @throws Exception
	 **/
	@SuppressWarnings("static-access")
	public int computeWSC(Product product, int prodInd) throws Exception {
		int wsc = 0;
		boolean isTheFavourite;
		int meScore, score, k, numTies;

		for (int i = 0; i < alg.CustomerProfiles.size(); i++) {
			isTheFavourite = true;
			numTies = 1;
			meScore = scoreProduct(alg.CustomerProfiles.get(i), product);

			if (alg.isAttributesLinked())
				meScore += alg.scoreLinkedAttributes(alg.CustomerProfiles.get(i).getLinkedAttributes(), product);

			k = 0;
			while (isTheFavourite && k < alg.Producers.size()) {
				if (k != prodInd) {

					score = scoreProduct(alg.CustomerProfiles.get(i), alg.Producers.get(k).getProduct());

					if (alg.isAttributesLinked())
						score += alg.scoreLinkedAttributes(alg.CustomerProfiles.get(i).getLinkedAttributes(), product);

					if (score > meScore)
						isTheFavourite = false;

					else if (score == meScore)
						numTies += 1;
				}
				k++;
			}

			if (isTheFavourite)
				wsc += alg.CustomerProfiles.get(i).getNumberCustomers() / numTies;
		}

		return wsc;
	}

	/**
	 * Calcular la puntuacion de un producto para cada perfil
	 */
	@SuppressWarnings("static-access")
	private int scoreProduct(CustomerProfile profile, Product product) throws Exception {
		int score = 0;
		for (int i = 0; i < alg.TotalAttributes.size(); i++)
			score += profile.getScoreAttributes().get(i).getScoreValues()
					.get(product.getAttributeValue().get(alg.TotalAttributes.get(i)));

		return score;
	}

	/** Contabilizar el número de perfiles */
	@SuppressWarnings("static-access")
	public int countCustomers() {
		int total = 0;
		for (int i = 0; i < alg.CustomerProfiles.size(); i++)
			total += alg.CustomerProfiles.get(i).getNumberCustomers();

		return total;
	}

	/**
	 * Calcular la varianza
	 */
	public double computeVariance(double mean) {
		double sqrSum = 0;
		for (int i = 0; i < alg.getNumExecutions(); i++) {
			sqrSum += Math.pow(mResults.get(i) - mean, 2);
		}
		return (sqrSum / alg.getNumExecutions());
	}

	@SuppressWarnings("static-access")
	public Object getObject(int playerIndex) {
		return alg.Producers.get(playerIndex).getProduct();
	}

	@SuppressWarnings("static-access")
	public int getDimens() {
		return alg.TotalAttributes.size();
	}

	@SuppressWarnings("static-access")
	public int getSolutionsSpace(int dimen) {
		return alg.TotalAttributes.get(dimen).getMAX();
	}

	@SuppressWarnings("static-access")
	@Override
	public int getSolution(int playerIndex, int dimension) {
		return alg.Producers.get(playerIndex).getProduct().getAttributeValue().get(alg.TotalAttributes.get(dimension));
	}

	@SuppressWarnings("static-access")
	@Override
	public boolean isPosibleToChange(int playerIndex, int dimension, int attrVal) {
		return alg.Producers.get(playerIndex).getAvailableAttribute().get(dimension).getAvailableValues().get(attrVal);
	}

	@SuppressWarnings("static-access")
	@Override
	public void changeChild(Object o, int dimension, int solutionSpaceIndex) {
		((Product) o).getAttributeValue().put(alg.TotalAttributes.get(dimension), solutionSpaceIndex);
	}

	@SuppressWarnings("static-access")
	@Override
	public void setSolution(int playerIndex, int dimension, int solution) {
		alg.Producers.get(playerIndex).getProduct().getAttributeValue().put(alg.TotalAttributes.get(dimension),
				solution);
	}

	/** MÉTODOS GETTERS Y SETTERS */

	public int getMAX_DEPTH_0() {
		return MAX_DEPTH_0;
	}

	public int getMAX_DEPTH_1() {
		return MAX_DEPTH_1;
	}

	public int getmPrevTurns() {
		return mPrevTurns;
	}

	public int getmNTurns() {
		return mNTurns;
	}

	public void setMAX_DEPTH_0(int mAX_DEPTH_0) {
		MAX_DEPTH_0 = mAX_DEPTH_0;
	}

	public void setMAX_DEPTH_1(int mAX_DEPTH_1) {
		MAX_DEPTH_1 = mAX_DEPTH_1;
	}

	public void setmPrevTurns(int mPrevTu) {
		mPrevTurns = mPrevTu;
	}

	public void setmNTurns(int mNTu) {
		mNTurns = mNTu;
	}

	public int getmNAttrMod() {
		return mNAttrMod;
	}

	public void setmNAttrMod(int mNAttrMod) {
		this.mNAttrMod = mNAttrMod;
	}

	@SuppressWarnings("static-access")
	public void setFitnessAcumulated(int i, ArrayList<Integer> customersAcumulated) {
		alg.Producers.get(i).setCustomersGathered(customersAcumulated);
	}

	@Override
	public Integer getFitness(Object object, int index) throws Exception {
		if (!alg.isMaximizar())
			((Product) object)
					.setPrice(alg.inter.calculatePrice((Product) object, alg.getTotalAttributes(), alg.getProducers()));

		if (!alg.isMaximizar())
			return computeBenefits((Product) object, index);
		else
			return computeWSC((Product) object, index);
	}

}
