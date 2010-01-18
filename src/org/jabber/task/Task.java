package org.jabber.task;

public class Task {
	public int id;
	public String sender = new String();
	public String owner = new String();
	public String theme = new String();
	public String description = new String();
	public int fulfilment;

	public Task() {
	}

	public Task(int id, String sender, String owner, String theme,
			String description, int fulfilment) {
		this.id = id;
		this.sender = sender;
		this.owner = owner;
		this.theme = theme;
		this.description = description;
		this.fulfilment = fulfilment;
	}
}
