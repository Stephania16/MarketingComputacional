package Comunes;

import java.util.HashMap;

public class Product implements Cloneable{
	
	private HashMap<Attribute, Integer> attributeValue;
	int price;

	public Product() {
		super();
	}

	public Product(HashMap<Attribute, Integer> product) {
		super();
		attributeValue = product;
	}

	public HashMap<Attribute, Integer> getAttributeValue() {
		return attributeValue;
	}

	public void setAttributeValue(HashMap<Attribute, Integer> product) {
		attributeValue = product;
	}

	/**Creates a deep copy of Product*/
	public Product clone(){
		Product product = new Product(this.attributeValue);
//		product.setFitness(this.Fitness);
		return product;
	}
	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}
}



