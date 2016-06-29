package general;

import java.util.HashMap;

/** Clase que representa un producto */

public class Product implements Cloneable {

	private HashMap<Attribute, Integer> attributeValue;
	//private HashMap<Attribute, Double> velocity;
	private int Price;

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

	/** Crea un copia del producto */
	public Product clone() {
		Product product = new Product(this.attributeValue);
		product.setPrice(this.Price);
		//product.setVelocity(this.velocity);
		return product;
	}

	public int getPrice() {
		return Price;
	}

	public void setPrice(int price) {
		this.Price = price;
	}

	/*public HashMap<Attribute, Double> getVelocity() {
		return velocity;
	}

	public void setVelocity(HashMap<Attribute, Double> velocity) {
		this.velocity = velocity;
	}*/
}
