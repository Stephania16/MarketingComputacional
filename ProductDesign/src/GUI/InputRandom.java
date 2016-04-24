package GUI;

import java.util.ArrayList;
import java.util.HashMap;

import Comunes.Attribute;
import Comunes.CustomerProfile;
import Comunes.Producer;
import Comunes.Product;
import genetic.SubProfile;

public class InputRandom {

	 private static ArrayList<Attribute> TotalAttributes = new ArrayList<>();
	    private static ArrayList<Producer> Producers = new ArrayList<>();
	    private static ArrayList<CustomerProfile> CustomerProfiles = new ArrayList<>();
	    A�adir a�adir = new A�adir();

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

	    static int RESP_PER_GROUP = 20; /* * We divide the respondents of each
	                                             * profile in groups of
												 * RESP_PER_GROUP respondents
												 */
	    static int NEAR_CUST_PROFS = 5; //4
	    
	    /**Variables minimax*/
	    private int MIN_ATTR = 2; // Minimum number of attributes values
	    private int MAX_ATTR = 2; // Maximum number of attributes values

	    private int MIN_VAL = 0; // Minimum valuation of an attribute
	    private int MAX_VAL = 5; // Maximum valuation of an attribute

	    private int MIN_NUM_CUST = 0; // Minimum number of customers of a profile
	    private int MAX_NUM_CUST = 10000; // Maximum number of customers of a profile
	    
	    /**
	     * Generating the input data
	     *
	     * @throws Exception
	     */
	    public void generate() throws Exception {
	        generateAttributeRandom();
	        generateCustomerProfiles();
	        generareNumberOfCustomers();
	        divideCustomerProfile();
	        generateProducers();
	    }
	    
	    
	    /*******************
	     * INPUT METHODS MINIMAX
	     ************************/
	    public void generateInput() {
	        
	      //  mNAttr = 10;
	      //  mNProd = 2;
	        genAttrVal();
	        genCustomerProfiles();
	        genCustomerProfilesNum();
	        genProducers();
	    }


	    private void generateAttributeRandom() {
	        TotalAttributes.clear();
	        for (int i = 0; i < a�adir.getnum_attr(); i++) {

	            double rnd = Math.random();
	            if (rnd < 0.34)
	                TotalAttributes.add(new Attribute("Atributo " + (i + 1), 1, 3));
	            else if (rnd < 0.67)
	                TotalAttributes.add(new Attribute("Atributo " + (i + 1), 1, 4));
	            else
	                TotalAttributes.add(new Attribute("Atributo " + (i + 1), 1, 5));
	        }
	    }

	    /**
	     * Creating different customer profiles
	     */
	    private void generateCustomerProfiles() {
	        CustomerProfiles.clear();

	        //Generate 4 random Customer Profile
	        for (int i = 0; i < 4; i++) {
	            ArrayList<Attribute> attrs = new ArrayList<>();
	            for (int j = 0; j < TotalAttributes.size(); j++) {
	                Attribute attr = new Attribute(TotalAttributes.get(j).getName(), TotalAttributes.get(j).getMIN(), TotalAttributes.get(j).getMAX());

	                ArrayList<Integer> scoreValues = new ArrayList<>();
	                for (int k = 0; k < attr.MAX; k++) {
	                    int random = (int) (attr.MAX * Math.random());
	                    scoreValues.add(random);
	                }
	                attr.setScoreValues(scoreValues);
	                attrs.add(attr);
	            }
	            CustomerProfiles.add(new CustomerProfile(attrs));
	        }

	        //Create 2 mutants for each basic profile
	        for (int i = 0; i < 4; i++) {
	            CustomerProfiles.add(mutateCustomerProfile(CustomerProfiles.get(i)));
	            CustomerProfiles.add(mutateCustomerProfile(CustomerProfiles.get(i)));
	        }

	        //Creating 4 isolated profiles
	        for (int i = 0; i < 4; i++) {
	            ArrayList<Attribute> attrs = new ArrayList<>();
	            for (int j = 0; j < TotalAttributes.size(); j++) {
	                Attribute attr = new Attribute(TotalAttributes.get(j).getName(), TotalAttributes.get(j).getMIN(), TotalAttributes.get(j).getMAX());
	                ArrayList<Integer> scoreValues = new ArrayList<>();
	                for (int k = 0; k < attr.MAX; k++) {
	                    int random = (int) (attr.MAX * Math.random());
	                    scoreValues.add(random);
	                }
	                attr.setScoreValues(scoreValues);
	                attrs.add(attr);
	            }
	            CustomerProfiles.add(new CustomerProfile(attrs));
	        }
	    }

	    private CustomerProfile mutateCustomerProfile(CustomerProfile customerProfile) {
	        CustomerProfile mutant = new CustomerProfile(null);
	        ArrayList<Attribute> attrs = new ArrayList<>();
	        for (int i = 0; i < TotalAttributes.size(); i++) {
	            Attribute attr = new Attribute(TotalAttributes.get(i).getName(), TotalAttributes.get(i).getMIN(), TotalAttributes.get(i).getMAX());
	            ArrayList<Integer> scoreValues = new ArrayList<>();
	            for (int k = 0; k < attr.MAX; k++) {
	                if (Math.random() < (getMUT_PROB_CUSTOMER_PROFILE() / 100)) {
	                    int random = (int) (attr.MAX * Math.random());
	                    scoreValues.add(random);
	                } else
	                    scoreValues.add(customerProfile.getScoreAttributes().get(i).getScoreValues().get(k));
	            }
	            attr.setScoreValues(scoreValues);
	            attrs.add(attr);
	        }
	        mutant.setScoreAttributes(attrs);
	        return mutant;
	    }


	    private void generareNumberOfCustomers() {
	        for (int i = 0; i < a�adir.getnum_prof(); i++)
	            CustomerProfiles.get(i).setNumberCustomers((int) ((Math.random() * 100) + (Math.random() * 100)));
	    }


	    /**
	     * Dividing the customer profiles into sub-profiles
	     *
	     * @throws Exception
	     */
	    private void divideCustomerProfile() throws Exception {


	        int numOfSubProfile;
	        for (int i = 0; i < CustomerProfiles.size(); i++) {
	            ArrayList<SubProfile> subProfiles = new ArrayList<>();
	            numOfSubProfile = CustomerProfiles.get(i).getNumberCustomers() / getRESP_PER_GROUP();

	            if ((CustomerProfiles.get(i).getNumberCustomers() % getRESP_PER_GROUP()) != 0)
	                numOfSubProfile++;

	            for (int j = 0; j < numOfSubProfile; j++) { //We divide into sub-profiles
	                SubProfile subprofile = new SubProfile();
	                subprofile.setName("Subperfil " + (j + 1));// + ", Perfil " + (i+1));

	                HashMap<Attribute, Integer> valuesChosen = new HashMap<>();
	                for (int k = 0; k < TotalAttributes.size(); k++) //Each of the sub-profiles choose a value for each of the attributes
	                    valuesChosen.put(TotalAttributes.get(k), chooseValueForAttribute(CustomerProfiles.get(i).getScoreAttributes().get(k)));

	                subprofile.setValueChosen(valuesChosen);
	                subProfiles.add(subprofile);
	            }
	            CustomerProfiles.get(i).setSubProfiles(subProfiles);
	        }
	    }


	    /**
	     * Given an index of a customer profile and the index of an attribute we choose a value
	     * for that attribute of the sub-profile having into account the values of the poll
	     */
	    private Integer chooseValueForAttribute(Attribute attribute) throws Exception {

	        int total = 0;
	        int accumulated = 0;
	        boolean found = false;

	        for (int i = 0; i < attribute.getScoreValues().size(); i++)
	            total += attribute.getScoreValues().get(i);

	        int rndVal = (int) (total * Math.random());

	        int value = 0;
	        while (!found) {
	            accumulated += attribute.getScoreValues().get(value);
	            if (rndVal <= accumulated)
	                found = true;
	            else
	                value++;
	            ;

	            if (value >= attribute.getScoreValues().size())
	                throw new Exception("Error 1 in chooseValueForAttribute() method: Value not found");

	        }

	        if (value >= attribute.getScoreValues().size())
	            throw new Exception("Error 2 in chooseValueForAttribute() method: Value not found");

	        return value;
	    }

	    public ArrayList<Attribute> getTotalAttributes() {
			return TotalAttributes;
		}


		public void setTotalAttributes(ArrayList<Attribute> totalAttributes) {
			TotalAttributes = totalAttributes;
		}


		public ArrayList<Producer> getProducers() {
			return Producers;
		}


		public void setProducers(ArrayList<Producer> producers) {
			Producers = producers;
		}


		public ArrayList<CustomerProfile> getCustomerProfiles() {
			return CustomerProfiles;
		}


		public void setCustomerProfiles(ArrayList<CustomerProfile> customerProfiles) {
			CustomerProfiles = customerProfiles;
		}


		/**
	     * Generating the producers
	     */
	    private void generateProducers() {
	        Producers.clear();

	        Producers = new ArrayList<>();
	        for (int i = 0; i < a�adir.getnum_prod(); i++) { //Creamos 10 productores random
	            Producer new_producer = new Producer();
	            new_producer.setName("Productor " + (i + 1));
	            new_producer.setAvailableAttribute(createAvailableAttributes());
	            new_producer.setProduct(createProduct(new_producer.getAvailableAttribute()));
	            Producers.add(new_producer);
	        }
	    }

	    /**
	     * Creating available attributes for the producer
	     */
	    private ArrayList<Attribute> createAvailableAttributes() {
	        ArrayList<Attribute> availableAttributes = new ArrayList<>();
	        int limit = (int) (TotalAttributes.size() * getKNOWN_ATTRIBUTES() / 100);

			/*All producers know the first ATTRIBUTES_KNOWN % of the attributes*/
	        for (int i = 0; i < limit; i++) {
	            Attribute attr = new Attribute(TotalAttributes.get(i).getName(), TotalAttributes.get(i).getMIN(), TotalAttributes.get(i).getMAX());
	            ArrayList<Boolean> availablevalues = new ArrayList<>();
	            for (int j = 0; j < attr.getMAX(); j++) {
	                availablevalues.add(true);
	            }

	            attr.setAvailableValues(availablevalues);
	            availableAttributes.add(attr);
	        }

			/*The remaining attributes are only known by SPECIAL_ATTRIBUTES % producers*/
	        for (int k = limit; k < TotalAttributes.size(); k++) {
	            Attribute attr = new Attribute(TotalAttributes.get(k).getName(), TotalAttributes.get(k).getMIN(), TotalAttributes.get(k).getMAX());
	            ArrayList<Boolean> availableValues = new ArrayList<>();

	            for (int j = 0; j < attr.getMAX(); j++) {
	                double rnd = Math.random();
	                double rndVal = Math.random();
					/*Furthermore, with a 50% of probabilities it can know this attribute*/
	                if (rndVal < (getSPECIAL_ATTRIBUTES() / 100) && rnd < 0.5)
	                    availableValues.add(true);
	                else
	                    availableValues.add(false);
	            }
	            attr.setAvailableValues(availableValues);
	            availableAttributes.add(attr);
	        }

	        return availableAttributes;
	    }

	    private Product createProduct(ArrayList<Attribute> availableAttrs) {

	        Product product = new Product(new HashMap<Attribute, Integer>());
	        ArrayList<Integer> customNearProfs = new ArrayList<>();

	        for (int i = 0; i < getNEAR_CUST_PROFS(); i++)
	            customNearProfs.add((int) Math.floor(CustomerProfiles.size() * Math.random()));

	        HashMap<Attribute, Integer> attrValues = new HashMap<>();

	        for (int j = 0; j < TotalAttributes.size(); j++)
	            attrValues.put(TotalAttributes.get(j), chooseAttribute(j, customNearProfs, availableAttrs)); //TotalAttributes.get(j) o availableAttrs.get(j)

	        product.setAttributeValue(attrValues);
	        product.setPrice((int)( Math.random() * 400) + 100);
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
	    
	    /**************
	     * INPUT METHODS MINIMAX
	     ***********************/
	    public void genAttrVal() {
	        TotalAttributes.clear();
	        for (int i = 0; i < a�adir.getnum_attr(); i++) {
	            int valueMax = (int) (Math.floor((MAX_ATTR - MIN_ATTR + 1) * Math.random()) + MIN_ATTR);
	            TotalAttributes.add(new Attribute("Atributo " + (i + 1), 1, valueMax));
	        }
	    }

	    
	    public void genCustomerProfiles() {
	        CustomerProfiles.clear();

	        //Generate 4 random Customer Profile
	        for (int i = 0; i < 4; i++) {
	            ArrayList<Attribute> attrs = new ArrayList<>();
	            for (int j = 0; j < TotalAttributes.size(); j++) {
	                Attribute attr = new Attribute(TotalAttributes.get(j).getName(), TotalAttributes.get(j).getMIN(), TotalAttributes.get(j).getMAX());

	                ArrayList<Integer> scoreValues = new ArrayList<>();
	                for (int k = 0; k < attr.MAX; k++) {
	                    int random = (int) (attr.MAX * Math.random());
	                    scoreValues.add(random);
	                }
	                attr.setScoreValues(scoreValues);
	                attrs.add(attr);
	            }
	            CustomerProfiles.add(new CustomerProfile(attrs));
	        }

	        //Create 2 mutants for each basic profile
	        for (int i = 0; i < 4; i++) {
	            CustomerProfiles.add(mutateCustomerProfile(CustomerProfiles.get(i)));
	            CustomerProfiles.add(mutateCustomerProfile(CustomerProfiles.get(i)));
	        }

	        //Creating 4 isolated profiles
	        for (int i = 0; i < 4; i++) {
	            ArrayList<Attribute> attrs = new ArrayList<>();
	            for (int j = 0; j < TotalAttributes.size(); j++) {
	                Attribute attr = new Attribute(TotalAttributes.get(j).getName(), TotalAttributes.get(j).getMIN(), TotalAttributes.get(j).getMAX());
	                ArrayList<Integer> scoreValues = new ArrayList<>();
	                for (int k = 0; k < attr.MAX; k++) {
	                    int random = (int) (attr.MAX * Math.random());
	                    scoreValues.add(random);
	                }
	                attr.setScoreValues(scoreValues);
	                attrs.add(attr);
	            }
	            CustomerProfiles.add(new CustomerProfile(attrs));
	        }
	    }
	    
	    private void genCustomerProfilesNum() {
	        for (int i = 0; i < CustomerProfiles.size(); i++) {
	            int number_customers = (int) (Math.floor(MAX_NUM_CUST - MIN_NUM_CUST + 1) * Math.random()) + MIN_NUM_CUST;
	            CustomerProfiles.get(i).setNumberCustomers(number_customers);
	        }
	    }

	    public void genProducers() {
	        Producers.clear();

	        Producers = new ArrayList<>();
	        for (int i = 0; i < a�adir.getnum_prod(); i++) { //Creamos 10 productores random
	            Producer new_producer = new Producer();
	            new_producer.setName("Productor " + (i + 1));
	            new_producer.setAvailableAttribute(createAvailableAttributes());
	            new_producer.setProduct(createProduct(new_producer.getAvailableAttribute()));
	            Producers.add(new_producer);
	        }
	    }


		
	    public static double getKNOWN_ATTRIBUTES() {
			return KNOWN_ATTRIBUTES;
		}


		public double getSPECIAL_ATTRIBUTES() {
			return SPECIAL_ATTRIBUTES;
		}


		public double getMUT_PROB_CUSTOMER_PROFILE() {
			return MUT_PROB_CUSTOMER_PROFILE;
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


		public void setRESP_PER_GROUP(int rESP_PER_GROUP) {
			RESP_PER_GROUP = rESP_PER_GROUP;
		}


		public void setNEAR_CUST_PROFS(int nEAR_CUST_PROFS) {
			NEAR_CUST_PROFS = nEAR_CUST_PROFS;
		}





}