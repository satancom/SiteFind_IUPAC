/*****************************************************************************\
*   ResultsPanel.java                                                         *
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
import javax.swing.text.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;

// ResultsPanel
public class ResultsPanel extends NextPanel {
	JTextArea resultsWindow;

	public ResultsPanel(NextPanelListener n) {
		super(n);

		nextString = "Start Over";
		resultsWindow = new JTextArea();
		resultsWindow.setFont(new Font("Monospaced", Font.PLAIN, 12));
		thePanel.setLayout(new BorderLayout());
		thePanel.add(new JLabel("Results"), BorderLayout.NORTH);
		thePanel.add(new JScrollPane(resultsWindow), BorderLayout.CENTER);
		nextButton.setText(nextString);
	}

	public void hasFocus(String results1, String results2, boolean defaultOutputFormat) {
		resultsWindow.setText(results1.equals("") ? "No sites found!" : results1);
	}
}
