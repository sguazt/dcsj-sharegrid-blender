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
import it.unipmn.di.dcs.common.util.collection.Collections;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

/**
 * Abstract base class for rendering remote commands.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class AbstractRenderRemoteCommand extends AbstractRenderJobInputFile implements IRenderRemoteCommand
{
	/** Commands to be executed before executing this render command. */
	private List<String> preRenderCmds = new ArrayList<String>();

	/** Commands to be executed after executing this render command. */
	private List<String> afterRenderCmds = new ArrayList<String>();

	private Map<String,List<String>> options = new HashMap<String,List<String>>();

        /** A constructor. */
        protected AbstractRenderRemoteCommand()
        {
		super();
	}

	/** A constructor. */
	protected AbstractRenderRemoteCommand(String local, String remote, String unpacker, ITextExpr constraints, boolean persistent, boolean overwriteDiff)
	{
		super( local, remote, unpacker, constraints, persistent, overwriteDiff );
	}

	/** A constructor. */
	protected AbstractRenderRemoteCommand(String local, String remote, String unpacker, ITextExpr constraints, boolean persistent, boolean overwriteDiff, List<String> preRenderCmds, List<String> afterRenderCmds)
	{
		super( local, remote, unpacker, constraints, persistent, overwriteDiff );

		if ( !Collections.IsNullOrEmpty( preRenderCmds ) )
		{
			this.preRenderCmds.addAll( preRenderCmds );
		}
		else
		{
			this.preRenderCmds.clear();
		}
		if ( !Collections.IsNullOrEmpty( afterRenderCmds ) )
		{
			this.afterRenderCmds.addAll( afterRenderCmds );
		}
		else
		{
			this.afterRenderCmds.clear();
		}
	}

	//@{ IRenderRemoteCommand implementation

	public void setPreRenderRemoteCommands(List<String> value)
	{
		this.preRenderCmds.clear();

		if ( !Collections.IsNullOrEmpty( value ) )
		{
			this.preRenderCmds.addAll( value );
		}
	}

	public List<String> getPreRenderRemoteCommands()
	{
		return this.preRenderCmds;
	}

	public void setAfterRenderRemoteCommands(List<String> value)
	{
		this.afterRenderCmds.clear();

		if ( !Collections.IsNullOrEmpty( value ) )
		{
			this.afterRenderCmds.addAll( value );
		}
	}

	public List<String> getAfterRenderRemoteCommands()
	{
		return this.afterRenderCmds;
	}

	public void setOption(String optName, String optValue)
	{
		List<String> l = new ArrayList<String>();
		l.add( optValue );
		this.options.put( optName, l );
	}

	public void setOption(String optName, List<String> optValues)
	{
		List<String> l = new ArrayList<String>();
		l.addAll( optValues );
		this.options.put( optName, l );
	}

	public String getOption(String optName)
	{
		List<String> l = null;
		l = this.options.get( optName );

		return l != null ? l.get(0) : null;
	}

	public List<String> getOptionValues(String optName)
	{
		List<String> l = null;
		l = this.options.get( optName );

		return l;
	}

	//@} IRenderRemoteCommand implementation
}
