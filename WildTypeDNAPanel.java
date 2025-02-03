/*****************************************************************************\
*   WildTypeDNAPanel.java                                                     *
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
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

// WildTypeDNAPanel
public class WildTypeDNAPanel extends NextPanel {
	JTextArea textArea;

	public WildTypeDNAPanel(NextPanelListener n) {
		super(n);

		textArea = new JTextArea();
		textArea.setLineWrap(true);
		textArea.setText("");//"CTCGAGCGACCGAATTGACCGAACGGTCATGGCTTCCCTTGGCTAGTGCAGTGGCCCAAGTGCTTACTACTAGGTGGGAACCCAGTCTGGCACTTACGCACGTGTACAGAA");

		// Add it all to the panel
        thePanel.setLayout(new BorderLayout());
		thePanel.add(new JLabel("Please enter your wild-type DNA sequence below"/*, starting in-frame"*/), BorderLayout.NORTH);
		thePanel.add(textArea, BorderLayout.CENTER);
		textArea.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
    }

	public void actionPerformed(ActionEvent a) {
		String command = a.getActionCommand();

		if(command.equals(backString))
			listener.backClicked();
		else if(command.equals(nextString)) {
			String dnaSeq = getDNASequence();
			if(dnaSeq == null)
				new NotificationPopup("Invalid DNA Sequence - can only contain " +
				                      "'A', 'C', 'G', or 'T'");
			/*else if(dnaSeq.length() % 3 != 0)
				new NotificationPopup("Invalid DNA Sequence - must contain only " +
									  "complete codons (multiples of three)");*/
			else
				listener.nextClicked(dnaSeq, null, true);
		}
	}

	String getDNASequence() {
		String text = textArea.getText().toUpperCase().trim();
		StringBuffer formattedSequence = new StringBuffer("");

		for(int i = 0; i < text.length(); i++) {
			char ch = text.charAt(i);
			if(Character.isWhitespace(ch) == false) {
				if(ch == 'A' || ch == 'C' || ch == 'G' || ch == 'T')
					formattedSequence.append(ch);
				else
					return null;
			}
		}

		return formattedSequence.toString();
	}

	// Don't need to do anything after the Welcome window's next button is clicked
	public void hasFocus(String results1, String results2, boolean defaultOutputFormat) {
	}
}
