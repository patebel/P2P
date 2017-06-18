package vs;
import java.util.TimerTask;

public class P2PViewUpdateTask extends TimerTask {

	private P2PSimulatorView p2pSimView = null;
	
	public P2PViewUpdateTask(P2PSimulatorView p2pSimView)
	{
		this.p2pSimView = p2pSimView;
	}
	
	public void run()
	{
		p2pSimView.repaint();
	}
}
