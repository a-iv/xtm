package org.jabber.task;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import javax.microedition.lcdui.*;
import javax.microedition.rms.InvalidRecordIDException;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreFullException;
import javax.microedition.rms.RecordStoreNotOpenException;
import javax.microedition.rms.RecordStoreNotFoundException;

import net.sourceforge.jxa.Jxa;
import net.sourceforge.jxa.XmppListener;
import net.sourceforge.jxa.packet.Packet;
import net.sourceforge.jxa.packet.pubsub.PubsubItem;
import net.sourceforge.jxa.packet.pubsub.PubsubRetract;
import net.sourceforge.jxa.packet.pubsub.PubsubSubscription;

import javax.microedition.media.*;
import javax.microedition.media.control.*;
import javax.microedition.midlet.*;

/**
 * Пример реализации мидлета для тестирования разработанного стантарда для XMPP
 */
public class SampleMIDlet extends MIDlet implements CommandListener,
		XmppListener {
	String recordStoreName = "user2";
	String pubsubNode = "sample";
	Display display;
	Form login = new Form("Введите пароль");
//	List main;
	List taskList = new List("", Choice.IMPLICIT);
	List formContacts;
	Form contactinfo = new Form("инфо о контакте");
	Form ychet = new Form("Учетная запись");
	TextBox messagedisplay;
	Form connection = new Form("");
	Form newContactSet = new Form("");
	Form formTask = new Form("Задание");
	Form thisTask = new Form("Задание");
	RecordStore recordStore = null;
	List sort;

	Jxa jxa;
	Image offlineImg;
	Image onlineImg;
	
	Player player;
	
	TextField pass = new TextField("Введите пароль", "", 20, TextField.PASSWORD);
	TextField password = new TextField("Пароль", "", 20, TextField.PASSWORD);
	TextField name = new TextField("Фамилия, Имя", "", 100, 0);
	TextField work = new TextField("Должность", "", 100, 0);
	TextField name2 = new TextField("Фамилия, Имя", "", 100, 0);
	TextField work2 = new TextField("Должность", "", 100, 0);
	TextField password2 = new TextField("Пароль", "", 20, TextField.PASSWORD);
	TextField newContactJID = new TextField(
			"Введите JID пользователя,которого хотите добавить", "", 100, 0);
	TextField topic = new TextField("Тема:", "", 160, 0);
	TextField descript = new TextField("Описание:", "", 1024, 0);
	TextField topic2 = new TextField("Тема:", "", 160, 0);
	TextField descript2 = new TextField("Описание:", "", 1024, 0);
	TextField userJID = new TextField("JID:", "", 1024, 0);
	TextField serv = new TextField("server", "", 128, 0);
	TextField rename = new TextField("Имя:  ", "", 128, 0);
	TextField regroup = new TextField("Группа:  ", "", 128, 0);

	Gauge gauge = new Gauge("Выполнено", true, 10, 0);

	Command create = new Command("Создать", Command.ITEM, 0);
	Command select = new Command("Выбрать", Command.ITEM, 0);
	Command back = new Command("Назад", Command.BACK, 1);
	Command exit = new Command("Выход", Command.EXIT, 2);
	Command newTask = new Command("Новое задание", Command.ITEM, 3);
	Command sent = new Command("Отправить", Command.ITEM, 4);
	Command info = new Command("Инфо о контакте", Command.ITEM, 4);
	Command open = new Command("Открыть", Command.ITEM, 0);
	Command deletec = new Command("Удалить контакт", Command.ITEM, 4);
	Command affiliation = new Command("Предоставить привелегии", Command.ITEM, 5);
	Command newc = new Command("Добавить контакт", Command.ITEM, 4);
	Command yes = new Command("Да", Command.ITEM, 5);
	Command no = new Command("Нет", Command.ITEM, 5);
	Command ok = new Command("OK", Command.OK, 0);
	Command cancel = new Command("Отмена", Command.BACK, 5);
	Command message = new Command("Комментировать", Command.ITEM, 5);
	Command deleteThisTask = new Command("Удалить задание", Command.ITEM, 5);
	Command fulfil = new Command("Отметить как выполненное", Command.ITEM, 5);
	Command cSort = new Command("Фильтрация", Command.ITEM, 5);
	Command save = new Command("Сохранить", Command.ITEM, 5);
	Command update = new Command("Обновить", Command.ITEM, 5);
	String State;

	Vector contacts = new Vector();
	Vector tasks = new Vector();
	Vector comments = new Vector();

	String thisUserJID = new String();
	String thisContactJID = new String();
	String thisTaskID = new String();
	String subid = null;
	int pt = 11;

	void printContacts() {
		int selectedItem = formContacts.getSelectedIndex();
		formContacts.deleteAll();
		for (int i = 0; i < contacts.size(); i++) {
			Contact contact = (Contact) contacts.elementAt(i);
			if (contact.online) {
				formContacts.append(contact.name, onlineImg);
			} else {
				formContacts.append(contact.name, offlineImg);
			}
		}
		if (selectedItem != -1 && formContacts.size() > selectedItem)
			formContacts.setSelectedIndex(selectedItem, true);
	}

	Vector printedTaskIDs = new Vector();

	void printTasks(boolean compleated, boolean notCompleated) {
		taskList.deleteAll();
		printedTaskIDs.removeAllElements();
		for (int i = 0; i < tasks.size(); i = i + 1) {
			Task task = (Task) tasks.elementAt(i);
			if ((task.owner.equals(thisUserJID) && task.sender
					.equals(thisContactJID))
					|| (task.owner.equals(thisContactJID) && task.sender
							.equals(thisUserJID))) {
				if (task.fulfilment == 10) {
					if (compleated) {
						taskList.append(task.theme, onlineImg);
						printedTaskIDs.addElement(task.id);
					}
				} else {
					if (notCompleated) {
						taskList.append(task.theme, offlineImg);
						printedTaskIDs.addElement(task.id);
					}
				}
			}
		}
	}

	void updateTask(Task task) {
		System.out.println("Update: " + task.id);
		for (int i = 0; tasks.size() > i; i++) {
			if (task.id.equals(((Task) tasks.elementAt(i)).id)) {
				String prevId = task.id;
				task.id = jxa.getRandomID();
				tasks.setElementAt(task, i);
				for (int j = 0; comments.size() > j; j++) {
					if (prevId.equals(((Comment) comments.elementAt(j)).task)) {
						Comment comment = (Comment) comments.elementAt(j);
						comment.task = task.id;
						updateComment(comment);
					}
				}
				if (pt==11){
				printTasks(true, true);}
				if (pt==10){
					printTasks(true, false);
				}
				if (pt==1){
					printTasks(false, true);
				}
				jxa.pubsubRetract(pubsubNode, prevId);
				jxa.pubsubPublish(pubsubNode, task.id, task);
			}
		}
	}

	void updateComment(Comment comment) {
		for (int i = 0; comments.size() > i; i++) {
			if (comment.id.equals(((Comment) comments.elementAt(i)).id)) {
				String prevId = comment.id;
				comment.id = jxa.getRandomID();
				comments.setElementAt(comment, i);
				jxa.pubsubRetract(pubsubNode, prevId);
				jxa.pubsubPublish(pubsubNode, comment.id, comment);
				printComments();
			}
		}
	}
	
	int getTaskByID() {
		int index = taskList.getSelectedIndex();
		if (index == -1)
			return -1;
		String id = ((String) printedTaskIDs.elementAt(index));
		for (int i = 0; i < tasks.size(); i++) {
			Task task = (Task) tasks.elementAt(i);
			if (task.id.equals(id)) {
				return i;
			}
		}
		return -1;
	}

	void printComments() {
		int index = getTaskByID();
		if (index == -1)
			return;
		Task task = (Task) tasks.elementAt(index);
		thisTask.deleteAll();
		thisTask.append(gauge);
		gauge.setPreferredSize(200, 50);
		gauge.setValue(task.fulfilment);
		// if (task.sender == thisUserJID) {
		thisTask.append(topic2);
		topic2.setString(task.theme);
		thisTask.append(descript2);
		descript2.setString(task.description);
		/*
		 * } else { thisTask.setTitle(task.theme); thisTask.append("Описание:" +
		 * "\n"); thisTask.append(task.description + "\n"); }
		 */
		thisTask.append("Комментарии:" + "\n");
		for (int i = 0; i < comments.size(); i = i + 1) {
			Comment comment = (Comment) comments.elementAt(i);
			if (comment.task.equals(task.id)) {
				thisTask.append(new StringItem(comment.sender, comment.text));
			}
		}
	}

	protected void destroyApp(boolean unconditional) {
		try {
			System.out.println("Try to close");
			recordStore.closeRecordStore();
		} catch (RecordStoreNotOpenException e) {
			System.out.println("Record store not open: " + e);
		} catch (RecordStoreException e) {
			System.out.println("Another error: " + e);
		}
		notifyDestroyed();
	}

	protected void pauseApp() {
		notifyPaused();
	}

	protected void startApp() throws MIDletStateChangeException {
		onlineImg = loadImage("/online.png");
		offlineImg = loadImage("/offline.png");

		display = Display.getDisplay(this);

		// экран ввода пароля
		login.addCommand(exit);
		login.addCommand(select);
		login.addCommand(create);
		login.setCommandListener(this);
		login.append(userJID);
		login.append(pass);
		// TODO: login.append(serv);

//		// главное меню
//		main = new List("Главное меню", Choice.IMPLICIT);
//		main.append("контакты", null);
//		// TODO: main.append("учетная запись", null);
//		main.append("выход", null);
//		main.setCommandListener(this);
//		main.addCommand(exit);
//		main.addCommand(select);

		// форма чат
		taskList.setCommandListener(this);
		taskList.addCommand(back);
		taskList.addCommand(select);
		taskList.addCommand(newTask);
		taskList.addCommand(deleteThisTask);
		taskList.addCommand(cSort);

		sort = new List("Сортировка", Choice.IMPLICIT);
		sort.setCommandListener(this);
		sort.append("Выполненные", null);
		sort.append("Невыполненные", null);
		sort.append("Отображать все", null);
		sort.addCommand(back);

		// форма контакты
		formContacts = new List("Контакты", Choice.IMPLICIT);
		formContacts.setCommandListener(this);
		formContacts.addCommand(exit);
		formContacts.addCommand(newTask);
		formContacts.addCommand(newc);
		formContacts.addCommand(info);
		formContacts.addCommand(deletec);
		//formContacts.addCommand(affiliation);
		
		formContacts.addCommand(open);
		printContacts();

		// экран ввода сообщения
		messagedisplay = new TextBox("Комментарий", "", 1000, TextField.ANY);
		messagedisplay.setCommandListener(this);
		messagedisplay.addCommand(back);
		messagedisplay.addCommand(sent);

		// окно настроек учетной записи
		ychet.setCommandListener(this);
		ychet.addCommand(back);
		ychet.addCommand(ok);
		ychet.append(name);
		ychet.append(work);
		ychet.append(password);

		// подключение
		connection.setCommandListener(this);
		connection.append("подключение к серверу");
		connection.addCommand(exit);

		// инфо о контакте
		contactinfo.setCommandListener(this);
		contactinfo.addCommand(back);
		contactinfo.addCommand(save);
		contactinfo.append(rename);
		contactinfo.append(regroup);

		newContactSet.setCommandListener(this);
		newContactSet.addCommand(cancel);
		newContactSet.addCommand(ok);
		newContactSet.append(newContactJID);

		formTask.setCommandListener(this);
		formTask.addCommand(ok);
		formTask.addCommand(cancel);
		formTask.append(topic);
		formTask.append(descript);

		thisTask.setCommandListener(this);
		thisTask.addCommand(back);
		thisTask.addCommand(message);
		thisTask.addCommand(fulfil);
		thisTask.addCommand(update);

		display.setCurrent(login);

		try {
			recordStore = RecordStore.openRecordStore(recordStoreName, false);
		} catch (RecordStoreNotFoundException e) {
			try {
				recordStore = RecordStore
						.openRecordStore(recordStoreName, true);
			} catch (RecordStoreNotFoundException e2) {
				System.out.println("Record store not found: " + e2);
			} catch (RecordStoreException e2) {
				System.out.println("Create record exception: " + e2);
			}
		} catch (RecordStoreException e) {
			System.out.println("Open record exception: " + e);
		}
		if (getRecordText(1) != null) {
			userJID.setString(getRecordText(1));
		} else {
			userJID.setString("test1@xmpptask.ru");
		}
		if (getRecordText(2) != null) {
			pass.setString(getRecordText(2));
		} else {
			pass.setString("qwe12345");
		}
		if (getRecordText(3) != null) {
			serv.setString(getRecordText(3));
		} else {
			serv.setString("xmpptask.ru");
		}
	}
	
	String getRecordText(int id) {
		String str = null;
		try {
			byte[] data = recordStore.getRecord(id);
			if (data != null)
				str = new String(data);
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("Array index out of bounds: " + e);
		} catch (InvalidRecordIDException e) {
			System.out.println("Invalide record ID: " + e);
		} catch (RecordStoreNotOpenException e) {
			System.out.println("Record store not open: " + e);
		} catch (RecordStoreException e) {
			System.out.println("Another record store exception: " + e);
		}
		return str;
	}

	void setRecordText(int id, byte[] mass) {
		try {
			recordStore.setRecord(id, mass, 0, mass.length);
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("Array index out of bounds: " + e);
		} catch (InvalidRecordIDException e) {
			try {
				recordStore.addRecord(mass, 0, mass.length);
			} catch (RecordStoreFullException e2) {
				System.out.println("Record store full: " + e2);
			} catch (RecordStoreNotOpenException e2) {
				System.out.println("Record store not open (add): " + e2);
			} catch (RecordStoreException e2) {
				System.out.println("Another record store exception (add): "
						+ e2);
			}
		} catch (RecordStoreNotOpenException e) {
			System.out.println("Record store not open (set): " + e);
		} catch (RecordStoreException e) {
			System.out.println("Another record store exception (set): " + e);
		}
	}

	public void loginAction(Command c, Displayable d) {
		// Форма входа
		if (c == select) {
			thisUserJID = userJID.getString();
			setRecordText(1, userJID.getString().getBytes());
			setRecordText(2, pass.getString().getBytes());
			setRecordText(3, serv.getString().getBytes());
			display.setCurrent(connection);
			// display.setCurrent(main);
			jxa = new Jxa(thisUserJID, pass.getString(), "mobile", 10, serv
					.getString(), "5222", false, "pubsub." + serv.getString(), false);
			jxa.addListener(this);
			jxa.addProvider(new TaskProvider());
			jxa.addProvider(new CommentProvider());
			jxa.start();
		} else if (c == create) {
			thisUserJID = userJID.getString();
			setRecordText(1, userJID.getString().getBytes());
			setRecordText(2, pass.getString().getBytes());
			setRecordText(3, serv.getString().getBytes());
			display.setCurrent(connection);
			// display.setCurrent(main);
			jxa = new Jxa(thisUserJID, pass.getString(), "mobile", 10, serv
					.getString(), "5222", false, "pubsub." + serv.getString(), true);
			jxa.addListener(this);
			jxa.addProvider(new TaskProvider());
			jxa.addProvider(new CommentProvider());
			jxa.start();
		} else if (c == exit) {
			destroyApp(true);
		}

	}

	public void connectionAction(Command c, Displayable d) {
		if (c == exit) {
			destroyApp(true);
		}
	}

	public void mainAction(Command c, Displayable d) {
//		if (c == exit) {
//			destroyApp(true);
//		} else {
//			switch (main.getSelectedIndex()) {
//			case 0:
//				printContacts();
//				display.setCurrent(formContacts);
//				break;
//			case 1:
//				destroyApp(true);
//				break;
//			// TODO:
//			case 2:
//				display.setCurrent(ychet);
//				break;
//			case 3:
//				jxa.pubsubCreateNode(pubsubNode);
//				break;
//			case 4:
//				jxa.pubsubCreateNode(pubsubNode);
//				jxa.pubsubSubscribe(pubsubNode);
//				break;
//			}
//		}
	}

	public void taskListAction(Command c, Displayable d) {
		if (c == back) {
			display.setCurrent(formContacts);
		} else if (c == deleteThisTask) {
			int index = taskList.getSelectedIndex();
			if (index != -1) {
				thisTaskID = ((String) printedTaskIDs.elementAt(index));
				jxa.pubsubRetract(pubsubNode, thisTaskID);
			}
		} else if (c == newTask) {
			display.setCurrent(formTask);
			topic.setString("");
			descript.setString("");
		} else if (c == cSort) {
			display.setCurrent(sort);
		} else {
			int index = taskList.getSelectedIndex();
			if (index != -1) {
				thisTaskID = ((String) printedTaskIDs.elementAt(index));
				String topictask = taskList.getString(index);
				display.setCurrent(thisTask);
				printComments();
				thisTask.setTitle(topictask);
			}
		}
	}

	public void formContactsAction(Command c, Displayable d) {
		if (c == exit) {
			destroyApp(true);
//			display.setCurrent(main);
		} else if (c == info) {
			int a;
			a = formContacts.getSelectedIndex();
			thisContactJID = ((Contact) contacts.elementAt(a)).jid;
			contactinfo.setTitle("Инфо о " + thisContactJID);
			rename.setString(((Contact) contacts.elementAt(a)).name);
			regroup.setString(((Contact) contacts.elementAt(a)).group);
			display.setCurrent(contactinfo);
		} else if (c == open) {
			thisContactJID = ((Contact) contacts.elementAt(formContacts
					.getSelectedIndex())).jid;
			String contname = formContacts.getString(formContacts
					.getSelectedIndex());
			taskList.setTitle(contname);
			if (pt==11){
				printTasks(true, true);}
				if (pt==10){
					printTasks(true, false);
				}
				if (pt==1){
					printTasks(false, true);
				}
			display.setCurrent(taskList);
		} else if (c == newc) {
			newContactJID.setString("");
			display.setCurrent(newContactSet);
		} else if (c == deletec) {
			jxa.unsubscribe(((Contact) contacts.elementAt(formContacts
					.getSelectedIndex())).jid);
		} else if (c == affiliation) {
			jxa.pubsubSetAffiliation(pubsubNode, ((Contact) contacts.
					elementAt(formContacts.getSelectedIndex())).jid, "owner");
		} else if (c == newTask) {
			thisContactJID = ((Contact) contacts.elementAt(formContacts
					.getSelectedIndex())).jid;
			topic.setString("");
			display.setCurrent(formTask);
		} else {
			thisContactJID = ((Contact) contacts.elementAt(formContacts
					.getSelectedIndex())).jid;
			String contname = formContacts.getString(formContacts
					.getSelectedIndex());
			taskList.setTitle(contname);
			if (pt==11){
				printTasks(true, true);}
				if (pt==10){
					printTasks(true, false);
				}
				if (pt==1){
					printTasks(false, true);
				}
			display.setCurrent(taskList);
		}
	}

	public void newContactSetAction(Command c, Displayable d) {
		if (c == cancel) {
			display.setCurrent(formContacts);
			printContacts();
		} else if (c == ok && !newContactJID.getString().equals("")) {
			jxa.subscribe(newContactJID.getString());
			display.setCurrent(formContacts);
		}
	}

	public void messageDisplayAction(Command c, Displayable d) {
		if (c == back) {
			display.setCurrent(thisTask);
		} else if (c == sent) {
			Comment comment = new Comment();
			comment.id = jxa.getRandomID();
			comment.task = thisTaskID;
			comment.sender = thisUserJID;
			comment.text = messagedisplay.getString();
			jxa.pubsubPublish(pubsubNode, comment.id, comment);
			display.setCurrent(thisTask);
		}
	}

	public void ychetAction(Command c, Displayable d) {
//		if (c == back) {
//			display.setCurrent(main);
//		} else if (c == ok && !name.getString().equals("")) {
//			// String s = name.getString();
//			/*
//			 * byte[] data = s.getBytes(); setRecordText(1,data);
//			 */
//			// String s2 = work.getString();
//			/*
//			 * byte[] data2 = s2.getBytes(); setRecordText(2,data2);/
//			 */
//			// String s3 = password.getString();
//			/*
//			 * byte[] data3 = s3.getBytes(); setRecordText(3, data3);
//			 */
//		}
//		display.setCurrent(main);
	}

	public void contactinfoAction(Command c, Displayable d) {
		if (c == back) {
			display.setCurrent(formContacts);
		} else if (c == save) {
			String name = rename.getString();
			String groupp = regroup.getString();
			Vector group = new Vector();
			group.addElement(groupp);
			jxa.saveContact(thisContactJID, name, group.elements(), null);
			display.setCurrent(formContacts);
			printContacts();
		}
	}

	public void formTaskAction(Command c, Displayable d) {
		if (c == cancel) {
			display.setCurrent(taskList);
		} else if (c == ok && !topic.equals("")) {
			String strTopic = topic.getString();
			String strDescript = descript.getString();
			Task task = new Task();
			task.id = jxa.getRandomID();
			task.description = strDescript;
			task.fulfilment = 0;
			task.owner = thisContactJID;
			task.sender = thisUserJID;
			task.theme = strTopic;
			jxa.pubsubPublish(pubsubNode, task.id, task);
			display.setCurrent(taskList);
		}
	}

	/**
	 * Обработка команд пользователя
	 */
	public void thisTaskAction(Command c, Displayable d) {
		if (c == back) {
			if (pt==11){
				printTasks(true, true);}
				if (pt==10){
					printTasks(true, false);
				}
				if (pt==1){
					printTasks(false, true);
				}
			display.setCurrent(taskList);
		} else if (c == message) {
			messagedisplay.setString("");
			display.setCurrent(messagedisplay);
		} else if (c == fulfil) {
			gauge.setValue(10);
		// Если была выполнена команда обновления задачи
		} else if (c == update) {
			// Производим поиск выбранной задачи
			for (int i = 0; tasks.size() > i; i++) {
				if (thisTaskID.equals(((Task) tasks.elementAt(i)).id)) {
					Task task = (Task) tasks.elementAt(i);
					// Создаём новую задачу
					Task update = new Task();
					// Переносим данные в новую задачу
					update.id = task.id;
					update.owner = task.owner;
					update.sender = task.sender;
					update.theme = topic2.getString();
					update.description = descript2.getString();
					update.fulfilment = gauge.getValue();
					// Публикуем изменённые данные в pubsub node'у с указанным идентификатором 
					//jxa.pubsubPublish(pubsubNode, update.id, update);
					updateTask(update);
					// Изменения будут применены после подтверждения со стороны сервера
				}
			}
			display.setCurrent(taskList);
		}
	}

	public void sortAction(Command c, Displayable d) {
		if (c == back) {
			display.setCurrent(taskList);
		} else {
			switch (sort.getSelectedIndex()) {
			case 0:
				display.setCurrent(taskList);
				printTasks(true, false);
				pt = 10;
				break;
			case 1:
				display.setCurrent(taskList);
				printTasks(false, true);
				pt = 1;
				break;
			case 2:
				display.setCurrent(taskList);
				printTasks(true, true);
				pt = 11;
				break;
			}
		}
	}

	public void commandAction(Command c, Displayable d) {
		if (display.getCurrent() == login) {
			loginAction(c, d);
		} else if (display.getCurrent() == connection) {
			// подключение
			connectionAction(c, d);
//		} else if (display.getCurrent() == main) {
//			// Главная форма
//			mainAction(c, d);
		} else if (display.getCurrent() == taskList) {
			// форма чат
			taskListAction(c, d);
		} else if (display.getCurrent() == formContacts) {
			// форма контактов
			formContactsAction(c, d);
		} else if (display.getCurrent() == newContactSet) {
			newContactSetAction(c, d);
		} else if (display.getCurrent() == messagedisplay) {
			// окно ввода сообщений
			messageDisplayAction(c, d);
		} else if (display.getCurrent() == ychet) {
			// окно настройки учетной записи
			ychetAction(c, d);
		} else if (display.getCurrent() == contactinfo) {
			contactinfoAction(c, d);
		} /*
		 * else if (display.getCurrent() == ycheterror) { if (c == exit) {
		 * destroyApp(true); } else if (c == ok) { display.setCurrent(ychet2); }
		 * }
		 */
		/*
		 * else if (display.getCurrent() == ychet2) { if (c == exit) {
		 * destroyApp(true); } else if ((c == ok) &&
		 * !name2.getString().equals("") && !work2.getString().equals("") ) {
		 * String s = name2.getString(); byte[] data = s.getBytes();
		 * setRecordText(1, data); String s2 = work2.getString(); byte[] data2 =
		 * s2.getBytes(); setRecordText(2, data2); String s3 =
		 * password2.getString(); byte[] data3 = s3.getBytes(); setRecordText(3,
		 * data3); display.setCurrent(connection); } else if ((c == ok) &&
		 * (name2.getString().equals("") || work2.getString().equals(""))) {
		 * ychet2.append("Заполните учетную запись полностью!"); } }
		 */
		else if (display.getCurrent() == formTask) {
			formTaskAction(c, d);
		} else if (display.getCurrent() == thisTask) {
			thisTaskAction(c, d);
		} else if (display.getCurrent() == sort) {
			sortAction(c, d);
		}
	}

	private Image loadImage(String fileName) {
		Image image = null;
		try {
			image = Image.createImage(fileName);
		} catch (IOException ioe) {
			System.out.println(ioe);
		}
		return image;
	}

	public void onAuth(String resource) {
		jxa.getRoster();
		jxa.pubsubAllSubscriptions(pubsubNode);
		printContacts();
		
		//jxa.pubsubCreateNode(pubsubNode);
		//jxa.pubsubSubscribe(pubsubNode);
		//jxa.pubsubSetAffiliation(pubsubNode, "test2@gpsgeotrace.com", "owner");
		//jxa.pubsubSetAffiliation(pubsubNode, "test3@gpsgeotrace.com", "owner");
		//jxa.pubsubSetAffiliation(pubsubNode, "test4@gpsgeotrace.com", "owner");
		
		display.setCurrent(formContacts);
	}

	public void onAuthFailed(String message) {
		display.setCurrent(login);
		Alert alert = new Alert("Ошибка авторизации", "проверьте настройки подключения и " +
				"правильность ввода логина и пароля.", null,
				AlertType.ERROR);
		alert.setTimeout(Alert.FOREVER);
		Display.getDisplay(this).setCurrent(alert);
	}

	public void onConnFailed(String msg) {
		Alert alert = new Alert("Ошибка подключения", "проверьте настройки подключения к сети Интернет", null,
				AlertType.ERROR);
		alert.setTimeout(Alert.FOREVER);
		Display.getDisplay(this).setCurrent(alert);
	}

	public void onContactEvent(final String jid, final String name,
			final String group, final String subscription) {
		System.out.println("Contact " + jid + "," + name + "," + group
				+ "," + subscription);
		if (subscription.equals("both") || subscription.equals("to")) {
			boolean found = false;
			int index = -1;
			for (int i = 0; i < contacts.size(); i++) {
				Contact con = (Contact) contacts.elementAt(i);
				if (con.jid.equals(jid)) {
					found = true;
					index = i;
				}
			}
			if (found) {
				Contact contact = (Contact) contacts.elementAt(index);
				contact.jid = jid;
				contact.name = name;
				contact.group = group;
				contacts.setElementAt(contact, index);
			} else {
				Contact contact = new Contact();
				contact.jid = jid;
				if (name != null) {
					contact.name = name;
				} else {
					contact.name = jid;
				}
				contact.group = group;
				contacts.addElement(contact);
			}
			printContacts();
		} else {
			int index = -1;
			for (int i = 0; i < contacts.size(); i = i + 1) {
				Contact contact = (Contact) contacts.elementAt(i);
				if (contact.jid.equals(jid)) {
					index = i;
				}
			}
			if (index != -1) {
				contacts.removeElementAt(index);
				printContacts();
			}
		}
	}

	public void onContactOverEvent() {
	}

	public void onMessageEvent(String from, String body) {
		Alert alert = new Alert("Сообщение от " + from, body, null,
				AlertType.INFO);
		alert.setTimeout(Alert.FOREVER);
		Display.getDisplay(this).setCurrent(alert);
	}

	public void onStatusEvent(String jid, String show, String status) {
		int i = jid.indexOf('/');
		String bareJid = jid.substring(0, i);
		for (int j = 0; j < contacts.size(); j++) {
			Contact contact = (Contact) contacts.elementAt(j);
			if (bareJid.equals(contact.jid)) {
				if (show.equals("na")) {
					contact.online = false;
				} else {
					contact.online = true;
				}
			}
		}
		printContacts();
	}

	public void onSubscribeEvent(String jid) {
		System.out.println("Subscribe from " + jid);
		jxa.subscribed(jid);
		jxa.subscribe(jid);
	}

	public void onUnsubscribeEvent(String jid) {
		System.out.println("Unsubscribe from " + jid);
		jxa.unsubscribed(jid);
		jxa.unsubscribe(jid);
	}

	/**
	 * Обработка полученного задания
	 * 
	 * @param task - объект с задачей
	 */
	public void onTaskEvent(Task task) {
		int index = -1;
		// Производим поиск задачи среди имеющихся
		for (int i = 0; i < tasks.size(); i++) {
			if (task.id.equals(((Task) tasks.elementAt(i)).id)) {
				index = i;
			}
		}
		// Если задача не найдена
		if (index == -1) {
			// Добавляем новую
			tasks.addElement(task);
		} else {
			// Изменяем имеющуюся
			tasks.setElementAt(task, index);
		}
		// Перерисовываем список контактов
		if (pt==11){
			printTasks(true, true);}
			if (pt==10){
				printTasks(true, false);
			}
			if (pt==1){
				printTasks(false, true);
			}
		
		if ((player == null)||(player.getState() == Player.PREFETCHED)){
		try {
			player = Manager.createPlayer(Manager.TONE_DEVICE_LOCATOR);
			player.realize();
			ToneControl tc1 = (ToneControl)player.getControl("ToneControl");
			byte[] Nots={
					ToneControl.VERSION, 1, //версия используемого атрибута
					ToneControl.TEMPO, 50, //темп мелодии. Переменная speed = 5-127
					ToneControl.BLOCK_START, 0, //начало блока 0
					ToneControl.C4+7, 8,
					ToneControl.C4+2, 16,
					ToneControl.BLOCK_END, 0, //конец блока 0
					ToneControl.PLAY_BLOCK, 0}; //воспроизведение блока 0
			//Подключаем последовательность нот, указанных в массиве Nots
			tc1.setSequence(Nots);
			player.start();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (MediaException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	}

	public void onCommentEvent(Comment comment) {
		int index = -1;
		for (int i = 0; i < comments.size(); i++) {
			if (comment.id.equals(((Comment) comments.elementAt(i)).id)) {
				index = i;
			}
		}
		if (index == -1) {
			comments.addElement(comment);
		} else {
			comments.setElementAt(comment, index);
		}
		printComments();
	}

	/**
	 * Удаление объекта по его уникальному идентификатору
	 * 
	 * @param id - идентификатор объекта
	 */
	public void onItemDelete(String id) {
		// Поиск задачи с указанным идентификатором
		for (int i = 0; i < tasks.size(); i++){
			if (id.equals(((Task) tasks.elementAt(i)).id)){
				if (thisTaskID.equals(id)) {
					display.setCurrent(taskList);
				}
				// Удаляем задачу
				tasks.removeElementAt(i);
				// Обновляем список задач
				if (pt==11){
					printTasks(true, true);}
					if (pt==10){
						printTasks(true, false);
					}
					if (pt==1){
						printTasks(false, true);
					}
			} 
		}
		// Поиск комментария с указанным идентификатором
		for (int i = 0; i < comments.size(); i++){
			if (id.equals(((Comment) comments.elementAt(i)).id)){
				// Удаляем комментарий
				comments.removeElementAt(i);
				// Обновляем список комментариев
				printComments();
			}
		}
	}
	
	/**
	 * Функция, вызываемая при обнаружении пакета
	 */
	public void onEvent(Packet packet) {
		// если пакет является информацией о подписке
		if (packet.equals("subscription", null)) {
			// преобразуем абстрактный пакет в пакет подписки
			PubsubSubscription subscription = (PubsubSubscription) packet;
			// если идентифокатор подписки соответствует идентификатору пользователя 
			// и идентификатор pubsub node'ы соответствует текущей
			if (subscription.jid.equals(jxa.myjid)
					&& subscription.node.equals(pubsubNode) && subid == null) {
				subid = subscription.subid;
				// запрашиваем все элементы, находящиеся в node'е для указанной подписки
				jxa.pubsubAllItems(pubsubNode, subid);
			}
		// если пакет является информацией о задаче или комментарии
		} else if (packet.equals("item", null)) {
			// преобразуем абстрактный пакет в пакет с элементом публикации
			PubsubItem item = (PubsubItem) packet;
			System.out.println("Found: " + item.id);
			// обработка вложенных пакетов
			for (Enumeration e = item.getPackets(); e.hasMoreElements();) {
				Packet found = (Packet) e.nextElement();
				// если вложенный пакет является информацией о задаче
				if (found.equals("task", "http://jabber.org/protocol/task")) {
					Task task = (Task) found;
					task.id = item.id;
					// вызываем обновление данных о задаче
					onTaskEvent(task);
				// если вложенный пакет является информацией о комментарии
				} else if (found.equals("comment",
						"http://jabber.org/protocol/task")) {
					Comment comment = (Comment) found;
					comment.id = item.id;
					// вызываем обновление данных по комментариям
					onCommentEvent(comment);
				}
			}
		// если пакет является информацией об удалении задачи или комментария
		} else if (packet.equals("retract", null)) {
			// преобразуем в пакет удаления элемента
			PubsubRetract retract = (PubsubRetract) packet;
			// вызываем удаление данного элемента
			onItemDelete(retract.id);
		}
	}
}
