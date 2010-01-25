package net.sourceforge.jxa.packet.pubsub;

import net.sourceforge.jxa.packet.Packet;

public class PubsubPublish extends PubsubContainer {
	public static final String ELEMENT_NAME = "publish";
	public static final String NAMESPACE = null;
	
	public PubsubPublish() {
		super(ELEMENT_NAME, NAMESPACE);
	}

	public PubsubPublish(String node, String jid, Packet inner) {
		this();
		this.node = node;
		this.jid = jid;
		addPacket(inner);
	}
}
