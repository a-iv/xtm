package net.sourceforge.jxa.provider.pubsub;

import java.io.IOException;
import java.util.Vector;

import net.sourceforge.jxa.Manager;
import net.sourceforge.jxa.packet.Packet;
import net.sourceforge.jxa.packet.pubsub.PubsubItems;
import net.sourceforge.jxa.provider.Provider;

public class PubsubItemsProvider extends Provider {
	private static final Provider itemProvider = new PubsubItemProvider();
	private static final Provider retractProvider = new PubsubRetractProvider();

	public PubsubItemsProvider() {
		super(PubsubItems.ELEMENT_NAME, PubsubItems.NAMESPACE, false);
	}

	protected Packet createPacket() {
		return new PubsubItems();
	}
	
	protected Packet parseComplited(Packet packet) {
		PubsubItems node = (PubsubItems) packet;
		node.node = packet.getProperty("node");
		return node;
	}

	protected Packet parseInner(Manager manager, Packet packet) throws IOException {
		Vector providers = new Vector();
		providers.addElement(itemProvider);
		providers.addElement(retractProvider);
		return manager.parse(providers.elements(), true);
	}
}
