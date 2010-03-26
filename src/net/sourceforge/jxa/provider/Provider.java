package net.sourceforge.jxa.provider;

import java.io.IOException;
import java.util.Enumeration;

import net.sourceforge.jxa.Manager;
import net.sourceforge.jxa.XmlReader;
import net.sourceforge.jxa.packet.Packet;

public class Provider {
	private String elementName;
	private String namespace;
	private boolean makeEvent;

	public Provider(String elementName, String namespace) {
		this(elementName, namespace, false);
	}
	
	public Provider(String elementName, String namespace, boolean makeEvent) {
		this.elementName = elementName;
		this.namespace = namespace;
		this.makeEvent = makeEvent;
	}
	
	/**
	 * Validate whether elementName and namespace correspond to
	 * this type of packet.
	 * 
	 * @param elementName
	 * @param namespace
	 * @return True if corresponds
	 */
	public boolean equals(String elementName, String namespace) {
		return ((this.elementName == null || 
						this.elementName.equals(elementName)) && 
				(this.namespace == null || 
						this.namespace.equals(namespace)));
	}
	
	/**
	 * Validate whether packet correspond to this type of packet.
	 * 
	 * @param packet
	 * @return True if corresponds
	 */
	public boolean equals(Packet packet) {
		return equals(packet.getElementName(), packet.getNamespace());
	}
	
	/**
	 * Creates Packet or inherited object. 
	 * 
	 * @return
	 */
	protected Packet createPacket() {
		return new Packet();
	}
	
	/**
	 * Parse inner packets using manager:
	 * <ul>
	 * <li>Use registered in manager providers
	 * <li>Or fall back to defaultProvider
	 * </ul>
	 * You may override this function in subclasses.
	 * 
	 * @param jxa
	 * @param packet - container for inner packets
	 * @return
	 * 			new inner packet or null
	 * @throws IOException
	 */
	protected Packet parseInner(Manager manager, Packet packet) throws IOException {
		return manager.parse(false);
	}
	
	/**
	 * Wrapper called when <code>parse</code> function was completed.
	 * You may override this function in subclasses.
	 * 
	 * @param packet
	 * @return
	 * 			Modified packet
	 */
	protected Packet parseComplited(Packet packet) {
		return packet;
	}
	
	public Packet parse(Manager manager) throws IOException {
		Packet packet = createPacket();
		packet.setElementName(manager.reader.getName());
		for (Enumeration e = manager.reader.getAttributes(); e.hasMoreElements();) {
			String name = (String) e.nextElement();
			packet.setProperty(name, manager.reader.getAttribute(name));
		}
		StringBuffer buffer = new StringBuffer("");
		while (true) {
			int type = manager.reader.next();
			if (type == XmlReader.END_TAG || type == XmlReader.END_DOCUMENT)
				break;
			else if (type == XmlReader.TEXT)
				buffer.append(manager.reader.getText());
			else if (type == XmlReader.START_TAG)
				packet.addPacket(parseInner(manager, packet));
		}
		packet.setPayload(buffer.toString());
		packet = parseComplited(packet);
		if (makeEvent)
			manager.event(packet);
		packet.log(false);
		return packet;
	}
}
