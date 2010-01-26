package org.jabber.task;

public class Comment {
	int task;
	String text = new String();
	
	public Comment(int task, String text){
		this.task = task;
		this.text = text;
	}
}
