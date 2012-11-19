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
import it.unipmn.di.dcs.grid.core.format.ExportFormatType;
import it.unipmn.di.dcs.grid.core.middleware.sched.ExecutionStatus;
import it.unipmn.di.dcs.grid.core.middleware.sched.IJobHandle;
import it.unipmn.di.dcs.grid.core.middleware.sched.monitor.IJobMonitor;
import it.unipmn.di.dcs.sharegrid.submitter.app.render.blender.BlenderJob;
import it.unipmn.di.dcs.sharegrid.submitter.app.render.blender.BlenderOutputFormat;
import it.unipmn.di.dcs.sharegrid.submitter.app.render.RenderAppException;

/**
 * Interface for user interface controllers of <em>blender</em> applications.
 *
 * This interface defines several handlers to events dispatched by an
 * AppUI view.
 * These events are:
 * <ul>
 * <li><em>new-job-insertion</em>: a new job will be inserted.</li>
 * <li><em>new-job-inserted</em>: a new job has been inserted.</li>
 * <li><em>job-updating</em>: an existing job will be updated.</li>
 * <li><em>job-updated</em>: an existing job has been updated.</li>
 * <li><em>scene-name-insertion</em>: a new value for the scene name will be inserted.</li>
 * <li><em>scene-name-inserted</em>: a new value for the scene name has been inserted.</li>
 * <li><em>scene-file-insertion</em>: a new value for the scene file will be inserted.</li>
 * <li><em>scene-file-inserted</em>: a new value for the scene file has been inserted.</li>
 * <li><em>remote-command-insertion</em>: a new value for the remote command will be inserted.</li>
 * <li><em>remote-command-inserted</em>: a new value for the remote command has been inserted.</li>
 * <li><em>remote-constraint-insertion</em>: a new value for the remote constraint will be inserted.</li>
 * <li><em>remote-constraint-inserted</em>: a new value for the remote constraint has been inserted.</li>
 * <li><em>start-frame-insertion</em>: a new value for the start frame will be inserted.</li>
 * <li><em>start-frame-inserted</em>: a new value for the start frame has been inserted.</li>
 * <li><em>end-frame-insertion</em>: a new value for the end frame will be inserted.</li>
 * <li><em>end-frame-inserted</em>: a new value for the end frame has been inserted.</li>
 * <li><em>step-frame-insertion</em>: a new value for the step frame will be inserted.</li>
 * <li><em>step-frame-inserted</em>: a new value for the step frame has been inserted.</li>
 * <li><em>input-file-insertion</em>: a new value for the scene input file will be inserted.</li>
 * <li><em>input-file-inserted</em>: a new value for the scene input file has been inserted.</li>
 * <li><em>output-format-insertion</em>: a new value for the output format will be inserted.</li>
 * <li><em>output-format-inserted</em>: a new value for the output format has been inserted.</li>
 * <li><em>output-file-insertion</em>: a new value for the scene input file will be inserted.</li>
 * <li><em>output-file-inserted</em>: a new value for the scene input file has been inserted.</li>
 * </ul>
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public interface IBlenderAppUIController
{
	/**
	 * Handle the <em>new-job-insertion</em> event.
	 */
	void onNewJobInsertion();

	/**
	 * Handle the <em>new-job-inserted</em> event.
	 */
	void onNewJobInserted();

	/**
	 * Handle the <em>job-updating</em> event.
	 */
	void onJobUpdating();

	/**
	 * Handle the <em>job-updated</em> event.
	 */
	void onJobUpdated();

	/**
	 * Handle the <em>scene-name-insertion</em> event.
	 */
	void onSceneNameInsertion();

	/**
	 * Handle the <em>scene-name-inserted</em> event.
	 */
	void onSceneNameInserted(String value);

	/**
	 * Handle the <em>scene-file-insertion</em> event.
	 */
	void onSceneFileInsertion();

	/**
	 * Handle the <em>scene-file-inserted</em> event.
	 */
	void onSceneFileInserted(String value);

	/**
	 * Handle the <em>remote-command-insertion</em> event.
	 */
	void onRemoteCommandInsertion();

	/**
	 * Handle the <em>remote-command-inserted</em> event.
	 */
	void onRemoteCommandInserted(String localPath, ITextExpr constraint, String remoteCmd, boolean persistent, boolean overwriteDiff);

	/**
	 * Handle the <em>remote-constraint-insertion</em> event.
	 */
	void onRemoteConstraintsInsertion();

	/**
	 * Handle the <em>remote-constraint-inserted</em> event.
	 */
	void onRemoteConstraintsInserted(ITextExpr value);

	/**
	 * Handle the <em>start-frame-insertion</em> event.
	 */
	void onStartFrameInsertion();

	/**
	 * Handle the <em>start-frame-inserted</em> event.
	 */
	void onStartFrameInserted(int value);

	/**
	 * Handle the <em>end-frame-insertion</em> event.
	 */
	void onEndFrameInsertion();

	/**
	 * Handle the <em>end-frame-inserted</em> event.
	 */
	void onEndFrameInserted(int value);

	/**
	 * Handle the <em>step-frame-insertion</em> event.
	 */
	void onStepFrameInsertion();

	/**
	 * Handle the <em>step-frame-inserted</em> event.
	 */
	void onStepFrameInserted(int value);

	/**
	 * Handle the <em>input-file-insertion</em> event.
	 */
	void onInputFileInsertion();

	/**
	 * Handle the <em>input-file-inserted</em> event.
	 */
	void onInputFileInserted(String local, String remote, String unpacker, ITextExpr constraint, boolean persistent, boolean overwriteDiff);

	/**
	 * Handle the <em>output-format-insertion</em> event.
	 */
	void onOutputFormatInsertion();

	/**
	 * Handle the <em>output-format-inserted</em> event.
	 */
	void onOutputFormatInserted(BlenderOutputFormat value);

	/**
	 * Handle the <em>output-file-insertion</em> event.
	 */
	void onOutputFileInsertion();

	/**
	 * Handle the <em>output-file-inserted</em> event.
	 */
	void onOutputFileInserted(String remote, String local, String packer, ITextExpr constraint);

	/**
	 * Returns the current job.
	 */
	BlenderJob getJob();

	/**
	 * Returns the current job.
	 */
	IJobHandle getJobExecHandle();

	/**
	 * Exports the current job to the specified format.
	 */
	void exportJob(String fileName, ExportFormatType format) throws RenderAppException;

	/**
	 * Tells if the current job can be exported.
	 */
	boolean canExportJob();

	/**
	 * Executes the current job.
	 */
	void executeJob() throws RenderAppException;

	/**
	 * Tells if the current job can be executed.
	 */
	boolean canExecuteJob();

	/**
	 * Returns the execution status of current job.
	 */
	ExecutionStatus getJobExecStatus();

	/**
	 * Returns the execution status of the job identified by {@code jobId}.
	 */
	ExecutionStatus getJobExecStatus(String jobId);

	/**
	 * Cancel the execution of the current job.
	 */
	void cancelJob() throws RenderAppException;

	/**
	 * Cancel the execution of the job identified by {@code jobId}.
	 */
	void cancelJob(String jobId) throws RenderAppException;

	/**
	 * Monitor the execution of the current job.
	 */
	IJobMonitor monitorJob() throws RenderAppException;

	/**
	 * Monitor the execution of the job identified by {@code jobId}.
	 */
	IJobMonitor monitorJob(String jobId) throws RenderAppException;

	/**
	 * Sets the default scene name for current job.
	 */
	void setDefaultSceneName();

	/**
	 * Sets the default scene file for current job.
	 */
	void setDefaultSceneFile();

	/**
	 * Sets the default remote commands for current job.
	 */
	void setDefaultRemoteCommands();

	/**
	 * Sets the default remote constraints for current job.
	 */
	void setDefaultRemoteConstraints();

	/**
	 * Sets the default start frame for current job.
	 */
	void setDefaultStartFrame();

	/**
	 * Sets the default end frame for current job.
	 */
	void setDefaultEndFrame();

	/**
	 * Sets the default step frame for current job.
	 */
	void setDefaultStepFrame();

	/**
	 * Sets the default input files for current job.
	 */
	void setDefaultInputFiles();

	/**
	 * Sets the default output format for current job.
	 */
	void setDefaultOutputFormat();

	/**
	 * Sets the default output files for current job.
	 */
	void setDefaultOutputFiles();
}
