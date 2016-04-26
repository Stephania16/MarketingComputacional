package minimax;

import java.util.ArrayList;

import javax.swing.JTextArea;
import Comunes.Attribute;
import Comunes.CustomerProfile;
import Comunes.Producer;
import Comunes.Product;
import GUI.A�adir;
import GUI.InputRandom;

public class Minimax {

    private int MY_PRODUCER = 0;

 //   private int NEAR_CUST_PROFS = 5; //  Number of near customer profiles to generate a product

    private double KNOWN_ATTRIBUTES = 100; // % of attributes known for all producers
 //   private double SPECIAL_ATTRIBUTES = 33; // % of special attributes known for some producers
 //   public double MUT_PROB_CUSTOMER_PROFILE = 33; // % of mutated attributes in a customer profile

    private int MAX_DEPTH_0 = 4; //Maximun depth of the minimax //depth 8 in initial
    private int MAX_DEPTH_1 = 2; //Maximun depth of the minimax //depth 2 in initial

    private int NUM_EXEC = 20;


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
    private ArrayList<Integer> mInitialResults = new ArrayList<>();
    A�adir a�adir = new A�adir();
    InputRandom in = new InputRandom();

    /************************
     * INITIAL METHOD
     **********************/

    public void start(JTextArea jtA, String archivo, boolean input_txt) throws Exception {
    	long inicio = System.currentTimeMillis();
        statisticsPDG(jtA, archivo, input_txt);
        long tiempo = System.currentTimeMillis()- inicio;
            
        double elapsedTimeSec = tiempo / 1.0E03;
        jtA.append("\nTiempo de ejecuci�n = " + String.format("%.2f", elapsedTimeSec) + " seconds" + "\n");
    }

    public void statisticsPDG(JTextArea jtA, String archivo, boolean input_txt) throws Exception {
    	double mean;
		double sum = 0; /*sum of customers achieved*/
		int sumCust = 0; /*sum of the total number of customers*/
		double custMean;
		double variance;
		double stdDev;
		double percCust; /*% of customers achieved*/
		
		if(input_txt)
    	{
			int input = archivo.indexOf(".xml");
			if(input != -1) a�adir.inputXML(archivo);
			else a�adir.muestraContenido(archivo);
			generarDatosGUI();    		
    	}
		
		mResults = new ArrayList<>();
        for(int i = 0; i < getNumExecutions(); i++){
            playPDG(archivo, input_txt);
            sum += mResults.get(i);
            sumCust += countCustomers() * getmNTurns() * 2;
        }
		
		mean = sum / getNumExecutions();
		variance = computeVariance(mean);
		stdDev = Math.sqrt(variance);
		custMean = sumCust / getNumExecutions();
		percCust = 100 * mean / custMean;
        
		jtA.setText("");
	    jtA.append("Num Ejecuciones: " + getNumExecutions() + "\n" + 
	     		   "Num atributos: " + getTotalAttributes().size() + "\n" + 
	     		   "Num productores: " + getProducers().size() + "\n" + 
	     		   "Num perfiles: " + getCustomerProfiles().size() + "\n" + 
	     		   "Number CustProf: " + a�adir.getnum() + "\n" + 
	    		   "Depth Prod 0: " + getMAX_DEPTH_0() + "\n" + 
	               "Depth Prod 1: " + getMAX_DEPTH_1() + "\n" +
	               "Num atributos modificables: " + getmNAttrMod() + "\n" + 
	               "Num turnos previos: " + getmPrevTurns() + "\n" +
	    		   "************* RESULTS *************" + "\n" + 
	               "Num turnos: " + getmNTurns() + "\n" +
	    		   "Mean: " + String.format("%.2f", mean) + "\n" + 
	               "stdDev: " + String.format("%.2f", stdDev) + "\n" + 
	    		   "custMean: " + String.format("%.2f", custMean) + "\n" + 
	               "percCust: " + String.format("%.2f", percCust) + " %" + "\n");
		/*System.out.println("Depth Prod 0: " + MAX_DEPTH_0);
		System.out.println("Depth Prod 1: " + MAX_DEPTH_1);
		System.out.println("Mean: " + mean);
		System.out.println("stdDev: " + stdDev);
		System.out.println("custMean: " + custMean);
		System.out.println("percCust: " + percCust + " %");*/
    }
    
    private void generarDatosGUI() throws Exception {
        TotalAttributes = a�adir.getTotalAttributes();
        CustomerProfiles = a�adir.getCustomerProfiles();
        a�adir.setProfiles();
        Producers = a�adir.getProducers();
	}
    
    public void playPDG(String datos_txt, boolean input_txt) throws Exception {
    	if(!input_txt)
    	{	if(a�adir.isGenerarDatosEntrada()) generarDatosGUI();
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
	
    public void showProducers(JTextArea jTextArea1) {
    	jTextArea1.setText("");
    	for (int i = 0; i < Producers.size(); i++) {
            Producer p = Producers.get(i);
            jTextArea1.append("PRODUCTOR " + (i + 1) + "\n");
           // System.out.println("PRODUCTOR " + (i + 1));
            for (int j = 0; j < p.getAvailableAttribute().size(); j++) {
            	jTextArea1.append(p.getAvailableAttribute().get(j).getName() + "\n" + "Value -> " + p.getProduct().getAttributeValue().get(TotalAttributes.get(j)) + "\n");
            	//System.out.println(p.getAvailableAttribute().get(j).getName() +  ":  Value -> "  + p.getProduct().getAttributeValue().get(TotalAttributes.get(j)));
            }
        }
    	jTextArea1.repaint();
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
             k = 0;
             while (isTheFavourite && k < Producers.size()) {
                 if (k != prodInd) {

                     score = scoreProduct(CustomerProfiles.get(i), Producers.get(k).getProduct());

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

/*	public double getSPECIAL_ATTRIBUTES() {
		return SPECIAL_ATTRIBUTES;
	}

	public void setSPECIAL_ATTRIBUTES(double sPECIAL_ATTRIBUTES) {
		SPECIAL_ATTRIBUTES = sPECIAL_ATTRIBUTES;
	}
	*/
}
