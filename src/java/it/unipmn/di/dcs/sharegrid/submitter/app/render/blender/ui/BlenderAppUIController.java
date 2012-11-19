/*
 * Copyright (C) 2008  Distributed Computing System (DCS) Group, Computer
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

package it.unipmn.di.dcs.sharegrid.submitter.app.render.blender.ui;

import it.unipmn.di.dcs.common.text.ITextExpr;
import it.unipmn.di.dcs.common.text.TextExprs;
import it.unipmn.di.dcs.common.util.collection.Collections;
import it.unipmn.di.dcs.common.util.Strings;
import it.unipmn.di.dcs.grid.core.format.ExportFormatType;
import it.unipmn.di.dcs.grid.core.format.IFormatExporter;
import it.unipmn.di.dcs.grid.core.format.jdf.JdfExporter;
import it.unipmn.di.dcs.grid.core.middleware.IMiddlewareManager;
import it.unipmn.di.dcs.grid.core.middleware.sched.ExecutionStatus;
import it.unipmn.di.dcs.grid.core.middleware.sched.IJobHandle;
import it.unipmn.di.dcs.grid.core.middleware.sched.monitor.IJobMonitor;
import it.unipmn.di.dcs.sharegrid.submitter.MiddlewareFactory;
import it.unipmn.di.dcs.sharegrid.submitter.app.render.blender.BlenderApp;
import it.unipmn.di.dcs.sharegrid.submitter.app.render.blender.BlenderJob;
import it.unipmn.di.dcs.sharegrid.submitter.app.render.blender.BlenderJobInputFile;
import it.unipmn.di.dcs.sharegrid.submitter.app.render.blender.BlenderJobOutputFile;
import it.unipmn.di.dcs.sharegrid.submitter.app.render.blender.BlenderOutputFormat;
import it.unipmn.di.dcs.sharegrid.submitter.app.render.blender.BlenderRemoteCommand;
import it.unipmn.di.dcs.sharegrid.submitter.app.render.IRenderJobInputFile;
import it.unipmn.di.dcs.sharegrid.submitter.app.render.IRenderJobOutputFile;
import it.unipmn.di.dcs.sharegrid.submitter.app.render.IRenderRemoteCommand;
import it.unipmn.di.dcs.sharegrid.submitter.app.render.RenderAppException;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A controller class for a blender submitter user interface.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
class BlenderAppUIController implements IBlenderAppUIController
{
	/** Current job. */
	private BlenderJob job;

	/** Job execution handle for current job. */
	private IJobHandle jobHnd;

	private IMiddlewareManager middleware;

	public BlenderAppUIController()
	{
		this.resetJob();
	}

	protected void resetJob()
	{
		// Reset job info
		this.job = new BlenderJob();
		this.job.setSceneName( BlenderApp.GetInstance().getDefaultSceneName( null ) );
		//this.job.setSceneFile( BlenderApp.GetInstance().getDefaultSceneFile( null ) );
		//this.job.setRemoteCommands( BlenderApp.GetInstance().getDefaultRemoteCommands( this.job.getSceneFile() ) );
		//this.job.setRemoteConstraints( BlenderApp.GetInstance().getDefaultRemoteConstraints( this.job.getSceneFile() ) );
		this.job.setStartFrame( BlenderApp.GetInstance().getDefaultStartFrame( this.job.getSceneFile() ) );
		this.job.setEndFrame( BlenderApp.GetInstance().getDefaultEndFrame( this.job.getSceneFile() ) );
		this.job.setStepFrame( BlenderApp.GetInstance().getDefaultStepFrame( this.job.getSceneFile() ) );
		//this.job.setInputFiles( BlenderApp.GetInstance().getDefaultInputFiles( this.job.getSceneFile() ) );
		this.job.setOutputFormat( BlenderApp.GetInstance().getDefaultOutputFormat( this.job.getSceneFile() ) );
		//this.job.setOutputFiles( BlenderApp.GetInstance().getDefaultOutputFiles( this.job.getSceneFile() ) );

		// Reset job execution info
		this.jobHnd = null;
	}

	protected void exportJobToJdf(String fileName) throws RenderAppException
	{
		try
		{
			PrintWriter wr = new PrintWriter( fileName );
			IFormatExporter exporter = new JdfExporter();
			exporter.export(
				this.getJob().getSchedulableBotJob(),
				wr
			);
			wr.close();
		}
		catch (Exception e)
		{
			throw new RenderAppException( "Error while exporting a blender job to JDF format", e );
		}
	}

	protected IMiddlewareManager getMiddleware()
	{
		return ( this.middleware != null ) ? this.middleware : MiddlewareFactory.GetInstance().getMiddlewareManager();
	}

	//@{ IBLenderAppUIController implementation

	public void onNewJobInsertion()
	{
		this.resetJob();
	}

	public void onNewJobInserted()
	{
		// empty
	}

	public void onJobUpdating()
	{
		// empty
	}

	public void onJobUpdated()
	{
		// empty
	}

	public void onSceneNameInsertion()
	{
		if ( Strings.IsNullOrEmpty( this.job.getSceneName() ) )
		{
			this.setDefaultSceneName();
		}
	}

	public void onSceneNameInserted(String value)
	{
		this.job.setSceneName( value );
	}

	public void onSceneFileInsertion()
	{
		if ( Strings.IsNullOrEmpty( this.job.getSceneFile() ) )
		{
			this.setDefaultSceneFile();
		}
	}

	public void onSceneFileInserted(String value)
	{
		this.job.setSceneFile( value );
	}

	public void onRemoteCommandInsertion()
	{
		if ( Collections.IsNullOrEmpty( this.job.getRemoteCommands() ) )
		{
			this.setDefaultRemoteCommands();
		}
	}

	public void onRemoteCommandInserted(String localPath, ITextExpr constraint, String remoteCmd, boolean persistent, boolean overwriteDiff)
	{
		this.job.addRemoteCommand(
			new BlenderRemoteCommand(
				localPath,
				remoteCmd,
				null,
				( constraint != null ? constraint : TextExprs.EMPTY_EXPR ),
				persistent,
				overwriteDiff
			)
		);
	}

	public void onRemoteConstraintsInsertion()
	{
		if ( this.job.getRemoteConstraints() == null )
		{
			this.setDefaultRemoteConstraints();
		}
	}

	public void onRemoteConstraintsInserted(ITextExpr value)
	{
		this.job.setRemoteConstraints( value );
	}

	public void onStartFrameInsertion()
	{
		if ( this.job.getStartFrame() <= 0 )
		{
			this.setDefaultStartFrame();
		}
	}

	public void onStartFrameInserted(int value)
	{
		this.job.setStartFrame( value );

		if ( this.job.getEndFrame() < value )
		{
			this.job.setEndFrame( value );
		}
	}

	public void onEndFrameInsertion()
	{
		if ( this.job.getEndFrame() <= 0 )
		{
			this.setDefaultEndFrame();
		}
	}

	public void onEndFrameInserted(int value)
	{
		this.job.setEndFrame( value );

		if ( this.job.getStartFrame() > value )
		{
			this.job.setStartFrame( value );
		}
	}

	public void onStepFrameInsertion()
	{
		if ( this.job.getStepFrame() <= 0 )
		{
			this.setDefaultStepFrame();
		}
	}

	public void onStepFrameInserted(int value)
	{
		this.job.setStepFrame( value );
	}

	public void onInputFileInsertion()
	{
		if ( Collections.IsNullOrEmpty( this.job.getInputFiles() ) )
		{
			this.setDefaultInputFiles();
		}
	}

	public void onInputFileInserted(String local, String remote, String unpacker, ITextExpr constraint, boolean persistent, boolean overwriteDiff)
	{
		this.job.addInputFile(
			new BlenderJobInputFile(
				local,
				remote,
				unpacker,
				constraint,
				persistent,
				overwriteDiff
			)
		);
	}

	public void onOutputFormatInsertion()
	{
		if ( this.job.getOutputFormat() == null )
		{
			this.setDefaultOutputFormat();
		}
	}

	public void onOutputFormatInserted(BlenderOutputFormat value)
	{
		this.job.setOutputFormat( value );
	}

	public void onOutputFileInsertion()
	{
		if ( Collections.IsNullOrEmpty( this.job.getOutputFiles() ) )
		{
			this.setDefaultOutputFiles();
		}
	}

	public void onOutputFileInserted(String remote, String local, String packer, ITextExpr constraint)
	{
		this.job.addOutputFile(
			new BlenderJobOutputFile(
				remote,
				local,
				packer,
				constraint
			)
		);
	}

	public BlenderJob getJob()
	{
		return this.job;
	}

	public IJobHandle getJobExecHandle()
	{
		return this.jobHnd;
	}

	public boolean canExportJob()
	{
		return (
				!Strings.IsNullOrEmpty( this.getJob().getSceneName() )
				&& !Collections.IsNullOrEmpty( this.getJob().getRemoteCommands() )
				&& !Strings.IsNullOrEmpty( this.getJob().getSceneFile() )
				&& !Collections.IsNullOrEmpty( this.getJob().getOutputFiles() )
			)
			? true
			: false;

	}

	public void exportJob(String fileName, ExportFormatType format) throws RenderAppException
	{
		switch ( format )
		{
			case JDF:
				this.exportJobToJdf( fileName );
				break;
			default:
				throw new RenderAppException( "Unknown format exporter: '" + format + "'" );
		}
	}

	public boolean canExecuteJob()
	{
		return this.canExportJob() && this.getMiddleware().getJobScheduler().isRunning();
	}

	public void executeJob() throws RenderAppException
	{
		IJobHandle jhnd = null;

		try
		{
			if ( !this.canExecuteJob() )
			{
				throw new RenderAppException( "Scheduler is not running." );
			}

			jhnd = this.getMiddleware().getJobScheduler().submitJob(
				this.getJob().getSchedulableBotJob()
			);
		}
		catch (Exception e)
		{
			throw new RenderAppException( "Error while executing current job", e );
		}

		this.jobHnd = jhnd;
	}

	public ExecutionStatus getJobExecStatus()
	{
		try
		{
			if ( this.jobHnd != null )
			{
				return this.getMiddleware().getJobScheduler().getJobStatus( this.jobHnd );
			}
		}
		catch (Exception e)
		{
			// empty
		}

		return null;
	}

	public ExecutionStatus getJobExecStatus(String jobId)
	{
		IJobHandle jhnd = null;

		try
		{
			jhnd = this.getMiddleware().getJobScheduler().getJobHandle(jobId);

			if ( jhnd != null )
			{
				return this.getMiddleware().getJobScheduler().getJobStatus( jhnd );
			}
		}
		catch (Exception e)
		{
			// empty
		}
		return null;

	}

	public void cancelJob() throws RenderAppException
	{
		try
		{
			this.getMiddleware().getJobScheduler().cancelJob( this.jobHnd );
		}
		catch (Exception e)
		{
			throw new RenderAppException( "Error while cancelling the execution of current job", e );
		}
	}

	public void cancelJob(String jobId) throws RenderAppException
	{
		IJobHandle jhnd = null;

		try
		{
			jhnd = this.getMiddleware().getJobScheduler().getJobHandle(jobId);

			this.getMiddleware().getJobScheduler().cancelJob( jhnd );
		}
		catch (Exception e)
		{
			throw new RenderAppException( "Error while cancelling the execution of job '" + jobId + "'", e );
		}
	}

	public IJobMonitor monitorJob() throws RenderAppException
	{
		IJobMonitor monitor = null;

		try
		{
			monitor = this.getMiddleware().getJobScheduler().getJobMonitor( this.jobHnd );
			monitor.run();
		}
		catch (Exception e)
		{
			throw new RenderAppException( "Error while getting the monitor for the execution of current job", e );
		}

		return monitor;
	}

	public IJobMonitor monitorJob(String jobId) throws RenderAppException
	{
		IJobHandle jhnd = null;
		IJobMonitor monitor = null;

		try
		{
			jhnd = this.getMiddleware().getJobScheduler().getJobHandle(jobId);

			monitor = this.getMiddleware().getJobScheduler().getJobMonitor( jhnd );
		}
		catch (Exception e)
		{
			throw new RenderAppException( "Error while monitoring the execution of job '" + jobId + "'", e );
		}

		return monitor;
	}

	public void setDefaultSceneName()
	{
		this.job.setSceneName( BlenderApp.GetInstance().getDefaultSceneName( this.job.getSceneFile() ) );
	}

	public void setDefaultSceneFile()
	{
		this.job.setSceneFile( BlenderApp.GetInstance().getDefaultSceneFile( this.job.getSceneFile() ) );
	}

	public void setDefaultRemoteCommands()
	{
		this.job.setRemoteCommands( BlenderApp.GetInstance().getDefaultRemoteCommands( this.job.getSceneFile() ) );
	}

	public void setDefaultRemoteConstraints()
	{
		this.job.setRemoteConstraints( BlenderApp.GetInstance().getDefaultRemoteConstraints( this.job.getSceneFile() ) );
	}

	public void setDefaultStartFrame()
	{
		this.job.setStartFrame( BlenderApp.GetInstance().getDefaultStartFrame( this.job.getSceneFile() ) );
	}

	public void setDefaultEndFrame()
	{
		this.job.setEndFrame( BlenderApp.GetInstance().getDefaultEndFrame( this.job.getSceneFile() ) );
	}

	public void setDefaultStepFrame()
	{
		this.job.setStepFrame( BlenderApp.GetInstance().getDefaultStepFrame( this.job.getSceneFile() ) );
	}

	public void setDefaultInputFiles()
	{
		this.job.setInputFiles( BlenderApp.GetInstance().getDefaultInputFiles( this.job.getSceneFile() ) );
	}

	public void setDefaultOutputFormat()
	{
		this.job.setOutputFormat( BlenderApp.GetInstance().getDefaultOutputFormat( this.job.getSceneFile() ) );
	}

	public void setDefaultOutputFiles()
	{
		this.job.setOutputFiles( BlenderApp.GetInstance().getDefaultOutputFiles( this.job.getSceneFile() ) );
	}

	//@} IBLenderAppUIController implementation
}
