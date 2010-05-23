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

import java.awt.Dimension;
import java.io.File;
import java.util.ArrayList;

import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import fr.umlv.unitex.GraphPresentationInfo;
import fr.umlv.unitex.io.GraphIO;


public class GraphFrameFactory {

	ArrayList<GraphFrame> frames=new ArrayList<GraphFrame>();
	
	GraphFrame getGraphFrame(File grf) {
		if (grf!=null) {
			for (GraphFrame gf:frames) {
				if (grf.equals(gf.getGraph())) {
					return gf;
				}
			}
		}
		final GraphFrame f;
		if (grf!=null) {
			GraphIO g=GraphIO.loadGraph(grf);
			if (g==null) return null;
			f=new GraphFrame();
			GraphPresentationInfo info=g.getGraphPresentationInfo();
			f.setGraphPresentationInfo(info);
			f.boxContentEditor.setFont(info.input.font);
			f.graphicalZone.Width = g.width;
			f.graphicalZone.Height = g.height;
			f.graphicalZone.graphBoxes = g.boxes;
			f.getScroll().setPreferredSize(new Dimension(g.width, g.height));
			f.graphicalZone.setPreferredSize(new Dimension(g.width, g.height));
			f.setGraph(grf);
			/* Some loading operations may have set the modified flag, so we
			 * reset it
			 */
			f.setModified(false);
		} else {
			f=new GraphFrame();
		}
		frames.add(f);
		f.addInternalFrameListener(new InternalFrameAdapter() {
			@Override
			public void internalFrameClosed(InternalFrameEvent e) {
				frames.remove(f);
			}
		});
		return f;
	}

	
	@SuppressWarnings("unchecked")
	void closeAllGraphFrames() {
		/* We have to make a copy of the frame list because as the close
		 * action of each frame way remove it from 'frames', we could have
		 * problems 
		 */
		ArrayList<GraphFrame> copy=(ArrayList<GraphFrame>) frames.clone();
		for (GraphFrame f:copy) {
			f.doDefaultCloseAction();
		}
	}
	
	GraphFrame[] getGraphFrames() {
		GraphFrame[] f=new GraphFrame[frames.size()];
		f=frames.toArray(f);
		return f;
	}

}