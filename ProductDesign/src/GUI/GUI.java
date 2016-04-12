package GUI;

import java.awt.*; 
import java.awt.event.*;
import java.util.HashMap;

import javax.swing.*;

import Comunes.Attribute;
import genetic.GeneticAlgorithm;
import minimax.Minimax;

/**
 * Genetic Algorithm GUI
 * @author Stephania
 */
public class GUI extends JFrame implements ActionListener{
	//Declaramos los componentes que usamos:
	private static final long serialVersionUID = 1L;
    private static final int NUMERO_COLUMNAS = 35;
    private static final int NUMERO_FILAS = 25;
	JFrame window;
	JTextArea jtA1, jtA2, jtA3;
	JLabel label, label2, label3, label4, label5, label6, label7,label8,
	       label9, label10,label11,label12, label13,label14,label15,label16,
	       label17,label18;
	JButton button1, button2, button3, button4, button5, 
	        button6, button7, button8, button9, button10, 
	        button11, button_prod, button_prof, button_txt,
	        borrar, attr, prod, prof, subprof, button_txt_min;
	JScrollPane sp1;
	JTabbedPane pesta�a;
	JPanel tab1, tab2, tab3, tab4, panel,panel2, panelLabel;
	JTextField textEjecucion,posAttr1, posAttr2, posAttr3,text3, text4,
	           text5,text6,text7,text8,text9,text10,text11,
	           text12,text13, text14, text15,text16, text17,text18,
	           text19,text20,text21, nombre_txt, nombre_txt_min;
	Container c1,c2;
	ScrollPane jspTabla;
	JTable jtTabla;
	HashMap<Attribute, Integer> attrValues = new HashMap<>();
	GeneticAlgorithm ga;
	Minimax minimax;
	A�adir a�adir = new A�adir();
	Input in = new Input();
	int maxValoraciones = 0;
	int valoracionActual = 0;
	
	boolean showGenetic = false;
	boolean showInput = false;
	
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
		minimax = new Minimax();
		//FRAME
		window = new JFrame();

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
		setTitle("Marketing Computacional");
        setResizable(false);

		//JTABBEPANE
		pesta�a = new JTabbedPane();
		
		//JTAB 1
		tab1 = new JPanel();
		tab1.setBorder(BorderFactory.createTitledBorder("Genetic Algorithm"));
		JPanel genetic = new JPanel();
		genetic.setLayout(new GridLayout(3,1));
		button1 = new JButton("Start Genetic Algorithm");
		button1.addActionListener(this);
		button_txt = new JButton("Start Input Genetic(.txt):");
		nombre_txt = new JTextField(5);
		button_txt.addActionListener(this);
		jtA1 = new JTextArea(NUMERO_FILAS, NUMERO_COLUMNAS);
		jtA1.setBorder(BorderFactory.createLineBorder(Color.black));
		jtA1.setEditable(false);
				
		//JTAB2
		tab2 = new JPanel();
		tab2.setBorder(BorderFactory.createTitledBorder("Minimax Algorithm"));
		JPanel panel_minimax = new JPanel();
		panel_minimax.setLayout(new GridLayout(3,1));
		button2 = new JButton("Start Minimax Algorithm");
		button2.addActionListener(this);
		button_txt_min = new JButton("Start Input Minimax(.txt):");
		nombre_txt_min = new JTextField(5);
		button_txt_min.addActionListener(this);
		jtA2 = new JTextArea(NUMERO_FILAS, NUMERO_COLUMNAS);
		jtA2.setBorder(BorderFactory.createLineBorder(Color.black));
		jtA2.setEditable(false);
		
		

		//JTAB3 -  contenedor
		tab3 = new JPanel();
		tab3.setBorder(BorderFactory.createTitledBorder("Datos Estadisticos"));
		// Panel de botones de la pesta�a 3
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

		// A�adimos opciones al panel de botones
		panel.add(attr);
		panel.add(prod);
		panel.add(prof);
		panel.add(subprof);

		FlowLayout tab3fl = new FlowLayout();
		tab3.setLayout(tab3fl);

		tab3.add(sp1);
		tab3.add(panel);
		
		//JTAB4
		tab4 = new JPanel(); //new BorderLayout()
		panelLabel = new JPanel();
	//	panelLabel.setBorder(BorderFactory.createTitledBorder("Datos de Entrada"));
	    panelLabel.setLayout(new GridLayout(4,1));
	    
	    /*Ejecuciones*/
	    JPanel ejecuciones = new JPanel();
	    //ejecuciones.setLayout(new GridLayout(5,3));
	    ejecuciones.setBorder(BorderFactory.createTitledBorder("Ejecuciones"));
		label2 = new JLabel("N�mero de ejecuciones:");
		textEjecucion = new JTextField(5);
		button3 = new JButton("Modificar Ejecuciones");
		button3.addActionListener(this);
		
		/*Atributo*/
		JPanel atributos = new JPanel();
		atributos.setBorder(BorderFactory.createTitledBorder("Atributos"));
		atributos.setLayout(new GridLayout(2,1));
		JPanel num_atributos = new JPanel();
		JPanel atributo = new JPanel();
		//atributos.setLayout(new GridLayout(1,1));
		label18 = new JLabel("N�mero de Atributos:");
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
		button4 = new JButton("A�adir Atributo");
		borrar = new JButton("Borrar Atributos");
		button4.addActionListener(this);
		borrar.addActionListener(this);
		
		/*Productores*/
		JPanel productores = new JPanel();
		productores.setBorder(BorderFactory.createTitledBorder("Productores"));
		productores.setLayout(new GridLayout(4,1));
		JPanel num_product = new JPanel();
		JPanel attr_disp = new JPanel();
		JPanel product = new JPanel();
		JPanel borrar_total_prod = new JPanel();
		label4 = new JLabel("N�mero de productores");
		text3 = new JTextField(5);
		button10 = new JButton("A�adir productores");
		button10.addActionListener(this);
		JLabel prod = new JLabel("Productor:");
		text20 = new JTextField(5);
		JLabel disp = new JLabel("Atributos disponibles:");
		label5 = new JLabel("Name");
		text10 = new JTextField(5); 
		label10 = new JLabel("MIN");
	    text11 = new JTextField(5);
	    label11 = new JLabel("MAX");
	    text12 = new JTextField(5);
	    JLabel valor = new JLabel("Valor atributo del producto:");
	    text16 = new JTextField(5);
		button5 = new JButton("A�adir atributo disponible");
		button5.addActionListener(this);
		button9 = new JButton("A�adir Productor");
		button9.addActionListener(this); 
		
		button_prod = new JButton("Borrar Productores");
		button_prod.addActionListener(this);
		
		
		/*Perfiles*/
		JPanel perfiles = new JPanel();
		perfiles.setBorder(BorderFactory.createTitledBorder("Perfiles"));
		perfiles.setLayout(new GridLayout(3,1));
		JPanel attr_pef = new JPanel();
		JPanel num_pef = new JPanel();
		JPanel borrar_total_prof = new JPanel();
		label9 = new JLabel("Perfil:");
		text17 = new JTextField(5);
		label6 = new JLabel("Atributo:");
		label12 = new JLabel("Name");
		text4 = new JTextField(5);
		label13 = new JLabel("MIN");
		text5 = new JTextField(5);
		label14 = new JLabel("MAX");
		text6 = new JTextField(5);
		label7 = new JLabel("Puntuaci�n:");
		text7 = new JTextField(5);
		button8 = new JButton("A�adir Valoracion");
		button8.addActionListener(this);
		button6 = new JButton("A�adir Customer");
		button6.addActionListener(this);
		label8 = new JLabel("N�mero de perfiles de un cliente");
		text8 = new JTextField(5);
		button7 = new JButton("A�adir Perfiles");
		button7.addActionListener(this);
		button_prof = new JButton("Borrar Perfiles");
		button_prof.addActionListener(this);
		
		/*A�ADIR*/
		add(pesta�a);
		pesta�a.add("Genetic Algorithm", tab1);
		pesta�a.add("Minimax Algorithm", tab2);
		pesta�a.add("Generar Listas", tab3);
		pesta�a.add("Modificar Datos de Entrada", tab4);
		
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
	
		tab4.add(panelLabel);
		panelLabel.add(ejecuciones);
		panelLabel.add(atributos);
		panelLabel.add(productores);
		panelLabel.add(perfiles);
		
		ejecuciones.add(label2);
		ejecuciones.add(textEjecucion);
		ejecuciones.add(button3);
		
		
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
		atributo.add(borrar);

		productores.add(num_product);
		num_product.add(label4);
		num_product.add(text3);
		num_product.add(button10);
		
		productores.add(attr_disp);
		attr_disp.add(prod);
		attr_disp.add(text20);
		attr_disp.add(disp);
		attr_disp.add(label5);
		attr_disp.add(text10);
		attr_disp.add(label10);
		attr_disp.add(text11);
		attr_disp.add(label11);
		attr_disp.add(text12);	
		
		productores.add(product);
		product.add(valor);
		product.add(text16);
		product.add(button5);
		product.add(button9);
		
		productores.add(borrar_total_prod);
		borrar_total_prod.add(button_prod);
			
		perfiles.add(attr_pef);
		attr_pef.add(label9);
		attr_pef.add(text17);
		attr_pef.add(label6);
		attr_pef.add(label12);
		attr_pef.add(text4);
		attr_pef.add(label13);
		attr_pef.add(text5);
		attr_pef.add(label14);
		attr_pef.add(text6);
		attr_pef.add(label7);
		attr_pef.add(text7);
		attr_pef.add(button8);
		attr_pef.add(button6);
		
		perfiles.add(num_pef);
		num_pef.add(label8);
		num_pef.add(text8);
		num_pef.add(button7);
		
		perfiles.add(borrar_total_prof);
		borrar_total_prof.add(button_prof);

		//Se llama a pack despu�s de haber agregado componenetes a la ventana
		pack();
	}
	/**
	  * Se encarga de a�adir los oyentes, ya sea de mouse,
	  * teclado o similares.
	 * @throws Exception 
	  */
	public void actionPerformed(ActionEvent e){
		//Buttons
		String op = e.getActionCommand();
		switch(op){
			case "Start Genetic Algorithm":
			try {
				ga.start(jtA1,null, false);
				showGenetic = true;
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			break;
			
			case "Start Input Genetic(.txt):":
				try {
					String nombre = nombre_txt.getText();
					if(nombre.equals("")) JOptionPane.showMessageDialog(jtA1, "Error abriendo el fichero");
					else 
					{	ga.start(jtA1,nombre,true);
						showInput = true;
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				break;
			case "Start Minimax Algorithm":
			try {
				minimax.start(jtA2,null,false);
				showGenetic = false;
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			break;
			
			case "Start Input Minimax(.txt):":
				try {
					String nombre_min = nombre_txt_min.getText();
					if(nombre_min.equals("")) JOptionPane.showMessageDialog(jtA1, "Error abriendo el fichero");
					else
					{	minimax.start(jtA2,nombre_min,true);
						showInput = false;
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				break;
			
			case "Modificar Ejecuciones":
				String num = textEjecucion.getText();
				if(num.isEmpty()){
					JOptionPane.showMessageDialog(tab4, "Casilla Vac�a");
				}
				else{
					int x1 = Integer.parseInt(num);
					ga.setNumExecutions(x1);
					minimax.setNumExecutions(x1);
				}
			break;
			
			case "Modificar Atributos":
				String num_attr = text21.getText();
				if(num_attr.isEmpty()){
					JOptionPane.showMessageDialog(tab4, "Casilla Vac�a");
				}
				else{
					int x1 = Integer.parseInt(num_attr);
					a�adir.setnum_attr(x1);
					//ga.setnum_attr(x1);
					//minimax.setnum_attr(x1);
				}
			break;
			
			case "A�adir Atributo":
				String name = posAttr1.getText();
				String min = posAttr2.getText();
				String max = posAttr3.getText();
				if(name.isEmpty() || min.isEmpty() || max.isEmpty()){JOptionPane.showMessageDialog(tab4, "Casilla Vac�a");}
				else {
					int x2 = Integer.parseInt(min);
					int x3 = Integer.parseInt(max);
					
					if(x2 > x3) JOptionPane.showMessageDialog(tab4, "MIN < MAX");
					else 
					{	Attribute attr = new Attribute(name,x2,x3);
					
						if(a�adir.isElement(a�adir.getTotalAttributes(),attr)) { JOptionPane.showMessageDialog(tab4, "Ya existe"); }
						else { 
							if(a�adir.getTotalAttributes().size() < a�adir.getnum_attr()){
								//ga.setTotalAttributes(attr, false); 
								//minimax.setTotalAttributes(attr, false); 
								a�adir.setTotalAttributes(attr); 
								a�adir.setisGenerarDatosEntrada(true);
							}
							else JOptionPane.showMessageDialog(tab4, "No se puede a�adir mas atributos");
						}
					}
				}
			break;
			
			case "Borrar Atributos":
				a�adir.getTotalAttributes().clear();
				//ga.getTotalAttributes().clear();
				//minimax.getTotalAttributes().clear();
			break;
			
			case "A�adir productores":
				String num2 = text3.getText();
				if(num2.isEmpty()){
					JOptionPane.showMessageDialog(tab4, "Casilla Vac�a");
				}
				else{
					int x2 = Integer.parseInt(num2);
					a�adir.setnum_prod(x2);
					//ga.setnum_prod(x2);
					//minimax.setnum_prod(x2);
				}
			break;
			
			case "A�adir Productor":
				String t20 = text20.getText();
				String t10 = text10.getText();
				String t11 = text11.getText();
				String t12 = text12.getText();
				String t16 = text16.getText();
				if(t20.isEmpty() || t10.isEmpty() || t11.isEmpty() || t12.isEmpty() || t16.isEmpty()){
					JOptionPane.showMessageDialog(tab4, "Casilla Vac�a");
				}
				else{
					int x20 = Integer.parseInt(t20);
					int x11 = Integer.parseInt(t11);
					int x12 = Integer.parseInt(t12);
					int x16 = Integer.parseInt(t16);
					if(x11 > x12) JOptionPane.showMessageDialog(tab4, "MIN < MAX");
					else 
					{
						Attribute attr = new Attribute(t10,x11,x12);
						if(a�adir.isElement(a�adir.getTotalAttributes(),attr)) 
						{ 
							if(a�adir.getnum_prod() <= x20) JOptionPane.showMessageDialog(tab4, "Sobrepasa el n�mero de productores");
							//ga.A�adirProducer(attr,x16, x20); 
							//minimax.A�adirProducer(attr,x16, x20); 
							a�adir.A�adirProducer(attr, x16, x20);
							text20.setEnabled(false);
							text10.setEnabled(false);
							text11.setEnabled(false);
							text12.setEnabled(false);
						}
						else 
						{	JOptionPane.showMessageDialog(tab4, "Este atributo disponible no existe"); }
					}
				}
			break;
			
			
			case "A�adir atributo disponible":
				t20 = text20.getText();
				t10 = text10.getText();
				t11 = text11.getText();
				t12 = text12.getText();
				t16 = text16.getText();
				if(t16.isEmpty()){
					JOptionPane.showMessageDialog(tab4, "Valor Vac�o");
				}
				else{
					int x20 = Integer.parseInt(t20);
					int x11 = Integer.parseInt(t11);
					int x12 = Integer.parseInt(t12);
					int x16 = Integer.parseInt(t16);
					if(x11 > x12) JOptionPane.showMessageDialog(tab4, "MIN < MAX");
					else 
					{
						Attribute attr = new Attribute(t10,x11,x12);
						if(a�adir.isElement(a�adir.getTotalAttributes(),attr)) 
						{ 
							//ga.A�adirValorAtributo(attr,x16,x20);
							//minimax.A�adirValorAtributo(attr,x16,x20);
							a�adir.A�adirValorAtributo(attr,x16,x20);
							text20.setEnabled(true);
							text10.setEnabled(true);
							text11.setEnabled(true);
							text12.setEnabled(true);
						}
						else 
						{	JOptionPane.showMessageDialog(tab4, "Este atributo disponible no existe"); }
					}
				}
			break;
			
			case "Borrar Productores":
				a�adir.getProducers().clear();
				//ga.getProducers().clear();
				//minimax.getProducers().clear();
			break;
			
			case "A�adir Customer":
				String posCust = text17.getText();
				String nombre = text4.getText();
				String minimo = text5.getText();
				String maximo = text6.getText();
				String puntuacion = text7.getText();
				if(posCust.isEmpty() || nombre.isEmpty() || minimo.isEmpty() || maximo.isEmpty() || puntuacion.isEmpty()){
					JOptionPane.showMessageDialog(tab4, "Casilla Vac�a");
				}
				else 
				{
					int x5 = Integer.parseInt(minimo);
					int x6 = Integer.parseInt(maximo);
					int x7 = Integer.parseInt(puntuacion);
					if(x5 > x6) JOptionPane.showMessageDialog(tab4, "MIN < MAX");
					else 
					{
						Attribute attr = new Attribute(nombre,x5,x6);
						maxValoraciones = attr.getMAX();
						int x17 = Integer.parseInt(posCust); 
						if(a�adir.isElement(a�adir.getTotalAttributes(), attr)){
							a�adir.A�adirCustomer(attr,x7,x17);
							//ga.A�adirCustomer(attr,x7,x17);
							//minimax.A�adirCustomer(attr,x7,x17);
							text17.setEnabled(false);
							text4.setEnabled(false);
							text5.setEnabled(false);
							text6.setEnabled(false);
						}
						else{
							JOptionPane.showMessageDialog(tab4, "Este atributo no existe");
						}
					}
							
				}
			break;
			
			case "A�adir Valoracion":
				 posCust = text17.getText();
				 nombre = text4.getText();
				 minimo = text5.getText();
				 maximo = text6.getText();
				 puntuacion = text7.getText();
				if(puntuacion.isEmpty()){
					JOptionPane.showMessageDialog(tab4, "Puntuaci�n Vac�a");
				}
				else if(valoracionActual ==  maxValoraciones - 1){ //habilitar todo lo deshabilitado y poner a 0 el valoracionActual
					int x5 = Integer.parseInt(minimo);
					int x6 = Integer.parseInt(maximo);
					int x7 = Integer.parseInt(puntuacion);
					int x17 = Integer.parseInt(posCust);
					if(x5 > x6) JOptionPane.showMessageDialog(tab4, "MIN < MAX");
					else 
					{
						Attribute attr = new Attribute(nombre,x5,x6);
						if(attr.getMAX() < x7) JOptionPane.showMessageDialog(tab4, "Puntuacion incorrecta");
						else {
							a�adir.A�adirValoracion(attr, x7, x17);
							//ga.A�adirValoracion(attr, x7, x17);
						//	minimax.A�adirValoracion(attr, x7, x17);
							valoracionActual = 0;
							
							text17.setEnabled(true);
							text4.setEnabled(true);
							text5.setEnabled(true);
							text6.setEnabled(true);
						}
					}
				}
				else{
					int x5 = Integer.parseInt(minimo);
					int x6 = Integer.parseInt(maximo);
					int x7 = Integer.parseInt(puntuacion);
					int x17 = Integer.parseInt(posCust);
					if(x5 > x6) JOptionPane.showMessageDialog(tab4, "MIN < MAX");
					else 
					{
						Attribute attr = new Attribute(nombre,x5,x6);
						a�adir.A�adirValoracion(attr, x7, x17);
						//ga.A�adirValoracion(attr, x7, x17);
						//minimax.A�adirValoracion(attr, x7, x17);
						valoracionActual++;
					}
				}
			break;
			
			case "A�adir Perfiles":
				String t8 = text8.getText();
				if(t8.isEmpty()){
					JOptionPane.showMessageDialog(tab4, "Casilla Vac�a");
				}
				else{
					int x8 = Integer.parseInt(t8);
					a�adir.setnum(x8);
					//ga.setnum(x8);
					//minimax.setnum(x8);
				}
			break;
			
			case "Borrar Perfiles":
				a�adir.getCustomerProfiles().clear();
				//ga.getCustomerProfiles().clear();
				//minimax.getCustomerProfiles().clear();
			break;
			
			case "Attributes":
					if(showGenetic || showInput)
						ga.showAttributes(jtA3);
					else minimax.showAttributes(jtA3);
			break;
			case "Producers":
					if(showGenetic || showInput)
						ga.showProducers(jtA3);
					else minimax.showProducers(jtA3);
			break;
			case "Profiles":
					if(showGenetic || showInput)
						ga.showCustomerProfile(jtA3);
					else
					minimax.showCustomerProfile(jtA3);
			break;
			case "SubProfiles":
					if(showGenetic || showInput)
						ga.showSubProfile(jtA3);
					else jtA3.setText("Subprofiles not available");			
			break;
		}
	}

}
