/********************************************************************************
 * This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Initial Contributor:
 * Fabio Rigo
 *
 * Contributors:
 * Fabio Rigo - Bug [238191] - Enhance exception handling
 * Daniel Barboza Franco (Eldorado Research Institute) - Bug [233064] - Add reconnection mechanism to avoid lose connection with the protocol
 ********************************************************************************/
package org.eclipse.tml.protocol.lib.internal.model;

import java.util.Collection;
import java.util.Map;

import org.eclipse.tml.protocol.lib.IProtocolExceptionHandler;
import org.eclipse.tml.protocol.lib.internal.engine.ProtocolEngine;
import org.eclipse.tml.protocol.lib.msgdef.ProtocolMsgDefinition;

/**
 * DESCRIPTION: This class implements a factory for protocol engines.<br>
 * 
 * RESPONSIBILITY: Create different protocol engine instances that use the same
 * set of messages and orientation. This is intended to be used by server
 * protocols, for quickly create new protocol engines to communicate to each
 * incoming client connection.<br>
 * 
 * COLABORATORS: ServerModel: Uses this factory to create protocol engines to
 * communicate with new clients.<br>
 * 
 * USAGE: This class is intended to be used by the protocol framework only.
 * 
 */
public class ServerProtocolEngineFactory {

	/**
	 * A map containing all messages that belong to the protocol. The message
	 * code as key
	 */
	private Map<Long, ProtocolMsgDefinition> allMessages;

	/**
	 * A collection containing the message ids of all incoming messages. The
	 * message ids much match the id field in a ProtocolMsgDefinition object
	 * from the allMessages map
	 */
	private Collection<String> incomingMessages;

	/**
	 * A collection containing the message ids of all outgoing messages. The
	 * message ids much match the id field in a ProtocolMsgDefinition object
	 * from the allMessages map
	 */
	private Collection<String> outgoingMessages;

	/**
	 * The handler that was registered with the server protocol at the moment it
	 * started listening to port. When constructing an engine object, it will be
	 * provided for exception handling delegation.
	 */
	private IProtocolExceptionHandler exceptionHandler;

	/**
	 * True if the protocol is big endian, false if little endian
	 */
	private boolean isBigEndianProtocol;

	/**
	 * Constructor. Collects data to use at each protocol engine
	 * 
	 * @param allMessages
	 *            A map containing all messages that belong to the protocol. The
	 *            message code as key
	 * @param incomingMessages
	 *            A collection containing the message ids of all incoming
	 *            messages. The message ids much match the id field in a
	 *            ProtocolMsgDefinition object from the allMessages map
	 * @param outgoingMessages
	 *            A collection containing the message ids of all outgoing
	 *            messages. The message ids much match the id field in a
	 *            ProtocolMsgDefinition object from the allMessages map
	 * @param isBigEndianProtocol
	 *            True if the protocol is big endian, false if little endian
	 */
	public ServerProtocolEngineFactory(
			Map<Long, ProtocolMsgDefinition> allMessages,
			Collection<String> incomingMessages,
			Collection<String> outgoingMessages,
			IProtocolExceptionHandler exceptionHandler,
			boolean isBigEndianProtocol) {
		this.allMessages = allMessages;
		this.incomingMessages = incomingMessages;
		this.outgoingMessages = outgoingMessages;
		this.exceptionHandler = exceptionHandler;
		this.isBigEndianProtocol = isBigEndianProtocol;
	}

	/**
	 * Creates a new protocol engine, based on the factory configuration
	 * 
	 * @return A new protocol engine to be used to connect to a client
	 */
	public ProtocolEngine getServerProtocolEngine() {
		ProtocolEngine eng = new ProtocolEngine(allMessages, incomingMessages,
				outgoingMessages, exceptionHandler, isBigEndianProtocol,0);
		return eng;
	}

	/*
	 * Getters section
	 */
	public Map<Long, ProtocolMsgDefinition> getAllMessages() {
		return allMessages;
	}

	public Collection<String> getIncomingMessages() {
		return incomingMessages;
	}

	public Collection<String> getOutgoingMessages() {
		return outgoingMessages;
	}

	public IProtocolExceptionHandler getExceptionHandler() {
		return exceptionHandler;
	}

	public boolean isBigEndianProtocol() {
		return isBigEndianProtocol;
	}
}
