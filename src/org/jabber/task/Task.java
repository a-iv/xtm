package org.jabber.task;

import java.io.IOException;

import net.sourceforge.jxa.Manager;
import net.sourceforge.jxa.packet.Packet;

/**
 * Пакет с задачей 
 */
public class Task extends Packet {
	public String id = new String();
	public String sender = new String();
	public String owner = new String();
	public String theme = new String();
	public String description = new String();
	public int fulfilment;

	/**
	 * Создание пакета задачи с указанием имени элемента и пространства имен
	 */
	public Task() {
		super("task", "http://jabber.org/protocol/task");
	}
	
	/**
	 * Функция подготовки к отправке xml-пакета с задачей
	 */
	public void emit(Manager manager) throws IOException {
		// Очистка вложенных пакетов
		clearPackets();
		// Заполнение вложенных пакетов с необходимым содержанием
		if (sender != null)
			addPacket(new Packet("sender", null, sender));
		if (owner != null)
			addPacket(new Packet("owner", null, owner));
		if (theme != null)
			addPacket(new Packet("theme", null, theme));
		if (description != null)
			addPacket(new Packet("description", null, description));
		addPacket(new Packet("id", null, String.valueOf(id)));
		addPacket(new Packet("fulfilment", null, String.valueOf(fulfilment)));
		// Вызов функции родительского класса для отправки произвольного xml-пакета
		super.emit(manager);
	}

	public Task(String id, String sender, String owner, String theme,
			String description, int fulfilment) {
		this();
		this.id = id;
		this.sender = sender;
		this.owner = owner;
		this.theme = theme;
		this.description = description;
		this.fulfilment = fulfilment;
	}
}
