package net.sourceforge.jxa.provider.pubsub;

import net.sourceforge.jxa.Manager;
import net.sourceforge.jxa.packet.Packet;
import net.sourceforge.jxa.packet.pubsub.Pubsub;
import net.sourceforge.jxa.packet.pubsub.PubsubEvent;
import net.sourceforge.jxa.provider.Provider;

public class PubsubEventProvider extends Provider {
	private static final Provider itemsProvider = new PubsubItemProvider();
	
	public PubsubProvider() {
		super(PubsubEvent.ELEMENT_NAME, PubsubEvent.NAMESPACE, false);
	}

	protected Packet createPacket() {
		return new PubsubEvent();
	}
	
	protected Packet parseInner(Manager manager, Packet packet) throws IOException {
		Vector providers = new Vector();
		providers.addElement(itemsProvider);
		return manager.parse(providers.elements(), true);
	}

}
