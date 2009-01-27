/********************************************************************************
 * This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Initial Contributor:
 * Fabio Rigo
 *
 * Contributors:
 * Daniel Barboza Franco - Bug [233775] - Does not have a way to enter the session password for the vnc connection
 * Fabio Rigo - Bug [238191] - Enhance exception handling
 * Fabio Rigo (Eldorado Research Institute) - [246212] - Enhance encapsulation of protocol implementer
 ********************************************************************************/
package org.eclipse.tml.protocol.lib;

import java.io.DataInputStream;
import java.io.OutputStream;
import java.util.Map;

import org.eclipse.tml.protocol.lib.exceptions.ProtocolHandshakeException;

/**
 * DESCRIPTION: This interface describes the contract to be used by an object
 * which purpose is to define the initialization procedure of a protocol. <br>
 * 
 * RESPONSIBILITY: Define methods that shall be implemented by objects that
 * defines the initialization procedure of a protocol. At least one of the
 * initialization methods must be provided for the protocol to work.<br>
 * 
 * COLABORATORS: None.<br>
 * 
 * USAGE: The protocol handshake must be provided as a protocol start parameter.<br>
 */
public interface IProtocolHandshake {

	/**
	 * Performs protocol handshake at server side. If this protocol
	 * implementation does not include the server part, the method can be left
	 * blank.<br>
	 * <br>
	 * In this method, it is expected that each protocol class implement its
	 * initialization routine (including handshaking), until the normal message
	 * exchange phase is reached.
	 * 
	 * @param handle
	 *            A handle to identify the connection being made
	 * @param in
	 *            The input stream where to read data sent by the server
	 * @param out
	 *            The output stream where to write data to send to the server
	 * @param parameters
	 *            A Map with parameters other than host and port, for customization purposes. Accepts null if apply.            
	 * 
	 * @throws ProtocolHandshakeException
	 *             If during the initialization an error that prevents the
	 *             protocol to continue working is identified.
	 */
	void serverHandshaking(ProtocolHandle handle, DataInputStream in, OutputStream out, Map parameters)
			throws ProtocolHandshakeException;

	/**
	 * Performs protocol handshake at client side. If this protocol
	 * implementation does not include the client part, the method can be left
	 * blank.<br>
	 * <br>
	 * In this method, it is expected that each protocol class implement its
	 * initialization routine (including handshaking), until the normal message
	 * exchange phase is reached.
	 * 
	 * @param handle
	 *            A handle to identify the connection being made
	 * @param in
	 *            The input stream where to read data sent by the client
	 * @param out
	 *            The output stream where to write data to send to the client
	 * 
	 * @throws ProtocolHandshakeException
	 *             If during the initialization an error that prevents the
	 *             protocol to continue working is identified.
	 */
	void clientHandshaking(ProtocolHandle handle, DataInputStream in, OutputStream out, Map parameters)
			throws ProtocolHandshakeException;
}
