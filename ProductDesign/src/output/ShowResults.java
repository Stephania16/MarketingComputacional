package output;

import java.util.ArrayList;

import javax.swing.JTextArea;

import general.Attribute;
import general.CustomerProfile;
import general.Producer;

public class ShowResults {

	public ShowResults() {
	}

	/**
	 * Mostrar atributos y sus valores posibles
	 */
	public void showAttributes(JTextArea jTextArea, ArrayList<Attribute> TotalAttributes) {
		jTextArea.setText("");
		for (int k = 0; k < TotalAttributes.size(); k++) {
			jTextArea.append(TotalAttributes.get(k).getName() + "\n" + "MIN: " + TotalAttributes.get(k).getMIN() + "\n"
					+ "MAX: " + TotalAttributes.get(k).getMAX() + "\n");
		}
		jTextArea.repaint();
	}

	/**
	 * Mostrar los perfiles de clientes y sus determinados valores
	 */
	public void showCustomerProfile(JTextArea jTextArea, ArrayList<CustomerProfile> CustomerProfiles) {
		jTextArea.setText("");
		for (int i = 0; i < CustomerProfiles.size(); i++) {
			CustomerProfile cp = CustomerProfiles.get(i);
			jTextArea.append("CUSTOMER PROFILE " + (i + 1) + "\n");
			for (int j = 0; j < cp.getScoreAttributes().size(); j++) {
				jTextArea.append(cp.getScoreAttributes().get(j).getName() + "\n");
				for (int z = 0; z < cp.getScoreAttributes().get(j).getScoreValues().size(); z++) {
					jTextArea.append("Value -> " + cp.getScoreAttributes().get(j).getScoreValues().get(z) + "\n");
				}
			}
		}
		jTextArea.repaint();
	}

	/**
	 * Mostrar los subperfiles a partir de los perfiles de clientes
	 */
	public void showSubProfile(JTextArea jTextArea, ArrayList<Attribute> TotalAttributes,
			ArrayList<CustomerProfile> CustomerProfiles) {
		jTextArea.setText("");
		for (int i = 0; i < CustomerProfiles.size(); i++) {
			CustomerProfile cp = CustomerProfiles.get(i);
			jTextArea.append("CUSTOMER PROFILE " + (i + 1) + "\n");
			for (int j = 0; j < cp.getSubProfiles().size(); j++) {
				jTextArea.append(cp.getSubProfiles().get(j).getName() + "\n");
				for (int z = 0; z < cp.getSubProfiles().get(j).getValueChosen().size(); z++) {
					jTextArea.append("Value -> "
							+ cp.getSubProfiles().get(j).getValueChosen().get(TotalAttributes.get(z)) + "\n");

				}
			}
		}
		jTextArea.repaint();
	}

	/**
	 * Mostrar los productores y sus determinados valores
	 */
	public void showProducers(JTextArea jTextArea, ArrayList<Attribute> TotalAttributes,
			ArrayList<Producer> Producers) {
		jTextArea.setText("");
		for (int i = 0; i < Producers.size(); i++) {
			Producer p = Producers.get(i);
			for (int k = 0; k < p.getProducts().size(); k++) {
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

}
