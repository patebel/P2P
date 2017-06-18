package vs;
import java.util.Iterator;


public interface IP2PNode {

	/**
	 * Findet gemäß dem Chord-Routingprotokoll den nächsten Knoten nach <code>id</code> und gibt eine Referenz darauf zurück.
	 * 
	 * @param id ID im ID-Raum, dessen Nachfolgeknoten gefunden werden soll
	 * @return Referenz auf den Nachfolgeknoten, <code>null</code> falls Knoten unbekannt
	 */
	public IP2PNode findSuccessor(long id);
	
	/**
	 * Findet gemäß dem Chord-Routingprotokoll den Vorgänger des Knotens und gibt eine Referenz darauf zurück.
	 * 
	 * @return Referenz auf den Vorgängerknoten, <code>null</code> falls Knoten nicht bekannt
	 */
	public IP2PNode getPredecessor();
	
	/**
	 * Findet gemäß dem Chord-Routingprotokoll den Nachfolger des Knotens und gibt eine Referenz darauf zurück.
	 * 
	 * @return Referenz auf den Nachfolgerknoten, <code>null</code> falls Knoten nicht bekannt
	 */
	public IP2PNode getSuccessor();
	
	/**
	 * Implementierung der notify-Funktion gemäß des Chord-Routingprotokolls.
	 * 
	 * @param newPredecessor neuer Vorgänger des Knotens
	 */
	public void notify(IP2PNode newPredecessor);
	
	/**
	 * Liefert die ID des aktuellen Knotens zurück.
	 * 
	 * @return die ID des aktuellen Knotens
	 */
	public long getID();
	
	/**
	 * Gibt den aktuellen Finger-Table des Knotens zurück.
	 * 
	 *  @return Iterator-Objekt mit dem der Finger-Table des Knotens durchlaufen werden kann.
	 */
	public Iterator<IP2PNode> getFingers();
	
	/**
	 * Speichert den Wert <code>value</code> zu dem Schlüssel <code>key</code> an der korrekten Stelle im P2P-Netzwerk. Die korrekte Stelle im P2P-Netzwerk ist der Knoten, 
	 * der gemäß dem Chord-Routingprotokoll für <code>Environment.hash(key)</code> verantwortlich ist.
	 * 
	 * @param key Schlüssel der den Speicherort für <code>value</code> bestimmt
	 * @param value der zu speichernde Wert
	 * @return <code>true</code> falls Speicherung erfolgreich, sonst <code>false</code>
	 */
	public boolean store(String key, String value);
	
	/**
	 * Sucht den Wert <code>value</code> zu dem Schlüssel <code>key</code> im P2P-Netzwerk und gibt ihn zurück. Der Wert <code>value</code> wurde zuvor mit der Operation <code>store</code>
	 * im P2P-Netzwerk gespeichert. 
	 * 
	 * @param key Schlüssel der den Speicherort für <code>value</code> bestimmt
	 * @return <code>value</code> falls Suche erfolgreich, sonst <code>null</code>
	 */
	public String lookup(String key);
	
	/**
	 * Gibt alle gespeicherten key-value Paare des Knotens in einer optisch übersichtlichen Form zurück.
	 * 
	 * @return alle gespeicherten key-value Paare des Knotens
	 */
	public String printStoredData(); 
	
}
