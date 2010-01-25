package net.sourceforge.jxa.packet;

import java.io.IOException;

import net.sourceforge.jxa.Manager;

public class IQ extends Packet {
	public String type;
	public String from;
	public String to;
	public String id;

	public IQ() {
		super("iq", null);
	}

	public IQ(String type, String to, String id, Packet inner) {
		this();
		this.type = type;
		this.to = to;
		this.id = id;
		addPacket(inner);
	}

	public void emit(Manager manager) throws IOException {
		setProperty("type", type);
		setProperty("from", from);
		setProperty("to", to);
		setProperty("id", id);
		super.emit(manager);
	}
}
