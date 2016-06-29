package input;

import java.util.ArrayList;
import java.util.HashMap;

import general.Attribute;
import general.CustomerProfile;
import general.LinkedAttribute;
import general.Producer;
import general.Product;
import general.StoredData;
import genetic.SubProfile;

/** Clase que crea aleatoriamente los datos de entrada */
public class InputRandom {

	private static ArrayList<Attribute> TotalAttributes = new ArrayList<>();
	private static ArrayList<Producer> Producers = new ArrayList<>();
	private static ArrayList<CustomerProfile> CustomerProfiles = new ArrayList<>();
	InputGUI inputGUI = new InputGUI();
	private static final double PROB_ATTRIBUTE_LINKED = 10;
	static double KNOWN_ATTRIBUTES = 100; // porcentaje de atributos conocidos
											// para todos los productores
	static double SPECIAL_ATTRIBUTES = 33; // porcentaje de atributos especiales
											// conocidos para algunos los
											// productores
	static double MUT_PROB_CUSTOMER_PROFILE = 33; // porcentaje de mutacion de
													// atributos en un perfil
	static int RESP_PER_GROUP = 20; // dividir cada perfil en grupos de
									// encuestados
	static int NEAR_CUST_PROFS = 4;
	//public static int number_Products = 1;
	//public static boolean isAttributesLinked = false;

	/**
	 * Generar datos de entrada
	 *
	 * @throws Exception
	 */
	public void generate() throws Exception {
		generateAttributeRandom();
		StoredData.Atributos = TotalAttributes;
		
		generateCustomerProfiles();
		genCustomerProfilesNum();
        divideCustomerProfile();
        StoredData.Profiles = CustomerProfiles;
        
        generateProducers();
        StoredData.Producers = Producers;
	}


	/** Generar atributos aleatorios */
	private void generateAttributeRandom() {
		TotalAttributes.clear();
		for (int i = 0; i < inputGUI.getnum_attr(); i++) {

			double rnd = Math.random();
			if (rnd < 0.34)
				TotalAttributes.add(new Attribute("Atributo " + (i + 1), 1, 3));
			else if (rnd < 0.67)
				TotalAttributes.add(new Attribute("Atributo " + (i + 1), 1, 4));
			else
				TotalAttributes.add(new Attribute("Atributo " + (i + 1), 1, 5));
		}
	}

	/** Generar perfiles de clientes aleatorios */
	private void generateCustomerProfiles() {
		CustomerProfiles.clear();

		// Generate 4 random Customer Profile
		for (int i = 0; i < 4; i++) {
			ArrayList<Attribute> attrs = new ArrayList<>();
			for (int j = 0; j < TotalAttributes.size(); j++) {
				Attribute attr = new Attribute(TotalAttributes.get(j).getName(), TotalAttributes.get(j).getMIN(),
						TotalAttributes.get(j).getMAX());

				ArrayList<Integer> scoreValues = new ArrayList<>();
				for (int k = 0; k < attr.MAX; k++) {
					int random = (int) (attr.MAX * Math.random());
					scoreValues.add(random);
				}
				attr.setScoreValues(scoreValues);
				attrs.add(attr);
			}
			CustomerProfile custProf = new CustomerProfile(attrs);

			if (StoredData.isAttributesLinked) {
				ArrayList<LinkedAttribute> linkedAttributes = new ArrayList<>();
				for (int k = 0; k < TotalAttributes.size(); k++) {
					if (Math.random() < (PROB_ATTRIBUTE_LINKED / 100)) {
						LinkedAttribute link = new LinkedAttribute();

						link.setAttribute1(TotalAttributes.get(k));
						link.setValue1((int) (link.getAttribute1().MAX * Math.random()));
						link.setAttribute2(TotalAttributes.get((int) (TotalAttributes.size() * Math.random())));
						link.setValue2((int) (link.getAttribute2().MAX * Math.random()));
						link.setScoreModification((int) (-1 * (2 * (TotalAttributes.get(k).MAX * Math.random()
								+ TotalAttributes.get(k).MAX * Math.random()))));

						linkedAttributes.add(link);
					}
				}
				custProf.setLinkedAttributes(linkedAttributes);
			}

			CustomerProfiles.add(custProf);
		}

		// Create 2 mutants for each basic profile
		for (int i = 0; i < 4; i++) {
			CustomerProfiles.add(mutateCustomerProfile(CustomerProfiles.get(i)));
			CustomerProfiles.add(mutateCustomerProfile(CustomerProfiles.get(i)));
		}

		// Creating 4 isolated profiles
		for (int i = 0; i < 4; i++) {
			ArrayList<Attribute> attrs = new ArrayList<>();
			for (int j = 0; j < TotalAttributes.size(); j++) {
				Attribute attr = new Attribute(TotalAttributes.get(j).getName(), TotalAttributes.get(j).getMIN(),
						TotalAttributes.get(j).getMAX());
				ArrayList<Integer> scoreValues = new ArrayList<>();
				for (int k = 0; k < attr.MAX; k++) {
					int random = (int) (attr.MAX * Math.random());
					scoreValues.add(random);
				}
				attr.setScoreValues(scoreValues);
				attrs.add(attr);
			}

			CustomerProfile custProf = new CustomerProfile(attrs);

			if (StoredData.isAttributesLinked) {
				ArrayList<LinkedAttribute> linkedAttributes = new ArrayList<>();
				for (int k = 0; k < TotalAttributes.size(); k++) {
					if (Math.random() < (PROB_ATTRIBUTE_LINKED / 100)) {
						LinkedAttribute link = new LinkedAttribute();

						link.setAttribute1(TotalAttributes.get(k));
						link.setValue1((int) (link.getAttribute1().MAX * Math.random()));
						link.setAttribute2(TotalAttributes.get((int) (TotalAttributes.size() * Math.random())));
						link.setValue2((int) (link.getAttribute2().MAX * Math.random()));
						link.setScoreModification((int) (-1 * (2 * (TotalAttributes.get(k).MAX * Math.random()
								+ TotalAttributes.get(k).MAX * Math.random()))));

						linkedAttributes.add(link);
					}
				}
				custProf.setLinkedAttributes(linkedAttributes);
			}

			CustomerProfiles.add(custProf);
		}
	}

	/** Añadir Valoraciones para cada perfil */
	private CustomerProfile mutateCustomerProfile(CustomerProfile customerProfile) {
		CustomerProfile mutant = new CustomerProfile(null);
		ArrayList<Attribute> attrs = new ArrayList<>();
		for (int i = 0; i < TotalAttributes.size(); i++) {
			Attribute attr = new Attribute(TotalAttributes.get(i).getName(), TotalAttributes.get(i).getMIN(),
					TotalAttributes.get(i).getMAX());
			ArrayList<Integer> scoreValues = new ArrayList<>();
			for (int k = 0; k < attr.MAX; k++) {
				if (Math.random() < (MUT_PROB_CUSTOMER_PROFILE / 100)) {
					int random = (int) (attr.MAX * Math.random());
					scoreValues.add(random);
				} else
					scoreValues.add(customerProfile.getScoreAttributes().get(i).getScoreValues().get(k));
			}
			attr.setScoreValues(scoreValues);
			attrs.add(attr);
		}
		mutant.setScoreAttributes(attrs);
		mutant.setLinkedAttributes(customerProfile.getLinkedAttributes());
		return mutant;
	}

	/**
	 * Dividir perfiles de clientes en subperfiles
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

			for (int j = 0; j < numOfSubProfile; j++) { // We divide into
														// sub-profiles
				SubProfile subprofile = new SubProfile();
				subprofile.setName("Subperfil " + (j + 1));// + ", Perfil " +
															// (i+1));

				HashMap<Attribute, Integer> valuesChosen = new HashMap<>();
				for (int k = 0; k < TotalAttributes.size(); k++) // Each of the
																	// sub-profiles
																	// choose a
																	// value for
																	// each of
																	// the
																	// attributes
					valuesChosen.put(TotalAttributes.get(k),
							chooseValueForAttribute(CustomerProfiles.get(i).getScoreAttributes().get(k)));

				subprofile.setValueChosen(valuesChosen);
				subProfiles.add(subprofile);
			}
			CustomerProfiles.get(i).setSubProfiles(subProfiles);
		}
	}

	/**
	 * Dado un atributo, nosotros elegimos u valor para ese atributo del
	 * superfil teniendo en cuenta los valores de la encuesta.
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

	/**
	 * Generar productores aleatorios
	 */
	private void generateProducers() {
		Producers.clear();

		Producers = new ArrayList<>();
		for (int i = 0; i < inputGUI.getnum_prod(); i++) { // Creamos
															// productores
															// random
			Producer new_producer = new Producer();
			new_producer.setName("Productor " + (i + 1));
			new_producer.setAvailableAttribute(createAvailableAttributes());
			new_producer.setProduct(createProduct(new_producer.getAvailableAttribute()));

			ArrayList<Product> products = new ArrayList<>();
			for (int k = 0; k < StoredData.number_Products; k++)
				products.add(createProduct(new_producer.getAvailableAttribute()));
			new_producer.setProducts(products);

			Producers.add(new_producer);
		}
	}

	/**
	 * Crear atributos disponibles para un productor
	 */
	private ArrayList<Attribute> createAvailableAttributes() {
		ArrayList<Attribute> availableAttributes = new ArrayList<>();
		int limit = (int) (TotalAttributes.size() * getKNOWN_ATTRIBUTES() / 100);

		/* All producers know the first ATTRIBUTES_KNOWN % of the attributes */
		for (int i = 0; i < limit; i++) {
			Attribute attr = new Attribute(TotalAttributes.get(i).getName(), TotalAttributes.get(i).getMIN(),
					TotalAttributes.get(i).getMAX());
			ArrayList<Boolean> availablevalues = new ArrayList<>();
			for (int j = 0; j < attr.getMAX(); j++) {
				availablevalues.add(true);
			}

			attr.setAvailableValues(availablevalues);
			availableAttributes.add(attr);
		}

		/*
		 * The remaining attributes are only known by SPECIAL_ATTRIBUTES %
		 * producers
		 */
		for (int k = limit; k < TotalAttributes.size(); k++) {
			Attribute attr = new Attribute(TotalAttributes.get(k).getName(), TotalAttributes.get(k).getMIN(),
					TotalAttributes.get(k).getMAX());
			ArrayList<Boolean> availableValues = new ArrayList<>();

			for (int j = 0; j < attr.getMAX(); j++) {
				double rnd = Math.random();
				double rndVal = Math.random();
				/*
				 * Furthermore, with a 50% of probabilities it can know this
				 * attribute
				 */
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
			attrValues.put(TotalAttributes.get(j), chooseAttribute(j, customNearProfs, availableAttrs)); // TotalAttributes.get(j)
																											// o
																											// availableAttrs.get(j)

		product.setAttributeValue(attrValues);
		product.setPrice((int) (Math.random() * 400) + 100);
		return product;
	}

	/**
	 * Elegir un atributo cercano dado un perfil
	 */
	private int chooseAttribute(int attrInd, ArrayList<Integer> custProfInd, ArrayList<Attribute> availableAttrs) {
		int attrVal;

		ArrayList<Integer> possibleAttr = new ArrayList<>();

		for (int i = 0; i < TotalAttributes.get(attrInd).getMAX(); i++) {
			/*
			 * We count the valoration of each selected profile for attribute
			 * attrInd value i
			 */
			int possible = 0;
			for (int j = 0; j < custProfInd.size(); j++) {
				possible += CustomerProfiles.get(custProfInd.get(j)).getScoreAttributes().get(attrInd).getScoreValues()
						.get(i);
			}
			possibleAttr.add(possible);
		}
		attrVal = getMaxAttrVal(attrInd, possibleAttr, availableAttrs);

		return attrVal;
	}

	/**
	 * Elegir atributo con la máxima puntuación de los perfiles de los clientes
	 * dados
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

	/** Generar número de integrantes de cada perfil */
	private void genCustomerProfilesNum() {
		for (int i = 0; i < inputGUI.getnum_prof(); i++) {
			int number_customers = (int) (Math.floor(inputGUI.getnum() + 1) * Math.random());
			CustomerProfiles.get(i).setNumberCustomers(number_customers);
		}
	}

	/** MÉTODOS GETTERS Y SETTERS */

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

/*	public boolean isAttributesLinked() {
		return isAttributesLinked;
	}

	public void setAttributesLinked(boolean isAttributesLinked) {
		InputRandom.isAttributesLinked = isAttributesLinked;
	}

	public int getNumber_Products() {
		return number_Products;
	}

	public void setNumber_Products(int number) {
		number_Products = number;
	}
*/
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
}
