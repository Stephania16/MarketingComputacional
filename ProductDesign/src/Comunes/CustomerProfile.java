package Comunes;

import java.util.ArrayList;

import genetic.SubProfile;

public class CustomerProfile {

	private int numberCustomers;
	private ArrayList<Attribute> scoreAttributes;
	private ArrayList<SubProfile> subProfiles;

	public CustomerProfile(ArrayList<Attribute> scoreAttributes) {
		super();
		this.scoreAttributes = scoreAttributes;
	}
	public ArrayList<Attribute> getScoreAttributes() {
		return scoreAttributes;
	}

	public void setScoreAttributes(ArrayList<Attribute> scoreAttributes) {
		this.scoreAttributes = scoreAttributes;
	}

	public int getNumberCustomers() {
		return numberCustomers;
	}

	public void setNumberCustomers(int numberCustomers) {
		this.numberCustomers = numberCustomers;
	}

	public ArrayList<SubProfile> getSubProfiles() {
		return subProfiles;
	}

	public void setSubProfiles(ArrayList<SubProfile> subProfiles) {
		this.subProfiles = subProfiles;
	}
}

