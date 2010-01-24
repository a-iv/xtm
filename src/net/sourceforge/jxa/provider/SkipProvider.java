package net.sourceforge.jxa.provider;

import java.io.IOException;

import net.sourceforge.jxa.Manager;
import net.sourceforge.jxa.XmlReader;
import net.sourceforge.jxa.packet.Packet;

public class SkipProvider extends Provider {
	public SkipProvider(String elementName, String namespace) {
		super(elementName, namespace);
	}

	public SkipProvider(String elementName, String namespace, boolean makeEvent) {
		super(elementName, namespace, makeEvent);
	}

	public Packet parse(Manager manager) throws IOException {
		System.out.println("Skip: " + manager.reader.getName());
		while (true) {
			int type = manager.reader.next();
			if (type == XmlReader.END_TAG || type == XmlReader.END_DOCUMENT)
				break;
			if (type == XmlReader.START_TAG)
				parse(manager);
		}
		return null;
	}
}
