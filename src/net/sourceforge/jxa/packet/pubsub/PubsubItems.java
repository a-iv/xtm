package net.sourceforge.jxa.packet.pubsub;

import java.io.IOException;

import net.sourceforge.jxa.Manager;
import net.sourceforge.jxa.packet.Packet;

public class PubsubItems extends Packet {
	public String node;

	public static final String ELEMENT_NAME = "items";
	public static final String NAMESPACE = null;
	
	public PubsubItems() {
		super(ELEMENT_NAME, NAMESPACE);
	}

	public PubsubItems(String node, Packet inner) {
		this();
		this.node = node;
		addPacket(inner);
	}

	public void emit(Manager manager) throws IOException {
		setProperty("node", node);
		super.emit(manager);
	}
}
