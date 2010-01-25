package net.sourceforge.jxa.provider.pubsub;

import java.io.IOException;
import java.util.Vector;

import net.sourceforge.jxa.Manager;
import net.sourceforge.jxa.packet.Packet;
import net.sourceforge.jxa.packet.pubsub.PubsubContainer;
import net.sourceforge.jxa.provider.Provider;

public class PubsubContainerProvider extends Provider {
	private static final Provider itemProvider = new PubsubItemProvider();
	private static final Provider retractProvider = new PubsubRetractProvider();
	private static final Provider subscriptionProvider = new PubsubSubscriptionProvider();

	public PubsubContainerProvider(String elementName, String namespace, boolean makeEvent) {
		super(elementName, namespace, makeEvent);
	}

	protected Packet createPacket() {
		return new PubsubContainer();
	}
	
	protected Packet parseComplited(Packet packet) {
		PubsubContainer node = (PubsubContainer) packet;
		node.jid = packet.getProperty("jid");
		node.node = packet.getProperty("node");
		return node;
	}

	protected Packet parseInner(Manager manager, Packet packet) throws IOException {
		Vector providers = new Vector();
		providers.addElement(itemProvider);
		providers.addElement(retractProvider);
		providers.addElement(subscriptionProvider);
		return manager.parse(providers.elements(), true);
	}
}
