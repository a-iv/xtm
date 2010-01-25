package net.sourceforge.jxa.packet.pubsub;

import net.sourceforge.jxa.packet.Packet;

public class PubsubNode extends Packet {
	public String node;
	public String jid;

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
