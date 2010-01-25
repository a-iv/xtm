package net.sourceforge.jxa.client;

/*
 * Copyright 2008 Yuan-Chu Tai
 * http://sourceforge.net/projects/jxa
 *
 * This file is part of JXAC. JXAC is free software, developed at university.
 * You can redistribute it and/or modify it under the terms of the GNU General 
 * Public License as published by the Free Software Foundation; 
 * either version 2 of the License, or (at your option) any later version.
 * JXAC is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
 * PURPOSE.  See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with mobber;
 * if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307 USA .
 */

import net.sourceforge.jxa.*;
import net.sourceforge.jxa.packet.Message;
import net.sourceforge.jxa.packet.Packet;

import java.io.*;
import java.util.Vector;

import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;

import org.jabber.task.Task;
import org.jabber.task.TaskProvider;

public class jxac extends MIDlet implements CommandListener, XmppListener {
	private Form login_form;
	private Form subscribe_form;
	private Form update_form;
	private List contacts_list;
	private Alert msg_alert;
	private TextBox send_box;
	private TextField id_field;
	private TextField passwd_field;
	private TextField server_field;
	private TextField subscribe_field;
	private TextField nick_field;
	private TextField group_field;
	private Command exit_cmd;
	private Command login_cmd;
	private Command send_cmd;
	private Command back_cmd;
	private Command contact_cmd;
	private Command subscribe_cmd;
	private Command unsubscribe_cmd;
	private Command rename_cmd;
	private Command update_cmd;
	private Command create_cmd;
	private Command list_cmd;
	private Image offline_img;
	private Image online_img;
	private String whom;
	private Jxa jxa;
	private Vector jid_list;
	private TaskProvider taskProvider = new TaskProvider(); 

	private Image loadImage(String name) {
		Image image = null;
		try {
			image = Image.createImage(name);
		} catch (IOException ioe) {
			System.out.println(ioe);
		}
		return image;
	}

	public void onConnFailed(String msg) {
		System.out.println("Connection failed");
	}

	public void onAuth(final String resource) {
		jxa.getRoster();
		contacts_list.setTitle("contacts");
	}

	public void onAuthFailed(final String message) {
		System.out.println("*debug* Auth Failed");
	}

	public void onMessageEvent(final String from, final String body) {
		msg_alert = new Alert("from " + from, body, null, AlertType.INFO);
		msg_alert.setTimeout(Alert.FOREVER);
		Display.getDisplay(this).setCurrent(msg_alert);
		System.out.println("*debug* message, " + from + ":" + body);
	}
	
	private int jidIndex(String jid) {
		for (int i = 0; i < jid_list.size(); i++)
			if (jid.equals((String) jid_list.elementAt(i)))
				return i;
		return -1;
	}

	public void onContactEvent(final String jid, final String name,
			final String group, final String subscription) {
		if (subscription.equals("both")) {
			int index = jidIndex(jid);

			String showName;
			if (name != null)
				showName = name;
			else
				showName = jid;
			if (group != null)
				showName = showName + " (" + group + ")";
			if (index == -1) {
				contacts_list.append(showName, offline_img);
				jid_list.addElement(jid);
			} else {
				Image image = contacts_list.getImage(index);
				contacts_list.set(index, showName, image);
			}
		}
	}

	public void onStatusEvent(final String jid, final String show,
			final String status) {
		System.out.println(":" + jid + " " + show + " " + status);
		int i = jid.indexOf('/');
		String bare_jid = jid.substring(0, i);
		int index = jidIndex(bare_jid);
		if (index != -1) {
			String name = contacts_list.getString(index);
			if (show.equals("na"))
				contacts_list.set(index, name, offline_img);
			else
				contacts_list.set(index, name, online_img);
		}
	}

	public void onSubscribeEvent(final String jid) {
		System.out.println("Subscribe from " + jid);
		jxa.subscribe(jid);
		jxa.saveContact(jid, null, null, null);
	}

	public void onUnsubscribeEvent(final String jid) {
		System.out.println("Unsubscribe from " + jid);
		jxa.unsubscribe(jid);
	}

	public void commandAction(final Command cmd, final Displayable displayable) {
		if (cmd == login_cmd) {
			String id = id_field.getString();
			String passwd = passwd_field.getString();
			String server = server_field.getString();
			Display.getDisplay(this).setCurrent(contacts_list);
			jxa = new Jxa(id, passwd, "jxac", 10, server, "5222", false);
			jxa.addListener(this);
			jxa.addProvider(taskProvider);
			jxa.start();
		} else if (cmd == back_cmd) {
			Display.getDisplay(this).setCurrent(contacts_list);
		} else if (cmd == send_cmd) {
			Message message = new Message();
			message.to = whom;
			Task task = new Task();
			task.description = send_box.getString();
			message.addPacket(task);
			jxa.sendPacket(message);
			Display.getDisplay(this).setCurrent(contacts_list);
		} else if (cmd == contact_cmd) {
			Display.getDisplay(this).setCurrent(subscribe_form);
		} else if (cmd == rename_cmd) {
			whom = (String) jid_list.elementAt(contacts_list
					.getSelectedIndex());
			String name = contacts_list.getString(contacts_list.
					getSelectedIndex());
			update_form.setTitle("update " + name);
			Display.getDisplay(this).setCurrent(update_form);
		} else if (cmd == update_cmd) {
			String nick = nick_field.getString();
			String group = group_field.getString();
			Vector groupList = new Vector();
			groupList.addElement(group);
			jxa.saveContact(whom, nick, groupList.elements(), null);
			jxa.getRoster();
			Display.getDisplay(this).setCurrent(contacts_list);
		} else if (cmd == subscribe_cmd) {
			jxa.subscribe(subscribe_field.getString());
			Display.getDisplay(this).setCurrent(contacts_list);
		} else if (cmd == unsubscribe_cmd) {
			jxa.unsubscribe(subscribe_field.getString());
			Display.getDisplay(this).setCurrent(contacts_list);
		} else if (cmd == create_cmd) {
			//jxa.createNode("gpsgeotrace.com", "jxa");
		} else if (cmd == list_cmd) {
			//jxa.getItems("gpsgeotrace.com", "jxa");
		} else if (cmd == List.SELECT_COMMAND) {
			whom = (String) jid_list.elementAt(contacts_list
					.getSelectedIndex());
			String name = contacts_list.getString(contacts_list.
					getSelectedIndex());
			update_form.setTitle("update " + name);
			send_box.setTitle("to " + name);
			Display.getDisplay(this).setCurrent(send_box);
		} else if (cmd == exit_cmd)
			notifyDestroyed();
	}

	public void startApp() {
		jid_list = new Vector();
		
		login_form = new Form("login");
		id_field = new TextField("JID(xxx@xxx.xxx)", "aiv.tst@gpsgeotrace.com", 30, TextField.ANY);
		login_form.append(id_field);
		passwd_field = new TextField("password", "qwe12345", 15, TextField.PASSWORD);
		login_form.append(passwd_field);
		server_field = new TextField("server", "gpsgeotrace.com", 20,
				TextField.ANY);
		login_form.append(server_field);
		exit_cmd = new Command("Exit", Command.EXIT, 0);
		login_cmd = new Command("Login", Command.OK, 1);
		login_form.addCommand(exit_cmd);
		login_form.addCommand(login_cmd);
		login_form.setCommandListener(this);

		contacts_list = new List("connecting...", List.IMPLICIT);
		//contacts_list.addCommand(exit_cmd);
		contact_cmd = new Command("Contact", Command.OK, 0);
		rename_cmd = new Command("Rename", Command.ITEM, 1);
		create_cmd = new Command("Create pubsub", Command.ITEM, 2);
		list_cmd = new Command("List pubsub", Command.ITEM, 3);
		contacts_list.addCommand(contact_cmd);
		contacts_list.addCommand(rename_cmd);
		contacts_list.addCommand(create_cmd);
		contacts_list.addCommand(list_cmd);
		contacts_list.setCommandListener(this);

		send_box = new TextBox(null, null, 50, TextField.ANY);
		back_cmd = new Command("Back", Command.BACK, 0);
		send_cmd = new Command("Send", Command.OK, 1);
		send_box.addCommand(back_cmd);
		send_box.addCommand(send_cmd);
		send_box.setCommandListener(this);
		
		subscribe_form = new Form("subscribe");
		subscribe_field = new TextField("JID(xxx@xxx.xxx)", "aiv.tst@gmail.com", 30, TextField.ANY);
		subscribe_cmd = new Command("Subscribe", Command.OK, 1);
		unsubscribe_cmd = new Command("Unsubscribe", Command.OK, 1);
		subscribe_form.append(subscribe_field);
		subscribe_form.addCommand(back_cmd);
		subscribe_form.addCommand(subscribe_cmd);
		subscribe_form.addCommand(unsubscribe_cmd);
		subscribe_form.setCommandListener(this);
		
		update_form = new Form("update");
		nick_field = new TextField("Nick", "", 20, TextField.ANY);
		group_field = new TextField("Group", "", 20, TextField.ANY);
		update_cmd = new Command("Update", Command.OK, 1);
		update_form.append(nick_field);
		update_form.append(group_field);
		update_form.addCommand(back_cmd);
		update_form.addCommand(update_cmd);
		update_form.setCommandListener(this);

		online_img = loadImage("/online.png");
		offline_img = loadImage("/offline.png");

		Display.getDisplay(this).setCurrent(login_form);
	}

	public void pauseApp() {
	}

	public void destroyApp(boolean unconditional) {
	}

	public void onEvent(Packet packet) {
		if (taskProvider.equals(packet)) {
			Task task = (Task) packet;
			msg_alert = new Alert("Task " + task.sender, task.description, null, AlertType.INFO);
			msg_alert.setTimeout(Alert.FOREVER);
			Display.getDisplay(this).setCurrent(msg_alert);
		}
	}
}
