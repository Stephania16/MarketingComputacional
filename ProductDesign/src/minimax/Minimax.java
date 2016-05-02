package minimax;

import java.util.ArrayList;

import javax.swing.JTextArea;
import Comunes.Attribute;
import Comunes.CustomerProfile;
import Comunes.LinkedAttribute;
import Comunes.Producer;
import Comunes.Product;
import GUI.Añadir;
import GUI.InputRandom;
import GUI.OutputResults;

public class Minimax {

    private int MY_PRODUCER = 0;

 //   private int NEAR_CUST_PROFS = 5; //  Number of near customer profiles to generate a product

    private double KNOWN_ATTRIBUTES = 100; // % of attributes known for all producers
 //   private double SPECIAL_ATTRIBUTES = 33; // % of special attributes known for some producers
 //   public double MUT_PROB_CUSTOMER_PROFILE = 33; // % of mutated attributes in a customer profile

    private int MAX_DEPTH_0 = 4; //4; //Maximun depth of the minimax //depth 8 in initial
    private int MAX_DEPTH_1 = 2; //Maximun depth of the minimax //depth 2 in initial

    private int NUM_EXEC = 5;


    // INPUT VARIABLES
    private int mNAttrMod = 1; // Number of attributes the producer can modify (D)
    private int mPrevTurns = 5; // Number of previous turns to compute (tp)
    private int mNTurns = 5; // Number of turns to play (tf)

    private int mNAttr = 10; // Number of attributes
    private int mNProd = 2; // Number of producers
    private int mNCustProf; // Number of customer profiles
    // Represents the list of attributes, its possible values, and its possible valuations:
    // mAttributes(i)(j) = valuation for attribute number i, value number j
    private ArrayList<Attribute> TotalAttributes = new ArrayList<>();
    private ArrayList<Producer> Producers = new ArrayList<>();
    // Represents the customer profiles:
    // mCustProf(i)(j)(k) = valuation for the customer type number i,
    // attribute number j, value k of attribute (each attribute can take k possible values)
    private ArrayList<CustomerProfile> CustomerProfiles = new ArrayList<>();
    
    // STATISTICAL VARIABLES
    private ArrayList<Integer> mResults = new ArrayList<>();
    private ArrayList<Integer> Prices = new ArrayList<>();
    private ArrayList<Integer> mInitialResults = new ArrayList<>();
    public boolean maximizar = false;
    public static boolean isAttributesLinked = false;
    Añadir añadir = new Añadir();
    InputRandom in = new InputRandom();
    OutputResults out = new OutputResults();

    /************************
     * INITIAL METHOD
     **********************/

    public void start(JTextArea jtA, String archivo, boolean input_txt) throws Exception {
    	long inicio = System.currentTimeMillis();
        statisticsPDG(jtA, archivo, input_txt);
        long tiempo = System.currentTimeMillis()- inicio;
            
        double elapsedTimeSec = tiempo / 1.0E03;
        jtA.append("\nTiempo de ejecución = " + String.format("%.2f", elapsedTimeSec) + " seconds" + "\n");
    }

    public void statisticsPDG(JTextArea jtA, String archivo, boolean inputFile) throws Exception {
    	double mean, initMean;
		double sum = 0; /*sum of customers achieved*/
		int sumCust = 0; /*sum of the total number of customers*/
		double custMean;
		double variance;
		int initSum = 0;
		double stdDev;
		double percCust; /*% of customers achieved*/
		int price = 0;
		double My_price;
		double initPercCust; /* % of initial customers achieved */
		
		mResults = new ArrayList<>();
        mInitialResults = new ArrayList<>();
        Prices = new ArrayList<>();
		
        if(inputFile)
    	{
			int input = archivo.indexOf(".xml");
			if(input != -1) añadir.inputXML(archivo);
			else añadir.muestraContenido(archivo);
			generarDatosGUI();    		
    	}	
        
        for(int i = 0; i < getNumExecutions(); i++){
            playPDG(archivo,inputFile);
            sum += mResults.get(i);
            initSum = mInitialResults.get(i);
            sumCust += countCustomers() * getmNTurns() * 2;
            price += Prices.get(i);
        }
		
		mean = sum / getNumExecutions();
		initMean = initSum / getNumExecutions();
		variance = computeVariance(mean);
		stdDev = Math.sqrt(variance);
		custMean = sumCust / getNumExecutions();
		percCust = 100 * mean / custMean;
		initPercCust = 100 * initMean / custMean;
		My_price = price / getNumExecutions();
        
		jtA.setText("");
	    jtA.append("Num Ejecuciones: " + getNumExecutions() + "\r\n" + 
	     		   "Num atributos: " + getTotalAttributes().size() + "\r\n" + 
	     		   "Num productores: " + getProducers().size() + "\r\n" + 
	     		   "Num perfiles: " + getCustomerProfiles().size() + "\r\n" + 
	     		   "Number CustProf: " + añadir.getnum() + "\r\n" + 
	    		   "Depth Prod 0: " + getMAX_DEPTH_0() + "\r\n" + 
	               "Depth Prod 1: " + getMAX_DEPTH_1() + "\r\n" +
	               "Num atributos modificables: " + getmNAttrMod() + "\r\n" + 
	               "Num turnos previos: " + getmPrevTurns() + "\r\n" +
	               "Num productos: " + in.getNumber_Products() + "\r\n" +
	               "Atributos linkados: " + isAttributesLinked() + "\r\n" +
	    		   "************* RESULTS *************" + "\r\n" + 
	               "Num turnos: " + getmNTurns() + "\r\n" +
	    		   "Mean: " + String.format("%.2f", mean) + "\r\n" + 
	               "stdDev: " + String.format("%.2f", stdDev) + "\r\n" + 
	    		   "custMean: " + String.format("%.2f", custMean) + "\r\n" + 
	    		   "Price: " + String.format("%.2f", My_price) + " €" + "\r\n");	    			
	    if (isMaximizar()) { // fit == customers
			jtA.append("percCust: " + String.format("%.2f", percCust) + " %" + "\r\n" 
					 + "initPercCust: " + String.format("%.2f", initPercCust) + " %" + "\r\n");
		} else { // if (fit == Benefits)
			jtA.append("percCust: " + String.format("%.2f", ((100 * mean) / initMean)) + " %" + "\r\n");
		}
	    out.output(jtA, "Algoritmo Minimax");
    }
    
    private void generarDatosGUI() throws Exception {
        TotalAttributes = añadir.getTotalAttributes();
        CustomerProfiles = añadir.getCustomerProfiles();
        añadir.setProfiles();
        Producers = añadir.getProducers();
	}
    
    public void playPDG(String archivo, boolean inputFile) throws Exception {
    	if(!inputFile)
    	{	
    		if(añadir.isGenerarDatosEntrada()) generarDatosGUI();
    		else{
    			in.generateInput();
    			TotalAttributes = in.getTotalAttributes();
    			Producers = in.getProducers();
    			CustomerProfiles = in.getCustomerProfiles();
    		}
    	}
        playGame();
    }
    

    public void playGame() throws Exception {
    	 mInitialResults.add(computeWSC(Producers.get(MY_PRODUCER).getProduct(), MY_PRODUCER));

         for (int i = 0; i < getmNTurns(); i++) {
             for (int j = 0; j < Producers.size(); j++) {
                 changeProduct(j);
                 updateCustGathered(i);
             }
         }

         mResults.add(Producers.get(MY_PRODUCER).getNumber_CustomerGathered());
         Prices.add(calculatePrice(Producers.get(MY_PRODUCER).getProduct()));
    }

   
    public void showAttributes(JTextArea jTextArea1) {
    	jTextArea1.setText("");
        for (int k = 0; k < TotalAttributes.size(); k++) {
        	jTextArea1.append(TotalAttributes.get(k).getName() + "\n" + 
                              "MIN: " + TotalAttributes.get(k).getMIN() + "\n" + 
        			          "MAX: " + TotalAttributes.get(k).getMAX() + "\n");
        }
        jTextArea1.repaint();
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

    public void showCustomerProfile(JTextArea jTextArea1) {
    	jTextArea1.setText("");
    	for (int i = 0; i < CustomerProfiles.size(); i++) {
            CustomerProfile cp = CustomerProfiles.get(i);
            jTextArea1.append("CUSTOMER PROFILE " + (i + 1) + "\n");
           // System.out.println("PRODUCTOR " + (i + 1));
            for (int j = 0; j < cp.getScoreAttributes().size(); j++) {
            	jTextArea1.append(cp.getScoreAttributes().get(j).getName() + "\n");
            	for (int z = 0; z < cp.getScoreAttributes().get(j).getScoreValues().size(); z++) {
            		jTextArea1.append("Value -> " + cp.getScoreAttributes().get(j).getScoreValues().get(z) + "\n");
            	//System.out.println(p.getAvailableAttribute().get(j).getName() +  ":  Value -> "  + p.getProduct().getAttributeValue().get(TotalAttributes.get(j)));
            	}
            }
    	}
    	jTextArea1.repaint();
    }
    
   
    /************************
     * AUXILIARY METHOD PlayGame
     ***********************/

    public void changeProduct(int producerIndex) throws Exception {
    	int depth;
        Producer producer = Producers.get(producerIndex);

        if (producer == Producers.get(MY_PRODUCER))
            depth = getMAX_DEPTH_0();
        else
            depth = getMAX_DEPTH_1();

        ArrayList<Product> list_products = new ArrayList<>();
        for (int i = 0; i < Producers.size(); i++)
            list_products.add(Producers.get(i).getProduct().clone());

        StrAB ab = alphaBetaInit(list_products, producerIndex, depth, Integer.MIN_VALUE, Integer.MAX_VALUE);
        producer.getProduct().getAttributeValue().put(TotalAttributes.get(ab.getAttriInd()), ab.getAttrVal());

    }

    private StrAB alphaBetaInit(ArrayList<Product> list_products, int producerindex, int depth, int alpha, int beta) throws Exception {

        ArrayList<StrAB> abList = new ArrayList<>();
        StrAB ab = new StrAB();
        int nCustGathered;
        boolean repetedChild = false; // To prune repeated childs
        Producer producer = Producers.get(producerindex);

        for (int attrInd = 0; attrInd < TotalAttributes.size(); attrInd++) {
            for (int attrVal = 0; attrVal < TotalAttributes.get(attrInd).getMAX(); attrVal++) {
                if (producer.AvailableAttribute.get(attrInd).getAvailableValues().get(attrVal)) {
                    if (producer.getProduct().getAttributeValue().get(TotalAttributes.get(attrInd)) != attrVal || !repetedChild) {

                        if (producer.getProduct().getAttributeValue().get(TotalAttributes.get(attrInd)) == attrVal)
                            repetedChild = true;

                        //Computing Childs
                        ArrayList<Product> childs = new ArrayList<>();
                        for (int i = 0; i < list_products.size(); i++)
                            childs.add(list_products.get(i));

                        childs.get(producerindex).getAttributeValue().put(TotalAttributes.get(attrInd), attrVal);

                        if (!isMaximizar())
                            childs.get(producerindex).setPrice(calculatePrice(childs.get(producerindex)));

                        if (!isMaximizar())
                            nCustGathered = computeBenefits(childs.get(producerindex), 0);
                        else
                            nCustGathered = computeWSC(childs.get(producerindex), 0);

                        ab.setAlphaBeta(alphaBeta(childs, nCustGathered, producerindex, (producerindex + 1) % 2, depth - 1, alpha, beta, false));
                        ab.setAttriInd(attrInd);
                        ab.setAttrVal(attrVal);

                        abList.add(ab);
                        alpha = Math.max(alpha, ab.getAlphaBeta());
                    }
                }
            }
        }
        return bestMovement(abList, alpha);
    }

    private int alphaBeta(ArrayList<Product> products, int nCustGathered, int prodInit, int prodIndex, int depth, int alpha, int beta, boolean maximizingPlayer) throws Exception {

        boolean exitFor;
        int wsc;
        boolean repeatedChild = false; // To prune repeated  childs
        Producer producer = Producers.get(prodIndex);

        // It is a terminal node
        if (depth == 0)
            return nCustGathered;

        for (int attrInd = 0; attrInd < TotalAttributes.size(); attrInd++){
            exitFor = false;
            for (int attrVal = 0; attrVal < TotalAttributes.get(attrInd).getMAX(); attrVal++) {
                if (producer.AvailableAttribute.get(attrInd).getAvailableValues().get(attrVal)) {
                    if (producer.getProduct().getAttributeValue().get(TotalAttributes.get(attrInd)) != attrVal || !repeatedChild) {

                        if (producer.getProduct().getAttributeValue().get(TotalAttributes.get(attrInd)) == attrVal)
                            repeatedChild = true;


                        //Computing Childs
                        ArrayList<Product> childs = new ArrayList<>();
                        for (int i = 0; i < products.size(); i++)
                            childs.add(products.get(i));

                        childs.get(prodIndex).getAttributeValue().put(TotalAttributes.get(attrInd), attrVal);

                        if (!isMaximizar())
                            childs.get(prodIndex).setPrice(calculatePrice(childs.get(prodIndex)));


                        if (!isMaximizar())
                            wsc = computeBenefits(childs.get(prodIndex), prodInit);
                        else
                            wsc = computeWSC(childs.get(prodIndex), prodInit);

                        if (maximizingPlayer){
                            alpha = Math.max(alpha, alphaBeta(childs, nCustGathered + wsc, prodInit, (prodIndex + 1) % 2, depth - 1, alpha, beta, false));

                            if(beta < alpha){
                                exitFor = true;
                                break;
                            }
                        }else{
                            beta = Math.max(beta, alphaBeta(childs, nCustGathered + wsc, prodInit, (prodIndex + 1) % 2, depth - 1, alpha, beta, false));

                            if(beta < alpha){
                                exitFor = true;
                                break;
                            }
                        }
                    }
                }
            }
            if(exitFor)
                break;
        }

        if (maximizingPlayer)
            return alpha;
        else
            return beta;
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
             isTheFavourite = true;
             numTies = 1;
             meScore = scoreProduct(CustomerProfiles.get(i), product);
             
             if (isAttributesLinked())
                 meScore += scoreLinkedAttributes(CustomerProfiles.get(i).getLinkedAttributes(), product);
             
             k = 0;
             while (isTheFavourite && k < Producers.size()) {
                 if (k != prodInd) {

                     score = scoreProduct(CustomerProfiles.get(i), Producers.get(k).getProduct());

                     if (isAttributesLinked())
                         score += scoreLinkedAttributes(CustomerProfiles.get(i).getLinkedAttributes(), product);
                     
                     if (score > meScore)
                         isTheFavourite = false;

                     else if (score == meScore)
                         numTies += 1;
                 }
                 k++;
             }
 				/*TODO: When there exists ties we loose some voters because of decimals (undecided voters)*/
             if (isTheFavourite)
                 wsc += CustomerProfiles.get(i).getNumberCustomers() / numTies;
         }

         return wsc;
    }


    /**
     * Computing the score of a product given the customer profile index
     * custProfInd and the product
     */
    private int scoreProduct(CustomerProfile profile, Product product) throws Exception {
        int score = 0;
        for (int i = 0; i < TotalAttributes.size(); i++)
            score += profile.getScoreAttributes().get(i).getScoreValues().get(product.getAttributeValue().get(TotalAttributes.get(i)));

        return score;
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
    
    
    private StrAB bestMovement(ArrayList<StrAB> abList, int best) {
        StrAB ab = new StrAB();

        for(int i = 0; i < abList.size(); i++){
            if(abList.get(i).getAlphaBeta() == best){
                ab.setAttrVal(abList.get(i).getAttrVal());
                ab.setAttriInd(abList.get(i).getAttriInd());
                ab.setAlphaBeta(abList.get(i).getAlphaBeta());
            }
        }
        return ab;
    }

    private void updateCustGathered(int turn) throws Exception {
        ArrayList<Product> list_products = new ArrayList<>();
        for (int i = 0; i < Producers.size(); i++)
            list_products.add(Producers.get(i).getProduct().clone());

        for(int i = 0;i < Producers.size(); i++){
            int wsc = computeWSC(Producers.get(i).getProduct(), i);

            if(Producers.get(i).getCustomersGathered().size() == getmPrevTurns() * 2){
                Producers.get(i).getCustomersGathered().remove(0);
            }

            Producers.get(i).getCustomersGathered().add(wsc);
            //TODO write results
        }
    }
    /**
     * Computing the variance
     */
    private double computeVariance(double mean) {
        double sqrSum = 0;
        for (int i = 0; i < getNumExecutions(); i++) {
            sqrSum += Math.pow(mResults.get(i) - mean, 2);
        }
        return (sqrSum / getNumExecutions());
    }

    public int countCustomers(){
        int total = 0;
        for(int i = 0; i < CustomerProfiles.size(); i++)
            total += CustomerProfiles.get(i).getNumberCustomers();

        return total;
    }
    
    /***************************************
     * " AUXILIARY METHODS GETTERS Y SETTERS
     ***************************************/
	public int getNumExecutions() {
		return NUM_EXEC;
	}
    	
	public void setNumExecutions(int exec){
		NUM_EXEC = exec;
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

	public double getKNOWN_ATTRIBUTES() {
		return KNOWN_ATTRIBUTES;
	}

/*	public double getMUT_PROB_CUSTOMER_PROFILE() {
		return MUT_PROB_CUSTOMER_PROFILE;
	}*/

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

	public void setKNOWN_ATTRIBUTES(double kNOWN_ATTRIBUTES) {
		KNOWN_ATTRIBUTES = kNOWN_ATTRIBUTES;
	}

/*	public void setMUT_PROB_CUSTOMER_PROFILE(double mUT_PROB_CUSTOMER_PROFILE) {
		MUT_PROB_CUSTOMER_PROFILE = mUT_PROB_CUSTOMER_PROFILE;
	}*/

	public void setMAX_DEPTH_0(int mAX_DEPTH_0) {
		MAX_DEPTH_0 = mAX_DEPTH_0;
	}

	public void setMAX_DEPTH_1(int mAX_DEPTH_1) {
		MAX_DEPTH_1 = mAX_DEPTH_1;
	}

	public void setmPrevTurns(int mPrevTurns) {
		this.mPrevTurns = mPrevTurns;
	}

	public void setmNTurns(int mNTurns) {
		this.mNTurns = mNTurns;
	}

	public int getmNAttrMod() {
		return mNAttrMod;
	}

	public void setmNAttrMod(int mNAttrMod) {
		this.mNAttrMod = mNAttrMod;
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

	public void setAttributesLinked(boolean isAttributesLinked) {
		Minimax.isAttributesLinked = isAttributesLinked;
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
}
