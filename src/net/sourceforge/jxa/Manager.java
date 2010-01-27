package net.sourceforge.jxa;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

import net.sourceforge.jxa.XmppListener;
import net.sourceforge.jxa.packet.Packet;
import net.sourceforge.jxa.provider.Provider;
import net.sourceforge.jxa.provider.SkipProvider;

public class Manager extends Thread {
	public XmlReader reader;
	public XmlWriter writer;
	private Vector providers = new Vector();
	protected Vector listeners = new Vector();
	final private Provider defaultProvider = new Provider(null, null);
	final private Provider skipProvider = new SkipProvider(null, null);

	/**
	 * Add a {@link Provider} for parsing.
	 * 
	 * @param provider
	 */
	public void addProvider(final Provider provider) {
		if (!providers.contains(provider))
			providers.addElement(provider);
	}

	/**
	 * Remove a {@link Provider}.
	 * 
	 * @param provider
	 */
	public void removeProvider(final Provider provider) {
		providers.removeElement(provider);
	}
	
	/**
	 * Gets providers
	 */
	public Enumeration getProviders() {
		return providers.elements();
	}

	/**
	 * Add a {@link XmppListener} to listen for events.
	 * 
	 * @param xl
	 *            a XmppListener object
	 */
	public void addListener(final XmppListener xl) {
		if (!listeners.contains(xl))
			listeners.addElement(xl);
	}

	/**
	 * Remove a {@link XmppListener} from this class.
	 * 
	 * @param xl
	 *            a XmppListener object
	 */
	public void removeListener(final XmppListener xl) {
		listeners.removeElement(xl);
	}

	/**
	 * Parse incoming data using providers
	 * 
	 * @param providers
	 * @param skipUnknown
	 * @return
	 * @throws IOException
	 */
	public Packet parse(Enumeration providers, boolean skipUnknown) throws IOException {
		String elementName = reader.getName();
		String namespace = reader.getAttribute("xmlns");
		System.out.println(elementName + ":" + namespace);
		for (; providers.hasMoreElements();) {
			Provider provider = (Provider) providers.nextElement();
			if (provider.equals(elementName, namespace)) {
				return provider.parse(this);
			}
		}
		if (skipUnknown)
			return skipProvider.parse(this);
		else
			return defaultProvider.parse(this);
	}

	/**
	 * Parse incoming data using registered providers
	 * 
	 * @param skipUnknown skip unknown packets
	 * @return Packet object or null
	 * @throws IOException
	 */
	public Packet parse(boolean skipUnknown) throws IOException {
		return parse(providers.elements(), skipUnknown);
	}
	
	/**
	 * Calls event in all registered listeners.
	 * 
	 * @param provider
	 * @param packet
	 */
	public void event(Packet packet) {
		for (Enumeration e = listeners.elements(); e.hasMoreElements();) {
			XmppListener xl = (XmppListener) e.nextElement();
			xl.onEvent(packet);
		}
	}

}
