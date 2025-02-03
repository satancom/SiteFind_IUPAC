/*****************************************************************************\
*   RestrictionEnzyme.java                                                    *
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

import java.util.*;

public class RestrictionEnzyme {
	public String name, site;
	public Vector sitesFound;
	public boolean useEnzyme;

	public RestrictionEnzyme(String n, String s) {
		name = n;
		site = s;
		sitesFound = new Vector();
		useEnzyme = true;
	}

	public RestrictionEnzyme(RestrictionEnzyme r) {
		name = r.name;
		site = r.site;
		sitesFound = r.sitesFound;
		useEnzyme = r.useEnzyme;
	}
}
