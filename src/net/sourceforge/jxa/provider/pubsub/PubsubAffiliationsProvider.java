package net.sourceforge.jxa.provider.pubsub;

import java.io.IOException;
import java.util.Vector;

import net.sourceforge.jxa.Manager;
import net.sourceforge.jxa.packet.Packet;
import net.sourceforge.jxa.packet.pubsub.PubsubAffiliations;
import net.sourceforge.jxa.provider.Provider;

public class PubsubAffiliationsProvider extends Provider {
	private static final Provider affiliationProvider = new PubsubAffiliationProvider();

	public PubsubAffiliationsProvider() {
		super(PubsubAffiliations.ELEMENT_NAME, PubsubAffiliations.NAMESPACE, false);
	}

	protected Packet createPacket() {
		return new PubsubAffiliations();
	}
	
	protected Packet parseComplited(Packet packet) {
		PubsubAffiliations node = (PubsubAffiliations) packet;
		node.node = packet.getProperty("node");
		return node;
	}

	protected Packet parseInner(Manager manager, Packet packet) throws IOException {
		Vector providers = new Vector();
		providers.addElement(affiliationProvider);
		return manager.parse(providers.elements(), true);
	}

}
