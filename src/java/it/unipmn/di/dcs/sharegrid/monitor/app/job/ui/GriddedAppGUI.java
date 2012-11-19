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

package it.unipmn.di.dcs.sharegrid.monitor.app.job.ui;

import it.unipmn.di.dcs.grid.core.middleware.sched.ExecutionStatus;
import it.unipmn.di.dcs.grid.core.middleware.sched.IJobHandle;
import it.unipmn.di.dcs.grid.core.middleware.sched.ITaskHandle;
import it.unipmn.di.dcs.grid.core.middleware.sched.monitor.IJobMonitor;
import it.unipmn.di.dcs.grid.core.middleware.sched.monitor.IJobMonitorContext;
import it.unipmn.di.dcs.grid.core.middleware.sched.monitor.IJobMonitorEventInterceptor;
import it.unipmn.di.dcs.grid.core.middleware.sched.monitor.ITaskMonitorContext;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;
import javax.swing.text.*;
//
//import javax.swing.GridLayout;
//import javax.swing.JFrame;
//import javax.swing.UIManager;
//import javax.swing.UnsupportedLookAndFeelException;


/**
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class GriddedAppGUI implements IJobMonitorAppUI
{
        private IJobMonitor monitor;
        private GriddedFrame ui;
	private BlockingQueue<ITaskMonitorContext> queue;;

        public GriddedAppGUI(IJobMonitor monitor)
        {
                this.monitor = monitor;
                this.ui = this.new GriddedFrame( monitor.getJobHandle() );
		this.queue = new ArrayBlockingQueue<ITaskMonitorContext>( monitor.getJobHandle().getTasksNum() );
        }

	public void updateUI()
	{
		//TODO
	}

	//@{ IJobMonitorAppUI implementation

        public void showUI()
        {
                this.ui.showUI();
		this.monitor.getDispatcher().addInterceptor( this.new JobMonitorEventInterceptor() );
		this.monitor.run();
        }

	public IJobMonitor attachMonitor(IJobMonitor monitor)
	{
		IJobMonitor oldMonitor = this.monitor;

		this.monitor = monitor;

		this.updateUI();

		return oldMonitor;
	}

	public IJobMonitor detachMonitor()
	{
		IJobMonitor m = this.monitor;

		this.monitor = null;

		return m;
	}

	//@} IJobMonitorAppUI implementation

	//@{ class Interceptor

	private final class JobMonitorEventInterceptor implements IJobMonitorEventInterceptor
	{
		public void interceptStartMonitoring(IJobMonitorContext jobCtx)
		{
//			System.err.println("In StartMonitoring interceptor");//XXX
		}

		public void interceptJobStatusChange(IJobMonitorContext jobCtx)
		{
//			System.err.println("In JobStatusChanger interceptor");//XXX
		}

		public void interceptTaskStatusChange(ITaskMonitorContext taskCtx)
		{
//			System.err.println("In TaskStatusChange interceptor");//XXX

			try
			{
				GriddedAppGUI.this.queue.put( taskCtx );
			}
			catch (Exception e)
			{
//				System.err.println( "Caught exception while putting the task id: " + e );
			}
		}

		public void interceptStopMonitoring(IJobMonitorContext jobCtx)
		{
//			System.err.println("In StopMonitoring interceptor");//XXX
		}
	}

	//@} class Interceptor

	//@{ class GriddedFrame

	private final class GriddedFrame extends JFrame
	{
		//private GridLayout layout;
		private JProgressBar progressBar;
		private ProgressTask task;
		//private JButton[] buttons;
		private JTextComponent[] cells;
		private int ncells;
		private Map<String,Integer> taskIdCellMap;

		public GriddedFrame(IJobHandle jobHnd)
		{
			super( jobHnd.getName() );

			this.setResizable( false );

			this.ncells = jobHnd.getTasksNum();
			this.taskIdCellMap = new HashMap<String,Integer>();
			int count = 0;
			for ( ITaskHandle taskHnd : jobHnd.getTasks() )
			{
				this.taskIdCellMap.put( taskHnd.getId(), new Integer(count) );
				count++;
			}

			this.initComponents();
		}

		private void initComponents()
		{
//			int nrows = Math.round( (float) Math.sqrt( ncells ) );
//			int ncols = Math.round( ncells / nrows );
//
//			this.layout = new GridLayout( nrows, ncols );
			//this.buttons = new JButton[ncells];
			this.cells = new JTextComponent[ncells];
			this.progressBar = new JProgressBar( 0, ncells );
			this.progressBar.setValue(0);
			this.progressBar.setStringPainted(true);

			//Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			//this.width = screenSize.width / 2;
			//this.height = screenSize.height / 2;
		}

		public void addComponentsToPane(final Container pane)
		{
			int nrows = Math.round( (float) Math.sqrt( this.ncells ) );
			int ncols = Math.round( this.ncells / nrows );

			final GridLayout layout = new GridLayout( nrows, ncols, 10, 10 );
			final JPanel cellsPanel = new JPanel();

			//cellsPanel.setLayout( this.layout );
			cellsPanel.setLayout( layout );
			for ( int i = 0; i < this.ncells; i++ )
			{
				//JButton b = new JButton( Integer.toString( i ) );
				//JLabel lbl = new JLabel( "<html><body><" + Integer.toString( i ) + "<br>" + "UNSTARTED" + "</html>");
				MultilineLabel lbl = new MultilineLabel( Integer.toString( i ) + "\n" + "UNSTARTED" );
				lbl.setOpaque(true);
				//lbl.setBackground(new Color(248, 213, 131)); //orange
				lbl.setBackground(new Color(0xff, 0xff, 0xff));
				//lbl.setHorizontalAlignment( SwingConstants.CENTER );
				//lbl.setAlignmentX( JComponent.CENTER_ALIGNMENT );
				//lbl.setAlignmentY( JComponent.CENTER_ALIGNMENT );
				this.cells[i] = lbl;
				//lbl.setPreferredSize(new Dimension(200, 180));
				//cellsPanel.add( b );
				cellsPanel.add( lbl );
			}
			pane.add( cellsPanel, BorderLayout.CENTER );
			pane.add( this.progressBar, BorderLayout.SOUTH );
		}

		/**
		 * Create the GUI and show it.
		 * For thread safety, this method is invoked from the
		 * event dispatch thread.
		 */
		protected void createAndShowGUI()
		{
			//Make sure we have nice window decorations.
			JFrame.setDefaultLookAndFeelDecorated(true);
			//JDialog.setDefaultLookAndFeelDecorated(true);

			//Create and set up the window.
			//this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
			//this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

			//Set up the content pane.
			this.addComponentsToPane(this.getContentPane());

			//Display the window.
			//this.setSize(new Dimension(this.width, this.height));
			this.pack();
			this.setLocationRelativeTo(null); //center it
			this.setVisible(true);

			this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

			this.task = new ProgressTask();
			this.task.addPropertyChangeListener(
				new PropertyChangeListener()
				{
					public  void propertyChange(PropertyChangeEvent evt)
					{
						if ( "progress".equals( evt.getPropertyName() ) )
						{
							//this.progressBar.setValue((Integer)evt.getNewValue());
							progressBar.setValue((Integer)evt.getNewValue());
						}
					}
				}
			);
			this.task.execute();
		}

		/**
		 * Show the user interface.
		 */
		public void showUI()
		{
			try
			{
				//UIManager.setLookAndFeel( "javax.swing.plaf.metal.MetalLookAndFeel" );
				//UIManager.setLookAndFeel( UIManager.getCrossPlatformLookAndFeelClassName() );
				UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
			}
			catch (UnsupportedLookAndFeelException e)
			{
				e.printStackTrace();
			}
			catch (IllegalAccessException e)
			{
				e.printStackTrace();
			}
			catch (InstantiationException e)
			{
				e.printStackTrace();
			}
			catch (ClassNotFoundException e)
			{
				e.printStackTrace();
			}

			///* Turn off metal's use of bold fonts */
			//UIManager.put("swing.boldMetal", Boolean.FALSE);

			javax.swing.SwingUtilities.invokeLater(
				new Runnable()
				{
					public void run()
					{
						createAndShowGUI();
					}
				}
			);
//Alternativa:
//			try
//			{
//				javax.swing.SwingUtilities.invokeAndWait(
//					new Runnable()
//					{
//						public void run()
//						{
//							createAndShowGUI();
//						}
//					}
//				);
//			}
//			catch (Exception e)
//			{
//				e.printStackTrace();
//			}
		}

		public JTextComponent getCell(int i)
		{
			return this.cells[i];
		}
		public int getCellIndex(String taskId)
		{
			return this.taskIdCellMap.get( taskId ).intValue();
		}
	}

	//@} class GriddedFrame

	//@{ class ProgressTask

	private final class ProgressTask extends SwingWorker<Void, Void>
	{
		// Main task. Executed in background thread.
		@Override
		public Void doInBackground()
		{
			int ntasksDone = 0;
			int ntasks = GriddedAppGUI.this.monitor.getJobHandle().getTasksNum();

			//while ( ntasksDone < ntasks && !this.isCancelled() )
			while ( ntasksDone < ntasks )
			{
				try
				{
//					System.err.println( "ProgressTask: gonna to take a task id from queue" );

					ITaskMonitorContext taskCtx = GriddedAppGUI.this.queue.take();

					String taskId = taskCtx.taskHandle().getId();

					int cell = GriddedAppGUI.this.ui.getCellIndex( taskId );
					Color color = null;
					String text;

					if ( taskCtx.curTaskStatus() == ExecutionStatus.FINISHED )
					{
						color = new Color(0, 0xff, 0);
						text = Integer.toString(cell) + "\n" + "FINISHED";
						ntasksDone++;
					}
					else if ( taskCtx.curTaskStatus() == ExecutionStatus.RUNNING )
					{
						color = new Color(0x5c, 0xc6, 0xff);
						text = Integer.toString(cell) + "\n" + "RUNNING";
					}
					else if ( taskCtx.curTaskStatus() == ExecutionStatus.FAILED )
					{
						color = new Color(0xff, 0x54, 0x45);
						text = Integer.toString(cell) + "\n" + "FAILED";
					}
					else if ( taskCtx.curTaskStatus() == ExecutionStatus.UNSTARTED )
					{
						color = new Color(0xff, 0xff, 0xff);
						text = Integer.toString(cell) + "\n" + "UNSTARTED";
					}
					else
					{
						color = new Color(0xc0, 0xc0, 0xc0);
						text = Integer.toString(cell) + "\n" + "UNKNOWN";
					}
					GriddedAppGUI.this.ui.getCell(cell).setBackground( color );
					GriddedAppGUI.this.ui.getCell(cell).setText( text );
					GriddedAppGUI.this.ui.getCell(cell).invalidate();

//					System.err.println( "ProgressTask: taken task-id: " + taskId );

					//this.setProgress( 100 * ntasksDone / ntasks );
					this.setProgress( ntasksDone );
				}
				catch (InterruptedException ie)
				{
//					System.err.println( "Caught interrupted exception: " + ie );
//					System.err.println( "Going to cancel the execution" );

					this.cancel(true);
				}
			}
			return null;
		}

		// Executed in event dispatching thread
		@Override
		public void done()
		{
			Toolkit.getDefaultToolkit().beep();
			GriddedAppGUI.this.ui.setCursor(null); //turn off the wait cursor
//			System.err.println( "ProgressTask: done!" );//XXX
		}
	}

	//@} class ProgressTask

	//@{ class MultilineLabel

/* XXX: don't support text-alignement!!
	private final class MultilineLabel extends JTextArea
	{
		public MultilineLabel(String text)
		{
			super(text);
			setEditable(false);
			setLineWrap(true);
			setWrapStyleWord(true);
			setBackground((Color) UIManager.get("Label.background"));
			setForeground((Color) UIManager.get("Label.foreground"));
			setFont((Font) UIManager.get("Label.font"));
		}
	}
*/

	private final class MultilineLabel extends JTextPane
	{
		public MultilineLabel(String text)
		{
			super();
			try
			{
				this.setEditable(false);
				this.setBackground( (Color) UIManager.get("Label.background") );
				this.setForeground( (Color) UIManager.get("Label.foreground") );
				this.setFont( (Font) UIManager.get("Label.font") );
				this.setEditorKit( new MultilineLabel.EditorKit() );
				SimpleAttributeSet attrs = new SimpleAttributeSet();
				StyleConstants.setAlignment( attrs,StyleConstants.ALIGN_CENTER );
				StyledDocument doc = (StyledDocument) this.getDocument();
				doc.insertString( 0, text,attrs );
				doc.setParagraphAttributes( 0, doc.getLength()-1, attrs, false );
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}

		private class EditorKit extends StyledEditorKit
		{
			public ViewFactory getViewFactory()
			{
				return new StyledViewFactory();
			}

			private class StyledViewFactory implements ViewFactory
			{
				public View create(Element elem)
				{
					String kind = elem.getName();
					if (kind != null)
					{
						if (kind.equals(AbstractDocument.ContentElementName))
						{
							return new LabelView(elem);
						}
						else if (kind.equals(AbstractDocument.ParagraphElementName))
						{
							return new ParagraphView(elem);
						}
						else if (kind.equals(AbstractDocument.SectionElementName))
						{
							return new CenteredBoxView(elem, View.Y_AXIS);
						}
						else if (kind.equals(StyleConstants.ComponentElementName))
						{
							return new ComponentView(elem);
						}
						else if (kind.equals(StyleConstants.IconElementName))
						{
							return new IconView(elem);
						}
					}

					// default to text display
					return new LabelView(elem);
				}
			}
		}

		class CenteredBoxView extends BoxView
		{
			public CenteredBoxView(Element elem, int axis)
			{
				super(elem,axis);
			}

			protected void layoutMajorAxis(int targetSpan, int axis, int[] offsets, int[] spans)
			{
				super.layoutMajorAxis(targetSpan,axis,offsets,spans);
				int textBlockHeight = 0;
				int offset = 0;
				for (int i = 0; i < spans.length; i++)
				{
					textBlockHeight += spans[ i ];
				}
				offset = (targetSpan - textBlockHeight) / 2;
				for (int i = 0; i < offsets.length; i++)
				{
					offsets[ i ] += offset;
				}
			}
		}
	}

	//@} class MultilineLabel
}
