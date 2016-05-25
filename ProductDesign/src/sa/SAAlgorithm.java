package sa;

import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JTextArea;
import general.Algorithm;
import general.Attribute;
import general.Product;

public class SAAlgorithm extends Algorithm{

    private final double Start_TEMP = 1000;
    private double TEMPERATURE = Start_TEMP;
    private double coolingRate = 0.003;
    private int CHANGE_ATTRIBUTE_PROB = 40;
    public static int number_Products = 1;

    /* STATISTICAL VARIABLES */
    private ArrayList<ArrayList<Integer>> Results = new ArrayList<>();
    private ArrayList<ArrayList<Integer>> Initial_Results = new ArrayList<>();
    private ArrayList<ArrayList<Integer>> Prices = new ArrayList<>();

    public void statisticsAlgorithm(JTextArea jtA, String archivo, boolean inputFile) throws Exception{

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
            if (i != 0) /*We reset myPP and create a new product as the first product*/ {
                ArrayList<Product> products = new ArrayList<>();
                for (int k = 0; k < getNumber_Products(); k++)
                    products.add(createNearProduct(Producers.get(MY_PRODUCER).getAvailableAttribute(), (int) (CustomerProfiles.size() * Math.random())));
                Producers.get(MY_PRODUCER).setProducts(products);
            }

            solveSA();

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
				+ "TEMPERATURE: " + getTEMPERATURE() + "\r\n" 
				+ "coolingRate: " + getCoolingRate() + "\r\n"
				+ "CHANGE_ATTRIBUTE_PROB: " + getCHANGE_ATTRIBUTE_PROB() + " %" + "\r\n"
				+ "Atributos conocidos: " + getKNOWN_ATTRIBUTES() + " %" + "\r\n" 
				+ "Atributos especiales: " + in.getSPECIAL_ATTRIBUTES() + " %" + "\r\n"
				+ "% Mutación de atributos: " + in.getMUT_PROB_CUSTOMER_PROFILE() + " %" + "\r\n" 
				+ "Num grupos de perfil: " + getRESP_PER_GROUP() + "\r\n" 
				+ "Num productos: " + in.getNumber_Products() + "\r\n" 
				+ "Atributos linkados: " + isAttributesLinked() + "\r\n" 
				+ "Centroides: " + inWeka.getIndexProfiles().toString() + "\r\n"
				+ "************* RESULTS *************" + "\r\n"  
				 + "Mean: " + meanTXT + "\r\n"
				 + "initMean: " + initMeanTXT + "\r\n"
				 + "stdDev: " + stdDevTXT + "\r\n"
				 + "initStdDev: " + initStdDevTXT + "\r\n"
			//	 + "percCust: " + percCustTXT + "\r\n"
			//	 + "initPercCust: " + initPercCustTXT + "\r\n"
				 + "My_priceString: " + priceTXT + "\r\n");
        if (isMaximizar()) { 
			jtA.append("percCust: " + percCustTXT + "\r\n"
					 + "initPercCust: " + initPercCustTXT  + "\r\n");
		} else { 
			jtA.append("percCust: " + percCustTXT + "\r\n");
		}
		out.output(jtA, "Algoritmo Simulated Annealing");
        
    }

    private void solveSA() throws Exception {

        ArrayList<Integer> initial = new ArrayList<>();
        for (int i = 0; i < Producers.get(MY_PRODUCER).getProducts().size(); i++) {
            if (isMaximizar())
                initial.add(computeWSC(Producers.get(MY_PRODUCER).getProducts().get(i), MY_PRODUCER, i));
            else
                initial.add(computeBenefits(Producers.get(MY_PRODUCER).getProducts().get(i), MY_PRODUCER, i));
        }
        Initial_Results.add(initial);


        for (int i = 0; i < Producers.get(MY_PRODUCER).getProducts().size(); i++) {
            Product BestProductFound = Producers.get(MY_PRODUCER).getProducts().get(i).clone();
            while (TEMPERATURE > 1) {

               // Log.d("Temperature", TEMPERATURE + "");
                Product originProduct = Producers.get(MY_PRODUCER).getProducts().get(i);
                HashMap<Attribute, Integer> AUXattributeValue = new HashMap<>();
                for (int q = 0; q < TotalAttributes.size(); q++) {
                    AUXattributeValue.put(TotalAttributes.get(q), originProduct.getAttributeValue().get(TotalAttributes.get(q)));
                }
                Product new_product = new Product(AUXattributeValue);
                new_product.setPrice(originProduct.getPrice());


                //CHANGE SOME ATTRIBUTES
                for (int j = 0; j < TotalAttributes.size(); j++) {
                    if ((Math.random() * 100) < CHANGE_ATTRIBUTE_PROB) {
                        int new_attr_value = (int) (Math.random() * TotalAttributes.get(j).getMAX());
                        new_product.getAttributeValue().put(TotalAttributes.get(j), new_attr_value);
                    }
                }

                int old_energy;
                int new_energy;
                if (isMaximizar()){
                    old_energy = computeWSC(Producers.get(MY_PRODUCER).getProducts().get(i), MY_PRODUCER, i);
                    new_energy = computeWSC(new_product, MY_PRODUCER, i);
                }else{
                    old_energy = computeBenefits(Producers.get(MY_PRODUCER).getProducts().get(i), MY_PRODUCER, i);
                    new_energy = computeBenefits(new_product, MY_PRODUCER, i);
                }

                //CALCULATE THE ACCEPTED FUNCTION
                if (acceptanceProbability(old_energy, new_energy) > Math.random()) {
                    Producers.get(MY_PRODUCER).getProducts().set(i, new_product);

                    //ACTUALIZE THE BEST
                    if (isMaximizar()) {
                        if (computeWSC(Producers.get(MY_PRODUCER).getProducts().get(i), MY_PRODUCER, i) > computeWSC(BestProductFound, MY_PRODUCER, i))
                            BestProductFound = Producers.get(MY_PRODUCER).getProducts().get(i);
                    }else{
                        if (computeBenefits(Producers.get(MY_PRODUCER).getProducts().get(i), MY_PRODUCER, i) > computeBenefits(BestProductFound, MY_PRODUCER, i))
                            BestProductFound = Producers.get(MY_PRODUCER).getProducts().get(i);
                    }
                }

                // Cool system
                TEMPERATURE *= 1 - coolingRate;
            }
            Producers.get(MY_PRODUCER).getProducts().set(i, BestProductFound);
            TEMPERATURE = Start_TEMP;
        }


        ArrayList<Integer> result = new ArrayList<>();
        for (int i = 0; i < Producers.get(MY_PRODUCER).getProducts().size(); i++) {
            if (isMaximizar())
                result.add(computeWSC(Producers.get(MY_PRODUCER).getProducts().get(i), MY_PRODUCER, i));
            else
                result.add(computeBenefits(Producers.get(MY_PRODUCER).getProducts().get(i), MY_PRODUCER, i));
        }
        Results.add(result);
        showWSC();

        //Set prices
        ArrayList<Integer> prices = new ArrayList<>();
        for (int i = 0; i < Producers.get(MY_PRODUCER).getProducts().size(); i++) {
            int price_MyProduct = inter.calculatePrice(Producers.get(MY_PRODUCER).getProducts().get(i), getTotalAttributes(), getProducers());
            Producers.get(MY_PRODUCER).getProducts().get(i).setPrice(price_MyProduct);
            prices.add(price_MyProduct);
        }
        Prices.add(prices);
    }

    private double acceptanceProbability(int OLD_fitness, int NEW_fitness) {
        double ret;
        if (NEW_fitness > OLD_fitness)
            ret = 1;
        else
            ret = Math.exp((NEW_fitness - OLD_fitness) / TEMPERATURE);

        return ret;
    }


    private Integer computeBenefits(Product product, int myProducer, int productIndex) throws Exception {
        return computeWSC(product, myProducer, productIndex) * product.getPrice();
    }

    /***
     * Computing the weighted score of the producer
     * prodInd is the index of the producer
     *
     * @throws Exception
     **/
    private int computeWSC(Product product, int prodInd, int productIndex) throws Exception {
        int wsc = 0;
        boolean isTheFavourite;
        int meScore, score, k, p, numTies;

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
                        if (k != MY_PRODUCER || p != productIndex) {

                            score = scoreProduct(CustomerProfiles.get(i).getSubProfiles().get(j), Producers.get(k).getProducts().get(p));

                            if (isAttributesLinked())
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



    /*************************************** " AUXILIARY METHODS STATISTICSPD()" ***************************************/

    /**
     * Computing the variance
     */
    public double computeVariance(double mean) {//TODO me fijo solo en el primero
        double sqrSum = 0;
        for (int i = 0; i < getNumExecutions(); i++) {
            sqrSum += Math.pow(Results.get(i).get(0) - mean, 2);
        }
        return (sqrSum / getNumExecutions());
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
                wsc = computeWSC(Producers.get(i).getProducts().get(j), i, j);
                wscSum += wsc;
            }
        }
    }
    
    public int getNumber_Products() {
		return number_Products;
	}

	public void setNumber_Products(int number) {
		number_Products = number;
	}

	public double getTEMPERATURE() {
		return TEMPERATURE;
	}

	public double getCoolingRate() {
		return coolingRate;
	}

	public int getCHANGE_ATTRIBUTE_PROB() {
		return CHANGE_ATTRIBUTE_PROB;
	}

	public void setTEMPERATURE(double tEMPERATURE) {
		TEMPERATURE = tEMPERATURE;
	}

	public void setCoolingRate(double coolingRate) {
		this.coolingRate = coolingRate;
	}

	public void setCHANGE_ATTRIBUTE_PROB(int cHANGE_ATTRIBUTE_PROB) {
		CHANGE_ATTRIBUTE_PROB = cHANGE_ATTRIBUTE_PROB;
	}

}
