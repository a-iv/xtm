package net.sourceforge.jxa.packet;

import java.io.IOException;
import java.util.Enumeration;

import net.sourceforge.jxa.Manager;

public class Message extends Packet {
	public String from;
	public String to;
	public String body;
	public String subject;
	
	public Message() {
		super("message", null);
	}

	public void emit(Manager manager) throws IOException {
		System.out.println("Cleaning: " + getPacketCount());
		for (Enumeration e = getPackets(); e.hasMoreElements();) {
			Packet found = (Packet) e.nextElement();
			System.out.println(found.getElementName());
			if (found.getElementName().equals("body") && found.getNamespace() == null) {
				System.out.println("Remove it");
				removePacket(found);
			}
		}
		System.out.println("Result: " + getPacketCount());
		if (body != null)
			addPacket(new Packet("body", null, body));
		if (subject != null)
			addPacket(new Packet("subect", null, subject));
		if (to != null)
			setProperty("to", to);
		super.emit(manager);
	}
}
