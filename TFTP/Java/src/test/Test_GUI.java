package test;

import gui.Controller;
import gui.GUI;

public class Test_GUI {
	GUI g;
	Controller c;
	public Test_GUI() {
		g = new GUI();
		c = new Controller(g);
		
		g.setDefaultDisplay();
    	g.updateConnectBtn(false);
	}
}
