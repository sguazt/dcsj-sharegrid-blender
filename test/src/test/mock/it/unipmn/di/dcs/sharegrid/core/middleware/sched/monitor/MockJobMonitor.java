package test.mock.it.unipmn.di.dcs.sharegrid.core.middleware.sched.monitor;

import it.unipmn.di.dcs.common.math.PermutationGenerator;
import it.unipmn.di.dcs.grid.core.middleware.sched.ExecutionStatus;
import it.unipmn.di.dcs.grid.core.middleware.sched.IJobHandle;
import it.unipmn.di.dcs.grid.core.middleware.sched.ITaskHandle;
import it.unipmn.di.dcs.grid.core.middleware.sched.monitor.AbstractJobMonitor;
import it.unipmn.di.dcs.grid.core.middleware.sched.monitor.IJobMonitorContext;
import it.unipmn.di.dcs.grid.core.middleware.sched.monitor.ITaskMonitorContext;
import it.unipmn.di.dcs.grid.core.middleware.sched.monitor.IJobMonitorEventDispatcher;

import java.util.Random;

/**
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class MockJobMonitor extends AbstractJobMonitor
{
	public MockJobMonitor(IJobHandle jobHnd)
	{
		super( jobHnd );
	}

	public MockJobMonitor(IJobHandle jobHnd, IJobMonitorEventDispatcher dispatcher)
	{
		super( jobHnd, dispatcher );
	}

	//@{ AbstractJobMonitor extension

	@Override
	public void run()
	{
		new Thread( this.new Runner()).start();
	}

	//@} AbstractJobMonitor extension

	//@{ MockJobMonitor.Runner

	private final class Runner implements Runnable
	{
		//@{ Runnable implementation

		public void run()
		{
			IJobHandle jobHnd = MockJobMonitor.this.getJobHandle();
			IJobMonitorContext jobCtx = new MockJobMonitorContext( jobHnd );

			int ntasks = jobHnd.getTasksNum();
			MockTaskMonitorContext[] tasksCtx = new MockTaskMonitorContext[ntasks];
			for (int i = 0; i < ntasks; i++)
			{
				tasksCtx[i] = new MockTaskMonitorContext( jobHnd.getTasks().get(i), jobCtx );
			}

			PermutationGenerator permGen = new PermutationGenerator();
			int[] perm = permGen.nextPermutation(ntasks);
			//System.err.println("Permutation size: " + perm.length );//XXX

			int count = 0;
			int countCompletes = 0;
			Random timer = new Random();
			double time = 0.0;

			while ( countCompletes < ntasks )
			{
				try
				{
					int taskDuration = timer.nextInt(5 * 1000);

					Thread.sleep( taskDuration );

					//System.err.println("Count: " + count );//XXX
					int taskIdx = perm[count] - 1;
					ITaskHandle taskHnd = jobHnd.getTasks().get( taskIdx );
					//MockTaskMonitorContext taskCtx = new MockTaskMonitorContext( taskHnd, jobCtx );
					MockTaskMonitorContext taskCtx = tasksCtx[ taskIdx ];

//					System.out.println( "Task '" + taskHnd.getId() + "' - status is: " + taskCtx.curTaskStatus() + " (clock: " + time + ")");//XXX

					time += taskDuration;

					ExecutionStatus status = ExecutionStatus.UNSTARTED;
					switch (taskCtx.curTaskStatus())
					{
						case UNSTARTED:
							status = ExecutionStatus.RUNNING;
							break;
						case RUNNING:
						case FINISHED:
							status = ExecutionStatus.FINISHED;
							countCompletes++;
							break;
						default:
							status = ExecutionStatus.FAILED;
							countCompletes++;
							break;
					}

					taskCtx.setCurTaskStatus( status );

//					System.out.println( "Task '" + taskHnd.getId() + "' executed for " + (taskDuration/1000.0) + " secs. New status is: " + status + " (clock: " + time + ")");//XXX

					MockJobMonitor.this.getDispatcher().dispatchTaskStatusChange( taskCtx );
				}
				catch (InterruptedException ie)
				{
					System.err.println( "Caught inturrupted exception: " + ie.getMessage() );
					System.err.println( "... deferring" );
				}
				finally
				{
					count++;

					if ( count == ntasks )
					{
						count = 0;
						perm = permGen.nextPermutation(ntasks);
					}
				}
			}
		}

		//@} Runnable implementation
	}

	//@} MockJobMonitor.Runner
}
