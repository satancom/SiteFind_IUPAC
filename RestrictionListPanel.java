/*****************************************************************************\
*   RestrictionListPanel.java                                                 *
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
 * - Changed how restriction enzymes and their cut sites are stored.
 * - Commented out commercially unavailable enzymes
 */

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

// RestrictionListPanel
public class RestrictionListPanel extends NextPanel {
	JList listBox;
	JButton addButton, removeButton, clearButton, defaultButton;
	DefaultListModel listModel;
	Map enzymeList;

	String wildTypeDNASeq, mutantAminoSeq;

	private static final Map<String, String> ENZYME_MAP;
	static {
		Map<String, String> map = new HashMap<>();

		// map.put("AbrI", "CTCGAG");
		map.put("AccI", "GTMKAC");
		map.put("AgeI", "ACCGGT");
		map.put("AluI", "AGCT");
		map.put("ApaLI", "GTGCAC");
		// map.put("AquI", "CYCGRG");
		map.put("AvaI", "CYCGRG");
		map.put("BamHI", "GGATCC");
		// map.put("BamHII", "GGATCC");
		map.put("BanI", "GGYRCC");
		map.put("BanIII", "ATCGAT");
		map.put("BbvI(1)", "GCAGC");
		map.put("BbvI(2)", "GCTGC");
		// map.put("BepI", "CGCG");
		map.put("BglI", "GCCNNNNNGGC");
		map.put("BglII", "AGATCT");
		// map.put("BseCI", "ATCGAT");
		map.put("BslI", "CCNNNNNNNGG");
		map.put("BsoBI", "CYCGRG");
		// map.put("Bsp6I", "GCNGC");
		// map.put("BspRI", "GGCC");
		// map.put("BstVI", "CTCGAG");
		// map.put("BsuBI", "CTGCAG");
		// map.put("BsuFI", "CCGG");
		// map.put("BsuRI", "GGCC");
		// map.put("CeqI", "GATATC");
		// map.put("Cfr9I", "CCCGGG");
		// map.put("Cfr10I", "RCCGGY");
		// map.put("CfrBI", "CCWWGG");
		map.put("ClaI", "ATCGAT");
		map.put("CviAII", "CATG");
		// map.put("CviBI", "GANTC");
		// map.put("CviJI", "RGCY");
		// map.put("CviRI", "TGCA");
		map.put("DdeI", "CTNAG");
		map.put("DpnI", "GATC");
		map.put("DpnII", "GATC");
		// map.put("DsaV", "CCNGG");
		// map.put("EcaI", "GGTNACC");
		// map.put("Eco47I", "GGWCC");
		// map.put("Eco47II", "GGNCC");
		// map.put("Eco57I(1)", "CTGAAG");
		// map.put("Eco57I(2)", "CTTCAG");
		map.put("EcoRI", "GAATTC");
		// map.put("EcoRII", "CCWGG");
		map.put("EcoRV", "GATATC");
		// map.put("FnuDI", "GGCC");
		map.put("FokI(1)", "GGATG");
		map.put("FokI(2)", "CATCC");
		map.put("HaeII", "RGCGCY");
		map.put("HaeIII", "GGCC");
		map.put("HgaI(1)", "GACGC");
		map.put("HgaI(2)", "GCGTC");
		// map.put("HgiBI", "GGWCC");
		// map.put("HgiCI", "GGYRCC");
		// map.put("HgiCII", "GGWCC");
		// map.put("HgiDI", "GRCGYC");
		// map.put("HgiDII", "GTCGAC");
		// map.put("HgiEI", "GGWCC");
		// map.put("HgiGI", "GRCGYC");
		map.put("HhaI", "GCGC");
		// map.put("HhaII", "GANTC");
		map.put("HincII", "GTYRAC");
		map.put("HindII", "GTYRAC");
		map.put("HindIII", "AAGCTT");
		map.put("HindV", "GRCGYC");
		map.put("HinfI", "GANTC");
		map.put("HpaI", "GTTAAC");
		map.put("HpaII", "CCGG");
		map.put("HphI(1)", "GGTGA");
		map.put("HphI(2)", "TCACC");
		map.put("KpnI", "GGTACC");
		// map.put("LlaDCHI", "GATC");
		// map.put("MvaI", "CCWGG");
		// map.put("NgoBI", "RGCGCY");
		// map.put("NgoBV", "GGNNCC");
		// map.put("NgoFVII", "GCSGC");
		// map.put("NgoPII", "GGCC");
		// map.put("NspV", "TTCGAA");
		// map.put("PspPI", "GGNCC");
		// map.put("RsrI", "GAATTC");
		// map.put("SinI", "GGWCC");
		// map.put("SsoII", "CCNGG");
		// map.put("StsI(1)", "GGATG");
		// map.put("StsI(2)", "CATCC");
		// map.put("TthHB8I", "TCGA");
		// map.put("VspI", "ATTAAT");
		// map.put("XamI", "GTCGAC");
		map.put("XbaI", "TCTAGA");
		// map.put("XcyI", "CCCGGG");
		map.put("XhoI", "CTCGAG");
		// map.put("XorII", "CGATCG");

		ENZYME_MAP = Collections.unmodifiableMap(map);
	}

	public static Map<String, String> getEnzymeMap() {
		return ENZYME_MAP;
	}

	public RestrictionListPanel(NextPanelListener n, Map e) {
		super(n);

		enzymeList = e;
		listModel = new DefaultListModel();
		listBox = new JList(listModel);
		addButton = new JButton("Add");
		removeButton = new JButton("Remove");
		clearButton = new JButton("Clear");
		defaultButton = new JButton("Default");

		JPanel buttonPanel = new JPanel(new GridLayout(4, 1, 5, 5));
		buttonPanel.add(addButton);
		buttonPanel.add(removeButton);
		buttonPanel.add(clearButton);
		buttonPanel.add(defaultButton);
		thePanel.setLayout(new BorderLayout(0, 5));
		thePanel.add(new JLabel("Please select which restriction sites to search for"),
			BorderLayout.NORTH);
		thePanel.add(new JScrollPane(listBox), BorderLayout.CENTER);
		thePanel.add(buttonPanel, BorderLayout.EAST);

		defaultList();

		addButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						addListPopup();
					}
		});
		removeButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						removeListElement(listBox.getSelectedIndices());
					}
		});
		clearButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						clearList();
					}
		});
		defaultButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						defaultList();
					}
		});
	}

	public void addListPopup() {
		new RestrictionPopup(this);
	}

	public void addListElement(String name, String site) {
		enzymeList.put(name, new RestrictionEnzyme(name, site));
		//new NotificationPopup("Added");
		listModel.clear();
		Object[] l = enzymeList.keySet().toArray();
		for(int i = 0; i < l.length; i++)
			listModel.addElement((String)l[i]);
	}

	public void removeListElement(int[] index_list) {
		for(int i = index_list.length-1; i >= 0; i--) {
			enzymeList.remove(listModel.elementAt(index_list[i]));
			listModel.removeElementAt(index_list[i]);
		}
	}
	public void clearList() {
		enzymeList.clear();
		listModel.clear();
	}

	public void defaultList() {
		enzymeList.clear();
		listModel.clear();

		for (String name : ENZYME_MAP.keySet()) {
			enzymeList.put(name, new RestrictionEnzyme(name, ENZYME_MAP.get(name))); //
			listModel.addElement(name);
        }

		//for(int i = 0; i < nameList.length; i++) {
		//	enzymeList.put(ENZYME_MAP.keySet()[i], new RestrictionEnzyme(nameList[i], siteList[i])); //
		//	listModel.addElement(nameList[i]);
		//}
	}

	public void actionPerformed(ActionEvent a) {
		String command = a.getActionCommand();

		if(command.equals(backString))
			listener.backClicked();
		else if(command.equals(nextString))
			listener.nextClicked(wildTypeDNASeq, mutantAminoSeq, true);
	}

	public void hasFocus(String results1, String results2, boolean defaultOutputFormat) {
		wildTypeDNASeq = results1;
		mutantAminoSeq = results2;
	}
}
