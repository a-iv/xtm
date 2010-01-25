package net.sourceforge.jxa.packet;

import java.io.IOException;

import net.sourceforge.jxa.Manager;

public class Data extends Packet {
	public String type;
	
	public Data() {
		super("x", "jabber:x:data");
	}
	
	public Data(String type) {
		this();
		this.type = type;
	}

	public void emit(Manager manager) throws IOException {
		setProperty("type", type);
		super.emit(manager);
	}

}
