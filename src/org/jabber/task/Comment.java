package org.jabber.task;

public class Comment {
	public int task;
	public String text = new String();
	public String sender = new String();
	public int id;
	public Comment(int task, String text, String sender, int id){
		this.task = task;
		this.text = text;
		this.sender = sender;
		this.id = id;
	}
}
