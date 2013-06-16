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

import java.io.IOException;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The Grid Server is a collector of {@link GridNode}'s
 * The execution of grid nodes is spread through the {@link GridServer} 
 * 
 * @author Felipe Santos
 * @email felchs@gmail.com
 */
public class GridServer
{
	/**
	 * A list of {@link GridNode}'s connected to this {@link GridServer}
	 */
	private List<GridNode> gridNodeList = new Vector<GridNode>();
	
	/**
	 * The {@link ExecutorService} that is responsible for the
	 * spread of works 
	 */
	private final ExecutorService executor;

	/**
	 * Constructor of {@link GridServer}
	 * Instantiate the {@link GridServer}{@link #executor} with initial num of threads
	 * equal to the local machine processors.
	 * GridNodes maintaining
	 * @throws IOException
	 */
	public GridServer() throws IOException
	{
		int numThreads = Runtime.getRuntime().availableProcessors();
		this.executor = Executors.newFixedThreadPool(numThreads);
		
		setupGridNodes();
	}
	
	/**
	 * Returns a list of {@link GridNode}'s
	 * @return a list of {@link GridNode}'s
	 */
	public List<GridNode> getGridNodeList()
	{
		return gridNodeList;
	}

	/**
	 * Returns a list of {@link GridNode}'s that are now connected to this {@link GridServer}
	 * @return a list of {@link GridNode}'s that are now connected to this {@link GridServer}
	 */
	public List<GridNode> getConnectedNodeList()
	{
		List<GridNode> gridNodesToRet = new Vector<GridNode>();
		
		int sz = gridNodeList.size();
		for (int i = 0; i < sz; i++)
		{
			GridNode gridNode = gridNodeList.get(i);
			if (gridNode.isConnected())
			{
				gridNodesToRet.add(gridNode);
			}
		}
		return gridNodesToRet;
	}
	
	/**
	 * Initializes the {@link GridNode}'s doing connections from
	 * the {@link GridServer} to the host that the {@link GridNode}'s are
	 * connected to
	 * @throws IOException
	 */
	protected void setupGridNodes() throws IOException
	{
		final ServerHostProperties hostProperties = ServerHostProperties.getInstance();
		
		int numLocalNodes = hostProperties.getNumLocalNodes();
		int numExternalNodes = hostProperties.getNumExternalNodes();
		int totalNodes = hostProperties.getTotalNodes();
		assert totalNodes == numLocalNodes + numExternalNodes;
		
		for (int i = 0; i < totalNodes; i++)
		{
			final boolean localNode = i < numLocalNodes;
			
			new Thread()
			{
				public void run() 
				{
					System.out.println("trying start new node...");
					GridNode gridNode = new GridNode(executor, localNode);
					gridNodeList.add(gridNode);
					gridNode.startUp();
				}
			}.start();
		}
	}
	
	/**
	 * Wait for a minimum of nodes to be connected to start the server
	 * Does a connection between server and nodes checking 
	 * which gathering node's info
	 * 
	 * @param minNodesToStart
	 */
	public void waitForNodesStart(int minNodesToStart)
	{
		int numClientsConnected = 0;

		long time = System.currentTimeMillis();
		while (numClientsConnected < minNodesToStart)
		{
			numClientsConnected = 0;
			
			int sz = gridNodeList.size();
			for (int i = 0; i < sz; i++)
			{
				GridNode gridNode = gridNodeList.get(i);
				if (gridNode.isConnected())
				{
					numClientsConnected++;
				}
				else
				{
					continue;
				}
			}

			long currTime = System.currentTimeMillis();
			if (currTime - time > 60 * 1000)
			{
				time = currTime;
				System.out.println("Waiting for nodes... minimun nodes to start: " + minNodesToStart + ", num connected nodes: " + numClientsConnected);
			}
			Thread.yield();
		}
	}
}
