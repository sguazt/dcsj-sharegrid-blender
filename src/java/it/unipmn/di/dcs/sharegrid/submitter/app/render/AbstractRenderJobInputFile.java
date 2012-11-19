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
import it.unipmn.di.dcs.common.text.TextExprs;

/**
 * Class for a rendering job input file.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public abstract class AbstractRenderJobInputFile implements IRenderJobInputFile
{
	/** The local input file path. */
	private String local;

	/** The remote file name. */
	private String remote;

	/** The remote unpacker command (with options). */
	private String unpacker;

	/** The constraints expression controlling the file upload. */
	private ITextExpr constraints = TextExprs.EMPTY_EXPR;

	/** The flag controlling the type of remote storage. */
	private boolean persistent;

	/** The uploading policy. */
	private boolean overwriteDiff;

	/** A constructor. */
	protected AbstractRenderJobInputFile()
	{
		// empty
	}

	/** A constructor. */
	protected AbstractRenderJobInputFile(String local, String remote, String unpacker, ITextExpr constraints, boolean persistent, boolean overwriteDiff)
	{
		this.local = local;
		this.remote = remote;
		this.unpacker = unpacker;
		this.constraints = ( constraints != null ) ? constraints : TextExprs.EMPTY_EXPR;
		this.persistent = persistent;
		this.overwriteDiff = overwriteDiff;
	}

	//@{ IRenderJobInputFile

	public void setLocalPath(String value)
	{
		this.local = value;
	}

	public String getLocalPath()
	{
		return this.local;
	}

	public void setRemoteName(String value)
	{
		this.remote = value;
	}

	public String getRemoteName()
	{
		return this.remote;
	}

	public void setRemoteUnpackerCommand(String value)
	{
		this.unpacker = value;
	}

	public String getRemoteUnpackerCommand()
	{
		return this.unpacker;
	}

	public void setUploadConstraint(ITextExpr value)
	{
		this.constraints = value;
	}

	public ITextExpr getUploadConstraint()
	{
		return this.constraints;
	}

	public void setPersistentFlag(boolean value)
	{
		this.persistent = value;
	}

	public boolean isPersistent()
	{
		return this.persistent;
	}

	public void setOverwriteIfDiffFlag(boolean value)
	{
		this.overwriteDiff = value;
	}

	public boolean overwriteIfDiff()
	{
		return this.overwriteDiff;
	}

	//@} IRenderJobInputFile
}
