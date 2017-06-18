package vs;

import java.util.Iterator;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class P2PNode extends TimerTask implements IP2PNode {

	public Timer timer;
	public Integer ID;
	public IP2PNode Predecessor;
	public IP2PNode Successor;

	public P2PNode() {
		timer = new Timer(true);
		timer.schedule(this, 1000, 1000);
		ID = setID();
	}

	private Integer setID() {
		Random r = new Random();
		Integer ID = r.ints(0, (int) 4294967295L).limit(1).findFirst().getAsInt();
		return ID;
	}

	@Override
	public IP2PNode findSuccessor(long id) {
		if ID in (this.ID, Successor.ID);
		return Successor;
		else
		nprime = closest_preceding_node(id);
			return nprime.find_successor(id);
		return null;
	}

	@Override
	public IP2PNode getPredecessor() {
		
		return this.Predecessor;
	}

	@Override
	public IP2PNode getSuccessor() {
	
		return this.Successor;
	}

	@Override
	public void notify(IP2PNode newPredecessor) {
		
			if (this.ID < this.Predecessor.getID()){
				if ((newPredecessor.getID()>this.Predecessor.getID()) || (newPredecessor.getID()<this.ID)){
					this.Predecessor = newPredecessor;
				}
			}
			else if ((newPredecessor.getID()>this.Predecessor.getID()) && (newPredecessor.getID()<this.ID)){
				this.Predecessor = newPredecessor;
			}
			else if (this.Predecessor==null){
				this.Predecessor = newPredecessor;
			}

	}

	@Override
	public long getID() {
		
		return this.ID;
	}

	@Override
	public Iterator<IP2PNode> getFingers() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean store(String key, String value) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String lookup(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String printStoredData() {
		// TODO Auto-generated method stub
		return null;
	}

	private void fix_fingers() {
		
		

	}

	private void stabilze() {
	IP2PNode n = this.Successor.getPredecessor();
	if (this.Successor.getID()<this.ID){
		if ((n.getID()<this.Successor.getID()) || (n.getID()>this.ID)){
			this.Successor = n;
		}
	}
	else if ((n.getID()<this.Successor.getID()) && (n.getID()>this.ID)){
		this.Successor = n;
	}
	this.Successor.notify(this);	
	}

	/*
	 * Periodische Methoden
	 */
	@Override
	public void run() {
		this.stabilze();
		this.fix_fingers();

	}
}
