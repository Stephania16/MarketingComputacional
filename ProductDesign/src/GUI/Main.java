package GUI;

import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

public class Main {

	public static void main(String[] args) throws Exception {
		
	    SwingUtilities.invokeLater(new Runnable() {
	     
        public void run() {
        	try {
				GUI gui = new GUI();
				gui.setBounds(0,0,800,820);
				gui.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);					
				gui.setResizable(false);
				gui.setVisible(true);
			} catch (Exception e) {
				e.printStackTrace();
			}

        }
		 });

	}

}
