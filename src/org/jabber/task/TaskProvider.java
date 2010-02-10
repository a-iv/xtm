package org.jabber.task;

import java.util.Enumeration;

import net.sourceforge.jxa.packet.Packet;
import net.sourceforge.jxa.provider.Provider;

/**
 * Класс, отвечающий за создание пакета задач из полученного с сервера xml
 */
public class TaskProvider extends Provider {
	/**
	 * Создание поставщика задач с указанием используемого имени элемента и пространства имен
	 */
	public TaskProvider() {
		super("task", "http://jabber.org/protocol/task", false);
	}

	/**
	 * Создание пакета
	 */
	protected Packet createPacket() {
		return new Task();
	}
	
	/**
	 * Обработка пакета поосле его наполнения
	 * 
	 * @param packet - пакет, полученный в результате разбора xml
	 */
	protected Packet parseComplited(Packet packet) {
		// Преобразование пакета в задачу
		Task task = (Task) packet;
		// Поиск вложенных пакетов и заполнение соответствующие поля задачи
		for (Enumeration e = packet.getPackets(); e.hasMoreElements();) {
			Packet found = (Packet) e.nextElement();
			if (found.getElementName().equals("sender"))
				task.sender = found.getPayload();
			if (found.getElementName().equals("owner"))
				task.owner = found.getPayload();
			if (found.getElementName().equals("theme"))
				task.theme = found.getPayload();
			if (found.getElementName().equals("description"))
				task.description = found.getPayload();
			if (found.getElementName().equals("fulfilment"))
				task.fulfilment = Integer.valueOf(found.getPayload()).intValue();
		}
		return task;
	}
}
