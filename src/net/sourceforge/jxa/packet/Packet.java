package net.sourceforge.jxa.packet;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import net.sourceforge.jxa.Manager;

public class Packet {
	private String elementName;
	private String payload;
	private Hashtable properties;
	private Vector packets;
	
	public Packet() {
		this(new String(), null);
	}
	
	public Packet(String elementName) {
		this(elementName, null);
	}
	
	public Packet(String elementName, String namespace) {
		this.elementName = elementName;
		payload = null;
		properties = new Hashtable();
		packets = new Vector();
		setNamespace(namespace);
	}
	
	public Packet(String elementName, String namespace, String payload) {
		this(elementName, namespace);
		this.payload = payload;
	}
	
	public Packet(String elementName, String namespace, Packet inner) {
		this(elementName, namespace);
		addPacket(inner);
	}
	
	/**
	 * Validate whether elementName and namespace correspond to this packet.
	 * 
	 * @param elementName
	 * @param namespace
	 * @return True if corresponds
	 */
	public boolean equals(String elementName, String namespace) {
		if (elementName == null && getElementName() != null)
			return false;
		if (elementName != null && !elementName.equals(getElementName()))
			return false;
		if (namespace == null && getNamespace() != null)
			return false;
		if (namespace != null && !namespace.equals(getNamespace()))
			return false;
		return true;
	}
	
	/**
	 * Gets element name
	 * 
	 * @return
	 */
	public String getElementName() {
		return elementName;
	}
	
	/**
	 * Gets text payload
	 * 
	 * @return
	 */
	public String getPayload() {
		return payload;
	}
	
	/**
	 * Gets xml namespace
	 * 
	 * @return String or null
	 */
	public String getNamespace() {
		return getProperty("xmlns");
	}
	
	/**
	 * Gets properties
	 * 
	 * @return Enumeration of String names
	 */
	public Enumeration getProperties() {
		return properties.keys();
	}
	
	/**
	 * Gets property by name
	 * 
	 * @param name
	 * 
	 * @return value
	 */
	public String getProperty(String name) {
		for (Enumeration e = getProperties(); e.hasMoreElements();) {
			Object key = e.nextElement();
			if (name.equals((String) key))
				return (String) properties.get(key);
		}
		return null;
	}
	
	/**
	 * Gets inner packets
	 * 
	 * @return Enumeration of Packet objects
	 */
	public Enumeration getPackets() {
		return packets.elements();
	}
	
	/**
	 * Gets number of inner packets
	 * 
	 * @return
	 */
	public int getPacketCount() {
		return packets.size();
	}
	
	/**
	 * Sets element name
	 * 
	 * @param elementName
	 */
	public void setElementName(String elementName) {
		this.elementName = elementName;
	}
	
	/**
	 * Sets payload
	 * 
	 * @param payloadName
	 */
	public void setPayload(String payload) {
		this.payload = payload;
	}
	
	/**
	 * Sets xml namespace
	 * 
	 * @param payloadName
	 */
	public void setNamespace(String namespace) {
		setProperty("xmlns", namespace);
	}
	
	/**
	 * Removes property by name
	 * 
	 * @param name
	 */
	public void removeProperty(String name) {
		for (Enumeration e = getProperties(); e.hasMoreElements();) {
			Object key = e.nextElement();
			if (name.equals((String) key))
				properties.remove(key);
		}
	}
	
	/**
	 * Sets property
	 * 
	 * @param name
	 * @param value
	 */
	public void setProperty(String name, String value) {
		if (value == null)
			removeProperty(name);
		else
			properties.put(name, value);
	}
	
	/**
	 * Add inner packet
	 * 
	 * @param packet to be added
	 */
	public void addPacket(Packet packet) {
		packets.addElement(packet);
	}
	
	/**
	 * Remove inner packet
	 * 
	 * @param elementName
	 * @param namespace
	 */
	public void removePacket(String elementName, String namespace) {
		for (int index = 0; index < packets.size(); index++) {
			Packet found = (Packet) packets.elementAt(index);
			if (found.equals(elementName, namespace)) {
				packets.removeElementAt(index);
				index--;
			}
		}
	}

	/**
	 * Remove inner packet
	 * 
	 * @param packet to be added
	 */
	public void clearPackets() {
		packets.removeAllElements();
	}
	
	/**
	 * Publish this packet to manager`s writer
	 * 
	 * @param manager
	 * @throws IOException
	 */
	public void emit(Manager manager) throws IOException {
		manager.writer.startTag(getElementName());
		for (Enumeration e = getProperties(); e.hasMoreElements();) {
			String name = (String) e.nextElement();
			manager.writer.attribute(name, getProperty(name));
		}
		if (payload != null) {
			manager.writer.text(payload);
		}
		for (Enumeration e = getPackets(); e.hasMoreElements();) {
			Packet packet = (Packet) e.nextElement();
			packet.emit(manager);
		}
		manager.writer.endTag();
	}
}
