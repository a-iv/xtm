package net.sourceforge.jxa.provider.pubsub;

import net.sourceforge.jxa.packet.Packet;
import net.sourceforge.jxa.packet.pubsub.PubsubPublish;
import net.sourceforge.jxa.provider.Provider;

public class PubsubPublishProvider extends Provider {
	//private static final Provider itemProvider = new PubsubItemProvider();

	public PubsubPublishProvider() {
		super(PubsubPublish.ELEMENT_NAME, PubsubPublish.NAMESPACE, true);
	}

	protected Packet createPacket() {
		return new PubsubPublish();
	}
	
	protected Packet parseComplited(Packet packet) {
		PubsubPublish publish = (PubsubPublish) packet;
		publish.node = packet.getProperty("node");
		return publish;
	}

//	protected Packet parseInner(Manager manager, Packet packet) throws IOException {
//		Vector providers = new Vector();
//		providers.addElement(itemProvider);
//		return manager.parse(providers.elements(), true);
//	}
}
