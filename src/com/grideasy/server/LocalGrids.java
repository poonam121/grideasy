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
package com.grideasy.server;

import java.util.Iterator;
import java.util.Vector;

import com.grideasy.client.GridClient;
import com.grideasy.client.NetDiscoverException;

/**
 * Gather local {@link GridNode}'s instances
 * 
 * @author Felipe Santos
 * @email felchs@gmail.com
 *
 */
public class LocalGrids
{
	/**
	 * Singleton instance
	 */
	private static LocalGrids instance;
	
	/**
	 * Gets the singleton instance of {@link LocalGrids}
	 * @return the singleton instance of {@link LocalGrids}
	 */
	public static LocalGrids getInstance()
	{
		if (instance == null)
		{
			instance = new LocalGrids();
		}
		return instance;
	}
	
	///////////////////////////////////////////////////////////////////////////
	
	/**
	 * A list of {@link GridClient}'s
	 */
	private Vector<GridClient> localGridClientsList = new Vector<GridClient>();
	
	/**
	 * Private constructor for singleton behavior
	 */
	private LocalGrids()
	{
	}
	
	/**
	 * Initializes a number of local {@link GridNode}'s
	 * @param numNodes num of local {@link GridNode}'s to be initialized
	 */
	public void initNewLocalGridNode(int numNodes)
	{
		for (int i = 0; i < numNodes; i++)
		{
			new Thread()
			{
				public void run()
				{
					GridClient gridClient;
					try
					{
						gridClient = new GridClient();
						localGridClientsList.add(gridClient);
						gridClient.startUp();
					}
					catch (NetDiscoverException e)
					{
						e.printStackTrace();
					}
				}
			}.start();
		}
		
		int numInitialized = 0;
		while (numInitialized < numNodes)
		{
			numInitialized = 0;
			for (int i = 0; i < localGridClientsList.size(); i++)
			{
				GridClient gridClient = localGridClientsList.get(i);
				if (!gridClient.isConnected())
				{
					break;
				}
				else
				{
					numInitialized++;
				}
			}
			
			Thread.yield();
		}
	}
	
	/**
	 * Force all local {@link GridClient}'s to be shutdown
	 */
	public void shutdownAllNodes()
	{
		Iterator<GridClient> it = localGridClientsList.iterator();
		while (it.hasNext())
		{
			GridClient gridClient = it.next();
			gridClient.shutdown();
		}
	}
}
