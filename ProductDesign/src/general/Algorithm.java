package general;

import java.util.ArrayList;
import javax.swing.JTextArea;
import genetic.SubProfile;

public abstract class Algorithm {
	public abstract void start(JTextArea jtA, String archivo, boolean inputFile) throws Exception;
	public abstract void statisticsAlgorithm(JTextArea jtA, String archivo, boolean inputFile) throws Exception;
	public abstract Product createRndProduct(ArrayList<Attribute> availableAttribute);
	public abstract Product createNearProduct(ArrayList<Attribute> availableAttribute, int nearCustProfs);
	public abstract int getMaxAttrVal(int attrInd, ArrayList<Integer> possibleAttr, ArrayList<Attribute> availableAttr);
	public abstract Integer computeBenefits(Product product, int myProducer) throws Exception;
	public abstract int computeWSC(Product product, int prodInd) throws Exception;
	public abstract int scoreLinkedAttributes(ArrayList<LinkedAttribute> linkedAttributes, Product product);
	public abstract int scoreProduct(SubProfile subprofile, Product product) throws Exception;
	public abstract int scoreAttribute(int numOfValsOfAttr, int valOfAttrCust, int valOfAttrProd) throws Exception;
	public abstract void showWSC() throws Exception;
	public abstract double computeVariance(double mean);
	public abstract int getNumExecutions();
	public abstract void setNumExecutions(int exec);
	public abstract double getKNOWN_ATTRIBUTES();
	public abstract void setKNOWN_ATTRIBUTES(double kNOWN_ATTRIBUTES);
	public abstract int getRESP_PER_GROUP();
	public abstract void setRESP_PER_GROUP(int rESP_PER_GROUP);
	public abstract ArrayList<Attribute> getTotalAttributes();
	public abstract ArrayList<Producer> getProducers();
	public abstract ArrayList<CustomerProfile> getCustomerProfiles();
	public abstract boolean isMaximizar();
	public abstract void setMaximizar(boolean maximizar);
	public abstract boolean isAttributesLinked();
	public abstract void setAttributesLinked(boolean isAttributesLinked);
}
