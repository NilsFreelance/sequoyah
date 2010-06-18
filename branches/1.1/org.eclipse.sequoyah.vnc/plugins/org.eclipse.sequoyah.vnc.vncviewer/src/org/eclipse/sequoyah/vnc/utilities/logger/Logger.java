/********************************************************************************
 * Copyright (c) 2007-2010 Motorola Inc. All rights reserved.
 * This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Initial Contributor:
 * Fabio Fantato (Motorola)
 *
 * Contributors:
 * Daniel Pastore (Eldorado) - [289870] Moving and renaming Tml to Sequoyah
 ********************************************************************************/

package org.eclipse.sequoyah.vnc.utilities.logger;

/**
* DESCRIPTION: 
* A Logger object is used to log messages for CDE Components.
*
* COLABORATORS: none
* 
* USAGE: 
* Used to register messages in some outputs like files or console.
*/
public class Logger
{
    private static ILogger log;

    static
    {
       // log = LoggerFactory.getLogger(LoggerConstants.LOG_SIMPLE, Logger.class);
    }

    /**
     * Configure a log to a specific class
     *
     * @param _class Class used to get log
     *
     * @return Ilogger instance
     */
    @SuppressWarnings("unchecked")
	public static ILogger log(Class _class)
    {
    	if (log==null) {
    		log = LoggerFactory.getLogger(LoggerConstants.LOG_SIMPLE, Logger.class);
    	}
        log.configureLogger(_class);

        return log;
    }

    /**

     *  Sets the current Logger.

     *  @param logger the logger  

     */

    public static void setLogger(ILogger logger){
    	log = logger;
    }

    
    /**
     * Configure log settings level and specific output, defined by logOutput id
     *
     * @param logOutput ID of logOutput
     * @param logMinLevel Level of Log
     * @param logFileName File name to write the log
     */
    public static void logConfig(int logOutput, String logMinLevel, String logFileName)
    {
        log = LoggerFactory.getLogger(LoggerConstants.LOG_SIMPLE, Logger.class, logOutput, logMinLevel,
                                      logFileName);
    }
}
