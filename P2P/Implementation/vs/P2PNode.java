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
	IP2PNode fingertable[] = new IP2PNode[32]; 
	int next = 0;
	boolean bootcomp = false;

	public P2PNode() {
		ID = setID();
		bootcomp = true;
		timer = new Timer(true);
		timer.schedule(this, 1000, 1000);
	}
	
	private void join(){
		IP2PNode b = Environment.getBootstrapNode();
		if(b.getID()==this.getID()){
			Successor = this;
			for (int i = 0; i <= 31; i++){
				this.fingertable[i] = this;
			}
		}
		else if (b!=null){
			IP2PNode s = b.findSuccessor(this.getID());
			Successor = s;
			build_fingers(s);
		}
	}

	private void build_fingers(IP2PNode s) {
			int i_zero = (int) Math.floor((Math.log(this.Successor.getID()-this.getID())/Math.log(2))+1);
			for (int i=0; i < i_zero; i++){
				this.fingertable[i]=s;
			}
				
			for (int i = i_zero; i <= 31; i++){
				long nextID = this.getID() + (long) Math.pow(2, i-1);
				this.fingertable[i] = s.findSuccessor(nextID);
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
		if (this.getID() > this.Successor.getID()){
			if ((id>this.getID()) || (id<Successor.getID())){
				return Successor;
			}
			else{
				IP2PNode n_prime = closest_preceding_node(id);
				IP2PNode NN = n_prime.findSuccessor(id);
				return NN;
			}
		}
		else if ((id>this.getID()) && (id<Successor.getID())){
			return Successor;
		}
		else if (this.Successor.getID() == this.getID()){
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
			if (this.getID()<this.fingertable[i].getID() && id>this.fingertable[i].getID()){
				return this.fingertable[i];
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
		
		if (this.Predecessor!=null){
		
		if (this.getID() < this.Predecessor.getID()){
				if ((newPredecessor.getID()>this.Predecessor.getID()) || (newPredecessor.getID()<this.getID())){
					this.Predecessor = newPredecessor;
				}
			}
		else if ((newPredecessor.getID()>this.Predecessor.getID()) && (newPredecessor.getID()<this.getID())){
			this.Predecessor = newPredecessor;
		}
	}
		else if (this.Predecessor==null) {
			this.Predecessor = newPredecessor;
		}
	}

	@Override
	public long getID() {
		
		return this.ID;
	}

	@Override
	public Iterator<IP2PNode> getFingers() {
		Iterator<IP2PNode> Fingers = Arrays.asList(this.fingertable).iterator();
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
		if (next >= 32){next=1;}
		
		this.fingertable[next]=findSuccessor((long) (this.getID() + Math.pow(2, next-1)));
	}

	private void stabilize() {
	IP2PNode n = this.Successor.getPredecessor();
	if (n!=null){
		if (this.Successor.getID()<this.getID()){
			if ((n.getID()<this.Successor.getID()) || (n.getID()>this.getID())){
				this.Successor = n;
			}
		}
		else if ((n.getID()<this.Successor.getID()) && (n.getID()>this.getID())){
			this.Successor = n;
		}
	}
	this.Successor.notify(this);	
	}

	/*
	 * Periodische Methoden
	 */
	@Override
	public void run() {
		if (bootcomp==true){
			join();
			bootcomp=false;
		}
		else{
			this.stabilize();
			this.fix_fingers();
		}
	}
}
