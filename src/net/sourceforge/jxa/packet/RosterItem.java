package net.sourceforge.jxa.packet;

import java.util.Enumeration;
import java.util.Vector;

public class RosterItem extends Packet {
	/**
	 * Gets groups
	 * 
	 * @return List of String
	 */
	public Enumeration getGroups() {
		Vector result = new Vector();
		for (Enumeration e = getPackets(); e.hasMoreElements();) {
			Packet found = (Packet) e.nextElement();
			if (found.equals("group", null))
				result.addElement(found.getPayload());
		}
		if (result.size() == 0)
			result.addElement(new String(""));
		return result.elements();
	}
}
