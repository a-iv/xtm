package net.sourceforge.jxa.packet;

import java.util.Enumeration;
import java.util.Vector;

public class Roster extends Packet {
	/**
	 * Gets items
	 * 
	 * @return List of RosterItem
	 */
	public Enumeration getItems() {
		Vector result = new Vector();
		for (Enumeration e = getPackets(); e.hasMoreElements();) {
			Packet found = (Packet) e.nextElement();
			if (found.equals("item", null))
				result.addElement(found);
		}
		return result.elements();
	}
}
