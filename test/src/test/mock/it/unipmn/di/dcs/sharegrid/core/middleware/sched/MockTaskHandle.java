package test.mock.it.unipmn.di.dcs.sharegrid.core.middleware.sched;

import it.unipmn.di.dcs.grid.core.middleware.sched.IJobHandle;
import it.unipmn.di.dcs.grid.core.middleware.sched.ITaskHandle;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class MockTaskHandle implements ITaskHandle
{
	private static Map<String,Integer> NextIdMap = new HashMap<String,Integer>();

	private IJobHandle jobHnd;
	private int id;

	public MockTaskHandle(IJobHandle jobHnd)
	{
		this.jobHnd = jobHnd;

		if ( MockTaskHandle.NextIdMap.containsKey( jobHnd.getId() ) )
		{
			this.id =  MockTaskHandle.NextIdMap.get( jobHnd.getId() ).intValue();
		}
		else
		{
			this.id = 1;
		}

		MockTaskHandle.NextIdMap.put( jobHnd.getId(), this.id + 1 );
	}

	public String getId()
	{
		//return "fake-task-id." + Integer.toString(this.id);
		return "ArrayDemoTask." + Integer.toString(this.id);
	}

	public IJobHandle getJobHandle()
	{
		return this.jobHnd;
	}
}
