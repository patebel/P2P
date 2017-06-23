package vs;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class P2PNode extends TimerTask implements IP2PNode {

	public Timer timer;
	public long ID;
	public IP2PNode Predecessor = null;
	public IP2PNode Successor = null;
	IP2PNode fingertable [] = null; 
	int next = 0;	

	public P2PNode() {
		ID = setID();
		join();
		timer = new Timer(true);
		timer.schedule(this, 1000, 1000);
	}
	
	private void join(){
		IP2PNode b = Environment.getBootstrapNode();
		if (b!=null){
			IP2PNode s = b.findSuccessor(this.getID());
			build_fingers(s);
			Successor = s;
		}
		else{
			Successor = this;
			Predecessor = this;
			build_fingers(this);
		}
	}

	private void build_fingers(IP2PNode s) {
		int i_zero = (int) Math.floor((Math.log(this.Successor.getID()-this.getID())/Math.log(2))+1);
		for (int i=0; i < i_zero; i++){
			fingertable[i]=s;
		}
			
		for (int i = i_zero; i <= 32; i++){
			fingertable[i] = s.findSuccessor((long) (this.getID() + Math.pow(2, i-1)));
		}
		
	}

	private long setID() {
		Random r = new Random();
		long ID = (long) Math.sqrt(Math.pow(r.nextInt(), 2)) * 2;
		//Integer ID = r.ints(0, (int) 4294967295).limit(1).findFirst().getAsInt();
		return ID;
	}

	@Override
	public IP2PNode findSuccessor(long id) {
		if ((this.getID() > this.Successor.getID()) && ((id>this.getID()) || (id<Successor.getID()))){
				return Successor;
		}
		else if ((id>this.getID()) && (id<Successor.getID())){
			return Successor;
		}
		else if (id == this.getID()){
			return this;
		}
		else {
			IP2PNode n_prime = closest_preceding_node(id);
			IP2PNode NN = n_prime.findSuccessor(id);
			return NN;
		}
	}
	
	public IP2PNode closest_preceding_node(long id){
		for(int i=32; i==1; i--){
			if (this.getID()<fingertable[i].getID() && id>fingertable[i].getID()){
				return fingertable[i];
			}
		}
		return this;
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
		
			if (this.getID() < this.Predecessor.getID()){
				if ((newPredecessor.getID()>this.Predecessor.getID()) || (newPredecessor.getID()<this.getID())){
					this.Predecessor = newPredecessor;
				}
			}
			else if ((newPredecessor.getID()>this.Predecessor.getID()) && (newPredecessor.getID()<this.getID())){
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
		Iterator<IP2PNode> Fingers = Arrays.asList(fingertable).iterator();
		return Fingers;
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
		next += 1;
		if (next > 32){next=1;}
		
		fingertable[next]=findSuccessor((long) (this.getID() + Math.pow(2, next-1)));
	}

	private void stabilze() {
	IP2PNode n = this.Successor.getPredecessor();
	if (this.Successor.getID()<this.getID()){
		if ((n.getID()<this.Successor.getID()) || (n.getID()>this.getID())){
			this.Successor = n;
		}
	}
	else if ((n.getID()<this.Successor.getID()) && (n.getID()>this.getID())){
		this.Successor = n;
	}
	this.Successor.notify(this);	
	}

	/*
	 * Periodische Methoden
	 */
	@Override
	public void run() {
		if (Environment.getBootstrapNode() != null){
			this.stabilze();
			this.fix_fingers();
		}
	}
}
