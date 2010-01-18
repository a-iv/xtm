package org.jabber.task;

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

public class SampleMIDlet extends MIDlet implements CommandListener {
	int k = 0;

	String recordStoreName = "user1";
	Display display;
	Form login = new Form("Введите пароль");
	List main;
	List chat = new List("", Choice.IMPLICIT);
	List formContacts;
	Form contactinfo = new Form("инфо о контакте");
	Form ychet = new Form("Учетная запись");
	Form todeletec = new Form("");
	TextBox messagedisplay;
	Form connection = new Form("");
	Form newContactSet = new Form("");
	Form formTask = new Form("Задание");
	Form thisTask = new Form("Задание");
	RecordStore recordStore = null;
	List sort;

	// 1 строка - имя
	// 2 строка - работа
	// 3 строка - пароль

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
	String State;

	public class User {
		String jid = new String();
		String password = new String();
		String name = new String();
		String post = new String();
	}

	User[] users = new User[32];
	int usersCount = 4;

	User getUserByJID(String jid) {
		for (int i = 0; i < usersCount; i++) {
			if (users[i].jid == jid)
				return users[i];
		}
		return null;
	}

	public class Contact {
		String owner = new String();
		String jid = new String();
		String name = new String();
		String post = new String();
	}

	Contact[] contacts = new Contact[32];
	int contactsCount = 6;

	Task[] tasks = new Task[200];
	int tasksCount = 4;

	Task getTaskByID(int id) {
		for (int i = 0; i < tasksCount; i++) {
			if (tasks[i].id == id)
				return tasks[i];
		}
		return null;
	}

	Comment[] comments = new Comment[2500];
	int commentsCount = 0;

	String thisUserJID = new String();
	String thisContactJID = new String();
	int thisTaskID;

	String[] printedContactIDs = new String[200];

	void printContacts() {
		formContacts.deleteAll();
		int k = 0;
		for (int i = 0; i < contactsCount; i++)
			if (contacts[i].owner == thisUserJID) {
				formContacts.append(contacts[i].name, null);
				printedContactIDs[k] = contacts[i].jid;
				k++;
			}
	}

	void onCreateContact(String jid, String name, String post) {
		contacts[contactsCount] = new Contact();
		contacts[contactsCount].jid = jid;
		contacts[contactsCount].name = name;
		contacts[contactsCount].post = post;
	}

	void createContact(String jid, String name, String post) {
		onCreateContact(jid, name, post);
		printContacts();
	}

	void deleteContact(String id) {
		int index = 0;
		for (int i = 0; i < contactsCount; i = i + 1) {
			if (contacts[i].jid == id) {
				index = i;
			}
		}
		for (int i = index; i < contactsCount; i++) {
			contacts[i] = contacts[i + 1];
		}
		contactsCount = contactsCount - 1;
		printContacts();
	}

	void onDeleteContact(String id) {
		deleteContact(id);
	}

	int[] printedTaskIDs = new int[200];

	void onCreateTask(int id, String theme, String description) {
		tasks[tasksCount] = new Task(id, thisUserJID, thisContactJID, theme,
				description, 0);
		tasksCount = tasksCount + 1;
	}

	void createTask(int id, String theme, String description) {
		onCreateTask(id, theme, description);
		printTasks(true, true);
	}

	void onUpdateTask(int id, String theme, String description, int fulfill) {
		for (int i = 0; i < tasksCount; i++) {
			if (id == tasks[i].id) {
				tasks[i].theme = theme;
				tasks[i].description = description;
				tasks[i].fulfilment = fulfill;
				printTasks(true, true);
			}
		}
	}

	void updateTask(int id, String theme, String description, int fulfill) {
		onUpdateTask(id, theme, description, fulfill);
	}

	void printTasks(boolean compleated, boolean notCompleated) {
		display.setCurrent(chat);
		chat.deleteAll();
		int k = 0;
		for (int i = 0; i < tasksCount; i = i + 1) {
			if ((tasks[i].owner == thisUserJID && tasks[i].sender == thisContactJID)
					|| (tasks[i].owner == thisContactJID && tasks[i].sender == thisUserJID)) {
				if (tasks[i].fulfilment == 10) {
					if (compleated) {
						chat.append("☺ " + tasks[i].theme, null);
						printedTaskIDs[k] = tasks[i].id;
						k = k + 1;
					}
				} else {
					if (notCompleated) {
						chat.append(tasks[i].theme, null);
						printedTaskIDs[k] = tasks[i].id;
						k = k + 1;
					}
				}
			}
		}
	}

	void onDeleteTask() {
		int id = printedTaskIDs[chat.getSelectedIndex()];
		int index = 0;
		for (int i = 0; i < tasksCount; i = i + 1) {
			if (tasks[i].id == id) {
				index = i;
			}
		}
		for (int i = index; i < tasksCount; i = i + 1) {
			tasks[i] = tasks[i + 1];
		}
		tasksCount = tasksCount - 1;
	}

	void deleteTask(int id) {
		int k = 0;
		for (int i = 0; i < tasksCount; i++) {
			if (id == tasks[i].id) {
				k = i;
			}
		}
		for (int i = k; i < tasksCount; i++) {
			tasks[i] = tasks[i + 1];
		}
		tasksCount = tasksCount - 1;
	}

	void printComments() {
		thisTask.deleteAll();
		thisTask.append(gauge);
		gauge.setPreferredSize(200, 50);
		display.setCurrent(thisTask);
		int id = printedTaskIDs[chat.getSelectedIndex()];
		Task task = getTaskByID(id);
		gauge.setValue(task.fulfilment);
		if (tasks[thisTaskID].sender == thisUserJID) {
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
		for (int i = 0; i < commentsCount; i = i + 1) {
			if (comments[i].task == task.id) {
				thisTask.append(comments[i].text + "\n");
			}
		}
	}

	void createComment(String text, int task) {
		comments[commentsCount] = new Comment();
		comments[commentsCount].task = task;
		comments[commentsCount].text = text;
		commentsCount = commentsCount + 1;
	}

	void onCreateComment(String text, int task) {
		createComment(text, task);
		printComments();
	}

	protected void destroyApp(boolean unconditional) {
		try {
			recordStore.closeRecordStore();
		} catch (RecordStoreNotOpenException e) {
			// уже закрыта
		} catch (RecordStoreException e) {
			// какие-то другие ошибки
		}
		notifyDestroyed();
	}

	protected void pauseApp() {
		notifyPaused();
	}

	protected void startApp() throws MIDletStateChangeException {
		display = Display.getDisplay(this);

		// тестер класов
		users[0] = new User();
		users[0].name = "1";
		users[0].jid = "1a";
		users[0].password = "a1";
		users[0].post = "aa";
		users[1] = new User();
		users[1].name = "2";
		users[1].jid = "2b";
		users[1].password = "b2";
		users[1].post = "bb";
		users[2] = new User();
		users[2].name = "3";
		users[2].jid = "3c";
		users[2].password = "c3";
		users[2].post = "cc";
		users[3] = new User();
		users[3].name = "4";
		users[3].jid = "4d";
		users[3].password = "d4";
		users[3].post = "dd";
		contacts[0] = new Contact();
		contacts[0].owner = "1a";
		contacts[0].jid = "2b";
		contacts[0].name = "2";
		contacts[0].post = "bb";
		contacts[1] = new Contact();
		contacts[1].owner = "1a";
		contacts[1].jid = "3c";
		contacts[1].name = "3";
		contacts[1].post = "cc";
		contacts[2] = new Contact();
		contacts[2].owner = "2b";
		contacts[2].jid = "1a";
		contacts[2].name = "1";
		contacts[2].post = "aa";
		contacts[3] = new Contact();
		contacts[3].owner = "2b";
		contacts[3].jid = "4d";
		contacts[3].name = "4";
		contacts[3].post = "dd";
		contacts[4] = new Contact();
		contacts[4].owner = "3c";
		contacts[4].jid = "1a";
		contacts[4].name = "1";
		contacts[4].post = "aa";
		contacts[5] = new Contact();
		contacts[5].owner = "4d";
		contacts[5].jid = "2b";
		contacts[5].name = "2";
		contacts[5].post = "bb";
		tasks[0] = new Task(0, "1a", "2b", "1aTo2B", "from1a", 0);
		tasks[1] = new Task(1, "2b", "1a", "2bTo1a", "from2b", 4);
		tasks[2] = new Task(2, "3c", "1a", "3cTo1a", "from3c", 6);
		tasks[3] = new Task(3, "4d", "2b", "4dTo2b", "from4d", 10);

		// экран ввода пароля
		login.addCommand(exit);
		login.addCommand(select);
		login.setCommandListener(this);
		login.append(userJID);
		login.append(pass);

		// главное меню
		main = new List("Главное меню", Choice.IMPLICIT);
		main.append("контакты", null);
		main.append("учетная запись", null);
		main.append("выход", null);
		main.setCommandListener(this);
		main.addCommand(exit);
		main.addCommand(select);

		// форма чат
		chat.setCommandListener(this);
		chat.addCommand(back);
		chat.addCommand(select);
		chat.addCommand(newTask);
		chat.addCommand(deleteThisTask);
		chat.addCommand(cSort);

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

		// окно подтверждения удаления контакта
		todeletec.append("Вы уверены, что хотите удалить контакт?");
		todeletec.setCommandListener(this);
		todeletec.addCommand(no);
		todeletec.addCommand(yes);

		// подключение
		connection.setCommandListener(this);
		connection.append("подключение к серверу");
		connection.addCommand(select);
		connection.addCommand(exit);

		// инфо о контакте
		contactinfo.setCommandListener(this);
		contactinfo.addCommand(back);

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
				// не существует
			} catch (RecordStoreException e2) {
				// какие-то другие ошибки
			}
			// не существует
		} catch (RecordStoreException e) {
			// какие-то другие ошибки
		}
		if (getRecordText(1) != null) {
			userJID.setString(getRecordText(1));
		}
		if (getRecordText(2) != null) {
			pass.setString(getRecordText(2));
		}

	}

	String getRecordText(int id) {
		String str = "";
		try {
			byte[] data = recordStore.getRecord(id);
			if (data != null)
				str = new String(data);
		} catch (ArrayIndexOutOfBoundsException e) {
			// запись не помещается в переданный массив
		} catch (InvalidRecordIDException e) {
			// записи с таким ID не существует
		} catch (RecordStoreNotOpenException e) {
			// record store была закрыта
		} catch (RecordStoreException e) {
			// другие ошибки
		}
		return str;
	}

	void setRecordText(int id, byte[] mass) {
		try {
			recordStore.setRecord(id, mass, 0, mass.length);
		} catch (ArrayIndexOutOfBoundsException e) {
			// запись не помещается в переданный массив
		} catch (InvalidRecordIDException e) {
			// записи с таким ID не существует
			try {
				recordStore.addRecord(mass, 0, mass.length);
			} catch (RecordStoreFullException e2) {
				// данные не умещаются в памяти
			} catch (RecordStoreNotOpenException e2) {
				// record store была закрыта
			} catch (RecordStoreException e2) {
				// другие ошибки
			}
		} catch (RecordStoreNotOpenException e) {
			// record store была закрыта
		} catch (RecordStoreException e) {
			// другие ошибки
		}
	}

	public void commandAction(Command c, Displayable d) {
		if (display.getCurrent() == login) {
			// Форма входа
			if (c == select) {
				String pw = new String();
				int k = 0;
				for (int i = 0; i < usersCount; i = i + 1) {
					if (userJID.getString().equals(users[i].jid)) {
						pw = users[i].password;
						thisUserJID = users[i].jid;
					}
				}
				if (pw.equals(pass.getString())) {
					setRecordText(1, userJID.getString().getBytes());
					setRecordText(2, pass.getString().getBytes());
					display.setCurrent(connection);
				}
				if (!pw.equals(pass.getString())) {
					login.append("Неверный парроль");
					k = k + 1;
				}
				if (!pw.equals(pass.getString()) && k == 3) {
					destroyApp(true);
				}
			} else if (c == exit) {
				destroyApp(true);
			}
		} else if (display.getCurrent() == connection) {
			// подключение
			if (c == select) {
				display.setCurrent(main);
			} else if (c == exit) {
				destroyApp(true);
			}
		} else if (display.getCurrent() == main) {
			// Главная форма
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
					for (int i = 0; i < usersCount; i++) {
						if (thisUserJID == users[i].jid) {
							name.setString(users[i].name);
							work.setString(users[i].post);
							password.setString(users[i].password);
						}
					}
					break;
				case 2:
					destroyApp(true);
					break;
				}
			}
		} else if (display.getCurrent() == chat) {
			// форма чат
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
				int qq = chat.getSelectedIndex();
				thisTaskID = printedTaskIDs[qq];
				String topictask = new String();
				topictask = chat.getString(qq);
				display.setCurrent(thisTask);
				printComments();
				thisTask.setTitle(topictask);
			}
		} else if (display.getCurrent() == formContacts) {
			// форма контактов
			if (c == back) {
				display.setCurrent(main);
			} else if (c == info) {
				int a;
				a = formContacts.getSelectedIndex();
				display.setCurrent(contactinfo);
				contactinfo.deleteAll();
				String s = printedContactIDs[a];
				for (int i = 0; i < 32; i++) {
					if (users[i].jid.equals(s)) {
						contactinfo.append("Имя:  " + users[i].name);
						contactinfo.append("Должность:  " + users[i].post);
					}
				}
			} else if (c == open) {
				thisContactJID = printedContactIDs[formContacts
						.getSelectedIndex()];
				String contname = formContacts.getString(formContacts
						.getSelectedIndex());
				chat.setTitle(contname);
				display.setCurrent(chat);
				printTasks(true, true);
			} else if (c == newc) {
				display.setCurrent(newContactSet);
				newContactJID.setString("");
			} else if (c == deletec) {
				deleteContact(printedContactIDs[formContacts.getSelectedIndex()]);
			} else if (c == newTask) {
				thisContactJID = printedContactIDs[formContacts
						.getSelectedIndex()];
				display.setCurrent(formTask);
				topic.setString("");
			} else {
				thisContactJID = printedContactIDs[formContacts
						.getSelectedIndex()];
				String contname = formContacts.getString(formContacts
						.getSelectedIndex());
				chat.setTitle(contname);
				display.setCurrent(chat);
				printTasks(true, true);
			}
		} else if (display.getCurrent() == newContactSet) {
			if (c == cancel) {
				display.setCurrent(formContacts);
				printContacts();
			} else if (c == ok && !newContactJID.getString().equals("")) {
				for (int i = 0; i < usersCount; i++) {
					if (newContactJID.getString().equals(users[i].jid)) {
						onCreateContact(users[i].jid, users[i].name,
								users[i].post);
					}
				}
				display.setCurrent(formContacts);
				printContacts();
			}
		} else if (display.getCurrent() == messagedisplay) {
			// окно ввода сообщений
			if (c == back) {
				display.setCurrent(thisTask);
			} else if (c == sent) {
				String text = messagedisplay.getString();
				createComment(text, thisTaskID);
				display.setCurrent(thisTask);
				thisTask.append(text + "\n");
			}
		} else if (display.getCurrent() == ychet) {
			// окно настройки учетной записи
			if (c == back) {
				display.setCurrent(main);
			} else if (c == ok && !name.getString().equals("")) {
				String s = name.getString();
				/*
				 * byte[] data = s.getBytes(); setRecordText(1,data);
				 */
				String s2 = work.getString();
				/*
				 * byte[] data2 = s2.getBytes(); setRecordText(2,data2);/
				 */
				String s3 = password.getString();
				/*
				 * byte[] data3 = s3.getBytes(); setRecordText(3, data3);
				 */
				for (int i = 0; i < 32; i++) {
					if (users[i].jid == thisUserJID) {
						users[i].name = s;
						users[i].password = s3;
						users[i].post = s2;
					}
				}
				display.setCurrent(main);
			}
		} else if (display.getCurrent() == todeletec) {
			if (c == no) {
				display.setCurrent(formContacts);
			}
		} else if (display.getCurrent() == contactinfo) {
			if (c == back) {
				display.setCurrent(formContacts);
			}
		} /*
		 * else if (display.getCurrent() == ycheterror) { if (c == exit) {
		 * destroyApp(true); } else if (c == ok) { display.setCurrent(ychet2); }
		 * }
		 *//*
			 * else if (display.getCurrent() == ychet2) { if (c == exit) {
			 * destroyApp(true); } else if ((c == ok) &&
			 * !name2.getString().equals("") && !work2.getString().equals("") )
			 * { String s = name2.getString(); byte[] data = s.getBytes();
			 * setRecordText(1, data); String s2 = work2.getString(); byte[]
			 * data2 = s2.getBytes(); setRecordText(2, data2); String s3 =
			 * password2.getString(); byte[] data3 = s3.getBytes();
			 * setRecordText(3, data3); display.setCurrent(connection); } else
			 * if ((c == ok) && (name2.getString().equals("") ||
			 * work2.getString().equals(""))) {
			 * ychet2.append("Заполните учетную запись полностью!"); } }
			 */else if (display.getCurrent() == formTask) {
			if (c == cancel) {
				display.setCurrent(chat);
			} else if (c == ok && !topic.equals("")) {
				String strTopic = topic.getString();
				String strDescript = descript.getString();
				onCreateTask(tasksCount, strTopic, strDescript);
				display.setCurrent(chat);
				printTasks(true, true);
			}
		} else if (display.getCurrent() == thisTask) {
			if (c == back) {
				int i;
				for (i = 0; i < tasksCount; i = i + 1) {
					if (tasks[i].id == thisTaskID) {
						tasks[i].fulfilment = gauge.getValue();
					}
				}
				display.setCurrent(chat);
				printTasks(true, true);
			} else if (c == message) {
				display.setCurrent(messagedisplay);
				messagedisplay.setString("");
			} else if (c == fulfil) {
				gauge.setValue(10);
			}
		} else if (display.getCurrent() == sort) {
			if (c == back) {
				display.setCurrent(chat);
			} else {
				switch (sort.getSelectedIndex()) {
				case 0:
					display.setCurrent(chat);
					printTasks(true, false);
					break;
				case 1:
					display.setCurrent(chat);
					printTasks(false, true);
					break;
				case 2:
					display.setCurrent(chat);
					printTasks(true, true);
					break;
				}
			}
		}
	}
}
