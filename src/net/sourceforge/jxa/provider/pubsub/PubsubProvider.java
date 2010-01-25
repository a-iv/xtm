package net.sourceforge.jxa.provider.pubsub;

import java.io.IOException;
import java.util.Vector;

import net.sourceforge.jxa.Manager;
import net.sourceforge.jxa.packet.Packet;
import net.sourceforge.jxa.packet.pubsub.Pubsub;
import net.sourceforge.jxa.provider.Provider;

public class PubsubProvider extends Provider {
	private static final Provider subscriptionProvider = new PubsubSubscriptionProvider();

	private static final Provider optionsProvider = new PubsubContainerProvider("options", null, false);
	private static final Provider itemsProvider = new PubsubContainerProvider("items", null, false);
	private static final Provider publishProvider = new PubsubContainerProvider("publish", null, false);
	private static final Provider subscriptionsProvider = new PubsubContainerProvider("subscriptions", null, false);
	
	public PubsubProvider() {
		super(Pubsub.ELEMENT_NAME, Pubsub.NAMESPACE, false);
	}

	protected Packet createPacket() {
		return new Pubsub();
	}
	
	protected Packet parseInner(Manager manager, Packet packet) throws IOException {
		Vector providers = new Vector();
		providers.addElement(subscriptionProvider);
		providers.addElement(optionsProvider);
		providers.addElement(itemsProvider);
		providers.addElement(publishProvider);
		providers.addElement(subscriptionsProvider);
		return manager.parse(providers.elements(), true);
	}
}
