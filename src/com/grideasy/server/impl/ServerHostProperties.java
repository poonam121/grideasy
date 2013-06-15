/*
 * This source file is part of Grideasy
 * For the latest info, see https://code.google.com/p/grideasy/
 * 
 * Grideasy is free software: you can redistribute it
 * and/or modify it under the terms of the MIT License.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.grideasy.server.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * This class has the server info about nodes, host and ports
 * 
 * @author Felipe Santos
 * @email felchs@gmail.com
 *
 */
public class ServerHostProperties
{
	/**
	 * Singleton instance
	 */
	private static ServerHostProperties instance;
	
	/**
	 * Gets the singleton instance
	 * @return the singleton instance
	 */
	public static ServerHostProperties getInstance()
	{
		if (instance == null)
		{
			instance = new ServerHostProperties();
		}
		
		return instance;
	}
	
	///////////////////////////////////////////////////////////////////////////
	
	/**
	 * The port which the client {@link GridNode}'s connect to
	 */
	private int port;
	
	/**
	 * Number of external nodes connected to the Server
	 */
	private int numExternalNodes;
	
	/**
	 * Number of local nodes connected to the server
	 */
	private int numLocalNodes;
	
	/**
	 * The minimum number of nodes that must be
	 * initialized to start the execution of {@link GridTask}'s
	 */
	private int minNodesToStart;
	
	/**
	 * The number of accepted connections to the server
	 */
	private int numAcceptedConnections;
	
	/**
	 * Constructor: loads local pre-defined properties  
	 */
	private ServerHostProperties()
	{
		Properties prop = new Properties();
		
		try
		{
			File file = new File("config/gridservermapproperties");
			InputStream in = new FileInputStream(file);
			prop.load(in);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		port = Integer.parseInt(prop.getProperty("port"));
		numExternalNodes = Integer.parseInt(prop.getProperty("numExternalNodes"));
		numLocalNodes = Integer.parseInt(prop.getProperty("numLocalNodes"));
		minNodesToStart = Integer.parseInt(prop.getProperty("minNodesToStart"));
	}
	
	/**
	 * Gets the number of external nodes connected to the server
	 * @return the number of external nodes connected to this server
	 */
	public int getNumExternalNodes()
	{
		return numExternalNodes;
	}
	
	/**
	 * Gets the number of local nodes connected to the server
	 * @return the number of local nodes connected to the server
	 */
	public int getNumLocalNodes()
	{
		return numLocalNodes;
	}
	
	/**
	 * Gets the total number of nodes connected
	 * @return the total number of nodes connected
	 */
	public int getTotalNodes()
	{
		return getNumLocalNodes() + getNumExternalNodes();
	}
	
	/**
	 * Gets the port which this server opens for client's {@link GridNode}'s connection
	 * @return
	 */
	public int getPort()
	{
		return port;
	}

	/**
	 * The minimum number of nodes that must be
	 * initialized to start the execution of {@link GridTask}'s 
	 * @return
	 */
	public int getMinNodesToStart()
	{
		return minNodesToStart;
	}
	
	/**
	 * Gets the number of clients connected to the server
	 * @return the number of clients connected to the server
	 */
	public int getNumClientsConnected()
	{
		return numAcceptedConnections;
	}

	/**
	 * Notifies when a client is connected incrementing 
	 * the number of accepted connections
	 */
	public void onClientConnected()
	{
		numAcceptedConnections++;
	}
	
	/**
	 * Notifies when a client disconnect decrementing
	 * the number of disconnected connections
	 * @param port
	 */
	public void onClientDisconnected(int port)
	{
		numAcceptedConnections--;
	}
}