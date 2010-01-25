package net.sourceforge.jxa.provider.pubsub;

import net.sourceforge.jxa.packet.Packet;
import net.sourceforge.jxa.packet.pubsub.PubsubSubscription;
import net.sourceforge.jxa.provider.Provider;

public class PubsubSubscriptionProvider extends Provider {
	public PubsubSubscriptionProvider() {
		super(PubsubSubscription.ELEMENT_NAME, PubsubSubscription.NAMESPACE, true);
	}

	protected Packet createPacket() {
		return new PubsubSubscription();
	}
	
	protected Packet parseComplited(Packet packet) {
		PubsubSubscription subscription = (PubsubSubscription) packet;
		subscription.jid = packet.getProperty("jid");
		subscription.node = packet.getProperty("node");
		subscription.subid = packet.getProperty("subid");
		subscription.subscription = packet.getProperty("subscription");
		return subscription;
	}
}
