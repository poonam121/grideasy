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

import java.util.Hashtable;
import java.util.Vector;

import com.grideasy.client.GridJob;

/**
 * This class manage the completion of a a task
 * It handles the occasional problems that can
 * occur in a job excecution
 * 
 * @author Felipe Santos
 * @email felchs@gmail.com
 */
public class GridTaskCompletionManager implements GridJobListener
{
	/**
	 * The max retries of a job to be executed in
	 * a {@link GridNode} when it fails
	 */
	private int MAX_RETRIES;
	
	/**
	 * The {@link GridNode} mapping of {@link GridNode}'s by key of {@link GridJob}
	 */
	private Hashtable<GridJob, GridNode> gridNodeMapping = new Hashtable<GridJob, GridNode>();
	
	/**
	 * A list of {@link GridNode}'s
	 */
	private Vector<GridNode> gridNodes = new Vector<GridNode>();
	
	/**
	 * A list of completed {@link GridJob}'s
	 */
	private Vector<GridJob> completedJobs = new Vector<GridJob>();

	/**
	 * The wrong tries of {@link GridJob} execution
	 */
	private Hashtable<GridJob, Integer> wrongJobTries = new Hashtable<GridJob, Integer>();

	/**
	 * The {@link GridTask} to be managed
	 */
	private GridTask gridTask;
	
	/**
	 * Sets whether the task was complete or not
	 */
	private boolean taskComplete;
	
	/**
	 * A constructor passing the {@link GridTask} to be managed
	 * @param gridTask the {@link GridTask} to be managed
	 */
	public GridTaskCompletionManager(GridTask gridTask)
	{
		this(3, gridTask);
	}
	
	/**
	 * A constructor passing the {@link GridTask} to be managed
	 * and a maximum of retries from a {@link GridJob}  be executed 
	 * on the same node
	 * @param maxRetries the max of retries of a {@link GridJob} to be executed on the same node
	 * @param gridTask the {@link GridTask} to be managed
	 */
	public GridTaskCompletionManager(int maxRetries, GridTask gridTask)
	{
		this.MAX_RETRIES = maxRetries;
		this.gridTask = gridTask;
	}
	
	/**
	 * Sets the {@link GridNode}'s to be handled
	 * @param gridNodes the {@link GridNode}'s to be handled
	 */
	public void setGridNodes(Vector<GridNode> gridNodes)
	{
		this.gridNodes = gridNodes;
	}
	
	/**
	 * Gets whether the task was completed or not
	 * @return whether the task was completed or not
	 */
	public synchronized boolean isTaskComplete()
	{
		return taskComplete;
	}
	
	/**
	 * Sets the task flag of completion
	 * @param taskComplete the flag of completion
	 */
	public synchronized void setTaskComplete(boolean taskComplete)
	{
		this.taskComplete = taskComplete;
	}
	
	/**
	 * Adds a {@link GridJob} to a given {@link GridNode} and
	 * do mapping the nodes for future handling 
	 * @param gridJob the {@link GridJob} to be added to a given {@link GridNode}
	 * @param gridNode the {@link GridNode} to be handled
	 */
	public void addJob(GridJob gridJob, GridNode gridNode)
	{
		if (!gridNodes.contains(gridNode))
		{
			gridNodes.add(gridNode);
		}
		
		gridNodeMapping.put(gridJob, gridNode);
	}

	/**
	 * The notification of a job completeness
	 * @param gridJob the {@link GridJob} that completed
	 */
	@Override
	public void onJobComplete(GridJob gridJob)
	{
		completedJobs.add(gridJob);
	}
	
	/**
	 * Gets an available node to be used when an error 
	 * is thrown
	 * 
	 * @param node the {@link GridNode} that failed
	 * @return gridNode an available {@link GridNode}
	 */
	private GridNode getAnAvailableNode(GridNode node)
	{
		int sz = gridNodes.size();
		if (sz > 1)
		{
			int gridNodeIdx = (int) (Math.random()  * (sz - 1));
			GridNode gridNode = gridNodes.get(gridNodeIdx);
			if (gridNode != node)
			{
				return gridNode;
			}
			else
			{
				if (++gridNodeIdx > sz)
				{
					gridNodeIdx = 0;
				}
				return gridNodes.get(gridNodeIdx);
			}
		}
		else
		{
			return gridNodes.get(0);
		}
	}
	
	/**
	 * A notification of a error in a job
	 * This method handles the retry of a erroneous {@link GridNode} with 
	 * another available {@link GridNode}
	 * 
	 * @param gridJob the {@link GridJob} that failed
	 * @param node the {@link GridNode} that failed
	 * @param e the {@link Exception} thrown when {@link GridJob} failed
	 */
	@Override
	public void onJobError(GridJob gridJob, GridNode node, Exception e)
	{
		Integer tries = wrongJobTries.get(gridJob);
		if (tries != null)
		{
			tries = tries + 1;
		}
		else
		{
			tries = 1;
		}
		
		wrongJobTries.put(gridJob, tries);
		
		if (tries < MAX_RETRIES)
		{
			node.executeJob(gridJob, gridTask, this);
		}
		else
		{
			GridNode availableNode = getAnAvailableNode(node);
			availableNode.executeJob(gridJob, gridTask, this);
		}
	}

	/**
	 * A waiter of jobs completion
	 * This methods holds this current thread (the thread that
	 * launch the {@link GridJob}'s) to wait until all
	 * the {@link GridJob}'s to be completed.
	 * When it completes the caller method: {@link GridTask#execute()} 
	 * gather the results of the {@link GridJob}'s to be returned 
	 * and passed through
	 */
	public synchronized void doWaitAllJobsToComplete()
	{
		if (!isTaskComplete())
		{
			try
			{
				this.wait();
			} 
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}

	/**
	 * When all the jobs were completed this method
	 * calls notify() for the waiting thread to be awaked
	 * The awaked thread comes from the {@link GridNode} object that
	 * calls waits for the client response.
	 */
	@Override
	public void onAllJobsComplete()
	{
		synchronized (this)
		{
			this.notify();
		}
		
		setTaskComplete(true);
	}
}