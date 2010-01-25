package net.sourceforge.jxa.packet.pubsub;

import net.sourceforge.jxa.packet.Packet;

public class Pubsub extends Packet {
	public static final String ELEMENT_NAME = "pubsub";
	public static final String NAMESPACE = "http://jabber.org/protocol/pubsub";
	
	public Pubsub() {
		super(ELEMENT_NAME, NAMESPACE);
	}

	public Pubsub(Packet inner) {
		this();
		addPacket(inner);
	}
}
