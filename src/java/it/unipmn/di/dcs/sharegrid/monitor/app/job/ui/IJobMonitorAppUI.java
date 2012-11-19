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

package it.unipmn.di.dcs.sharegrid.monitor.app.job.ui;

import it.unipmn.di.dcs.grid.core.middleware.sched.monitor.IJobMonitor;

/**
 * Interface for Job Monitor user interfaces.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public interface IJobMonitorAppUI
{
	/**
	 * Shows the user interface.
	 */
	void showUI();

	/**
	 * Attaches the given monitor to this user interface.
	 *
	 * The previously attached monitor, if any, is detached and returned.
	 */
	IJobMonitor attachMonitor(IJobMonitor monitor);

	/**
	 * Detach the job monitor from this user interface.
	 */
	IJobMonitor detachMonitor();
}
