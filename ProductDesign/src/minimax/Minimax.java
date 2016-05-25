package minimax;

import java.util.ArrayList;
import javax.swing.JTextArea;
import general.Algorithm;
import general.CustomerProfile;
import general.Producer;
import general.Product;

public class Minimax extends Algorithm{

    private int MAX_DEPTH_0 = 4; //4; //Maximun depth of the minimax //depth 8 in initial
    private int MAX_DEPTH_1 = 2; //Maximun depth of the minimax //depth 2 in initial



    // INPUT VARIABLES
    private int mNAttrMod = 1; // Number of attributes the producer can modify (D)
    private int mPrevTurns = 5; // Number of previous turns to compute (tp)
    private int mNTurns = 5; // Number of turns to play (tf)


    /************************
     * INITIAL METHOD
     **********************/
    public void statisticsAlgorithm(JTextArea jtA, String archivo, boolean inputFile) throws Exception {
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
		
		Results = new ArrayList<>();
		Initial_Results = new ArrayList<>();
        Prices = new ArrayList<>();
		
        if(inputFile)
    	{
			int input = archivo.indexOf(".xml");
			if(input != -1) inputGUI.inputXML(archivo);
			else inputGUI.inputTxt(archivo);
			generarDatosGUI();    		
    	}	
        
        for(int i = 0; i < getNumExecutions(); i++){
            playPDG(archivo,inputFile);
            sum += Results.get(i);
            initSum = Initial_Results.get(i);
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
	     		   "Number CustProf: " + inputGUI.getnum() + "\r\n" + 
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
	    if (isMaximizar()) {
			jtA.append("percCust: " + String.format("%.2f", percCust) + " %" + "\r\n" 
					 + "initPercCust: " + String.format("%.2f", initPercCust) + " %" + "\r\n");
		} else { 
			jtA.append("percCust: " + String.format("%.2f", ((100 * mean) / initMean)) + " %" + "\r\n");
		}
	    out.output(jtA, "Algoritmo Minimax");
    }
    
    public void playPDG(String archivo, boolean inputFile) throws Exception {
    	if(!inputFile)
    	{	
    		if(inputGUI.isGenerarDatosEntrada()) generarDatosGUI();
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
    	Initial_Results.add(computeWSC(Producers.get(MY_PRODUCER).getProduct(), MY_PRODUCER));

         for (int i = 0; i < getmNTurns(); i++) {
             for (int j = 0; j < Producers.size(); j++) {
                 changeProduct(j);
                 updateCustGathered(i);
             }
         }

         Results.add(Producers.get(MY_PRODUCER).getNumber_CustomerGathered());
         Prices.add(inter.calculatePrice(Producers.get(MY_PRODUCER).getProduct(), getTotalAttributes(), getProducers()));
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
                            childs.get(producerindex).setPrice(inter.calculatePrice(childs.get(producerindex), getTotalAttributes(), getProducers()));

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
                            childs.get(prodIndex).setPrice(inter.calculatePrice(childs.get(prodIndex), getTotalAttributes(), getProducers()));


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


    /***
   /  * Computing the weighted score of the producer
     * prodInd is the index of the producer
     *
     * @throws Exception
     **/
    public int computeWSC(Product product, int prodInd) throws Exception {
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

    public int countCustomers(){
        int total = 0;
        for(int i = 0; i < CustomerProfiles.size(); i++)
            total += CustomerProfiles.get(i).getNumberCustomers();

        return total;
    }
    
    /***************************************
     * " AUXILIARY METHODS GETTERS Y SETTERS
     ***************************************/

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

}
