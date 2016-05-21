package GUI;

import java.awt.*; 
import java.awt.event.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.swing.*;

import general.Attribute;
import genetic.GeneticAlgorithm;
import genetic.GeneticAlgorithmVariant;
import input.InputGUI;
import input.InputRandom;
import input.InputWeka;
import minimax.Minimax;
import output.ShowResults;
import pso.PSOAlgorithm;

/**
 * Genetic Algorithm GUI
 * @author Stephania
 */
public class GUI extends JFrame implements ActionListener{
	//Declaramos los componentes que usamos:
	private static final long serialVersionUID = 1L;
    private static final int NUMERO_COLUMNAS = 35;
    private static final int NUMERO_FILAS = 25;
    private int numProd = 1;
	JFrame window;
	JTextArea jtA1, jtA2, jtA3,jtA4,jt,jtA6,jtA7,jtA8;
	JLabel label, label2, label3, label4, label5, label6, label7,label8,
	       label9, label10,label11,label12, label13,label14,label15,label16,
	       label17,label18, labelTxt, numClusters, archivoCSV;
	JButton button1, button2, button3, button4, button5, 
	        button6, button7, button8, button9, button10, 
	        button11, button_prod, button_prof, button_txt,
	        borrar, attr, prod, prof, subprof, button_txt_min, 
	        buttonTxt, buttonCSV,buttonPSO,button_txt_pso, buttonSA ,button_txt_SA;
	JScrollPane sp1,sp2,sp3;
	JTabbedPane pestaña;
	JPanel tab1, tab2, tab3, tab4, tab7, tab8, panel, panel2, panelLabel,tab5,panelGeneral,tab6;
	JTextField textEjecucion,posAttr1, posAttr2, posAttr3,text3, text4,
	           text5,text6,text7,text8,text9,text10,text13, text14, text15,text16, text17,text18,
	           text19,text20,text21, nombre_txt, nombre_txt_min, textTxt,textProducto,textP, 
	           textClusters, textCSV;
	GeneticAlgorithm ga;
	GeneticAlgorithmVariant gaVar;
	PSOAlgorithm pso;
	Minimax minimax;
	InputGUI inputGUI = new InputGUI();
	InputRandom in = new InputRandom();
	InputWeka inW = new InputWeka();
	ShowResults show = new ShowResults();
	
	int maxValoraciones = 0;
	int valoracionActual = 0;
	
	boolean showGenetic = false, showInputGen = false, showGeneticVar = false, showInputGenVar = false,
			showPSO = false, showInputPso = false, showMinimax = false, showInputMin = false;
	private JLabel labelAttrCon, labelAttrEsp,labelMutAttr,labelProfNear,labelNumPop,
				   labelNumGen,labelGruPer,labelCross,labelMutProb,labelDepth0,labelDepth1,
				   labelNumAttr,labelTurPrev,labelTur,pAttr, sAttr, puntNueva, a1,a2;
	private JTextField textAttrCon, textAttrEsp, textMutAttr, textProfNear, textNumPop, 
					   textNumGen,textGruPer,textCross,textMutProb,textDepth0,textDepth1,textNumAttr,
					   textTurPrev,textTur,textPAttr, textSAttr, textpuntNueva, textA1, textA2, nombre_txt_pso,
					   nombre_txt_SA;
	private JButton buttonAttrCon,buttonAttrEsp,buttonMutAttr,buttonProfNear,buttonNumPop,buttonNumGen,
					buttonGruPer,buttonCross,buttonMutProb,buttonDepth0,buttonDepth1,buttonNumAttr,
					buttonTurPrev,buttonTur, buttonP, buttonPunt;
	

	//En el constructor solo llamamos un m�todo:
	public GUI() throws Exception{
		iniciarGUI();
	}
	
	/**
	  * M�todo que se encarga de llamar a todos los m�todos
	  * encargados de crear la GUI y mostrarla.
	 * @throws Exception 
	  */
	private void iniciarGUI() throws Exception {
		instanciarGUI();
	}

	/**
	  * Se encarga de instanciar cada componente que va en 
	  * nuestra GUI.
	 * @throws Exception 
	  */
	private void instanciarGUI() throws Exception {
		ga = new GeneticAlgorithm();
		gaVar = new GeneticAlgorithmVariant();
		minimax = new Minimax();
		pso = new PSOAlgorithm();
		
		options();
		
			//FRAME
			window = new JFrame();
		
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
			setTitle("Marketing Computacional");
		    setResizable(false);
		
			//JTABBEPANE
			pestaña = new JTabbedPane();
			
			//JTAB 1
			tab1 = new JPanel();
			tab1.setBorder(BorderFactory.createTitledBorder("Algoritmo Genético"));
			JPanel genetic = new JPanel();
			genetic.setLayout(new GridLayout(3,1));
			button1 = new JButton("Start Genetic Algorithm");
			button1.addActionListener(this);
			button_txt = new JButton("Start Input Genetic(.txt o .xml):");
			nombre_txt = new JTextField(5);
			button_txt.addActionListener(this);
			jtA1 = new JTextArea(NUMERO_FILAS, NUMERO_COLUMNAS);
			jtA1.setBorder(BorderFactory.createLineBorder(Color.black));
			jtA1.setEditable(false);
					
			//JTAB2
			tab2 = new JPanel();
			tab2.setBorder(BorderFactory.createTitledBorder("Algoritmo Minimax"));
			JPanel panel_minimax = new JPanel();
			panel_minimax.setLayout(new GridLayout(3,1));
			button2 = new JButton("Start Minimax Algorithm");
			button2.addActionListener(this);
			button_txt_min = new JButton("Start Input Minimax(.txt o .xml):");
			nombre_txt_min = new JTextField(5);
			button_txt_min.addActionListener(this);
			jtA2 = new JTextArea(NUMERO_FILAS, NUMERO_COLUMNAS);
			jtA2.setBorder(BorderFactory.createLineBorder(Color.black));
			jtA2.setEditable(false);
			
			
			//JTAB7
			tab7 = new JPanel();
			tab7.setBorder(BorderFactory.createTitledBorder("Algoritmo PSO"));
			JPanel panel_pso = new JPanel();
			panel_pso.setLayout(new GridLayout(3,1));
			buttonPSO = new JButton("Ejecutar PSO");
			buttonPSO.addActionListener(this);
			button_txt_pso = new JButton("Ejecutar PSO(.txt o .xml):");
			nombre_txt_pso = new JTextField(5);
			button_txt_pso.addActionListener(this);
			jtA7 = new JTextArea(NUMERO_FILAS, NUMERO_COLUMNAS);
			jtA7.setBorder(BorderFactory.createLineBorder(Color.black));
			jtA7.setEditable(false);
			
			//JTAB8
			tab8 = new JPanel();
			tab8.setBorder(BorderFactory.createTitledBorder("Algoritmo SA"));
			JPanel panel_SA = new JPanel();
			panel_SA.setLayout(new GridLayout(3,1));
			buttonSA = new JButton("Ejecutar SA");
			buttonSA.addActionListener(this);
			button_txt_SA = new JButton("Ejecutar SA(.txt o .xml):");
			nombre_txt_SA = new JTextField(5);
			button_txt_SA.addActionListener(this);
			jtA8 = new JTextArea(NUMERO_FILAS, NUMERO_COLUMNAS);
			jtA8.setBorder(BorderFactory.createLineBorder(Color.black));
			jtA8.setEditable(false);
			
			//JTAB3 -  contenedor
			tab3 = new JPanel();
			tab3.setBorder(BorderFactory.createTitledBorder("Datos Estadísticos"));
			// Panel de botones de la pestaña 3
			panel = new JPanel();
			panel.setLayout(new GridLayout(4, 1));
		
			// Inicializamos el text area
			jtA3 = new JTextArea(NUMERO_FILAS, NUMERO_COLUMNAS);
			jtA3.setBorder(BorderFactory.createLineBorder(Color.black));
			jtA3.setEditable(false);
			// A�adimos el Scroll
			sp1 = new JScrollPane();
			sp1.setViewportView(jtA3);
		
			attr = new JButton("Attributes");
			prod = new JButton("Producers");
			prof = new JButton("Profiles");
			subprof = new JButton("SubProfiles");
			attr.addActionListener(this);
			prod.addActionListener(this);
			prof.addActionListener(this);
			subprof.addActionListener(this);
		
			// Añadimos opciones al panel de botones
			panel.add(attr);
			panel.add(prod);
			panel.add(prof);
			panel.add(subprof);
		
			FlowLayout tab3fl = new FlowLayout();
			tab3.setLayout(tab3fl);
		
			tab3.add(sp1);
			tab3.add(panel);
			
			//JTAB4
			tab4 = new JPanel(new GridLayout(1,1));
			panelLabel = new JPanel();
		    panelLabel.setLayout(new GridLayout(3,1));
	
		    JPanel paneltexts = new JPanel();
		    JPanel paneltextArea = new JPanel();
		    JPanel panelTxt = new JPanel(new GridLayout(1,1));
		    panelTxt.setBorder(BorderFactory.createTitledBorder("Guardar en TXT o XML"));
		    JPanel borrar_total = new JPanel();
		   // JPanel borrar_total_prof = new JPanel();
		    
		    jtA4 = new JTextArea(28, 28);
		    //jtA4.setBorder(BorderFactory.createLineBorder(Color.black));
		    jtA4.setEditable(false);
		    // A�adimos el Scroll
			sp2 = new JScrollPane();
			sp2.setViewportView(jtA4);
		
		    /*Guardar en txt*/
		    labelTxt = new JLabel("Nombre del fichero: ");
		    textTxt = new JTextField(8);
		    buttonTxt = new JButton("Guardar");
		    buttonTxt.addActionListener(this);
		    
		    /*Borrar productores y perfiles*/
		    button_prod = new JButton("Borrar Productores");
			button_prod.addActionListener(this);
			button_prof = new JButton("Borrar Perfiles");
			button_prof.addActionListener(this);
		    
			
			/*Atributo*/
			JPanel atributos = new JPanel();
			atributos.setBorder(BorderFactory.createTitledBorder("Atributos"));
			atributos.setLayout(new GridLayout(2,1));
			JPanel num_atributos = new JPanel();
			JPanel atributo = new JPanel();
			//atributos.setLayout(new GridLayout(1,1));
			label18 = new JLabel("Número de Atributos:");
			text21 = new JTextField(5);
			button11 = new JButton("Modificar Atributos");
			button11.addActionListener(this);
			label3 = new JLabel("Atributos:");
			label17 = new JLabel("Name");
			posAttr1 = new JTextField(5);
			label15 = new JLabel("MIN");
			posAttr2 = new JTextField(5);
			label16 = new JLabel("MAX");
			posAttr3 = new JTextField(5);
			button4 = new JButton("Añadir Atributo");
			borrar = new JButton("Borrar Atributos");
			button4.addActionListener(this);
			borrar.addActionListener(this);
			
			/*Productores*/
			JPanel productores = new JPanel();
			productores.setLayout(new GridLayout(6,1));
			productores.setBorder(BorderFactory.createTitledBorder("Productores"));
			JPanel numeroProductos = new JPanel();
			JPanel num_product = new JPanel(); 
			JPanel productor_producto = new JPanel();
			JPanel attr_disp = new JPanel();
			JPanel product = new JPanel(); 
			JPanel button_add = new JPanel();
			
			JLabel numProds = new JLabel("Número de productos");
			textP = new JTextField(5);
			buttonP = new JButton("Añadir productos");
			buttonP.addActionListener(this);
			label4 = new JLabel("Número de productores");
			text3 = new JTextField(5);
			button10 = new JButton("Añadir productores");
			button10.addActionListener(this);
			JLabel prod = new JLabel("Productor:");
			text20 = new JTextField(5);
			JLabel producto = new JLabel("Producto:");
			textProducto = new JTextField(5);
			JLabel disp = new JLabel("Atributos disponibles:");
			label5 = new JLabel("Name");
			text10 = new JTextField(5); 
		    JLabel valor = new JLabel("Valor atributo del producto:");
		    text16 = new JTextField(5);
			button5 = new JButton("Añadir atributo disponible");
			button5.addActionListener(this);
			button9 = new JButton("Añadir Productor");
			button9.addActionListener(this); 
			
					
			/*Perfiles*/
			JPanel perfiles = new JPanel();
			perfiles.setBorder(BorderFactory.createTitledBorder("Perfiles"));
			perfiles.setLayout(new GridLayout(3,1));
			JPanel attr_pef = new JPanel();
			JPanel num_pef = new JPanel();
			JPanel attr_linkados = new JPanel();
			label9 = new JLabel("Perfil:");
			text17 = new JTextField(5);
			label6 = new JLabel("Atributo:");
			label12 = new JLabel("Name");
			text4 = new JTextField(5);
			label7 = new JLabel("Puntuación:");
			text7 = new JTextField(5);
			button8 = new JButton("Añadir Valoracion");
			button8.addActionListener(this);
			button6 = new JButton("Añadir Perfil");
			button6.addActionListener(this);
			label8 = new JLabel("Número de perfiles de un cliente");
			text8 = new JTextField(5);
			button7 = new JButton("Añadir Perfiles");
			button7.addActionListener(this);
			
			a1 = new JLabel("Atributo 1: ");
			textA1 = new JTextField(5);
			pAttr = new JLabel("Valor Primer Atributo: ");
			textPAttr = new JTextField(5);
			a2 = new JLabel("Atributo 2: ");
			textA2 = new JTextField(5);
			sAttr = new JLabel("Valor Segundo Atributo: ");
			textSAttr = new JTextField(5);
			puntNueva = new JLabel("Puntuación nueva:");
			textpuntNueva = new JTextField(5);
			buttonPunt = new JButton("Añadir Puntuación");
			buttonPunt.addActionListener(this);
			
			//JTAB5
			tab5 = new JPanel(new GridLayout(1,1));
			panelGeneral = new JPanel();
			panelGeneral.setLayout(new GridLayout(3,1));
		
		    /*Ejecuciones*/
		    JPanel ejecuciones = new JPanel();
		    ejecuciones.setLayout(new GridLayout(5,1));
		    ejecuciones.setBorder(BorderFactory.createTitledBorder("Ejecuciones"));
		    JPanel exec = new JPanel();
		    JPanel attrKnow = new JPanel();
		    JPanel attrEsp = new JPanel();
		    JPanel mutAttr = new JPanel();
		    JPanel profNear = new JPanel();
			label2 = new JLabel("Número de ejecuciones:");
			textEjecucion = new JTextField(5);
			button3 = new JButton("Modificar Ejecuciones");
			button3.addActionListener(this);
			
			labelAttrCon = new JLabel("% Atributos Conocidos: ");
			textAttrCon = new JTextField(5);
			buttonAttrCon = new JButton("Modificar Atributos Conocidos");
			buttonAttrCon.addActionListener(this);
			
			labelAttrEsp = new JLabel("% Atributos Especiales: ");
			textAttrEsp = new JTextField(5);
			buttonAttrEsp = new JButton("Modificar Atributos Especiales");
			buttonAttrEsp.addActionListener(this);
			
			labelMutAttr = new JLabel("% Mutación Atributos: ");
			textMutAttr = new JTextField(5);
			buttonMutAttr = new JButton("Modificar Mutación Atributos");
			buttonMutAttr.addActionListener(this);
			
			labelProfNear = new JLabel("Número de perfiles cercanos: ");
			textProfNear = new JTextField(5);
			buttonProfNear = new JButton("Modificar perfiles cercanos");
			buttonProfNear.addActionListener(this);
			
			/*Panel Genetico*/
		    JPanel panelGenetico = new JPanel();
		    panelGenetico.setLayout(new GridLayout(5,1));
		    panelGenetico.setBorder(BorderFactory.createTitledBorder("Algoritmo Gen�tico"));
		    JPanel numPopu = new JPanel();
		    JPanel numGen = new JPanel();
		    JPanel grupProf = new JPanel();
		    JPanel cross = new JPanel();
		    JPanel mutprob = new JPanel();
		    
		    labelNumPop = new JLabel("Número de Población: ");
			textNumPop = new JTextField(5);
			buttonNumPop = new JButton("Modificar Número de Población");
			buttonNumPop.addActionListener(this);
		    
			labelNumGen = new JLabel("Número de Generaciones: ");
			textNumGen = new JTextField(5);
			buttonNumGen = new JButton("Modificar Número de Generaciones");
			buttonNumGen.addActionListener(this);
		    		
			labelGruPer = new JLabel("Número de Grupos del Perfil: ");
			textGruPer = new JTextField(5);
			buttonGruPer = new JButton("Modificar Grupos del Perfil");
			buttonGruPer.addActionListener(this);
			
			labelCross = new JLabel("% de Crossover: ");
			textCross = new JTextField(5);
			buttonCross = new JButton("Modificar Crossover");
			buttonCross.addActionListener(this);
			
			labelMutProb = new JLabel("% de Mutación: ");
			textMutProb = new JTextField(5);
			buttonMutProb = new JButton("Modificar Mutación");
			buttonMutProb.addActionListener(this);
		    
		    JPanel panelMinimax = new JPanel();
		    panelMinimax.setBorder(BorderFactory.createTitledBorder("Algoritmo Minimax"));
		    panelMinimax.setLayout(new GridLayout(5,1));
		    JPanel depth = new JPanel();
		    JPanel numberAttr = new JPanel();
		    JPanel numberPrev = new JPanel();
		    JPanel numberTurn = new JPanel();
		    
		    labelDepth0 = new JLabel("Depth 0: ");
			textDepth0 = new JTextField(5);
			buttonDepth0 = new JButton("Modificar Depth 0");
			buttonDepth0.addActionListener(this);
			
			labelDepth1 = new JLabel("Depth 1: ");
			textDepth1 = new JTextField(5);
			buttonDepth1 = new JButton("Modificar Depth 1");
			buttonDepth1.addActionListener(this);
			
			labelNumAttr = new JLabel("Número de atributos modificables: ");
			textNumAttr = new JTextField(5);
			buttonNumAttr = new JButton("Modificar atributos modificables");
			buttonNumAttr.addActionListener(this);
			
			labelTurPrev = new JLabel("Número de turnos previos: ");
			textTurPrev = new JTextField(5);
			buttonTurPrev = new JButton("Modificar turnos previos");
			buttonTurPrev.addActionListener(this);
			
			labelTur = new JLabel("Número de turnos en el juego: ");
			textTur = new JTextField(5);
			buttonTur = new JButton("Modificar turnos");
			buttonTur.addActionListener(this);
			
			//JTAB6 
			tab6 = new JPanel(new GridLayout(1,1)); //
			JPanel panelClusterBut = new JPanel();
			panelClusterBut.setBorder(BorderFactory.createTitledBorder("Datos de Entrada Weka"));
			JPanel panelClusterTex = new JPanel();
			panelClusterTex.setBorder(BorderFactory.createTitledBorder("Resultados"));
			//Clusters
			numClusters = new JLabel("Número de clusters: ");
			textClusters = new JTextField(5);
			archivoCSV = new JLabel("Archivo .csv");
			textCSV = new JTextField(5);
			buttonCSV = new JButton("Guardar CSV");
			buttonCSV.addActionListener(this); 
			
			jtA6 = new JTextArea(35, NUMERO_COLUMNAS);
		    jtA6.setBorder(BorderFactory.createLineBorder(Color.black));
		    jtA6.setEditable(false);
		    // Añadimos el Scroll
			sp3 = new JScrollPane();
			sp3.setViewportView(jtA6);
		
			/*Añadir*/
			add(pestaña);
			pestaña.add("Algoritmo Genético", tab1);
			pestaña.add("Algoritmo Minimax", tab2);
			pestaña.add("Algoritmo PSO", tab7);
			pestaña.add("Algoritmo SA", tab8);
			pestaña.add("Generar Listas", tab3);
			pestaña.add("Modificar Datos de Entrada", tab4);
			pestaña.add("Modificar Datos Generales", tab5);
			pestaña.add("Clusterización", tab6);			
			
			tab1.add(jtA1);
			tab1.add(genetic);
			genetic.add(button1);
			genetic.add(button_txt);
			genetic.add(nombre_txt);
			tab2.add(jtA2);
			tab2.add(panel_minimax);
			panel_minimax.add(button2);
			panel_minimax.add(button_txt_min);
			panel_minimax.add(nombre_txt_min);
		
			//tab7
			tab7.add(jtA7);
			tab7.add(panel_pso);
			panel_pso.add(buttonPSO);
			panel_pso.add(button_txt_pso);
			panel_pso.add(nombre_txt_pso);
			
			//tab7
			tab8.add(jtA8);
			tab8.add(panel_SA);
			panel_SA.add(buttonSA);
			panel_SA.add(button_txt_SA);
			panel_SA.add(nombre_txt_SA);
			
			//tab4
			tab4.add(panelLabel);
			tab4.add(paneltexts);
			
			panelLabel.add(atributos);
			panelLabel.add(productores);
			panelLabel.add(perfiles);
			
			
			paneltexts.add(paneltextArea);
			paneltexts.add(panelTxt);
			paneltextArea.add(sp2);
			panelTxt.add(labelTxt);
			panelTxt.add(textTxt);
			panelTxt.add(buttonTxt);
			paneltexts.add(borrar_total);
			borrar_total.add(borrar);
			borrar_total.add(button_prod);
			borrar_total.add(button_prof);

			atributos.add(num_atributos);
			atributos.add(atributo);
			num_atributos.add(label18);
			num_atributos.add(text21);
			num_atributos.add(button11);
			atributo.add(label3);
			atributo.add(label17);
			atributo.add(posAttr1);
			atributo.add(label15);
			atributo.add(posAttr2);
			atributo.add(label16);
			atributo.add(posAttr3);
			atributo.add(button4);
			//atributo.add(borrar);
			
			productores.add(numeroProductos);
			productores.add(num_product);
			productores.add(productor_producto);
			productores.add(attr_disp);
			productores.add(product);
			productores.add(button_add);
			
			
			numeroProductos.add(numProds);
			numeroProductos.add(textP);
			numeroProductos.add(buttonP);

			num_product.add(label4);
			num_product.add(text3);
			num_product.add(button10);

			productor_producto.add(prod);
			productor_producto.add(text20);
			productor_producto.add(producto);
			productor_producto.add(textProducto);
			
			attr_disp.add(disp);
			attr_disp.add(label5);
			attr_disp.add(text10);

			product.add(valor);
			product.add(text16);
			
			button_add.add(button5);
			button_add.add(button9);
		
	
			perfiles.add(attr_pef);
			attr_pef.add(label9);
			attr_pef.add(text17);
			attr_pef.add(label6);
			attr_pef.add(label12);
			attr_pef.add(text4);
			attr_pef.add(label7);
			attr_pef.add(text7);
			attr_pef.add(button8);
			attr_pef.add(button6);
			
			perfiles.add(num_pef);
			num_pef.add(label8);
			num_pef.add(text8);
			num_pef.add(button7);
			
			num_pef.add(a1);
			num_pef.add(textA1);
			num_pef.add(pAttr);
			num_pef.add(textPAttr);

			perfiles.add(attr_linkados);
			
			attr_linkados.add(a2);
			attr_linkados.add(textA2);
			attr_linkados.add(sAttr);
			attr_linkados.add(textSAttr);
			attr_linkados.add(puntNueva);
			attr_linkados.add(textpuntNueva);
			attr_linkados.add(buttonPunt);


			tab5.add(panelGeneral);
			panelGeneral.add(ejecuciones);
			panelGeneral.add(panelGenetico);
			panelGeneral.add(panelMinimax);
			
			ejecuciones.add(exec);
			ejecuciones.add(attrKnow);
			ejecuciones.add(attrEsp);
			ejecuciones.add(mutAttr);
			ejecuciones.add(profNear);
			
			exec.add(label2);
			exec.add(textEjecucion);
			exec.add(button3);
			
			attrKnow.add(labelAttrCon);
			attrKnow.add(textAttrCon);
			attrKnow.add(buttonAttrCon);
		
		    attrEsp.add(labelAttrEsp);
		    attrEsp.add(textAttrEsp);
		    attrEsp.add(buttonAttrEsp);
		
		    mutAttr.add(labelMutAttr);
		    mutAttr.add(textMutAttr);
		    mutAttr.add(buttonMutAttr);
			
		    profNear.add(labelProfNear);
		    profNear.add(textProfNear);
		    profNear.add(buttonProfNear);
			    
		    panelGenetico.add(numPopu);
		    panelGenetico.add(numGen);
		    panelGenetico.add(grupProf);
		    panelGenetico.add(cross);
		    panelGenetico.add(mutprob);
		    
		    numPopu.add(labelNumPop);
		    numPopu.add(textNumPop);
		    numPopu.add(buttonNumPop);
		    
		    numGen.add(labelNumGen);
		    numGen.add(textNumGen);
		    numGen.add(buttonNumGen);
		
		    grupProf.add(labelGruPer);
		    grupProf.add(textGruPer);
		    grupProf.add(buttonGruPer);
		
		    cross.add(labelCross);
		    cross.add(textCross);
		    cross.add(buttonCross);
		    
		    mutprob.add(labelMutProb);
		    mutprob.add(textMutProb);
		    mutprob.add(buttonMutProb);
		  
		    panelMinimax.add(depth);
		    panelMinimax.add(numberAttr);
		    panelMinimax.add(numberPrev);
		    panelMinimax.add(numberTurn);
		    
		    depth.add(labelDepth0);
		    depth.add(textDepth0);
		    depth.add(buttonDepth0);
		    
		    depth.add(labelDepth1);
		    depth.add(textDepth1);
		    depth.add(buttonDepth1);
			
		    numberAttr.add(labelNumAttr);
		    numberAttr.add(textNumAttr);
		    numberAttr.add(buttonNumAttr);
		    
		    numberPrev.add(labelTurPrev);
		    numberPrev.add(textTurPrev);
		    numberPrev.add(buttonTurPrev);
		    
		    numberTurn.add(labelTur);
		    numberTurn.add(textTur);
		    numberTurn.add(buttonTur);
		    
		    tab6.add(panelClusterBut);
		    tab6.add(panelClusterTex);

		    panelClusterBut.add(numClusters);
		    panelClusterBut.add(textClusters);
		    panelClusterBut.add(archivoCSV);
		    panelClusterBut.add(textCSV);
		    panelClusterBut.add(buttonCSV);

		    panelClusterTex.add(sp3);
		    
			//Se llama a pack despu�s de haber agregado componenetes a la ventana
			pack();
		//	}
	}

	/**
	  * Se encarga de Añadir los oyentes, ya sea de mouse,
	  * teclado o similares.
	 * @throws Exception 
	  */
	public void actionPerformed(ActionEvent e){
		//Buttons
		String op = e.getActionCommand();
		String min, max;
		switch(op){
			case "Start Genetic Algorithm":
			try {
				if(getNumProd() > 1){
					gaVar.start(jtA1, null, false);
					showGeneticVar = true;
				}
				else {
					ga.start(jtA1,null, false);
					showGenetic = true;
				}
			} catch (Exception e1) {
				JOptionPane.showMessageDialog(tab1, e1.getMessage());
			}
			break;
			
			case "Start Input Genetic(.txt o .xml):":
				try {
					String nombre = nombre_txt.getText();
					if(nombre.equals("")) JOptionPane.showMessageDialog(jtA1, "Error abriendo el fichero");
					else 
					{	if(getNumProd() > 1){
							gaVar.start(jtA1,nombre,true);
							showInputGenVar = true;
						}
						else{
							ga.start(jtA1,nombre,true);
							showInputGen = true;
						}
						
					}
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(tab1, e1.getMessage());
				}
				break;
			case "Start Minimax Algorithm":
			try {
				minimax.start(jtA2,null,false);
				showMinimax = false;
			} catch (Exception e1) {
				JOptionPane.showMessageDialog(tab2, e1.getMessage());
			}
			break;
			
			case "Start Input Minimax(.txt o .xml):":
				try {
					String nombre_min = nombre_txt_min.getText();
					if(nombre_min.equals("")) JOptionPane.showMessageDialog(jtA1, "Error abriendo el fichero");
					else
					{	minimax.start(jtA2,nombre_min,true);
						showInputMin = false;
					}
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(tab2, e1.getMessage());
					e1.printStackTrace();
				}
			break;
				
			case "Ejecutar PSO":
				try {
					pso.setPSO(true);
					pso.start(jtA7,null,false);
					showPSO = false;
				} catch (Exception e1) {
					try {
						pso.start(jtA7,null,false);
					} catch (Exception e2) {
						JOptionPane.showMessageDialog(tab7, e2.getMessage());
					}
				}
			break;
				
			case "Ejecutar PSO(.txt o .xml):":
				String nombre_pso = nombre_txt_pso.getText();
				try {
					if(nombre_pso.equals("")) JOptionPane.showMessageDialog(jtA7, "Error abriendo el fichero");
					else
					{	
						pso.setPSO(true);
						pso.start(jtA7,nombre_pso,true);
						showInputPso = false;
					}
				} catch (Exception e1) {
					try {
						pso.start(jtA7,nombre_pso,true);
					} catch (Exception e2) {
						JOptionPane.showMessageDialog(tab7, e2.getMessage());
					}
				}
			break;
			
			case "Ejecutar SA":
				try {
					/*pso.setPSO(true);
					pso.start(jtA8,null,false);
					showPSO = false;*/
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(tab8, e1.getMessage());
				}
			break;
				
			case "Ejecutar SA(.txt o .xml):":
				try {
					String nombre_SA = nombre_txt_SA.getText();
					if(nombre_SA.equals("")) JOptionPane.showMessageDialog(jtA8, "Error abriendo el fichero");
				/*	else
					{	
						pso.setPSO(true);
						pso.start(jtA7,nombre_SA,true);
						showInput = false;
					}*/
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(tab8, e1.getMessage());
					
				}
			break;
			case "Modificar Ejecuciones":
				String num = textEjecucion.getText();
				if(num.isEmpty()){
					JOptionPane.showMessageDialog(tab5, "Casilla Vacía");
				}
				else{
					try{
						int x1 = Integer.parseInt(num);
						ga.setNumExecutions(x1);
						minimax.setNumExecutions(x1);
					}catch(NumberFormatException nfe)
					{ JOptionPane.showMessageDialog(tab5, nfe.getMessage());}
				}
			break;
			
			case "Modificar Atributos":
				String num_attr = text21.getText();
				if(num_attr.isEmpty()){
					JOptionPane.showMessageDialog(tab4, "Casilla Vacía");
				}
				else{
					try{
						int x1 = Integer.parseInt(num_attr);
						inputGUI.setNumData(true);
						inputGUI.setnum_attr(x1);
					}catch(NumberFormatException nfe)
					{ JOptionPane.showMessageDialog(tab4, nfe.getMessage());}
				}
			break;
			
			case "Añadir Atributo":
				String name = posAttr1.getText();
				min = posAttr2.getText();
				max = posAttr3.getText();
				if(name.isEmpty() || min.isEmpty() || max.isEmpty()){JOptionPane.showMessageDialog(tab4, "Casilla Vacía");}
				else {
					try{
						int x2 = Integer.parseInt(min);
						int x3 = Integer.parseInt(max);
						
						if(x2 > x3) JOptionPane.showMessageDialog(tab4, "MIN < MAX");
						else 
						{	Attribute attr = new Attribute(name,x2,x3);
						
							if(inputGUI.isElement(inputGUI.getTotalAttributes(),attr)) { JOptionPane.showMessageDialog(tab4, "Ya existe"); }
							else { 
								if(inputGUI.getTotalAttributes().size() < inputGUI.getnum_attr()){
									inputGUI.setTotalAttributes(attr); 
									jtA4.append("Nombre: " + name + "  " + "MIN: "+ min + "  " + "MAX: " + max + "\n");
									inputGUI.setisGenerarDatosEntrada(true);
								}
								else JOptionPane.showMessageDialog(tab4, "No se puede Añadir mas atributos");
							}
						}
					}catch(NumberFormatException nfe)
					{ JOptionPane.showMessageDialog(tab4, nfe.getMessage());}
				}
			break;
			
			case "Borrar Atributos":
				jtA4.append("Borrando todos los atributos.." + "\n");
				inputGUI.getTotalAttributes().clear();
			break;
			
			case "Añadir productos":
				String numProduc = textP.getText();
				if(numProduc.isEmpty()){
					JOptionPane.showMessageDialog(tab4, "Casilla Vacía");
				}
				else{
					try{
						numProd = Integer.parseInt(numProduc);
						setNumProd(numProd);
						in.setNumber_Products(numProd);
						gaVar.setNumber_Products(numProd);
					}catch(NumberFormatException nfe)
					{ JOptionPane.showMessageDialog(tab4, nfe.getMessage());}

				}
			break;
			
			case "Añadir productores":
				String num2 = text3.getText();
				if(num2.isEmpty()){
					JOptionPane.showMessageDialog(tab4, "Casilla Vacía");
				}
				else{
					try{
						int x2 = Integer.parseInt(num2);
						inputGUI.setNumData(true);
						inputGUI.setnum_prod(x2);
					}catch(NumberFormatException nfe)
					{ JOptionPane.showMessageDialog(tab4, nfe.getMessage());}

				}
			break;
			
			case "Añadir Productor":
				String t20 = text20.getText();
				String tProd = textProducto.getText();
				String t10 = text10.getText();
				if(t20.isEmpty() || t10.isEmpty() || tProd.isEmpty()){
					JOptionPane.showMessageDialog(tab4, "Casilla Vacía");
				}
				else{
					try{
						int x20 = Integer.parseInt(t20);
						int xProd = Integer.parseInt(tProd);
						Attribute attr = inputGUI.getAttribute(inputGUI.getTotalAttributes(), t10);
						Attribute attribute = new Attribute(attr.getName(),attr.getMIN(),attr.getMAX());
						if(inputGUI.isElement(inputGUI.getTotalAttributes(),attribute)) 
						{ 
							if(inputGUI.getnum_prod() <= x20) JOptionPane.showMessageDialog(tab4, "Sobrepasa el Número de productores");
							else {
								inputGUI.inputGUIProducer(x20,xProd);
								inputGUI.products(x20,xProd);
								text20.setEnabled(false);
								textProducto.setEnabled(false);
								text10.setEnabled(false);
							}
						}
						else 
						{	JOptionPane.showMessageDialog(tab4, "Este atributo disponible no existe"); }
					}catch(NumberFormatException nfe)
					{ JOptionPane.showMessageDialog(tab4, nfe.getMessage());}
				}
			break;
			
			
			case "Añadir atributo disponible":
				t20 = text20.getText();
				tProd = textProducto.getText();
				t10 = text10.getText();
				String t16 = text16.getText();
				if(t16.isEmpty()){
					JOptionPane.showMessageDialog(tab4, "Valor Vac�o");
				}
				else{
					try{
						int x20 = Integer.parseInt(t20);
						int xProd = Integer.parseInt(tProd);
						int x16 = Integer.parseInt(t16);
						Attribute attr = inputGUI.getAttribute(inputGUI.getTotalAttributes(), t10);
						Attribute attribute = new Attribute(attr.getName(),attr.getMIN(),attr.getMAX());
						if(inputGUI.isElement(inputGUI.getTotalAttributes(),attribute)) 
						{ 
							if(attribute.getMAX() > x16){
								inputGUI.inputGUIValorAtributo(attribute,x16,x20,xProd);
								jtA4.append("Productor " + x20 + "  " + "Producto " + xProd + "  " + "Nombre: " + attribute.getName() + " " + "Valor " +  inputGUI.getIndexOf(inputGUI.getTotalAttributes(), attribute) + ": " + x16 + "\n");
								text20.setEnabled(true);
								textProducto.setEnabled(true);
								text10.setEnabled(true);
							}
							else{
								JOptionPane.showMessageDialog(tab4, "Valor de atributo no correcto");
							}
						}
						else 
						{	JOptionPane.showMessageDialog(tab4, "Este atributo disponible no existe"); }
					}catch(NumberFormatException nfe)
					{ JOptionPane.showMessageDialog(tab4, nfe.getMessage());}
				}
			break;
			
			case "Borrar Productores":
				jtA4.append("Borrando todos los productores.." + "\n");
				inputGUI.getProducers().clear();
			break;
			
			case "Añadir Perfil":
				String posCust = text17.getText();
				String nombre = text4.getText();
				try{
					if(posCust.isEmpty() || nombre.isEmpty()){
						JOptionPane.showMessageDialog(tab4, "Casilla Vacía");
					}
					else 
					{
							Attribute attr = inputGUI.getAttribute(inputGUI.getTotalAttributes(), nombre);
							Attribute attribute = new Attribute(attr.getName(),attr.getMIN(),attr.getMAX());
							maxValoraciones = attribute.getMAX();
							int x17 = Integer.parseInt(posCust); 
							if(inputGUI.isElement(inputGUI.getTotalAttributes(), attribute)){
								inputGUI.inputGUICustomer(attribute,x17);
								text17.setEnabled(false);
								text4.setEnabled(false);
							}
							else{
								JOptionPane.showMessageDialog(tab4, "Este atributo no existe");
							}
					}
				}catch(NumberFormatException nfe)
				{ JOptionPane.showMessageDialog(tab4, nfe.getMessage());}
							
			break;
			
			case "Añadir Valoracion":
				 posCust = text17.getText();
				 nombre = text4.getText();
				
				 String puntuacion = text7.getText();
				 Attribute attr = inputGUI.getAttribute(inputGUI.getTotalAttributes(), nombre);
				 Attribute attribute = new Attribute(attr.getName(),attr.getMIN(),attr.getMAX());
				if(puntuacion.isEmpty()){
					JOptionPane.showMessageDialog(tab4, "Puntuación Vacía");
				}
				else if(valoracionActual ==  maxValoraciones - 1){ //habilitar todo lo deshabilitado y poner a 0 el valoracionActual
					try{
						int x7 = Integer.parseInt(puntuacion);
						int x17 = Integer.parseInt(posCust);
						if(attribute.getMAX() < x7) JOptionPane.showMessageDialog(tab4, "Puntuacion incorrecta");
						else {
							inputGUI.inputGUIValoracion(attribute, x7, x17);
							jtA4.append("Perfil " + posCust + "  " + "Nombre: " + attribute.getName() + " " + "Valor " +  valoracionActual + ": " + x7 + "\n");
							valoracionActual = 0;
							text17.setEnabled(true);
							text4.setEnabled(true);
						}
					}catch(NumberFormatException nfe)
					{ JOptionPane.showMessageDialog(tab4, nfe.getMessage());}
					
				}
				else{
					try{
						int x7 = Integer.parseInt(puntuacion);
						int x17 = Integer.parseInt(posCust);
						if(attribute.getMAX() < x7) JOptionPane.showMessageDialog(tab4, "Puntuacion incorrecta");
						else {
							inputGUI.inputGUIValoracion(attribute, x7, x17);
							
							jtA4.append("Perfil " + posCust + "  " + "Nombre: " + attribute.getName() + " " + "Valor " + valoracionActual + ": " + x7 + "\n");
							valoracionActual++;
						}
					}catch(NumberFormatException nfe)
					{ JOptionPane.showMessageDialog(tab4, nfe.getMessage());}
					
				}
			break;
			
			case "Añadir Puntuación":
				String cust = text17.getText();
				String a1 = textA1.getText();
				String Va1 = textPAttr.getText();
				String a2 = textA2.getText();
				String Va2 = textSAttr.getText();
				String pun = textpuntNueva.getText();
				int pos = 0;
				try{
					if(a1.isEmpty() || Va1.isEmpty() || a2.isEmpty() || Va2.isEmpty() || pun.isEmpty()){
						JOptionPane.showMessageDialog(tab4, "Casilla Vacía");
					}
					else 
					{
							Attribute attr1 = inputGUI.getAttribute(inputGUI.getTotalAttributes(), a1);
							Attribute attribute1 = new Attribute(attr1.getName(),attr1.getMIN(),attr1.getMAX());
							Attribute attr2 = inputGUI.getAttribute(inputGUI.getTotalAttributes(), a2);
							Attribute attribute2 = new Attribute(attr2.getName(),attr2.getMIN(),attr2.getMAX());
							int postCust = Integer.parseInt(cust);
							int xVa1 = Integer.parseInt(Va1);
							int xVa2 = Integer.parseInt(Va2); 
							int Xpun = Integer.parseInt(pun); 
							if(inputGUI.isElement(inputGUI.getTotalAttributes(), attribute1) && inputGUI.isElement(inputGUI.getTotalAttributes(), attribute2)){
								in.setAttributesLinked(true);
								ga.setAttributesLinked(true);
								gaVar.setAttributesLinked(true);
								minimax.setAttributesLinked(true);
								if(attribute1.getMAX() < xVa1 || attribute2.getMAX() < xVa2) JOptionPane.showMessageDialog(tab4, "Valor incorrecto");
								else {
									inputGUI.AtributosLinkados(postCust, attribute1, attribute2, xVa1, xVa2, Xpun);
									jtA4.append("Perfil " + postCust + "  " + "Atributo 1: " + attribute1.getName() + " " + "Valor " + xVa1 + ": " + inputGUI.getCustomerProfiles().get(postCust).getScoreAttributes().get(inputGUI.getIndexOf(inputGUI.getTotalAttributes(), attribute1)).getScoreValues().indexOf(xVa1) +  "\n");
									jtA4.append("Perfil " + postCust + "  " + "Atributo 2: " + attribute2.getName() + " " + "Valor " + xVa2  + ": " + inputGUI.getCustomerProfiles().get(postCust).getScoreAttributes().get(inputGUI.getIndexOf(inputGUI.getTotalAttributes(), attribute2)).getScoreValues().indexOf(xVa2) + "\n");
									jtA4.append("Puntuacion nueva: " + Xpun + "\n");
								}
							}
							else{
								JOptionPane.showMessageDialog(tab4, "Este atributo no existe");
							}
					}
				}catch(NumberFormatException nfe)
				{ JOptionPane.showMessageDialog(tab4, nfe.getMessage());}
							
			break;
				
			case "Añadir Perfiles":
				String t8 = text8.getText();
				if(t8.isEmpty()){
					JOptionPane.showMessageDialog(tab4, "Casilla Vacía");
				}
				else{
					try{
						int x8 = Integer.parseInt(t8);
						inputGUI.setNumData(true);
						inputGUI.setnum(x8);
					}catch(NumberFormatException nfe)
					{ JOptionPane.showMessageDialog(tab4, nfe.getMessage());}
				}
			break;
			
			case "Borrar Perfiles":
				jtA4.append("Borrando todos los perfiles.." + "\n");
				inputGUI.getCustomerProfiles().clear();
			break;
			
			case "Attributes":
					if(showGenetic || showInputGen)
						show.showAttributes(jtA3, ga.getTotalAttributes());
					else if(showGeneticVar || showInputGenVar)
						show.showAttributes(jtA3, gaVar.getTotalAttributes());
					else if(showPSO || showInputPso)
						show.showAttributes(jtA7, pso.getTotalAttributes());
					else show.showAttributes(jtA3, minimax.getTotalAttributes());
			break;
			case "Producers":
					if(showGenetic || showInputGen)
						show.showProducers(jtA3, ga.getTotalAttributes(), ga.getProducers());
					else if(showGeneticVar || showInputGenVar)
						show.showProducers(jtA3, gaVar.getTotalAttributes(), gaVar.getProducers());
					else if(showPSO || showInputPso)
						show.showProducers(jtA7, pso.getTotalAttributes(), pso.getProducers());
					else show.showProducers(jtA3, minimax.getTotalAttributes(), minimax.getProducers());
			break;
			case "Profiles":
					if(showGenetic || showInputGen)
						show.showCustomerProfile(jtA3, ga.getCustomerProfiles());
					else if(showGeneticVar || showInputGenVar)
						show.showCustomerProfile(jtA3, gaVar.getCustomerProfiles());
					else if(showPSO || showInputPso)
						show.showCustomerProfile(jtA7, pso.getCustomerProfiles());
					else
						show.showCustomerProfile(jtA3, minimax.getCustomerProfiles());
			break;
			case "SubProfiles":
					if(showGenetic || showInputGen)
						show.showSubProfile(jtA3, ga.getTotalAttributes(), ga.getCustomerProfiles());
					else if(showGeneticVar || showInputGenVar)
						show.showSubProfile(jtA3, gaVar.getTotalAttributes(), gaVar.getCustomerProfiles());
					else if(showPSO || showInputPso)
						show.showSubProfile(jtA7, pso.getTotalAttributes(), pso.getCustomerProfiles());
					else jtA3.setText("Subprofiles not available");			
			break;
			
			case "Guardar":
				String archivo = textTxt.getText();
				try {
					int cadena = archivo.indexOf(".xml");
					if(cadena != -1) inputGUI.writeXML(archivo);
					else inputGUI.writeTxt(archivo);
					
				} catch (FileNotFoundException e1) {
					JOptionPane.showMessageDialog(tab4, e1.getMessage());
				} catch (IOException e1) {
					
					JOptionPane.showMessageDialog(tab4, e1.getMessage());
				}	
			break;
			
			case "Modificar Atributos Conocidos":
				String AttrCon = textAttrCon.getText();
				
				if(AttrCon.isEmpty()){
					JOptionPane.showMessageDialog(tab5, "Casilla Vacía");
				}
				else{
					try{
						double attrCon = Double.parseDouble(AttrCon);
						ga.setKNOWN_ATTRIBUTES(attrCon);
						gaVar.setKNOWN_ATTRIBUTES(attrCon);
						minimax.setKNOWN_ATTRIBUTES(attrCon);
						in.setKNOWN_ATTRIBUTES(attrCon);
					}catch(NumberFormatException nfe)
					{ JOptionPane.showMessageDialog(tab5, nfe.getMessage());}
				}
			break;
			
			case "Modificar Atributos Especiales":
				String AttrEs = textAttrEsp.getText();
				
				if(AttrEs.isEmpty()){
					JOptionPane.showMessageDialog(tab5, "Casilla Vacía");
				}
				else{
					try{
						double attrEs = Double.parseDouble(AttrEs);
						in.setSPECIAL_ATTRIBUTES(attrEs);
						//minimax.setSPECIAL_ATTRIBUTES(attrEs);
					}catch(NumberFormatException nfe)
					{ JOptionPane.showMessageDialog(tab5, nfe.getMessage());}
				}
			break;
			
			case "Modificar Mutación Atributos":
				String MutAt = textMutAttr.getText();
				
				if(MutAt.isEmpty()){
					JOptionPane.showMessageDialog(tab5, "Casilla Vacía");
				}
				else{
					try{
						double mutAttr = Double.parseDouble(MutAt);
						in.setMUT_PROB_CUSTOMER_PROFILE(mutAttr);
						//minimax.setMUT_PROB_CUSTOMER_PROFILE(mutAttr);
					}catch(NumberFormatException nfe)
					{ JOptionPane.showMessageDialog(tab5, nfe.getMessage());}
				}
			break;
			
			case "Modificar perfiles cercanos":
				String profNe = textProfNear.getText();
				
				if(profNe.isEmpty()){
					JOptionPane.showMessageDialog(tab5, "Casilla Vacía");
				}
				else{
					try{
						int profne = Integer.parseInt(profNe);
						ga.setNEAR_CUST_PROFS(profne);
						gaVar.setNEAR_CUST_PROFS(profne);
						in.setNEAR_CUST_PROFS(profne);
						//minimax.setNEAR_CUST_PROFS(profne);
					}catch(NumberFormatException nfe)
					{ JOptionPane.showMessageDialog(tab5, nfe.getMessage());}
				}
			break;
			
			case "Modificar Número de Población":
				String nump = textNumPop.getText();
				if(nump.isEmpty()){
					JOptionPane.showMessageDialog(tab5, "Casilla Vacía");
				}
				else{
					try{
						int numpob = Integer.parseInt(nump);
						ga.setNUM_POPULATION(numpob);
						gaVar.setNUM_POPULATION(numpob);
					}catch(NumberFormatException nfe)
					{ JOptionPane.showMessageDialog(tab5, nfe.getMessage());}
				}
			break;
			
			case "Modificar Número de Generaciones":
				String Numge = textNumGen.getText();
				if(Numge.isEmpty()){
					JOptionPane.showMessageDialog(tab5, "Casilla Vacía");
				}
				else{
					try{
						int numgen = Integer.parseInt(Numge);
						ga.setNUM_GENERATIONS(numgen);
						gaVar.setNUM_GENERATIONS(numgen);
					}catch(NumberFormatException nfe)
					{ JOptionPane.showMessageDialog(tab5, nfe.getMessage());}
				}
			break;
			
			case "Modificar Grupos del Perfil":
				String GruPe = textGruPer.getText();
				if(GruPe.isEmpty()){
					JOptionPane.showMessageDialog(tab5, "Casilla Vacía");
				}
				else{
					try{
						int gruper = Integer.parseInt(GruPe);
						ga.setRESP_PER_GROUP(gruper);
						gaVar.setRESP_PER_GROUP(gruper);
						in.setRESP_PER_GROUP(gruper);
					}catch(NumberFormatException nfe)
					{ JOptionPane.showMessageDialog(tab5, nfe.getMessage());}
				}
			break;
			
			case "Modificar Crossover":
				String Cro = textCross.getText();
				if(Cro.isEmpty()){
					JOptionPane.showMessageDialog(tab5, "Casilla Vacía");
				}
				else{
					try{
						int cross = Integer.parseInt(Cro);
						ga.setCROSSOVER_PROB(cross);
						gaVar.setCROSSOVER_PROB(cross);
					}catch(NumberFormatException nfe)
					{ JOptionPane.showMessageDialog(tab5, nfe.getMessage());}
				}
			break;
			
			case "Modificar Mutación":
				String MutPro = textMutProb.getText();
				if(MutPro.isEmpty()){
					JOptionPane.showMessageDialog(tab5, "Casilla Vacía");
				}
				else{
					try{
						int mutpro = Integer.parseInt(MutPro);
						ga.setMUTATION_PROB(mutpro);
						gaVar.setMUTATION_PROB(mutpro);
					}catch(NumberFormatException nfe)
					{ JOptionPane.showMessageDialog(tab5, nfe.getMessage());}
				}
			break;
			
			case "Modificar Depth 0":
				String depth0 = textDepth0.getText();
				if(depth0.isEmpty()){
					JOptionPane.showMessageDialog(tab5, "Casilla Vacía");
				}
				else{
					try{
						int Depth0 = Integer.parseInt(depth0);
						minimax.setMAX_DEPTH_0(Depth0);
					}catch(NumberFormatException nfe)
					{ JOptionPane.showMessageDialog(tab5, nfe.getMessage());}
				}
			break;
			
			case "Modificar Depth 1":
				String depth1 = textDepth1.getText();
				if(depth1.isEmpty()){
					JOptionPane.showMessageDialog(tab5, "Casilla Vacía");
				}
				else{
					try{
						int Depth1 = Integer.parseInt(depth1);
						minimax.setMAX_DEPTH_1(Depth1);
					}catch(NumberFormatException nfe)
					{ JOptionPane.showMessageDialog(tab5, nfe.getMessage());}
				}
			break;
			
			case "Modificar atributos modificables":
				String NumAtt = textNumAttr.getText();
				if(NumAtt.isEmpty()){
					JOptionPane.showMessageDialog(tab5, "Casilla Vacía");
				}
				else{
					try{
						int numattr = Integer.parseInt(NumAtt);
						minimax.setmNAttrMod(numattr);
					}catch(NumberFormatException nfe)
					{ JOptionPane.showMessageDialog(tab5, nfe.getMessage());}
				}
			break;
			
			case "Modificar turnos previos":
				String turPre = textTurPrev.getText();
				if(turPre.isEmpty()){
					JOptionPane.showMessageDialog(tab5, "Casilla Vacía");
				}
				else{
					try{
						int turnoPrev = Integer.parseInt(turPre);
						minimax.setmPrevTurns(turnoPrev);
					}catch(NumberFormatException nfe)
					{ JOptionPane.showMessageDialog(tab5, nfe.getMessage());}
				}
			break;
			
			case "Modificar turnos":
				String turno = textTur.getText();
				if(turno.isEmpty()){
					JOptionPane.showMessageDialog(tab5, "Casilla Vacía");
				}
				else{
					try{
						int turn = Integer.parseInt(turno);
						minimax.setmNTurns(turn);
					}catch(NumberFormatException nfe)
					{ JOptionPane.showMessageDialog(tab5, nfe.getMessage());}
				}
			break;
			
			case "Guardar CSV":
				String clusters = textClusters.getText();
				String archcsv = textCSV.getText();
				
				if(clusters.isEmpty() || archcsv.isEmpty()){
					JOptionPane.showMessageDialog(tab6, "Casilla Vacía");
				}
				else{
					try{
						int clus = Integer.parseInt(clusters);
						try {
							inW.ClusteringWeka(jtA6,clus,archcsv,"archivoAr.arff");
							inW.setClusters(true);
						} catch (FileNotFoundException e1) {
							JOptionPane.showMessageDialog(tab6, e1.getMessage());
						} catch (IOException e1) {
							JOptionPane.showMessageDialog(tab6, e1.getMessage());
						}
					}catch(NumberFormatException nfe)
					{ JOptionPane.showMessageDialog(tab6, nfe.getMessage());}
				}
			break;
			
		}
	}

	public int getNumProd() {
		return numProd;
	}

	public void setNumProd(int numProd) {
		this.numProd = numProd;
	}

	public void options(){
		Object[] opciones = { "Maximizar clientes", "Maximizar ingresos"};
		int resp = JOptionPane.showOptionDialog(null, "Seleccione opción", "Opciones", 
				JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, opciones, opciones[0]);

		if (resp == 0){ 
	    	  ga.setMaximizar(true);
	    	  gaVar.setMaximizar(true);
	    	  pso.setMaximizar(true);
	    	  minimax.setMaximizar(true);
		}
		else if (resp == 1){ 
			ga.setMaximizar(false);
			gaVar.setMaximizar(false);
			pso.setMaximizar(false);
			minimax.setMaximizar(false);
		}
		else if (resp == -1){System.exit(0);}
	}
	
}
