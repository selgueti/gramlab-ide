/*
 * Unitex
 *
 * Copyright (C) 2001-2010 Université Paris-Est Marne-la-Vallée <unitex@univ-mlv.fr>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.
 *
 */

package fr.umlv.unitex.frames;

import java.awt.BorderLayout;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

import fr.umlv.unitex.GraphBox;
import fr.umlv.unitex.GraphPresentationInfo;
import fr.umlv.unitex.GraphicalZone;
import fr.umlv.unitex.MyCursors;
import fr.umlv.unitex.MyDropTarget;
import fr.umlv.unitex.Preferences;
import fr.umlv.unitex.TextField;
import fr.umlv.unitex.io.SVG;

/**
 * This class describes a frame used to display and edit a graph.
 * 
 * @author Sébastien Paumier
 *  
 */
public class GraphFrame extends JInternalFrame {

	static int openFrameCount = 0;

	static final int offset = 30;

	/**
	 * Text field used to edit box content
	 */
	public TextField boxContentEditor;

	/**
	 * Drawing area
	 */
	public GraphicalZone graphicalZone;

	public GraphicalZone getGraphicalZone() {
		return graphicalZone;
	}

	/**
	 * The graph file
	 */
	private File grf;

	/**
	 * Indicates if the graph must be saved
	 */
	public boolean modified = false;

	/** undo redo manager */
	UndoManager manager;

	/** redo button */
	private JButton redoButton;

	/** undo button */
	private JButton undoButton;

	private JScrollPane scroll;

	private boolean nonEmptyGraph = false;

	public boolean isNonEmptyGraph() {
		return nonEmptyGraph;
	}
	
	private JPanel mainPanel;

	/**
	 * Component used to listen frame changes. It is used to adapt the zoom
	 * factor to the frame's dimensions when the zoom mode is "Fit in Windows"
	 */
	public ComponentListener compListener = null;

	/**
	 * The frame's tool bar that contains icons
	 */
	private JToolBar myToolBar;

	/**
	 * Constructs a new <code>GraphFrame</code>
	 * 
	 * @param nonEmpty
	 *            indicates if the graph is non empty
	 */
	GraphFrame() {
		super("", true, true, true, true);
		MyDropTarget.newDropTarget(this);
		openFrameCount++;
		setTitle("Graph");
		mainPanel = new JPanel(new BorderLayout());
		mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

		JPanel top = new JPanel(new BorderLayout());
		top.add(buildTextPanel(), BorderLayout.NORTH);
		graphicalZone = new GraphicalZone(1188, 840, boxContentEditor, this);
		graphicalZone.setPreferredSize(new Dimension(
				(int) (1188 * graphicalZone.scaleFactor),
				(int) (840 * graphicalZone.scaleFactor)));

		manager = new UndoManager();
		manager.setLimit(30);

		graphicalZone.addUndoableEditListener(manager);
		GraphPresentationInfo info=getGraphPresentationInfo();
		scroll = new JScrollPane(graphicalZone);
		scroll.getHorizontalScrollBar().setUnitIncrement(20);
		scroll.getVerticalScrollBar().setUnitIncrement(20);
		scroll.setPreferredSize(new Dimension(1188, 840));
		top.add(scroll, BorderLayout.CENTER);
		boxContentEditor.setFont(info.input.font);
		createToolBar(info.iconBarPosition);
		if (!(info.iconBarPosition
				.equals(Preferences.NO_ICON_BAR))) {
			mainPanel.add(myToolBar, info.iconBarPosition);
		}
		mainPanel.add(top, BorderLayout.CENTER);
		setContentPane(mainPanel);
		pack();
		addInternalFrameListener(new MyInternalFrameListener());
		setBounds(offset * (openFrameCount % 6), offset * (openFrameCount % 6),
				800 + 40, 600 + 80);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
	}

	private void createToolBar(String iconBarPosition) {
		myToolBar = new JToolBar("Tools");
		if (iconBarPosition
				.equals(Preferences.ICON_BAR_WEST)
				|| iconBarPosition
						.equals(Preferences.ICON_BAR_EAST)) {
			myToolBar.setOrientation(SwingConstants.VERTICAL);
		} else {
			myToolBar.setOrientation(SwingConstants.HORIZONTAL);
		}
		myToolBar.setMargin(new Insets(0, 0, 0, 0));
		JButton save = new JButton(MyCursors.saveIcon);
		save.setMaximumSize(new Dimension(36, 36));
		save.setMinimumSize(new Dimension(36, 36));
		save.setPreferredSize(new Dimension(36, 36));
		JButton compile = new JButton(MyCursors.compilationIcon);
		compile.setMaximumSize(new Dimension(36, 36));
		compile.setMinimumSize(new Dimension(36, 36));
		compile.setPreferredSize(new Dimension(36, 36));

		JButton copy = new JButton(MyCursors.copyIcon);
		JButton cut = new JButton(MyCursors.cutIcon);
		JButton paste = new JButton(MyCursors.pasteIcon);
		copy.setMaximumSize(new Dimension(36, 36));
		cut.setMaximumSize(new Dimension(36, 36));
		paste.setMaximumSize(new Dimension(36, 36));
		copy.setMinimumSize(new Dimension(36, 36));
		cut.setMinimumSize(new Dimension(36, 36));
		paste.setMinimumSize(new Dimension(36, 36));
		copy.setPreferredSize(new Dimension(36, 36));
		cut.setPreferredSize(new Dimension(36, 36));
		paste.setPreferredSize(new Dimension(36, 36));

		redoButton = new JButton(MyCursors.redoIcon);
		undoButton = new JButton(MyCursors.undoIcon);
		redoButton.setToolTipText("Redo");
		undoButton.setToolTipText("Undo");

		redoButton.addActionListener(new RedoIt());
		redoButton.setMaximumSize(new Dimension(36, 36));
		redoButton.setMinimumSize(new Dimension(36, 36));
		redoButton.setPreferredSize(new Dimension(36, 36));

		undoButton.addActionListener(new UndoIt());
		undoButton.setMaximumSize(new Dimension(36, 36));
		undoButton.setMinimumSize(new Dimension(36, 36));
		undoButton.setPreferredSize(new Dimension(36, 36));

		JToggleButton normal = new JToggleButton(MyCursors.arrowIcon);
		normal.setMaximumSize(new Dimension(36, 36));
		normal.setMinimumSize(new Dimension(36, 36));
		normal.setPreferredSize(new Dimension(36, 36));

		JToggleButton create = new JToggleButton(MyCursors.createBoxesIcon);
		create.setMaximumSize(new Dimension(36, 36));
		create.setMinimumSize(new Dimension(36, 36));
		create.setPreferredSize(new Dimension(36, 36));
		JToggleButton kill = new JToggleButton(MyCursors.killBoxesIcon);
		kill.setMaximumSize(new Dimension(36, 36));
		kill.setMinimumSize(new Dimension(36, 36));
		kill.setPreferredSize(new Dimension(36, 36));
		JToggleButton link = new JToggleButton(MyCursors.linkBoxesIcon);
		link.setMaximumSize(new Dimension(36, 36));
		link.setMinimumSize(new Dimension(36, 36));
		link.setPreferredSize(new Dimension(36, 36));
		JToggleButton reverseLink = new JToggleButton(
				MyCursors.reverseLinkBoxesIcon);
		reverseLink.setMaximumSize(new Dimension(36, 36));
		reverseLink.setMinimumSize(new Dimension(36, 36));
		reverseLink.setPreferredSize(new Dimension(36, 36));
		JToggleButton openSubgraph = new JToggleButton(
				MyCursors.openSubgraphIcon);
		openSubgraph.setMaximumSize(new Dimension(36, 36));
		openSubgraph.setMinimumSize(new Dimension(36, 36));
		openSubgraph.setPreferredSize(new Dimension(36, 36));

		JButton configuration = new JButton(MyCursors.configurationIcon);
		configuration.setMaximumSize(new Dimension(36, 36));
		configuration.setMinimumSize(new Dimension(36, 36));
		configuration.setPreferredSize(new Dimension(36, 36));

		save.setToolTipText("Save graph");
		compile.setToolTipText("Compile graph");

		copy.setToolTipText("Copy");
		cut.setToolTipText("Cut");
		paste.setToolTipText("Paste");

		normal.setToolTipText("Normal editing mode");
		create.setToolTipText("Create a new box");
		kill.setToolTipText("Remove a box");
		link.setToolTipText("Link boxes");
		reverseLink.setToolTipText("Reversed link between boxes");
		openSubgraph.setToolTipText("Open a sub-graph");

		configuration.setToolTipText("Graph configuration");

		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GraphFrame f = UnitexFrame.mainFrame.frameManager.getCurrentFocusedGraphFrame();
				if (f != null)
					UnitexFrame.mainFrame.saveGraph(f);
			}
		});
		compile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						UnitexFrame.mainFrame.compileGraph();
					}
				});
			}
		});
		copy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final ActionEvent E = e;
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						((TextField) graphicalZone.text).getSpecialCopy()
								.actionPerformed(E);
						repaint();
					}
				});
			}
		});
		cut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final ActionEvent E = e;
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						((TextField) graphicalZone.text).getCut()
								.actionPerformed(E);
						repaint();
					}
				});
			}
		});
		paste.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final ActionEvent E = e;
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						((TextField) graphicalZone.text).getSpecialPaste()
								.actionPerformed(E);
						repaint();
					}
				});
			}
		});
		normal.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				graphicalZone.setCursor(MyCursors.normalCursor);
				graphicalZone.EDITING_MODE = MyCursors.NORMAL;
				repaint();
			}
		});
		create.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				graphicalZone.setCursor(MyCursors.createBoxesCursor);
				graphicalZone.EDITING_MODE = MyCursors.CREATE_BOXES;
				repaint();
			}
		});
		kill.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				graphicalZone.setCursor(MyCursors.killBoxesCursor);
				graphicalZone.EDITING_MODE = MyCursors.KILL_BOXES;
				graphicalZone.unSelectAllBoxes();
				boxContentEditor.validateTextField();
			}
		});
		link.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				graphicalZone.setCursor(MyCursors.linkBoxesCursor);
				graphicalZone.EDITING_MODE = MyCursors.LINK_BOXES;
				repaint();
			}
		});
		reverseLink.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				graphicalZone.setCursor(MyCursors.reverseLinkBoxesCursor);
				graphicalZone.EDITING_MODE = MyCursors.REVERSE_LINK_BOXES;
				repaint();
			}
		});
		openSubgraph.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				graphicalZone.setCursor(MyCursors.openSubgraphCursor);
				graphicalZone.EDITING_MODE = MyCursors.OPEN_SUBGRAPH;
				boxContentEditor.validateTextField();
			}
		});
		configuration.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
				    	  GraphPresentationInfo info=UnitexFrame.getFrameManager().newGraphPresentationDialog(getGraphPresentationInfo(),true);
				    	  if (info!=null) {
				    		  setGraphPresentationInfo(info);
				    	  }
					}
				});
			}
		});

		ButtonGroup bg = new ButtonGroup();
		bg.add(normal);
		bg.add(create);
		bg.add(kill);
		bg.add(link);
		bg.add(reverseLink);
		bg.add(openSubgraph);

		normal.setSelected(true);
		myToolBar.add(save);
		myToolBar.add(compile);

		myToolBar.addSeparator();
		myToolBar.addSeparator();

		myToolBar.add(copy);
		myToolBar.add(cut);
		myToolBar.add(paste);
		myToolBar.add(redoButton);
		myToolBar.add(undoButton);

		myToolBar.addSeparator();
		myToolBar.addSeparator();

		myToolBar.add(normal);
		myToolBar.add(create);
		myToolBar.add(kill);
		myToolBar.add(link);
		myToolBar.add(reverseLink);
		myToolBar.add(openSubgraph);

		myToolBar.addSeparator();
		myToolBar.addSeparator();

		myToolBar.add(configuration);
	}

	private JPanel buildTextPanel() {
		JPanel p = new JPanel(new BorderLayout());
		boxContentEditor = new TextField(25, this);
		boxContentEditor.setComponentOrientation(Preferences.rightToLeft()?ComponentOrientation.RIGHT_TO_LEFT:ComponentOrientation.LEFT_TO_RIGHT);
		p.add(boxContentEditor);
		return p;
	}

	class MyInternalFrameListener extends InternalFrameAdapter {

		public void internalFrameActivated(InternalFrameEvent e) {
			boxContentEditor.requestFocus();
			boxContentEditor.getCaret().setVisible(true);
		}

		public void internalFrameClosing(InternalFrameEvent e) {
			if (modified) {
				Object[] options_on_exit = { "Save", "Don't save" };
				Object[] normal_options = { "Save", "Don't save", "Cancel" };
				int n;
				if (UnitexFrame.closing) {
					n = JOptionPane
							.showOptionDialog(
									GraphFrame.this,
									"Graph has been modified. Do you want to save it ?",
									"", JOptionPane.YES_NO_CANCEL_OPTION,
									JOptionPane.QUESTION_MESSAGE, null,
									options_on_exit, options_on_exit[0]);
				} else {
					n = JOptionPane
							.showOptionDialog(
									GraphFrame.this,
									"Graph has been modified. Do you want to save it ?",
									"", JOptionPane.YES_NO_CANCEL_OPTION,
									JOptionPane.QUESTION_MESSAGE, null,
									normal_options, normal_options[0]);
				}
				if (n == JOptionPane.CLOSED_OPTION)
					return;
				if (n == 0) {
					if (!UnitexFrame.mainFrame.saveGraph(GraphFrame.this))
						return;
				}
				if (n != 2) {
					dispose();
					return;
				}
				return;
			}
			dispose();
		}
	}

	/**
	 * Resizes the drawing area
	 * 
	 * @param x
	 * @param y
	 */
	public void reSizeGraphicalZone(int x, int y) {
		graphicalZone.Width = x;
		graphicalZone.Height = y;
		graphicalZone.setPreferredSize(new Dimension(x, y));
		graphicalZone.revalidate();
		graphicalZone.repaint();
		setModified(true);
	}

	/**
	 * Sets the <code>modified</code> field
	 * 
	 * @param b
	 *            <code>true</code> if the graph must be marked as modified,
	 *            <code>false</code> otherwise
	 */
	public void setModified(boolean b) {
		modified = b;
		if (grf != null) {
			if (modified)
				setTitle(grf.getName() + " (Unsaved)");
			else
				setTitle(grf.getName());
		} else {
			if (modified)
				setTitle(" (Unsaved)");
			else
				setTitle("Graph");
		}
	}

	/**
	 * Sets the zoom scale factor
	 * 
	 * @param d
	 *            scale factor
	 */
	public void setScaleFactor(double d) {
		graphicalZone.scaleFactor = d;
		graphicalZone.setPreferredSize(new Dimension(
				(int) (graphicalZone.Width * graphicalZone.scaleFactor),
				(int) (graphicalZone.Height * graphicalZone.scaleFactor)));
		graphicalZone.revalidate();
		graphicalZone.repaint();
	}

	/**
	 * Sorts lines of all selected boxes
	 *  
	 */
	public void sortNodeLabel() {
		if (graphicalZone.selectedBoxes.isEmpty())
			return;
		for (int i = 0; i < graphicalZone.selectedBoxes.size(); i++) {
			GraphBox g = (GraphBox) graphicalZone.selectedBoxes.get(i);
			g.sortNodeLabel();
		}
		graphicalZone.unSelectAllBoxes();
		boxContentEditor.initText("");
		graphicalZone.repaint();
	}

	/**
	 * Inverts the antialiasing flag
	 *  
	 */
	public void changeAntialiasingValue() {
		GraphPresentationInfo info=getGraphPresentationInfo();
		info.antialiasing = !info.antialiasing;
		graphicalZone.repaint();
	}

	/**
	 *  
	 */
	private void updateDoUndoButtons() {

		if (undoButton != null && redoButton != null) {
			undoButton.setEnabled(manager.canUndo());
			redoButton.setEnabled(manager.canRedo());
		}

	}

	class UndoIt implements ActionListener {
		public void actionPerformed(ActionEvent ev) {
			try {
				manager.undo();
			} catch (CannotUndoException ex) {
				ex.printStackTrace();
			} finally {
				repaint();
			}
		}

	}

	class RedoIt implements ActionListener {
		public void actionPerformed(ActionEvent ev) {
			try {
				manager.redo();
			} catch (CannotRedoException ex) {
				ex.printStackTrace();
			} finally {
				repaint();
			}
		}
	}

	public void repaint() {
		super.repaint();
		updateDoUndoButtons();
	}

	public void setRedoEnabled(boolean b) {
		redoButton.setEnabled(b);
	}

	public void setUndoEnabled(boolean b) {
		undoButton.setEnabled(b);
	}

	public File getGraph() {
		return grf;
	}

	public void setGraph(File grf) {
		this.grf = grf;
		this.nonEmptyGraph=true;
		this.setTitle(grf.getName()+" ("+grf.getParent()+")");
	}

	public void saveGraphAsAnImage(File output) {
		BufferedImage image=new BufferedImage(graphicalZone.Width,graphicalZone.Height,BufferedImage.TYPE_INT_RGB);
		Graphics g=image.getGraphics();
		graphicalZone.paintAll(g);
		try {
			ImageOutputStream stream=ImageIO.createImageOutputStream(output);
			ImageIO.write(image,"png",stream);
			stream.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		g.dispose();
	}

	/**
	 * This function saves the current graph frame as a SVG file.
	 * @param file
	 */
	public void saveGraphAsAnSVG(File file) {
	      FileOutputStream stream;
	      try {
	         if (!file.exists())
	            file.createNewFile();
	      } catch (IOException e) {
	         JOptionPane.showMessageDialog(
	            null,
	            "Cannot write " + file.getAbsolutePath(),
	            "Error",
	            JOptionPane.ERROR_MESSAGE);
	         return;
	      }
	      if (!file.canWrite()) {
	         JOptionPane.showMessageDialog(
	            null,
	            "Cannot write " + file.getAbsolutePath(),
	            "Error",
	            JOptionPane.ERROR_MESSAGE);
	         return;
	      }
	      try {
			stream=new FileOutputStream(file);
			OutputStreamWriter writer=new OutputStreamWriter(stream,"UTF-8");
			SVG svg=new SVG(writer,this);
			svg.save();
			writer.close();
			stream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	      
	}

	public JScrollPane getScroll() {
		return scroll;
	}

	public GraphPresentationInfo getGraphPresentationInfo() {
		return graphicalZone.getGraphPresentationInfo();
	}

	public void setGraphPresentationInfo(GraphPresentationInfo info) {
		updateToolBar(info.iconBarPosition);
		graphicalZone.setGraphPresentationInfo(info);
		setModified(true);
	}

	private void updateToolBar(String iconBarPosition) {
		mainPanel.remove(myToolBar);
		if (!(iconBarPosition
				.equals(Preferences.NO_ICON_BAR))) {
			if (iconBarPosition
					.equals(Preferences.ICON_BAR_WEST)
					|| iconBarPosition
							.equals(Preferences.ICON_BAR_EAST)) {
				myToolBar.setOrientation(SwingConstants.VERTICAL);
			} else {
				myToolBar.setOrientation(SwingConstants.HORIZONTAL);
			}
			mainPanel.add(myToolBar, iconBarPosition);
		}
		mainPanel.revalidate();
		mainPanel.repaint();
	}

	public void HTopAlign() {
		graphicalZone.HTopAlign();
		setModified(true);
	}

	public void HCenterAlign() {
		graphicalZone.HCenterAlign();
		setModified(true);
	}
	
	public void HBottomAlign() {
		graphicalZone.HBottomAlign();
		setModified(true);
	}

	public void VLeftAlign() {
		graphicalZone.VLeftAlign();
		setModified(true);
	}

	public void VCenterAlign() {
		graphicalZone.VCenterAlign();
		setModified(true);
	}

	public void VRightAlign() {
		graphicalZone.VRightAlign();
		setModified(true);
	}

	public void setGrid(boolean b,int n) {
		graphicalZone.setGrid(b,n);
		setModified(true);
	}

} /* end of GraphFrame */