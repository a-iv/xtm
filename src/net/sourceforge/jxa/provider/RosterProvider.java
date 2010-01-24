package net.sourceforge.jxa.provider;

import java.io.IOException;
import java.util.Vector;

import net.sourceforge.jxa.Manager;
import net.sourceforge.jxa.packet.Packet;
import net.sourceforge.jxa.packet.Roster;

public class RosterProvider extends Provider {
	private static final Provider rosterItemProvider = new RosterItemProvider();
	
	public RosterProvider() {
		super("query", "jabber:iq:roster", true);
	}
	
	protected Packet createPacket() {
		return new Roster();
	}
	
	protected Packet parseInner(Manager manager, Packet packet) throws IOException {
		Vector providers = new Vector();
		providers.addElement(rosterItemProvider);
		return manager.parse(providers.elements(), true);
	}
}
