package net.sourceforge.jxa.provider.pubsub;

import net.sourceforge.jxa.packet.Packet;
import net.sourceforge.jxa.packet.pubsub.PubsubAffiliation;
import net.sourceforge.jxa.provider.Provider;

public class PubsubAffiliationProvider extends Provider {
	public PubsubAffiliationProvider() {
		super(PubsubAffiliation.ELEMENT_NAME, PubsubAffiliation.NAMESPACE, true);
	}

	protected Packet createPacket() {
		return new PubsubAffiliation();
	}
	
	protected Packet parseComplited(Packet packet) {
		PubsubAffiliation affiliation = (PubsubAffiliation) packet;
		affiliation.jid = packet.getProperty("jid");
		affiliation.affiliation = packet.getProperty("affiliation");
		return affiliation;
	}
}
