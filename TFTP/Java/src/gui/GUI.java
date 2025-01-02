package gui;

import javax.swing.*;

import mains.Controller;
import utils.Utility;

import java.awt.*;
import java.awt.event.*;

/**
 * GUI for the program.
 * Carried over from previous project for CSARCH2
 *
 */
public class GUI extends JFrame{
	private final String[] blockSizeValues = {"Default","128","512","1024","1428","2048","4096","8192","16384","32768","65536"};
	private boolean debug = false;
	private final int WIDTH = 1024, HEIGHT = 620;
	private final int BTNWIDTH = 256, BTNHEIGHT = 50;
	private final String WindowTitle = "NSCOM01 - TFTP";
	private final String typeFace = "Arial", consoleFace = "Consolas";
	private ActionListener listener;
	private JLabel titleLabel, serverIPLabel, serverPortLabel, consoleLabel, localSelectedFileLabel, remoteSelectedFileLabel, blockSizeLabel;
	private JTextField serverIPField, serverPortField, localSelectedFileField, remoteSelectedFileField ;
	private JTextArea outputArea;
	private JScrollPane outputScroll;
	private JButton pingBtn, openFileBtn, sendFileBtn, recvFileBtn, resetBtn, aboutBtn, exitBtn, dataPortBtn;
	private JComboBox blockSizes;
	
	public GUI(boolean setVisible) {
		setSize(WIDTH, HEIGHT);
		setResizable(false);
		setTitle(WindowTitle);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(setVisible);
		testMode();
	}
	
	public void setListener(Controller c) {
		listener = c;
	}
	
	public void setDefaultDisplay() {
		buildDisplayContents();
	}
	
	public static void popDialog(Component parent, String message, String title, int messageType) {
		JOptionPane.showMessageDialog(parent, message, title, messageType);
	}
	
	public static void errorDialog(Component parent, String message) {
		popDialog(parent, message, "Error Occured", JOptionPane.ERROR_MESSAGE);
	}
	
	public static void warningDialog(Component parent, String message) {
		popDialog(parent, message, "Warning", JOptionPane.WARNING_MESSAGE);
	}
	
	public static String inputDialog(Component parent, String message) {
		return JOptionPane.showInputDialog(parent, message);
	}
	
	public static int confirmDialog(Component parent, String message, String title, int optionType, int messageType) {
		return JOptionPane.showConfirmDialog(parent, message, title, optionType, messageType);
	}
	
	public String getServerIPInput() {
		return getServerConfigInput()[0];
	}
	
	public String getServerPortInput() {
		return getServerConfigInput()[1];
	}
	
	public String[] getServerConfigInput() {
		String[] config = {serverIPField.getText(), serverPortField.getText()};
		return config;
	}
	
	public void setRemoteSelectedFileText(String text) {
		remoteSelectedFileField.setText(text);
	}
	
	public String getRemoteSelectedFileText() {
		return remoteSelectedFileField.getText();
	}
	
	public void setLocalSelectedFileText(String text) {
		localSelectedFileField.setText(text);
	}
	
	public String getLocalSelectedFileText() {
		return localSelectedFileField.getText();
	}
	
	public String getBlockSize() {
		String str = (String)blockSizes.getSelectedItem();
		if(str.equalsIgnoreCase("default"))
			return "512";
		return str;
	}
	
	public int getBlockSizeInt() {
		return Integer.parseInt(getBlockSize());
	}
	
	private void appendOutputText(String text) {
		outputArea.setText(getOutputText() + "\n" + text);
	}
	
	public void setOutputText(String text) {
		outputArea.setText(text);
	}
	
	public void clearIO() {
		setOutputText("");
		localSelectedFileField.setText("No File Selected");
		remoteSelectedFileField.setText("");
		serverIPField.setText("");
		serverPortField.setText("");
		blockSizes.setSelectedIndex(0);
	}
	
	public String getOutputText() {
		return outputArea.getText();
	}
	
	public boolean hideGUI() {
		setVisible(false);
		return !isVisible();
	}
	
	public boolean showGUI() {
		setVisible(true);
		return isVisible();
	}
	
	public void printConsole(String message) {
		appendOutputText(Utility.getGUIConsoleMessage(message));
	}
	
	private void buildDisplayContents(){
		JPanel panel = new JPanel();
		panel.setLayout(null);
		
		//LABELS
		titleLabel = createLabel(WindowTitle, newFont(Font.BOLD, 24), WIDTH/2-200,24,400,32, SwingConstants.CENTER, SwingConstants.TOP);
		panel.add(titleLabel);
		serverIPLabel = createLabel("Server IP:", newFont(Font.BOLD, 16),32,64*1,128,32, SwingConstants.LEFT, SwingConstants.CENTER);
		panel.add(serverIPLabel);
		serverPortLabel = createLabel("Server Port:", newFont(Font.BOLD, 16),32,64*2,128,32, SwingConstants.LEFT, SwingConstants.CENTER);
		panel.add(serverPortLabel);
		consoleLabel = createLabel("Console:", newFont(Font.BOLD, 16),256+64,64*4,128,32, SwingConstants.LEFT, SwingConstants.CENTER);
		panel.add(consoleLabel);
		localSelectedFileLabel = createLabel("Locally Selected File:", newFont(Font.BOLD, 16),256+64,64*1,256,32, SwingConstants.LEFT, SwingConstants.CENTER);
		panel.add(localSelectedFileLabel);
		remoteSelectedFileLabel = createLabel("Remote Selected File:", newFont(Font.BOLD, 16),256+64,64*2,256,32, SwingConstants.LEFT, SwingConstants.CENTER);
		panel.add(remoteSelectedFileLabel);
		blockSizeLabel = createLabel("Block Size (Bytes):", newFont(Font.BOLD, 16),32,64*3,256,32, SwingConstants.LEFT, SwingConstants.CENTER);
		panel.add(blockSizeLabel);
		
		//INPUT/OUTPUT FIELDS/AREAS
		serverIPField = createTextField(newFont(Font.PLAIN, 16),32,(64*1)+32,256,32);
		panel.add(serverIPField);
		serverPortField = createTextField(newFont(Font.PLAIN, 16),32,(64*2)+32,256,32);
		panel.add(serverPortField);
		localSelectedFileField = createTextField(newFont(Font.PLAIN, 16),256+64,(64*1)+32,656,32);
		localSelectedFileField.setEditable(false);
		localSelectedFileField.setText("No File Selected");
		panel.add(localSelectedFileField);
		remoteSelectedFileField = createTextField(newFont(Font.PLAIN, 16),256+64,(64*2)+32,656,32);
		panel.add(remoteSelectedFileField);
		outputArea = createTextArea(newFont(consoleFace,Font.PLAIN, 12),256+64,(64*4)+32,656,210,false);
		outputScroll = createScrollPane(outputArea);
		outputArea.setText("");
		panel.add(outputScroll);
		
		//DROP DOWN BOX
		blockSizes = createComboBox(blockSizeValues,newFont(Font.BOLD, 16),32,(64*3)+32,256,32,listener,"BlockSelector");
		panel.add(blockSizes);
		
		//BUTTONS
		dataPortBtn = createButton("Set Data Port", newFont(Font.BOLD,16),32,(72*4), this.BTNWIDTH, this.BTNHEIGHT, listener, "SetDataPort");
		panel.add(dataPortBtn);
		pingBtn = createButton("Ping Server", newFont(Font.BOLD,16),32,(72*5),this.BTNWIDTH,this.BTNHEIGHT,listener,"ServerConnection");
		panel.add(pingBtn);
		openFileBtn = createButton("Open File", newFont(Font.BOLD,16),32,(72*6),this.BTNWIDTH,this.BTNHEIGHT,listener,"OpenFile");
		panel.add(openFileBtn);
		sendFileBtn = createButton("Send File", newFont(Font.BOLD,16),656-this.BTNWIDTH,(64*3)+16,this.BTNWIDTH,this.BTNHEIGHT,listener,"SendFile");
		panel.add(sendFileBtn);
		recvFileBtn = createButton("Receive File", newFont(Font.BOLD,16),656+this.BTNWIDTH/2-this.BTNWIDTH/4,(64*3)+16,this.BTNWIDTH,this.BTNHEIGHT,listener,"RecvFile");
		panel.add(recvFileBtn);
		aboutBtn = createButton("About",newFont(Font.BOLD,16),32,(72*7),this.BTNWIDTH,this.BTNHEIGHT,listener,"AboutProgram");
		panel.add(aboutBtn);
		resetBtn = createButton("Reset",newFont(Font.BOLD,16),this.WIDTH-(this.BTNWIDTH*2)-(2*48),(64*8),this.BTNWIDTH,this.BTNHEIGHT,listener,"Reset");
		panel.add(resetBtn);
		exitBtn = createButton("Exit",newFont(Font.BOLD,16),this.WIDTH-this.BTNWIDTH-48,(64*8),this.BTNWIDTH,this.BTNHEIGHT,listener,"EndProgram");
		panel.add(exitBtn);
		
		add(panel);
		revalidate();
	}
	
	private Font newFont(int style, int size) {
		return new Font(this.typeFace, style, size);
	}
	
	private Font newFont(String typeFace, int style, int size) {
		return new Font(typeFace, style, size);
	}
	
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
	
	private JTextField createTextField(Font f, int x, int y, int width, int height) {
		JTextField tf = new JTextField();
		tf.setFont(f);
		tf.setBounds(x,y,width,height);	
		return tf;
	}
	
	private JButton createButton(String text, Font f, int x, int y, int width, int height, ActionListener listener, String actionCommand) {
		JButton b = new JButton(text);
		b.setFont(f);
		b.setBounds(x,y,width,height);
		b.addActionListener(listener);
		b.setActionCommand(actionCommand);
		return b;
	}
	
	private JCheckBox createCheckBox(String text, Font f, int x, int y, int width, int height, boolean selected) {
		JCheckBox cb = new JCheckBox(text,selected);
		cb.setFont(f);
		cb.setBounds(x,y,width,height);	
		return cb;
	}
	
	private JTextArea createTextArea(Font f, int x, int y, int width, int height, boolean editable) {
		JTextArea ta = new JTextArea();
		ta.setFont(f);
		ta.setBounds(x,y,width,height);
		ta.setEditable(editable);
		ta.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		ta.setLineWrap(false);
		
		if(debug) {
			ta.setText(lorem_ipsum);
		}
		
		return ta;
	}
	
	private JScrollPane createScrollPane(JTextArea ta) {
		JScrollPane scroll = new JScrollPane(ta);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scroll.setBounds(ta.getX(), ta.getY(), ta.getWidth(), ta.getHeight());
		return scroll;
	}
	
	private JComboBox createComboBox(String[] selection, Font f, int x, int y, int width, int height, ActionListener listener, String actionCommand) {
		JComboBox combo = new JComboBox(selection);
		combo.setFont(f);
		combo.setBounds(x, y, width, height);
		combo.setSelectedItem(0);
		combo.setEditable(false);
		combo.addActionListener(listener);
		combo.setActionCommand(actionCommand);
		return combo;
	}
	
	private void testMode(){
		if(debug)
			JOptionPane.showMessageDialog(null, "GUI under construction", WindowTitle, JOptionPane.INFORMATION_MESSAGE);
	}
	
	private String lorem_ipsum = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.";
}
