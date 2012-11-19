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

package it.unipmn.di.dcs.sharegrid.submitter.app.render.blender;

import it.unipmn.di.dcs.common.text.BinaryTextExpr;
import it.unipmn.di.dcs.common.text.ITextExpr;
import it.unipmn.di.dcs.common.text.TerminalTextExpr;
import it.unipmn.di.dcs.common.text.TextExprs;
import it.unipmn.di.dcs.grid.core.middleware.sched.JobRequirementsOps;
import it.unipmn.di.dcs.sharegrid.submitter.app.render.IRenderApp;
import it.unipmn.di.dcs.sharegrid.submitter.app.render.IRenderJobInputFile;
import it.unipmn.di.dcs.sharegrid.submitter.app.render.IRenderJobOutputFile;
import it.unipmn.di.dcs.sharegrid.submitter.app.render.IRenderRemoteCommand;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Class representing a Blender application.
 *
 * Regarding the implementation of {@code IRenderApp} interface, the
 * {@code context} argument represents the scene file name; it can be
 * {@code null}.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class BlenderApp implements IRenderApp<String>
{
	/** The singleton instance. */
	private static BlenderApp instance;

	/** A constructor. */
	private BlenderApp()
	{
		// empty
	}

	/** Returns the singleton instance. */
	public static synchronized BlenderApp GetInstance()
	{
		if ( BlenderApp.instance == null )
		{
			BlenderApp.instance = new BlenderApp();
		}

		return BlenderApp.instance;
	}

	public BlenderOutputFormat getDefaultOutputFormat(String context)
	{
		return BlenderOutputFormat.DEFAULT;
	}

	public String getDefaultPackerCommand(String context)
	{
		return "zip -r -9 -q";
	}

	public String getDefaultUnpackerCommand(String context)
	{
		return "unzip -qq -o";
	}

	public String getDefaultLocalInputFile(String context)
	{
		if ( context != null )
		{
			return ( new File(context).getName() ) + "-extras.zip";
		}
		return this.getDefaultSceneName(null) + "-extras.zip";
	}

	public String getDefaultRemoteInputFile(String context)
	{
		if ( context != null )
		{
			return ( new File(context).getName() ) + "-extras.zip";
		}
		return this.getDefaultSceneName(null) + "-extras.zip";
	}

	public String getDefaultRemoteOutputFile(String context)
	{
		if ( context != null )
		{
			return ( new File(context).getName() ) + "-rendered";
		}
		return this.getDefaultSceneName(null) + "-rendered";
	}

	public String getDefaultLocalOutputFile(String context)
	{
		String cwd = new File( System.getProperty( "user.dir" ) ).getAbsolutePath();

		return cwd + "/" + this.getDefaultRemoteOutputFile(context) + ".zip";
	}

	//@{ IRenderApp implementation

	public String getDefaultSceneName(String context)
	{
		SimpleDateFormat dtFormatter = new SimpleDateFormat( "yyyyMMdd_HHmm" );
		String user;

		try
		{
			user = System.getProperty("user.name");
		}
		catch (Exception e)
		{
			// Unable to get the user.name properties
			// Set a default one
			user = "nouser";
		}

		return user + "-blender_" + dtFormatter.format( new Date() );
	}

	public String getDefaultSceneFile(String context)
	{
		return this.getDefaultSceneName( context ) + ".blend";
	}

	public Integer getDefaultStartFrame(String context)
	{
		return 1;
	}

	public Integer getDefaultEndFrame(String context)
	{
		return 1;
	}

	public Integer getDefaultStepFrame(String context)
	{
		return 25;
	}

	public String getDefaultRemoteCommand(String context)
	{
		return "blender";
	}

	public List<IRenderRemoteCommand> getDefaultRemoteCommands(String context)
	{
		List<IRenderRemoteCommand> list = null;
		list = new ArrayList<IRenderRemoteCommand>();
		list.add(
			new BlenderRemoteCommand(
				null,
				this.getDefaultRemoteCommand(context),
				null,
				TextExprs.EMPTY_EXPR,
				false,
				false
			)
		);
		return list;
	}

	public ITextExpr getDefaultRemoteConstraints(String context)
	{
		return new BinaryTextExpr(
			new TerminalTextExpr( "blender" ),
			JobRequirementsOps.EQ,
			new TerminalTextExpr( "true" )
		);
	}

	public List<IRenderJobInputFile> getDefaultInputFiles(String context)
	{
		List<IRenderJobInputFile> list = null;
		list = new ArrayList<IRenderJobInputFile>();
		list.add(
			new BlenderJobInputFile(
				this.getDefaultLocalInputFile(context),
				this.getDefaultRemoteInputFile(context),
				this.getDefaultUnpackerCommand(context),
				null,
				false,
				false
			)
		);
		return list;
	}

	public List<IRenderJobOutputFile> getDefaultOutputFiles(String context)
	{
		List<IRenderJobOutputFile> list = null;
		list = new ArrayList<IRenderJobOutputFile>();
		list.add(
			new BlenderJobOutputFile(
				this.getDefaultRemoteOutputFile(context),
				this.getDefaultLocalOutputFile(context),
				this.getDefaultPackerCommand(context),
				null
			)
		);
		return list;
	}

	//@} IRenderApp implementation
}
