package test.mock.it.unipmn.di.dcs.sharegrid.core.middleware.sched;

import it.unipmn.di.dcs.grid.core.middleware.sched.IJobHandle;
import it.unipmn.di.dcs.grid.core.middleware.sched.ITaskHandle;
import it.unipmn.di.dcs.grid.core.middleware.sched.JobType;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class MockJobHandle implements IJobHandle
{
	private static int NextId = 1;

	private int id;
	private List<ITaskHandle> tasks = new ArrayList<ITaskHandle>();

	public MockJobHandle(int ntasks)
	{
		this.id = MockJobHandle.NextId++;
		for (int i = 0; i < ntasks; i++)
		{
			this.tasks.add( new MockTaskHandle(this) );
		}
	}

	//@{ IJobHandle implementation

	public String getId()
	{
		//return "fake-job-id." + Integer.toString(this.id);
		return "ArrayDemo." + Integer.toString(this.id);
	}

	public String getName()
	{
		//return "fake-job-name." + Integer.toString(this.id);
		return "ArrayDemo." + Integer.toString(this.id);
	}

	public JobType getType()
	{
		return JobType.BOT;
	}

	public int getTasksNum()
	{
		return this.tasks.size();
	}

	public List<ITaskHandle> getTasks()
	{
		return this.tasks;
	}

	//@} IJobHandle implementation
}
