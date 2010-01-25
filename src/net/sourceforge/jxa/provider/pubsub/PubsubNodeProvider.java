package net.sourceforge.jxa.provider.pubsub;

import net.sourceforge.jxa.Manager;
import net.sourceforge.jxa.packet.IQ;
import net.sourceforge.jxa.packet.Packet;
import net.sourceforge.jxa.packet.pubsub.Enumeration;
import net.sourceforge.jxa.packet.pubsub.PubsubItems;
import net.sourceforge.jxa.packet.pubsub.PubsubNode;
import net.sourceforge.jxa.provider.BindProvider;
import net.sourceforge.jxa.provider.IOException;
import net.sourceforge.jxa.provider.Provider;
import net.sourceforge.jxa.provider.String;
import net.sourceforge.jxa.provider.Vector;

public class PubsubNodeProvider extends Provider {
	private static final Provider itemProvider = new PubsubItemProvider();
	private static final Provider retractProvider = new PubsubRetractProvider();

	protected Packet createPacket() {
		return new PubsubNode();
	}
	
	protected Packet parseComplited(Packet packet) {
		PubsubNode node = (PubsubNode) packet;
		node.jid = packet.getProperty("jid");
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
