package net.sourceforge.jxa.packet.pubsub;

import java.io.IOException;

import net.sourceforge.jxa.Manager;
import net.sourceforge.jxa.packet.Packet;

public class PubsubDelete extends Packet {
	public String node;

	public static final String ELEMENT_NAME = "delete";
	public static final String NAMESPACE = null;
	
	public PubsubDelete() {
		super(ELEMENT_NAME, NAMESPACE);
	}

	public PubsubDelete(String node) {
		this();
		this.node = node;
	}

	public void emit(Manager manager) throws IOException {
		setProperty("node", node);
		super.emit(manager);
	}
}
