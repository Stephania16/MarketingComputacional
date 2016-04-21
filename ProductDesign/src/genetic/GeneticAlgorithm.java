package genetic;

import java.util.HashMap;
import javax.swing.JTextArea;

import Comunes.Attribute;
import Comunes.CustomerProfile;
import Comunes.Producer;
import Comunes.Product;
import GUI.Añadir;
import GUI.InputRandom;

import java.util.ArrayList;

public class GeneticAlgorithm {

    static double KNOWN_ATTRIBUTES = 100; /* 100
                                                 * % of attributes known for all
												 * producers
												 */
    static double SPECIAL_ATTRIBUTES = 33; /* 33
                                                 * % of special attributes known
												 * for some producers

												 						 */
    static double MUT_PROB_CUSTOMER_PROFILE = 33; /*  * % of mutated
                                                         * attributes in a
														 * customer profile
														 */
    static int CROSSOVER_PROB = 80; /* % of crossover */
    static int MUTATION_PROB = 1; /* % of mutation */
    static int NUM_GENERATIONS = 100; /* number of generations */
    static int NUM_POPULATION = 20; /* number of population */
    static int RESP_PER_GROUP = 20; /* * We divide the respondents of each
                                             * profile in groups of
											 * RESP_PER_GROUP respondents
											 */
    static int NEAR_CUST_PROFS = 4;
    static int NUM_EXECUTIONS = 20; /* number of executions = 20*/

    static final int MY_PRODUCER = 0;  //The index of my producer
    
    public static int fit = 0;
    public static int customers = 0;
    public static int Benefits = 1;

    private ArrayList<Attribute> TotalAttributes = new ArrayList<>();
    private ArrayList<Producer> Producers = new ArrayList<>();

    /* GA VARIABLES */
    private int BestWSC; /* Stores the best wsc found */
    private ArrayList<Product> Population;   //Private mPopu As List(Of List(Of Integer))
    private ArrayList<Integer> Fitness; /* * mFitness(i) = wsc of mPopu(i) */

    /* STATISTICAL VARIABLES */
    private ArrayList<Integer> Results = new ArrayList<>();
    private ArrayList<Integer> Initial_Results = new ArrayList<>();
    private int wscSum;

    private ArrayList<Integer> Prices = new ArrayList<>();
    private ArrayList<CustomerProfile> CustomerProfiles = new ArrayList<>();
    Añadir añadir = new Añadir();
    InputRandom in = new InputRandom();

    /***************************************
     * " AUXILIARY EXCEL METHODS " * @throws Exception
     ***************************************/

    public void start(JTextArea jtA, String datos_txt, boolean input_txt) throws Exception { //String args[]
        long inicio = System.currentTimeMillis();
        statisticsPD(jtA, datos_txt, input_txt);
       // calculatePrice();
        long tiempo = System.currentTimeMillis()- inicio;
            
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
        
        int price_MyProduct = calculatePrice(Producers.get(MY_PRODUCER).getProduct());
        Producers.get(MY_PRODUCER).getProduct().setPrice(price_MyProduct);
        Prices.add(price_MyProduct);
    }

    /**
     * Generating statistics about the PD problem
     */
    private void statisticsPD(JTextArea jtA, String datos_txt, boolean input_txt) throws Exception {

    	double mean;
        double initMean;
        double sum = 0; /*sum of customers achieved*/
        double initSum = 0; /*sum of initial customers*/
        int sumCust = 0; /*sum of the total number of customers*/
        double custMean;
        double variance;
        double initVariance;
        double stdDev;
        double initStdDev;
        double percCust; /*% of customers achieved*/
        double initPercCust; /*% of initial customers achieved*/
        int price = 0;
        int My_price;
        
        Results = new ArrayList<>();
        Initial_Results = new ArrayList<>();
        Prices = new ArrayList<>();

        Math.random();

        if(input_txt){ 
        	añadir.muestraContenido(datos_txt);
        	generarDatosGUI();
        }
        else{
        	if(añadir.isGenerarDatosEntrada()) generarDatosGUI();
        	else 
        		if (Producers.size() == 0){ 
        			in.generate();
        			TotalAttributes = in.getTotalAttributes();
        			Producers = in.getProducers();
        			CustomerProfiles = in.getCustomerProfiles();
        		}
        }


        for (int i = 0; i < getNumExecutions(); i++) {
            if (i != 0) /*We reset myPP and create a new product as the first product*/ {
                Producers.get(MY_PRODUCER).setProduct(createNearProduct(Producers.get(MY_PRODUCER).getAvailableAttribute(), (int) (CustomerProfiles.size() * Math.random())));
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
        
        jtA.setText("");
        jtA.append("Num Ejecuciones: " + getNumExecutions() + "\n" + 
        		   "Num atributos: " + TotalAttributes.size() + "\n" + 
        		   "Num productores: " + Producers.size() + "\n" + 
        		   "Num perfiles: " + CustomerProfiles.size() + "\n" + 
        		   "Number CustProf: " + añadir.getnum() + "\n" + 
        		   "Num Población: " +  + getNUM_POPULATION() + "\n" + 
        		   "Num Generaciones: " + getNUM_GENERATIONS() + "\n" + 
        		   "Atributos conocidos: " + getKNOWN_ATTRIBUTES() + " %" + "\n" + 
        		   "Atributos especiales: " + getSPECIAL_ATTRIBUTES() + " %" + "\n" + 
        		   "% Mutación de atributos: " + getMUT_PROB_CUSTOMER_PROFILE() + " %" + "\n" + 
        		   "% Crossover: " + getCROSSOVER_PROB() + " %" + "\n" + 
        		   "% Mutación: " + getMUTATION_PROB() + " %" + "\n" + 
        		   "Num grupos de perfil: " + getRESP_PER_GROUP() + "\n" + 
        		   "Num perfiles cercanos: " + getNEAR_CUST_PROFS() + "\n" + 
        		   "************* RESULTS *************" + "\n" + 
        		   "BestWSC: " + BestWSC + "\n" + 
        		   "Mean: " + String.format("%.2f", mean) + "\n" + 
                   "initMean: " + String.format("%.2f", initMean) + "\n" + 
        		   "variance: " + String.format("%.2f", variance) + "\n" + 
        		   "initVariance: " + String.format("%.2f", initVariance) + "\n" + 
        		   "stdDev: " + String.format("%.2f", stdDev) + "\n" + 
        		   "initStdDev: " + String.format("%.2f", initStdDev) + "\n" + 
        		   "custMean: " + String.format("%.2f", custMean) + "\n"); 
     
        if(fit == customers){
        	jtA.append("percCust: " + String.format("%.2f", percCust) + " %" + "\n" + 
         		       "initPercCust: " + String.format("%.2f", initPercCust) + " %" + "\n");
        }else if (fit == Benefits){
        	jtA.append("percCust: " + String.format("%.2f",(100 * mean) / initMean) + " %" + "\n");
        }
        
        My_price = price / getNUM_GENERATIONS();
        /*  System.out.println("mean: " + mean);
        System.out.println("initMean: " + initMean);
        System.out.println("variance: " + variance);
        System.out.println("initVariance: " + initVariance);
        System.out.println("stdDev: " + stdDev);
        System.out.println("initStdDev: " + initStdDev);
        System.out.println("custMean: " + custMean);
        System.out.println("percCust: " + percCust + " %");
        System.out.println("initPercCust: " + initPercCust + " %");*/
		
    }

	 public void generarDatosGUI() throws Exception {
		  TotalAttributes = añadir.getTotalAttributes();
		  CustomerProfiles = añadir.getCustomerProfiles();
	      añadir.setProfiles();
	      añadir.divideCustomerProfile();
	      Producers = añadir.getProducers();
			
		}
	  
	/*************************************** " AUXILIARY METHODS GENERATEINPUT()" ***************************************/

    /**
     * Creating the attributes and the possible values of them
     */
 /*   private static void generateAttributeValor(List sheetData) {

        int MIN_VAL = 1;

        double number_valors = 0.0;
        for (int i = 4; i < sheetData.size(); i++) {
            // System.out.println("Celda [" + i + ", 0]: ");

            if (number_valors == 0) {
                Cell cell = (Cell) ((List) sheetData.get(i)).get(0);
                if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                    number_valors = cell.getNumericCellValue() + 1;
                    TotalAttributes.add(new Attribute("Attribute " + (TotalAttributes.size() + 1), MIN_VAL, (int) number_valors - 1));
                } else if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
                    if (cell.getRichStringCellValue().equals("MMM"))
                        break;
                }
            }
            number_valors--;
        }
    }*/
    
    public void showAttributes(JTextArea jTextArea) {
    	jTextArea.setText("");
        for (int k = 0; k < TotalAttributes.size(); k++) {
        	jTextArea.append(TotalAttributes.get(k).getName() + "\n" + 
                              "MIN: " + TotalAttributes.get(k).getMIN() + "\n" + 
        			          "MAX: " + TotalAttributes.get(k).getMAX() + "\n");
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
            		jTextArea.append("Value -> " + cp.getSubProfiles().get(j).getValueChosen().get(TotalAttributes.get(z)) + "\n");
            	
            	}
            }
    	}
    	jTextArea.repaint();
    }
    

    public void showProducers(JTextArea jTextArea) {
    	jTextArea.setText("");
    	for (int i = 0; i < Producers.size(); i++) {
            Producer p = Producers.get(i);
            jTextArea.append("PRODUCTOR " + (i + 1) + "\n");
            for (int j = 0; j < p.getAvailableAttribute().size(); j++) {
            	jTextArea.append(p.getAvailableAttribute().get(j).getName() + "\n" + "Value -> " + p.getProduct().getAttributeValue().get(TotalAttributes.get(j)) + "\n");
    
            }
        }
    	jTextArea.repaint();
    }
    

   /* private static void showExcelData(List sheetData) {
        // Iterates the data and print it out to the console.
        for (int i = 0; i < sheetData.size(); i++) {
            List list = (List) sheetData.get(i);
            for (int j = 0; j < list.size(); j++) {
                Cell cell = (Cell) list.get(j);
                if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                    System.out.print(cell.getNumericCellValue());
                } else if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
                    System.out.print(cell.getRichStringCellValue());
                } else if (cell.getCellType() == Cell.CELL_TYPE_BOOLEAN) {
                    System.out.print(cell.getBooleanCellValue());
                }
                if (j < list.size() - 1) {
                    System.out.print(", ");
                }
            }
            System.out.println("");
        }
    }*/


    /**
     * Creating a random product
     */
    private Product createRndProduct(ArrayList<Attribute> availableAttribute) {
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
        product.setPrice(calculatePrice(product));
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
        product.setPrice(calculatePrice(product));
        return product;
    }


    /**
     * Chosing an attribute near to the customer profiles given
     */
    private int chooseAttribute(int attrInd, ArrayList<Integer> custProfInd, ArrayList<Attribute> availableAttrs) {
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
    private int getMaxAttrVal(int attrInd, ArrayList<Integer> possibleAttr, ArrayList<Attribute> availableAttr) {

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
    private void createInitPopu() throws Exception {
        Population = new ArrayList<>();
        Fitness = new ArrayList<>();

        Population.add((Producers.get(MY_PRODUCER).getProduct()).clone());
        if (fit == customers)
            Fitness.add(computeWSC(Population.get(MY_PRODUCER), MY_PRODUCER));
        else
            Fitness.add(computeBenefits(Population.get(MY_PRODUCER), MY_PRODUCER));
       
        BestWSC = Fitness.get(MY_PRODUCER);
        Initial_Results.add(BestWSC);

        for (int i = 1; i < getNUM_POPULATION(); i++) {

	            if (i % 2 == 0) /*We create a random product*/
	                Population.add(createRndProduct(Producers.get(MY_PRODUCER).getAvailableAttribute()));
	            else /*We create a near product*/
	                Population.add(createNearProduct(Producers.get(MY_PRODUCER).getAvailableAttribute(), (int) (CustomerProfiles.size() * Math.random())));  /////////??verificar//////////
	            
            if (fit == customers)
                Fitness.add(computeWSC(Population.get(i), MY_PRODUCER));
            else
                Fitness.add(computeBenefits(Population.get(i), MY_PRODUCER));

            if (Fitness.get(i) > BestWSC) {
                BestWSC = Fitness.get(i);
                Producers.get(MY_PRODUCER).setProduct(Population.get(i).clone());
            }
        }
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
    private int computeWSC(Product product, int prodInd) throws Exception {
        int wsc = 0;
        boolean isTheFavourite;
        int meScore, score, k, numTies;

        for (int i = 0; i < CustomerProfiles.size(); i++) {
            for (int j = 0; j < CustomerProfiles.get(i).getSubProfiles().size(); j++) {
                isTheFavourite = true;
                numTies = 1;
                meScore = scoreProduct(CustomerProfiles.get(i).getSubProfiles().get(j), product);
                k = 0;
                while (isTheFavourite && k < Producers.size()) {
                    if (k != prodInd) {

                        score = scoreProduct(CustomerProfiles.get(i).getSubProfiles().get(j), Producers.get(k).product);

                        if (score > meScore)
                            isTheFavourite = false;

                        else if (score == meScore)
                            numTies += 1;
                    }
                    k++;
                }
				/*TODO: When there exists ties we loose some voters because of decimals (undecided voters)*/
                if (isTheFavourite) {
                    if ((j == CustomerProfiles.get(i).getSubProfiles().size()) && ((CustomerProfiles.get(i).getNumberCustomers() % getRESP_PER_GROUP()) != 0)) {
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
     * Computing the score of a product given the customer profile index
     * custProfInd and the product
     */
    private int scoreProduct(SubProfile subprofile, Product product) throws Exception {
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
    private int scoreAttribute(int numOfValsOfAttr, int valOfAttrCust, int valOfAttrProd) throws Exception {
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
            default:
                throw new Exception("Error in scoreAttribute() function: " +
                        "Number of values of the attribute unexpected");
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
            
            if (fit == customers)
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
     * Metodo que dado un padre y una madre los cruza para obtener un hijo.
     * Para cada posiciï¿½n del array eligiremos aleatoriamente si el hijo heredarï¿½
     * esa posiciï¿½n del padre o de la madre.
     */
    private Product breed(int father, int mother) {
        Product son = new Product();
		/*Random value in range [0,100)*/
        int crossover = (int) (100 * Math.random());
        int rndVal;

        if (crossover <= getCROSSOVER_PROB()) {//El hijo sera una mezcla de la madre y el padre
            HashMap<Attribute, Integer> crossover_attributeValue = new HashMap<>();
            for (int i = 0; i < TotalAttributes.size(); i++) { //With son

                rndVal = (int) (2 * Math.random()); /*Generamos aleatoriamente un 0 (padre) o un 1 (madre).*/
                if (rndVal == 0)
                    crossover_attributeValue.put(TotalAttributes.get(i), Population.get(father).getAttributeValue().get(TotalAttributes.get(i)));
                else
                    crossover_attributeValue.put(TotalAttributes.get(i), Population.get(mother).getAttributeValue().get(TotalAttributes.get(i)));
            }
            son.setAttributeValue(crossover_attributeValue);
        } else {//El hijo seria completamente igual a la madre o al padre
            rndVal = (int) (2 * Math.random()); /*Generamos aleatoriamente un 0 (padre) o un 1 (madre).*/

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
			/*Random value in range [0,100)*/
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
        mutant.setPrice(calculatePrice(mutant));
        return mutant;
    }


    /**
     * Mï¿½todo que dada la poblaciï¿½n original y una nueva poblaciï¿½n elige la siguente
     * ' generaciï¿½n de individuos. Actualizo la mejor soluciï¿½n encontrada en caso de mejorarla.
     */
    private ArrayList<Product> tournament(ArrayList<Product> newPopu, ArrayList<Integer> newFitness) {

        ArrayList<Product> nextGeneration = new ArrayList<>();
        for (int i = 0; i < getNUM_POPULATION(); i++) {

            if (Fitness.get(i) >= newFitness.get(i))
                nextGeneration.add((Population.get(i)).clone());
            else {

                nextGeneration.add((newPopu.get(i)).clone());
                Fitness.set(i, newFitness.get(i));// We update the fitness of the new individual

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

    private void showWSC() throws Exception {
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
    private double computeVariance(double mean) {
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
    	
	public void setNumExecutions(int exec){
		NUM_EXECUTIONS = exec;
	}

	public double getKNOWN_ATTRIBUTES() {
		return KNOWN_ATTRIBUTES;
	}


	public double getSPECIAL_ATTRIBUTES() {
		return SPECIAL_ATTRIBUTES;
	}


	public double getMUT_PROB_CUSTOMER_PROFILE() {
		return MUT_PROB_CUSTOMER_PROFILE;
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


	public void setSPECIAL_ATTRIBUTES(double sPECIAL_ATTRIBUTES) {
		SPECIAL_ATTRIBUTES = sPECIAL_ATTRIBUTES;
	}


	public void setMUT_PROB_CUSTOMER_PROFILE(double mUT_PROB_CUSTOMER_PROFILE) {
		MUT_PROB_CUSTOMER_PROFILE = mUT_PROB_CUSTOMER_PROFILE;
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

    /***************************************
     * " AUXILIARY METHODS TO CALCULATE THE PRICE"
     ***************************************/
	
    private int calculatePrice(Product product) {
    	int price_MyProduct = 0;

        for (int i = 1; i < Producers.size(); i++) {
            Product prod_competence = Producers.get(i).getProduct();
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
 
//		// An excel file name. You can create a file name with a full path
//		// information.
//		String filename = "EncuestasCIS.xlsx";
//		// Create an ArrayList to store the data read from excel sheet.
//		List sheetData = new ArrayList();
//		FileInputStream fis = null;
//		try {
//			// Create a FileInputStream that will be use to read the excel file.
//			fis = new FileInputStream(filename);
//
//			// Create an excel workbook from the file system.
//			XSSFWorkbook workbook = new XSSFWorkbook(fis);
//
//			// Get the first sheet on the workbook.
//			XSSFSheet sheet = workbook.getSheetAt(0);
//
//			/*
//			 * When we have a sheet object in hand we can iterator on each
//			 * sheet's rows and on each row's cells. We store the data read on
//			 * an ArrayList so that we can printed the content of the excel to
//			 * the console.
//			 */
//			Iterator rows = sheet.rowIterator();
//			while (rows.hasNext()) {
//				XSSFRow row = (XSSFRow) rows.next();
//				Iterator cells = row.cellIterator();
//				List data = new ArrayList();
//				while (cells.hasNext()) {
//					XSSFCell cell = (XSSFCell) cells.next();
//					// System.out.println("Aï¿½adiendo Celda: " +
//					// cell.toString());
//					data.add(cell);
//				}
//				sheetData.add(data);
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		} finally {
//			if (fis != null) {
//				try {
//					fis.close();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//		}

}