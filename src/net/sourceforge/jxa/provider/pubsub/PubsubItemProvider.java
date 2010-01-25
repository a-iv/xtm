package net.sourceforge.jxa.provider.pubsub;

import net.sourceforge.jxa.Manager;
import net.sourceforge.jxa.packet.Packet;
import net.sourceforge.jxa.packet.pubsub.Pubsub;
import net.sourceforge.jxa.packet.pubsub.PubsubItem;
import net.sourceforge.jxa.provider.Provider;

public class PubsubItemProvider extends Provider {
	public PubsubItemProvider() {
		super(PubsubItem.ELEMENT_NAME, PubsubItem.NAMESPACE, true);
	}

	protected Packet createPacket() {
		return new PubsubItem();
	}
	
	protected Packet parseComplited(Packet packet) {
		PubsubItem item = (PubsubItem) packet;
		item.id = packet.getProperty("id");
		item.publisher = packet.getProperty("publisher");
		return item;
	}
}
