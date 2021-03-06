package net.sourceforge.jxa.packet.pubsub;

import java.io.IOException;

import net.sourceforge.jxa.Manager;
import net.sourceforge.jxa.packet.Packet;

public class PubsubRetract extends Packet {
	public String id;
	public String node;
	
	public static final String ELEMENT_NAME = "retract";
	public static final String NAMESPACE = null;
	
	public PubsubRetract() {
		super(ELEMENT_NAME, NAMESPACE);
	}
	
	public PubsubRetract(String node, Packet inner) {
		this();
		this.node = node;
		addPacket(inner);
	}

	public void emit(Manager manager) throws IOException {
		setProperty("id", id);
		setProperty("node", node);
		super.emit(manager);
	}
}
