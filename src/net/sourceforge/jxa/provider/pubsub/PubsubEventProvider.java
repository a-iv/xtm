package net.sourceforge.jxa.provider.pubsub;

import java.io.IOException;
import java.util.Vector;

import net.sourceforge.jxa.Manager;
import net.sourceforge.jxa.packet.Packet;
import net.sourceforge.jxa.packet.pubsub.PubsubEvent;
import net.sourceforge.jxa.provider.Provider;

public class PubsubEventProvider extends Provider {
	private static final Provider subscriptionProvider = new PubsubSubscriptionProvider();

	private static final Provider itemsProvider = new PubsubItemProvider();
	
	public PubsubEventProvider() {
		super(PubsubEvent.ELEMENT_NAME, PubsubEvent.NAMESPACE, false);
	}

	protected Packet createPacket() {
		return new PubsubEvent();
	}
	
	protected Packet parseInner(Manager manager, Packet packet) throws IOException {
		Vector providers = new Vector();
		providers.addElement(subscriptionProvider);
		providers.addElement(itemsProvider);
		return manager.parse(providers.elements(), true);
	}

}
