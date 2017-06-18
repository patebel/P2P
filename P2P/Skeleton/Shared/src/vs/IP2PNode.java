package vs;
import java.util.Iterator;


public interface IP2PNode {

	/**
	 * Findet gem�� dem Chord-Routingprotokoll den n�chsten Knoten nach <code>id</code> und gibt eine Referenz darauf zur�ck.
	 * 
	 * @param id ID im ID-Raum, dessen Nachfolgeknoten gefunden werden soll
	 * @return Referenz auf den Nachfolgeknoten, <code>null</code> falls Knoten unbekannt
	 */
	public IP2PNode findSuccessor(long id);
	
	/**
	 * Findet gem�� dem Chord-Routingprotokoll den Vorg�nger des Knotens und gibt eine Referenz darauf zur�ck.
	 * 
	 * @return Referenz auf den Vorg�ngerknoten, <code>null</code> falls Knoten nicht bekannt
	 */
	public IP2PNode getPredecessor();
	
	/**
	 * Findet gem�� dem Chord-Routingprotokoll den Nachfolger des Knotens und gibt eine Referenz darauf zur�ck.
	 * 
	 * @return Referenz auf den Nachfolgerknoten, <code>null</code> falls Knoten nicht bekannt
	 */
	public IP2PNode getSuccessor();
	
	/**
	 * Implementierung der notify-Funktion gem�� des Chord-Routingprotokolls.
	 * 
	 * @param newPredecessor neuer Vorg�nger des Knotens
	 */
	public void notify(IP2PNode newPredecessor);
	
	/**
	 * Liefert die ID des aktuellen Knotens zur�ck.
	 * 
	 * @return die ID des aktuellen Knotens
	 */
	public long getID();
	
	/**
	 * Gibt den aktuellen Finger-Table des Knotens zur�ck.
	 * 
	 *  @return Iterator-Objekt mit dem der Finger-Table des Knotens durchlaufen werden kann.
	 */
	public Iterator<IP2PNode> getFingers();
	
	/**
	 * Speichert den Wert <code>value</code> zu dem Schl�ssel <code>key</code> an der korrekten Stelle im P2P-Netzwerk. Die korrekte Stelle im P2P-Netzwerk ist der Knoten, 
	 * der gem�� dem Chord-Routingprotokoll f�r <code>Environment.hash(key)</code> verantwortlich ist.
	 * 
	 * @param key Schl�ssel der den Speicherort f�r <code>value</code> bestimmt
	 * @param value der zu speichernde Wert
	 * @return <code>true</code> falls Speicherung erfolgreich, sonst <code>false</code>
	 */
	public boolean store(String key, String value);
	
	/**
	 * Sucht den Wert <code>value</code> zu dem Schl�ssel <code>key</code> im P2P-Netzwerk und gibt ihn zur�ck. Der Wert <code>value</code> wurde zuvor mit der Operation <code>store</code>
	 * im P2P-Netzwerk gespeichert. 
	 * 
	 * @param key Schl�ssel der den Speicherort f�r <code>value</code> bestimmt
	 * @return <code>value</code> falls Suche erfolgreich, sonst <code>null</code>
	 */
	public String lookup(String key);
	
	/**
	 * Gibt alle gespeicherten key-value Paare des Knotens in einer optisch �bersichtlichen Form zur�ck.
	 * 
	 * @return alle gespeicherten key-value Paare des Knotens
	 */
	public String printStoredData(); 
	
}
