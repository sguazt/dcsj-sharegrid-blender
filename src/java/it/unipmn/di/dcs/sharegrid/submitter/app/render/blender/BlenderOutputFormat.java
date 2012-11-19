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

/**
 * Blender output formats.
 *
 * Output formats suitable for command line options "-F". Formats are updated
 * to version 2.44 of blender.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public enum BlenderOutputFormat
{
	AVIJPEG,
	AVIRAW,
	BMP,
	FRAMESERVER,
	FTYPE,
	HAMX,
	IRIS,
	IRIZ,
	JPEG,
	MOVIE,
	PNG,
	RAWTGA,
	TGA,
	DEFAULT, // This is useful for using what specified inside a .blend file
	// Here below are ormats that can be compiled into blender,
	// but not available on all systems
        HDR,
	TIFF,
	EXR,
	MPEG,
	AVICODEC,
	QUICKTIME,
	CINEON,
	DPX
}
