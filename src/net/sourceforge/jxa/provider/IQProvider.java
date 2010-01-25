package net.sourceforge.jxa.provider;

import java.io.IOException;
import java.util.Vector;

import net.sourceforge.jxa.Manager;
import net.sourceforge.jxa.packet.IQ;
import net.sourceforge.jxa.packet.Packet;

public class IQProvider extends Provider {
	private static final Provider errorProvider = new Provider("error", null, true);
	private static final Provider bindProvider = new BindProvider();
	
	public IQProvider() {
		super(IQ.ELEMENT_NAME, IQ.NAMESPACE, false);
	}

	protected Packet createPacket() {
		return new IQ();
	}
	
	protected Packet parseInner(Manager manager, Packet packet) throws IOException {
		String type = packet.getProperty("type");
		String id = packet.getProperty("id");
		if (type.equals("error")) {
			Vector providers = new Vector();
			providers.addElement(errorProvider);
			return manager.parse(providers.elements(), true);
		} else if (type.equals("result") && id != null && id.equals("res_binding")) {
			// TODO: fix id check
			Vector providers = new Vector();
			providers.addElement(bindProvider);
			return manager.parse(providers.elements(), true);
		}
		return manager.parse(false);
	}
	
	protected Packet parseComplited(Packet packet) {
		IQ iq = (IQ) packet;
		iq.type = packet.getProperty("type");
		iq.from = packet.getProperty("from");
		iq.to = packet.getProperty("to");
		iq.id = packet.getProperty("id");
		return iq;
	}
}
