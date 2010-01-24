package org.jabber.task;

import java.util.Enumeration;

import net.sourceforge.jxa.packet.Packet;
import net.sourceforge.jxa.provider.Provider;

public class TaskProvider extends Provider {
	public TaskProvider() {
		super("task", "http://jabber.org/protocol/task", true);
	}

	protected Packet createPacket() {
		return new Task();
	}
	
	protected Packet parseComplited(Packet packet) {
		Task task = (Task) packet;
		for (Enumeration e = packet.getPackets(); e.hasMoreElements();) {
			Packet found = (Packet) e.nextElement();
			if (found.getElementName().equals("sender"))
				task.sender = found.getPayload();
			if (found.getElementName().equals("owner"))
				task.owner = found.getPayload();
			if (found.getElementName().equals("theme"))
				task.theme = found.getPayload();
			if (found.getElementName().equals("description"))
				task.description = found.getPayload();
		}
		return task;
	}
}
