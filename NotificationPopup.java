/*****************************************************************************\
*   NotificationPopup.java                                                    *
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

public class NotificationPopup extends JFrame implements ActionListener {
	JLabel label;
	JButton okButton;

	public NotificationPopup(String badString) {
		super("Error");

		label = new JLabel(badString);
		okButton = new JButton("OK");
		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints constraints = new GridBagConstraints();

		okButton.addActionListener(this);
		getContentPane().setLayout(new FlowLayout());
		constraints.anchor = GridBagConstraints.NORTH;
		layout.setConstraints(label, constraints);
		getContentPane().add(label);
		constraints.anchor = GridBagConstraints.SOUTH;
		layout.setConstraints(okButton, constraints);
		getContentPane().add(okButton);
		pack();
		setSize(label.getWidth()+40, getHeight()+20);
		setVisible(true);
	}

	public void actionPerformed(ActionEvent e) {
		setVisible(false);
		dispose();
	}
}
