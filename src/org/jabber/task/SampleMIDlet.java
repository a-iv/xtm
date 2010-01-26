package org.jabber.task;

import java.io.IOException;
import java.util.Vector;

import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import javax.microedition.lcdui.*;
import javax.microedition.midlet.*;
import javax.microedition.rms.InvalidRecordIDException;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreFullException;
import javax.microedition.rms.RecordStoreNotOpenException;

/*
 RecordStore zapis = null;
 //1 строка - имя
 //2 строка - дата рождения
 //3 строка - работа
 //4 строка - пароль

 try {
 zapis = RecordStore.openRecordStore( "", false );
 }
 catch( RecordStoreNotFoundException e ){
 // не существует
 }
 catch( RecordStoreException e ){
 // какие-то другие ошибки
 }

 try {
 zapis.closeRecordStore();
 }
 catch( RecordStoreNotOpenException e ){
 // уже закрыта
 }
 catch( RecordStoreException e ){
 // какие-то другие ошибки
 }

 */
import javax.microedition.rms.RecordStoreNotFoundException;

import net.sourceforge.jxa.Jxa;
import net.sourceforge.jxa.XmppListener;
import net.sourceforge.jxa.client.Roster;

public class SampleMIDlet extends MIDlet implements CommandListener,
		XmppListener {
	int k = 0;

	String recordStoreName = "user2";
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
	// private Roster roster = new Roster();

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
	TextField rename = new TextField("Имя:  ", "" , 128, 0);
	TextField regroup = new TextField("Группа:  ", "" , 128, 0);

	Gauge gauge = new Gauge("Выполнено", true, 10, 0);
	// gauge.getValue()

	Command select = new Command("Выбрать", Command.ITEM, 0);
	Command back = new Command("Назад", Command.BACK, 1);
	Command exit = new Command("Выход", Command.EXIT, 2);
	Command newTask = new Command("Новое задание", Command.ITEM, 3);
	Command sent = new Command("Отправить", Command.ITEM, 4);
	Command info = new Command("Инфо о контакте", Command.ITEM, 4);
	Command open = new Command("Открыть", Command.ITEM, 0);
	Command deletec = new Command("Удалить контакт", Command.ITEM, 4);
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
	String State;

	Vector contacts = new Vector();
	Vector tasks = new Vector();
	Vector comments = new Vector();
	/*
	 * Task[] tasks = new Task[200]; int tasksCount = 4;
	 * 
	 * Task getTaskByID(int id) { for (int i = 0; i < tasksCount; i++) { if
	 * (tasks[i].id == id) return tasks[i]; } return null; }
	 */
	String thisUserJID = new String();
	String thisContactJID = new String();
	int thisTaskID;

	void printContacts() {
		int selectedItem = formContacts.getSelectedIndex();
		formContacts.deleteAll();
		for (int i = 0; i < contacts.size(); i++) {
			Contact contact = (Contact) contacts.elementAt(i);
			if (contact.name != null)
				formContacts.append(contact.name, null);
			else
				formContacts.append(contact.jid, null);
		}
		if (selectedItem != -1 && formContacts.size() > selectedItem)
			formContacts.setSelectedIndex(selectedItem, true);
	}

	Vector printedTaskIDs = new Vector();

	void onCreateTask(int id, String theme, String description) {
		Task task = new Task(id, thisUserJID, thisContactJID, theme,
				description, 0);
		tasks.addElement(task);
	}

	void createTask(int id, String theme, String description) {
		onCreateTask(id, theme, description);
		printTasks(true, true);
	}

	void onUpdateTask(int id, String theme, String description, int fulfill) {
		for (int i = 0; i < tasks.size(); i++) {
			Task task = (Task) tasks.elementAt(i);
			if (id == task.id) {
				task.theme = theme;
				task.description = description;
				task.fulfilment = fulfill;
				printTasks(true, true);
			}
		}
	}

	void updateTask(int id, String theme, String description, int fulfill) {
		onUpdateTask(id, theme, description, fulfill);
	}

	void printTasks(boolean compleated, boolean notCompleated) {
		display.setCurrent(taskList);
		taskList.deleteAll();
		int k = 0;
		System.out.println("Tasks: " + tasks.size());
		System.out.println(thisUserJID + ":" + thisContactJID);
		for (int i = 0; i < tasks.size(); i = i + 1) {
			Task task = (Task) tasks.elementAt(i);
			System.out.println(task.owner + ":" + task.sender);
			if ((task.owner.equals(thisUserJID) && task.sender
					.equals(thisContactJID))
					|| (task.owner.equals(thisContactJID) && task.sender
							.equals(thisUserJID))) {
				if (task.fulfilment == 10) {
					if (compleated) {
						taskList.append("☺ " + task.theme, null);
						printedTaskIDs.addElement(new Integer(task.id));
						k = k + 1;
					}
				} else {
					if (notCompleated) {
						taskList.append(task.theme, null);
						printedTaskIDs.addElement(new Integer(task.id));
						k = k + 1;
					}
				}
			}
		}
	}

	void onDeleteTask() {
		int id = ((Integer) printedTaskIDs.elementAt(taskList
				.getSelectedIndex())).intValue();
		int index = 0;
		for (int i = 0; i < tasks.size(); i = i + 1) {
			Task task = (Task) tasks.elementAt(i);
			if (task.id == id) {
				index = i;
			}
		}
		tasks.removeElementAt(index);
		printTasks(true, true);
	}

	void deleteTask(int id) {
		int k = 0;
		for (int i = 0; i < tasks.size(); i++) {
			Task task = (Task) tasks.elementAt(i);
			if (id == task.id) {
				k = i;
			}
		}
		tasks.removeElementAt(k);
		printTasks(true, true);
	}

	int getTaskByID() {
		int id = ((Integer) printedTaskIDs.elementAt(taskList
				.getSelectedIndex())).intValue();
		int index;
		for (int i = 0; i < tasks.size(); i++) {
			Task task = (Task) tasks.elementAt(i);
			if (task.id == id) {
				index = i;
				return index;
			}
		}
		return -1;
	}

	void printComments() {
		thisTask.deleteAll();
		thisTask.append(gauge);
		gauge.setPreferredSize(200, 50);
		display.setCurrent(thisTask);
		Task task = (Task) tasks.elementAt(getTaskByID());
		gauge.setValue(task.fulfilment);
		if (task.sender == thisUserJID) {
			thisTask.append(topic2);
			topic2.setString(task.theme);
			thisTask.append(descript2);
			descript2.setString(task.description);
		} else {
			thisTask.setTitle(task.theme);
			thisTask.append("Описание:" + "\n");
			thisTask.append(task.description + "\n");
		}
		thisTask.append("Комментарии:" + "\n");
		for (int i = 0; i < comments.size(); i = i + 1) {
			Comment comment = (Comment) comments.elementAt(i);
			if (comment.task == task.id) {
				thisTask.append(comment.text + "\n");
			}
		}
	}

	void createComment(String text, int task) {
		comments.addElement(new Comment(task, text));
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

	void onCreateComment(String text, int task) {
		createComment(text, task);
		printComments();
	}

	protected void startApp() throws MIDletStateChangeException {
		onlineImg = loadImage("/online.png");
		offlineImg = loadImage("/offline.png");

		display = Display.getDisplay(this);

		// тестер класов
		tasks.addElement(new Task(0, "1a", "2b", "1aTo2B", "from1a", 0));
		tasks.addElement(new Task(1, "2b", "1a", "2bTo1a", "from2b", 4));
		tasks.addElement(new Task(2, "3c", "1a", "3cTo1a", "from3c", 6));
		tasks.addElement(new Task(3, "4d", "2b", "4dTo2b", "from4d", 10));

		// экран ввода пароля
		login.addCommand(exit);
		login.addCommand(select);
		login.setCommandListener(this);
		login.append(userJID);
		login.append(pass);
		login.append(serv);

		// главное меню
		main = new List("Главное меню", Choice.IMPLICIT);
		main.append("контакты", null);
		main.append("учетная запись", null);
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
			userJID.setString("aiv.tst@gmail.com");
		}
		if (getRecordText(2) != null) {
			pass.setString(getRecordText(2));
		} else {
			pass.setString("qwe12345");
		}
		if (getRecordText(3) != null) {
			serv.setString(getRecordText(3));
		} else {
			serv.setString("talk.google.com");
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
			System.out.println("Try to add");
			try {
				System.out.println(recordStore.addRecord(mass, 0, mass.length));
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
					.getString(), "5223", true);
			jxa.addListener(this);
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
				display.setCurrent(formContacts);
				printContacts();
				break;
			case 1:
				display.setCurrent(ychet);
				{

				}
				break;
			case 2:
				destroyApp(true);
				break;
			}
		}
	}

	public void taskListAction(Command c, Displayable d) {
		if (c == back) {
			display.setCurrent(formContacts);
		} else if (c == deleteThisTask) {
			onDeleteTask();
			printTasks(true, true);
		} else if (c == newTask) {
			display.setCurrent(formTask);
			topic.setString("");
		} else if (c == cSort) {
			display.setCurrent(sort);
		} else {
			int qq = taskList.getSelectedIndex();
			thisTaskID = ((Integer) printedTaskIDs.elementAt(qq)).intValue();
			String topictask = new String();
			topictask = taskList.getString(qq);
			display.setCurrent(thisTask);
			printComments();
			thisTask.setTitle(topictask);
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
			thisContactJID = (String) contacts.elementAt(formContacts
					.getSelectedIndex());
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
			String text = messagedisplay.getString();
			createComment(text, thisTaskID);
			display.setCurrent(thisTask);
			thisTask.append(text + "\n");
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
		} else if (c == save){
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
			onCreateTask(tasks.size(), strTopic, strDescript);
			display.setCurrent(taskList);
			printTasks(true, true);
		}
	}

	public void thisTaskAction(Command c, Displayable d) {
		if (c == back) {
			int i;
			for (i = 0; i < tasks.size(); i = i + 1) {
				Task task = (Task) tasks.elementAt(i);
				if (task.id == thisTaskID) {
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
		 */else if (display.getCurrent() == formTask) {
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
		try {
			jxa.getRoster();
		} catch (final IOException ex) {
			ex.printStackTrace();
		}
		display.setCurrent(main);
	}

	public void onAuthFailed(String message) {
		// TODO Auto-generated method stub
		System.out.println("Auth failed");
	}

	public void onConnFailed(String msg) {
		// TODO Auto-generated method stub
		System.out.println("Connection failed");
	}

	public void onContactEvent(final String jid, String name,
			final String group, final String subscription) {
		System.out.println("Contact event " + jid + "  " + name + 
				"  " + group + "  " + subscription);
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
		Alert alert = new Alert("Сообщение от " + from, body, null, AlertType.INFO);
		alert.setTimeout(Alert.FOREVER);
		Display.getDisplay(this).setCurrent(alert);
	}

	public void onStatusEvent(String jid, String show, String status) {
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
		/*
		 * thisTask = new Alert("Task " + task.sender, task.description, null,
		 * AlertType.INFO); thisTask.setTimeout(Alert.FOREVER);
		 * Display.getDisplay(this).setCurrent(thisTask);
		 */
	}
	
}

/*
 * aiv.tst@gmail.com qwe12345 talk.google.com
 */