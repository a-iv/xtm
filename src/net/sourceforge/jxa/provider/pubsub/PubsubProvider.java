package net.sourceforge.jxa.provider.pubsub;

import net.sourceforge.jxa.Manager;
import net.sourceforge.jxa.packet.IQ;
import net.sourceforge.jxa.packet.Packet;
import net.sourceforge.jxa.packet.pubsub.Pubsub;
import net.sourceforge.jxa.provider.BindProvider;
import net.sourceforge.jxa.provider.Provider;

public class PubsubProvider extends Provider {
	private static final Provider subscriptionProvider = new Provider("subscription", null, true);
	private static final Provider optionsProvider = new PubsubNodeProvider("options", null, false);
	private static final Provider itemsProvider = new PubsubNodeProvider("items", null, false);
	private static final Provider publishProvider = new PubsubNodeProvider("publish", null, false);
	
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
		return manager.parse(providers.elements(), true);
	}
}
