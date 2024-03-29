/*
 * Copyright (C) 2010 Geometer Plus <contact@geometerplus.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 */

package org.geometerplus.fbreader.network.tree;

import java.util.*;

import org.geometerplus.fbreader.tree.FBTree;
import org.geometerplus.fbreader.network.*;


public class NetworkSeriesTree extends NetworkTree {

	public final String SeriesTitle;

	private final boolean myShowAuthors;

	NetworkSeriesTree(NetworkTree parent, String seriesTitle, boolean showAuthors) {
		super(parent);
		SeriesTitle = seriesTitle;
		myShowAuthors = showAuthors;
	}

	@Override
	public String getName() {
		return SeriesTitle;
	}

	@Override
	public String getSummary() {
		if (!myShowAuthors) {
			return super.getSecondString();
		}

		StringBuilder builder = new StringBuilder();
		int count = 0;

		Set<NetworkBookItem.AuthorData> authorSet = new TreeSet<NetworkBookItem.AuthorData>();
		for (FBTree tree: this) {
			if (!(tree instanceof NetworkBookTree)) {
				continue;
			}
			final NetworkBookItem book = (NetworkBookItem) ((NetworkBookTree)tree).Book;

			for (NetworkBookItem.AuthorData author: book.Authors) {
				if (!authorSet.contains(author)) {
					authorSet.add(author);
					if (count++ > 0) {
						builder.append(",  ");
					}
					builder.append(author.DisplayName);
					if (count == 5) {
						return builder.toString();
					}
				}
			}
		}
		return builder.toString();
	}

}
