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

package it.unipmn.di.dcs.sharegrid.submitter.app.render.blender;

import it.unipmn.di.dcs.common.text.ITextExpr;
import it.unipmn.di.dcs.sharegrid.submitter.app.render.AbstractRenderRemoteCommand;

import java.util.List;

/**
 * Class for a <em>blender</em> executable.
 *
 * In addition to implement the {@code IRenderRemoteCommand} interface, this
 * class contains several attributes related to the blender (command line)
 * options. See the
 * <a href="http://wiki.blender.org/index.php/Reference/CL">Blender reference
 * manual</a> for more information.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class BlenderRemoteCommand extends AbstractRenderRemoteCommand
{
	/**
	 * Option: -b &lt;file&gt;.
	 *
	 * Render &lt;file&gt; in background.
	 */
	private String opt_b = null;

	/**
	 * Option: -S &lt;name&gt;.
	 *
	 * Set scene &lt;name&gt;.
	 */
	private String opt_S = null;

	/**
	 * Option -f &lt;frame&gt;.
	 *
	 * Render frame &lt;frame&gt; and save it).
	 */
	private String opt_f = null;

	/**
	 * Option: -s &lt;frame&gt;.
	 *
	 * Set start to frame &lt;frame&gt; (use with -a)
	 */
	private String opt_s = null;
	/**
	 * Option: -e &lt;frame&gt;.
	 *
	 * Set end to frame &lt;frame&gt; (use with -a)
	 */
	private String opt_e = null;

	/**
	 * Option: -o &lt;path&gt;.
	 *
	 * Set the render path and file name. Use // at the start of the path to
	 * render relative to the blend file. The frame number will be added at
	 * the end of the filename.
	 * eg: blender -b foobar.blend -o //render_ -F PNG -x 1
	 */
	private String opt_o = null;

	/**
	 * Option: -F &lt;format&gt;.
	 *
	 * Set the render format, Valid options are:
	 *   TGA IRIS HAMX FTYPE JPEG MOVIE IRIZ RAWTGA
	 *   AVIRAW AVIJPEG PNG BMP FRAMESERVER
	 * (formats that can be compiled into blender, not available on all
	 * systems)
         * HDR TIFF EXR MPEG AVICODEC QUICKTIME CINEON DPX
         */
	private String opt_F = null;

	/**
	 * Options: -x &lt;bool&gt;.
	 *
	 * Set option to add the file extension to the end of the file.
	 */
	private Boolean opt_x = null;

	/**
	 * Option: -t &lt;threads&gt;.
	 *
	 * Use amount of &lt;threads&gt; for rendering.
	 */
	private Integer opt_t = null;

	/**
	 * Option: -a &lt;file(s)&gt;.
	 *
	 * Playback &lt;file(s)&gt;.
	 */
	private String opt_a = null;

	/** A constructor. */
	public BlenderRemoteCommand()
	{
		super();
	}

	/** A constructor. */
	public BlenderRemoteCommand(String local, String remote, String unpacker, ITextExpr constraints, boolean persistent, boolean overwriteDiff)
	{
		super( local, remote, unpacker, constraints, persistent, overwriteDiff );
	}

	/** A constructor. */
	public BlenderRemoteCommand(String local, String remote, String unpacker, ITextExpr constraints, boolean persistent, boolean overwriteDiff, List<String> preRenderCmds, List<String> afterRenderCmds)
	{
		super( local, remote, unpacker, constraints, persistent, overwriteDiff, preRenderCmds, afterRenderCmds );
	}

	public void setOption_b(String value)
	{
		this.opt_b = value;
	}

	public String getOption_b()
	{
		return this.opt_b;
	}

//TODO

	public void setOption_a(String value)
	{
		this.opt_a = value;
	}

	public String getOption_a()
	{
		return this.opt_a;
	}
}
