/*****************************************************************************\
*   NextPanel.java                                                            *
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

// NextPanel
//
// Generic panel, allowing user to click the "Next" button at the
// bottom, right-hand corner of the window to go to the next panel.
// Parent window must take care of removing this panel and replacing
// it with the subsequent panel.  The ActionListener object specified
// in the constructor is called when the "Next" button is clicked and
// should respond by changing panels.
public abstract class NextPanel extends JPanel implements ActionListener {
	JPanel thePanel;
	JButton nextButton, backButton;
	NextPanelListener listener;
	String nextString, backString;

	// Constructor
	// Give thePanel all the space not taken up by the "Next" button,
	// The "Next" button gets its own panel at the bottom to ensure
	// it is placed correctly.
	public NextPanel(NextPanelListener n) {
		listener = n;
		nextString = "Next>";
		backString = "<Back";

		setLayout(new BorderLayout());
		thePanel = new JPanel();
		JPanel buttonPanel = new JPanel(new BorderLayout());
		backButton = new JButton(backString);
		nextButton = new JButton(nextString);

		buttonPanel.add(backButton, BorderLayout.WEST);
		buttonPanel.add(nextButton, BorderLayout.EAST);
		add(thePanel, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);

		backButton.addActionListener(this);
		nextButton.addActionListener(this);
	}

	public void actionPerformed(ActionEvent a) {
		String command = a.getActionCommand();

		if(command.equals(backString))
			listener.backClicked();
		else if(command.equals(nextString))
			listener.nextClicked(null, null, true);
	}

	public abstract void hasFocus(String results1, String results2, boolean defaultOutputFormat);
}