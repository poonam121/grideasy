For starting use Grideasy you just need:

-Serve side: to put the grideasyserver.jar into your classpath
In the configuration file config/gridservermapproperties (in the root of the project) you must set the host and port params. by default the gridservermapproperties is with this configuration:

```
server_ip=127.0.0.1
port=50000
numExternalNodes=0
numLocalNodes=1
minNodesToStart=1
```

-Client side: initialize the grideasyclient.jar on the clients
In the configuration file config/gridclientmapproperties (in the root of the jar) you must set the host and port params. by default the gridclientmapproperties is with this configuration:
```
server_ip=127.0.0.1
port=50000
```
For example, if you have one server machine and three external nodes you must start the program that uses the grideasyserver.jar and initializes three instances of the grideasyclient.jar.
In this case the program configuration must be set to:
```
gridservermapproperties:
server_ip=127.0.0.1
port=50000
numExternalNodes=3
numLocalNodes=0
minNodesToStart=3
```
gridclientmapproperties:
```
server_ip=127.0.0.1
port=50000
```
The minNodesToStart is your choice but must in the maximum range of the external and local nodes you created.
If you set the minNodesToStart to less than the machines you create the task will begin the execution by this number.

An example program: A task that greates 100 jobs. The work of the job is just to sleep some milliseconds.

```
public static void main(String[] args) throws IOException, InterruptedException
{
	Grid.start();
	
	final LinkedList<GridJob> jobsList = new LinkedList<GridJob>();
	int numOfJobs = 100;
	for (int i = 0; i < numOfJobs; i++)
	{
		jobsList.add(new GridJob() 
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected Object execute() {
				final long TIME_TO_SLEEP = (long) (Math.random() * 1000);
				System.out.println("Job sleeping: " + TIME_TO_SLEEP);
				
				try
				{
					Thread.sleep(TIME_TO_SLEEP);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
				
				return "JOB of sleep: " + TIME_TO_SLEEP;
			}
		});
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
	
	Object[] splitParams = new Object[] { "DummySplitParams" };
	
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
```

After in the end of the execution of the GridJob there is a return where the MapReduce occurs.