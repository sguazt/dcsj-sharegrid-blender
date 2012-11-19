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
import it.unipmn.di.dcs.common.util.Strings;
import it.unipmn.di.dcs.grid.core.middleware.sched.IBotJob;
import it.unipmn.di.dcs.grid.core.middleware.sched.IJob;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Base class for rendering jobs.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public abstract class AbstractRenderJob implements IRenderJob
{
	/** The scene name. */
	private String name;

	/**
	 * The rendering command (there is a rendering command for each constraints
	 * and local path).
	 */
	private Map<ITextExpr,IRenderRemoteCommand> commands = new HashMap<ITextExpr,IRenderRemoteCommand>();

	/** The starting frame to render. */
	private int startFrame;

	/** The last frame to render. */
	private int endFrame;

	/** The number of frame each job task will render. */
	private int stepFrame;

	/** The remote constraints. */
	private ITextExpr constraints;

	/** The input files collection. */
	private List<IRenderJobInputFile> infiles = new ArrayList<IRenderJobInputFile>();

	/** The output files collection. */
	private List<IRenderJobOutputFile> outfiles = new ArrayList<IRenderJobOutputFile>();

	/** Returns the command related to the given constraint. */
	protected IRenderJobInputFile getRemoteCommandByConstraint(ITextExpr constraint)
	{
		return this.commands.get(constraint);
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();

		sb.append( "Scene Name: " + this.getSceneName() + "\n" );
		sb.append( "Start frame: " + this.getStartFrame() + "\n" );
		sb.append( "End frame: " + this.getEndFrame() + "\n" );
		sb.append( "Step frame: " + this.getStepFrame() + "\n" );
		sb.append( "Remote constraints: " + ( (this.getRemoteConstraints() != null) ? this.getRemoteConstraints() : "none" ) + "\n" );
		sb.append( "Remote commands: " + ( Collections.IsNullOrEmpty( this.getRemoteCommands() ) ? "none" : "" ) + "\n" );
		if ( !Collections.IsNullOrEmpty( this.getRemoteCommands() ) )
		{
			for ( IRenderRemoteCommand cmd : this.getRemoteCommands() )
			{
				sb.append(
					"* "
					+ ( !Strings.IsNullOrEmpty( cmd.getLocalPath() ) ? ("'" + cmd.getLocalPath() + "'") : "<NA>" )
					+ " ==> "
					+ ( !Strings.IsNullOrEmpty( cmd.getRemoteName() ) ? ("'" + cmd.getRemoteName() + "'") : "<NA>" )
					+ " ["
					+ ( cmd.isPersistent() ? "persistent" : "volatile" )
					+ "; "
					+ ( cmd.overwriteIfDiff() ? "overwrite if different" : "always overwrite" )
					+ "]"
					+ " (unpacker: " + ( !Strings.IsNullOrEmpty( cmd.getRemoteUnpackerCommand() ) ? ("'" + cmd.getRemoteUnpackerCommand() + "')") : "<NA>" ) + ")"
					+ " IF " + ( cmd.getUploadConstraint() != null ? ("'" + cmd.getUploadConstraint() + "'") : "<NA>" )
					+ "\n"
				);
			}
		}
		sb.append( "Input files: " + ( Collections.IsNullOrEmpty( this.getInputFiles() ) ? "none" : "" ) + "\n" );
		if ( !Collections.IsNullOrEmpty( this.getInputFiles() ) )
		{
			for ( IRenderJobInputFile input : this.getInputFiles() )
			{
				sb.append(
					"* "
					+ ( !Strings.IsNullOrEmpty( input.getLocalPath() ) ? ("'" + input.getLocalPath() + "'") : "<NA>" )
					+ " ==> "
					+ ( !Strings.IsNullOrEmpty( input.getRemoteName() ) ? ("'" + input.getRemoteName() + "'") : "<NA>" )
					+ " ["
					+ ( input.isPersistent() ? "persistent" : "volatile" )
					+ "; "
					+ ( input.overwriteIfDiff() ? "overwrite if different" : "always overwrite" )
					+ "]"
					+ " (unpacker: " + ( !Strings.IsNullOrEmpty( input.getRemoteUnpackerCommand() ) ? ("'" + input.getRemoteUnpackerCommand() + "')") : "<NA>" )
					+ " IF " + ( input.getUploadConstraint() != null ? ("'" + input.getUploadConstraint() + "'") : "<NA>" )
					+ "\n"
				);
			}
		}
		sb.append( "Output files: " + ( Collections.IsNullOrEmpty( this.getInputFiles() ) ? "none" : "" ) + "\n" );
		if ( !Collections.IsNullOrEmpty( this.getOutputFiles() ) )
		{
			for ( IRenderJobOutputFile output : this.getOutputFiles() )
			{
				sb.append(
					"* "
					+ ( !Strings.IsNullOrEmpty( output.getRemoteName() ) ? ("'" + output.getRemoteName() + "'") : "<NA>" )
					+ " ==> "
					+ ( !Strings.IsNullOrEmpty( output.getLocalPath() ) ? ("'" + output.getLocalPath() + "'") : "<NA>" )
					+ " (packer: " + ( !Strings.IsNullOrEmpty( output.getRemotePackerCommand() ) ? ("'" + output.getRemotePackerCommand() + "')") : "<NA>" )
					+ " IF " + ( output.getDownloadConstraint() != null ? ("'" + output.getDownloadConstraint() + "'") : "<NA>" )
					+ "\n"
				);
			}
		}

		return sb.toString();
	}

	//@{ IRenderJob implementation

	public void setSceneName(String value)
	{
		this.name = value;
	}

	public String getSceneName()
	{
		return this.name;
	}

	public void setRemoteCommands(Collection<IRenderRemoteCommand> value)
	{
		this.commands.clear();

		for ( IRenderRemoteCommand cmd : value )
		{
			this.commands.put( cmd.getUploadConstraint(), cmd );
		}
	}

	public void addRemoteCommand(IRenderRemoteCommand cmd)
	{
		this.commands.put( cmd.getUploadConstraint(), cmd );
	}

	public Collection<IRenderRemoteCommand> getRemoteCommands()
	{
		return this.commands.values();
	}

	public void setStartFrame(int value)
	{
		this.startFrame = value;
	}

	public int getStartFrame()
	{
		return this.startFrame;
	}

	public void setEndFrame(int value)
	{
		this.endFrame = value;
	}

	public int getEndFrame()
	{
		return this.endFrame;
	}

	public void setStepFrame(int value)
	{
		this.stepFrame = value;
	}

	public int getStepFrame()
	{
		return this.stepFrame;
	}

	public void setRemoteConstraints(ITextExpr value)
	{
		this.constraints = value;
	}

	public ITextExpr getRemoteConstraints()
	{
		return this.constraints;
	}

	public void setInputFiles(Collection<IRenderJobInputFile> infiles)
	{
		this.infiles.clear();
		this.infiles.addAll(infiles);
	}

	public void addInputFile(IRenderJobInputFile infile)
	{
		this.infiles.add(infile);
	}

	public Collection<IRenderJobInputFile> getInputFiles()
	{
		return this.infiles;
	}

	public void removeInputFiles()
	{
		this.infiles.clear();
	}

	public void setOutputFiles(Collection<IRenderJobOutputFile> outfiles)
	{
		this.outfiles.clear();
		this.outfiles.addAll(outfiles);
	}

	public void addOutputFile(IRenderJobOutputFile outfile)
	{
		this.outfiles.add(outfile);
	}

	public Collection<IRenderJobOutputFile> getOutputFiles()
	{
		return this.outfiles;
	}

	public void removeOutputFiles()
	{
		this.outfiles.clear();
	}

	public abstract IJob getSchedulableJob();

	public abstract IBotJob getSchedulableBotJob();

	//@} IRenderJob implementation
}
