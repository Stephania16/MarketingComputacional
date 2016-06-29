package general;

import java.util.ArrayList;

public class StoredData {
	// INPUT DATA
	public static ArrayList<Attribute> Atributos;
	public static ArrayList<CustomerProfile> Profiles;
	public static ArrayList<Producer> Producers;

	// FINAL DATA
	public static double mean = -1;
    public static String meanString = "";
    public static double initMean = -1;
    public static String initMeanString = "";
    public static double stdDev = -1;
    public static String stdDevString = "";
    public static double initStdDev = -1;
    public static String initStdDevString = "";
    public static int custMean = -1;
    public static double percCust = -1;
    public static String percCustString = "";
    public static double initPercCust = -1;
    public static String initPercCustString = "";
    public static int Algorithm;
    public static int My_price;
    public static String My_priceString;
	
	public static boolean isAttributesLinked = false;
    public static int number_Products = 1;

    public static final double VEL_LOW = -2;
    public static final double VEL_HIGH = 2;

}
