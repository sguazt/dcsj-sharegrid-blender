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

package it.unipmn.di.dcs.sharegrid.submitter.app.render;

import it.unipmn.di.dcs.common.text.ITextExpr;
import it.unipmn.di.dcs.grid.core.middleware.sched.IBotJob;
import it.unipmn.di.dcs.grid.core.middleware.sched.IJob;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Interface for rendering jobs.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public interface IRenderJob
{
	/**
	 * Sets the descriptive name of the scene/job.
	 */
	void setSceneName(String value);

	/**
	 * Returns the descriptive name of the scene/job.
	 */
	String getSceneName();

	/**
	 * Sets the collection of remote commands (with their related
	 * constraints and local paths).
	 */
	void setRemoteCommands(Collection<IRenderRemoteCommand> commands);

	/**
	 * Adds a remote command with the given constraints.
	 *
	 * There might be different executables, according to the given set of
	 * constraints and the local path.
	 */
	void addRemoteCommand(IRenderRemoteCommand command);

	/**
	 * Returns the complete collection of commands along with their
	 * related constraints and loca paths.
	 */
	Collection<IRenderRemoteCommand> getRemoteCommands();

	/**
	 * Sets the first frame to be rendered.
	 */
	void setStartFrame(int value);

	/**
	 * Returns the first frame to be rendered.
	 */
	int getStartFrame();

	/**
	 * Sets the last frame to be rendered.
	 */
	void setEndFrame(int value);

	/**
	 * Returns the last frame to be rendered.
	 */
	int getEndFrame();

	/**
	 * Sets the number of frames in which subdividing the rendering.
	 */
	void setStepFrame(int value);

	/**
	 * Returns the number of frames in which subdividing the rendering.
	 */
	int getStepFrame();

	/**
	 * Sets the constraints expression used for discriminating the choose
	 * of remote machines.
	 */
	void setRemoteConstraints(ITextExpr value);

	/**
	 * Returns the constraints expression used for discriminating the choose
	 * of remote machines.
	 */
	ITextExpr getRemoteConstraints();

	/**
	 * Sets a list of input files.
	 */
	void setInputFiles(Collection<IRenderJobInputFile> infiles);

	/**
	 * Adds a input file.
	 */
	void addInputFile(IRenderJobInputFile infile);

	/**
	 * Returns the list of input files.
	 */
	Collection<IRenderJobInputFile> getInputFiles();

	/**
	 * Removes all input files.
	 */
	void removeInputFiles();

	/**
	 * Sets a list of output files.
	 */
	void setOutputFiles(Collection<IRenderJobOutputFile> outfiles);

	/**
	 * Adds a output file.
	 */
	void addOutputFile(IRenderJobOutputFile outfile);

	/**
	 * Returns the list of output files.
	 */
	Collection<IRenderJobOutputFile> getOutputFiles();

	/**
	 * Removes all output files.
	 */
	void removeOutputFiles();

	/**
	 * Returns a representation of this job as a {@code IJob} object.
	 */
	IJob getSchedulableJob();

	/**
	 * Returns a representation of this job as a {@code IBotJob} object.
	 */
	IBotJob getSchedulableBotJob();
}
