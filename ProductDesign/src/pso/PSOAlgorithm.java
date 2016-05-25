package pso;

import java.util.ArrayList;
import java.util.Random;
import javax.swing.JTextArea;
import general.Algorithm;
import general.Product;


public class PSOAlgorithm extends Algorithm{
	    private static final int MY_BEST_PRODUCT = 0;

	    public int MAX_ITERATION = 60;
	    public int SWARM_SIZE = 20;

	    double W_UPPERBOUND = 1;
	    double W_LOWERBOUND = 0;
	    double C1 = 2.0;
	    double C2 = 2.0;

	    /* GA VARIABLES */
	    private ArrayList<Integer> BestWSC = new ArrayList<>(); /* Stores the best wsc found */
	    private ArrayList<Product> Population = new ArrayList<>();  //Private mPopu As List(Of List(Of Integer))
	    private ArrayList<Integer> Fitness = new ArrayList<>(); /* * mFitness(i) = wsc of mPopu(i) */

	    private ArrayList<Integer> ParticleBestWSC = new ArrayList<>(); /* Stores the best wsc found */
	    private ArrayList<Product> ProductBestWSC = new ArrayList<>(); /* Stores the best wsc found */
	  //  private Product BestProduct = new Product(); /* Stores the best wsc found */

	    /* STATISTICAL VARIABLES */
	    private ArrayList<ArrayList<Integer>> Results = new ArrayList<>();
	    private ArrayList<ArrayList<Integer>> Initial_Results = new ArrayList<>();
	    private ArrayList<ArrayList<Integer>> Prices = new ArrayList<>();
		public static int number_Products = 1;
		
		
	    public void statisticsAlgorithm(JTextArea jtA, String archivo, boolean inputFile) throws Exception {

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
					+ "Máx Iteraciones: " + getMAX_ITERATION() + "\r\n" 
					+ "Swarm Size: " + getSWARM_SIZE() + "\r\n"
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
					+ "Centroides: " + inWeka.getIndexProfiles().toString() + "\r\n"
					+ "************* RESULTS *************" + "\r\n"  
					+ "BestWSC: " + BestWSC + "\r\n" 
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
			out.output(jtA, "Algoritmo PSO");
	    }

	    private void solvePSO() throws Exception {

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

	    private void updateFitness() throws Exception {
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

	    private void createInitSwarm() throws Exception {
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

	        	for(int j = 0; j < inWeka.getIndexProfiles().size(); j++){
					createNearProductCluster(Producers.get(MY_PRODUCER).getAvailableAttribute(),inWeka.getIndexProfiles().get(j));
					
				}
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
	                wsc = computeWSC(Producers.get(i).getProducts().get(j), i);
	                wscSum += wsc;
	            }
	        }
	    }

		/*************************************** " GETTERS Y SETTERS OF ATTRIBUTES " ***************************************/
		
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
	
}
