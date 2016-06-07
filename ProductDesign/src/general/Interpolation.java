package general;

import java.util.ArrayList;

/** Clase que implementa el algoritmo de interpolación */
public class Interpolation {

	public Interpolation() {
	}

	/***************************************
	 * " MÉTODOS PARA CALCULAR EL PRECIO"
	 ***************************************/

	public int calculatePrice(Product product, ArrayList<Attribute> TotalAttributes, ArrayList<Producer> Producers) {
		int price_MyProduct = 0;

		for (int i = 1; i < Producers.size(); i++) {
			Product prod_competence = Producers.get(i).getProduct();
			double distance_product = getDistanceTo(product, prod_competence, TotalAttributes);

			if (distance_product == 0) {
				price_MyProduct = prod_competence.getPrice();
				break;
			}

			price_MyProduct += prod_competence.getPrice() / distance_product;
		}

		return price_MyProduct;

	}

	public double getDistanceTo(Product my_product, Product prod_competence, ArrayList<Attribute> TotalAttributes) {
		double distance = 0;
		for (int i = 0; i < TotalAttributes.size(); i++) {
			distance += Math.pow(my_product.getAttributeValue().get(TotalAttributes.get(i))
					- prod_competence.getAttributeValue().get(TotalAttributes.get(i)), 2);
		}
		distance = Math.sqrt(distance);
		return distance;
	}

}
