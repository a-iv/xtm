package net.sourceforge.jxa.packet;

import java.io.IOException;

import net.sourceforge.jxa.Manager;

public class DataField extends Packet {
	public String var;
	public String type;
	
	public DataField() {
		super("field", null);
	}
	
	public DataField(String var, String type, Packet inner) {
		this();
		this.var = var;
		this.type = type;
		addPacket(inner);
	}

	public DataField(String var, String type, String value) {
		this(var, type, new Packet("value", null, value));
	}

	public void emit(Manager manager) throws IOException {
		setProperty("var", var);
		setProperty("type", type);
		super.emit(manager);
	}
}
