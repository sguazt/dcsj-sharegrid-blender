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

/**
 * Interface for a rendering output file.
 *
 * Each output file information is a made up of the following elements:
 * <ul>
 * <li>The name (or relative path) of the remote file.</li>
 * <li>The path to the resulting local input file.</li>
 * <li>The command used to process (i.e. to pack) the remote output
 *     file.</li>
 * <li>The constraint expression, controlling the download of the output
 *     file.</li>
 * </ul>
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public interface IRenderJobOutputFile
{
	/**
	 * Sets the remote file name of this output file.
	 */
	void setRemoteName(String value);

	/**
	 * Returns the remote file name of this output file.
	 */
	String getRemoteName();

	/**
	 * Sets the local path to the output file.
	 */
	void setLocalPath(String value);

	/**
	 * Returns the local path to the output file.
	 */
	String getLocalPath();

	/**
	 * Sets the remote command used to pack this output file onto the
	 * remote machine.
	 */
	void setRemotePackerCommand(String value);

	/**
	 * Returns the remote command used to pack this output file onto the
	 * remote machine.
	 */
	String getRemotePackerCommand();

	/**
	 * Sets the constraints expression controlling the download of this
	 * output file from the remote machine.
	 *
	 * This output file will be download only if the given constraint is
	 * satisfied (e.g. download a specific file only if the operating system
	 * of the remote machine is 'linux').
	 */
	void setDownloadConstraint(ITextExpr value);

	/**
	 * Returns the constraints expression controlling the download of this
	 * output file from the remote machine.
	 */
	ITextExpr getDownloadConstraint();
}
