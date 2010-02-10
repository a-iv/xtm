package org.jabber.task;

import java.io.IOException;

import net.sourceforge.jxa.Manager;
import net.sourceforge.jxa.packet.Packet;

public class Comment extends Packet {
	public String task = new String();
	public String text = new String();
	public String sender = new String();
	public String id = new String();

	public Comment() {
		super("comment", "http://jabber.org/protocol/task");
	}

	public Comment(String task, String text, String sender, String id) {
		this();
		this.task = task;
		this.text = text;
		this.sender = sender;
		this.id = id;
	}

	public void emit(Manager manager) throws IOException {
		clearPackets();
		if (task != null)
			addPacket(new Packet("task", null, task));
		if (text != null)
			addPacket(new Packet("text", null, text));
		if (sender != null)
			addPacket(new Packet("sender", null, sender));
		super.emit(manager);
	}
}