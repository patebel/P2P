package vs;

import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

public class P2PSimulator extends JFrame implements ActionListener, IP2PSimulator {
	
	private P2PSimulatorView p2pSimView;

	private JTextArea logField;
	private Hashtable<String, String> phoneBook;
	
	JMenuItem loadP2PNodeItem;
	JMenuItem loadDataFileItem;
	JMenuItem closeItem;
	
	private Vector<IP2PNode> nodes;
	
	public static void main(String [] args)
	{
		P2PSimulator p2pSimulator = new P2PSimulator();
	}
	
	private P2PSimulator()
	{
		super("CIT P2P Simulator");
		
		this.nodes = new Vector<IP2PNode>();
		this.phoneBook = new Hashtable<String, String>();
		
		this.initializeInterface();
		
		Environment.registerP2PSimualtor(this);
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(800, 600);
		this.setVisible(true);
	}
	
	private void initializeInterface()
	{
		//Menu bar
		JMenuBar jmb = new JMenuBar();
		
		JMenu fileMenu = new JMenu("File");
		
		loadP2PNodeItem = new JMenuItem("Load P2P node...");
		loadP2PNodeItem.addActionListener(this);
		loadDataFileItem = new JMenuItem("Load data source...");
		loadDataFileItem.setEnabled(false);
		loadDataFileItem.addActionListener(this);
		closeItem = new JMenuItem("Close");
		closeItem.addActionListener(this);
		
		fileMenu.add(loadP2PNodeItem);
		fileMenu.add(loadDataFileItem);
		fileMenu.addSeparator();
		fileMenu.add(closeItem);
		
		
		this.setJMenuBar(jmb);
		
		jmb.add(fileMenu);
		
		
		JPanel jc = new JPanel();
		jc.setLayout(new BorderLayout());
		
			
		
				
		//Log box
		JPanel logPanel = new JPanel();
		logPanel.setLayout(new BorderLayout());
		logPanel.setBorder(BorderFactory.createTitledBorder("Log"));
		
		JLabel dummy = new JLabel();
		
		
		logField = new JTextArea();
		logField.setBackground(dummy.getBackground());
		logField.setEditable(false);
		
		JScrollPane scrollPane = new JScrollPane(logField);
		
		logPanel.add(scrollPane, BorderLayout.CENTER);
		
		jc.add(logPanel, BorderLayout.CENTER);
		
		p2pSimView = new P2PSimulatorView(nodes, this);
		
		JSplitPane jsp = new JSplitPane(JSplitPane.VERTICAL_SPLIT, p2pSimView, jc);
		jsp.setOneTouchExpandable(true);
		jsp.setDividerLocation(400);
		
		this.add(jsp);
		
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		
		if(arg0.getSource() == this.closeItem)
			System.exit(0);
		
		if(arg0.getSource() == this.loadP2PNodeItem)
			loadP2PNodeItem();
		
		if(arg0.getSource() == this.loadDataFileItem)
			loadDataFileItem();
		
	}
	
	private void loadDataFileItem()
	{
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileFilter(new FileNameExtensionFilter("TXT Files", "txt"));
		if(fileChooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION)
			return;
		
		try
		{
			BufferedReader rd = new BufferedReader(new FileReader(fileChooser.getSelectedFile()));
			String line;
		
			int count = 0;
			while ((line = rd.readLine()) != null)
			{
				line.replace("\"", "");
				String [] fields = line.split(";");
				if(fields.length != 2)
				{
					throw new IOException("data source violates expected format at line " + (count+1));
				}
				
				if(!this.phoneBook.containsKey(fields[0]))
					this.phoneBook.put(fields[0], fields[1]);
				
				count++;
			}
		}
		catch(IOException ioe)
		{
			JOptionPane.showMessageDialog(this, "An error occured while reading the data file:\r\n\r\n" + ioe.toString(), "IOException occured", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		Enumeration<String> e = phoneBook.keys();
		
		while(e.hasMoreElements())
		{
			String key = e.nextElement();
			String value = phoneBook.get(key);
			if(!this.p2pSimView.getSelectedNode().store(key, value))
			{
				JOptionPane.showMessageDialog(this, this.p2pSimView.getSelectedNode().getID() + " reported an error for storing " + key + ", " + value, "Error storing data", JOptionPane.ERROR_MESSAGE);
				return;
			}
		}
	}
	
	private void loadP2PNodeItem()
	{
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileFilter(new FileNameExtensionFilter("JAR Files", "jar"));
		if(fileChooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION)
			return;
		
		URL jarfile = null;
		try {
			jarfile = new URL("jar", "", "file:" + fileChooser.getSelectedFile().getAbsoluteFile() +"!/");
		} catch (MalformedURLException e) {
			JOptionPane.showMessageDialog(this, "An exception occured while composing URL for classloader:\r\n\r\n" + e.toString(),
					"Unexpected MalformedURLException occured", JOptionPane.ERROR_MESSAGE); 
			return;
		}
		
		URLClassLoader cl = URLClassLoader.newInstance(new URL[] { jarfile });
		
		Class loadedClass = null;
		try {
			 loadedClass = cl.loadClass("vs.P2PNode");
		} catch (ClassNotFoundException e) {
			JOptionPane.showMessageDialog(this, "The JAR file you chose does not include the class P2PNode:\r\n\r\n" + e.toString(),
					"ClassNotFoundException occured", JOptionPane.ERROR_MESSAGE);
			
			return;
		}
		
		Class[] interfaces = loadedClass.getInterfaces();
		boolean interfaceFound = false;
		
		for(int i = 0; i < interfaces.length; i++)
		{
			if(interfaces[0].getName() == "vs.IP2PNode")
			{
				interfaceFound = true;
				break;
			}
		}
		
		if(!interfaceFound)
		{
			JOptionPane.showMessageDialog(this, "The class you chose does not implement the interface IP2PNode",
					"Interface IP2PNode not implemented", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		int numberOfNodes = -1;
		
		while(numberOfNodes < 0)
		{
			int i = 0;
			
			String retVal = (String)JOptionPane.showInputDialog(this, "Please enter the number of nodes you want to participate in the P2P network.\r\nThe value must be a positive integer between 1 and 100.", "Enter number of P2P nodes", JOptionPane.QUESTION_MESSAGE);
			if(retVal == null)
				return;
			
			try {
				
				i = Integer.parseInt(retVal);
			}
			catch(NumberFormatException nfe)
			{
				continue;
			}
			
			if(i >= 1 && i <= 100)
				numberOfNodes = i;
		}
		
		//Instantiate nodes
		for(int i = 0; i < numberOfNodes; i++)
		{
			try {
				IP2PNode p = (IP2PNode) loadedClass.newInstance();
				if(Environment.getBootstrapNode() == null)
					Environment.setBootstrapNode(p);
				nodes.add(p);
			} catch (InstantiationException e) {
				JOptionPane.showMessageDialog(this, e.toString(), "InstantiationException occured", JOptionPane.ERROR_MESSAGE);
				return;
			} catch (IllegalAccessException e) {
				JOptionPane.showMessageDialog(this, e.toString(), "IllegalAccessException occured", JOptionPane.ERROR_MESSAGE);
				return;
			}
		}
		
		this.loadP2PNodeItem.setEnabled(false);
	}

	@Override
	public synchronized void log(IP2PNode caller, String message) {
		
		String m = caller.getID() + ": " + message + "\r\n";
		
		addToLog(m);
	}
	
	public void nodeSelected(IP2PNode selectedNode)
	{
		this.loadDataFileItem.setEnabled(true);
		addToLog("Simulator: Node " + selectedNode.getID() + " selected\r\n");
	}
	
	public void addToLog(String message)
	{
		String logSoFar = this.logField.getText();
		logSoFar += message;
		this.logField.setText(logSoFar);
	}
}
