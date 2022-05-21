package test;

import gui.GUI;
import mains.Controller;

public class Test_GUI {
	GUI g;
	Controller c;
	public Test_GUI() {
		System.out.println("===Test_GUI===");
		g = new GUI();
		c = new Controller(g);
		
		g.setDefaultDisplay();
    	g.updateConnectBtn(false);
	}
}
