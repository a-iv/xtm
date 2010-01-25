package net.sourceforge.jxa.packet.pubsub;

import java.io.IOException;

import net.sourceforge.jxa.Manager;
import net.sourceforge.jxa.packet.Packet;

public class PubsubAffiliations extends Packet {
	public String node;

	public static final String ELEMENT_NAME = "affiliations";
	public static final String NAMESPACE = null;
	
	public PubsubAffiliations() {
		super(ELEMENT_NAME, NAMESPACE);
	}

	public PubsubAffiliations(String node, Packet inner) {
		this();
		this.node = node;
		addPacket(inner);
	}

	public void emit(Manager manager) throws IOException {
		setProperty("node", node);
		super.emit(manager);
	}
}
