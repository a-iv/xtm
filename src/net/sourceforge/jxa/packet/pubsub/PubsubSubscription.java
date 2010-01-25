package net.sourceforge.jxa.packet.pubsub;

import java.io.IOException;

import net.sourceforge.jxa.Manager;
import net.sourceforge.jxa.packet.Packet;

public class PubsubSubscription extends Packet {
	public String jid;
	public String node;
	public String subid;
	public String subscription;
	
	public static final String ELEMENT_NAME = "subscription";
	public static final String NAMESPACE = null;
	
	public PubsubSubscription() {
		super(ELEMENT_NAME, NAMESPACE);
	}

	public PubsubSubscription(String jid, String subscription) {
		this();
		this.jid = jid;
		this.subscription = subscription;
	}

	public void emit(Manager manager) throws IOException {
		setProperty("jid", jid);
		setProperty("node", node);
		setProperty("subid", subid);
		setProperty("subscription", subscription);
		super.emit(manager);
	}
}
