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
		this(new String(), null, null);
	}
	
	public Packet(String elementName) {
		this(elementName, null, null);
	}
	
	public Packet(String elementName, String namespace) {
		this(elementName, namespace, null);
	}
	
	public Packet(String elementName, String namespace, String payload) {
		this.elementName = elementName;
		this.payload = payload;
		properties = new Hashtable();
		packets = new Vector();
		setNamespace(namespace);
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
	 * @param packet to be added
	 */
	public void removePacket(Packet packet) {
		packets.removeElement(packet);
	}
	
	/**
	 * Receive this packet
	 * 
	 * @param manager
	 * @throws IOException
	 */
	public void receive(Manager manager) throws IOException {
		System.out.println("Start " + getElementName());
		manager.writer.startTag(getElementName());
		System.out.println("Properties");
		for (Enumeration e = getProperties(); e.hasMoreElements();) {
			String name = (String) e.nextElement();
			manager.writer.attribute(name, getProperty(name));
		}
		System.out.println("Payload");
		if (payload != null) {
			manager.writer.text(payload);
		}
		System.out.println("Inner");
		for (Enumeration e = getPackets(); e.hasMoreElements();) {
			Packet packet = (Packet) e.nextElement();
			packet.receive(manager);
		}
		System.out.println("End");
		manager.writer.endTag();
	}
}
