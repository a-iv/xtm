package net.sourceforge.jxa.packet.pubsub;

import net.sourceforge.jxa.packet.Packet;

public class PubsubOptions extends PubsubNode {
	public static final String ELEMENT_NAME = "options";
	public static final String NAMESPACE = null;
	
	public PubsubOptions() {
		super(ELEMENT_NAME, NAMESPACE);
	}

	public PubsubOptions(String node, String jid, Packet inner) {
		this();
		this.node = node;
		this.jid = jid;
		addPacket(inner);
	}
}
