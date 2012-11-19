package test.mock.it.unipmn.di.dcs.sharegrid.core.middleware.sched.monitor;

import it.unipmn.di.dcs.grid.core.middleware.sched.ExecutionStatus;
import it.unipmn.di.dcs.grid.core.middleware.sched.IJobHandle;
import it.unipmn.di.dcs.grid.core.middleware.sched.monitor.IJobMonitorContext;

/**
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class MockJobMonitorContext implements IJobMonitorContext
{
	private IJobHandle jobHnd;
	private ExecutionStatus oldStatus;
	private ExecutionStatus curStatus;

	public MockJobMonitorContext(IJobHandle jobHnd)
	{
		this.jobHnd = jobHnd;
		this.oldStatus = this.curStatus = ExecutionStatus.UNSTARTED;
	}

	public void setCurJobStatus(ExecutionStatus value)
	{
		this.oldStatus = this.curStatus;
		this.curStatus = value;
	}

	//@{ IJobMonitorContext implementation

	public IJobHandle jobHandle()
	{
		return this.jobHnd;
	}

	public ExecutionStatus oldJobStatus()
	{
		return this.oldStatus;
	}

	public ExecutionStatus curJobStatus()
	{
		return this.curStatus;
	}

	//@} IJobMonitorContext implementation
}
