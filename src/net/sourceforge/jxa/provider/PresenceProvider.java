package net.sourceforge.jxa.provider;

import java.util.Enumeration;

import net.sourceforge.jxa.packet.Packet;
import net.sourceforge.jxa.packet.Presence;

public class PresenceProvider extends Provider {
	public PresenceProvider() {
		super("presence", null, true);
	}

	protected Packet createPacket() {
		return new Presence();
	}
	
	protected Packet parseComplited(Packet packet) {
		Presence presence = (Presence) packet;
		presence.from = packet.getProperty("from");
		presence.type = packet.getProperty("type");
		for (Enumeration e = packet.getPackets(); e.hasMoreElements();) {
			Packet found = (Packet) e.nextElement();
			System.out.println(found.getElementName());
			if (found.equals("show", null)) 
				presence.show = found.getPayload();
			if (found.equals("status", null))
				presence.status = found.getPayload();
		}
		return presence;
	}
}
