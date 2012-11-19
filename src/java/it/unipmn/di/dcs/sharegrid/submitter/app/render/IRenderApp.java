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

import java.util.List;

/**
 * Interface for rendering applications.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public interface IRenderApp<T>
{
	/** Returns the default scene name. */
	String getDefaultSceneName(T context);

	/** Returns the default scene file path. */
	String getDefaultSceneFile(T context);

	/** Returns the default start frame number. */
	Integer getDefaultStartFrame(T context);

	/** Returns the default end frame number. */
	Integer getDefaultEndFrame(T context);

	/** Returns the default step frame number. */
	Integer getDefaultStepFrame(T context);

	/** Returns the default remote render commands. */
	List<IRenderRemoteCommand> getDefaultRemoteCommands(T context);

	/** Returns the default remote constraints. */
	ITextExpr getDefaultRemoteConstraints(T context);

	/** Returns the default input files. */
	List<IRenderJobInputFile> getDefaultInputFiles(T context);

	/** Returns the default output files. */
	List<IRenderJobOutputFile> getDefaultOutputFiles(T context);

	/** Returns the default remote render commands. */
	String getDefaultRemoteCommand(T context);

	/** Returns the default local input files. */
	String getDefaultLocalInputFile(T context);

	/** Returns the default remote input files. */
	String getDefaultRemoteInputFile(T context);

	/** Returns the default local output files. */
	String getDefaultLocalOutputFile(T context);

	/** Returns the default remote output files. */
	String getDefaultRemoteOutputFile(T context);
}
