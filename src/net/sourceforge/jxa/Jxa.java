/*
 * Copyright 2004-2006 Swen Kummer, Dustin Hass, Sven Jost, Grzegorz Grasza
 * modified by Yuan-Chu Tai
 * http://jxa.sourceforge.net/
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version. Mobber is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details. You should have received a copy of the GNU General Public License
 * along with mobber; if not, write to the Free Software Foundation, Inc., 59
 * Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 */

package net.sourceforge.jxa;

import javax.microedition.io.*;

import net.sourceforge.jxa.packet.Bind;
import net.sourceforge.jxa.packet.Data;
import net.sourceforge.jxa.packet.DataField;
import net.sourceforge.jxa.packet.IQ;
import net.sourceforge.jxa.packet.Message;
import net.sourceforge.jxa.packet.Packet;
import net.sourceforge.jxa.packet.Presence;
import net.sourceforge.jxa.packet.Roster;
import net.sourceforge.jxa.packet.RosterItem;
import net.sourceforge.jxa.packet.pubsub.Pubsub;
import net.sourceforge.jxa.packet.pubsub.PubsubAffiliation;
import net.sourceforge.jxa.packet.pubsub.PubsubAffiliations;
import net.sourceforge.jxa.packet.pubsub.PubsubItem;
import net.sourceforge.jxa.packet.pubsub.PubsubItems;
import net.sourceforge.jxa.packet.pubsub.PubsubOwner;
import net.sourceforge.jxa.packet.pubsub.PubsubPublish;
import net.sourceforge.jxa.packet.pubsub.PubsubRetract;
import net.sourceforge.jxa.packet.pubsub.PubsubSubscription;
import net.sourceforge.jxa.provider.IQProvider;
import net.sourceforge.jxa.provider.MessageProvider;
import net.sourceforge.jxa.provider.PresenceProvider;
import net.sourceforge.jxa.provider.Provider;
import net.sourceforge.jxa.provider.RosterProvider;
import net.sourceforge.jxa.provider.pubsub.PubsubEventProvider;
import net.sourceforge.jxa.provider.pubsub.PubsubProvider;

import java.io.*;
import java.util.*;

/**
 * J2ME XMPP API Class
 * 
 * @author Swen Kummer, Dustin Hass, Sven Jost, Grzegorz Grasza
 * @version 4.0
 * @since 1.0
 */

public class Jxa extends Manager {

	final static boolean DEBUG = true;

	public final String host, port, username, password, myjid, server, pubsubServer;
	public boolean create;
	private final boolean use_ssl;
	private String resource;
	private final int priority;

	/**
	 * If you create this object all variables will be saved and the method
	 * {@link #run()} is started to log in on jabber server and listen to parse
	 * incomming xml stanzas. Use {@link #addListener(XmppListener xl)} to
	 * listen to events of this object.
	 * 
	 * @param host
	 *            the hostname/ip of the jabber server
	 * @param port
	 *            the port number of the jabber server
	 * @param username
	 *            the username of the jabber account
	 * @param password
	 *            the passwort of the jabber account
	 * @param resource
	 *            a unique identifier of the used resource, for e.g. "mobile"
	 * @param priority
	 *            the priority of the jabber session, defines on which resource
	 *            the messages arrive
	 */
	/*
	 * public Jxa(final String host, final String port, final String username,
	 * final String password, final String resource, final int priority) {
	 * this.host = host; this.port = port; this.username = username;
	 * this.password = password; this.resource = resource; this.priority =
	 * priority; this.myjid = username + "@" + host; this.early_jabber = true;
	 * this.server = host; this.start(); }
	 */
	// jid must in the form "username@host"
	// to login Google Talk, set port to 5223 (NOT 5222 in their offical guide)
	public Jxa(final String jid, final String password, final String resource,
			final int priority, final String server, final String port,
			final boolean use_ssl, final String pubsubServer, boolean create) {
		int i = jid.indexOf('@');
		this.host = jid.substring(i + 1);
		this.port = port;
		this.username = jid.substring(0, i);
		this.password = password;
		this.resource = resource;
		this.priority = priority;
		this.myjid = jid;
		if (server == null)
			this.server = host;
		else
			this.server = server;
		this.use_ssl = use_ssl;
		this.pubsubServer = pubsubServer;
		this.create = create;
		addProvider(new MessageProvider());
		addProvider(new IQProvider());
		addProvider(new PresenceProvider());
		addProvider(new RosterProvider());
		addProvider(new Provider("query", "jabber:iq:version", true));
		addProvider(new PubsubProvider());
		addProvider(new PubsubEventProvider());
	}

	/**
	 * The <code>run</code> method is called when {@link Jxa} object is created.
	 * It sets up the reader and writer, calls {@link #login()} methode and
	 * listens on the reader to parse incomming xml stanzas.
	 */
	public void run() {
		try {
			InputStream is;
			OutputStream os;
			if (!use_ssl) {
				final StreamConnection connection = (StreamConnection) Connector
						.open("socket://" + this.host + ":" + this.port);
				is = connection.openInputStream();
				os = connection.openOutputStream();
			} else {
				final SecureConnection sc = (SecureConnection) Connector.open(
						"ssl://" + this.server + ":" + this.port,
						Connector.READ_WRITE);
				// sc.setSocketOption(SocketConnection.DELAY, 1);
				// sc.setSocketOption(SocketConnection.LINGER, 0);
				is = sc.openInputStream();
				os = sc.openOutputStream();
			}
			this.reader = new XmlReader(is);
			this.writer = new XmlWriter(os);
			/*
			KXmlParser parser = new KXmlParser();
            parser.setFeature(KXmlParser.FEATURE_PROCESS_NAMESPACES, true);
            parser.setInput(new InputStreamReader(is, "UTF-8"));
            */
		} catch (final Exception e) {
			this.connectionFailed(e.toString());
			return;
		}

		java.lang.System.out.println("connected");
		/*
		 * for (Enumeration enu = listeners.elements(); enu.hasMoreElements();)
		 * { XmppListener xl = (XmppListener) enu.nextElement();
		 * xl.onDebug("connected"); }
		 */

		// connected
		try {
			this.login();
			this.parse();
			// java.lang.System.out.println("done");
		} catch (final Exception e) {
			// e.printStackTrace();
			/*
			 * for (Enumeration enu = listeners.elements();
			 * enu.hasMoreElements();) { XmppListener xl = (XmppListener)
			 * enu.nextElement(); xl.onDebug(e.getMessage()); }
			 */
			/*
			 * try { this.writer.close(); this.reader.close(); } catch (final
			 * IOException io) { io.printStackTrace(); }
			 */
			// hier entsteht der connection failed bug (Network Down)
			this.connectionFailed(e.toString());
		}
	}
	
	public Packet waitPacket(String id) {
		try {
			Packet packet;
			while (this.reader.next() == XmlReader.START_TAG) {
				packet = parse(true);
				if (packet.getProperty("id").equals(id))
					return packet;
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Opens the connection with a stream-tag, queries authentication type and
	 * sends authentication data, which is username, password and resource.
	 * 
	 * @throws java.io.IOException
	 *             is thrown if {@link XmlReader} or {@link XmlWriter} throw an
	 *             IOException.
	 */
	public void login() throws IOException {
		writer.startTag("stream:stream");
		writer.attribute("xmlns", "jabber:client");
		writer.attribute("xmlns:stream", "http://etherx.jabber.org/streams");
		writer.attribute("to", host);
		writer.attribute("version", "1.0");
		writer.flush();
		do {
			reader.next();
		} while ((reader.getType() != XmlReader.END_TAG)
				|| (!reader.getName().equals("stream:features")));

		if (create) {
			System.out.println("Create");
			
			IQ result;
			Packet query;
			
			query = new Packet("query", "jabber:iq:register");
			sendPacket(new IQ("get", null, "reg1", query));
			
			result = (IQ) waitPacket("reg1");
			query = result.getPacket("query", "jabber:iq:register");
			if (query == null) {
				query = new Packet("query", "jabber:iq:register");
				query.addPacket(new Packet("username", null));
				query.addPacket(new Packet("password", null));
			}
			query.removePacket("instructions", null);
			query.removePacket("x", "jabber:x:data");
			
			for (Enumeration e = query.getPackets(); e.hasMoreElements();) {
				Packet packet = (Packet) e.nextElement();
				if (packet.getElementName().equals("username")) {
					packet.setPayload(username);
				} else if (packet.getElementName().equals("password")) {
					packet.setPayload(password);
				} else if (packet.getElementName().equals("email")) {
					packet.setPayload(username + "@" + host);
				}
			}

			sendPacket(new IQ("set", null, "reg2", query));

			result = (IQ) waitPacket("reg2");
		}
		
		System.out.println("SASL phase1");		
		
		Packet auth = new Packet("auth", "urn:ietf:params:xml:ns:xmpp-sasl");
		auth.setProperty("mechanism", "PLAIN");
		byte[] auth_msg = (username + "@" + host + "\0" + username + "\0" + password)
				.getBytes();
		auth.setPayload(Base64.encode(auth_msg));
		sendPacket(auth);

		reader.next();
		if (reader.getName().equals("success")) {
			while (true) {
				if ((reader.getType() == XmlReader.END_TAG)
						&& reader.getName().equals("success"))
					break;
				reader.next();
			}
		} else {
			for (Enumeration e = listeners.elements(); e.hasMoreElements();) {
				XmppListener xl = (XmppListener) e.nextElement();
				xl.onAuthFailed(reader.getName()
						+ ", failed authentication");
			}
			return;
		}

		System.out.println("SASL phase2");

		writer.startTag("stream:stream");
		writer.attribute("xmlns", "jabber:client");
		writer.attribute("xmlns:stream", "http://etherx.jabber.org/streams");
		writer.attribute("to", host);
		writer.attribute("version", "1.0");
		writer.flush();
		reader.next();
		while (true) {
			if ((reader.getType() == XmlReader.END_TAG)
					&& reader.getName().equals("stream:features"))
				break;
			reader.next();
		}
		System.out.println("SASL done");

		IQ iq = new IQ("set", null, "res_binding", new Bind(resource));
		sendPacket(iq);
	}

	/**
	 * Closes the stream-tag and the {@link XmlWriter}.
	 */
	public void logoff() {
		try {
			this.writer.endTag();
			this.writer.flush();
			this.writer.close();
		} catch (final Exception e) {
			this.connectionFailed();
		}
	}

	public void sendPacket(Packet packet) {
		try {
			packet.emit(this);
			writer.flush();
		} catch (IOException e) {
			connectionFailed();
		}
	}

	/**
	 * Sends a presence stanza to a jid. This method can do various task but
	 * it's private, please use setStatus to set your status or explicit
	 * subscription methods subscribe, unsubscribe, subscribed and unsubscribed
	 * to change subscriptions.
	 */
	private void sendPresence(final String to, final String type,
			final String show, final String status, final int priority) {
		sendPacket(new Presence(to, type, show, status, priority));
	}

	/**
	 * Sets your Jabber Status.
	 * 
	 * @param show
	 *            is one of the following: <code>null</code>, chat, away, dnd,
	 *            xa, invisible
	 * @param status
	 *            an extended text describing the actual status
	 * @param priority
	 *            the priority number (5 should be default)
	 */
	public void setStatus(String show, String status, final int priority) {
		if (show.equals("")) {
			show = null;
		}
		if (status.equals("")) {
			status = null;
		}
		if (show.equals("invisible")) {
			this.sendPresence(null, "invisible", null, null, priority);
		} else {
			this.sendPresence(null, null, show, status, priority);
		}
	}

	/**
	 * Requesting a subscription.
	 * 
	 * @param to
	 *            the jid you want to subscribe
	 */
	public void subscribe(final String to) {
		this.sendPresence(to, "subscribe", null, null, 0);
	}

	/**
	 * Remove a subscription.
	 * 
	 * @param to
	 *            the jid you want to remove your subscription
	 */
	public void unsubscribe(final String to) {
		this.sendPresence(to, "unsubscribe", null, null, 0);
	}

	/**
	 * Approve a subscription request.
	 * 
	 * @param to
	 *            the jid that sent you a subscription request
	 */
	public void subscribed(final String to) {
		this.sendPresence(to, "subscribed", null, null, 0);
	}

	/**
	 * Refuse/Reject a subscription request.
	 * 
	 * @param to
	 *            the jid that sent you a subscription request
	 */
	public void unsubscribed(final String to) {
		this.sendPresence(to, "unsubscribed", null, null, 0);
	}

	/**
	 * Save a contact to roster. This means, a message is send to jabber server
	 * (which hosts your roster) to update the roster.
	 * 
	 * @param jid
	 *            the jid of the contact
	 * @param name
	 *            the nickname of the contact
	 * @param group
	 *            the group of the contact
	 * @param subscription
	 *            the subscription of the contact
	 */
	public void saveContact(final String jid, final String name,
			final Enumeration group, final String subscription) {
		try {
			this.writer.startTag("iq");
			this.writer.attribute("type", "set");
			this.writer.startTag("query");
			this.writer.attribute("xmlns", "jabber:iq:roster");
			this.writer.startTag("item");
			this.writer.attribute("jid", jid);
			if (name != null) {
				this.writer.attribute("name", name);
			}
			if (subscription != null) {
				this.writer.attribute("subscription", subscription);
			}
			if (group != null) {
				while (group.hasMoreElements()) {
					this.writer.startTag("group");
					this.writer.text((String) group.nextElement());
					this.writer.endTag(); // group
				}
			}
			this.writer.endTag(); // item
			this.writer.endTag(); // query
			this.writer.endTag(); // iq
			this.writer.flush();
		} catch (final Exception e) {
			// e.printStackTrace();
			this.connectionFailed();
		}
	}
	
	private static Random random = new Random();
	private static final String Symbols = "0123456789abcdefghijklmnopqrstuvwxyz";
	public String getRandomID() {
		StringBuffer buffer = new StringBuffer(16);
		for (int i = 0; i < 16; i ++)
			buffer.append(Symbols.charAt(random.nextInt(Symbols.length())));
		return buffer.toString(); 
	}

	/**
	 * Sends a roster query.
	 * 
	 * @throws java.io.IOException
	 *             is thrown if {@link XmlReader} or {@link XmlWriter} throw an
	 *             IOException.
	 */
	public void getRoster() {
		IQ iq = new IQ("get", null, getRandomID(), new Roster());
		sendPacket(iq);
	}
	
	/*************************************************
	 * 					PUBSUB						 *
	 *************************************************/
	public void pubsubSubscribe(String node) {
		Packet subscribe = new Packet("subscribe");
		subscribe.setProperty("node", node);
		subscribe.setProperty("jid", myjid);
		IQ iq = new IQ("set", pubsubServer, getRandomID(), new Pubsub(subscribe));
		sendPacket(iq);
	}
	
	public void pubsubUnsubscribe(String node) {
		Packet subscribe = new Packet("unsubscribe");
		subscribe.setProperty("node", node);
		subscribe.setProperty("jid", myjid);
		IQ iq = new IQ("set", pubsubServer, getRandomID(), new Pubsub(subscribe));
		sendPacket(iq);
	}
	
	public void pubsubOptions(String node, boolean include_body) {
		Data data = new Data("submit");
		data.addPacket(new DataField("FORM_TYPE", "hidden", "http://jabber.org/protocol/pubsub#subscribe_options"));
		data.addPacket(new DataField("pubsub#include_body", null, include_body ? "true" : "false"));

		Packet options = new Packet("options", null, data);
		options.setProperty("node", node);
		options.setProperty("jid", myjid);
		
		IQ iq = new IQ("set", pubsubServer, getRandomID(), new Pubsub(options));
		sendPacket(iq);
	}
	
	public void pubsubAllItems(String node, String subid) {
		Packet items = new PubsubItems(node, null);
		items.setProperty("subid", subid);
		IQ iq = new IQ("get", pubsubServer, getRandomID(), new Pubsub(items));
		sendPacket(iq);
	}
	
	public void pubsubPublish(String node, String id, Packet packet) {
		if (id == null)
			id = getRandomID();
		Packet publish = new PubsubPublish(node, new PubsubItem(id, packet));
		IQ iq = new IQ("set", pubsubServer, getRandomID(), new Pubsub(publish));
		sendPacket(iq);
	}
	
	public void pubsubRetract(String node, String id) {
		Packet retract = new PubsubRetract(node, new PubsubItem(id, null));
		IQ iq = new IQ("set", pubsubServer, getRandomID(), new Pubsub(retract));
		sendPacket(iq);
	}
	
	public void pubsubAllSubscriptions(String node) {
		Packet packet = new Packet("subscriptions", null);
		packet.setProperty("node", node);
		IQ iq = new IQ("get", pubsubServer, getRandomID(), new Pubsub(packet));
		sendPacket(iq);
	}
	
	public void pubsubSetSubscription(String node, String jid, String subscription) {
		Packet packet = new Packet("subscriptions", null);
		packet.setProperty("node", node);
		packet.addPacket(new PubsubSubscription(jid, subscription));
		IQ iq = new IQ("set", pubsubServer, getRandomID(), new Pubsub(packet));
		sendPacket(iq);
	}
	
	public void pubsubAllAffiliations(String node) {
		IQ iq = new IQ("get", pubsubServer, getRandomID(), new Pubsub(new PubsubAffiliations(node, null)));
		sendPacket(iq);
	}
	
	public void pubsubSetAffiliation(String node, String jid, String affiliation) {
		IQ iq = new IQ("set", pubsubServer, getRandomID(), new PubsubOwner(new PubsubAffiliations(node, new PubsubAffiliation(jid, affiliation))));
		sendPacket(iq);
	}
	
	public void pubsubCreateNode(String node) {
		Packet create = new Packet("create", null);
		create.setProperty("node", node);
		
		Data data = new Data("submit");
		data.addPacket(new DataField("FORM_TYPE", "hidden", "http://jabber.org/protocol/pubsub#node_config"));
		data.addPacket(new DataField("pubsub#access_model", null, "open"));
		data.addPacket(new DataField("pubsub#publish_model", null, "open"));
		data.addPacket(new DataField("pubsub#max_payload_size", null, "65536"));
		data.addPacket(new DataField("pubsub#persist_items", null, "1"));
		data.addPacket(new DataField("pubsub#deliver_payloads", null, "1"));
		data.addPacket(new DataField("pubsub#deliver_notifications", null, "1"));
		data.addPacket(new DataField("pubsub#max_items", null, "1000"));
		
		Pubsub pubsub = new Pubsub();
		pubsub.addPacket(create);
		pubsub.addPacket(new Packet("configure", null, data));

		IQ iq = new IQ("set", pubsubServer, getRandomID(), pubsub);
		sendPacket(iq);
	}

	/**
	 * The main parse methode is parsing all types of XML stanzas
	 * <code>message</code>, <code>presence</code> and <code>iq</code>. Although
	 * ignores any other type of xml.
	 * 
	 * @throws java.io.IOException
	 *             is thrown if {@link XmlReader} or {@link XmlWriter} throw an
	 *             IOException.
	 */
	private void parse() throws IOException {
		while (this.reader.next() == XmlReader.START_TAG)
			parse(true);
		this.reader.close();
	}

	/**
	 * This method is used to be called on a parser or a connection error. It
	 * tries to close the XML-Reader and XML-Writer one last time.
	 * 
	 */
	private void connectionFailed() {
		this.writer.close();
		this.reader.close();

		for (Enumeration e = listeners.elements(); e.hasMoreElements();) {
			XmppListener xl = (XmppListener) e.nextElement();
			xl.onConnFailed("");
		}
	}

	private void connectionFailed(final String msg) {
		try {
			this.writer.close();
		} catch (Exception e) {
		}
		try {
			this.reader.close();
		} catch (Exception e) {
		}

		for (Enumeration e = listeners.elements(); e.hasMoreElements();) {
			XmppListener xl = (XmppListener) e.nextElement();
			xl.onConnFailed(msg);
		}
	}

	public void event(Packet packet) {
		System.out.println("JXA event: " + packet);
		for (Enumeration e = listeners.elements(); e.hasMoreElements();) {
			XmppListener xl = (XmppListener) e.nextElement();
			if (packet.equals("message", null)) {
				Message message = (Message) packet;
				if (message.body != null) {
					int index = message.from.indexOf('/');
					xl.onMessageEvent((index == -1) ? message.from : 
						message.from.substring(0, index), message.body);
					continue;
				}
			} else if (packet.equals("iq", null)) {
				IQ iq = (IQ) packet;
				System.out.println("IQ: " + iq.type + " from " + iq.from + " to " + iq.to + " : " + iq.id);
				for (Enumeration packets = iq.getPackets(); packets.hasMoreElements();) {
					System.out.println("With: " + packets.nextElement());
				}
				
			} else if (packet.equals("error", null)) {
				System.out.println("Error: " + packet.getProperty("type"));
			} else if (packet.equals("bind", "urn:ietf:params:xml:ns:xmpp-bind")) {
				Bind bind = (Bind) packet;
				xl.onAuth(bind.getResource());
			} else if (packet.equals("query", "jabber:iq:roster")) {
				Roster roster = (Roster) packet;
				for (Enumeration items = roster.getItems(); items.hasMoreElements();) {
					RosterItem item = (RosterItem) items.nextElement();
					String jid = item.getProperty("jid");
					String name = item.getProperty("name");
					String subscription = item.getProperty("subscription");
					for (Enumeration groups = item.getGroups(); groups.hasMoreElements();) {
						String group = (String) groups.nextElement();
			 			xl.onContactEvent(jid, name, group, subscription);
					}
				}
			} else if (packet.equals("presence", null)) {
				Presence presence = (Presence) packet;
				if (presence.type == null) {
					xl.onStatusEvent(presence.from, presence.show, presence.status);
				} else if (presence.type.equals("unsubscribe") || presence.type.equals("error")) {
					xl.onUnsubscribeEvent(presence.from);
				} else if (presence.type.equals("subscribe")) {
					xl.onSubscribeEvent(presence.from);
				} else if (presence.type.equals("unavailable")) {
					xl.onStatusEvent(presence.from, "na", presence.status);
				}
			} else {
				xl.onEvent(packet);
			}
		}
		if (packet.equals("bind", "urn:ietf:params:xml:ns:xmpp-bind")) {
			sendPresence(null, null, null, null, this.priority);
		} else if (packet.equals("query", "jabber:iq:version")) {
			Packet query = new Packet("query", "jabber:iq:version");
			query.addPacket(new Packet("name", null, "mja"));
			query.addPacket(new Packet("version", null, "1.0"));
			query.addPacket(new Packet("os", null, "J2ME"));
			
			IQ iq = new IQ("result", packet.getProperty("from"), packet.getProperty("id"), query);
			sendPacket(iq);
		}
	}
}
