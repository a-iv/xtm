package net.sourceforge.jxa.packet.pubsub;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

import net.sourceforge.jxa.Manager;
import net.sourceforge.jxa.packet.Packet;

public class PubsubContainer extends Packet {
	public String node;
	public String jid;

	public PubsubContainer() {
		super();
	}

	public PubsubContainer(String elementName, String namespace) {
		super(elementName, namespace);
	}

	public PubsubContainer(String elementName, String namespace, String node, String jid) {
		this(elementName, namespace);
		this.node = node;
		this.jid = jid;
	}

	public void emit(Manager manager) throws IOException {
		setProperty("node", node);
		setProperty("jid", jid);
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
