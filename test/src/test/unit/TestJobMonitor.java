package test.unit;

//import it.unipmn.di.dcs.sharegrid.core.*;
import it.unipmn.di.dcs.grid.core.middleware.sched.IJobHandle;
import it.unipmn.di.dcs.grid.core.middleware.sched.monitor.IJobMonitor;
import it.unipmn.di.dcs.sharegrid.monitor.app.job.ui.GriddedAppGUI;
import it.unipmn.di.dcs.sharegrid.monitor.app.job.ui.IJobMonitorAppUI;

import java.util.Random;

import org.junit.*;
import static org.junit.Assert.*;
import org.junit.runner.JUnitCore;

import test.mock.it.unipmn.di.dcs.sharegrid.core.middleware.sched.MockJobHandle;
import test.mock.it.unipmn.di.dcs.sharegrid.core.middleware.sched.monitor.MockJobMonitor;

/**
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class TestJobMonitor
{
	@Before
	public void setUp()
	{
		// empty
	}

	@Test
	public void testGriddedAppGUI()
	{
		IJobHandle jobHnd = new MockJobHandle( 30 );

		IJobMonitor monitor = new MockJobMonitor( jobHnd );

		IJobMonitorAppUI ui = new GriddedAppGUI(monitor);

		ui.showUI();

		assertTrue( true );
	}

	@After
	public void tearDown()
	{
		// empty
	}

	public static void main(String args[])
	{
//		JUnitCore.main( TestJobMonitor.class.getName() );
		new TestJobMonitor().testGriddedAppGUI();

/*
		try
		{
			Thread.currentThread().join();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.exit(1);
		}

		System.exit(0);
*/
	}
}
