package net.sourceforge.jxa.provider.pubsub;

import java.io.IOException;
import java.util.Vector;

import net.sourceforge.jxa.Manager;
import net.sourceforge.jxa.packet.Packet;
import net.sourceforge.jxa.packet.pubsub.PubsubEvent;
import net.sourceforge.jxa.provider.Provider;

public class PubsubEventProvider extends Provider {
	private static final Provider deleteProvider = new PubsubDeleteProvider();
	private static final Provider itemsProvider = new PubsubItemsProvider();
	private static final Provider subscriptionProvider = new PubsubSubscriptionProvider();
	
	public PubsubEventProvider() {
		super(PubsubEvent.ELEMENT_NAME, PubsubEvent.NAMESPACE, false);
	}

	protected Packet createPacket() {
		return new PubsubEvent();
	}
	
	protected Packet parseInner(Manager manager, Packet packet) throws IOException {
		Vector providers = new Vector();
		providers.addElement(deleteProvider);
		providers.addElement(itemsProvider);
		providers.addElement(subscriptionProvider);
		return manager.parse(providers.elements(), true);
	}

}
