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

import it.unipmn.di.dcs.common.text.TextExprs;
import it.unipmn.di.dcs.common.util.collection.Collections;
import it.unipmn.di.dcs.common.util.Strings;
import it.unipmn.di.dcs.grid.core.middleware.sched.BotJob;
import it.unipmn.di.dcs.grid.core.middleware.sched.BotTask;
import it.unipmn.di.dcs.grid.core.middleware.sched.ConditionalRemoteCommand;
import it.unipmn.di.dcs.grid.core.middleware.sched.ConditionalStageInRule;
import it.unipmn.di.dcs.grid.core.middleware.sched.ConditionalStageOutRule;
import it.unipmn.di.dcs.grid.core.middleware.sched.IBotJob;
import it.unipmn.di.dcs.grid.core.middleware.sched.IBotTask;
import it.unipmn.di.dcs.grid.core.middleware.sched.IJob;
import it.unipmn.di.dcs.grid.core.middleware.sched.IStageIn;
import it.unipmn.di.dcs.grid.core.middleware.sched.IStageInAction;
import it.unipmn.di.dcs.grid.core.middleware.sched.IStageInRule;
import it.unipmn.di.dcs.grid.core.middleware.sched.IStageOut;
import it.unipmn.di.dcs.grid.core.middleware.sched.IStageOutAction;
import it.unipmn.di.dcs.grid.core.middleware.sched.IStageOutRule;
import it.unipmn.di.dcs.grid.core.middleware.sched.JobRequirements;
import it.unipmn.di.dcs.grid.core.middleware.sched.RemoteCommand;
import it.unipmn.di.dcs.grid.core.middleware.sched.RemoteCommandCondition;
import it.unipmn.di.dcs.grid.core.middleware.sched.StageIn;
import it.unipmn.di.dcs.grid.core.middleware.sched.StageInAction;
import it.unipmn.di.dcs.grid.core.middleware.sched.StageInMode;
import it.unipmn.di.dcs.grid.core.middleware.sched.StageInRule;
import it.unipmn.di.dcs.grid.core.middleware.sched.StageInType;
import it.unipmn.di.dcs.grid.core.middleware.sched.StageOut;
import it.unipmn.di.dcs.grid.core.middleware.sched.StageOutAction;
import it.unipmn.di.dcs.grid.core.middleware.sched.StageOutMode;
import it.unipmn.di.dcs.grid.core.middleware.sched.StageOutRule;
import it.unipmn.di.dcs.grid.core.middleware.sched.StagingCondition;
import it.unipmn.di.dcs.sharegrid.submitter.app.render.AbstractRenderJob;
import it.unipmn.di.dcs.sharegrid.submitter.app.render.IRenderJobInputFile;
import it.unipmn.di.dcs.sharegrid.submitter.app.render.IRenderJobOutputFile;
import it.unipmn.di.dcs.sharegrid.submitter.app.render.IRenderRemoteCommand;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;

/**
 * A <em>blender</em> job.
 *
 * A <a href="http://wwww.blender.org">blender</a> job is made up of the
 * following components:
 * <ul>
 * <li>a scene name</li>
 * <li>the path to the <em>blender</em> executable; actually there might exist
 * a different executable according to a target platform.</li>
 * <li>the start frame number</li>
 * <li>the end frame number</li>
 * <li>the step frame number, that is the number of frame each job task will
 * render</li>
 * </ul>
 *
 * See Blender Python classes "Render" and "RenderData" for more information.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class BlenderJob extends AbstractRenderJob
{
	/** The scene file name. */
	private String sceneFile;

	/** The output format (png, jpeg, ...). */
	private BlenderOutputFormat outfmt;

	/** A constructor. */
	public BlenderJob()
	{
		this.sceneFile = "";
		this.outfmt = BlenderOutputFormat.JPEG;
	}

	/** Sets the scene file name. */
	public void setSceneFile(String value)
	{
		this.sceneFile = value;
	}

	/** Returns the scene file name. */
	public String getSceneFile()
	{
		return this.sceneFile;
	}

	/** Set rendering output format (jpeg, png, ...). */
	public void setOutputFormat(BlenderOutputFormat value)
	{
		this.outfmt = value;
	}

	/** Returns rendering output format (jpeg, png, ...). */
	public BlenderOutputFormat getOutputFormat()
	{
		return this.outfmt;
	}

	@Override
	public void setRemoteCommands(Collection<IRenderRemoteCommand> value)
	{
		Iterator<IRenderRemoteCommand> it = value.iterator();
		while ( it.hasNext() )
		{
			IRenderRemoteCommand cmd = it.next();

			if ( cmd.getUploadConstraint() == null || cmd.getUploadConstraint().equals( TextExprs.EMPTY_EXPR ) )
			{
				// No constraint
				// Since blender has only one executable we remove all existent commands
				this.addRemoteCommand(cmd);
				return;
			}
		}

		super.setRemoteCommands(value);
	}

	@Override
	public void addRemoteCommand(IRenderRemoteCommand cmd)
	{
		if ( cmd.getUploadConstraint() == null || cmd.getUploadConstraint().equals( TextExprs.EMPTY_EXPR ) )
		{
			// No constraint
			// Since blender has only one executable we remove all existent commands
			this.getRemoteCommands().clear();
		}
		else
		{
			if (
				this.getRemoteCommandByConstraint( null ) != null
				|| this.getRemoteCommandByConstraint( TextExprs.EMPTY_EXPR ) != null
			)
			{
				// Removes all commands since this constraint is more general 
				this.getRemoteCommands().clear();
			}
		}

		super.addRemoteCommand(cmd);
	}

	@Override
	public IJob getSchedulableJob()
	{
		//TODO
		return null;
	}

	@Override
	public IBotJob getSchedulableBotJob()
	{
		// preconditions
		assert !Strings.IsNullOrEmpty( this.getSceneFile() );

		IBotJob job = new BotJob( this.getSceneName() );

		String remoteSceneFile = new File( this.getSceneFile() ).getName();
		String blenderCommonOptsHead = new String( "-b " + remoteSceneFile );
		String blenderCommonOptsTail = new String( " -a" );

		IStageIn jobStageIn = new StageIn();
		IStageOut jobStageOut = new StageOut();

		// Remote constraints
		if (
			this.getRemoteConstraints() != null
			&& this.getRemoteConstraints() != TextExprs.EMPTY_EXPR
		)
		{
			job.setRequirements(
				new JobRequirements( this.getRemoteConstraints() )
			);
		}

		// Stage-in for Scene file
		{
			// Creates a stage-in action for scene file
			IStageInAction action = new StageInAction();
			action.setLocalName( this.getSceneFile() );
			action.setRemoteName( remoteSceneFile );
			action.setType( StageInType.VOLATILE );
			action.setMode( StageInMode.ALWAYS_OVERWRITE );

			// Adds the action to the job stage-in rule-set
			jobStageIn.addRule(
				new StageInRule( action )
			);
		}

		// Stage-in/Unpacking commands Additional input files
		// Note: this must come before the remote commands translation since
		// before the execution of a remote command it might be necessary to
		// unpack an input file.
		if ( !Collections.IsNullOrEmpty( this.getInputFiles() ) )
		{
			for ( IRenderJobInputFile input : this.getInputFiles() )
			{
				IStageInAction action = new StageInAction();

				action.setLocalName( input.getLocalPath() );

				action.setRemoteName( input.getRemoteName() );

				if ( input.isPersistent() )
				{
					action.setType( StageInType.PERSISTENT );
				}
				else
				{
					action.setType( StageInType.VOLATILE );
				}

				if ( input.overwriteIfDiff() )
				{
					action.setMode( StageInMode.DIFF_OVERWRITE );
				}
				else
				{
					action.setMode( StageInMode.ALWAYS_OVERWRITE );
				}

				IStageInRule rule = null;
				if (
					input.getUploadConstraint() == null
					|| input.getUploadConstraint().equals( TextExprs.EMPTY_EXPR )
				)
				{
					// unconditional stage-in rule
					rule = new StageInRule( action );

					// unconditional remote command for unpacker
					if ( !Strings.IsNullOrEmpty( input.getRemoteUnpackerCommand() ) )
					{
						job.addCommand(
							new RemoteCommand(
								//input.getRemoteUnpackerCommand() + " " + input.getRemoteName()
								input.getRemoteUnpackerCommand() + " " + this.blenderOutputFileToRegularOutputFile( input.getRemoteName() )
							)
						);
					}
				}
				else
				{
					// conditional stage-in rule
					rule = new ConditionalStageInRule(
						new StagingCondition( input.getUploadConstraint() ),
						new StageInRule( action )
					);

					// conditional remote command for unpacker
					if ( !Strings.IsNullOrEmpty( input.getRemoteUnpackerCommand() ) )
					{
						job.addCommand(
							new ConditionalRemoteCommand(
								input.getRemoteUnpackerCommand() + " " + this.blenderOutputFileToRegularOutputFile( input.getRemoteName() ),
								new RemoteCommandCondition( input.getUploadConstraint() )
							)
						);
					}
				}
				if ( rule != null )
				{
					jobStageIn.addRule( rule );
				}
			}
		}

		// Remote commands
		// Note: must come after the input files translation
		if ( !Collections.IsNullOrEmpty( this.getRemoteCommands() ) )
		{
			boolean firstIteration = true; // controls the job stage-in
			for ( int startFrame = this.getStartFrame(); startFrame <= this.getEndFrame(); startFrame += this.getStepFrame() )
			{
				IBotTask task = new BotTask();

				int endFrame = Math.min( startFrame + this.getStepFrame() - 1, this.getEndFrame() );

				for ( IRenderRemoteCommand command : this.getRemoteCommands() )
				{
					String blenderOpts = new String(
						blenderCommonOptsHead
						+ " -s " + startFrame
						+ " -e " + endFrame
						+ ( !Strings.IsNullOrEmpty( command.getOption("-o") ) ? (" -o " + command.getOption("-o")) : "" )
						+ ( this.getOutputFormat() != BlenderOutputFormat.DEFAULT ? (" -F " + this.getOutputFormat() ) : "" )
						+ " " + blenderCommonOptsTail
					);

					IStageInAction action = null;

					if ( !Strings.IsNullOrEmpty( command.getLocalPath() ) )
					{
						// The command must be uploaded to the remote machine

						action = new StageInAction();

						action.setLocalName( command.getLocalPath() );

						action.setRemoteName( command.getRemoteName() );

						if ( command.isPersistent() )
						{
							action.setType( StageInType.PERSISTENT );
						}
						else
						{
							action.setType( StageInType.VOLATILE );
						}

						if ( command.overwriteIfDiff() )
						{
							action.setMode( StageInMode.DIFF_OVERWRITE );
						}
						else
						{
							action.setMode( StageInMode.ALWAYS_OVERWRITE );
						}

						IStageInRule rule = null;
						if (
							command.getUploadConstraint() == null
							|| command.getUploadConstraint().equals( TextExprs.EMPTY_EXPR )
						)
						{
							if ( firstIteration )
							{
								// unconditional stage-in rule
								rule = new StageInRule( action );
							}

							// unconditional remote pre-render commands
							if ( !Collections.IsNullOrEmpty( command.getPreRenderRemoteCommands() ) )
							{
								for (String preRenderCmd : command.getPreRenderRemoteCommands() )
								{
									task.addCommand(
										new RemoteCommand( preRenderCmd )
									);
								}
							}

							// unconditional remote command
							task.addCommand(
								new RemoteCommand(
									command.getRemoteName() + " " + blenderOpts
								)
							);

							// unconditional remote after-render commands
							if ( !Collections.IsNullOrEmpty( command.getAfterRenderRemoteCommands() ) )
							{
								for (String afterRenderCmd : command.getAfterRenderRemoteCommands() )
								{
									task.addCommand(
										new RemoteCommand( afterRenderCmd )
									);
								}
							}
						}
						else
						{
							if ( firstIteration )
							{
								// conditional stage-in rule
								rule = new ConditionalStageInRule(
									new StagingCondition( command.getUploadConstraint() ),
									new StageInRule( action )
								);
							}

							// conditional remote pre-render commands
							if ( !Collections.IsNullOrEmpty( command.getPreRenderRemoteCommands() ) )
							{
								for (String preRenderCmd : command.getPreRenderRemoteCommands() )
								{
									task.addCommand(
										new ConditionalRemoteCommand(
											preRenderCmd,
											new RemoteCommandCondition( command.getUploadConstraint() )
										)
									);
								}
							}

							// conditional remote command
							task.addCommand(
								new ConditionalRemoteCommand(
									command.getRemoteName() + " " + blenderOpts,
									new RemoteCommandCondition( command.getUploadConstraint() )
								)
							);

							// conditional remote after-render commands
							if ( !Collections.IsNullOrEmpty( command.getAfterRenderRemoteCommands() ) )
							{
								for (String afterRenderCmd : command.getAfterRenderRemoteCommands() )
								{
									task.addCommand(
										new ConditionalRemoteCommand(
											afterRenderCmd,
											new RemoteCommandCondition( command.getUploadConstraint() )
										)
									);
								}
							}
						}
						if ( firstIteration && rule != null )
						{
							// Add the rule to the job-level stage-in part
							jobStageIn.addRule( rule );
						}
					}
					else
					{
						// We assume the command is already present onto the remote machine

						if (
							command.getUploadConstraint() == null
							|| command.getUploadConstraint().equals( TextExprs.EMPTY_EXPR )
						)
						{
							// unconditional remote pre-render commands
							if ( !Collections.IsNullOrEmpty( command.getPreRenderRemoteCommands() ) )
							{
								for (String preRenderCmd : command.getPreRenderRemoteCommands() )
								{
									task.addCommand(
										new RemoteCommand( preRenderCmd )
									);
								}
							}

							// unconditional remote command
							task.addCommand(
								new RemoteCommand(
									command.getRemoteName() + " " + blenderOpts
								)
							);

							// unconditional remote after-render commands
							if ( !Collections.IsNullOrEmpty( command.getAfterRenderRemoteCommands() ) )
							{
								for (String afterRenderCmd : command.getAfterRenderRemoteCommands() )
								{
									task.addCommand(
										new RemoteCommand( afterRenderCmd )
									);
								}
							}
						}
						else
						{
							// conditional remote pre-render commands
							if ( !Collections.IsNullOrEmpty( command.getPreRenderRemoteCommands() ) )
							{
								for (String preRenderCmd : command.getPreRenderRemoteCommands() )
								{
									task.addCommand(
										new ConditionalRemoteCommand(
											preRenderCmd,
											new RemoteCommandCondition( command.getUploadConstraint() )
										)
									);
								}
							}

							// conditional remote command
							task.addCommand(
								new ConditionalRemoteCommand(
									command.getRemoteName() + " " + blenderOpts,
									new RemoteCommandCondition( command.getUploadConstraint() )
								)
							);

							// conditional remote after-render commands
							if ( !Collections.IsNullOrEmpty( command.getAfterRenderRemoteCommands() ) )
							{
								for (String afterRenderCmd : command.getAfterRenderRemoteCommands() )
								{
									task.addCommand(
										new ConditionalRemoteCommand(
											afterRenderCmd,
											new RemoteCommandCondition( command.getUploadConstraint() )
										)
									);
								}
							}
						}
					}
				}

				IStageOut taskStageOut = new StageOut();
				// Output files
				// NOTE: if the packer information is present, we must add the
				// packing command to the list of remote commands
				if ( !Collections.IsNullOrEmpty( this.getOutputFiles() ) )
				{
					for ( IRenderJobOutputFile output : this.getOutputFiles() )
					{
						IStageOutAction action = new StageOutAction();

						String remote = null;
						String local = null;

						if ( !Strings.IsNullOrEmpty( output.getRemotePackerCommand() ) )
						{
							// If a packer command is present, the stage-out action
							// must download the packed remote file and not the plain
							// remote file. So in this case we use as remote name the
							// resulting local file

							remote = new File( output.getLocalPath() ).getName() + "_" + startFrame + "_" + endFrame;
							local = output.getLocalPath() + "_" + startFrame + "_" + endFrame;
						}
						else
						{
							// The stage-out action will just download the remote
							// plain file.
							remote = output.getRemoteName();
							local = output.getLocalPath();
						}

						action.setRemoteName( remote );

						action.setLocalName( local );

						action.setMode( StageOutMode.ALWAYS_OVERWRITE );

						IStageOutRule rule = null;
						if (
							output.getDownloadConstraint() == null
							|| output.getDownloadConstraint().equals( TextExprs.EMPTY_EXPR )
						)
						{
							// unconditional stage-out rule
							rule = new StageOutRule( action );

							// unconditional remote command for packer
							if ( !Strings.IsNullOrEmpty( output.getRemotePackerCommand() ) )
							{
								task.addCommand(
									new RemoteCommand(
										output.getRemotePackerCommand()
										+ " " + remote
										+ " " + this.blenderOutputFileToRegularOutputFile( output.getRemoteName() )
									)
								);
							}
						}
						else
						{
							// conditional stage-out rule
							rule = new ConditionalStageOutRule(
								new StagingCondition( output.getDownloadConstraint() ),
								new StageOutRule( action )
							);

							// conditional remote command for packer
							if ( !Strings.IsNullOrEmpty( output.getRemotePackerCommand() ) )
							{
								task.addCommand(
									new ConditionalRemoteCommand(
										output.getRemotePackerCommand()
										+ " " + remote
										+ " " + this.blenderOutputFileToRegularOutputFile( output.getRemoteName() ),
										new RemoteCommandCondition( output.getDownloadConstraint() )
									)
								);
							}
						}
						if ( rule != null )
						{
							taskStageOut.addRule( rule );
						}
					}
				}

				task.setStageOut( taskStageOut );

				if ( task != null )
				{
					job.addTask( task );
				}

				if ( firstIteration )
				{
					firstIteration = false;
				}
			}
		}

		job.setStageIn( jobStageIn );

		return job;
	}

	@Override
	public String toString()
	{
		StringBuilder sb = null;

		sb = new StringBuilder( super.toString() );

		sb.append( "Scene file: '" + this.getSceneFile() + "'" + "\n" );
		sb.append( "Output format: " + this.getOutputFormat() + "\n" );

		return sb.toString();
	}

	private String blenderOutputFileToRegularOutputFile(String file)
	{
		String newfile = file.replaceFirst( "^//", "./" );
		newfile = newfile.replace( "#", this.getStartFrame() + "-" + this.getEndFrame() );

		return newfile;
	}
}
