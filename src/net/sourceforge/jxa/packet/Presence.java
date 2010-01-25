package net.sourceforge.jxa.packet;

import java.io.IOException;

import net.sourceforge.jxa.Manager;

public class Presence extends Packet {
	public String from;
	public String to;
	public String type;
	public String show = new String("");
	public String status = new String("");
	public int priority;
	
	public static final String ELEMENT_NAME = "presence";
	public static final String NAMESPACE = null;
	
	public Presence() {
		super(ELEMENT_NAME, NAMESPACE);
	}
	
	public Presence(String to, String type, String show, String status, int priority) {
		this();
		this.to = to;
		this.type = type;
		this.show = show;
		this.status = status;
		this.priority = priority;
	}

	public void emit(Manager manager) throws IOException {
		setProperty("to", to);
		setProperty("type", type);
		if (show != null) {
			removePacket("show", null);
			addPacket(new Packet("show", null, show));
		}
		if (status != null) {
			removePacket("status", null);
			addPacket(new Packet("status", null, status));
		}
		if (priority != 0) {
			removePacket("priority", null);
			addPacket(new Packet("priority", null, String.valueOf(priority)));
		}
		super.emit(manager);
	}
}
