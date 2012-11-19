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

import java.util.List;

/**
 * Interface for rendering executables.
 *
 * Each class for a rendering application (like Blender, Terragen, Maya, ...)
 * should extends this interface by adding options specific to that application.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public interface IRenderRemoteCommand extends IRenderJobInputFile
{
	/** Sets the commands to execute just before the rendering. */
	void setPreRenderRemoteCommands(List<String> value);

	/** Returns the commands to execute just before the rendering. */
	List<String> getPreRenderRemoteCommands();

	/** Sets the commands to execute just after the rendering. */
	void setAfterRenderRemoteCommands(List<String> value);

	/** Returns the commands to execute just after the rendering. */
	List<String> getAfterRenderRemoteCommands();

	/**
	 * Sets the remote command option {@code optName} to the given value
	 * {@code optValue}.
	 */
	void setOption(String optName, String optValue);

	/**
	 * Sets the remote command option {@code optName} to the given multiple
	 * values {@code optValues}.
	 */
	void setOption(String optName, List<String> optValues);

	/**
	 * Returns the value of the given remote command option.
	 *
	 * If {@code optName} has multiple values, the method returns
	 * the first one.
	 */
	String getOption(String optName);

	/** Returns all the values of the given remote command option. */
	List<String> getOptionValues(String optName);
}
