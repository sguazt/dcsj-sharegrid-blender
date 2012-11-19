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

package it.unipmn.di.dcs.sharegrid.submitter.app.render.blender.ui;

import it.unipmn.di.dcs.common.ui.AnsiTextConsoleUIDriver;
import it.unipmn.di.dcs.common.ui.TextConsoleUIDriver;
import it.unipmn.di.dcs.common.ui.UIException;
import it.unipmn.di.dcs.common.ui.IUIDriver;
import it.unipmn.di.dcs.common.text.ITextExpr;
import it.unipmn.di.dcs.common.text.ITextOp;
import it.unipmn.di.dcs.common.text.ParenthesizedTextExpr;
import it.unipmn.di.dcs.common.text.TerminalTextExpr;
import it.unipmn.di.dcs.common.text.TextOpType;
import it.unipmn.di.dcs.common.text.TextExprs;
import it.unipmn.di.dcs.common.util.Strings;
import it.unipmn.di.dcs.common.util.collection.Collections;
import it.unipmn.di.dcs.grid.core.format.ExportFormatType;
import it.unipmn.di.dcs.grid.core.middleware.IMiddlewareManager;
import it.unipmn.di.dcs.grid.core.middleware.WorkerProperties;
import it.unipmn.di.dcs.grid.core.middleware.sched.ExecutionStatus;
import it.unipmn.di.dcs.grid.core.middleware.sched.JobRequirements;
import it.unipmn.di.dcs.grid.core.middleware.sched.JobRequirementsOps;
import it.unipmn.di.dcs.grid.core.middleware.sched.JobRequirementsUtil;
import it.unipmn.di.dcs.grid.core.middleware.sched.monitor.IJobMonitor;
import it.unipmn.di.dcs.sharegrid.submitter.app.render.blender.BlenderApp;
import it.unipmn.di.dcs.sharegrid.submitter.app.render.blender.BlenderJob;
import it.unipmn.di.dcs.sharegrid.submitter.app.render.blender.BlenderJobInputFile;
import it.unipmn.di.dcs.sharegrid.submitter.app.render.blender.BlenderOutputFormat;
import it.unipmn.di.dcs.sharegrid.submitter.app.render.blender.BlenderRemoteCommand;
import it.unipmn.di.dcs.sharegrid.submitter.app.render.IRenderJobInputFile;
import it.unipmn.di.dcs.sharegrid.submitter.app.render.IRenderJobOutputFile;
import it.unipmn.di.dcs.sharegrid.submitter.app.render.IRenderRemoteCommand;
import it.unipmn.di.dcs.sharegrid.submitter.app.render.RenderAppException;
import it.unipmn.di.dcs.sharegrid.submitter.MiddlewareFactory;
import it.unipmn.di.dcs.sharegrid.monitor.app.job.ui.IJobMonitorAppUI;
import it.unipmn.di.dcs.sharegrid.monitor.app.job.ui.GriddedAppGUI;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Textual oriented interface for blender job submissions.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public final class ConsoleAppUI
{
	/** The console driver. */
	private IUIDriver uiDriver;

	/** The application name. */
	public static final String AppName = "DcsGridBlender";

	/** The application version. */
	public static final String Version = "1.0.9";

	/** A comparator for the BlenderOutputFormat type. */
	private static Comparator<BlenderOutputFormat> BlenderOutputFormatCmp;

	/** Flag for controlling the ease of main menu. */
	private boolean useSimpleMode = true; // TODO: add a CLI option

	/** Flag for controlling the job requirements menu. */
	private boolean useSimpleConstraints = true; // TODO: add a CLI option

	/** The controller for this view. */
	private IBlenderAppUIController controller;

	/**
	 * Bitmask for keeping trace of what information has been entered
	 * by the user.
	 */
	private int filledFieldMask = 0x000;

	private static final int SCENENAME_MASK = 0x001;

	private static final int BLENDEREXEC_MASK = 0x002;

	private static final int SCENEFILE_MASK = 0x004;

	private static final int STARTFRAME_MASK = 0x008;

	private static final int ENDFRAME_MASK = 0x010;

	private static final int STEPFRAME_MASK = 0x020;

	private static final int REMOTECONSTRAINTS_MASK = 0x040;

	private static final int INPUTFILES_MASK = 0x080;

	private static final int OUTPUTFORMAT_MASK = 0x100;

	private static final int OUTPUTFILES_MASK = 0x200;

	// Static initializations
	static
	{
		ConsoleAppUI.BlenderOutputFormatCmp = new Comparator<BlenderOutputFormat>()
		{
			public int compare(BlenderOutputFormat o1, BlenderOutputFormat o2)
			{
				return o1.toString().compareTo( o2.toString() );
			}

			public boolean equals(Object o)
			{
				return this.toString().equals( o.toString() );
			}
		};
	}

	/**
	 * A constructor.
	 */
	public ConsoleAppUI()
	{
		// Initializes the console driver (a color terminal is preferred)
		String term = null;
		term = System.getenv("TERM");
		if (
			!Strings.IsNullOrEmpty(term)
			&& (
				term.equals("xterm")
				|| term.equals("vt100")
				|| term.equals("rxvt")
			)
		)
		{
			// FIXME: maybe instead of using "equals" we might use "matches" or like
			// for allowing values such as "xterm-color", ...

			// We found a 16-color ANSI terminal
			this.uiDriver = new AnsiTextConsoleUIDriver(); // Not supported on Windows :(
		}
		else
		{
			this.uiDriver = new TextConsoleUIDriver();
		}

		if ( this.useSimpleMode )
		{
			this.controller = new SimpleModeBlenderAppUIController();
		}
		else
		{
			this.controller = new BlenderAppUIController();
		}
	}

	/**
	 * Shows the user interface.
	 */
	public void showUI() throws RenderAppException
	{
		this.menuMain();
	}

	/**
	 * Shows the main menu.
	 */
	private void menuMain() throws RenderAppException
	{
		int opt;
		boolean newJobInserted = false;

		do
		{
			do
			{
				this.uiDriver.showMsg( "" );
				this.uiDriver.showMsg( "### Sharegrid Grid Renderer ###" );
				this.uiDriver.showMsg( "--- Main Menu:" );
				this.uiDriver.showMsg( "1. Insert a new job" );
				//this.uiDriver.showMsg( "2. Import a job" );
				if ( newJobInserted )
				{
					this.uiDriver.showMsg( "2. Modify current job" );
					this.uiDriver.showMsg( "3. Execute current job" );
					this.uiDriver.showMsg( "4. Export current job" );
				}
				this.uiDriver.showMsg( "8. About" );
				this.uiDriver.showMsg( "9. Exit" );

				try
				{
					opt = this.uiDriver.askInt( "" );
				}
				catch (UIException uie)
				{
					throw new RenderAppException( uie );
				}
			} while ( ( opt < 1 || (newJobInserted && opt > 4) || (!newJobInserted && opt > 1) ) && opt != 8 && opt != 9 );

			switch ( opt )
			{
				case 1: // New job
					this.menuJobNew();
					newJobInserted = true;
					break;
//				case 2: // Import job
//					this.menuJobImport();
//					break;
				case 2: // Update current job
					this.menuJobUpdate();
					break;
				case 3: // Execute current job
					this.menuJobExecution();
					break;
				case 4: // Export current job
					this.menuJobExport();
					break;
				case 8:
					this.menuAbout();
					break;
				default:
					break;
			}
		} while ( opt != 9 );
	}

	/**
	 * Shows a message regarding the development of this app.
	 */
	private void menuAbout() throws RenderAppException
	{
		this.uiDriver.showMsg( "" );
		this.uiDriver.showMsg( "_____________________________________________________________" );
		this.uiDriver.showMsg( "" );
		this.uiDriver.showMsg( ConsoleAppUI.AppName + " v." + ConsoleAppUI.Version );
		this.uiDriver.showMsg( "_____________________________________________________________" );
		this.uiDriver.showMsg( "" );
		this.uiDriver.showMsg( "A Blender job submitter for the ShareGrid project." );
		this.uiDriver.showMsg( "" );
		this.uiDriver.showMsg( "Developed by:" );
		this.uiDriver.showMsg( "\tThe Distributed Computing System (DCS) group" );
		this.uiDriver.showMsg( "\thttp://dcs.di.unipmn.it" );
		this.uiDriver.showMsg( "_____________________________________________________________" );
	}
 
	/**
	 * Shows a menu for inserting a new job.
	 */
	private void menuJobNew() throws RenderAppException
	{
		this.filledFieldMask = 0;

		// Dispatch the "new-job-insertion" event
		this.controller.onNewJobInsertion();

		this.menuJobEdit();

		// Dispatch the "new-job-inserted" event
		this.controller.onNewJobInserted();
	}

	/**
	 * Shows a menu for inserting a new job.
	 */
	private void menuJobUpdate() throws RenderAppException
	{
		// Dispatch the "new-job-updating" event
		this.controller.onJobUpdating();

		this.menuJobEdit();

		// Dispatch the "new-job-update" event
		this.controller.onJobUpdated();
	}

	/**
	 * Shows a menu for inserting/updating a job.
	 */
	private void menuJobEdit() throws RenderAppException
	{
		if ( this.useSimpleMode )
		{
			this.menuJobEditSimple();
		}
		else
		{
			this.menuJobEditAdvanced();
		}
	}

	/**
	 * Shows a simple menu for inserting/updating a job.
	 */
	private void menuJobEditSimple() throws RenderAppException
	{
		boolean ans;
		int opt;
		int opt2;

		IMiddlewareManager midMngr = MiddlewareFactory.GetInstance().getMiddlewareManager();

		try
		{
			do
			{
				do
				{
					this.uiDriver.showMsg( "" );
					this.uiDriver.showMsg( "### Job selection ###" );
					this.uiDriver.showMsg( "--- New job" );
					this.uiDriver.showMsg( "1. Scene name             {" + ( ( filledFieldMask & SCENENAME_MASK ) == SCENENAME_MASK ? "X" : " " ) + "}" );
					this.uiDriver.showMsg( "2. Scene file             {" + ( ( filledFieldMask & SCENEFILE_MASK ) == SCENEFILE_MASK ? "X" : " " ) + "} [*]" );
					this.uiDriver.showMsg( "3. First frame            {" + ( ( filledFieldMask & STARTFRAME_MASK ) == STARTFRAME_MASK ? "X" : " " ) + "}" );
					this.uiDriver.showMsg( "4. Last frame             {" + ( ( filledFieldMask & ENDFRAME_MASK ) == ENDFRAME_MASK ? "X" : " " ) + "}" );
					this.uiDriver.showMsg( "5. Step frame             {" + ( ( filledFieldMask & STEPFRAME_MASK ) == STEPFRAME_MASK ? "X" : " " ) + "}" );
					this.uiDriver.showMsg( "6. Input files            {" + ( ( filledFieldMask & INPUTFILES_MASK ) == INPUTFILES_MASK ? "X" : " " ) + "}" );
					this.uiDriver.showMsg( "98. Review job settings" );
					this.uiDriver.showMsg( "99. Back to previous menu" );
					this.uiDriver.showMsg( "(---" );
					this.uiDriver.showMsg( " Informations marked with:" );
					this.uiDriver.showMsg( " - '[*]' are mandatory" );
					this.uiDriver.showMsg( " - '{X}' have been already inserted by the user" );
					this.uiDriver.showMsg( "---)" );

					opt = this.uiDriver.askInt( "" );
				} while ( ( opt < 1 || opt > 6 ) && opt != 98 && opt != 99 );

				switch ( opt )
				{
					case 1: // Scene name;
						{
							String sceneName;

							// Dispatch the "scene-name-insertion" event
							this.controller.onSceneNameInsertion();

							sceneName = this.uiDriver.ask(
								"{Scene name>> Symbolic name used for identifying the scene",
								this.controller.getJob().getSceneName()
							);
							if ( !Strings.IsNullOrEmpty( sceneName ) )
							{
								this.filledFieldMask |= SCENENAME_MASK;
							}
							else
							{
								this.filledFieldMask &= ~SCENENAME_MASK;
							}

							// Dispatch the "scene-name-inserted" event
							this.controller.onSceneNameInserted( sceneName );
						}
						break;
					case 2: // Scene file
						{
							String sceneFile;

							// Dispatch the "scene-file-insertion" event
							this.controller.onSceneFileInsertion();

							sceneFile = this.checkPath(
								this.uiDriver.ask(
									"{Scene file>> Path to the scene file",
									this.controller.getJob().getSceneFile()
								),
								true
							);
							if ( !Strings.IsNullOrEmpty( sceneFile ) )
							{
								this.filledFieldMask |= SCENEFILE_MASK;
							}
							else
							{
								this.filledFieldMask &= ~SCENEFILE_MASK;
							}

							// Dispatch the "scene-file-inserted" event
							this.controller.onSceneFileInserted( sceneFile );
						}
						break;
					case 3: // Start frame
						{
							Integer startFrame;

							// Dispatch the "start-frame-insertion" event
							this.controller.onStartFrameInsertion();

							do
							{
								startFrame = this.uiDriver.askInt(
									"{Start frame>> Number of the first frame of the scene to be rendered",
									this.controller.getJob().getStartFrame()
								);

								if ( startFrame != null && startFrame.intValue() <= 0 )
								{
									this.uiDriver.showWarning( "Start frame must be a number greater than zero!" );
								}
							} while ( startFrame != null && startFrame.intValue() <= 0 );

							if ( startFrame != null )
							{
								this.filledFieldMask |= STARTFRAME_MASK;
							}
							else
							{
								this.filledFieldMask &= ~STARTFRAME_MASK;
							}

							// Dispatch the "start-frame-inserted" event
							this.controller.onStartFrameInserted( startFrame );
						}
						break;
					case 4: // End frame
						{
							Integer endFrame;

							// Dispatch the "end-frame-insertion" event
							this.controller.onEndFrameInsertion();

							do
							{
								endFrame = this.uiDriver.askInt(
									"{End frame>> Number of the last frame of the scene to be rendered",
									this.controller.getJob().getEndFrame()
								);

								if ( endFrame != null && endFrame.intValue() <= 0 )
								{
									this.uiDriver.showWarning( "End frame must be a number greater than zero!" );
								}
							} while ( endFrame != null && endFrame.intValue() <= 0 );

							if ( endFrame != null )
							{
								this.filledFieldMask |= ENDFRAME_MASK;
							}
							else
							{
								this.filledFieldMask &= ~ENDFRAME_MASK;
							}

							// Dispatch the "end-frame-inserted" event
							this.controller.onEndFrameInserted( endFrame );
						}
						break;
					case 5: // Step frame
						{
							Integer stepFrame;

							// Dispatch the "step-frame-insertion" event
							this.controller.onStepFrameInsertion();

							do
							{
								stepFrame = this.uiDriver.askInt(
									"{Step frame>> Number of contiguous frames the scene will be subdivided",
									this.controller.getJob().getStepFrame()
								);

								if ( stepFrame != null && stepFrame.intValue() <= 0 )
								{
									this.uiDriver.showWarning( "Step frame must be a number greater than zero!" );
								}
							} while ( stepFrame != null && stepFrame.intValue() <= 0 );

							if ( stepFrame != null )
							{
								this.filledFieldMask |= STEPFRAME_MASK;
							}
							else
							{
								this.filledFieldMask &= ~STEPFRAME_MASK;
							}

							// Dispatch the "step-frame-inserted" event
							this.controller.onStepFrameInserted( stepFrame );
						}
						break;
					case 6: // Input files
						{
							String localFile = null;
							String remoteFile = null;
							String unpacker = null;
							ITextExpr constraints = null;
							boolean unpack = false;
							boolean persistent = false;
							boolean alwaysOverwrite = false;

							// Dispatch the "input-file-insertion" event
							this.controller.onInputFileInsertion();

							IRenderJobInputFile curInput = null;
							if ( !Collections.IsNullOrEmpty( this.controller.getJob().getInputFiles() ))
							{
								curInput = this.controller.getJob().getInputFiles().iterator().next();
							}
							else
							{
								if ( !Collections.IsNullOrEmpty( BlenderApp.GetInstance().getDefaultInputFiles( this.controller.getJob().getSceneFile() ) ) )
								{
									curInput = BlenderApp.GetInstance().getDefaultInputFiles( this.controller.getJob().getSceneFile() ).iterator().next();
								}
								else
								{
									curInput = new BlenderJobInputFile(
										BlenderApp.GetInstance().getDefaultLocalInputFile( this.controller.getJob().getSceneFile() ),
										BlenderApp.GetInstance().getDefaultRemoteInputFile( this.controller.getJob().getSceneFile() ),
										BlenderApp.GetInstance().getDefaultUnpackerCommand( this.controller.getJob().getSceneFile() ),
										null,
										false,
										false
									);
								}

							}

							localFile = this.checkPath(
								this.uiDriver.ask(
									"{Input files>> Path to the input file (for textures, animations, ...)",
									curInput != null ? curInput.getLocalPath() : null
								),
								true
							);

							remoteFile = new File(localFile).getName();

							unpack = this.uiDriver.askYesNo( "{Input files>> Is this file compressed", true );
							if ( unpack )
							{
								unpacker = curInput.getRemoteUnpackerCommand();
							}

							constraints = ( curInput != null ) ? curInput.getUploadConstraint() : null;
							persistent = ( curInput != null ) ? curInput.isPersistent() : false;
							alwaysOverwrite = ( curInput != null ) ? !curInput.overwriteIfDiff() : true;

							this.controller.getJob().removeInputFiles();

							// Dispatch the "input-file-inserted" event
							this.controller.onInputFileInserted(
								localFile,
								remoteFile,
								unpacker,
								constraints,
								persistent,
								!alwaysOverwrite
							);

							if ( !Collections.IsNullOrEmpty( this.controller.getJob().getInputFiles() ) )
							{
								this.filledFieldMask |= INPUTFILES_MASK;
							}
							else
							{
								this.filledFieldMask &= ~INPUTFILES_MASK;
							}
						}
						break;
					case 98: // Review job settings
						this.showJobInfo();
						break;
//					case 99:
//						this.controller.setDefaultRemoteCommands();
//						this.controller.setDefaultRemoteConstraints();
//						this.controller.setDefaultOutputFormat();
//						this.controller.setDefaultOutputFiles();
//						break;
				}
			} while ( opt != 99 );

			//// Ask for confirmation
			//ans = this.uiDriver.askYesNo( "Save job settings", true );
		}
		catch (UIException uie)
		{
			throw new RenderAppException( uie );
		}
	}

	/**
	 * Shows an advanced menu for inserting/updating a job.
	 */
	private void menuJobEditAdvanced() throws RenderAppException
	{
		boolean ans;
		int opt;
		int opt2;

		IMiddlewareManager midMngr = MiddlewareFactory.GetInstance().getMiddlewareManager();

		try
		{
			do
			{
				do
				{
					this.uiDriver.showMsg( "" );
					this.uiDriver.showMsg( "### Job selection ###" );
					this.uiDriver.showMsg( "--- New job" );
					this.uiDriver.showMsg( "1. Scene name             {" + ( ( filledFieldMask & SCENENAME_MASK ) == SCENENAME_MASK ? "X" : " " ) + "} [*]" );
					this.uiDriver.showMsg( "2. Blender executable     {" + ( ( filledFieldMask & BLENDEREXEC_MASK ) == BLENDEREXEC_MASK ? "X" : " " ) + "} [*]" );
					this.uiDriver.showMsg( "3. Scene file             {" + ( ( filledFieldMask & SCENEFILE_MASK ) == SCENEFILE_MASK ? "X" : " " ) + "} [*]" );
					this.uiDriver.showMsg( "4. First frame            {" + ( ( filledFieldMask & STARTFRAME_MASK ) == STARTFRAME_MASK ? "X" : " " ) + "}" );
					this.uiDriver.showMsg( "5. Last frame             {" + ( ( filledFieldMask & ENDFRAME_MASK ) == ENDFRAME_MASK ? "X" : " " ) + "}" );
					this.uiDriver.showMsg( "6. Step frame             {" + ( ( filledFieldMask & STEPFRAME_MASK ) == STEPFRAME_MASK ? "X" : " " ) + "}" );
					this.uiDriver.showMsg( "7. Remote constraints     {" + ( ( filledFieldMask & REMOTECONSTRAINTS_MASK ) == REMOTECONSTRAINTS_MASK ? "X" : " " ) + "}" );
					this.uiDriver.showMsg( "8. Input files            {" + ( ( filledFieldMask & INPUTFILES_MASK ) == INPUTFILES_MASK ? "X" : " " ) + "}" );
					this.uiDriver.showMsg( "9. Output format          {" + ( ( filledFieldMask & OUTPUTFORMAT_MASK ) == OUTPUTFORMAT_MASK ? "X" : " " ) + "}" );
					this.uiDriver.showMsg( "10. Output files          {" + ( ( filledFieldMask & OUTPUTFILES_MASK ) == OUTPUTFILES_MASK ? "X" : " " ) + "} [*]" );
					this.uiDriver.showMsg( "98. Review job settings" );
					this.uiDriver.showMsg( "99. Back to previous menu" );
					this.uiDriver.showMsg( "(---" );
					this.uiDriver.showMsg( " Informations marked with:" );
					this.uiDriver.showMsg( " - '[*]' are mandatory" );
					this.uiDriver.showMsg( " - '{X}' have been already inserted by the user" );
					this.uiDriver.showMsg( "---)" );

					opt = this.uiDriver.askInt( "" );
				} while ( ( opt < 1 || opt > 11 ) && opt != 98 && opt != 99 );

				switch ( opt )
				{
					case 1: // Scene name;
						{
							String sceneName;

							// Dispatch the "scene-name-insertion" event
							this.controller.onSceneNameInsertion();

							sceneName = this.uiDriver.ask(
								"{Scene name>> Symbolic name used for identifying the scene",
								this.controller.getJob().getSceneName()
							);
							if ( !Strings.IsNullOrEmpty( sceneName ) )
							{
								filledFieldMask |= SCENENAME_MASK;
							}
							else
							{
								filledFieldMask &= ~SCENENAME_MASK;
							}

							// Dispatch the "scene-name-inserted" event
							this.controller.onSceneNameInserted( sceneName );
						}
						break;
					case 2: // Blender executable
						{
							// Dispatch the "remote-command-insertion" event
							this.controller.onRemoteCommandInsertion();

							if ( midMngr.getMiddlewareCapabilities().supportConditionalRemoteCommand() )
							{
								//TODO: Actually OurGrid does not handle conditional remote command.
								//      So this feature may be implemented in a later version! ;)
							}
							else
							{
								String localPath = null;
								ITextExpr constraints = null;
								String remoteCmd = null;

								remoteCmd = this.uiDriver.ask(
									"{Blender executable>> Remote path to the Blender executable",
									!Collections.IsNullOrEmpty( this.controller.getJob().getRemoteCommands() )
										? this.controller.getJob().getRemoteCommands().iterator().next().getRemoteName()
										: BlenderApp.GetInstance().getDefaultRemoteCommands(  this.controller.getJob().getSceneFile() ).iterator().next().getRemoteName()
								);

								do
								{
									localPath = this.uiDriver.ask(
											"{Blender executable>> Local path to a Blender executable",
											""
									);
									if ( !Strings.IsNullOrEmpty( localPath ) )
									{
										localPath = this.checkPath( localPath, true );
									}
									else
									{
										this.uiDriver.showInfo( "Leaving blank the path to the local Blender executable, it will be assumed the remote machine will already have that." );
									}

									if ( this.useSimpleConstraints )
									{
										constraints = this.askForConstraintsSimple( "{Blender executable>> Constraints for choosing the remote machines where this executable is runnable:" );
									}
									else
									{
										constraints = this.askForConstraintsAdvanced( "{Blender executable>> Constraints for choosing the remote machines where this executable is runnable:" );
									}

									// Dispatch the "remote-command-inserted" event
									this.controller.onRemoteCommandInserted( localPath, constraints, remoteCmd, true, true );

									if ( constraints == null || constraints.equals( TextExprs.EMPTY_EXPR ) )
									{
										ans = false;
									}
									else
									{
										ans = this.uiDriver.askYesNo( "Another executable", true );
									}
								} while ( ans );

								if ( !Strings.IsNullOrEmpty( remoteCmd ) )
								{
									filledFieldMask |= BLENDEREXEC_MASK;
								}
								else
								{
									filledFieldMask &= ~BLENDEREXEC_MASK;
								}
							}
						}
						break;
					case 3: // Scene file
						{
							String sceneFile;

							// Dispatch the "scene-file-insertion" event
							this.controller.onSceneFileInsertion();

							sceneFile = this.checkPath(
								this.uiDriver.ask(
									"{Scene file>> Path to the scene file",
									this.controller.getJob().getSceneFile()
								),
								true
							);
							if ( !Strings.IsNullOrEmpty( sceneFile ) )
							{
								filledFieldMask |= SCENEFILE_MASK;
							}
							else
							{
								filledFieldMask &= ~SCENEFILE_MASK;
							}

							// Dispatch the "scene-file-inserted" event
							this.controller.onSceneFileInserted( sceneFile );
						}
						break;
					case 4: // Start frame
						{
							Integer startFrame;

							// Dispatch the "start-frame-insertion" event
							this.controller.onStartFrameInsertion();

							do
							{
								startFrame = this.uiDriver.askInt(
									"{Start frame>> Number of the first frame of the scene to be rendered",
									this.controller.getJob().getStartFrame()
								);

								if ( startFrame != null && startFrame.intValue() <= 0 )
								{
									this.uiDriver.showWarning( "Start frame must be a number greater than zero!" );
								}
							} while ( startFrame != null && startFrame.intValue() <= 0 );

							if ( startFrame != null )
							{
								filledFieldMask |= STARTFRAME_MASK;
							}
							else
							{
								filledFieldMask &= ~STARTFRAME_MASK;
							}

							// Dispatch the "start-frame-inserted" event
							this.controller.onStartFrameInserted( startFrame );
						}
						break;
					case 5: // End frame
						{
							Integer endFrame;

							// Dispatch the "end-frame-insertion" event
							this.controller.onEndFrameInsertion();

							do
							{
								endFrame = this.uiDriver.askInt(
									"{End frame>> Number of the last frame of the scene to be rendered",
									this.controller.getJob().getEndFrame()
								);

								if ( endFrame != null && endFrame.intValue() <= 0 )
								{
									this.uiDriver.showWarning( "End frame must be a number greater than zero!" );
								}
							} while ( endFrame != null && endFrame.intValue() <= 0 );

							if ( endFrame != null )
							{
								filledFieldMask |= ENDFRAME_MASK;
							}
							else
							{
								filledFieldMask &= ~ENDFRAME_MASK;
							}

							// Dispatch the "end-frame-inserted" event
							this.controller.onEndFrameInserted( endFrame );
						}
						break;
					case 6: // Step frame
						{
							Integer stepFrame;

							// Dispatch the "step-frame-insertion" event
							this.controller.onStepFrameInsertion();

							do
							{
								stepFrame = this.uiDriver.askInt(
									"{Step frame>> Number of contiguous frames the scene will be subdivided",
									this.controller.getJob().getStepFrame()
								);

								if ( stepFrame != null && stepFrame.intValue() <= 0 )
								{
									this.uiDriver.showWarning( "Step frame must be a number greater than zero!" );
								}
							} while ( stepFrame != null && stepFrame.intValue() <= 0 );

							if ( stepFrame != null )
							{
								filledFieldMask |= STEPFRAME_MASK;
							}
							else
							{
								filledFieldMask &= ~STEPFRAME_MASK;
							}

							// Dispatch the "step-frame-inserted" event
							this.controller.onStepFrameInserted( stepFrame );
						}
						break;
					case 7: // Remote constraints
						{
							ITextExpr remConstraints;

							// Dispatch the "remote-requirements-insertion" event
							this.controller.onRemoteConstraintsInsertion();

							if ( this.useSimpleConstraints )
							{
								remConstraints = this.askForConstraintsSimple( "{Remote constraints>> Constraints for choosing the remote machines:" );
							}
							else
							{
								remConstraints = this.askForConstraintsAdvanced( "{Remote constraints>> Constraints for choosing the remote machines:" );
							}
							if ( remConstraints != null )
							{
								filledFieldMask |= REMOTECONSTRAINTS_MASK;
							}
							else
							{
								filledFieldMask &= ~REMOTECONSTRAINTS_MASK;
							}

							// Dispatch the "remote-requirements-inserted" event
							this.controller.onRemoteConstraintsInserted( remConstraints );
						}
						break;
					case 8: // Input files
						{
							do
							{
								String localFile = null;
								String remoteFile = null;
								String unpacker = null;
								ITextExpr constraints = null;
								boolean persistent = false;
								boolean alwaysOverwrite = false;

								// Dispatch the "input-file-insertion" event
								this.controller.onInputFileInsertion();

								localFile = this.checkPath(
									this.uiDriver.ask( "{Input files>> Path to the input file" ),
									true
								);
								remoteFile = this.uiDriver.ask( "{Input files>> Name of the file onto the remote machine", new File(localFile).getName() );
								unpacker = this.uiDriver.ask( "{Input files>> Command (with options) to unpack the remote file", "" );
								persistent = this.uiDriver.askYesNo( "{Input files>> Should the remote file persist after the rendering completion", true );
								if ( persistent )
								{
									alwaysOverwrite = this.uiDriver.askYesNo( "{Input files>> Should the remote file overwrite an already exisistent copy", true );
								}
								else
								{
									alwaysOverwrite = true;
								}

								if ( this.useSimpleConstraints )
								{
									constraints = this.askForConstraintsSimple( "{Input files>> Constraints for this input file:" );
								}
								else
								{
									constraints = this.askForConstraintsAdvanced( "{Input files>> Constraints for this input file:" );
								}

								// Dispatch the "input-file-inserted" event
								this.controller.onInputFileInserted(
									localFile,
									remoteFile,
									unpacker,
									constraints,
									persistent,
									!alwaysOverwrite
								);

								ans = this.uiDriver.askYesNo( "Another input file", true );
							} while ( ans );

							if ( !Collections.IsNullOrEmpty( this.controller.getJob().getInputFiles() ) )
							{
								filledFieldMask |= INPUTFILES_MASK;
							}
							else
							{
								filledFieldMask &= ~INPUTFILES_MASK;
							}
						}
						break;
					case 9: // Output format
						{
							BlenderOutputFormat outFormat = null;

							// Dispatch the "output-format-insertion" event
							this.controller.onOutputFormatInsertion();

							this.uiDriver.showMsg( "{Output format>> Choose the scene output format:" );

/*
							List<BlenderOutputFormat> outfmts = null;
							outfmts = new ArrayList<BlenderOutputFormat>(
								java.util.Arrays.asList(
									BlenderOutputFormat.values()
								)
							);
							java.util.Collections.sort( outfmts );
*/
							BlenderOutputFormat[] outfmts = BlenderOutputFormat.values();
							Arrays.sort(
								outfmts,
								ConsoleAppUI.BlenderOutputFormatCmp
							);

							int count;
							do
							{
								count = 1;
								for ( BlenderOutputFormat outfmt : outfmts )
								{
									this.uiDriver.showMsg( count + ". " + outfmt );
									count++;
								}

								opt2 = this.uiDriver.askInt( "" );
							} while ( opt2 < 1 || opt2 >= count );

							//outFormat = outfmts.get( opt2 - 1 );
							outFormat = outfmts[ opt2 - 1 ];

							if ( outFormat != null )
							{
								filledFieldMask |= OUTPUTFORMAT_MASK;
							}
							else
							{
								filledFieldMask &= ~OUTPUTFORMAT_MASK;
							}

							// Dispatch the "output-format-inserted" event
							this.controller.onOutputFormatInserted( outFormat );
						}
						break;
					case 10: // Output files
						{
/*
							do
							{
								String remoteFile = null;
								String localFile = null;
								String packer = null;
								ITextExpr constraints = null;

								// Dispatch the "output-file-insertion" event
								this.controller.onOutputFileInsertion();

								remoteFile = this.uiDriver.ask( "{Output files>> Name of the file/directory onto the remote machine" );
								localFile = this.uiDriver.ask( "{Output files>> Path to the local file", new File(remoteFile).getName() );
								packer = this.uiDriver.ask( "{Output files>> Command (with options) to pack the remote file/directory", "" );

								if ( this.useSimpleConstraints )
								{
									constraints = this.askForConstraintsSimple( "{Output files>> Constraints for this output file:" );
								}
								else
								{
									constraints = this.askForConstraintsAdvanced( "{Output files>> Constraints for this output file:" );
								}

								// Dispatch the "output-file-inserted" event
								this.controller.onOutputFileInserted(
									remoteFile,
									localFile,
									packer,
									constraints
								);

								ans = this.uiDriver.askYesNo( "Another output file", true );
							} while ( ans );
*/

							String remoteFile = null;
							String localFile = null;
							String packer = null;
							ITextExpr constraints = null;

							// Dispatch the "output-file-insertion" event
							this.controller.onOutputFileInsertion();

							remoteFile = this.uiDriver.ask( "{Output files>> Set the render path and file name onto the remote machine", "//" + this.controller.getJob().getSceneName() + "_#" );

							do
							{
								packer = this.uiDriver.ask( "{Output files>> Command (with options) to pack the remote file/directory", "" );

								if ( this.useSimpleConstraints )
								{
									constraints = this.askForConstraintsSimple( "{Output files>> Constraints for this output file:" );
								}
								else
								{
									constraints = this.askForConstraintsAdvanced( "{Output files>> Constraints for this output file:" );
								}

								localFile = this.uiDriver.ask( "{Output files>> Path to the local file", new File(remoteFile).getName() );

								// Dispatch the "output-file-inserted" event
								this.controller.onOutputFileInserted(
									remoteFile,
									localFile,
									packer,
									constraints
								);

								ans = this.uiDriver.askYesNo( "Another output file", true );
							} while ( ans );

							if ( !Collections.IsNullOrEmpty( this.controller.getJob().getOutputFiles() ) )
							{
								filledFieldMask |= OUTPUTFILES_MASK;
							}
							else
							{
								filledFieldMask &= ~OUTPUTFILES_MASK;
							}
						}
						break;
					case 98: // Review job settings
						this.showJobInfo();
						break;
				}
			} while ( opt != 99 );

			//// Ask for confirmation
			//ans = this.uiDriver.askYesNo( "Save job settings", true );
		}
		catch (UIException uie)
		{
			throw new RenderAppException( uie );
		}
	}

	/**
	 * Shows a menu for importing a job.
	 */
	private void menuJobImport() throws RenderAppException
	{
		//TODO
	}

	/**
	 * Shows a menu for executing a job
	 */
	private void menuJobExecution() throws RenderAppException
	{
		int opt = 9;
		boolean firstTime = true;

		do
		{
			try
			{
				if ( firstTime )
				{
					// Go directly to the exeecution
					opt = 3;
					firstTime = false;
				}
				else
				{
					do
					{
						this.uiDriver.showMsg( "" );
						this.uiDriver.showMsg( "### Job execution ###" );
						this.uiDriver.showMsg( "1. Execution status" );
						this.uiDriver.showMsg( "2. Cancel execution" );
						this.uiDriver.showMsg( "3. Execute again" );
						this.uiDriver.showMsg( "4. Monitor execution" );
						this.uiDriver.showMsg( "9. Back to previous menu" );

						opt = this.uiDriver.askInt( "" );
					} while ( ( opt < 1 || opt > 4 ) && opt != 9 );
				}

				switch ( opt )
				{
					case 1: // Execution status
						{
							ExecutionStatus status = null;
							String jid = null;

							status = this.controller.getJobExecStatus();
							if ( status == null || status == ExecutionStatus.UNKNOWN )
							{
								jid = this.uiDriver.ask( "Please, insert a job execution identifier" );
								status = this.controller.getJobExecStatus( jid );
							}
							else
							{
								jid = this.controller.getJobExecHandle().getId();
							}
							if ( status != null && status != ExecutionStatus.UNKNOWN )
							{
								this.uiDriver.showInfo( "The execution IDENTIFIER of the job is: " + jid );
								this.uiDriver.showInfo( "The execution STATUS of the job is: " + status );
							}
							else
							{
								this.uiDriver.showWarning( "Unable to retrieve the job execution status." );
							}
						}
						break;
					case 2: // Cancel execution
						{
							boolean ans;

							ans = this.uiDriver.askYesNo( "Are you sure you want to cancel the job execution", true );

							if ( ans )
							{
								ExecutionStatus status = null;

								status = this.controller.getJobExecStatus();
								if ( status == null || status == ExecutionStatus.UNKNOWN )
								{
									String jid = null;
									jid = this.uiDriver.ask( "Please, insert a job execution identifier" );
									status = this.controller.getJobExecStatus( jid );
								}
								if ( status != null && status != ExecutionStatus.UNKNOWN )
								{
									if ( status == ExecutionStatus.RUNNING )
									{
										this.controller.cancelJob();
									}
									this.uiDriver.showOk( "Job successfully cancelled." );
								}
								else
								{
									this.uiDriver.showWarning( "Unable to cancel the job." );
								}
							}
						}
						break;
					case 3: // Execute
						if ( this.controller.canExecuteJob() )
						{
							ExecutionStatus status = null;

							status = this.controller.getJobExecStatus();
							if ( status == null || status != ExecutionStatus.RUNNING )
							{
								this.controller.executeJob();

								this.uiDriver.showOk( "Job successfully submitted." );

								this.uiDriver.showInfo( "The job execution identifier is: " + this.controller.getJobExecHandle() );
								this.uiDriver.showInfo( "Use this identifier for retrieving job execution information or for cancelling the job execution." );
							}
							else
							{
								this.uiDriver.showWarning( "It seems the current job is already in execution." );
							}
						}
						else
						{
							this.uiDriver.showError( "Current job cannot be executed. Please, check if the scheduler is running!" );
						}
						break;
					case 4: // Monitor
						{
							IJobMonitor monitor = null;
							monitor = this.controller.monitorJob();
							IJobMonitorAppUI monitorUI = new GriddedAppGUI( monitor );
							monitorUI.showUI();

							this.uiDriver.showOk( "Job monitor has been successfully started." );
						}
						break;
				}
			}
			catch (RenderAppException rae)
			{
				// catch and retry
				this.uiDriver.showError( "It seems something goes wrong. Please, check if the scheduler is working properly!" );
				rae.printStackTrace();
			}
			catch (UIException uie)
			{
				throw new RenderAppException( uie );
			}
		} while ( opt != 9 );
	}

	/**
	 * Shows a menu for exporting a job.
	 */
	private void menuJobExport() throws RenderAppException
	{
		// TODO: actually the only output format is JDF.
		// So, for now, we keep the UI as simple as possible

		String outfname = null;
		boolean ok = true;

		// Check settings
		if ( !this.controller.canExportJob() )
		{
			this.uiDriver.showError( "Job cannot be exported: missing mandatory information!" );
			ok = false;
		}

		if ( ok )
		{
			try
			{
				do
				{
					outfname = this.uiDriver.ask( "Name of the output file", this.controller.getJob().getSceneName() + ".jdf" );
					File f = new File( outfname );
					if ( f.exists() )
					{
						ok = this.uiDriver.askYesNo( "File already exists. Overwrite" , true);
					}
					if ( ok )
					{
						SecurityManager security = System.getSecurityManager();
						if ( security != null )
						{
							try
							{
								security.checkWrite( outfname );
							}
							catch (SecurityException se)
							{
								this.uiDriver.showWarning( "You don't have enough permission to write this file!" );
								ok = false;
							}
						}
					}
				} while ( !ok );

				if ( ok )
				{
					this.controller.exportJob( outfname, ExportFormatType.JDF );

					this.uiDriver.showOk( "File successfully exported!" );
				}
			}
			catch (Exception e)
			{
				throw new RenderAppException( "Error while exporting the job to the JDF format", e);
			}
		}
	}

	/** Asks for constraints (the simple way). */
	private ITextExpr askForConstraintsSimple( String title ) throws UIException
	{
		ITextExpr constraintExpr = null; // The constraints expression
		boolean ans;

		// Get the middleware manager
		IMiddlewareManager midMngr = MiddlewareFactory.GetInstance().getMiddlewareManager();

		// Get properties for constraints
		WorkerProperties wkrProps = midMngr.getWorkerProperties();

		List<String> propNames = null; // we want to be able to index the collection of constraints properties
		if ( wkrProps.getPropertyNames() != null && wkrProps.getPropertyNames().size() > 0 )
		{
			propNames = new ArrayList<String>( wkrProps.getPropertyNames() );
		}
		else
		{
			propNames = new ArrayList<String>();
		}

		do
		{
			ITextExpr newConstraintExpr = askForConstraintSimple( title, midMngr, wkrProps, propNames );

			if ( newConstraintExpr != null )
			{
				if ( constraintExpr != null )
				{
					int opt;

					do
					{
						this.uiDriver.showMsg( "Logical relation to apply between constraint '" + constraintExpr + "' and '" + newConstraintExpr + "':" );
						this.uiDriver.showMsg( "1. '" + JobRequirementsOps.LOGICAL_AND.getSymbol() + "' (" + JobRequirementsOps.LOGICAL_AND.getName() + ")" );
						this.uiDriver.showMsg( "2. '" + JobRequirementsOps.LOGICAL_OR.getSymbol() + "' (" + JobRequirementsOps.LOGICAL_OR.getName() + ")");

						opt = this.uiDriver.askInt( "" );
					} while ( opt < 1 || opt > 2 );

					switch ( opt )
					{
						case 1: // AND
							constraintExpr = JobRequirementsUtil.GetInstance().createBinaryTextExpr(
										new ParenthesizedTextExpr( constraintExpr ),
										JobRequirementsOps.LOGICAL_AND,
										newConstraintExpr // Parenthesization not need since we always have a terminal expr
							);
							break;
						case 2: // OR
							constraintExpr = JobRequirementsUtil.GetInstance().createBinaryTextExpr(
										new ParenthesizedTextExpr( constraintExpr ),
										JobRequirementsOps.LOGICAL_OR,
										newConstraintExpr // Parenthesization not need since we always have a terminal expr
							);
							break;
					}
				}
				else
				{
					constraintExpr = newConstraintExpr;
				}

				// Ask for new constraint
				ans = this.uiDriver.askYesNo( "Another constraint", true );
			}
			else
			{
				// No constraint inserted ==> No ask for new constraint
				ans = false;
			}
		} while ( ans );

		return constraintExpr;
	}

	/** Asks for a constraint (the simple way). */
	private ITextExpr askForConstraintSimple( String title, IMiddlewareManager midMngr, WorkerProperties wkrProps, List<String> propNames) throws UIException
	{
		int opt2;

		String propName = null; // Property name
		ITextOp propOp = null; // Logical/Relational operator
		String propValue = null; // Property value
		ITextExpr leftExpr = null;
		ITextExpr rightExpr = null;
		ITextExpr resExpr = null;

		int propCount = 1;
		do
		{
			if ( title != null )
			{
				this.uiDriver.showMsg( title );
			}

			// Ask for executable constraints
			propCount = 1;
			for ( String name : propNames )
			{
				this.uiDriver.showMsg( propCount + ". '" + name + "'" );
				propCount++;
			}
			this.uiDriver.showMsg( propCount + ". User supplied" );
			propCount++;
			this.uiDriver.showMsg( propCount + ". No constraints" );

			opt2 = this.uiDriver.askInt( "" );
		} while ( opt2 < 1 || opt2 > propCount );

		if ( opt2 == propCount )
		{
			// no constraint => do nothing
			resExpr = null;
		}
		else if ( opt2 == (propCount-1) )
		{
			// User supplied

			// Asks for name
			propName = this.uiDriver.ask( "Insert the constraint name" );
			leftExpr = new TerminalTextExpr( propName );

			// Lists all supported operators and asks for one of them
			List<ITextOp> reqOps = midMngr.getJobRequirementOperators();
			if ( reqOps != null )
			{
				int propOpCount = 1;
				int propOpIndex = 1;
				int opt3;

				this.uiDriver.showMsg( "Choose the operator for checking the '" + propName + "' constraint:" );

				Map<Integer,Integer> optToIdxMap = new HashMap<Integer,Integer>();
				do
				{
					propOpCount = 1;
					propOpIndex = 1;
					optToIdxMap.clear();
					for ( ITextOp op : reqOps )
					{
						if (
							!JobRequirementsUtil.GetInstance().sameTextOp( op, JobRequirementsOps.LOGICAL_NOT )
							&& !JobRequirementsUtil.GetInstance().sameTextOp( op, JobRequirementsOps.LOGICAL_AND )
							&& !JobRequirementsUtil.GetInstance().sameTextOp( op, JobRequirementsOps.LOGICAL_OR )
						)
						{
							this.uiDriver.showMsg( propOpCount + ". '" + op.getSymbol() + "' (" + op.getName() + ")" );

							optToIdxMap.put( propOpCount, propOpIndex );
							propOpCount++;
						}
						propOpIndex++;
					}

					opt3 = this.uiDriver.askInt( "" );
				} while ( opt3 < 1 || opt3 >= propOpCount );
				propOp = reqOps.get( optToIdxMap.get(opt3) - 1 );
			}

			// Creates the constraints expression
			if ( propOp != null )
			{
				if ( propOp.getType() == TextOpType.UNARY )
				{
					// Creates a unary expression
					resExpr = JobRequirementsUtil.GetInstance().createUnaryTextExpr(
							leftExpr,
							propOp
					);
				}
				else
				{
					// Asks for a value
					propValue = this.uiDriver.ask( "Insert the value for the '" + propName + "' constraint" );
					rightExpr = new TerminalTextExpr( propValue );

					// Creates a binary expression
					resExpr = JobRequirementsUtil.GetInstance().createBinaryTextExpr(
							leftExpr,
							propOp,
							rightExpr
					);
				}
			}
		}
		else
		{
			// One specific constraint

			propName = propNames.get(opt2-1);
			leftExpr = new TerminalTextExpr( propName );

			// Lists all supported operators and ask for one of them
			List<ITextOp> reqOps = midMngr.getJobRequirementOperators();
			if ( reqOps != null )
			{
				int propOpCount = 1;
				int propOpIndex = 1;
				int opt3 = 0;

				this.uiDriver.showMsg( "Choose the operator for checking the '" + propName + "' constraint:" );

				Map<Integer,Integer> optToIdxMap = new HashMap<Integer,Integer>();
				do
				{
					propOpCount = 1;
					propOpIndex = 1;
					optToIdxMap.clear();
					for ( ITextOp op : reqOps )
					{
						if (
							!JobRequirementsUtil.GetInstance().sameTextOp( op, JobRequirementsOps.LOGICAL_NOT )
							&& !JobRequirementsUtil.GetInstance().sameTextOp( op, JobRequirementsOps.LOGICAL_AND )
							&& !JobRequirementsUtil.GetInstance().sameTextOp( op, JobRequirementsOps.LOGICAL_OR )
						)
						{
							this.uiDriver.showMsg( propOpCount + ". '" + op.getSymbol() + "' (" + op.getName() + ")" );

							optToIdxMap.put( propOpCount, propOpIndex );
							propOpCount++;
						}
						propOpIndex++;
					}

					opt3 = this.uiDriver.askInt( "" );
				} while ( opt3 < 1 || opt3 >= propOpCount );
				propOp = reqOps.get( optToIdxMap.get(opt3) - 1 );
			}

			// Creates the constraints expression
			if ( propOp != null )
			{
				if ( propOp.getType() == TextOpType.UNARY )
				{
					resExpr = JobRequirementsUtil.GetInstance().createUnaryTextExpr(
						leftExpr,
						propOp
					);
				}
				else
				{
					// Asks for a constraint value

					int propValCount = 1;
					int opt3;
					do
					{
						propValCount = 1;
						this.uiDriver.showMsg( "Values for the '" + propName + "' constraint:" );
						for ( String value : wkrProps.getProperty( propName ) )
						{
							this.uiDriver.showMsg( propValCount + ". '" + value + "'" );
							propValCount++;
						}
						this.uiDriver.showMsg( propValCount + ". User supplied" );

						opt3 = this.uiDriver.askInt( "" );
					} while ( opt3 < 1 || opt3 > propValCount );

					if ( opt3 == propValCount )
					{
						// User supplied
						propValue = this.uiDriver.ask( "Insert the value for the '" + propName + "' constraint" );
					}
					else
					{
						propValue =  wkrProps.getProperty( propName ).get(opt3-1);
					}
					rightExpr = new TerminalTextExpr( propValue );

					resExpr = JobRequirementsUtil.GetInstance().createBinaryTextExpr(
						leftExpr,
						propOp,
						rightExpr
					);
				}
			}
		}

		return resExpr;
	}

	/** Asks for constraints (the advanced way). */
	private ITextExpr askForConstraintsAdvanced( String title ) throws UIException
	{
		ITextExpr constraintExpr = null; // The constraints expression
		boolean ans;

		// Get the middleware manager
		IMiddlewareManager midMngr = MiddlewareFactory.GetInstance().getMiddlewareManager();

		// Get properties for constraints
		WorkerProperties wkrProps = midMngr.getWorkerProperties();

		List<String> propNames = null; // we want to be able to index the collection of constraints properties
		if ( wkrProps.getPropertyNames() != null && wkrProps.getPropertyNames().size() > 0 )
		{
			propNames = new ArrayList<String>( wkrProps.getPropertyNames() );
		}
		else
		{
			propNames = new ArrayList<String>();
		}

		do
		{
			constraintExpr = askForConstraintAdvanced( title, midMngr, wkrProps, propNames, constraintExpr, true );

			if ( constraintExpr != null )
			{
				// Ask for new constraint
				ans = this.uiDriver.askYesNo( "Another constraint", true );
			}
			else
			{
				// No constraint inserted ==> No ask for new constraint
				ans = false;
			}
		} while ( ans );

		return constraintExpr;
	}

	/** Asks for a constraint (the advanced way). */
	private ITextExpr askForConstraintAdvanced( String title, IMiddlewareManager midMngr, WorkerProperties wkrProps, List<String> propNames, ITextExpr constraintExpr, boolean useRecursion ) throws UIException
	{
		int opt2;

		String propName = null; // Property name
		ITextOp propOp = null; // Logical/Relational operator
		String propValue = null; // Property value
		ITextExpr leftExpr = null;
		ITextExpr rightExpr = null;

		int propCount = 1;
		do
		{
			if ( title != null )
			{
				this.uiDriver.showMsg( title );
			}

			// Ask for executable constraints
			propCount = 1;
			for ( String name : propNames )
			{
				this.uiDriver.showMsg( propCount + ". '" + name + "'" );
				propCount++;
			}
			if ( constraintExpr != null )
			{
				this.uiDriver.showMsg( propCount + ". Use current constraints '" + constraintExpr + "'" );
				propCount++;
			}
			this.uiDriver.showMsg( propCount + ". User supplied" );
			propCount++;
			this.uiDriver.showMsg( propCount + ". No constraints" );

			opt2 = this.uiDriver.askInt( "" );
		} while ( opt2 < 1 || opt2 > propCount );

		if ( opt2 == propCount )
		{
			// no constraint => do nothing
			constraintExpr = null;
		}
		else if ( opt2 == (propCount-1) )
		{
			// User supplied

			// Asks for name
			propName = this.uiDriver.ask( "Insert the constraint name" );
			leftExpr = new TerminalTextExpr( propName );

			// Lists all supported operators and asks for one of them
			List<ITextOp> reqOps = midMngr.getJobRequirementOperators();
			if ( reqOps != null )
			{
				int propOpCount = 1;
				int opt3;
				this.uiDriver.showMsg( "Choose the operator for checking the '" + propName + "' constraint:" );
				do
				{
					propOpCount = 1;
					for ( ITextOp op : reqOps )
					{
						this.uiDriver.showMsg( propOpCount + ". '" + op.getSymbol() + "' (" + op.getName() + ")" );
						propOpCount++;
					}

					opt3 = this.uiDriver.askInt( "" );
				} while ( opt3 < 1 || opt3 >= propOpCount );
				propOp = reqOps.get(opt3-1);
			}

			// Creates the constraints expression
			if ( propOp != null )
			{
				if ( propOp.getType() == TextOpType.UNARY )
				{
					// Creates a unary expression
					constraintExpr = JobRequirementsUtil.GetInstance().createUnaryTextExpr(
							leftExpr,
							propOp
					);
				}
				else
				{
					// Asks for a value
					if ( useRecursion )
					{
						int opt3;
						do
						{
							this.uiDriver.showMsg( "Insert the value for the '" + propName + "' constraint" );
							this.uiDriver.showMsg( "1. Simple value" );
							this.uiDriver.showMsg( "2. Expression" );

							opt3 = this.uiDriver.askInt( "" );
						} while ( opt3 < 1 || opt3 > 2 );

						switch ( opt3 )
						{
							case 1:
								propValue = this.uiDriver.ask( "Insert a simple value for the '" + propName + "' constraint" );
								rightExpr = new TerminalTextExpr( propValue );
								break;
							case 2:
								// for simplying the user interface we allow only one level of recursion
								rightExpr = this.askForConstraintAdvanced( "Expression for the '" + propName + "' constraint", midMngr, wkrProps, propNames, null, false );
								break;
						}
					}
					else
					{
						propValue = this.uiDriver.ask( "Insert the value for the '" + propName + "' constraint" );
						rightExpr = new TerminalTextExpr( propValue );
					}

					// Creates a binary expression
					constraintExpr = JobRequirementsUtil.GetInstance().createBinaryTextExpr(
							leftExpr,
							propOp,
							rightExpr
					);
				}
			}
		}
		else if ( constraintExpr != null && opt2 == (propCount-2) )
		{
			// Use current constraints

			leftExpr = new ParenthesizedTextExpr( constraintExpr );

			// Lists all supported operators and asks for one of them
			List<ITextOp> reqOps = midMngr.getJobRequirementOperators();
			if ( reqOps != null )
			{
				int propOpCount = 1;
				int opt3;
				this.uiDriver.showMsg( "Choose the operator for checking the '" + constraintExpr + "' constraint:" );
				do
				{
					propOpCount = 1;
					for ( ITextOp op : reqOps )
					{
						this.uiDriver.showMsg( propOpCount + ". '" + op.getSymbol() + "' (" + op.getName() + ")" );
						propOpCount++;
					}

					opt3 = this.uiDriver.askInt( "" );
				} while ( opt3 < 1 || opt3 >= propOpCount );
				propOp = reqOps.get(opt3-1);
			}

			// Updates the constraints expression
			if ( propOp != null )
			{
				if ( propOp.getType() == TextOpType.UNARY )
				{
					// Creates a unary expression
					constraintExpr = JobRequirementsUtil.GetInstance().createUnaryTextExpr(
							leftExpr,
							propOp
					);
				}
				else
				{
					// Asks for a value
					if ( useRecursion )
					{
						int opt3;
						do
						{
							this.uiDriver.showMsg( "Insert the value for the '" + constraintExpr + "' constraint" );
							this.uiDriver.showMsg( "1. Simple value" );
							this.uiDriver.showMsg( "2. Expression" );

							opt3 = this.uiDriver.askInt( "" );
						} while ( opt3 < 1 || opt3 > 2 );

						switch ( opt3 )
						{
							case 1:
								propValue = this.uiDriver.ask( "Insert a simple value for the '" + constraintExpr + "' constraint" );
								rightExpr = new TerminalTextExpr( propValue );
								break;
							case 2:
								// for simplying the user interface we allow only one level of recursion
								rightExpr = this.askForConstraintAdvanced( "Expression for the '" + constraintExpr + "' constraint", midMngr, wkrProps, propNames, null, false );
								break;
						}
					}
					else
					{
						propValue = this.uiDriver.ask( "Insert the value for the '" + constraintExpr + "' constraint" );
						rightExpr = new TerminalTextExpr( propValue );
					}

					// Creates a binary expression
					constraintExpr = JobRequirementsUtil.GetInstance().createBinaryTextExpr(
							leftExpr,
							propOp,
							rightExpr
					);
				}
			}
		}
		else
		{
			// One specific constraint

			propName = propNames.get(opt2-1);
			leftExpr = new TerminalTextExpr( propName );

			// Lists all supported operators and ask for one of them
			List<ITextOp> reqOps = midMngr.getJobRequirementOperators();
			if ( reqOps != null )
			{
				int propOpCount = 1;
				int opt3;
				this.uiDriver.showMsg( "Choose the operator for checking the '" + propName + "' constraint:" );
				do
				{
					propOpCount = 1;
					for ( ITextOp op : reqOps )
					{
						this.uiDriver.showMsg( propOpCount + ". '" + op.getSymbol() + "' (" + op.getName() + ")" );
						propOpCount++;
					}

					opt3 = this.uiDriver.askInt( "" );
				} while ( opt3 < 1 || opt3 >= propOpCount );
				propOp = reqOps.get(opt3-1);
			}

			// Creates the constraints expression
			if ( propOp != null )
			{
				if ( propOp.getType() == TextOpType.UNARY )
				{
					constraintExpr = JobRequirementsUtil.GetInstance().createUnaryTextExpr(
						leftExpr,
						propOp
					);
				}
				else
				{
					// Asks for a constraint value

					int propValCount = 1;
					int opt3;
					do
					{
						propValCount = 1;
						this.uiDriver.showMsg( "Values for the '" + propName + "' constraint:" );
						for ( String value : wkrProps.getProperty( propName ) )
						{
							this.uiDriver.showMsg( propValCount + ". '" + value + "'" );
							propValCount++;
						}
						this.uiDriver.showMsg( propValCount + ". User supplied" );

						opt3 = this.uiDriver.askInt( "" );
					} while ( opt3 < 1 || opt3 > propValCount );

					if ( opt3 == propValCount )
					{
						// User supplied
						propValue = this.uiDriver.ask( "Insert the value for the '" + propName + "' constraint" );
					}
					else
					{
						propValue =  wkrProps.getProperty( propName ).get(opt3-1);
					}
					rightExpr = new TerminalTextExpr( propValue );

					constraintExpr = JobRequirementsUtil.GetInstance().createBinaryTextExpr(
						leftExpr,
						propOp,
						rightExpr
					);
				}
			}
		}

		return constraintExpr;
	}

	/**
	 * Shows job informations.
	 */
	private void showJobInfo()
	{
		BlenderJob job = this.controller.getJob();

		this.uiDriver.showMsg( "" );
		this.uiDriver.showMsg( "_________________________[ Job settings ]_________________________" );
		this.uiDriver.showMsg( "" );
		this.uiDriver.showMsg( "Scene name: " + ( !Strings.IsNullOrEmpty( job.getSceneName() ) ? job.getSceneName() : "<No name>" ) );
		if ( !this.useSimpleMode )
		{
			this.uiDriver.showMsg( "Blender executable (<local> ==> <remote> IF <constraint>):" + ( Collections.IsNullOrEmpty( job.getRemoteCommands() ) ? " <none>" : "" ) );
			if ( !Collections.IsNullOrEmpty( job.getRemoteCommands() ) )
			{
				for ( IRenderRemoteCommand command : job.getRemoteCommands() )
				{
					this.uiDriver.showMsg(
							"* "
							+ ( !Strings.IsNullOrEmpty( command.getLocalPath() ) ? ("'" + command.getLocalPath() + "'") : "<no local path>" )
							+ " ==> "
							+ ( !Strings.IsNullOrEmpty( command.getRemoteName() ) ? ("'" + command.getRemoteName() + "'") : "<no remote name>" )
							+ " "
							+ (
								command.getUploadConstraint() != null
								&& !Strings.IsNullOrEmpty( command.getUploadConstraint().toString() )
								? ("IF '" + command.getUploadConstraint() + "'")
								: "<no constraint>"
							)
					);
				}
			}
		}
		this.uiDriver.showMsg( "Scene file: " + ( !Strings.IsNullOrEmpty( job.getSceneFile() ) ? job.getSceneFile() : "<no file>" ) );
		this.uiDriver.showMsg( "Start frame number: " + job.getStartFrame() );
		this.uiDriver.showMsg( "End frame number: " + job.getEndFrame() );
		this.uiDriver.showMsg( "Step frame number: " + job.getStepFrame() );
		if ( this.useSimpleMode )
		{
			// only one input file

			this.uiDriver.showMsg(
				"Input files: "
				+ (
					!Collections.IsNullOrEmpty( job.getInputFiles() )
					&& !Strings.IsNullOrEmpty( job.getInputFiles().iterator().next().getLocalPath() )
						? job.getInputFiles().iterator().next().getLocalPath()
						: " <none>"
				)
			);
		}
		else
		{
			this.uiDriver.showMsg( "Remote Constraints: " + ( (job.getRemoteConstraints() != null) ? job.getRemoteConstraints() : "<no constraint>" ) );
			this.uiDriver.showMsg( "Input files (<local> ==> <remote> [<persistence>; <overwriting>] (<unpacker>) IF <constraint>):" + ( Collections.IsNullOrEmpty( job.getInputFiles() ) ? " <none>" : "" ) );
			if ( !Collections.IsNullOrEmpty( job.getInputFiles() ) )
			{
				for ( IRenderJobInputFile infile : job.getInputFiles() )
				{
					this.uiDriver.showMsg(
							"* "
							+ ( !Strings.IsNullOrEmpty( infile.getLocalPath() ) ? ("'" + infile.getLocalPath() + "'") : "<no local path>" )
							+ " ==> "
							+ ( !Strings.IsNullOrEmpty( infile.getRemoteName() ) ? ( "'" + infile.getRemoteName() + "'" ) : "<no remote name>" )
							+ " ["
							+ ( infile.isPersistent() ? "persistent" : "volatile" )
							+ "; "
							+ ( infile.overwriteIfDiff() ? "overwrite if different" : "always overwrite" )
							+ "]"
							+ " "
							+ ( !Strings.IsNullOrEmpty( infile.getRemoteUnpackerCommand() ) ? ("(unpacker: '" + infile.getRemoteUnpackerCommand() + "')") : "<no unpacker>" )
							+ " "
							+ (
								infile.getUploadConstraint() != null
								&& !Strings.IsNullOrEmpty( infile.getUploadConstraint().toString() )
								? ("IF '" + infile.getUploadConstraint() + "'")
								: "<no constraint>"
							)
					);
				}
			}
			this.uiDriver.showMsg( "Output format: " + job.getOutputFormat() );
			this.uiDriver.showMsg( "Output files (<remote> ==> <local> (<packer>) IF <constraint>):" + ( Collections.IsNullOrEmpty( job.getOutputFiles() ) ? " <none>" : "" ) );
			if ( !Collections.IsNullOrEmpty( job.getOutputFiles() ) )
			{
				for ( IRenderJobOutputFile outfile : job.getOutputFiles() )
				{
					this.uiDriver.showMsg(
							"* "
							+ ( !Strings.IsNullOrEmpty( outfile.getRemoteName() ) ? ( "'" + outfile.getRemoteName() + "'" ) : "<no remote name>" )
							+ " ==> "
							+ ( !Strings.IsNullOrEmpty( outfile.getLocalPath() ) ? ("'" + outfile.getLocalPath() + "'") : "<no local path>" )
							+ " "
							+ ( !Strings.IsNullOrEmpty( outfile.getRemotePackerCommand() ) ? ("(packer: '" + outfile.getRemotePackerCommand() + "')") : "<no packer>" )
							+ " "
							+ (
								outfile.getDownloadConstraint() != null
								&& !Strings.IsNullOrEmpty( outfile.getDownloadConstraint().toString() )
								? ("IF '" + outfile.getDownloadConstraint() + "'")
								: "<no constraint>"
							)
					);
				}
			}
		}
		this.uiDriver.showMsg( "__________________________________________________________________" );
	}

	/**
	 * Check the given path {@code path} for existence.
	 *
	 * If the given path does not exist and {@code verbose} is {@code true},
	 * show a warning message.
	 *
	 * @param path The path against to check for existence.
	 * @param verbose Control messages visualization.
	 * @return The absolute path if {@code path} exists; otherwise, the
	 * original {@code path}.
	 */
	private String checkPath( String path, boolean verbose )
	{
		if ( Strings.IsNullOrEmpty( path ) )
		{
			if ( verbose )
			{
				this.uiDriver.showError( "Path not specified." );
			}
			return "";
		}

		File f = new File( path );
/*
		if ( f.exists() )
		{
			return f.getAbsolutePath();
		}

		if ( verbose )
		{
			this.uiDriver.showWarning( "The path '" + path + "' does not exist. You might have problems during job execution." );
		}

		return path;
*/
		if ( !f.exists() && verbose )
		{
			this.uiDriver.showWarning( "The path '" + path + "' does not exist. You might have problems during job execution." );
		}
		return f.getAbsolutePath();
	}

	/**
	 * Application entry point.
	 */
	public static void main(String[] args)
	{
		ConsoleAppUI ui = new ConsoleAppUI();

		try
		{
			ui.showUI();
		}
		catch (Exception e)
		{
			System.err.println( "An error is occurred (" + e.getMessage() + ")." );
			e.printStackTrace( System.err );
			System.err.println( "Sorry for the inconvenience!" );

			System.exit(1);
		}

//		//@{ Debugging: checks for pending threads
//		java.util.Map<Thread, StackTraceElement[]> map = Thread.getAllStackTraces();
//
//		for (java.util.Iterator itr = map.keySet().iterator(); itr.hasNext();)
//		{
//			Thread thread = (Thread)itr.next();
//			System.out.println(thread.getName() +" --> "+ thread.isDaemon());
//			StackTraceElement[] stackTraceElements = map.get(thread);
//			for (int i = 0; i < stackTraceElements.length; i++)
//			{
//				System.out.println(stackTraceElements[i]);
//			}
//		}
//		//@} Debugging: checks for pending threads

		System.exit(0);
	}
}
