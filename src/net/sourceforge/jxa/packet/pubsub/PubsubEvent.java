package net.sourceforge.jxa.packet.pubsub;

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
}
