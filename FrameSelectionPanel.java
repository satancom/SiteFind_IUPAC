/*****************************************************************************\
*   FrameSelectionPanel.java                                                  *
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

// FrameSelectionPanel
public class FrameSelectionPanel extends NextPanel {
	String dnaSeq1, dnaSeq2, dnaSeq3, seqFrame1, seqFrame2, seqFrame3;
	JRadioButton buttonFrame1, buttonFrame2, buttonFrame3;

	public FrameSelectionPanel(NextPanelListener n) {
		super(n);

		buttonFrame1 = new JRadioButton("Frame 1");
		buttonFrame1.setSelected(true);
		//buttonFrame1.setBackground(Color.WHITE);
		buttonFrame2 = new JRadioButton("Frame 2");
		//buttonFrame2.setBackground(Color.WHITE);
		buttonFrame3 = new JRadioButton("Frame 3");
		//buttonFrame3.setBackground(Color.WHITE);
		ButtonGroup g = new ButtonGroup();
		g.add(buttonFrame1);
		g.add(buttonFrame2);
		g.add(buttonFrame3);
		JPanel buttonPanel = new JPanel(new GridLayout(0, 1));
		buttonPanel.add(buttonFrame1);
		buttonPanel.add(buttonFrame2);
		buttonPanel.add(buttonFrame3);
		buttonPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
		//buttonPanel.setBackground(Color.WHITE);
		thePanel.setLayout(new BorderLayout());
		thePanel.add(new JLabel("Please select the correct reading frame for your sequence"), BorderLayout.NORTH);
		thePanel.add(buttonPanel, BorderLayout.CENTER);
	}

	public void actionPerformed(ActionEvent a) {
		String command = a.getActionCommand();

		if(command.equals(backString))
			listener.backClicked();
		else if(command.equals(nextString)) {
			if(buttonFrame1.isSelected())
				listener.nextClicked(dnaSeq1, null, true);
			else if(buttonFrame2.isSelected())
				listener.nextClicked(dnaSeq2, null, true);
			else if(buttonFrame3.isSelected())
				listener.nextClicked(dnaSeq3, null, true);
		}
	}

	public void hasFocus(String results1, String results2, boolean defaultOutputFormat) {
		dnaSeq1 = results1.substring(0, results1.length()-(results1.length()%3));
		if(results1.length() > 3)
			dnaSeq2 = results1.substring(1, results1.length()-((results1.length()-1)%3));
		else
			dnaSeq2 = "";
		if(results1.length() > 4)
			dnaSeq3 = results1.substring(2, results1.length()-((results1.length()-2)%3));
		else
			dnaSeq3 = "";

		seqFrame1 = Translator.fromDNAtoAA(dnaSeq1);
		seqFrame2 = Translator.fromDNAtoAA(dnaSeq2);
		seqFrame3 = Translator.fromDNAtoAA(dnaSeq3);
		buttonFrame1.setText("Frame 1:  "+seqFrame1);
		buttonFrame2.setText("Frame 2:  "+seqFrame2);
		buttonFrame3.setText("Frame 3:  "+seqFrame3);
	}
}