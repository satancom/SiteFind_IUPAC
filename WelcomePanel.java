/*****************************************************************************\
*   WelcomePanel.java                                                         *
*                                                                             *
*   Copyright 2005, Paul M Evans                                              *
*                                                                             *
*   This file is part of SiteFind.                                            *
*                                                                             *
*   SiteFind is free software; you can redistribute it and/or modify          *
*   it under the terms of the GNU General Public License as published by      *
*   the Free Software Foundation; either version 2 of the License, or         *
*   (at your option) any later version.                                       *
*                                                                             *
*   SiteFind is distributed in the hope that it will be useful,               *
*   but WITHOUT ANY WARRANTY; without even the implied warranty of            *
*   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the             *
*   GNU General Public License for more details.                              *
*                                                                             *
*   You should have received a copy of the GNU General Public License         *
*   along with Foobar; if not, write to the Free Software                     *
*   Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA  *
\*****************************************************************************/

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;

// WelcomePanel
//
// WelcomePanel is a specific implementation of NextPanel.  It
// simply displays the name of the program, and a brief explanation
// of how it works.
public class WelcomePanel extends NextPanel {

	// Constructor
	public WelcomePanel(NextPanelListener n) {
		super(n);

		JEditorPane text = new JEditorPane();

		text.setContentType("text/html");
		text.setText(
		"<HTML><H1>SiteFind v4.0</H1><P>"+
		"Paul M. Evans, Chunming Liu<P>"+
		"Department of Human Biological Chemistry and Genetics<BR>"+
		"Sealy Center for Cancer Cell Biology<BR>"+
		"University of Texas Medical Branch<BR>"+
		"Galveston, TX 77555<P>"+
		"Site-directed mutagenesis is a common technique for introducing a single amino acid<BR>"+
		"mutation into a given nucleotide sequence.  SiteFind is an intuitive, web-based program<BR>"+
		"that enables the user to introduce a novel restriction site into the mutated nucleotide<BR>"+
		"sequence for use as a marker of successful mutation.<P>"+

		"If you have any questions or suggestions, please contact<BR>"+
		"Paul M. Evans (pmevans@utmb.edu) or Chunming Liu (chliu@utmb.edu).</HTML>");

		text.setEditable(false);
		text.setBackground(getBackground());

		thePanel.setLayout(new BorderLayout());
		thePanel.add(new JScrollPane(text), BorderLayout.CENTER);
		backButton.setVisible(false);
	}

	public void hasFocus(String results1, String results2, boolean defaultOutputFormat) {
	}
}
