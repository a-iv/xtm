package net.sourceforge.jxa.packet;

import java.util.Enumeration;

public class Pubsub extends Packet {
	public Pubsub() {
		super("pubsub", "http://jabber.org/protocol/pubsub");
	}

	public Pubsub(Packet inner) {
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
