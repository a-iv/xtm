package net.sourceforge.jxa.packet;

import java.util.Enumeration;

public class Bind extends Packet {
	public Bind() {
		super("bind", "urn:ietf:params:xml:ns:xmpp-bind");
	}
	
	/**
	 * Gets full JID value
	 * 
	 * @return
	 */
	public String getJid() {
		for (Enumeration e = getPackets(); e.hasMoreElements();) {
			Packet found = (Packet) e.nextElement();
			if (found.equals("jid", null))
				return found.getPayload();
		}
		return null;
	}
	
	/**
	 * Gets JID`s resource
	 * 
	 * @return
	 */
	public String getResource() {
		String jid = getJid();
		return jid.substring(jid.indexOf('/') + 1);
	}
}
