package test.mock.it.unipmn.di.dcs.sharegrid.core.middleware.sched.monitor;

import it.unipmn.di.dcs.grid.core.middleware.sched.ExecutionStatus;
import it.unipmn.di.dcs.grid.core.middleware.sched.ITaskHandle;
import it.unipmn.di.dcs.grid.core.middleware.sched.monitor.ITaskMonitorContext;
import it.unipmn.di.dcs.grid.core.middleware.sched.monitor.IJobMonitorContext;

/**
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class MockTaskMonitorContext implements ITaskMonitorContext
{
	private ITaskHandle taskHnd;
	private IJobMonitorContext jobCtx;
	private ExecutionStatus oldStatus;
	private ExecutionStatus curStatus;
 
	public MockTaskMonitorContext(ITaskHandle taskHnd, IJobMonitorContext jobCtx)
	{
		this.taskHnd = taskHnd;
		this.jobCtx = jobCtx;
		this.oldStatus = this.curStatus = ExecutionStatus.UNSTARTED;
	}

	public void setCurTaskStatus(ExecutionStatus value)
	{
		this.oldStatus = this.curStatus;
		this.curStatus = value;
	}

	//@{ ITaskMonitorContext implementation

	public ITaskHandle taskHandle()
	{
		return this.taskHnd;
	}

	public IJobMonitorContext jobContext()
	{
		return this.jobCtx;
	}

	public ExecutionStatus oldTaskStatus()
	{
		return this.oldStatus;
	}

	public ExecutionStatus curTaskStatus()
	{
		return this.curStatus;
	}

	//@} ITaskMonitorContext implementation
}
