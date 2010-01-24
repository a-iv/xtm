package net.sourceforge.jxa.provider;

import java.io.IOException;
import java.util.Vector;

import net.sourceforge.jxa.Manager;
import net.sourceforge.jxa.packet.Packet;
import net.sourceforge.jxa.packet.RosterItem;

public class RosterItemProvider extends Provider {
	private static final Provider groupProvider = new Provider("group", null, false);
	
	public RosterItemProvider() {
		super("item", null, false);
	}

	protected Packet createPacket() {
		return new RosterItem();
	}
	
	protected Packet parseInner(Manager manager, Packet packet) throws IOException {
		Vector providers = new Vector();
		providers.addElement(groupProvider);
		return manager.parse(providers.elements(), true);
	}
}
