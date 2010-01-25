package net.sourceforge.jxa.provider;

import java.io.IOException;
import java.util.Vector;

import net.sourceforge.jxa.Manager;
import net.sourceforge.jxa.packet.Bind;
import net.sourceforge.jxa.packet.Packet;

public class BindProvider extends Provider {
	private static final Provider jidProvider = new Provider("jid", null, false);
	
	public BindProvider() {
		super(Bind.ELEMENT_NAME, Bind.NAMESPACE, true);
	}
	
	protected Packet createPacket() {
		return new Bind();
	}
	
	protected Packet parseInner(Manager manager, Packet packet) throws IOException {
		Vector providers = new Vector();
		providers.addElement(jidProvider);
		return manager.parse(providers.elements(), true);
	}
}
