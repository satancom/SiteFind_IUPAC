/*****************************************************************************\
*   ResultsFormatPanel.java                                                   *
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
import java.awt.*;
import java.awt.event.*;

// ResultsFormatPanel
public class ResultsFormatPanel extends NextPanel {
	String wildTypeDNASeq, mutantAminoSeq;
	JRadioButton bestButton, allButton;

	public ResultsFormatPanel(NextPanelListener n) {
		super(n);

		bestButton = new JRadioButton("Display only location closest to point mutation (default)");
		bestButton.setSelected(true);
		allButton = new JRadioButton("List every location found");
		ButtonGroup g = new ButtonGroup();
		g.add(bestButton);
		g.add(allButton);
		JPanel buttonPanel = new JPanel(new GridLayout(0, 1));
		buttonPanel.add(new JLabel("For each restriction enzyme, "));
		buttonPanel.add(bestButton);
		buttonPanel.add(allButton);

		JPanel formatPanel = new JPanel(new BorderLayout());
		formatPanel.add(buttonPanel, BorderLayout.NORTH);
		JPanel formatPanel2 = new JPanel(new BorderLayout(20, 20));
		formatPanel2.add(formatPanel, BorderLayout.CENTER);
		formatPanel2.add(new JLabel(" "), BorderLayout.WEST);
		formatPanel2.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

		thePanel.setLayout(new BorderLayout());
		thePanel.add(formatPanel2, BorderLayout.CENTER);
		thePanel.add(new JLabel("Please select the output format"), BorderLayout.NORTH);
	}

	public void actionPerformed(ActionEvent a) {
		String command = a.getActionCommand();

		if(command.equals(backString))
			listener.backClicked();
		else if(command.equals(nextString)) {
			if(bestButton.isSelected())
				listener.nextClicked(wildTypeDNASeq, mutantAminoSeq, true);
			else if(allButton.isSelected())
				listener.nextClicked(wildTypeDNASeq, mutantAminoSeq, false);
		}
	}

	public void hasFocus(String results1, String results2, boolean defaultOutputFormat) {
		wildTypeDNASeq = results1;
		mutantAminoSeq = results2;
	}
}