package org.jabber.task;

import java.util.Enumeration;

import net.sourceforge.jxa.packet.Packet;
import net.sourceforge.jxa.provider.Provider;

public class CommentProvider extends Provider {
	public CommentProvider() {
		super("comment", "http://jabber.org/protocol/task", true);
	}

	protected Packet createPacket() {
		return new Comment();
	}
	
	protected Packet parseComplited(Packet packet) {
		Comment comment = (Comment) packet;
		for (Enumeration e = packet.getPackets(); e.hasMoreElements();) {
			Packet found = (Packet) e.nextElement();
			if (found.getElementName().equals("id"))
				comment.id = found.getPayload();
			if (found.getElementName().equals("sender"))
				comment.sender = found.getPayload();
			if (found.getElementName().equals("task"))
				comment.task = found.getPayload();
			if (found.getElementName().equals("text"))
				comment.text = found.getPayload();
		}
		return comment;
	}

}
