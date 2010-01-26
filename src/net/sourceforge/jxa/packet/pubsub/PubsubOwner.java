package net.sourceforge.jxa.packet.pubsub;

import net.sourceforge.jxa.packet.Packet;

public class PubsubOwner extends Packet {
	public static final String ELEMENT_NAME = "pubsub";
	public static final String NAMESPACE = "http://jabber.org/protocol/pubsub#owner";
	
	public PubsubOwner() {
		super(ELEMENT_NAME, NAMESPACE);
	}

	public PubsubOwner(Packet inner) {
		this();
		addPacket(inner);
	}
}
