/*****************************************************************************\
*   Translator.java                                                           *
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

// Translator
//
// For internal use only!  These functions make no attempt at checking
// the integrity of the data passed to them, and will break if any
// invalid characters are passed to them or if the DNA sequences are
// not in multiple of threes
public class Translator {

	public static String fromDNAtoAA(String dnaSeq) {
		StringBuffer aa = new StringBuffer("");

		for(int i = 0; i < dnaSeq.length(); i += 3)
			aa.append(translateCodon(dnaSeq.substring(i, i+3)));

		return aa.toString();
	}

	public static char translateCodon(String codon) {
		char ch1 = codon.charAt(0),
		     ch2 = codon.charAt(1),
		     ch3 = codon.charAt(2);

		switch(ch1) {
			case 'T':
				switch(ch2) {
					case 'T':
						switch(ch3) {
							case 'T':
							case 'C': return 'F';
							case 'A':
							case 'G': return 'L';
						}
					case 'C': return 'S';
					case 'A':
						switch(ch3) {
							case 'T':
							case 'C': return 'Y';
							case 'A':
							case 'G': return '-';
						}
					case 'G':
						switch(ch3) {
							case 'T':
							case 'C': return 'C';
							case 'A': return '-';
							case 'G': return 'W';
						}
				}
			case 'C':
				switch(ch2) {
					case 'T': return 'L';
					case 'C': return 'P';
					case 'A':
						switch(ch3) {
							case 'T':
							case 'C': return 'H';
							case 'A':
							case 'G': return 'Q';
						}
					case 'G': return 'R';
				}
			case 'A':
				switch(ch2) {
					case 'T':
						switch(ch3) {
							case 'T':
							case 'C':
							case 'A': return 'I';
							case 'G': return 'M';
						}
					case 'C': return 'T';
					case 'A':
						switch(ch3) {
							case 'T':
							case 'C': return 'N';
							case 'A':
							case 'G': return 'K';
						}
					case 'G':
						switch(ch3) {
							case 'T':
							case 'C': return 'S';
							case 'A':
							case 'G': return 'R';
						}
				}
			case 'G':
				switch(ch2) {
					case 'T': return 'V';
					case 'C': return 'A';
					case 'A':
						switch(ch3) {
							case 'T':
							case 'C': return 'D';
							case 'A':
							case 'G': return 'E';
						}
					case 'G': return 'G';
				}
		}
		return '?';
	}

}