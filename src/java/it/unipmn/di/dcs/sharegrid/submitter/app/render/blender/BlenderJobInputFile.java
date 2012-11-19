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

import it.unipmn.di.dcs.common.text.ITextExpr;
import it.unipmn.di.dcs.sharegrid.submitter.app.render.AbstractRenderJobInputFile;

/**
 * Class for a <em>blender</em> input file.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class BlenderJobInputFile extends AbstractRenderJobInputFile
{
	/** A constructor. */
	public BlenderJobInputFile()
	{
		super();
	}

	/** A constructor. */
	public BlenderJobInputFile(String local, String remote, String unpacker, ITextExpr constraints, boolean persistent, boolean overwriteDiff)
	{
		super( local, remote, unpacker, constraints, persistent, overwriteDiff );
	}
}
