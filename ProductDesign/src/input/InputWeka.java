package input;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JTextArea;

import general.Attribute;
import general.CustomerProfile;
import output.OutputCSV;
import weka.clusterers.SimpleKMeans;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.NumericToNominal;

public class InputWeka {
	OutputCSV inCsv = new OutputCSV();
	InputRandom in = new InputRandom();
	InputGUI inputGUI = new InputGUI();
	static ArrayList<CustomerProfile> CustomerProfiles = new ArrayList<>();
	static ArrayList<Integer> indexProfiles = new ArrayList<>();
	static boolean clusters = false;
	public InputWeka(){}
	
	public void ClusteringWeka(JTextArea jtA, int numClusters, String archivoCSV, String archivoArff) throws FileNotFoundException, IOException{

		if (numClusters < 1 || archivoCSV.equals("")) {
            mensajeError(jtA);
            System.exit(0);
        }
		
		if (inputGUI.isGenerarDatosEntrada())
			inCsv.WriteCSV(inputGUI.getCustomerProfiles(), archivoCSV);
		else{
			inCsv.WriteCSV(in.getCustomerProfiles(), archivoCSV);
		}
		
        try {
        	
        	long time_start = System.currentTimeMillis();
        	
            // Crear y configurar algoritmo de clustering
            jtA.append("Crear algoritmo de clustering ..." + "\r\n");
            SimpleKMeans clusterer = new SimpleKMeans();
            clusterer.setNumClusters(numClusters);
            clusterer.setMaxIterations(100);
            
            // Cargar dataset desde CSV
            CSVLoader loader = new CSVLoader();
            loader.setSource(new File(archivoCSV)); //FileInputStream
            Instances dataset = loader.getDataSet();
            
            NumericToNominal prueba = new NumericToNominal();

            prueba.setInputFormat(dataset);
            Instances newData = Filter.useFilter(dataset, prueba); 

            // save ARFF
            ArffSaver saver = new ArffSaver();
            saver.setInstances(newData);           
            saver.setFile(new File(archivoArff));
            saver.writeBatch();

            // Entrenar algoritmo de clustering
            jtA.append("Entrenar algoritmo de clustering ..." + "\r\n");
            clusterer.buildClusterer(newData);
            jtA.append("\r\n");

            // Identificar el cluster de cada instancia
            Instance instancia;
            int cluster;
            jtA.append("Asignacion de instancias a clusters ..." + "\r\n");
            for (int i=0; i < newData.numInstances(); i++)  {
                instancia = newData.instance(i);
                cluster = clusterer.clusterInstance(instancia);
                jtA.append("[Cluster "+cluster+"] Instancia: "+instancia.toString() + "\r\n");
            }
            jtA.append("\r\n");
            
            // Imprimir centroides           
            double[] tamanoClusters = clusterer.getClusterSizes();
            Instances centroides = clusterer.getClusterCentroids();
            Instance centroide;
            jtA.append("Centroides k-means ..." + "\r\n");
            for(cluster=0; cluster < clusterer.numberOfClusters(); cluster++){
                centroide = centroides.instance(cluster);
                jtA.append("Cluster "+cluster+" ("+tamanoClusters[cluster]+" instancias): " + "\r\n");
                jtA.append("Centroide["+centroide.toString()+"]" + "\r\n");  
                ArrayList<Integer> ScoreValues = new ArrayList<>();
                
                ArrayList<Attribute> attrs = new ArrayList<>();
                Attribute attr = inputGUI.getAttribute(in.getTotalAttributes(), in.getCustomerProfiles().get((int)(centroide.value(centroide.attribute(0)))).getScoreAttributes().get((int)(centroide.value(1))).getName());
                for(int i = 2; i < centroide.numAttributes();i++)
                {
                	//if(centroide.value(i) != 0)
                		ScoreValues.add((int)(centroide.value(i)));
                }
                attr.setScoreValues(ScoreValues);
                attrs.add(attr);
                CustomerProfile custprof = new CustomerProfile(attrs);
                indexProfiles.add(Integer.parseInt(centroide.toString((centroide.attribute(0)))));
                CustomerProfiles.add(custprof);
            }
            jtA.append("\r\n");
            long time_end = System.currentTimeMillis();
            double elapsed = (time_end - time_start) / 1000.0;
            jtA.append("Time taken to build model (full training data): " + elapsed + " seconds" + "\r\n");
            
        } catch (Exception e) {
        	jtA.append("Error en clustering: Ejecutar primero un algoritmo para generar perfiles" /*e.getMessage()*/ + "\r\n");
        }
    }

    private static void mensajeError(JTextArea jtA) {
    	jtA.append("ERROR: parametros incorrectos" + "\r\n");
    	jtA.append("formato: java ClusteringSimple [num. clusters] [fichero ARFF]" + "\r\n");
    }

	public ArrayList<CustomerProfile> getCustomerProfiles() {
		return CustomerProfiles;
	}

	public void setCustomerProfiles(ArrayList<CustomerProfile> customerProfiles) {
		CustomerProfiles = customerProfiles;
	}

	public boolean isClusters() {
		return clusters;
	}

	public void setClusters(boolean clus) {
		clusters = clus;
	}

	public ArrayList<Integer> getIndexProfiles() {
		return indexProfiles;
	}

	public void setIndexProfiles(ArrayList<Integer> indexProf) {
		indexProfiles = indexProf;
	}
		
	

}
