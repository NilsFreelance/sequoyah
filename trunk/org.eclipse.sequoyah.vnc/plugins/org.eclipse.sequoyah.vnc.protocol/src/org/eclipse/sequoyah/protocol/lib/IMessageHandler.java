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
 ********************************************************************************/
package org.eclipse.tml.protocol.lib;

import org.eclipse.tml.protocol.lib.exceptions.MessageHandleException;

/**
 * DESCRIPTION: This interface describes the contract to be used by an object
 * which purpose is to handle a protocol message. <br>
 * 
 * RESPONSIBILITY: Define methods that shall be implemented by objects that
 * handles a protocol message.<br>
 * 
 * COLABORATORS: None.<br>
 * 
 * USAGE: The message contributors must provide a handler to the message, which
 * must implement this interface.<br>
 * 
 */
public interface IMessageHandler {

	/**
	 * Handles a protocol message after it has been completely received and
	 * parsed.
	 * 
	 * @param protocolImplementer
	 *            The message owner protocol implementer.
	 * @param message
	 *            The object that contains all the message parsed fields.
	 * 
	 * @return If a message should be sent in response to the handled one,
	 *         return the response message. If not, return <code>null</code>.
	 * 
	 * @throws MessageHandleException
	 *             If the message cannot be handled.
	 */
	ProtocolMessage handleMessage(IProtocolImplementer protocolImplementer,
			ProtocolMessage message) throws MessageHandleException;
}
