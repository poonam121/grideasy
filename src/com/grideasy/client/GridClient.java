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
package com.grideasy.client;

import java.io.IOException;
import java.net.Socket;

/**
 * This class holds a instance of client execution
 * A client is responsible by receiving execution info from the
 * server and return the results in a MapReduce behavior
 * 
 * @see This class has a static main method to initialize a instance of {@link GridClient} 
 * 
 * @author Felipe Santos
 * @email felchs@gmail.com
 */
public class GridClient
{
	/**
	 * Holds server info about nodes, host and ports
	 */
	private ClientHostProperties hostProperties = new ClientHostProperties();
	
	/**
	 * Holds a {@link Socket} connection
	 */
	private ClientSocketConnection socketConnection;
	
	/**
	 * Net discover of hosts and ports form client to server
	 */
	private NetDiscover netDiscover;
	
	/**
	 * Whether the client is active or not
	 */
	private boolean active = true;

	/**
	 * When the information between client and server could not
	 * be delivered, the server retries N times
	 * The maximum of retries is hold by this attribute MAX_WRONGPTRIES
	 * If the MAX_WRONGPTRIES == -1, it re-tries infinitely
	 */
	private final int MAX_WRONGPTRIES;
	
	/**
	 * Constructor initializing the MAX_WRONGPTRIES with -1,
	 * If the MAX_WRONGPTRIES == -1, it tries infinitely 
	 * @throws NetDiscoverException
	 */
	public GridClient() throws NetDiscoverException
	{
		this(-1);
	}

	/**
	 * Constructor passing max wrong tries
	 *  
	 * @param maxWrongTries the maximum retries when the message could not be delivered from client to sever
	 * @throws NetDiscoverException
	 */
	public GridClient(int maxWrongTries) throws NetDiscoverException
	{
		this.MAX_WRONGPTRIES = maxWrongTries;
		this.netDiscover = new NetDiscover(hostProperties.ip, hostProperties.port, hostProperties.port);
	}
	
	/**
	 * Gets whether this client is active or not
	 * @return whether this client is active or not
	 */
	public boolean isActive()
	{
		return active;
	}
	
	/**
	 * Sets whether this client is active or not
	 * @param active whether this client is active or not
	 */
	public void setActive(boolean active)
	{
		this.active = active;
	}
	
	/**
	 * Gets the {@link ClientProcessExecuter} from this {@link GridClient} object
	 * @return the {@link ClientProcessExecuter} from this {@link GridClient} object
	 */
	protected ClientProcessExecuter getExecuter()
	{
		return new ClientProcessExecuter();
	}
	
	/**
	 * Gets whether this {@link GridClient} is connected or not
	 * @return whether this {@link GridClient} is connected or not
	 */
	public boolean isConnected()
	{
		return socketConnection != null && socketConnection.isConnected();
	}
	
	/**
	 * Starts up the grid client sending initial info from the client
	 * This initializes the connection between client and server
	 */
	public void startUp()
	{
		int numWrongTries = 0;
		
		while (isActive() && (MAX_WRONGPTRIES < 0 || (numWrongTries < MAX_WRONGPTRIES)))
		{
			String ip = netDiscover.getIp();
			int port = netDiscover.getPort();

			socketConnection = new ClientSocketConnection(ip, port, getExecuter());

			try
			{
				socketConnection.run();
			} 
			catch (IOException e)
			{
				numWrongTries++;
			}
		}
		
		if (numWrongTries == MAX_WRONGPTRIES)
		{
			System.err.println("Exited because of too many wrong tries to connect on host: " + netDiscover.getIp() + ":" + netDiscover.getPort());
		}
	}
	
	/**
	 * Shuts down this {@link GridClient}
	 */
	public void shutdown()
	{
		setActive(false);
	}
	
	///////////////////////////////////////////////////////////////////////////
	
	public static void main(String[] args)
	{
		try
		{
			GridClient gridClient = new GridClient();
			gridClient.startUp();
		} catch (NetDiscoverException e)
		{
			e.printStackTrace();
		}
	}
}