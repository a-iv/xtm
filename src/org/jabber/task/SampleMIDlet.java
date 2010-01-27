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

public class SampleMIDlet extends MIDlet implements CommandListener,
		XmppListener {
	int k = 0;

	String recordStoreName = "user2";
	String pubsubNode = "sample";
	Display display;
	Form login = new Form("Введите пароль");
	List main;
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
	Command cSort = new Command("Сортировка", Command.ITEM, 5);
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

	void onDeleteTask() {
		String id = ((String) printedTaskIDs.elementAt(taskList
				.getSelectedIndex()));
		int index = 0;
		for (int i = 0; i < tasks.size(); i = i + 1) {
			Task task = (Task) tasks.elementAt(i);
			if (task.id.equals(id)) {
				index = i;
			}
		}
		tasks.removeElementAt(index);
		printTasks(true, true);
	}

	void deleteTask(String id) {
		int k = 0;
		for (int i = 0; i < tasks.size(); i++) {
			Task task = (Task) tasks.elementAt(i);
			if (id.equals(task.id)) {
				k = i;
			}
		}
		tasks.removeElementAt(k);
		printTasks(true, true);
	}
	
	void updateTask(Task task) {
		for (int i = 0; tasks.size() > i; i++) {
			if (task.id.equals(((Task) tasks.elementAt(i)).id)) {
				String prevId = task.id;
				task.id = jxa.getRandomID();
				tasks.setElementAt(task, i);
				jxa.pubsubRetract(pubsubNode, prevId);
				jxa.pubsubPublish(pubsubNode, task.id, task);
				printTasks(true, true);
				for (int j = 0; comments.size() > j; j++) {
					if (task.id.equals(((Comment) comments.elementAt(j)).task)) {
						updateComment((Comment) comments.elementAt(j));
					}
				}
			}
		}
	}

	void updateComment(Comment comment) {
		for (int i = 0; comments.size() > i; i++) {
			if (comment.id.equals(((Comment) comments.elementAt(i)).id)) {
				String prevId = comment.id;
				comment.id = jxa.getRandomID();
				tasks.setElementAt(comment, i);
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
		login.setCommandListener(this);
		login.append(userJID);
		login.append(pass);
		// TODO: login.append(serv);

		// главное меню
		main = new List("Главное меню", Choice.IMPLICIT);
		main.append("контакты", null);
		// TODO: main.append("учетная запись", null);
		main.append("выход", null);
		main.setCommandListener(this);
		main.addCommand(exit);
		main.addCommand(select);

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
		formContacts.addCommand(back);
		formContacts.addCommand(newTask);
		formContacts.addCommand(newc);
		formContacts.addCommand(info);
		formContacts.addCommand(deletec);
		formContacts.addCommand(affiliation);
		
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
			userJID.setString("test1@gpsgeotrace.com");
		}
		if (getRecordText(2) != null) {
			pass.setString(getRecordText(2));
		} else {
			pass.setString("qwe12345");
		}
		if (getRecordText(3) != null) {
			serv.setString(getRecordText(3));
		} else {
			serv.setString("gpsgeotrace.com");
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
					.getString(), "5222", false, "pubsub." + serv.getString());
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
		if (c == exit) {
			destroyApp(true);
		} else {
			switch (main.getSelectedIndex()) {
			case 0:
				printContacts();
				display.setCurrent(formContacts);
				break;
			case 1:
				destroyApp(true);
				break;
			// TODO:
			case 2:
				display.setCurrent(ychet);
				break;
			case 3:
				jxa.pubsubCreateNode(pubsubNode);
				break;
			case 4:
				jxa.pubsubSubscribe(pubsubNode);
				break;
			}
		}
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
		if (c == back) {
			display.setCurrent(main);
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
			printTasks(true, true);
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
			printTasks(true, true);
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
			System.out.println("Comment: " + comment.id);
			jxa.pubsubPublish(pubsubNode, comment.id, comment);
			display.setCurrent(thisTask);
		}
	}

	public void ychetAction(Command c, Displayable d) {
		if (c == back) {
			display.setCurrent(main);
		} else if (c == ok && !name.getString().equals("")) {
			// String s = name.getString();
			/*
			 * byte[] data = s.getBytes(); setRecordText(1,data);
			 */
			// String s2 = work.getString();
			/*
			 * byte[] data2 = s2.getBytes(); setRecordText(2,data2);/
			 */
			// String s3 = password.getString();
			/*
			 * byte[] data3 = s3.getBytes(); setRecordText(3, data3);
			 */
		}
		display.setCurrent(main);
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
			System.out.println("Create " + task.id);
			jxa.pubsubPublish(pubsubNode, task.id, task);
			display.setCurrent(taskList);
		}
	}

	public void thisTaskAction(Command c, Displayable d) {
		if (c == back) {
			int i;
			for (i = 0; i < tasks.size(); i = i + 1) {
				Task task = (Task) tasks.elementAt(i);
				if (task.id.equals(thisTaskID)) {
					task.fulfilment = gauge.getValue();
				}
			}
			display.setCurrent(taskList);
			printTasks(true, true);
		} else if (c == message) {
			display.setCurrent(messagedisplay);
			messagedisplay.setString("");
		} else if (c == fulfil) {
			gauge.setValue(10);
		} else if (c == update) {
			for (int i = 0; tasks.size() > i; i++) {
				if (thisTaskID.equals(((Task) tasks.elementAt(i)).id)) {
					Task task = (Task) tasks.elementAt(i);
					Task update = new Task();
					update.id = task.id;
					update.theme = topic2.getString();
					update.description = descript2.getString();
					update.fulfilment = gauge.getValue();
					updateTask(update);
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
				break;
			case 1:
				display.setCurrent(taskList);
				printTasks(false, true);
				break;
			case 2:
				display.setCurrent(taskList);
				printTasks(true, true);
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
		} else if (display.getCurrent() == main) {
			// Главная форма
			mainAction(c, d);
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
		display.setCurrent(main);
	}

	public void onAuthFailed(String message) {
		System.out.println("Auth failed");
	}

	public void onConnFailed(String msg) {
		System.out.println("Connection failed");
	}

	public void onContactEvent(final String jid, final String name,
			final String group, final String subscription) {
		System.out.println("Contact event " + jid + "  " + name + "  " + group
				+ "  " + subscription);
		if (subscription.equals("both")) {
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
		System.out.println("Status of  " + jid);
		System.out.println("Status:  " + status);
		int i = jid.indexOf('/');
		String bareJid = jid.substring(0, i);
		for (int j = 0; j < contacts.size(); j++) {
			Contact contact = (Contact) contacts.elementAt(j);
			if (bareJid.equals(contact.jid)) {
				System.out.println("found");
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
		jxa.subscribe(jid);
	}

	public void onUnsubscribeEvent(String jid) {
		System.out.println("Unsubscribe from " + jid);
		jxa.unsubscribe(jid);
	}

	public void onTaskEvent(Task task) {
		int index = -1;
		for (int i = 0; i < tasks.size(); i++) {
			if (task.id.equals(((Task) tasks.elementAt(i)).id)) {
				index = i;
			}
		}
		if (index == -1) {
			tasks.addElement(task);
		} else {
			tasks.setElementAt(task, index);
		}
		printTasks(true, true);
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

	public void onItemDelete(String id) {

	}

	public void onEvent(Packet packet) {
		if (packet.equals("subscription", null)) {
			PubsubSubscription subscription = (PubsubSubscription) packet;
			if (subscription.jid.equals(jxa.myjid)
					&& subscription.node.equals(pubsubNode) && subid == null) {
				subid = subscription.subid;
				jxa.pubsubAllItems(pubsubNode, subid);
			}
		} else if (packet.equals("item", null)) {
			PubsubItem item = (PubsubItem) packet;
			System.out.println("Found " + item.getProperty("id"));
			for (Enumeration e = item.getPackets(); e.hasMoreElements();) {
				Packet found = (Packet) e.nextElement();
				if (found.equals("task", "http://jabber.org/protocol/task")) {
					Task task = (Task) found;
					task.id = item.id;
					onTaskEvent(task);
				} else if (found.equals("comment",
						"http://jabber.org/protocol/task")) {
					Comment comment = (Comment) found;
					comment.id = item.id;
					onCommentEvent(comment);
				}
			}
		} else if (packet.equals("retract", null)) {
			PubsubRetract retract = (PubsubRetract) packet;
			System.out.println("Retract " + retract.id);
			onItemDelete(retract.id);
		}
	}
}
