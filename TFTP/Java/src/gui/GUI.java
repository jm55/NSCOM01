package gui;

import javax.swing.*;
import javax.swing.BorderFactory;

import mains.Controller;
import utils.Monitor;

import java.awt.*;
import java.awt.event.*;

/**
 * GUI for the program.
 * 
 * Carried over from previous project for CSARCH2
 * 
 * @author Escalona, Jose Miguel
 *
 */
public class GUI extends JFrame{
	private Monitor m = new Monitor();
	private final String className = "GUI";
	
	//PRIVATE GLOBAL VALUES
	private boolean debug = false;
	private final int WIDTH = 1024, HEIGHT = 620;
	private final int BTNWIDTH = 256, BTNHEIGHT = 50;
	private String WindowTitle = "NSCOM01 - TFTP";
	private String typeFace = "Helvetica";
	private ActionListener listener;
	private JLabel titleLabel, serverIPLabel, serverPortLabel, outputLabel;
	private JTextField serverIPField, serverPortField;
	private JTextArea outputArea;
	private JScrollPane outputScroll;
	private JCheckBox csvCheckBox;
	private JButton connectBtn, openFileBtn, sendFileBtn, resetBtn, aboutBtn, exitBtn;
	
	/**
	 * Default constructor that builds the window.
	 */
	public GUI() {
		setSize(WIDTH, HEIGHT);
		setResizable(false);
		setTitle(WindowTitle);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
		
		testMode();
	}
	
	/**
	 * Sets the listener for the program
	 * @param c Controller object managing components the window.
	 */
	public void setListener(Controller c) {
		m.printMessage(this.className, "setListener()", "Setting listener...");
		listener = c;
	}
	
	/**
	 * Builds the default JPanel build of the program.
	 */
	public void setDefaultDisplay() {
		m.printMessage(this.className, "setDefaultDisplay()", "Setting DefaultDisplay...");
		JPanel panel = new JPanel();
		panel.setLayout(null);
		
		//LABELS
		m.printMessage(this.className, "setDefaultDisplay()", "Setting Labels...");
		titleLabel = createLabel(WindowTitle, newFont(Font.BOLD, 24), WIDTH/2-200,24,400,32, SwingConstants.CENTER, SwingConstants.TOP);
		panel.add(titleLabel);
		serverIPLabel = createLabel("Server IP:", newFont(Font.BOLD, 16),32,64*1,128,32, SwingConstants.LEFT, SwingConstants.CENTER);
		panel.add(serverIPLabel);
		serverPortLabel = createLabel("Server Port:", newFont(Font.BOLD, 16),32,64*2,128,32, SwingConstants.LEFT, SwingConstants.CENTER);
		panel.add(serverPortLabel);
		outputLabel = createLabel("Console:", newFont(Font.BOLD, 16),256+64,64,128,32, SwingConstants.LEFT, SwingConstants.CENTER);
		panel.add(outputLabel);
		
		//INPUT/OUTPUT FIELDS/AREAS
		m.printMessage(this.className, "setDefaultDisplay()", "Setting I/O Fields...");
		serverIPField = createTextField(newFont(Font.PLAIN, 12),32,(64*1)+32,256,32);
		panel.add(serverIPField);
		serverPortField = createTextField(newFont(Font.PLAIN, 12),32,(64*2)+32,256,32);
		panel.add(serverPortField);
		outputArea = createTextArea(newFont("Consolas",Font.PLAIN, 16),256+64,64+32,656,400,false);
		outputScroll = createScrollPane(outputArea);
		outputArea.setText("Console Log");
		panel.add(outputScroll);
		
		//CHECKBOX
		//csvCheckBox = createCheckBox("Comma Separated", newFont(Font.BOLD, 16),32,64*2,256,32,false);
		//panel.add(csvCheckBox);
		
		//BUTTONS
		m.printMessage(this.className, "setDefaultDisplay()", "Setting Buttons...");
		connectBtn = createButton("", newFont(Font.BOLD,16),32,(64*4),this.BTNWIDTH,50,listener,"ServerConnection");
		panel.add(connectBtn);
		openFileBtn = createButton("Open File", newFont(Font.BOLD,16),32,(64*5),this.BTNWIDTH,50,listener,"OpenFile");
		panel.add(openFileBtn);
		sendFileBtn = createButton("Send File", newFont(Font.BOLD,16),32,(64*6),this.BTNWIDTH,50,listener,"SendFile");
		panel.add(sendFileBtn);
		aboutBtn = createButton("About",newFont(Font.BOLD,16),32,(64*7),this.BTNWIDTH,50,listener,"AboutProgram");
		panel.add(aboutBtn);
		resetBtn = createButton("Reset",newFont(Font.BOLD,16),this.WIDTH-(this.BTNWIDTH*2)-(2*48),(64*8),this.BTNWIDTH,50,listener,"Reset");
		panel.add(resetBtn);
		exitBtn = createButton("Exit",newFont(Font.BOLD,16),this.WIDTH-this.BTNWIDTH-48,(64*8),this.BTNWIDTH,50,listener,"EndProgram");
		panel.add(exitBtn);
		
		add(panel);
		revalidate();
	}
	
	public void updateConnectBtn(boolean connected) {
		m.printMessage(this.className, "updateConnectBtn(Connected)", "Updating connect status (" + connected + ")...");
		if(connected)
			connectBtn.setText("Disconnect");
		else
			connectBtn.setText("Connect");
	}
	
	/**
	 * Shows a simple pop-up message using the given parameters.
	 * @param message Message of the pop-up
	 * @param title Title of the pop-up
	 * @param type Type of message, e.g. JOptionPane.PLAIN_MESSAGE, JOptionPane.ERROR_MESSAGE,etc.
	 */
	public void popDialog(String message, String title, int type) {
		JOptionPane.showMessageDialog(this, message, WindowTitle, type);
	}
	
	/**
	 * Shows a simple input pop-up message.
	 * @param message Message to ask the user.
	 * @return User entry to input dialog.
	 */
	public String inputDialog(String message) {
		return JOptionPane.showInputDialog(this, message);
	}
	
	/**
	 * Brings up a dialog with the options Yes, No and Cancel; with thetitle, Select an Option.
	 * @param message
	 * @return
	 */
	public int confirmDialog(String message) {
		return JOptionPane.showConfirmDialog(this, message);
	}
	
	/**
	 * Get input value for Server IP
	 * @return String value of input for Server IP
	 */
	public String getServerIPInput() {
		return getServerConfigInput()[0];
	}
	
	/**
	 * Get input value for Server port
	 * @return String value of input for Server port
	 */
	public String getServerPortInput() {
		return getServerConfigInput()[1];
	}
	
	/**
	 * Get String[] input value of Server configuration inputs
	 * @return String[] containing {Server IP, Server port}
	 */
	public String[] getServerConfigInput() {
		String[] config = {serverIPField.getText(), serverPortField.getText()};
		return config;
	}
	
	/**
	 * Returns if input is comma separated
	 * @return True if input is comma separated, false otherwise.
	 */
	public boolean isCSV() {
		return csvCheckBox.isSelected();
	}
	
	/**
	 * Sets the display text in the window.
	 * @param text Text to display.
	 */
	public void setOutputText(String text) {
		outputArea.setText(text);
	}
	
	/**
	 * Clears input and output components of the window
	 */
	public void clearIO() {
		setOutputText("");
		serverIPField.setText("");
		serverPortField.setText("");
	}
	
	/**
	 * Gets contents of outputArea.
	 * @return Contents of outputArea
	 */
	public String getOutputText() {
		return outputArea.getText();
	}
	
	//PRIVATE METHODS
	
	/**
	 * Builds a Font object with the default typeFace.
	 * @param style Style of the font (bold, italicized, normal).
	 * @param size Font size of the Font.
	 * @return Font object with the specified parameter.
	 */
	private Font newFont(int style, int size) {
		return new Font(this.typeFace, style, size);
	}
	
	/**
	 * Builds a Font object using the specified typeface, style and size.
	 * @param typeFace Fontface of the Font.
	 * @param style Style of the font (bold, italicized, normal).
	 * @param size Font size of the Font.
	 * @return Font object with the specified parameter.
	 */
	private Font newFont(String typeFace, int style, int size) {
		return new Font(typeFace, style, size);
	}
	
	/**
	 * Builds a JLabel object given the text data, font, position & size, and text alignment.
	 * @param text Initial value of the object
	 * @param f Font of object
	 * @param x X position of the JLabel (via setBounds)
	 * @param y Y position of the JLabel (via setBounds)
	 * @param width Width of the JLabel (via setBounds)
	 * @param height Height of the JLabel (via setBounds)
	 * @param hAlignment Horizontal alignment of the text in JLabel (e.g. "SwingConstants.LEFT", "SwingConstants.CENTER", "SwingConstants.RIGHT", "SwingConstants.LEADING", "SwingConstants.TRAILING")
	 * @param vAlignment Vertical alignment of the text in JLabel (e.g. "SwingConstants.TOP", "SwingConstants.CENTER", "SwingConstants.BOTTOM")
	 * @return JLabel object configured using the given basic parameters.
	 */
	private JLabel createLabel(String text, Font f, int x, int y, int width, int height, int hAlignment, int vAlignment) {
		JLabel l = new JLabel(text);
		l.setFont(f);
		l.setBounds(x,y,width,height);
		l.setHorizontalAlignment(hAlignment);
		l.setVerticalAlignment(vAlignment);
		
		if(this.debug) {
			l.setOpaque(true);
			l.setBackground(Color.CYAN);
		}
		
		return l;
	}
	
	/**
	 * Builds a JTextField object given the text data, font, and position & size.
	 * @param f Font of object
	 * @param x X position of the JLabel (via setBounds)
	 * @param y Y position of the JLabel (via setBounds)
	 * @param width Width of the JLabel (via setBounds)
	 * @param height Height of the JLabel (via setBounds)
	 * @return JTextField object configured using the given basic parameters.
	 */
	private JTextField createTextField(Font f, int x, int y, int width, int height) {
		JTextField tf = new JTextField();
		tf.setFont(f);
		tf.setBounds(x,y,width,height);	
		return tf;
	}
	
	/**
	 * Builds a JButton object given the text data, font, position & size, and listener. 
	 * @param text Display text of the button
	 * @param f Font 
	 * @param x X position of the JLabel (via setBounds)
	 * @param y Y position of the JLabel (via setBounds)
	 * @param width Width of the JLabel (via setBounds)
	 * @param height Height of the JLabel (via setBounds)
	 * @param listener Listener of the button
	 * @param actionCommand ActionCommand label of the button.
	 * @return JButon object configured using the given basic parameters.
	 */
	private JButton createButton(String text, Font f, int x, int y, int width, int height, ActionListener listener, String actionCommand) {
		JButton b = new JButton(text);
		b.setFont(f);
		b.setBounds(x,y,width,height);
		b.addActionListener(listener);
		b.setActionCommand(actionCommand);
		return b;
	}
	
	/**
	 * Builds a JCheckBox object given the text data, font, position & size, and default initial select state.
	 * @param text Display text of the button
	 * @param f Font 
	 * @param x X position of the JLabel (via setBounds)
	 * @param y Y position of the JLabel (via setBounds)
	 * @param width Width of the JLabel (via setBounds)
	 * @param height Height of the JLabel (via setBounds)
	 * @param selected Default initial select state of the checkbox
	 * @return JCheckBox object configured using the given basic parameters.
	 */
	private JCheckBox createCheckBox(String text, Font f, int x, int y, int width, int height, boolean selected) {
		JCheckBox cb = new JCheckBox(text,selected);
		cb.setFont(f);
		cb.setBounds(x,y,width,height);	
		return cb;
	}
	
	/**
	 * Builds a JTextArea object given the text data, font, position & size, and editable state.
	 * @param f Font 
	 * @param x X position of the JLabel (via setBounds)
	 * @param y Y position of the JLabel (via setBounds)
	 * @param width Width of the JLabel (via setBounds)
	 * @param height Height of the JLabel (via setBounds)
	 * @param editable Set whether editable or not
	 * @return Configured JTextArea
	 */
	private JTextArea createTextArea(Font f, int x, int y, int width, int height, boolean editable) {
		JTextArea ta = new JTextArea();
		ta.setFont(f);
		ta.setBounds(x,y,width,height);
		ta.setEditable(editable);
		ta.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		ta.setLineWrap(true);
		
		if(debug) {
			ta.setText(lorem_ipsum);
		}
		
		return ta;
	}
	
	/**
	 * Builds a JScrollPane object given a JText area object.
	 * @param ta JTextArea to add a JScrollPane for.
	 * @return JScrollPane for the given JTextArea.
	 */
	private JScrollPane createScrollPane(JTextArea ta) {
		JScrollPane scroll = new JScrollPane(ta);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scroll.setBounds(ta.getX(), ta.getY(), ta.getWidth(), ta.getHeight());
		return scroll;
	}
	
	
	/**
	 * Warns if the GUI is in test mode.
	 */
	private void testMode(){
		if(debug)
			JOptionPane.showMessageDialog(null, "GUI under construction", WindowTitle, JOptionPane.INFORMATION_MESSAGE);
	}
	
	private String lorem_ipsum = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.";
}
