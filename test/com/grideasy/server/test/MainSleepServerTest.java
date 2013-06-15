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
package com.grideasy.server.test;

import java.io.IOException;
import java.util.LinkedList;

import com.grideasy.client.GridJob;
import com.grideasy.client.NetDiscoverException;
import com.grideasy.server.impl.Grid;
import com.grideasy.server.impl.GridTask;

public class MainSleepServerTest
{
	public static void main(String[] args) throws IOException, InterruptedException
	{
		Grid.start();
		
		final LinkedList<GridJob> jobsList = new LinkedList<GridJob>();
		int numOfJobs = 100;
		for (int i = 0; i < numOfJobs; i++)
		{
			long timeToSleep = (long) (Math.random() * 1000);
			jobsList.add(new SleepGridDummyJob(timeToSleep));
		}

		GridTask task = new GridTask(jobsList)
		{
			@Override
			public void jobReduce(GridJob job, Object result)
			{
				System.out.println("Job reduce: " + result);
			}
			
			@Override
			public Object reduce(Object[] results)
			{
				System.out.println("All jobs done");
				return null;
			}
		};
		
		Object[] splitParams = new Object[] { "abc" };
		
		try
		{
			Grid.execute(task, splitParams);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (NetDiscoverException e)
		{
			e.printStackTrace();
		}
	}
}
