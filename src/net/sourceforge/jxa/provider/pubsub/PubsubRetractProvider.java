package net.sourceforge.jxa.provider.pubsub;

import net.sourceforge.jxa.packet.Packet;
import net.sourceforge.jxa.packet.pubsub.PubsubItem;
import net.sourceforge.jxa.packet.pubsub.PubsubRetract;
import net.sourceforge.jxa.provider.Provider;

public class PubsubRetractProvider extends Provider {
	public PubsubRetractProvider() {
		super(PubsubRetract.ELEMENT_NAME, PubsubRetract.NAMESPACE, true);
	}

	protected Packet createPacket() {
		return new PubsubRetract();
	}
	
	protected Packet parseComplited(Packet packet) {
		PubsubRetract retract = (PubsubRetract) packet;
		retract.id = packet.getProperty("id");
		return retract;
	}
}
