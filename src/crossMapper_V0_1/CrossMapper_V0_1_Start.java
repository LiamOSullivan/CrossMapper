/**
 * 
 */
package crossMapper_V0_1;


/**
 * @author lmosulli
 *
 */
public class CrossMapper_V0_1_Start {

	/**
	 * 
	 */
	static CrossMapper_V0_1_GUI myMapperGUI = new CrossMapper_V0_1_GUI();
	static CrossMapper_V0_1 myMapperV3 = new CrossMapper_V0_1();
	//static Splash splashScreen;
	//public MapperV3Start() {
		// TODO Auto-generated constructor stub
		/**
	 * @param args
	 */
	//	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				//MapperV3GUI myMapperGUI = new MapperV3GUI();
				//SimplePApplet myPApplet = new SimplePApplet();
				//splashScreen = new Splash(null);
				myMapperV3.init();
				myMapperGUI.add(myMapperV3); //Embed the PApplet in the GUI frame
				myMapperGUI.setVisible(true);

			}
		});
	}

}
