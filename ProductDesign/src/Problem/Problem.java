package Problem;

import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JTextArea;

import general.Attribute;
import general.CustomerProfile;
import general.Interpolation;
import general.LinkedAttribute;
import general.Producer;
import general.Product;
import general.StoredData;
import genetic.SubProfile;
import input.InputGUI;
import input.InputRandom;
import input.InputWeka;

public abstract class Problem {
    static double KNOWN_ATTRIBUTES = 100;
    static int CROSSOVER_PROB = 80; /* % of crossover */
    static int MUTATION_PROB = 1; /* % of mutation */
    static int NUM_POPULATION = 20; /* number of population */
    static int RESP_PER_GROUP = 20; /* * We divide the respondents of each
                                             * profile in groups of
											 * RESP_PER_GROUP respondents*/

    static int NUM_EXECUTIONS = 20; /* number of executions */
    private static int EXECUTION = 0;

    static final int MY_PRODUCER = 0;  //The index of my producer


    private int mNTurns = 5; // Number of turns to play (tf)

    private static ArrayList<CustomerProfile> CustomerProfiles = new ArrayList<>();
    private static ArrayList<Attribute> TotalAttributes = new ArrayList<>();
    private static ArrayList<Producer> Producers = new ArrayList<>();

    /* STATISTICAL VARIABLES */
    private ArrayList<ArrayList<Integer>> Results = new ArrayList<>();
    private ArrayList<ArrayList<Integer>> Initial_Results = new ArrayList<>();
    private ArrayList<ArrayList<Integer>> Prices = new ArrayList<>();
    private int wscSum;

    /* GA VARIABLES */
    private ArrayList<Integer> BestWSC = new ArrayList<>(); /* Stores the best wsc found */
    private ArrayList<Product> Population = new ArrayList<>();  //Private mPopu As List(Of List(Of Integer))
    private ArrayList<Integer> Fitness = new ArrayList<>(); /* * mFitness(i) = wsc of mPopu(i) */
    public Interpolation inter = new Interpolation();
    public InputGUI inputGUI = new InputGUI();
    public InputRandom in = new InputRandom();
	public InputWeka inWeka = new InputWeka();
    public static boolean maximizar = false;
    public static boolean isAlgMin = false;
	//public static boolean isAttributesLinked = false;
	//public static int number_Products = 1;
    private int CHANGE_ATTRIBUTE_PROB = 40;

    /****************************************************************************************************
     * GENERAL                                              *
     ****************************************************************************************************/


    public void start(JTextArea jtA, String archivo, boolean inputFile) throws Exception {

    	long inicio = System.currentTimeMillis();
		statisticsAlgorithm(jtA, archivo, inputFile);
		long tiempo = System.currentTimeMillis() - inicio;

		double elapsedTimeSec = tiempo / 1.0E03;
		jtA.append("\nTiempo de ejecución = " + String.format("%.2f", elapsedTimeSec) + " seconds" + "\n");

    }
    private void statisticsAlgorithm(JTextArea jtA, String archivo, boolean inputFile) throws Exception {

        ArrayList<Double> sum = new ArrayList<>();
        ArrayList<Double> initSum = new ArrayList<>();
        ArrayList<Integer> prices = new ArrayList<>();
        int sumCust = 0;

        for (int i = 0; i < StoredData.number_Products; i++) {
            sum.add((double) 0);
            initSum.add((double) 0);
            prices.add(0);
        }

        Results = new ArrayList<>();
        Initial_Results = new ArrayList<>();
        Prices = new ArrayList<>();

        inputData(inputFile, archivo);

        for (EXECUTION = 0; EXECUTION < NUM_EXECUTIONS; EXECUTION++) {

            startProblem(jtA);

            ArrayList<Double> auxSum = new ArrayList<>();
            ArrayList<Double> auxinitSum = new ArrayList<>();
            ArrayList<Integer> auxprice = new ArrayList<>();
            for (int k = 0; k < StoredData.number_Products; k++) {
                auxSum.add(sum.get(k) + Results.get(EXECUTION).get(k));
                auxinitSum.add(initSum.get(k) + Initial_Results.get(EXECUTION).get(k));
                auxprice.add(prices.get(k) + Prices.get(EXECUTION).get(k));
            }

            sum = auxSum;
            initSum = auxinitSum;
            prices = auxprice;

            sumCust += wscSum;
            if (isAlgMin())
                sumCust += countCustomers() * mNTurns * 2;
        }

        String meanTXT = "";
        String initMeanTXT = "";
        String stdDevTXT = "";
        String initStdDevTXT = "";
        String percCustTXT = "";
        String initPercCustTXT = "";
        String priceTXT = "";

        StoredData.custMean = sumCust / NUM_EXECUTIONS;

        for (int i = 0; i < StoredData.number_Products; i++) {

            double mean = sum.get(i) / NUM_EXECUTIONS;
            double initMean = initSum.get(i) / NUM_EXECUTIONS;
            double variance = computeVariance(mean);
            double initVariance = computeVariance(initMean);
            double stdDev = Math.sqrt(variance);
            double initStdDev = Math.sqrt(initVariance);
            double percCust;
            double initPercCust = -1;
            if (isMaximizar()) {
                percCust = 100 * mean / StoredData.custMean;
                initPercCust = 100 * initMean / StoredData.custMean;
            } else {
                percCust = 100 * mean / initMean;
            }
            double priceDoub = prices.get(i) / NUM_EXECUTIONS;

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
        
        StoredData.mean = sum.get(0) / NUM_EXECUTIONS;
        StoredData.initMean = initSum.get(0) / NUM_EXECUTIONS;
        double variance = computeVariance(StoredData.mean);
        double initVariance = computeVariance(StoredData.initMean);
        StoredData.stdDev = Math.sqrt(variance);
        StoredData.initStdDev = Math.sqrt(initVariance);
        if (isMaximizar()) {
        	StoredData.percCust = 100 * StoredData.mean / StoredData.custMean;
        	StoredData.initPercCust = 100 * StoredData.initMean / StoredData.custMean;
        } else if (!isMaximizar()) {
        	StoredData.percCust = (100 * StoredData.mean) / StoredData.initMean;
        }
        StoredData.My_price = prices.get(0) / NUM_EXECUTIONS;

        jtA.append("Num Ejecuciones: " + getNumExecutions() + "\r\n" 
                + "Num atributos: " + TotalAttributes.size() + "\r\n" 
        		+ "Num productores: " + Producers.size() + "\r\n"
				+ "Num perfiles: " + CustomerProfiles.size() + "\r\n" 
        		+ "Number CustProf: " + inputGUI.getnum() + "\r\n" + "Atributos conocidos: "
				+ getKNOWN_ATTRIBUTES() + " %" + "\r\n" + "Atributos especiales: " + in.getSPECIAL_ATTRIBUTES()
				+ " %" + "\r\n" + "% Mutación de atributos: " + in.getMUT_PROB_CUSTOMER_PROFILE() + " %" + "\r\n"
				+ "Num grupos de perfil: " + getRESP_PER_GROUP() + "\r\n" + "Num productos: "
				+ StoredData.number_Products + "\r\n" + "Atributos linkados: " + StoredData.isAttributesLinked + "\r\n"
				+ "Centroides: " + inWeka.getIndexProfiles().toString() + "\r\n"
				+ "************* RESULTS *************" + "\r\n" + "BestWSC: " + BestWSC + "\r\n" + "Mean: " + meanTXT
				+ "\r\n" + "initMean: " + initMeanTXT + "\r\n" + "stdDev: " + stdDevTXT + "\r\n" + "initStdDev: "
				+ initStdDevTXT + "\r\n" + "My_priceString: " + priceTXT + "\r\n");
		if (isMaximizar()) {
			jtA.append("percCust: " + percCustTXT + "\r\n" + "initPercCust: " + initPercCustTXT + "\r\n");
		} else {
			jtA.append("percCust: " + percCustTXT + "\r\n");
		}
    }

    protected abstract void startProblem(JTextArea jtA) throws Exception;

    protected abstract Object solveProblem() throws Exception;

    private void generateInput() {
        TotalAttributes = StoredData.Atributos;
        CustomerProfiles = StoredData.Profiles;
        if (isAlgMin()) {
            ArrayList<Producer> producerMinimax = new ArrayList<>();
            producerMinimax.add(StoredData.Producers.get(0));
            producerMinimax.add(StoredData.Producers.get(1));

            Producers = producerMinimax;
        } else
            Producers = StoredData.Producers;
    }
    
    /** Generar datos */
	public void inputData(boolean inputFile, String archivo) throws Exception {
		if (inputFile) {
			int input = archivo.indexOf(".xml");
			if (input != -1)
				inputGUI.inputXML(archivo);
			else
				inputGUI.inputTxt(archivo);
			inputGUI.generateGUI();
		} else 
			if (inputGUI.isNumData())
				in.generate();/*
			else if (Producers.size() == 0 || inputGUI.isNumData()) {
				in.generate();
				TotalAttributes = in.getTotalAttributes();
				Producers = in.getProducers();
				CustomerProfiles = in.getCustomerProfiles();
			}
		}*/
		//else{
			generateInput();
		//}
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

    /****************************************************************************************************
     * GENETIC ALGORITHM                                    *
     ****************************************************************************************************/

    /**
     * Solving the PD problem by using a GA
     */
    @SuppressWarnings("unchecked")
	protected void solvePD_GA() throws Exception {

        ArrayList<Object> BestResults = (ArrayList<Object>) solveProblem();

        for(int i = 0; i < BestResults.size(); i++) {
            BestWSC.set(i, getFitness(BestResults.get(i)));
            Producers.get(MY_PRODUCER).getProducts().set(i, (Product) BestResults.get(i));
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
    
    protected Object breed(Object father, Object mother) {
        Product son = new Product();
        /*Random value in range [0,100)*/
        int crossover = (int) (100 * Math.random());
        int rndVal;

        if (crossover <= CROSSOVER_PROB) {//El hijo sera una mezcla de la madre y el padre
            HashMap<Attribute, Integer> crossover_attributeValue = new HashMap<>();
            for (int i = 0; i < TotalAttributes.size(); i++) { //With son

                rndVal = (int) (2 * Math.random()); /*Generamos aleatoriamente un 0 (padre) o un 1 (madre).*/
                if (rndVal == 0)
                    crossover_attributeValue.put(TotalAttributes.get(i), ((Product) father).getAttributeValue().get(TotalAttributes.get(i)));
                else
                    crossover_attributeValue.put(TotalAttributes.get(i), ((Product) mother).getAttributeValue().get(TotalAttributes.get(i)));
            }
            son.setAttributeValue(crossover_attributeValue);
        } else {//El hijo seria completamente igual a la madre o al padre
            rndVal = (int) (2 * Math.random()); /*Generamos aleatoriamente un 0 (padre) o un 1 (madre).*/

            if (rndVal == 0)
                son = ((Product) father).clone();
            else
                son = ((Product) mother).clone();
        }
        return son;
    }

    protected Object mutate(Object indiv) {
        Product mutant = ((Product) indiv).clone();
        int attrVal = 0;

        for (int j = 0; j < TotalAttributes.size(); j++) {
        	int size = 0;
    		if(TotalAttributes.get(j).getMAX() == 43) size = 7;
    		else if(TotalAttributes.get(j).getMAX() == 37) size = 6;
    		else size = TotalAttributes.get(j).getMAX();
            /*Random value in range [0,100)*/
            double mutation = 100 * Math.random();
            if (mutation <= MUTATION_PROB) {
                boolean attrFound = false;
                while (!attrFound) {
                    attrVal = (int) (Math.floor(size * Math.random()));
                    if (Producers.get(MY_PRODUCER).getAvailableAttribute().get(j).getAvailableValues().get(attrVal))
                        attrFound = true;
                }
                mutant.getAttributeValue().put(TotalAttributes.get(j), attrVal);
            }
        }

        mutant.setPrice(inter.calculatePrice(mutant, getTotalAttributes(), getProducers()));
        return mutant;
    }

    public ArrayList<Object> createInitPopulation() throws Exception {
        ArrayList<Object> Population = new ArrayList<>();
        Fitness = new ArrayList<>();
        BestWSC = new ArrayList<>();

        for (int i = 0; i < Producers.get(MY_PRODUCER).getProducts().size(); i++) {
            Population.add((Producers.get(MY_PRODUCER).getProducts().get(i)).clone());

            if (isMaximizar())
                Fitness.add(computeWSC((Product) Population.get(i), MY_PRODUCER));
            else
                Fitness.add(computeBenefits((Product) Population.get(i), MY_PRODUCER));
        }

        for (int t = 0; t < Fitness.size(); t++)
            BestWSC.add(Fitness.get(t));

        ArrayList<Integer> aux = new ArrayList<>();
        for (int q = 0; q < BestWSC.size(); q++)
            aux.add(BestWSC.get(q));

        Initial_Results.add(aux);

        for (int i = Producers.get(MY_PRODUCER).getProducts().size(); i < NUM_POPULATION; i++) {

            if (i % 2 == 0) /*We create a random product*/
                Population.add(createRndProduct(Producers.get(MY_PRODUCER).getAvailableAttribute()));
            else /*We create a near product*/
                Population.add(createNearProduct(Producers.get(MY_PRODUCER).getAvailableAttribute(), (int) (CustomerProfiles.size() * Math.random())));  /////////??verificar//////////

            if (isMaximizar())
                Fitness.add(computeWSC((Product) Population.get(i), MY_PRODUCER));
            else
                Fitness.add(computeBenefits((Product) Population.get(i), MY_PRODUCER));

            int worstIndex = isBetweenBest(Fitness.get(i));
            if (worstIndex != -1) {
                BestWSC.set(worstIndex, Fitness.get(i));
                Producers.get(MY_PRODUCER).getProducts().set(worstIndex, (Product) Population.get(i));
            }
        }

        return Population;
    }

    protected Integer getFitness(Object object) throws Exception {
        if (isMaximizar())
            return computeWSC((Product) object, MY_PRODUCER);
        else
            return computeBenefits((Product) object, MY_PRODUCER);
    }


    private int isBetweenBest(int fitness) {
        for (int i = 0; i < BestWSC.size(); i++) {
            if (fitness > BestWSC.get(i))
                return i;
        }
        return -1;
    }

    private Integer computeBenefits(Product product, int myProducer) throws Exception {
        return computeWSC(product, myProducer) * product.getPrice();
    }

    /***
     * Computing the weighted score of the producer
     * prodInd is the index of the producer
     *
     * @throws Exception
     **/
    private int computeWSC(Product product, int prodInd) throws Exception { //, int prodInd
        int wsc = 0;
        boolean isTheFavourite;
        int meScore, score, k, p, numTies;

        for (int i = 0; i < CustomerProfiles.size(); i++) {
            for (int j = 0; j < CustomerProfiles.get(i).getSubProfiles().size(); j++) {
                isTheFavourite = true;
                numTies = 1;
//                if (StoredData.Algorithm == StoredData.MINIMAX)
//                    meScore = scoreProductMM(CustomerProfiles.get(i), product);
//                else
                meScore = scoreProduct(CustomerProfiles.get(i).getSubProfiles().get(j), product);

                if (StoredData.isAttributesLinked)
                    meScore += scoreLinkedAttributes(CustomerProfiles.get(i).getLinkedAttributes(), product);

                k = 0;
                while (isTheFavourite && k < Producers.size()) {
                    p = 0;
                    while (isTheFavourite && p < Producers.get(k).getProducts().size()) {
                        if (Producers.get(k).getProducts().get(p) != product) {

//                            if (StoredData.Algorithm == StoredData.MINIMAX)
//                                score = scoreProductMM(CustomerProfiles.get(i), Producers.get(k).getProduct());
//                            else
                            score = scoreProduct(CustomerProfiles.get(i).getSubProfiles().get(j), Producers.get(k).getProducts().get(p));

                            if (StoredData.isAttributesLinked)
                                score += scoreLinkedAttributes(CustomerProfiles.get(i).getLinkedAttributes(), product);

                            if (score > meScore)
                                isTheFavourite = false;

                            else if (score == meScore)
                                numTies += 1;
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
    @SuppressWarnings("unused")
	private int scoreProductMM(CustomerProfile profile, Product product) throws Exception {
        int score = 0;
        for (int i = 0; i < TotalAttributes.size(); i++)
            score += profile.getScoreAttributes().get(i).getScoreValues().get(product.getAttributeValue().get(TotalAttributes.get(i)));

        return score;
    }

    /**
     * Computing the score of a product given the customer profile index
     * custProfInd and the product
     */
    private int scoreProduct(SubProfile subprofile, Product product) throws Exception {
        int score = 0;
        for (int i = 0; i < TotalAttributes.size(); i++) {
        	int size = 0;
    		if(TotalAttributes.get(i).getMAX() == 43) size = 7;
    		else if(TotalAttributes.get(i).getMAX() == 37) size = 6;
    		else size = TotalAttributes.get(i).getMAX();
            score += scoreAttribute(size, subprofile.getValueChosen().get(TotalAttributes.get(i)), product.getAttributeValue().get(TotalAttributes.get(i)));
        }
        return score;
    }

    /**
     * Computing the score of an attribute for a product given the
     * ' number of values
     */
    private int scoreAttribute(int numOfValsOfAttr, int valOfAttrCust, int valOfAttrProd) throws Exception {
        int score = 0;
        switch (numOfValsOfAttr) {
            case 1:
                score = 10;
                break;

            case 2:
                if (valOfAttrCust == valOfAttrProd) score = 10;
                else score = 0;
                break;

            case 3:
                if (valOfAttrCust == valOfAttrProd) score = 10;
                else if (Math.abs(valOfAttrCust - valOfAttrProd) == 1) score = 5;
                else score = 0;
                break;

            case 4:
                if (valOfAttrCust == valOfAttrProd) score = 10;
                else if (Math.abs(valOfAttrCust - valOfAttrProd) == 1) score = 6;
                else if (Math.abs(valOfAttrCust - valOfAttrProd) == 2) score = 2;
                else score = 0;
                break;

            case 5:
                if (valOfAttrCust == valOfAttrProd) score = 10;
                else if (Math.abs(valOfAttrCust - valOfAttrProd) == 1) score = 6;
                else if (Math.abs(valOfAttrCust - valOfAttrProd) == 2) score = 2;
                else if (Math.abs(valOfAttrCust - valOfAttrProd) == 3) score = 1;
                else score = 0;
                break;

            case 6:
                if (valOfAttrCust == valOfAttrProd) score = 10;
                else if (Math.abs(valOfAttrCust - valOfAttrProd) == 1) score = 6;
                else if (Math.abs(valOfAttrCust - valOfAttrProd) == 2) score = 2;
                else if (Math.abs(valOfAttrCust - valOfAttrProd) == 3) score = 1;
                else score = 0;
                break;

            case 7:
                if (valOfAttrCust == valOfAttrProd) score = 10;
                else if (Math.abs(valOfAttrCust - valOfAttrProd) == 1) score = 6;
                else if (Math.abs(valOfAttrCust - valOfAttrProd) == 2) score = 2;
                else if (Math.abs(valOfAttrCust - valOfAttrProd) == 3) score = 1;
                else score = 0;
                break;

            case 8:
                if (valOfAttrCust == valOfAttrProd) score = 10;
                else if (Math.abs(valOfAttrCust - valOfAttrProd) == 1) score = 6;
                else if (Math.abs(valOfAttrCust - valOfAttrProd) == 2) score = 2;
                else if (Math.abs(valOfAttrCust - valOfAttrProd) == 3) score = 1;
                else score = 0;
                break;

            case 9:
                if (valOfAttrCust == valOfAttrProd) score = 10;
                else if (Math.abs(valOfAttrCust - valOfAttrProd) == 1) score = 6;
                else if (Math.abs(valOfAttrCust - valOfAttrProd) == 2) score = 2;
                else if (Math.abs(valOfAttrCust - valOfAttrProd) == 3) score = 1;
                else score = 0;
                break;
            case 10: 
                if (valOfAttrCust == valOfAttrProd) score = 10;
                else if (Math.abs(valOfAttrCust - valOfAttrProd) == 1) score = 8;
                else if (Math.abs(valOfAttrCust - valOfAttrProd) == 2) score = 5;
                else if (Math.abs(valOfAttrCust - valOfAttrProd) == 3) score = 1;
                else score = 0;
                break;

           // case 11:
            default:
            	if (valOfAttrCust == valOfAttrProd) score = 10;
                else if (Math.abs(valOfAttrCust - valOfAttrProd) == 1) score = 8;
                else if (Math.abs(valOfAttrCust - valOfAttrProd) == 2) score = 6;
                else if (Math.abs(valOfAttrCust - valOfAttrProd) == 3) score = 4;
                else if (Math.abs(valOfAttrCust - valOfAttrProd) == 4) score = 2;
                else score = 0;
                break;
                /*throw new Exception("Error in scoreAttribute() function: " +
                        "Number of values of the attribute unexpected");*/
        }
        return score;
    }


    /*************************************** " AUXILIARY METHODS GENERATEINPUT()" ***************************************/

    /**
     * Creating a random product
     */
    private Product createRndProduct(ArrayList<Attribute> availableAttribute) {
        Product product = new Product(new HashMap<Attribute, Integer>());
        int limit = (int) (TotalAttributes.size() * KNOWN_ATTRIBUTES / 100);
        int attrVal = 0;

        for (int i = 0; i < limit; i++) {
        	int size = 0;
    		if(TotalAttributes.get(i).getMAX() == 43) size = 7;
    		else if(TotalAttributes.get(i).getMAX() == 37) size = 6;
    		else size = TotalAttributes.get(i).getMAX();
            attrVal = (int) (size * Math.random());
            product.getAttributeValue().put(TotalAttributes.get(i), attrVal);

        }

        for (int i = limit; i < TotalAttributes.size(); i++) {
        	int size = 0;
    		if(TotalAttributes.get(i).getMAX() == 43) size = 7;
    		else if(TotalAttributes.get(i).getMAX() == 37) size = 6;
    		else size = TotalAttributes.get(i).getMAX();
            boolean attrFound = false;
            while (!attrFound) {
                attrVal = (int) (size * Math.random());

                if (availableAttribute.get(i).getAvailableValues().get(attrVal))
                    attrFound = true;

            }
            product.getAttributeValue().put(TotalAttributes.get(i), attrVal);
        }

        product.setPrice(inter.calculatePrice(product, getTotalAttributes(), getProducers()));
        return product;
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
		product.setPrice(inter.calculatePrice(product, getTotalAttributes(), getProducers()));
		return product;
	}
	
	/**
	 * Elegir un atributo cercano por cada perfil obtenido como centroide
	 */
	private int chooseAttributeCluster(int attrInd, int index, ArrayList<Attribute> availableAttrs) {
		int attrVal;

		ArrayList<Integer> possibleAttr = new ArrayList<>();
		int size = 0;
		if(TotalAttributes.get(attrInd).getMAX() == 43) size = 7;
		else if(TotalAttributes.get(attrInd).getMAX() == 37) size = 6;
		else size = TotalAttributes.get(attrInd).getMAX();

		for (int i = 0; i < size - 1; i++) {
			int possible = 0;
			possible += getCustomerProfiles().get(index).getScoreAttributes().get(attrInd).getScoreValues().get(i);
			possibleAttr.add(possible);
		}
		attrVal = getMaxAttrVal(attrInd, possibleAttr, availableAttrs);
		return attrVal;
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

        product.setPrice(inter.calculatePrice(product, getTotalAttributes(), getProducers()));
        return product;
    }

    /**
     * Chosing an attribute near to the customer profiles given
     */
    private static int chooseAttribute(int attrInd, ArrayList<Integer> custProfInd, ArrayList<Attribute> availableAttrs) {
        int attrVal;

        ArrayList<Integer> possibleAttr = new ArrayList<>();

        int size = 0;
		if(TotalAttributes.get(attrInd).getMAX() == 43) size = 7;
		else if(TotalAttributes.get(attrInd).getMAX() == 37) size = 6;
		else size = TotalAttributes.get(attrInd).getMAX();
        for (int i = 0; i < size - 1; i++) {
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


    /****************************************************************************************************
     * MINIMAX                                              *
     ****************************************************************************************************/

    public void initializeMinimax() throws Exception {

        ArrayList<Integer> aux = new ArrayList<>();
        if (!isMaximizar())
            aux.add(computeBenefits(Producers.get(MY_PRODUCER).getProduct(), MY_PRODUCER));
        else
            aux.add(computeWSC(Producers.get(MY_PRODUCER).getProduct(), MY_PRODUCER));
        Initial_Results.add(aux);


        solveProblem();

        aux = new ArrayList<>();
        if (!isMaximizar())
            aux.add(Producers.get(MY_PRODUCER).getNumber_CustomerGathered() * inter.calculatePrice(Producers.get(MY_PRODUCER).getProduct(), getTotalAttributes(), getProducers()));
        else
            aux.add(Producers.get(MY_PRODUCER).getNumber_CustomerGathered());
        Results.add(aux);

        aux = new ArrayList<>();
        aux.add(inter.calculatePrice(Producers.get(MY_PRODUCER).getProduct(), getTotalAttributes(), getProducers()));
        Prices.add(aux);
    }

    protected void setFitnessAcumulated(int i, ArrayList<Integer> customersAcumulated) {
        Producers.get(i).setCustomersGathered(customersAcumulated);
    }

    public Object getObject(int playerIndex) {
        return Producers.get(playerIndex).getProducts().get(0);
    }

    public Integer getFitness(Object object, int index) throws Exception {
        if (!isMaximizar())
            ((Product) object).setPrice(inter.calculatePrice((Product) object, getTotalAttributes(), getProducers()));

        if (!isMaximizar())
            return computeBenefits((Product) object, index);
        else
            return computeWSC((Product) object, index);
    }

    public int getDimens() {
        return TotalAttributes.size();
    }

    protected int getSolutionsSpace(int dimen) {
    	int size;
    	if(TotalAttributes.get(dimen).getMAX() == 43) size = 7;
		else if(TotalAttributes.get(dimen).getMAX() == 37) size = 6;
		else size = TotalAttributes.get(dimen).getMAX();
        return size;
    }

    protected int getSolution(int playerIndex, int dimension) {
        return Producers.get(playerIndex).getProducts().get(0).getAttributeValue().get(TotalAttributes.get(dimension));
    }

    protected boolean isPosibleToChange(int playerIndex, int dimension, int attrVal) {
        return Producers.get(playerIndex).getAvailableAttribute().get(dimension).getAvailableValues().get(attrVal);
    }

    protected void changeChild(Object o, int dimension, int solutionSpaceIndex) {
        ((Product) o).getAttributeValue().put(TotalAttributes.get(dimension), solutionSpaceIndex);
    }

    protected void setSolution(int playerIndex, int dimension, int solution) {
        Producers.get(playerIndex).getProducts().get(0).getAttributeValue().put(TotalAttributes.get(dimension), solution);
    }

    public int countCustomers() {
        int total = 0;
        for (int i = 0; i < CustomerProfiles.size(); i++)
            total += CustomerProfiles.get(i).getNumberCustomers();

        return total;
    }


    /****************************************************************************************************
     * PARTICLE SWARM OPTIMITATION                                    *
     ****************************************************************************************************/


    protected int getDimensions() {
        return TotalAttributes.size();
    }

    public void initializePSOproblem() throws Exception {

        if (EXECUTION != 0) { /*We reset myPP and create a new product as the first product*/
            ArrayList<Product> products = new ArrayList<>();
            for (int k = 0; k < StoredData.number_Products; k++)
                products.add(createNearProduct(Producers.get(MY_PRODUCER).getAvailableAttribute(), (int) (CustomerProfiles.size() * Math.random())));
            Producers.get(MY_PRODUCER).setProducts(products);
        }

        Population = new ArrayList<>();
        @SuppressWarnings("unchecked")
		ArrayList<Object> finalPupolation = (ArrayList<Object>) solveProblem();

        for (int i = 0; i < finalPupolation.size(); i++) {
            Population.add((Product) finalPupolation.get(i));
            Fitness.set(i, getFitness(Population.get(i)));
        }

        // STEP 2 - UPDATE GENERAL BEST
        for (int j = 0; j < Fitness.size(); j++) {
            int worstIndex = isBetweenBest(Fitness.get(j));
            if (worstIndex != -1) {
                BestWSC.set(worstIndex, Fitness.get(j));
                Producers.get(MY_PRODUCER).getProducts().set(worstIndex, Population.get(j));
            }
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

    private void showWSC() throws Exception {
        int wsc;
        wscSum = 0;

        for (int i = 0; i < Producers.size(); i++) {
            for (int j = 0; j < Producers.get(i).getProducts().size(); j++) {
                wsc = computeWSC(Producers.get(i).getProducts().get(j), i);
                wscSum += wsc;
            }
        }
    }

    public ArrayList<Object> createInitSwarm() throws Exception {

        ArrayList<Object> Population = new ArrayList<>();
        Fitness = new ArrayList<>();
        BestWSC = new ArrayList<>();

        for (int i = 0; i < Producers.get(MY_PRODUCER).getProducts().size(); i++) {
            Population.add((Producers.get(MY_PRODUCER).getProducts().get(i)).clone());

            if (isMaximizar())
                Fitness.add(computeWSC((Product) Population.get(i), MY_PRODUCER));
            else
                Fitness.add(computeBenefits((Product) Population.get(i), MY_PRODUCER));
        }

        for (int t = 0; t < Fitness.size(); t++)
            BestWSC.add(Fitness.get(t));

        ArrayList<Integer> aux = new ArrayList<>();
        for (int q = 0; q < BestWSC.size(); q++)
            aux.add(BestWSC.get(q));

        Initial_Results.add(aux);

        for (int i = Producers.get(MY_PRODUCER).getProducts().size(); i < NUM_POPULATION; i++) {

        	for (int j = 0; j < inWeka.getIndexProfiles().size(); j++) {
				createNearProductCluster(Producers.get(MY_PRODUCER).getAvailableAttribute(),
						inWeka.getIndexProfiles().get(j));

			}
        	
            if (i % 2 == 0) /*We create a random product*/
                Population.add(createRndProduct(Producers.get(MY_PRODUCER).getAvailableAttribute()));
            else /*We create a near product*/
                Population.add(createNearProduct(Producers.get(MY_PRODUCER).getAvailableAttribute(), (int) (CustomerProfiles.size() * Math.random())));  /////////??verificar//////////


            if (isMaximizar())
                Fitness.add(computeWSC((Product) Population.get(i), MY_PRODUCER));
            else
                Fitness.add(computeBenefits((Product) Population.get(i), MY_PRODUCER));


            int worstIndex = isBetweenBest(Fitness.get(i));
            if (worstIndex != -1) {
                BestWSC.set(worstIndex, Fitness.get(i));
                Producers.get(MY_PRODUCER).getProducts().set(worstIndex, (Product) Population.get(i));
            }
        }

        return Population;
    }

    protected Integer getLocationValue(Object obj, int dimen) {
        return ((Product) obj).getAttributeValue().get(TotalAttributes.get(dimen));
    }

    protected void updateLocation(Object obj, int dimen, int new_value_for_location) {
        ((Product) obj).getAttributeValue().put(TotalAttributes.get(dimen), new_value_for_location);
    }


    /****************************************************************************************************
     * SIMUMLATED ANNEALING                                            *
     ****************************************************************************************************/

    private int productIndex = 0;

    public int getFitness_SA(Object origin) throws Exception {
        Product p = (Product) origin;

        if (isMaximizar()) {
            return computeWSC_SA(p, productIndex);
        } else {
            return computeBenefits_SA(p, MY_PRODUCER, productIndex);
        }
    }


    private Integer computeBenefits_SA(Product product, int myProducer, int productIndex) throws Exception {
        return computeWSC_SA(product, productIndex) * product.getPrice();
    }

    /***
     * Computing the weighted score of the producer
     * prodInd is the index of the producer
     *
     * @throws Exception
     **/
    private int computeWSC_SA(Product product, int productIndex) throws Exception {
        int wsc = 0;
        boolean isTheFavourite;
        int meScore, score, k, p, numTies;

        for (int i = 0; i < CustomerProfiles.size(); i++) {
            for (int j = 0; j < CustomerProfiles.get(i).getSubProfiles().size(); j++) {
                isTheFavourite = true;
                numTies = 1;
                meScore = scoreProduct(CustomerProfiles.get(i).getSubProfiles().get(j), product);

                if (StoredData.isAttributesLinked)
                    meScore += scoreLinkedAttributes(CustomerProfiles.get(i).getLinkedAttributes(), product);

                k = 0;
                while (isTheFavourite && k < Producers.size()) {
                    p = 0;
                    while (isTheFavourite && p < Producers.get(k).getProducts().size()) {
                        if (k != MY_PRODUCER || p != productIndex) {

                            score = scoreProduct(CustomerProfiles.get(i).getSubProfiles().get(j), Producers.get(k).getProducts().get(p));

                            if (StoredData.isAttributesLinked)
                                score += scoreLinkedAttributes(CustomerProfiles.get(i).getLinkedAttributes(), product);

                            if (score > meScore)
                                isTheFavourite = false;

                            else if (score == meScore)
                                numTies += 1;
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


    public Object changeObject(Object currency_product) {

        Product originProduct = (Product) currency_product;
        HashMap<Attribute, Integer> AUXattributeValue = new HashMap<>();
        for (int q = 0; q < TotalAttributes.size(); q++) {
            AUXattributeValue.put(TotalAttributes.get(q), originProduct.getAttributeValue().get(TotalAttributes.get(q)));
        }
        Product new_product = new Product(AUXattributeValue);
        new_product.setPrice(originProduct.getPrice());

        
        //CHANGE SOME ATTRIBUTES
        for (int j = 0; j < TotalAttributes.size(); j++) {
        	int size = 0;
    		if(TotalAttributes.get(j).getMAX() == 43) size = 7;
    		else if(TotalAttributes.get(j).getMAX() == 37) size = 6;
    		else size = TotalAttributes.get(j).getMAX();
            if ((Math.random() * 100) < CHANGE_ATTRIBUTE_PROB) {
                int new_attr_value = (int) (Math.random() * size);
                new_product.getAttributeValue().put(TotalAttributes.get(j), new_attr_value);
            }
        }

        return new_product;
    }

    public void initializeSAProblem() throws Exception {

        if (EXECUTION != 0) { /*We reset myPP and create a new product as the first product*/
            ArrayList<Product> products = new ArrayList<>();
            for (int k = 0; k < StoredData.number_Products; k++)
                products.add(createNearProduct(Producers.get(MY_PRODUCER).getAvailableAttribute(), (int) (CustomerProfiles.size() * Math.random())));
            Producers.get(MY_PRODUCER).setProducts(products);
        }

        ArrayList<Integer> initial = new ArrayList<>();
        for (int i = 0; i < Producers.get(MY_PRODUCER).getProducts().size(); i++) {
            if (isMaximizar())
                initial.add(computeWSC_SA(Producers.get(MY_PRODUCER).getProducts().get(i), i));
            else
                initial.add(computeBenefits_SA(Producers.get(MY_PRODUCER).getProducts().get(i), MY_PRODUCER, i));
        }
        Initial_Results.add(initial);


        //Apply the SA Algorithm for each Product of our Producer
        for (int i = 0; i < Producers.get(MY_PRODUCER).getProducts().size(); i++) {
            productIndex = i;
            Product better_product = (Product) solveProblem();
            Producers.get(MY_PRODUCER).getProducts().set(i, better_product);
        }


        ArrayList<Integer> result = new ArrayList<>();
        for (int i = 0; i < Producers.get(MY_PRODUCER).getProducts().size(); i++) {
            if (isMaximizar())
                result.add(computeWSC_SA(Producers.get(MY_PRODUCER).getProducts().get(i), i));
            else
                result.add(computeBenefits_SA(Producers.get(MY_PRODUCER).getProducts().get(i), MY_PRODUCER, i));
        }
        Results.add(result);
        showWSC_SA();

        //Set prices
        ArrayList<Integer> prices = new ArrayList<>();
        for (int i = 0; i < Producers.get(MY_PRODUCER).getProducts().size(); i++) {
            int price_MyProduct = inter.calculatePrice(Producers.get(MY_PRODUCER).getProducts().get(i), getTotalAttributes(), getProducers());
            Producers.get(MY_PRODUCER).getProducts().get(i).setPrice(price_MyProduct);
            prices.add(price_MyProduct);
        }
        Prices.add(prices);
    }

    /**
     * Showing the wsc of the rest of products
     *
     * @throws Exception
     */

    private void showWSC_SA() throws Exception {
        int wsc;
        wscSum = 0;

        for (int i = 0; i < Producers.size(); i++) {
            for (int j = 0; j < Producers.get(i).getProducts().size(); j++) {
                wsc = computeWSC_SA(Producers.get(i).getProducts().get(j), j);
                wscSum += wsc;
            }
        }
    }

    public Object getBestObjFound() throws Exception {
        return Producers.get(MY_PRODUCER).getProducts().get(productIndex).clone();
    }
	public int getCROSSOVER_PROB() {
		return CROSSOVER_PROB;
	}

	public int getMUTATION_PROB() {
		return MUTATION_PROB;
	}
	public int getNUM_POPULATION() {
		return NUM_POPULATION;
	}


	public void setCROSSOVER_PROB(int cROSSOVER_PROB) {
		CROSSOVER_PROB = cROSSOVER_PROB;
	}

	public void setMUTATION_PROB(int mUTATION_PROB) {
		MUTATION_PROB = mUTATION_PROB;
	}
	public void setNUM_POPULATION(int nUM_POPULATION) {
		NUM_POPULATION = nUM_POPULATION;
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

/*	public boolean isAttributesLinked() {
		return StoredData.isAttributesLinked;
	}

	public void setAttributesLinked(boolean isAttributesLink) {
		StoredData.isAttributesLinked = isAttributesLink;
	}

	public int getNumber_Products() {
		return StoredData.number_Products;
	}

	public void setNumber_Products(int number) {
		StoredData.number_Products = number;
	}
	*/
	public boolean isAlgMin() {
		return isAlgMin;
	}
	public void setAlgMin(boolean isAlg) {
		isAlgMin = isAlg;
	}
	public int getCHANGE_ATTRIBUTE_PROB() {
		return CHANGE_ATTRIBUTE_PROB;
	}
	public void setCHANGE_ATTRIBUTE_PROB(int cHANGE_ATTRIBUTE_PROB) {
		CHANGE_ATTRIBUTE_PROB = cHANGE_ATTRIBUTE_PROB;
	}
    
}
