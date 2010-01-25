package net.sourceforge.jxa.packet.pubsub;

import net.sourceforge.jxa.Manager;
import net.sourceforge.jxa.packet.Packet;

public class PubsubEvent extends Packet {
	public static final String ELEMENT_NAME = "event";
	public static final String NAMESPACE = "http://jabber.org/protocol/pubsub#event";
	
	public PubsubEvent() {
		super(ELEMENT_NAME, NAMESPACE);
	}

	public PubsubEvent(Packet inner) {
		this();
		addPacket(inner);
	}
	
	/**
	 * Gets list of items
	 * 
	 * @return list of Packet object
	 */
	public Enumeration getItems() {
		for (Enumeration e = getPackets(); e.hasMoreElements();) {
			Packet found = (Packet) e.nextElement();
			if (found.equals("items", null))
				return ((PubsubItems) found).getItems();
		}
		return null;
	}
}
