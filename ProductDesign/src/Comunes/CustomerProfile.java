package Comunes;

import java.util.ArrayList;

import genetic.SubProfile;

public class CustomerProfile {

	private int numberCustomers;
	private ArrayList<Attribute> scoreAttributes;
	private ArrayList<SubProfile> subProfiles;
	private ArrayList<LinkedAttribute> linkedAttributes;

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
	public ArrayList<LinkedAttribute> getLinkedAttributes() {
		return linkedAttributes;
	}

	public void setLinkedAttributes(ArrayList<LinkedAttribute> linkedAttributes) {
		this.linkedAttributes = linkedAttributes;
	}
	
	public boolean equals(CustomerProfile cust)
	{
		for(int i = 0; i < cust.getScoreAttributes().size(); i++)
		{
			if((cust.getScoreAttributes().get(i).getName().equals(scoreAttributes.get(i).getName())) &&
			   (cust.getScoreAttributes().get(i).getMAX() == (scoreAttributes.get(i).getMAX()))	&&
			   (cust.getScoreAttributes().get(i).getMIN() == (scoreAttributes.get(i).getMIN()))) return true;
			
		}
		return false;
	}
}

