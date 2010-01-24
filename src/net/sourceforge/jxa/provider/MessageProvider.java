package net.sourceforge.jxa.provider;

import java.util.Enumeration;

import net.sourceforge.jxa.packet.Message;
import net.sourceforge.jxa.packet.Packet;

public class MessageProvider extends Provider {
	public MessageProvider() {
		super("message", null, true);
	}

	protected Packet createPacket() {
		return new Message();
	}
	
	protected Packet parseComplited(Packet packet) {
		Message message = (Message) packet;
		message.from = packet.getProperty("from");
		for (Enumeration e = packet.getPackets(); e.hasMoreElements();) {
			Packet found = (Packet) e.nextElement();
			if (found.getElementName().equals("body")) 
				message.body = found.getPayload();
			if (found.getElementName().equals("subject"))
				message.subject = found.getPayload();
		}
		return message;
	}
}
