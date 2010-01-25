package net.sourceforge.jxa.packet;

import java.io.IOException;

import net.sourceforge.jxa.Manager;

public class DataField extends Packet {
	public String var;
	public String type;
	
	public static final String ELEMENT_NAME = "field";
	public static final String NAMESPACE = null;
	
	public DataField() {
		super(ELEMENT_NAME, NAMESPACE);
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
