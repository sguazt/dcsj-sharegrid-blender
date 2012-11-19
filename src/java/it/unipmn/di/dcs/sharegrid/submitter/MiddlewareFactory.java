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

package it.unipmn.di.dcs.sharegrid.submitter;

import it.unipmn.di.dcs.grid.core.middleware.IMiddlewareFactory;
import it.unipmn.di.dcs.grid.core.middleware.IMiddlewareManager;
import it.unipmn.di.dcs.grid.middleware.ourgrid.OurGridMiddlewareManager;

/**
 * A factory concrete class for creating middleware manager objects.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class MiddlewareFactory implements IMiddlewareFactory
{
	private static MiddlewareFactory instance; // The singleton instance

	protected MiddlewareFactory()
	{
		// empty
	}

	public static synchronized MiddlewareFactory GetInstance()
	{
		if ( MiddlewareFactory.instance == null )
		{
			MiddlewareFactory.instance = new MiddlewareFactory();
		}

		return MiddlewareFactory.instance;
	}

	public IMiddlewareManager getMiddlewareManager()
	{
		//FIXME: Actually OurGrid is the only managed middleware
		//       In the future we can have several middleware.
		//       So this code should be replaced with a more general
		//       one (e.g. one that read from a XML file)

		return new OurGridMiddlewareManager();
	}
}
