package net.sourceforge.jxa.packet.pubsub;

import net.sourceforge.jxa.Manager;
import net.sourceforge.jxa.packet.Packet;

public class PubsubItem extends Packet {
	public String id;
	public String publisher;
	
	public static final String ELEMENT_NAME = "item";
	public static final String NAMESPACE = null;
	
	public PubsubItem() {
		super(ELEMENT_NAME, NAMESPACE);
	}

	public PubsubItem(String id, Packet inner) {
		this();
		this.id = id;
		addPacket(inner);
	}

	public void emit(Manager manager) throws IOException {
		setProperty("id", id);
		setProperty("publisher", publisher);
		super.emit(manager);
	}
}
