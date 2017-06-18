package vs;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.math.BigInteger;


public class Environment {

	private static IP2PNode bootstrapNode = null;
	private static IP2PSimulator p2pSimulator = null;
	
	/**
	 * Größe des ID-Raums
	 */
	public static final long ID_SPACE_SIZE = 4294967295L; //2^32-1
	
	/**
	 * Größe der zu implementierenden Finger-Table
	 */
	public static final int FINGER_TABLE_SIZE = 32; 
	
	/**
	 * Liefert Referenz auf den Bootstrap-Knoten zum Betreten des P2P-Netzwerks.
	 * 
	 * @return Referenz auf den Bootstrap-Knoten, <code>null</code> falls noch kein Bootstrap-Knoten vorhanenden
	 */
	public static IP2PNode getBootstrapNode()
	{
		return bootstrapNode;
	}
	
	public static void setBootstrapNode(IP2PNode bsn)
	{
		bootstrapNode = bsn;
	}
	
	/**
	 * Schreibt eine Nachricht in das Log-Fenster des CIT P2P-Simulators
	 * 
	 * @param p2pNode Referenz auf Knoten, der die Funktion aufruft
	 * @param message Nachricht, die geloggt werden soll
	 */
	public static void log(IP2PNode p2pNode, String message)
	{
		if(p2pSimulator != null)
			p2pSimulator.log(p2pNode, message);
	}
	
	public static void registerP2PSimualtor(IP2PSimulator p2pSim)
	{
		if(p2pSimulator == null)
			p2pSimulator = p2pSim;
	}
	
	/**
	 * Hashfunktion, die für <code>store</code>, <code>lookup</code>-Operation genutzt werden soll.
	 * 
	 * @param key der zu hashende Schlüssel
	 * @return Hashwert des Schlüssels
	 */
	public static long hash(String key)
	{
		MessageDigest md;
		
		try
		{
			md = MessageDigest.getInstance("SHA-1");
		}
		catch(NoSuchAlgorithmException e)
		{
			System.err.println("Your Java does not support SHA-1 hashing. Please upgrade...");
			System.exit(1);
			return 0;
		}
		
		md.update(key.getBytes(), 0, key.length());
		BigInteger bi = new BigInteger(md.digest());
		long l = bi.longValue();
		l = l % (ID_SPACE_SIZE + 1);
		
		if(l < 0)
			l += (ID_SPACE_SIZE + 1);
		
		return l;
	}
}
