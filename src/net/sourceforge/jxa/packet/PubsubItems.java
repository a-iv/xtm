package net.sourceforge.jxa.packet;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

import net.sourceforge.jxa.Manager;

public class PubsubItems extends Packet {
	public String node;
	
	public PubsubItems() {
		super("items", null);
	}

	public PubsubItems(String node) {
		this();
		this.node = node;
	}

	public void emit(Manager manager) throws IOException {
		setProperty("node", node);
		super.emit(manager);
	}
	
	/**
	 * Gets list of items
	 * 
	 * @return list of Packet object
	 */
	public Enumeration getItems() {
		Vector result = new Vector();
		for (Enumeration e = getPackets(); e.hasMoreElements();) {
			Packet found = (Packet) e.nextElement();
			if (found.equals("item", null))
				result.addElement(found);
		}
		return result.elements();
	}
}
