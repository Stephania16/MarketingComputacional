package Comunes;

import java.util.ArrayList;

public class Producer {

	private String Name;
	public ArrayList<Attribute> AvailableAttribute;
	public Product product;
	
	private ArrayList<Product> products = new ArrayList<>();

	/****** FOR MINIMAX *******/
	private ArrayList<Integer> CustomersGathered = new ArrayList<>();

	public Producer() {
		
	}

	public Producer(ArrayList<Attribute> availableAttribute, Product product) {
		super();
		AvailableAttribute = availableAttribute;
		this.product = product;
	}

	public ArrayList<Attribute> getAvailableAttribute() {
		return AvailableAttribute;
	}

	public void setAvailableAttribute(ArrayList<Attribute> availableAttribute) {
		AvailableAttribute = availableAttribute;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public ArrayList<Integer> getCustomersGathered() {
		return CustomersGathered;
	}

	public void setCustomersGathered(ArrayList<Integer> customersGathered) {
		CustomersGathered = customersGathered;
	}

	public int getNumber_CustomerGathered() {

		int Number_CG = 0;
		for (int Cust = 0; Cust < CustomersGathered.size(); Cust++)
			Number_CG += CustomersGathered.get(Cust);

		return Number_CG;
	}
	
	public ArrayList<Product> getProducts() {
		return products;
	}

	public void setProducts(ArrayList<Product> products) {
		this.products = products;
	}
}