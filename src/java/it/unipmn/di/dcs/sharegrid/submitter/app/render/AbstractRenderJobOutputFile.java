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

package it.unipmn.di.dcs.sharegrid.submitter.app.render;

import it.unipmn.di.dcs.common.text.ITextExpr;
import it.unipmn.di.dcs.common.text.TextExprs;

/**
 * Class for a rendering job output file.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public abstract class AbstractRenderJobOutputFile implements IRenderJobOutputFile
{
	/** The remote file path. */
	private String remote;

	/** The local input file path. */
	private String local;

	/** The remote packer command (with options). */
	private String packer;

	/** The constraints expression controlling the file download. */
	private ITextExpr constraints = TextExprs.EMPTY_EXPR;

	/** A constructor. */
	protected AbstractRenderJobOutputFile()
	{
		// empty
	}

	/** A constructor. */
	protected AbstractRenderJobOutputFile(String remote, String local, String packer, ITextExpr constraints)
	{
		this.remote = remote;
		this.local = local;
		this.packer = packer;
		this.constraints = ( constraints != null ) ? constraints : TextExprs.EMPTY_EXPR;
	}

	//@{ IRenderJobOutputFile

	public void setRemoteName(String value)
	{
		this.remote = value;
	}

	public String getRemoteName()
	{
		return this.remote;
	}

	public void setLocalPath(String value)
	{
		this.local = value;
	}

	public String getLocalPath()
	{
		return this.local;
	}

	public void setRemotePackerCommand(String value)
	{
		this.packer = value;
	}

	public String getRemotePackerCommand()
	{
		return this.packer;
	}

	public void setDownloadConstraint(ITextExpr value)
	{
		this.constraints = value;
	}

	public ITextExpr getDownloadConstraint()
	{
		return this.constraints;
	}

	//@} IRenderJobOutputFile
}
