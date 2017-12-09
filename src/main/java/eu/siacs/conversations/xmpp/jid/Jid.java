package eu.siacs.conversations.xmpp.jid;

import net.java.otr4j.session.SessionID;

public final class Jid {

	private final rocks.xmpp.addr.Jid inner;

	private Jid(rocks.xmpp.addr.Jid inner) {
		this.inner = inner;
	}

	public String getLocalpart() {
		return inner.getLocal();
	}


	public String getDomainpart() {
		return inner.getDomain();
	}

	public String getResourcepart() {
		return inner.getResource();
	}

	public static Jid fromSessionID(final SessionID id) throws InvalidJidException{
		if (id.getUserID().isEmpty()) {
			return Jid.fromString(id.getAccountID());
		} else {
			return Jid.fromString(id.getAccountID()+"/"+id.getUserID());
		}
	}

	public static Jid fromString(final String jid) throws InvalidJidException {
		try {
			return new Jid(rocks.xmpp.addr.Jid.of(jid));
		} catch (IllegalArgumentException e) {
			throw new InvalidJidException(e);
		}
	}

	public static Jid fromString(final String jid, final boolean safe) throws InvalidJidException {
		return fromString(jid);
	}

	public static Jid fromParts(final String localpart,
			final String domainpart,
			final String resourcepart) throws InvalidJidException {
		try {
			return new Jid(rocks.xmpp.addr.Jid.of(localpart,domainpart,resourcepart));
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException(e);
		}
	}


	public Jid toBareJid() {
		return new Jid(inner.asBareJid());
	}

	public Jid toDomainJid() {
		return new Jid(rocks.xmpp.addr.Jid.ofDomain(inner.getDomain()));
	}

	@Override
	public String toString() {
		return inner.toString();
	}

	public String toPreppedString() {
		return inner.toString();
	}

	@Override
	public boolean equals(final Object o) {
		if (o instanceof Jid) {
			return this.inner.equals(((Jid) o).inner);
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return inner.hashCode();
	}

	public boolean hasLocalpart() {
		return inner.getLocal() != null;
	}

	public boolean isBareJid() {
		return inner.isBareJid();
	}

	public boolean isDomainJid() {
		return inner.getLocal() == null;
	}
}
