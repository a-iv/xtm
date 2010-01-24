package net.sourceforge.jxa.packet;

import java.io.IOException;

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
		setProperty("to", to);
		if (body != null) {
			removePacket("body", null);
			addPacket(new Packet("body", null, body));
		}
		if (subject != null) {
			removePacket("subect", null);
			addPacket(new Packet("subect", null, subject));
		}
		super.emit(manager);
	}
}
