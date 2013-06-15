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
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.grideasy.client.GridJob;

/**
 * The tasks for processing is aggregated in this class
 * The {@link GridJob}'s are put into this are put into the task and 
 * mapped via {@link GridTask#map(List, Object[])} method.
 * 
 * @author Felipe Santos
 * @email felchs@gmail.com
 *
 */
public abstract class GridTask
{
	/**
	 * The object result of a task
	 */
	private Object taskResult;
	
	/**
	 * All jobs from this task 
	 */
	private Map<GridJob, GridNode> jobsMap = new Hashtable<GridJob, GridNode>();
	
	/**
	 * The task completion manger. This is used to check if a task is
	 * completed and manage the re-start of a task in case of not being completed
	 */
	private GridTaskCompletionManager taskCompletionManager = new GridTaskCompletionManager(this);
	
	/**
	 * An array of {@link GridJob}'s results
	 */
	private Object[] jobReducedResults = new Object[0];

	/**
	 * The list of {@link GridJob}'s
	 */
	private LinkedList<GridJob> jobsList = new LinkedList<GridJob>();
	
	/**
	 * The package names that are used to handle code spreading among {@link GridNode}'s
	 */
	private Set<String> packageNames = new HashSet<String>();
	
	/**
	 * Constructor passing {@link GridJob}'s list
	 * @param jobsList a List of {@link GridJob}'s
	 */
	public GridTask(LinkedList<GridJob> jobsList)
	{
		if (jobsList.isEmpty())
		{
			throw new IllegalArgumentException("The list is empty, you should put at least one job on this task");
		}
		this.jobsList = jobsList;
	}
	
	/**
	 * Returns a map of {@link GridNode}'s
	 * @return a map of {@link GridNode}'s
	 */
	public Map<GridJob, GridNode> getJobsMap()
	{
		return jobsMap;
	}
	
	/**
	 * Sets a mapping of {@link GridJob}'s
	 * @param jobsMap the jobs to be mapped
	 */
	public void setJobsMap(Map<GridJob, GridNode> jobsMap)
	{
		this.jobsMap = jobsMap;
	}
	
	/**
	 * Gets a list of {@link GridJob}'s
	 * @return jobsList a list of {@link GridJob}'s
	 */
	public LinkedList<GridJob> getJobsList() 
	{
		return jobsList;
	}

	/**
	 * Add a package name to be used when spreading
	 * code among {@link GridNode}'s
	 * @param packageName
	 */
	public void addPackageName(String packageName)
	{
		packageNames.add(packageName);
	}
	
	/**
	 * A set of package names to be used when spreading
	 * code among {@link GridNode}'s
	 * @param packageNames
	 */
	public void addPackageNames(Set<String> packageNames)
	{
		for (String packageName : packageNames)
		{
			packageNames.add(packageName);
		}
	}
	
	/**
	 * Set a package name to a given {@link GridNode} 
	 * The package name is used to spread code from 
	 * the server machine to {@link GridNode}'s instances
	 * @param node the {@link GridNode} which the package name is set  
	 */
	protected void addPackageNameToNode(GridNode node) 
	{
		Iterator<String> it = packageNames.iterator();
		while (it.hasNext())
		{
			String packageName = it.next();
			node.addPackageName(packageName);
		}
	}
	
	/**
	 * Execute all jobs from this task and waiting 
	 * for tasks to be completed
	 * @return the task result object
	 */
	public Object execute()
	{
		Set<GridJob> jobsSet = jobsMap.keySet();
		for (GridJob gridJob : jobsSet)
		{
			GridNode gridNode = jobsMap.get(gridJob);
			taskCompletionManager.addJob(gridJob, gridNode);
			gridNode.executeJob(gridJob, this, taskCompletionManager);
		}
		
		taskCompletionManager.doWaitAllJobsToComplete();
		
		return taskResult;
	}
	
	/**
	 * Check whether all jobs are completed or not
	 * @return whether all jobs are completed or not
	 */
	public boolean isAllJobsComplete()
	{
		boolean allJobsComplete = true;
		
		Set<GridJob> jobsSet = jobsMap.keySet();
		for (GridJob gridJob : jobsSet)
		{
			if (!gridJob.isComplete())
			{
				allJobsComplete = false;
				break;
			}
		}
		return allJobsComplete;
	}
	
	/**
	 * Internal copy of job results updating the 
	 * job results array
	 * @param result the result to be updated 
	 */
	private void updateReducedResutls(Object result)
	{
		if (result == null)
		{
			return;
		}
		
		int lastSz = jobReducedResults.length;
		int newLength = lastSz + 1;
		jobReducedResults = Arrays.copyOf(jobReducedResults, newLength);
		jobReducedResults[lastSz] = result;
	}
	
	/**
	 * Notifies this task that a job was reduced
	 * @param job the {@link GridJob} reduced
	 * @param result the result from a {@link GridJob}
	 * @throws IOException if a exception was thrown 
	 */
	public void notifyTaskJobReduce(GridJob job, Object result) throws IOException
	{
		updateReducedResutls(result);
		
		jobReduce(job, result);
		
		if (isAllJobsComplete())
		{
			this.taskResult = reduce(jobReducedResults);
		}
	}

	/**
	 * Force all {@link GridNode}'s connections to be closed 
	 * @throws IOException if a exception was thrown
	 */
	public void closeAllGridConnections() throws IOException
	{
		Set<GridJob> gridJobKeys = jobsMap.keySet();
		for (GridJob gridJob : gridJobKeys)
		{
			GridNode gridNode = jobsMap.get(gridJob);
			if (!gridNode.isClosed())
			{
				gridNode.closeAllConnections();
				gridNode.setClosed(true);
			}
		}
	}

	/**
	 * It maps the {@link GridNode}'s to be executed into a topology
	 * 
	 * @param topology
	 * @param args
	 * @return
	 */
	public Map<GridJob, GridNode> map(List<GridNode> topology, Object[] args)
	{
		Map<GridJob, GridNode> jobMapping = new HashMap<GridJob, GridNode>(topology.size());

		Iterator<GridJob> jobsListIt = jobsList.iterator();
		
		int gridSz = topology.size();

		int gridIdx = 0;
		while (jobsListIt.hasNext())
		{
        	GridNode node = topology.get(gridIdx++);
        	
        	addPackageNameToNode(node);
        	
            GridJob job = jobsListIt.next();
            
        	jobMapping.put(job, node);

        	if (gridIdx >= gridSz)
       		{
        		gridIdx = 0;
        	}
		}

        return jobMapping;
	}

	/**
	 * The notification of a {@link GridJob} reducing
	 * @param job {@link GridJob} that finished and reduced
	 * @param result the result of the {@link GridJob}
	 */
	public abstract void jobReduce(GridJob job, Object result);
	
	/**
	 * This final {@link GridTask} reduce
	 *   
	 * @param results the reduced results
	 * @return the return from this {@link GridTask} reducing
	 */
	public abstract Object reduce(Object[] results);
}