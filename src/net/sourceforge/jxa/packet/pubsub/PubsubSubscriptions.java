package net.sourceforge.jxa.packet.pubsub;

import java.io.IOException;

import net.sourceforge.jxa.Manager;
import net.sourceforge.jxa.packet.Packet;

public class PubsubSubscriptions extends Packet {
	public String node;

	public static final String ELEMENT_NAME = "subscriptions";
	public static final String NAMESPACE = null;
	
	public PubsubSubscriptions() {
		super(ELEMENT_NAME, NAMESPACE);
	}

	public PubsubSubscriptions(String node, Packet inner) {
		this();
		this.node = node;
		addPacket(inner);
	}

	public void emit(Manager manager) throws IOException {
		setProperty("node", node);
		super.emit(manager);
	}
}
