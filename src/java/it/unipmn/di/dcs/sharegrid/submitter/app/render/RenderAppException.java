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

/**
 * Exception thrown when something goes wrong in a rendering applications.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class RenderAppException extends Exception
{
	/**
	 * Constructs a new exception with null as its detail message.
	 */
	public RenderAppException()
	{
		super();
	}

	/**
	 * Constructs a new exception with the specified detail message.
	 */
	public RenderAppException(String message)
	{
		super( message );
	}

	/**
	 * Constructs a new exception with the specified detail message and
	 * cause.
	 */
	public RenderAppException(String message, Throwable cause)
	{
		super( message, cause );
	}

	/**
	 * Constructs a new exception with the specified cause and a detail
	 * message of (cause==null ? null : cause.toString()) (which typically
	 * contains the class and detail message of cause).
	 */
	public RenderAppException(Throwable cause)
	{
		super( cause );
	}
}
