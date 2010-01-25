package net.sourceforge.jxa.provider.pubsub;

import net.sourceforge.jxa.packet.Packet;
import net.sourceforge.jxa.packet.pubsub.PubsubDelete;
import net.sourceforge.jxa.provider.Provider;

public class PubsubDeleteProvider extends Provider {
	public PubsubDeleteProvider() {
		super(PubsubDelete.ELEMENT_NAME, PubsubDelete.NAMESPACE, true);
	}

	protected Packet createPacket() {
		return new PubsubDelete();
	}
	
	protected Packet parseComplited(Packet packet) {
		PubsubDelete delete = (PubsubDelete) packet;
		delete.node = packet.getProperty("node");
		return delete;
	}
}
