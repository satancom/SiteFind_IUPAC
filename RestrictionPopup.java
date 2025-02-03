/*****************************************************************************\
*   RestrictionPopup.java                                                     *
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

/*
 * Modified version of SiteFind
 * Original Copyright (C) 2005 Paul M Evans
 * Modifications by Max Saltykov, 2025
 * Changes:
 * - Now supports adding IUPAC coded recognition sites
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class RestrictionPopup extends JFrame {
	protected RestrictionListPanel parent;
	protected JTextField name, site;
	protected JButton okButton, cancelButton;


	public RestrictionPopup(RestrictionListPanel p) {
		super("Add Restriction Enzyme");
		parent = p;
		name = new JTextField(10);
		site = new JTextField(10);
		okButton = new JButton("OK");
		cancelButton = new JButton("Cancel");

		JPanel namePanel = new JPanel(new FlowLayout());
		namePanel.add(new JLabel("Name"));
		namePanel.add(name);
		JPanel sitePanel = new JPanel(new FlowLayout());
		sitePanel.add(new JLabel("Site"));
		sitePanel.add(site);
		JPanel fieldPanel = new JPanel(new GridLayout(2, 1));
		fieldPanel.add(namePanel);
		fieldPanel.add(sitePanel);

		JPanel buttonPanel = new JPanel(new FlowLayout());
		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(fieldPanel, BorderLayout.CENTER);
		getContentPane().add(buttonPanel, BorderLayout.SOUTH);

		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				okClicked();
			}
		});
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cancelClicked();
			}
		});
		pack();
		setVisible(true);
	}

	public Dimension getPreferredSize() {
		return new Dimension(150, 200);
	}

	public void okClicked() {
		String n = name.getText(), s = site.getText().toUpperCase();
		if(s.length() < 4)
			new NotificationPopup("\""+s+"\" is too few characters (must be at least four)");
		else {
			for(int i = 0; i < s.length(); i++){
				if(s.charAt(i) != 'A' && s.charAt(i) != 'C' && s.charAt(i) != 'G' && s.charAt(i) != 'T' &&
				s.charAt(i) != 'U' && s.charAt(i) != 'R' && s.charAt(i) != 'Y' && s.charAt(i) != 'S' &&
				s.charAt(i) != 'W' && s.charAt(i) != 'K' && s.charAt(i) != 'M' && s.charAt(i) != 'B' &&
				s.charAt(i) != 'D' && s.charAt(i) != 'H' && s.charAt(i) != 'V' && s.charAt(i) != 'N') {
					new NotificationPopup("\""+s+"\" is not a valid restriction site");
					return;
				}
			}
			parent.addListElement(n, s);
			setVisible(false);
			dispose();
		}
	}

	public void cancelClicked() {
		setVisible(false);
		dispose();
	}
}
