/*****************************************************************************\
*   SearchStatusPanel.java                                                    *
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
import java.util.*;
import java.util.List;

// To Do
// Rank results in order of distance from mutation sites, if no mutations
// rank by fewest number of changes

// SearchStatusPanel
public class SearchStatusPanel extends NextPanel {

	// Java is kinda annoying about creating static data structures
	// I'm trying to create a data structure that contains all the possible
	// codons that code for a particular amino acid
	class AminoCode {
		public String codeList[];
		public AminoCode(int numCodes, String c1, String c2, String c3) {
			if(numCodes > 0) {
				codeList = new String[numCodes];
				if(numCodes > 0) {
					codeList[0] = c1;
					if(numCodes > 1) {
						codeList[1] = c2;
						if(numCodes > 2)
							codeList[2] = c3;
					}
				}
			}
		}
	};

	JProgressBar bar;
	JLabel searchLabel;
	SearchThread search;
	String wildTypeDNASeq, mutantAminoSeq, resultsString;
	Map aminoTable, enzymeList;
	boolean showBestResultOnly;

	public SearchStatusPanel(NextPanelListener n, Map l, int width) {
		super(n);

		resultsString = null;
		enzymeList = l;

		// Initialize the aminoTable Map with all the codes for each amino acid
		aminoTable = new Hashtable();
		aminoTable.put("A", new AminoCode(1, "GCN", null, null));
		aminoTable.put("C", new AminoCode(2, "TGT", "TGC", null));
		aminoTable.put("D", new AminoCode(2, "GAT", "GAC", null));
		aminoTable.put("E", new AminoCode(2, "GAA", "GAG", null));
		aminoTable.put("F", new AminoCode(2, "TTT", "TTC", null));
		aminoTable.put("G", new AminoCode(1, "GGN", null, null));
		aminoTable.put("H", new AminoCode(2, "CAT", "CAC", null));
		aminoTable.put("I", new AminoCode(3, "ATT", "ATC", "ATA"));
		aminoTable.put("K", new AminoCode(2, "AAA", "AAG", null));
		aminoTable.put("L", new AminoCode(3, "TTA", "TTG", "CTN"));
		aminoTable.put("M", new AminoCode(1, "ATG", null, null));
		aminoTable.put("N", new AminoCode(2, "AAT", "AAC", null));
		aminoTable.put("P", new AminoCode(1, "CCN", null, null));
		aminoTable.put("Q", new AminoCode(2, "CAA", "CAG", null));
		aminoTable.put("R", new AminoCode(3, "CGN", "AGA", "AGG"));
		aminoTable.put("S", new AminoCode(3, "TCN", "AGT", "AGC"));
		aminoTable.put("T", new AminoCode(1, "ACN", null, null));
		aminoTable.put("V", new AminoCode(1, "GTN", null, null));
		aminoTable.put("W", new AminoCode(1, "TGG", null, null));
		aminoTable.put("Y", new AminoCode(2, "TAT", "TAC", null));
		aminoTable.put("-", new AminoCode(3, "TGA", "TAG", "TAA"));

		searchLabel = new JLabel("Searching...");
		bar = new JProgressBar();
		bar.setPreferredSize(new Dimension(width-width/8, 20));
		JPanel progressPanel = new JPanel(new GridBagLayout());
		progressPanel.add(bar);
		thePanel.setLayout(new BorderLayout(20, 20));
		thePanel.add(searchLabel, BorderLayout.NORTH);
		thePanel.add(progressPanel, BorderLayout.CENTER);
		thePanel.add(new JPanel(), BorderLayout.SOUTH);
		showBestResultOnly = true;
	}

	public void hasFocus(String results1, String results2, boolean defaultOutputFormat) {
		wildTypeDNASeq = results1;
		mutantAminoSeq = results2;
		showBestResultOnly = defaultOutputFormat;
		nextButton.setVisible(false);
		backButton.setText("Cancel");
		searchLabel.setText("Searching...");
		clearSitesFound();
		resultsString = null;
		search = new SearchThread();
		search.start();
	}

	public void actionPerformed(ActionEvent a) {
		String command = a.getActionCommand();

		if(command.equals("Cancel")) {
			search.keepRunning = false;
			backButton.setText("<Back");
		}
		else if(command.equals("<Back"))
			listener.backClicked();
		else if(command.equals("Next>"))
			listener.nextClicked(resultsString, null, true);
	}

	void clearSitesFound() {
		String curName;
		RestrictionEnzyme curEnzyme;
		Object nameList[] = enzymeList.keySet().toArray();

		for(int i = 0; i < nameList.length; i++) {
			curName = (String)nameList[i];
			curEnzyme = (RestrictionEnzyme)enzymeList.get(curName);
			curEnzyme.sitesFound.clear();
		}
	}

	// SearchThread
	//
	// This class gets its own thread and does all the actual work of searching
	// for restriction sites.  Some of the functions might be more logically put
	// somewhere else, but since they're only used by this class, I think it's
	// best if they stay here.
	class SearchThread extends Thread {
		public boolean keepRunning;
		int windowIncrement, windowSize;
		int curCount;

		public void run() {
			keepRunning = true;

			int total = countNumberOfSequences(mutantAminoSeq),
			    totalMW = countNumberOfSequencesMovingWindow(mutantAminoSeq),
			    max;

			//System.out.println("Total seq = " + total + "\nTotalMW = "+totalMW);
			disableWTRestrictionSites();
			Object nameList[] = enzymeList.keySet().toArray();
			curCount = 0;

			if(totalMW > 0 && total > 0) {
				if(totalMW < total) {
					bar.setMaximum(totalMW);
					searchMovingWindow(mutantAminoSeq);
				}
				else {
					bar.setMaximum(total);
					buildList(mutantAminoSeq, "", "", "");
				}
			}
			else {
				bar.setMinimum(0);
				bar.setMaximum(100);
				bar.setValue(100);
			}
			if(keepRunning == true)
				resultsString = getResults();
			if(keepRunning == true) {
				backButton.setText("<Back");
				nextButton.setVisible(true);
				searchLabel.setText("Searching...Done!");
				listener.nextClicked(resultsString, null, true);
			}
			else
				searchLabel.setText("Cancelled.");
		}

		// disableWTRestrictionSites
		//
		// Searches through the wild-type sequence and sets the useEnzyme flag
		// to false if it is present in the wild-type sequence, since such a
		// sequence isn't usually a good marker
		void disableWTRestrictionSites() {
			Object nameList[] = enzymeList.keySet().toArray();
			String curName, curSite;
			RestrictionEnzyme curEnzyme;
			int i, j;

			for(i = 0; i < nameList.length; i++) {
				curName = (String)nameList[i];
				curEnzyme = (RestrictionEnzyme)enzymeList.get(curName);
				curSite = curEnzyme.site;

				curEnzyme.useEnzyme = true;
				for(j = 0; curSite.length()+j <= wildTypeDNASeq.length(); j++) {

					// We found a restriction site, disable it
					if(sequenceCompare(wildTypeDNASeq.substring(j), curSite))
						curEnzyme.useEnzyme = false;
				}
			}
		}

		// searchMovingWindow
		//
		// New algorithm.  Searches through the entire wild-type sequence
		// using a moving window for the buildList function, rather than
		// simply building a list of every possible DNA sequence coding for
		// a given amino acid sequence.
		void searchMovingWindow(String mutantAminoSeq) {

			// Shift the window in increments of the longest restriction site,
			// rounding up to the nearest codon
			int i, n = findLongestSite();
			windowIncrement = (n+2)/3;

			// The window should be long enough to ensure an overlap equal to
			// the length of the longest restriction site minus one, rounding up
			windowSize = (n+2)/3 + (n+1)/3;

			for(i = 0; i < mutantAminoSeq.length()-windowSize; i += windowIncrement)
				buildList(mutantAminoSeq.substring(i, i+windowSize), "",
						  wildTypeDNASeq.substring(0, i*3),
						  wildTypeDNASeq.substring((i+windowSize)*3));
			if(i < mutantAminoSeq.length())
				buildList(mutantAminoSeq.substring(i), "",
				          wildTypeDNASeq.substring(0, i*3), "");
		}

		// findLongestSite
		//
		// Searches through the restriction site database and returns the longest
		// possible restriction site.  Used to set the size of the moving window
		// for the search algorithm.
		int findLongestSite() {
			Object nameList[] = enzymeList.keySet().toArray();
			RestrictionEnzyme curEnzyme;
			String curName;
			int longestSite = 0;

			for(int i = 0; i < nameList.length; i++) {
				curName = (String)nameList[i];
				curEnzyme = (RestrictionEnzyme)enzymeList.get(curName);
				if(curEnzyme.site.length() > longestSite)
					longestSite = curEnzyme.site.length();
			}

			return longestSite;
		}

		int countNumberOfSequences(String seq) {
			if(seq.length() == 0)
				return 0;

			AminoCode code;
			int count = 1;

			for(int i = 0; i < seq.length(); i++) {
				code = (AminoCode)aminoTable.get(seq.substring(i,i+1));
				if(count > Integer.MAX_VALUE / code.codeList.length)
					return Integer.MAX_VALUE;
				count *= code.codeList.length;
			}

			return count;
		}

		int countNumberOfSequencesMovingWindow(String seq) {
			int i, count = 0,
			    n = findLongestSite(),
				windowIncrement = (n+2)/3,
				windowSize = (n+2)/3 + (n+1)/3;

			for(i = 0; i < seq.length()-windowSize; i += windowIncrement)
				count += countNumberOfSequences(seq.substring(i, i+windowSize));
			if(i < seq.length())
				count += countNumberOfSequences(seq.substring(i));

			return count;
		}

		// buildList
		//
		// Recursively builds every possible DNA sequence expressing a
		// particular amino acid sequence.  Once the recursion reaches
		// a point where a complete sequence is made, it then calls
		// searchForRestrictionSites to search for any possible
		// restriction sites in that sequence
		void buildList(String aminoSeq, String dnaSeq, String beforeSeq, String afterSeq) { // I dont like this. There are better ways to do this
			AminoCode code;

			// Stop if the user hit cancel
			if(keepRunning == false)
				return;

			// Otherwise keep searching
			if(aminoSeq.equals("") == false) {
				code = (AminoCode)aminoTable.get(aminoSeq.substring(0, 1));
				for(int i = 0; i < code.codeList.length; i++)
					buildList(aminoSeq.substring(1), dnaSeq+code.codeList[i], beforeSeq, afterSeq);
			}
			else {
				searchForRestrictionSites(dnaSeq, beforeSeq, afterSeq);
				bar.setValue(++curCount);
			}
		}

		// searchForRestrictionSites
		void searchForRestrictionSites(String dnaSeq, String beforeSeq, String afterSeq) {
			Object nameList[] = enzymeList.keySet().toArray();
			RestrictionEnzyme curEnzyme;
			String curName, curSite;
			int i, j, k;

			

			for(i = 0; i < nameList.length; i++) {
				curName = (String)nameList[i];
				curEnzyme = (RestrictionEnzyme)enzymeList.get(curName); // create instance of rest enz class
				if(curName == "test"){
					if(i==131){
					new NotificationPopup("Added");
					}
				}
				if(curEnzyme.useEnzyme) {
					curSite = curEnzyme.site;

					for(j = 0; curSite.length()+j <= dnaSeq.length(); j++) {
						
						// We found a restriction site, add it the current DNA sequence
						// to the list of found sequences for the current restriction
						// enzyme
						if(sequenceCompare(dnaSeq.substring(j), curSite)) {
							String foundSeq = reconcileSequences(curSite, beforeSeq.length()+j);
							curEnzyme.sitesFound.add(new SiteFoundEntry(
								foundSeq, j+beforeSeq.length(),
								countNumberOfDNAChanges(foundSeq)));
						}
					}
				}
			}
		}

		String reconcileSequences(String site, int siteIndex) {
			StringBuffer result = new StringBuffer();
			int i, j;
			char ch;

			// First, reconcile with mutant sequence
			for(i = 0; i < mutantAminoSeq.length(); i++)
				result.append(reconcileCodons(wildTypeDNASeq.substring(i*3, i*3+3),
											  mutantAminoSeq.charAt(i)));

			// Then merge in the restriction site
			for(i = 0; i < site.length(); i++) {
				ch = site.charAt(i);
				if(ch == 'A' || ch == 'C' || ch == 'G' || ch == 'T')
					result.setCharAt(siteIndex+i, ch);
			}

			return result.toString();
		}

		String reconcileCodons(String oldCodon, char newAA) {
			AminoCode code;
			char oldAA = Translator.translateCodon(oldCodon);
			int fewestChanges, curChange, fewestIndex;

			// If both the original and new (i.e. mutated) codon translate
			// to the same amino acid, we're done, simply use the original codon
			if(oldAA == newAA)
				return oldCodon;

			// Otherwise, we have to find the new codon that uses the
			// fewest changes from the old one
			code = (AminoCode)aminoTable.get(""+newAA);
			fewestChanges = 4;
			fewestIndex = 0;
			for(int i = 0; i < code.codeList.length; i++) {
				curChange = 0;
				if(oldCodon.charAt(0) != code.codeList[i].charAt(0))
					curChange++;
				if(oldCodon.charAt(1) != code.codeList[i].charAt(1))
					curChange++;
				if(oldCodon.charAt(2) != code.codeList[i].charAt(2) &&
				   code.codeList[i].charAt(2) != 'N')
					curChange++;
				if(curChange < fewestChanges) {
					fewestChanges = curChange;
					fewestIndex = i;
				}
			}

			// Return the best codon
			if(code.codeList[fewestIndex].charAt(2) == 'N') {
				StringBuffer temp = new StringBuffer(code.codeList[fewestIndex]);
				temp.setCharAt(2, oldCodon.charAt(2));
				return temp.toString();
			}
			else
				return code.codeList[fewestIndex];
		}

		int countNumberOfDNAChanges(String dnaSeq) {
			int changes = 0;

			if(dnaSeq.length() != wildTypeDNASeq.length()) {
				System.err.println("Different lengths\ndnaSeq = "+dnaSeq+
					"\nWT = "+wildTypeDNASeq);
				return 0;
			}
			for(int i = 0; i < wildTypeDNASeq.length(); i++)
				if(wildTypeDNASeq.charAt(i) != dnaSeq.charAt(i))
					changes++;

			return changes;
		}


		private final Map<Character, List<Character>> IUPAC_MAP = createIUPACMap(); // Assign here
		Map<Character, List<Character>> createIUPACMap() {
			Map<Character, List<Character>> map = new HashMap<>();
			// Use Arrays.asList(Character.valueOf(char)) for single-character lists
			map.put('A', Arrays.asList(Character.valueOf('A')));
			map.put('T', Arrays.asList(Character.valueOf('T')));
			map.put('C', Arrays.asList(Character.valueOf('C')));
			map.put('G', Arrays.asList(Character.valueOf('G')));
		
			// Use Arrays.asList() for multi-character lists (works in all Java versions)
			map.put('R', Arrays.asList(Character.valueOf('A'), Character.valueOf('G'))); // Purine
			map.put('Y', Arrays.asList(Character.valueOf('C'), Character.valueOf('T'))); // Pyrimidine
			map.put('S', Arrays.asList(Character.valueOf('G'), Character.valueOf('C'))); // Strong
			map.put('W', Arrays.asList(Character.valueOf('A'), Character.valueOf('T'))); // Weak
			map.put('K', Arrays.asList(Character.valueOf('G'), Character.valueOf('T'))); // Keto
			map.put('M', Arrays.asList(Character.valueOf('A'), Character.valueOf('C'))); // Amino
			map.put('B', Arrays.asList(Character.valueOf('C'), Character.valueOf('G'), Character.valueOf('T'))); // Not A
			map.put('D', Arrays.asList(Character.valueOf('A'), Character.valueOf('G'), Character.valueOf('T'))); // Not C
			map.put('H', Arrays.asList(Character.valueOf('A'), Character.valueOf('C'), Character.valueOf('T'))); // Not G
			map.put('V', Arrays.asList(Character.valueOf('A'), Character.valueOf('C'), Character.valueOf('G'))); // Not T
			map.put('N', Arrays.asList(Character.valueOf('A'), Character.valueOf('C'), Character.valueOf('G'), Character.valueOf('T'))); // Any base
			return Collections.unmodifiableMap(map); // Make map immutable
		}
		public Map<Character, List<Character>> getIUPACMap() {
			return IUPAC_MAP;
		}
		

		// sequenceCompare
		//
		//Modified
		//Compares two sequences (in a weird way, I kept it like this to not remake implementations)
		//Recognizes all IUPAC characters now using a hashmap of IUPAC characters
		//
		boolean sequenceCompare(String seq1 /*DNA*/, String seq2 /*Restr. enz  recognition seq*/) {
			int length = seq1.length();
			if(seq2.length() < length)
				length = seq2.length(); 
				for(int ii = 0; ii<length; ii++){
					char iupac_nt = seq2.charAt(ii);
					boolean match = false;
					for (char nt : getIUPACMap().get(iupac_nt)) {
						if(seq1.charAt(ii) == 'N'|| seq1.charAt(ii) == nt){ //because how codons are stored need to consider N, legacy
							//if character matches, proceed
							match = true;
							break;
						}
					}
					if(match == false){// if not skip to next DNA nt
						return false;
					}
				}
			return true;
		}

		// Find the midpoint of all the point mutations
		// (i.e. add up the indexes of all the point mutations and
		// divide by the of mutations.)  Returned index is in terms of
		// the DNA index (at the centerpoint of an amino acid)
		//
		// Returns -1 if there are no mutations
		int calcMutationMidpointIndex() {
			String wildTypeAminoSeq = Translator.fromDNAtoAA(wildTypeDNASeq);
			int sum = 0, numberOfMutations = 0;

			for(int i = 0; i < wildTypeAminoSeq.length(); i++) {
				if(wildTypeAminoSeq.charAt(i) != mutantAminoSeq.charAt(i)) {
					sum += i*3+1;
					numberOfMutations++;
				}
			}

			if(numberOfMutations > 0)
				return sum / numberOfMutations;
			else
				return -1;
		}

		// Due to the searching method, there's usually mulitple copies of the same
		// restriction site.  This function will search for and remove those extra copies
		void removeRedundantSites(Vector v) {
			SiteFoundEntry curEntry, nextEntry;

			for(int i = 0; i < v.size(); i++) {
				curEntry = (SiteFoundEntry)v.elementAt(i);
				for(int j = v.size()-1; j > i; j--) {
					nextEntry = (SiteFoundEntry)v.elementAt(j);
					if(curEntry.dnaSeq.equals(nextEntry.dnaSeq))
						v.remove(j);
				}
			}
		}

		public String getResults() {
			String curName;
			StringBuffer results = new StringBuffer("");
			RestrictionEnzyme curEnzyme;
			SiteFoundEntry curEntry;
			Object nameList[] = enzymeList.keySet().toArray();
			int numChanges, lowestIndex, lowestDist, curDist, i, j, k, disabledCounter = 0,
			    midpointIndex = calcMutationMidpointIndex();
			boolean anySiteFound = false;

			for(i = 0; i < nameList.length; i++) {
				curName = (String)nameList[i];
				curEnzyme = (RestrictionEnzyme)enzymeList.get(curName);

				if(curEnzyme.useEnzyme) {

					if(curEnzyme.sitesFound.size() > 0) {
						anySiteFound = true;

						// Search for the best restriction site and display that one only
						if(showBestResultOnly) {
							if(midpointIndex != -1) {
								// Find the sequence with the restriction site closest to the
								// midpoint index
								lowestIndex = -1;
								lowestDist = -1;
								for(j = 0; j < curEnzyme.sitesFound.size(); j++) {
									curEntry = (SiteFoundEntry)curEnzyme.sitesFound.elementAt(j);

									// Restriction site is before midpoint
									if(curEntry.siteStartIndex < midpointIndex)
									    curDist = midpointIndex -
									       (curEntry.siteStartIndex+curEnzyme.site.length()/2);

									// Restriction is after midpoint
									else
										curDist = curEntry.siteStartIndex+(curEnzyme.site.length()+1)/2-
										   midpointIndex;

									if(lowestDist == -1 || curDist < lowestDist) {
										lowestDist = curDist;
										lowestIndex = j;
									}
								}
							}
							else {
								// Find fewest number of changes for this restriction enzyme
								numChanges = -1;
								lowestIndex = -1;
								for(j = 0; j < curEnzyme.sitesFound.size(); j++) {
									curEntry = (SiteFoundEntry)curEnzyme.sitesFound.elementAt(j);
									if(curEntry.numChanges < numChanges || numChanges == -1) {
										numChanges = curEntry.numChanges;
										lowestIndex = j;
									}
								}
							}

							// Add the results for this enzyme
							if(lowestIndex != -1) {
								curEntry = (SiteFoundEntry)curEnzyme.sitesFound.elementAt(lowestIndex);
								results.append("WT    " + wildTypeDNASeq + "\n      ");
								for(k = 0; k < wildTypeDNASeq.length(); k++) {
									if(wildTypeDNASeq.charAt(k) == curEntry.dnaSeq.charAt(k))
										results.append(" ");
									else
										results.append("|");
								}
								results.append("\nMUT   " + curEntry.dnaSeq);
								for(k = 0; k < curEntry.siteStartIndex+6; k++)
									results.append(" ");
								results.append("\n");
								for(k = 0; k < curEntry.siteStartIndex+6; k++)
									results.append(" ");
								for(k = 0; k < curEnzyme.site.length(); k++)
									results.append("^");
								results.append("\n");
								for(k = 0; k < curEntry.siteStartIndex+6; k++)
									results.append(" ");
								results.append(curName + "\n\n\n");
							}
						}

						// Show every location each restriction site is found
						else {
							removeRedundantSites(curEnzyme.sitesFound);
							results.append(curName+":\n\n");
							for(j = 0; j < curEnzyme.sitesFound.size(); j++) {
								curEntry = (SiteFoundEntry)curEnzyme.sitesFound.elementAt(j);
								results.append("   WT    " + wildTypeDNASeq + "\n         ");
								for(k = 0; k < wildTypeDNASeq.length(); k++) {
									if(wildTypeDNASeq.charAt(k) == curEntry.dnaSeq.charAt(k))
										results.append(" ");
									else
										results.append("|");
								}
								results.append("\n   MUT   " + curEntry.dnaSeq);
								for(k = 0; k < curEntry.siteStartIndex+6; k++)
									results.append(" ");
								results.append("\n   ");
								for(k = 0; k < curEntry.siteStartIndex+6; k++)
									results.append(" ");
								for(k = 0; k < curEnzyme.site.length(); k++)
									results.append("^");
								results.append("\n   ");
								for(k = 0; k < curEntry.siteStartIndex+6; k++)
									results.append(" ");
								results.append(curName + "\n\n\n");
							}
						}
					}
				}
				else
					disabledCounter++;
			}

			if(anySiteFound == false)
				return "No Sites Found!\n\n";

			// Add a list of sites that weren't searched for since they were
			// disabled
			if(disabledCounter > 0) {
				String temp = "The following sites were excluded from the search" +
				              "\nsince they cut the wild-type sequence:\n\n";
				for(i = 0; i < nameList.length; i++) {
					curName = (String)nameList[i];
					curEnzyme = (RestrictionEnzyme)enzymeList.get(curName);

					if(curEnzyme.useEnzyme == false)
						temp += curName + "\n";
				}
				return temp + "\n\n" + results;
			}
			else
				return results.toString();
		}
	}
}
