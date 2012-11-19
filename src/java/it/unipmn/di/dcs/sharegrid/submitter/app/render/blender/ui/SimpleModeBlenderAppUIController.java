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

package it.unipmn.di.dcs.sharegrid.submitter.app.render.blender.ui;

import it.unipmn.di.dcs.common.text.TextExprs;
import it.unipmn.di.dcs.common.util.collection.Collections;
import it.unipmn.di.dcs.sharegrid.submitter.app.render.blender.BlenderApp;
import it.unipmn.di.dcs.sharegrid.submitter.app.render.blender.BlenderRemoteCommand;
import it.unipmn.di.dcs.sharegrid.submitter.app.render.IRenderRemoteCommand;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller class for a Blender application view running in a
 * <em>simple mode</em>.
 *
 * A Blender application view running in a <em>simple mode</em> is a view with
 * limited functionalities and simpler user interactions.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
class SimpleModeBlenderAppUIController extends BlenderAppUIController
{
	/** A constructor. */
	public SimpleModeBlenderAppUIController()
	{
		super();
	}

	@Override
	public void onNewJobInserted()
	{
		this.setDefaultRemoteCommands();
		this.setDefaultRemoteConstraints();
		this.setDefaultOutputFormat();
		this.setDefaultOutputFiles();
	}

	@Override
	public void onJobUpdated()
	{
		this.setDefaultRemoteCommands();
		this.setDefaultRemoteConstraints();
		this.setDefaultOutputFormat();
		this.setDefaultOutputFiles();
	}

	@Override
	public void setDefaultRemoteCommands()
	{
		List<IRenderRemoteCommand> cmds = null;

		String remoteOut = null;
		remoteOut = BlenderApp.GetInstance().getDefaultRemoteOutputFile( this.getJob().getSceneFile() ) + "/"; // this last slash is needed for option -o of blender

		List<String> preRenderCmds = new ArrayList<String>();

		preRenderCmds.add( "mkdir " + remoteOut );

		cmds = BlenderApp.GetInstance().getDefaultRemoteCommands( this.getJob().getSceneFile() );

		if ( !Collections.IsNullOrEmpty( cmds ) )
		{
			for (IRenderRemoteCommand cmd : cmds )
			{
				cmd.setPreRenderRemoteCommands( preRenderCmds );
				cmd.setOption( "-o", remoteOut );
			}
		}

		this.getJob().setRemoteCommands( cmds );
	}
}
