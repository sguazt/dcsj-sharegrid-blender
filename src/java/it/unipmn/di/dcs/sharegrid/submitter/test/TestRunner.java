/*
 * Copyright (C) 2007  Distributed Computing System (DCS) Group, Computer
 * Science Department - University of Piemonte Orientale, Alessandria (Italy).
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package it.unipmn.di.dcs.sharegrid.submitter.test;

import it.unipmn.di.dcs.common.text.BinaryTextExpr;
import it.unipmn.di.dcs.common.text.TerminalTextExpr;
import it.unipmn.di.dcs.grid.core.middleware.sched.BotJob;
import it.unipmn.di.dcs.grid.core.middleware.sched.BotTask;
import it.unipmn.di.dcs.grid.core.middleware.sched.IBotJob;
import it.unipmn.di.dcs.grid.core.middleware.sched.IBotScheduler;
import it.unipmn.di.dcs.grid.core.middleware.sched.IBotTask;
import it.unipmn.di.dcs.grid.core.middleware.sched.IRemoteCommand;
import it.unipmn.di.dcs.grid.core.middleware.sched.IScheduler;
import it.unipmn.di.dcs.grid.core.middleware.sched.IStageIn;
import it.unipmn.di.dcs.grid.core.middleware.sched.IStageOut;
import it.unipmn.di.dcs.grid.core.middleware.sched.JobRequirements;
import it.unipmn.di.dcs.grid.core.middleware.sched.JobRequirementsOps;
import it.unipmn.di.dcs.grid.core.middleware.sched.RemoteCommand;
import it.unipmn.di.dcs.grid.core.middleware.sched.StageIn;
import it.unipmn.di.dcs.grid.core.middleware.sched.StageInAction;
import it.unipmn.di.dcs.grid.core.middleware.sched.StageInMode;
import it.unipmn.di.dcs.grid.core.middleware.sched.StageInRule;
import it.unipmn.di.dcs.grid.core.middleware.sched.StageInType;
import it.unipmn.di.dcs.grid.core.middleware.sched.StageOut;
import it.unipmn.di.dcs.grid.core.middleware.sched.StageOutAction;
import it.unipmn.di.dcs.grid.core.middleware.sched.StageOutMode;
import it.unipmn.di.dcs.grid.core.middleware.sched.StageOutRule;
import it.unipmn.di.dcs.sharegrid.submitter.MiddlewareFactory;;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * A tester class.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
@Deprecated
public class TestRunner
{
	public static void main(String[] args)
	{
		boolean isOk = true;

		// Prepares stage-in files
		File[] stageInFiles = new File[5];
		try
		{
			stageInFiles[0] = File.createTempFile( "stagein0_", null );
			stageInFiles[0].deleteOnExit();
			stageInFiles[1] = File.createTempFile( "stagein1_", null );
			stageInFiles[1].deleteOnExit();
			stageInFiles[2] = File.createTempFile( "stagein2_", null );
			stageInFiles[2].deleteOnExit();
			stageInFiles[3] = File.createTempFile( "stagein3_", null );
			stageInFiles[3].deleteOnExit();
			stageInFiles[4] = File.createTempFile( "stagein4_", null );
			stageInFiles[4].deleteOnExit();
		}
		catch (Exception e)
		{
			System.err.println( "Error while creating STAGE-IN temporary files: " + e );
			e.printStackTrace();
			System.exit(1);
		}

		IBotJob job = new BotJob( "testjob" );
		job.setRequirements( new JobRequirements( new BinaryTextExpr( new TerminalTextExpr("os"), JobRequirementsOps.EQ, new TerminalTextExpr("linux") ) ) );
		IStageIn stageIn = new StageIn();
		stageIn.addRule(
			new StageInRule(
				new StageInAction( StageInMode.DIFF_OVERWRITE, stageInFiles[0].getAbsolutePath(), "$STORAGE/" + stageInFiles[0].getName(), StageInType.VOLATILE )
			)
		);
		stageIn.addRule(
			new StageInRule(
				new StageInAction( StageInMode.ALWAYS_OVERWRITE, stageInFiles[1].getAbsolutePath(), "$PLAYPEN/" + stageInFiles[1].getName(), StageInType.VOLATILE )
			)
		);
		stageIn.addRule(
			new StageInRule(
				new StageInAction( StageInMode.ALWAYS_OVERWRITE, stageInFiles[2].getAbsolutePath(), stageInFiles[2].getName(), StageInType.VOLATILE )
			)
		);
		job.setStageIn( stageIn );
		job.addCommand(
			new RemoteCommand( "ls > $PLAYPEN/$JOB-$TASK.out" )
		);
		IStageOut stageOut = new StageOut();
		stageOut.addRule(
				new StageOutRule(
					new StageOutAction( StageOutMode.ALWAYS_OVERWRITE, "$STORAGE/$JOB-$TASK.out", "$JOB-$TASK.out" )
				)
		);
		job.setStageOut( stageOut );

		IBotTask task = null;

		// task #1
		task = new BotTask( null, null, null );
		job.addTask( task );
		// task #2
		task = new BotTask( null, null, null );
		job.addTask( task );
		// task #3
		{
			IStageIn in = new StageIn();
			in.addRule(
				new StageInRule(
					new StageInAction( StageInMode.ALWAYS_OVERWRITE, stageInFiles[3].getAbsolutePath(), stageInFiles[3].getName() + "-$JOB.$TASK", StageInType.VOLATILE )
				)
			);
			in.addRule(
				new StageInRule(
					new StageInAction( StageInMode.DIFF_OVERWRITE, stageInFiles[4].getAbsolutePath(), stageInFiles[4].getName() + "-$JOB.$TASK", StageInType.VOLATILE )
				)
			);

			List<IRemoteCommand> com = new ArrayList<IRemoteCommand>();
			com.add( new RemoteCommand( "date >date-$JOB.$TASK.out" ) );

			IStageOut out = new StageOut();
			out.addRule(
				new StageOutRule(
					new StageOutAction( StageOutMode.ALWAYS_OVERWRITE, "date-$JOB.$TASK.out", "date-$JOB.$TASK.out" )
				)
			);

			task = new BotTask( in, com, out );
			job.addTask( task );
		}

		System.out.println( job );

		IScheduler sched = MiddlewareFactory.GetInstance().getMiddlewareManager().getJobScheduler();

		if ( sched.isRunning() )
		{
			try
			{
				sched.submitJob( job );
			}
			catch (Exception e)
			{
				System.err.println( "Caught exception: " + e );
				e.printStackTrace();
				isOk = false;
			}
		}
		else
		{
			System.out.println( "Could not run the test: scheduler is not running." );
		}

		if ( !isOk )
		{
			System.exit(1);
		}

		System.out.println("Bye bye!");
		System.exit(0);
	}
}
