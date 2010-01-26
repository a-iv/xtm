package net.sourceforge.jxa.provider.pubsub;

import java.io.IOException;
import java.util.Vector;

import net.sourceforge.jxa.Manager;
import net.sourceforge.jxa.packet.Packet;
import net.sourceforge.jxa.packet.pubsub.PubsubSubscriptions;
import net.sourceforge.jxa.provider.Provider;

public class PubsubSubscriptionsProvider extends Provider {
	private static final Provider subscriptionProvider = new PubsubSubscriptionProvider();

	public PubsubSubscriptionsProvider() {
		super(PubsubSubscriptions.ELEMENT_NAME, PubsubSubscriptions.NAMESPACE, false);
	}

	protected Packet createPacket() {
		return new PubsubSubscriptions();
	}
	
	protected Packet parseComplited(Packet packet) {
		PubsubSubscriptions node = (PubsubSubscriptions) packet;
		node.node = packet.getProperty("node");
		return node;
	}

	protected Packet parseInner(Manager manager, Packet packet) throws IOException {
		Vector providers = new Vector();
		providers.addElement(subscriptionProvider);
		return manager.parse(providers.elements(), true);
	}
}
