package pso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import javax.swing.JTextArea;

import general.Attribute;
import general.CustomerProfile;
import general.Interpolation;
import general.LinkedAttribute;
import general.Producer;
import general.Product;
import genetic.SubProfile;
import input.InputGUI;
import input.InputRandom;
import output.OutputResults;

public class PSOAlgorithm {
	 	static int NUM_EXECUTIONS = 1; /* number of executions */
	    static final int MY_PRODUCER = 0;  //The index of my producer
	    static final int RESP_PER_GROUP = 20; // We divide the respondents of each
	    static final double KNOWN_ATTRIBUTES = 100;
	    private static final int MY_BEST_PRODUCT = 0;
	    public static double VEL_LOW = -1;
		public static double VEL_HIGH = 1;

	    public int MAX_ITERATION = 60;
	    public int SWARM_SIZE = 20;

	    double W_UPPERBOUND = 1;
	    double W_LOWERBOUND = 0;
	    double C1 = 2.0;
	    double C2 = 2.0;

	    private static ArrayList<Attribute> TotalAttributes = new ArrayList<>();
	    private static ArrayList<Producer> Producers = new ArrayList<>();
	    private static ArrayList<CustomerProfile> CustomerProfiles = new ArrayList<>();

	    /* GA VARIABLES */
	    private ArrayList<Integer> BestWSC = new ArrayList<>(); /* Stores the best wsc found */
	    private ArrayList<Product> Population = new ArrayList<>();  //Private mPopu As List(Of List(Of Integer))
	    private ArrayList<Integer> Fitness = new ArrayList<>(); /* * mFitness(i) = wsc of mPopu(i) */

	    private ArrayList<Integer> ParticleBestWSC = new ArrayList<>(); /* Stores the best wsc found */
	    private ArrayList<Product> ProductBestWSC = new ArrayList<>(); /* Stores the best wsc found */
	    private Product BestProduct = new Product(); /* Stores the best wsc found */


	    /* STATISTICAL VARIABLES */
	    private ArrayList<ArrayList<Integer>> Results = new ArrayList<>();
	    private ArrayList<ArrayList<Integer>> Initial_Results = new ArrayList<>();
	    private ArrayList<ArrayList<Integer>> Prices = new ArrayList<>();
	    private int wscSum;
	    public boolean maximizar = false;
	    public static boolean isPSO = false;
		public static boolean isAttributesLinked = false;
		public static int number_Products = 1;
		InputGUI añadir = new InputGUI();
		InputRandom in = new InputRandom();
		OutputResults out = new OutputResults();
		Interpolation inter = new Interpolation();


	    public void start(JTextArea jtA, String archivo, boolean inputFile) throws Exception {
			long inicio = System.currentTimeMillis();
			statisticsPSO(jtA, archivo, inputFile);
			// calculatePrice();
			long tiempo = System.currentTimeMillis() - inicio;

			double elapsedTimeSec = tiempo / 1.0E03;
			jtA.append("\nTiempo de ejecución = " + String.format("%.2f", elapsedTimeSec) + " seconds" + "\n");
		}

	    private void statisticsPSO(JTextArea jtA, String archivo, boolean inputFile) throws Exception {

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

	        if (inputFile) {
				int input = archivo.indexOf(".xml");
				if(input != -1) añadir.inputXML(archivo);
				else añadir.inputTxt(archivo);
				generarDatosGUI();
			} else {
				if (añadir.isGenerarDatosEntrada())
					generarDatosGUI();
				else if (Producers.size() == 0) {
					in.generate();
					TotalAttributes = in.getTotalAttributes();
					Producers = in.getProducers();
					CustomerProfiles = in.getCustomerProfiles();
				}
			}

	        for (int i = 0; i < getNumExecutions(); i++) {
	            if (i != 0) /*We reset myPP and create a new product as the first product*/ {
	            	ArrayList<Product> products = new ArrayList<>();
					for (int k = 0; k < getNumber_Products(); k++)
						products.add(createNearProduct(Producers.get(MY_PRODUCER).getAvailableAttribute(),
								(int) (CustomerProfiles.size() * Math.random())));
					Producers.get(MY_PRODUCER).setProducts(products);
	            }

	            solvePSO();

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
					percCustTXT += String.format("%.2f", percCust);
				else
					percCustTXT += ", " + String.format("%.2f", percCust);

				if (isMaximizar())
					if (i == 0)
						initPercCustTXT += String.format("%.2f", initPercCust);
					else
						initPercCustTXT += ", " + String.format("%.2f", initPercCust);

				if (i == 0)
					priceTXT += String.format("%.2f", priceDoub) + " €";
				else
					priceTXT += ", " + String.format("%.2f", priceDoub) + " €";
			}

	       /* StoredData.mean = sum.get(0) / NUM_EXECUTIONS;
	        StoredData.initMean = initSum.get(0) / NUM_EXECUTIONS;
	        double variance = computeVariance(StoredData.mean);
	        double initVariance = computeVariance(StoredData.initMean);
	        StoredData.stdDev = Math.sqrt(variance);
	        StoredData.initStdDev = Math.sqrt(initVariance);
	        if (StoredData.Fitness == StoredData.Customers) {
	            StoredData.percCust = 100 * StoredData.mean / StoredData.custMean;
	            StoredData.initPercCust = 100 * StoredData.initMean / StoredData.custMean;
	        } else if (StoredData.Fitness == StoredData.Benefits) {
	            StoredData.percCust = (100 * StoredData.mean) / StoredData.initMean;
	        }

	        StoredData.My_price = prices.get(0) / NUM_EXECUTIONS;*/
			jtA.setText("");

	        /*StoredData.meanString = meanTXT;
	        StoredData.initMeanString = initMeanTXT;
	        StoredData.stdDevString = stdDevTXT;
	        StoredData.initStdDevString = initStdDevTXT;
	        StoredData.percCustString = percCustTXT;
	        StoredData.initPercCustString = initPercCustTXT;
	        StoredData.My_priceString = priceTXT;*/
			
			jtA.append("Num Ejecuciones: " + getNumExecutions() + "\r\n" 
					+ "Num atributos: " + TotalAttributes.size() + "\r\n"
					+ "Num productores: " + Producers.size() + "\r\n" 
					+ "Num perfiles: " + CustomerProfiles.size() + "\r\n"
					+ "Number CustProf: " + añadir.getnum() + "\r\n" 
					+ "Máx Iteraciones: " + getMAX_ITERATION() + " %" + "\r\n" 
					+ "Swarm Size: " + getSWARM_SIZE() + " %" + "\r\n"
					+ "W_UPPERBOUND: " + getW_UPPERBOUND() + " %" + "\r\n"
					+ "W_LOWERBOUND: " + getW_LOWERBOUND() + " %" + "\r\n"
					+ "C1: " + getC1() + " %" + "\r\n"
					+ "C2: " + getC2() + " %" + "\r\n"
					+ "Atributos conocidos: " + getKNOWN_ATTRIBUTES() + " %" + "\r\n" 
					+ "Atributos especiales: " + in.getSPECIAL_ATTRIBUTES() + " %" + "\r\n"
					+ "% Mutación de atributos: " + in.getMUT_PROB_CUSTOMER_PROFILE() + " %" + "\r\n" 
					+ "Num grupos de perfil: " + getRESP_PER_GROUP() + "\r\n" 
					+ "Num productos: " + in.getNumber_Products() + "\r\n" 
					+ "Atributos linkados: " + isAttributesLinked() + "\r\n" 
					+ "************* RESULTS *************" + "\r\n"  
					+ "BestWSC: " + BestWSC + "\r\n" 
					 + "Mean: " + meanTXT + "\r\n"
					 + "initMean: " + initMeanTXT + "\r\n"
					 + "stdDev: " + stdDevTXT + "\r\n"
					 + "initStdDev: " + initStdDevTXT + "\r\n"
					 + "percCust: " + percCustTXT + "\r\n"
					 + "initPercCust: " + initPercCustTXT + "\r\n"
					 + "My_priceString: " + priceTXT + "\r\n");
			out.output(jtA, "Algoritmo PSO");
	    }


	    
		/**
		 * Generating the input data
		 *
		 * @throws Exception
		 */
		public void generarDatosGUI() throws Exception {
			TotalAttributes = añadir.getTotalAttributes();
			CustomerProfiles = añadir.getCustomerProfiles();
			añadir.setProfiles();
			añadir.divideCustomerProfile();
			Producers = añadir.getProducers();

		}

	    /**
	     * Creating a random product
	     */
	    private Product createRndProduct(ArrayList<Attribute> availableAttribute) {
	        Product product = new Product(new HashMap<Attribute, Integer>());
	        int limit = (int) (TotalAttributes.size() * KNOWN_ATTRIBUTES / 100);
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

	        //IF WE NEED THE VELOCTY FOR PSO
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
	     * Creating a product near various customer profiles
	     */
	    private Product createNearProduct(ArrayList<Attribute> availableAttribute, int nearCustProfs) {
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

	        //IF WE NEED THE VELOCTY FOR PSO
	        if (isPSO()) {
	            product.setVelocity(new HashMap<Attribute, Double>());
	            for (int i = 0; i < TotalAttributes.size(); i++) {
	                Double velocity = (((VEL_HIGH - VEL_LOW) * Math.random()) + VEL_LOW);
	                product.getVelocity().put(TotalAttributes.get(i), velocity);
	            }
	        }

	        product.setPrice(inter.calculatePrice(product, getTotalAttributes(), getProducers()));
	        return product;
	    }


	    /**
	     * Chosing an attribute near to the customer profiles given
	     */
	    private static int chooseAttribute(int attrInd, ArrayList<Integer> custProfInd, ArrayList<Attribute> availableAttrs) {
	        int attrVal;

	        ArrayList<Integer> possibleAttr = new ArrayList<>();

	        for (int i = 0; i < TotalAttributes.get(attrInd).getMAX(); i++) {
	            /*We count the valoration of each selected profile for attribute attrInd value i*/
	            int possible = 0;
	            for (int j = 0; j < custProfInd.size(); j++) {
	                possible += CustomerProfiles.get(custProfInd.get(j)).getScoreAttributes().get(attrInd).getScoreValues().get(i);
	            }
	            possibleAttr.add(possible);
	        }
	        attrVal = getMaxAttrVal(attrInd, possibleAttr, availableAttrs);

	        return attrVal;
	    }

	    /**
	     * Chosing the attribute with the maximum score for the customer profiles given
	     */
	    private static int getMaxAttrVal(int attrInd, ArrayList<Integer> possibleAttr, ArrayList<Attribute> availableAttr) {

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

	    private void solvePSO() {

	        ProductBestWSC = new ArrayList<>();
	        ParticleBestWSC = new ArrayList<>();

	        createInitSwarm();

	        for (int i = 0; i < SWARM_SIZE; i++) {
	            ParticleBestWSC.add(Fitness.get(i));
	            ProductBestWSC.add(Population.get(i));
	        }

	        double w;
	        for (int i = 0; i < MAX_ITERATION; i++) {

	            // STEP 1 - UPDATE PARTICLE'S BEST
	            for (int j = 0; j < SWARM_SIZE; j++) {
	                if (Fitness.get(j) > ParticleBestWSC.get(j)) {
	                    ParticleBestWSC.set(j, Fitness.get(j));
	                    ProductBestWSC.set(j, Population.get(j));
	                }
	            }

	            // STEP 2 - UPDATE GENERAL BEST
	            for(int j = 0; j < Fitness.size(); j++){
	                int worstIndex = isBetweenBest(Fitness.get(j));
	                if(worstIndex != -1){
	                    BestWSC.set(worstIndex, Fitness.get(j));
	                    Producers.get(MY_PRODUCER).getProducts().set(worstIndex, Population.get(i));
	                }
	            }

	            w = W_UPPERBOUND - (((double) i) / MAX_ITERATION) * (W_UPPERBOUND - W_LOWERBOUND);

	            for(int k = 0; k < SWARM_SIZE; k++){

	                Random generator = new Random();

	                double r1 = generator.nextDouble();
	                double r2 = generator.nextDouble();

	                Product product = Population.get(k);

	                for(int p = 0; p < TotalAttributes.size(); p++){

	                    //STEP 3 - UPDATE VELOCITY
	                    double vel = (w * product.getVelocity().get(TotalAttributes.get(p))) +
	                            (r1 * C1) * (ProductBestWSC.get(k).getAttributeValue().get(TotalAttributes.get(p)) - product.getAttributeValue().get(TotalAttributes.get(p))) +
	                            (r2 * C2) * (Producers.get(MY_PRODUCER).getProducts().get(MY_BEST_PRODUCT).getAttributeValue().get(TotalAttributes.get(p)) - product.getAttributeValue().get(TotalAttributes.get(p)));

	                    product.getVelocity().put(TotalAttributes.get(p), vel);


	                    //STEP 4 - UPDATE LOCATION
	                    product.getAttributeValue().put(TotalAttributes.get(p), (int) (product.getAttributeValue().get(TotalAttributes.get(p)) + product.getVelocity().get(TotalAttributes.get(p))));
	                }
	            }

	            //TODO Update Fitness
	            updateFitness();
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

	    private void updateFitness() {
	        for(int i = 0; i < Population.size(); i++){

	            int ParticleFitness = 0;

	            if (isMaximizar())
	                ParticleFitness = computeWSC(Population.get(i), MY_PRODUCER);
	            else
	                ParticleFitness = computeBenefits(Population.get(i), MY_PRODUCER);

	            if(ParticleFitness > Fitness.get(i)){
	                Fitness.set(i, ParticleFitness);
	            }
	        }
	    }

	    private void createInitSwarm() {
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

	        for (int i = Producers.get(MY_PRODUCER).getProducts().size(); i < SWARM_SIZE; i++) {


	            if (i % 2 == 0) /*We create a random product*/
	                Population.add(createRndProduct(Producers.get(MY_PRODUCER).getAvailableAttribute()));
	            else /*We create a near product*/
	                Population.add(createNearProduct(Producers.get(MY_PRODUCER).getAvailableAttribute(), (int) (CustomerProfiles.size() * Math.random())));  /////////??verificar//////////


	            if (isMaximizar())
	                Fitness.add(computeWSC(Population.get(i), MY_PRODUCER));
	            else
	                Fitness.add(computeBenefits(Population.get(i), MY_PRODUCER));


	            int worstIndex = isBetweenBest(Fitness.get(i));
	            if (worstIndex != -1) {
	                BestWSC.set(worstIndex, Fitness.get(i));
	                Producers.get(MY_PRODUCER).getProducts().set(worstIndex, Population.get(i));
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

	    private Integer computeBenefits(Product product, int myProducer) {
	        return computeWSC(product, myProducer) * product.getPrice();
	    }

	    /***
	     * Computing the weighted score of the producer
	     * prodInd is the index of the producer
	     *
	     * @throws Exception
	     **/
	    private int computeWSC(Product product, int prodInd) {
	        int wsc = 0;
	        boolean isTheFavourite;
	        int meScore, score, k, p, numTies;
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

	                            score = scoreProduct(CustomerProfiles.get(i).getSubProfiles().get(j), Producers.get(k).getProducts().get(p));

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
	                /* When there exists ties we loose some voters because of decimals (undecided voters)*/
	                if (isTheFavourite) {
	                    if ((j == CustomerProfiles.get(i).getSubProfiles().size()) && ((CustomerProfiles.get(i).getNumberCustomers() % RESP_PER_GROUP) != 0)) {
	                        wsc += (CustomerProfiles.get(i).getNumberCustomers() % RESP_PER_GROUP) / numTies;
	                    } else {
	                        wsc += RESP_PER_GROUP / numTies;
	                    }
	                }
	            }
	        }

	        return wsc;
	    }

	    private int scoreLinkedAttributes(ArrayList<LinkedAttribute> linkedAttributes, Product product) {
	        int modifyScore = 0;
	        for (int i = 0; i < linkedAttributes.size(); i++) {
	            LinkedAttribute link = linkedAttributes.get(i);
	            if (product.getAttributeValue().get(link.getAttribute1()) == link.getValue1() && product.getAttributeValue().get(link.getAttribute2()) == link.getValue2()) {
	                modifyScore += link.getScoreModification();
	            }
	        }
	        return modifyScore;
	    }


	    /**
	     * Computing the score of a product given the customer profile index
	     * custProfInd and the product
	     */
	    private int scoreProduct(SubProfile subprofile, Product product) {
	        int score = 0;
	        for (int i = 0; i < TotalAttributes.size(); i++) {
	            score += scoreAttribute(TotalAttributes.get(i).getMAX(), subprofile.getValueChosen().get(TotalAttributes.get(i)), product.getAttributeValue().get(TotalAttributes.get(i)));
	            // score += scoreAttribute(mAttributes(i), mCustProfAux(custProfInd)(custSubProfInd)(i), product(i))
	        }
	        return score;
	    }

	    /**
	     * Computing the score of an attribute for a product given the
	     * ' number of values
	     */
	    private int scoreAttribute(int numOfValsOfAttr, int valOfAttrCust, int valOfAttrProd) {
	        int score = 0;
	        switch (numOfValsOfAttr) {
	            case 2: {
	                if (valOfAttrCust == valOfAttrProd) score = 10;
	                else score = 0;
	            }
	            break;
	            case 3: {
	                if (valOfAttrCust == valOfAttrProd) score = 10;
	                else if (Math.abs(valOfAttrCust - valOfAttrProd) == 1) score = 5;
	                else score = 0;
	            }
	            break;
	            case 4: {
	                if (valOfAttrCust == valOfAttrProd) score = 10;
	                else if (Math.abs(valOfAttrCust - valOfAttrProd) == 1) score = 6;
	                else if (Math.abs(valOfAttrCust - valOfAttrProd) == 2) score = 2;
	                else score = 0;
	            }
	            break;
	            case 5: {
	                if (valOfAttrCust == valOfAttrProd) score = 10;
	                else if (Math.abs(valOfAttrCust - valOfAttrProd) == 1) score = 6;
	                else if (Math.abs(valOfAttrCust - valOfAttrProd) == 2) score = 2;
	                else if (Math.abs(valOfAttrCust - valOfAttrProd) == 3) score = 1;
	                else score = 0;
	            }
	            break;
	            case 11: {
	                if (valOfAttrCust == valOfAttrProd) score = 10;
	                else if (Math.abs(valOfAttrCust - valOfAttrProd) == 1) score = 8;
	                else if (Math.abs(valOfAttrCust - valOfAttrProd) == 2) score = 6;
	                else if (Math.abs(valOfAttrCust - valOfAttrProd) == 3) score = 4;
	                else if (Math.abs(valOfAttrCust - valOfAttrProd) == 4) score = 2;
	                else score = 0;
	            }
	            break;
	        }
	        return score;
	    }


	    /*************************************** " AUXILIARY METHODS STATISTICSPD()" ***************************************/

	    /**
	     * Computing the variance
	     */
	    private double computeVariance(double mean) {//TODO me fijo solo en el primero
	        double sqrSum = 0;
	        for (int i = 0; i < NUM_EXECUTIONS; i++) {
	            sqrSum += Math.pow(Results.get(i).get(0) - mean, 2);
	        }
	        return (sqrSum / NUM_EXECUTIONS);
	    }

	    /***************************************
	     * " AUXILIARY METHODS TO CALCULATE THE PRICE"
	     ***************************************/
/*
	    private int calculatePrice(Product product) {//TODO me fijo solo en el primero
	        int price_MyProduct = 0;

	        for (int i = 1; i < Producers.size(); i++) {
	            Product prod_competence = Producers.get(i).getProducts().get(0);
	            double distance_product = getDistanceTo(product, prod_competence);

	            if (distance_product == 0) {
	                price_MyProduct = prod_competence.getPrice();
	                break;
	            }

	            price_MyProduct += prod_competence.getPrice() / distance_product;
	        }

	        return price_MyProduct;
	    }

	    private double getDistanceTo(Product my_product, Product prod_competence) {
	        double distance = 0;
	        for (int i = 0; i < TotalAttributes.size(); i++) {
	            distance += Math.pow(my_product.getAttributeValue().get(TotalAttributes.get(i)) - prod_competence.getAttributeValue().get(TotalAttributes.get(i)), 2);
	        }
	        distance = Math.sqrt(distance);
	        return distance;
	    }
*/
	    /**
	     * Showing the wsc of the rest of products
	     *
	     * @throws Exception
	     */

	    private void showWSC() {
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
	    public void showAttributes(JTextArea jTextArea) {
			jTextArea.setText("");
			for (int k = 0; k < TotalAttributes.size(); k++) {
				jTextArea.append(TotalAttributes.get(k).getName() + "\n" + "MIN: " + TotalAttributes.get(k).getMIN() + "\n"
						+ "MAX: " + TotalAttributes.get(k).getMAX() + "\n");
			}
			jTextArea.repaint();
		}

		public void showCustomerProfile(JTextArea jTextArea) {
			jTextArea.setText("");
			for (int i = 0; i < CustomerProfiles.size(); i++) {
				CustomerProfile cp = CustomerProfiles.get(i);
				jTextArea.append("CUSTOMER PROFILE " + (i + 1) + "\n");
				for (int j = 0; j < cp.getScoreAttributes().size(); j++) {
					jTextArea.append(cp.getScoreAttributes().get(j).getName() + "\n");
					for (int z = 0; z < cp.getScoreAttributes().get(j).getScoreValues().size(); z++) {
						jTextArea.append("Value -> " + cp.getScoreAttributes().get(j).getScoreValues().get(z) + "\n");
					}
				}
			}
			jTextArea.repaint();
		}

		public void showSubProfile(JTextArea jTextArea) {
			jTextArea.setText("");
			for (int i = 0; i < CustomerProfiles.size(); i++) {
				CustomerProfile cp = CustomerProfiles.get(i);
				jTextArea.append("CUSTOMER PROFILE " + (i + 1) + "\n");
				for (int j = 0; j < cp.getSubProfiles().size(); j++) {
					jTextArea.append(cp.getSubProfiles().get(j).getName() + "\n");
					for (int z = 0; z < cp.getSubProfiles().get(j).getValueChosen().size(); z++) {
						jTextArea.append("Value -> "
								+ cp.getSubProfiles().get(j).getValueChosen().get(TotalAttributes.get(z)) + "\n");

					}
				}
			}
			jTextArea.repaint();
		}

		public void showProducers(JTextArea jTextArea) {
			jTextArea.setText("");
			for (int i = 0; i < Producers.size(); i++) {
				Producer p = Producers.get(i);
				for(int k = 0; k < p.getProducts().size(); k++){
					for (int j = 0; j < p.getAvailableAttribute().size(); j++) {
						jTextArea.append("PRODUCTOR " + (i + 1) + "\n");
						jTextArea.append("Producto " + (k + 1) + " ");
						jTextArea.append(p.getAvailableAttribute().get(j).getName() + " " + "Value -> "
								+ p.getProducts().get(k).getAttributeValue().get(TotalAttributes.get(j)) + "\n");
		
					}
				}
			}
			jTextArea.repaint();
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
		public int getRESP_PER_GROUP() {
			return RESP_PER_GROUP;
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

		public void setAttributesLinked(boolean AttributesLinked) {
			isAttributesLinked = AttributesLinked;
		}
		
		public int getNumber_Products() {
			return number_Products;
		}

		public void setNumber_Products(int number) {
			number_Products = number;
		}

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

		public boolean isPSO() {
			return isPSO;
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

		public void setPSO(boolean ispso) {
			this.isPSO = ispso;
		}

		public int getMAX_ITERATION() {
			return MAX_ITERATION;
		}

		public void setMAX_ITERATION(int mAX_ITERATION) {
			MAX_ITERATION = mAX_ITERATION;
		}
}
