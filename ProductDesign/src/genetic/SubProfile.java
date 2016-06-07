package genetic;

import java.util.HashMap;

import general.Attribute;

/**
 * Representa la estructura de un subperfil
 */
public class SubProfile {

	private String name;
	private HashMap<Attribute, Integer> ValueChosen;

	public SubProfile() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public HashMap<Attribute, Integer> getValueChosen() {
		return ValueChosen;
	}

	public void setValueChosen(HashMap<Attribute, Integer> valueChosen) {
		ValueChosen = valueChosen;
	}
}
