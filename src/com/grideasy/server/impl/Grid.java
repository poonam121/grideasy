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

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.grideasy.client.GridJob;
import com.grideasy.client.NetDiscoverException;

/**
 * Grid class gathers all the grid structure allowing you to 
 * obtain the instances of nodes to map, start, stop the
 * grid distribution.
 *    
 * @author Felipe Santos
 * @email felchs@gmail.com
 */
public class Grid
{
	/**
	 * The singleton instance of Grid
	 */
	private static Grid instance;
	
	/**
	 * Returns the instance of the grid.
	 * 
	 * @return the instance of the grid
	 * @throws IOException
	 */
	public static Grid getInstance() throws IOException
	{
		if (instance == null)
		{
			instance = new Grid();
		}
		
		return instance;
	}
	
	/**
	 * Start up the Grid instance
	 * When the Grid is started it does a look for {@link GridNode} that
	 * mapped by the configuration file of the system. This file is
	 * by default located on the file /config/gridclientmapproperties
	 * 
	 * @throws IOException
	 */
	public static void start() throws IOException
	{
		Grid.getInstance().startUp();
	}
	
	/**
	 * Execute a task on a previous mapped grid.
	 * The jobs are spread among {@link GridNode} 
	 * by {@link GridTask} object that is passed through the argument

	 * @param task the {@link GridTask} to be executed 
	 * @param argsParams the arguments of the task
	 * @return a object of the simulation
	 * 
	 * @throws IOException
	 * @throws NetDiscoverException
	 */
	public static Object execute(GridTask task, Object[] argsParams) throws IOException, NetDiscoverException
	{
		List<GridNode> gridNodeList = getInstance().getGridNodeList();

		Map<GridJob, GridNode> jobsMap = task.map(gridNodeList, argsParams);
		task.setJobsMap(jobsMap);
		
		return task.execute();
	}

	///////////////////////////////////////////////////////////////////////////
	
	/**
	 * The instance of the {@link GridServer}
	 * The grid server gather a list of {@link GridNode}
	 */
	private GridServer gridServer;
	
	/**
	 * Constructor of {@link GridNode}
	 * 
	 * @throws IOException
	 */
	private Grid() throws IOException
	{
		this.gridServer = new GridServer();
	}
	
	/**
	 * Returns the {@link GridServer}
	 * @return a {@link GridServer} object
	 */
	public GridServer getGridServer()
	{
		return gridServer;
	}
	
	/**
	 * Returns a {@link GridNode} list
	 * @return a {@link GridNode} list
	 */
	public List<GridNode> getGridNodeList()
	{
		return gridServer.getConnectedNodeList();
	}
	
	/**
	 * Starts the {@link Grid} mapping the associated {@link GridNode} 
	 */
	public void startUp()
	{
		int numNodes = ServerHostProperties.getInstance().getNumLocalNodes();
		LocalGrids.getInstance().initNewLocalGridNode(numNodes);
		int minNodesToStart = ServerHostProperties.getInstance().getMinNodesToStart();
		gridServer.waitForNodesStart(minNodesToStart);
	}
}
