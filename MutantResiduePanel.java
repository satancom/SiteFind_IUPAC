/*****************************************************************************\
*   MutantResiduePanel.java                                                   *
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

// MutantResiduePanel
public class MutantResiduePanel extends NextPanel implements CaretListener, ItemListener {
	JTextPane textPane;
	JScrollPane textScrollPane;
	JLabel selectedResidue;
	JComboBox residueList;
	String wildTypeDNASeq, wildTypeAminoSeq;
	int aminoSeqOffset;
	boolean aminoSelected;

	// Constructor
	public MutantResiduePanel(NextPanelListener n) {
		super(n);

		// Put all the text into the text window
		textPane = new JTextPane();
		textPane.setEditable(false);
		textPane.addCaretListener(this);
		JPanel noWrapPanel = new JPanel();
		noWrapPanel.setLayout(new BorderLayout());
		noWrapPanel.add(textPane);
		textScrollPane = new JScrollPane(noWrapPanel);

		// Adds the two text styles used for this panel: "regular", and "red" for
		// highlighted text
		StyledDocument doc = textPane.getStyledDocument();
        Style def = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
        Style regular = doc.addStyle("regular", def);
        StyleConstants.setFontFamily(def, "Monospaced");
        Style s = doc.addStyle("red", regular);
        StyleConstants.setForeground(s, Color.red);

		// Add the other components to a separate panel
		selectedResidue = new JLabel("A");
		String[] comboList = {
			"A", "C", "D", "E", "F", "G", "H", "I", "K", "L",
			"M", "N", "P", "Q", "R", "S", "T", "V", "W", "Y", "-" };
		residueList = new JComboBox(comboList);
		residueList.addItemListener(this);
		JPanel selectionPanel = new JPanel(new FlowLayout());
		selectionPanel.add(selectedResidue);
		selectionPanel.add(residueList);

		// Add it all to the panel
        thePanel.setLayout(new BorderLayout());
		thePanel.add(new JLabel("Double-click to select which residues to mutate (if any)"), BorderLayout.NORTH);
		thePanel.add(textScrollPane, BorderLayout.CENTER);
		thePanel.add(selectionPanel, BorderLayout.SOUTH);

		// Set the current selection to none (-1)
		aminoSeqOffset = -1;
		aminoSelected = false;
	}

	int getSelectedResidueIndex() {
		String text = textPane.getSelectedText();

		if(text != null) {
			int start = textPane.getSelectionStart(),
			    end = textPane.getSelectionEnd();

			// User selected the DNA sequence line
			if(start < aminoSeqOffset)
				return start / 4;

			// User selected the amino acid sequence line
			else
				return (start-aminoSeqOffset) / 4;
		}
		else
			return -1;
	}

	// Called whenever the mouse is clicked on the text area
	// Checks to see if the user has now selected a valid, single
	// amino acid from the text window, and if so change the
	// selectedResidue label to reflect the new selection
	public void caretUpdate(CaretEvent e) {
		int	selectedResidueIndex = getSelectedResidueIndex();

		if(selectedResidueIndex != -1) {
			String text;

			// Get the selected amino acid
			try {
				text = textPane.getText(selectedResidueIndex*4+aminoSeqOffset, 1);
			}
			catch(BadLocationException b) {
				System.err.println("Error getting text from textPane");
				text = null;
			}

			// Set that the wild type amino acid in the label, and the mutant amino
			// acid (if different) to the selected element in the combo box
			selectedResidue.setText(wildTypeAminoSeq.substring(selectedResidueIndex,
			                                                   selectedResidueIndex+1));
			residueList.setSelectedItem(text);
			aminoSelected = true;;
		}
		else {
			selectedResidue.setText(" ");
			aminoSelected = false;
		}
	}

	// Called whenever selection in combo box changes.  Then checks to
	// make sure there is a valid selection in the text window and changes
	// the appropriate amino acid to red if this is the case
	public void itemStateChanged(ItemEvent e) {
		if(e.getStateChange() == ItemEvent.SELECTED && aminoSelected) {
			int selectedResidueIndex = getSelectedResidueIndex(),
			    textIndex = selectedResidueIndex*4+aminoSeqOffset;
				StyledDocument doc = textPane.getStyledDocument();

		    String newText = (String)residueList.getSelectedItem(),
		           originalText = wildTypeAminoSeq.substring(selectedResidueIndex,
		                                                     selectedResidueIndex+1);
			try {
				doc.remove(textIndex, 1);
		    	if(originalText.equals(newText))
					doc.insertString(textIndex, newText, doc.getStyle("regular"));
				else
					doc.insertString(textIndex, newText, doc.getStyle("red"));
				textPane.setSelectionStart(textIndex);
				textPane.setSelectionEnd(textIndex+1);
			}
			catch(BadLocationException b) {
				System.err.println("Error replacing text in JTextPane");
			}
		}
	}


	// Should be called whenever the previous panel, WildTypeDNAPanel,
	// is finished and wants to pass on the validated DNA sequence
	// (formatted as one contiguous string) and translated amino
	// acid sequence (as one contiguous string).  loadText will then
	// enter the text into the text window, all displayed in the default
	// color (black)
	//
	// Note: dnaSeq.length() must equal aminoSeq.length()*3 for everything
	// to work properly
	public void loadText(String dnaSeq, String aminoSeq) {
		wildTypeDNASeq = dnaSeq;
		wildTypeAminoSeq = aminoSeq;
		StyledDocument doc = textPane.getStyledDocument();
		aminoSeqOffset = dnaSeq.length()+dnaSeq.length()/3+1;
		aminoSelected = false;

		if(dnaSeq.length() != aminoSeq.length()*3) {
			System.err.println("DNA sequence and Amino Acid sequence are of "+
				"incompatible lengths!");
			return;
		}

		try {
			Style s = doc.getStyle("regular");
			for(int i = 0; i < dnaSeq.length(); i += 3)
				doc.insertString(doc.getLength(), dnaSeq.substring(i,i+3)+" ", s);
			doc.insertString(doc.getLength(), "\n", s);
			for(int i = 0; i < aminoSeq.length(); i++)
				doc.insertString(doc.getLength(), aminoSeq.substring(i,i+1)+"   ", s);
		}
		catch(BadLocationException b) {
			System.err.println("Error inserting text into JTextPane");
		}
	}

	// Next button on the Wild-Type DNA Sequence window has been clicked and the
	// sequence validated, go ahead and enter it into this window.  Remove any
	// previous data in the window, if any.
	//
	// results1 = Wild-Type DNA sequence
	// results2 = null
	public void hasFocus(String results1, String results2, boolean defaultOutputFormat) {
		StyledDocument doc = textPane.getStyledDocument();
		try {
			doc.remove(0, doc.getLength());
		}
		catch(BadLocationException b) {
			System.err.println("Error removing text from JTextPane");
		}
		loadText(results1, Translator.fromDNAtoAA(results1));
		selectedResidue.setText(" ");
		residueList.setSelectedItem("A");
	}

	public void actionPerformed(ActionEvent a) {
		String command = a.getActionCommand();

		if(command.equals(backString))
			listener.backClicked();
		else if(command.equals(nextString)) {
			StyledDocument doc = textPane.getStyledDocument();
			String temp = "";
			StringBuffer mutantAminoSeq = new StringBuffer("");
			try {
				temp = doc.getText(aminoSeqOffset, doc.getLength()-aminoSeqOffset+1);
			}
			catch(BadLocationException b) {
				System.err.println("Error reading text from JTextPane");
			}
			for(int i = 0; i < temp.length(); i++) {
				char ch = temp.charAt(i);
				if(Character.isWhitespace(ch) == false)
					mutantAminoSeq.append(ch);
			}
			listener.nextClicked(wildTypeDNASeq, mutantAminoSeq.toString(), true);
		}
	}
}
