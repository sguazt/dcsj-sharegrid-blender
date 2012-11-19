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
 * Interface for a rendering input file.
 *
 * Each input file information is a made up of five elements:
 * <ul>
 * <li>The path to the local input file.</li>
 * <li>The name (or relative path) of the resulting remote file.</li>
 * <li>The command used to process (i.e. to unpack) the remote input
 *     file.</li>
 * <li>The constraint expression, controlling the upload of the input
 *     file.</li>
 * <li>A flag indicating whether the input file should persist after job
 *     completion.</li>
 * <li>The policy used when writing the file to the remote machine.
 * </ul>
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public interface IRenderJobInputFile
{
	/**
	 * Sets the local path to the input file.
	 */
	void setLocalPath(String value);

	/**
	 * Returns the local path to the input file.
	 */
	String getLocalPath();

	/**
	 * Sets the remote file name of this input file.
	 */
	void setRemoteName(String value);

	/**
	 * Returns the remote file name of this input file.
	 */
	String getRemoteName();

	/**
	 * Sets the remote command used to unpack this input file onto the
	 * remote machine.
	 */
	void setRemoteUnpackerCommand(String value);

	/**
	 * Returns the remote command used to unpack this input file onto the
	 * remote machine.
	 */
	String getRemoteUnpackerCommand();

	/**
	 * Sets the constraints expression controlling the upload of this input
	 * file onto the remote machine.
	 *
	 * This input file will be uploaded only if the given constraint is
	 * satisfied (e.g. upload a specific file only if the operating system
	 * is 'linux').
	 */
	void setUploadConstraint(ITextExpr value);

	/**
	 * Returns the constraints expression controlling the upload of this
	 * input file onto the remote machine.
	 */
	ITextExpr getUploadConstraint();

	/**
	 * Sets the type of remote storage of this input file.
	 *
	 * If {@code value} is {@code true}, this input file will persist onto
	 * the remote machine even after job completion; if {@code false}, the
	 * file will be removed after job completion.
	 */
	void setPersistentFlag(boolean value);

	/**
	 * Returns the type of remote storage of this input file.
	 */
	boolean isPersistent();

	/**
	 * Sets the uploading policy flag.
	 *
	 * If {@code true} the file will be uploaded only if the remote
	 * destination does not contain an identical copy of this file.
	 */
	void setOverwriteIfDiffFlag(boolean value);

	/**
	 * Returns the uploading policy flag.
	 */
	boolean overwriteIfDiff();
}
