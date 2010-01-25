package net.sourceforge.jxa.packet.pubsub;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

import net.sourceforge.jxa.Manager;
import net.sourceforge.jxa.packet.Packet;

public class PubsubItems extends PubsubNode {
	public static final String ELEMENT_NAME = "items";
	public static final String NAMESPACE = null;
	
	public PubsubItems() {
		super(ELEMENT_NAME, NAMESPACE);
	}

	public PubsubItems(String node, String jid, Packet inner) {
		this();
		this.node = node;
		this.jid = jid;
		addPacket(inner);
	}
}
