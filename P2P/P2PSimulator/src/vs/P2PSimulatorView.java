package vs;


import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Timer;
import java.util.Vector;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

public class P2PSimulatorView extends JPanel implements MouseListener, ActionListener {

	private static int offset = 20;
	private Vector<IP2PNode> nodes;
	private IP2PNode selectedNode;
	private P2PSimulator p2pSimulator;
	
	private JMenuItem fingerTableItem = new JMenuItem("Print finger table");
	private JMenuItem storedDataItem = new JMenuItem("Print stored data");
	
	private JPopupMenu popupMenu = new JPopupMenu();
	
	public P2PSimulatorView(Vector<IP2PNode> nodes, P2PSimulator p2pSimulator)
	{
		this.setBackground(Color.WHITE);
		this.setDoubleBuffered(true);
		this.nodes = nodes;
		this.selectedNode = null;
		this.p2pSimulator = p2pSimulator;
		this.addMouseListener(this);
		
		this.fingerTableItem = new JMenuItem("Print finger table");
		this.fingerTableItem.addActionListener(this);
		this.storedDataItem = new JMenuItem("Print stored data");
		this.storedDataItem.addActionListener(this);
		
		
		this.popupMenu = new JPopupMenu();
		this.popupMenu.add(fingerTableItem);
		this.popupMenu.add(storedDataItem);
		
		
		P2PViewUpdateTask updateTask = new P2PViewUpdateTask(this);
		Timer timer = new Timer();
		timer.schedule(updateTask, 2000, 2000);
	}
	
	public IP2PNode getSelectedNode()
	{
		return this.selectedNode;
	}
	
	public void paintComponent(Graphics g)
	{
		//Clear background
		super.paintComponent(g);
		
		//Draw the basic Chord ring
		//drawBasicRing(g);
		//drawSegments(g);
		drawNodes(g);
		
	}
	
	private void drawSegments(Graphics g)
	{
		int centerX = getSize().width/2;
		int centerY = getSize().height/2;
		int pX, pY;
		int radius;
		double angle;
		int sqrtNodes = (int) Math.ceil(Math.sqrt(this.nodes.size()));
		
		if(centerX < centerY)
			radius = centerX - offset;
		else
			radius = centerY - offset;
		
		for(int i = 0; i < sqrtNodes; i++)
		{
			angle = 2*Math.PI * ((double)i/ (double) sqrtNodes) - Math.PI/2;
			
			pX = centerX + (int) ((radius+40) * Math.cos(angle));
			pY = centerY + (int) ((radius+40) * Math.sin(angle));
			
			g.drawLine(centerX, centerY, pX, pY);
		}
	}
	
	private void drawNodes(Graphics g)
	{
		int centerX = getSize().width/2;
		int centerY = getSize().height/2;
		int circleX, circleY;
		int radius;
		double angle;
		long maxNodes = 0, minNodes = Integer.MAX_VALUE;
		
		if(centerX < centerY)
			radius = centerX - offset;
		else
			radius = centerY - offset;
	
		//Draw the entire edges
		/*g.setColor(Color.RED);
		Enumeration<AugNetNode> e = nodes.elements();
		
		while(e.hasMoreElements())
		{
			AugNetNode ann = e.nextElement();
			Vector<AugNetNode> routingTable = ann.getRoutingTable();
			
			if(!routingTable.isEmpty())
			{
				circleX = centerX + (int) (radius * Math.cos(ann.getAngle()));
				circleY = centerY + (int) (radius * Math.sin(ann.getAngle()));
				
				Enumeration<AugNetNode> e2 = routingTable.elements();
				
				while(e2.hasMoreElements())
				{
					AugNetNode ann2 = e2.nextElement();
					int nodeX = centerX + (int) (radius * Math.cos(ann2.getAngle()));
					int nodeY = centerY + (int) (radius * Math.sin(ann2.getAngle()));
					
					g.drawLine(circleX, circleY, nodeX, nodeY);
				}
			}
		}*/
		
		//Draw successor edges
		g.setColor(Color.DARK_GRAY);
		Enumeration<IP2PNode> e = nodes.elements();
			
		while(e.hasMoreElements())
		{
			IP2PNode n = e.nextElement();
			IP2PNode successor = n.getSuccessor();
			
			if(successor != null)
			{
				circleX = centerX + (int) (radius * Math.cos(getAngle(n.getID())));
				circleY = centerY + (int) (radius * Math.sin(getAngle(n.getID())));
				
				int successorX = centerX + (int) (radius * Math.cos(getAngle(successor.getID())));
				int successorY = centerY + (int) (radius * Math.sin(getAngle(successor.getID())));
				
				g.drawLine(circleX, circleY, successorX, successorY);
			}
		}
		
		//Draw predecessor edges
		g.setColor(Color.LIGHT_GRAY);
		e = nodes.elements();
		
		while(e.hasMoreElements())
		{
			IP2PNode n = e.nextElement();
			IP2PNode predecessor = n.getPredecessor();
			
			circleX = centerX + (int) (radius * Math.cos(getAngle(n.getID())));
			circleY = centerY + (int) (radius * Math.sin(getAngle(n.getID())));
			
			if(predecessor != null)
			{
				int predecessorX = centerX + (int) (radius * Math.cos(getAngle(predecessor.getID())));
				int predecessorY = centerY + (int) (radius * Math.sin(getAngle(predecessor.getID())));
				
				g.drawLine(circleX, circleY, predecessorX, predecessorY);
			}
		}
		
		//Draw fingers for selected node
		if(selectedNode != null)
		{
			g.setColor(Color.CYAN);
			Iterator<IP2PNode> i = selectedNode.getFingers();
			
			circleX = centerX + (int) (radius * Math.cos(getAngle(selectedNode.getID())));
			circleY = centerY + (int) (radius * Math.sin(getAngle(selectedNode.getID())));
			
			while(i.hasNext())
			{
				IP2PNode finger = i.next();
				int fingerX = centerX + (int) (radius * Math.cos(getAngle(finger.getID())));
				int fingerY = centerY + (int) (radius * Math.sin(getAngle(finger.getID())));
				
				g.drawLine(circleX, circleY, fingerX, fingerY);
			}
		}
		
		//Draw nodes
		g.setColor(Color.GRAY);
		e = nodes.elements();
		
		while(e.hasMoreElements())
		{
			IP2PNode n = e.nextElement();
			angle = getAngle(n.getID());
			
			circleX = centerX + (int) (radius * Math.cos(angle));
			circleY = centerY + (int) (radius * Math.sin(angle));
			
			if(n == this.selectedNode)
				g.setColor(Color.BLUE);
			else
				g.setColor(Color.GREEN);
			
			g.fillOval(circleX - 10,  circleY - 10, 20, 20);
			g.setColor(Color.GRAY);
		}
		
		//Draw node IDs
		g.setColor(Color.BLACK);
		e = nodes.elements();
		while(e.hasMoreElements())
		{
			IP2PNode n = e.nextElement();
			angle = getAngle(n.getID());
			
			circleX = centerX + (int) (radius * Math.cos(angle));
			circleY = centerY + (int) (radius * Math.sin(angle));
			
			g.drawString(String.format("%d", n.getID()),circleX, circleY);
			/*if(ann.getPredecessor()!= null)
				g.drawString(String.format("P: %x", ann.getPredecessor().getNodeID()),circleX, circleY + 10);
			if(ann.getSuccessor() != null)
				g.drawString(String.format("S: %x", ann.getSuccessor().getNodeID()),circleX, circleY + 20);*/
		}
		
		//Draw statistics
		//g.drawString(String.format("Min: %d", minNodes), 2, 10);
		//g.drawString(String.format("Max: %d", maxNodes), 2, 20);
	}
	
	private double getAngle(long id)
	{
		return 2*Math.PI * ((double)id/ (double) (Environment.ID_SPACE_SIZE + 1)) - Math.PI/2;
	}
	
	private void drawBasicRing(Graphics g)
	{
		int diameter = 0;
		if(getSize().width < getSize().height)
			diameter = getSize().width - 2*offset;
		else
			diameter = getSize().height - 2*offset;
		
		
		
		g.drawOval(getSize().width/2-diameter/2, getSize().height/2-diameter/2,diameter,diameter);
	}
	
	private void nodeClicked(IP2PNode node)
	{
		if(selectedNode != node)
		{
			this.selectedNode = node;
			p2pSimulator.nodeSelected(selectedNode);
			repaint();
		}
	}

	@Override
	public void mouseClicked(MouseEvent me) {
		
		if(me.getButton() != MouseEvent.BUTTON1)
			return;
		
		IP2PNode clickedNode = clickedOnNode(me.getPoint());
		if(clickedNode == null)
			return;
		
		switch(me.getClickCount())
		{
		case 1:
			nodeClicked(clickedNode);
			break;
		case 2:
			String retVal = JOptionPane.showInputDialog(this, "Lookup telephone number of", "Lookup at node " + selectedNode.getID(), JOptionPane.QUESTION_MESSAGE);
			if(retVal == null)
				return;
				
			p2pSimulator.addToLog("Simulator: Response from " + selectedNode.getID() + " for name " + retVal + ": " + selectedNode.lookup(retVal) + "\r\n");
			break;
		}
		
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		//Not implemented
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		//Not implemented
		
	}

	@Override
	public void mousePressed(MouseEvent me) {
		
		if(me.getButton() != MouseEvent.BUTTON3)
			return;
		
		Point p = me.getPoint();
		
		IP2PNode clickedNode = this.clickedOnNode(p);
		if(clickedNode == null)
			return;
		
		nodeClicked(clickedNode);
		
		popupMenu.show(this, (int) p.getX(), (int) p.getY());
		
	}
	
	private IP2PNode clickedOnNode(Point p)
	{
		IP2PNode clickedNode = null;
		
		int centerX = getSize().width/2;
		int centerY = getSize().height/2;
		int radius;
		
		if(centerX < centerY)
			radius = centerX - offset;
		else
			radius = centerY - offset;
		
		Enumeration<IP2PNode> e = nodes.elements();
		while(e.hasMoreElements())
		{
			IP2PNode n = e.nextElement();
			double angle = getAngle(n.getID());
			int circleX = centerX + (int) (radius * Math.cos(angle));
			int circleY = centerY + (int) (radius * Math.sin(angle));
			
			Rectangle rect = new Rectangle(circleX - 10, circleY - 10, 20, 20);
			if(rect.contains(p))
			{
				clickedNode = n;
				break;
			}
		}
		
		return clickedNode;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		//Not implemented
		
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		
		if(arg0.getSource() == this.fingerTableItem)
		{
			p2pSimulator.addToLog("Simulator: printing finger table of node " + selectedNode.getID() + "\r\n");
			p2pSimulator.addToLog(fingersToString(selectedNode.getFingers()));
			return;
		}
		
		
		if(arg0.getSource() == this.storedDataItem)
		{
			p2pSimulator.addToLog("Simulator: printing stored data of node " + selectedNode.getID() + "\r\n");
			p2pSimulator.addToLog(selectedNode.printStoredData());
			return;
		}
		
	}
	
	private String fingersToString(Iterator<IP2PNode> i)
	{
		String message = "";
		
		int count = 1;
		while(i.hasNext())
		{
			IP2PNode n = i.next();
			message += count + ": " + n.getID() + "\r\n";
			count++;
		}
		
		return message;
	}
}