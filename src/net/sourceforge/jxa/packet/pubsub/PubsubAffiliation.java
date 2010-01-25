package net.sourceforge.jxa.packet.pubsub;

import java.io.IOException;

import net.sourceforge.jxa.Manager;
import net.sourceforge.jxa.packet.Packet;

public class PubsubAffiliation extends Packet {
	public String jid;
	public String affiliation;
	
	public static final String ELEMENT_NAME = "affiliation";
	public static final String NAMESPACE = null;
	
	public PubsubAffiliation() {
		super(ELEMENT_NAME, NAMESPACE);
	}

	public PubsubAffiliation(String jid, String affiliation) {
		this();
		this.jid = jid;
		this.affiliation = affiliation;
	}

	public void emit(Manager manager) throws IOException {
		setProperty("jid", jid);
		setProperty("affiliation", affiliation);
		super.emit(manager);
	}
}
