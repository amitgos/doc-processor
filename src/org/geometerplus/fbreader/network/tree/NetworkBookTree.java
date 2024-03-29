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

import org.geometerplus.zlibrary.core.image.ZLImage;

import org.geometerplus.fbreader.tree.FBTree;
import org.geometerplus.fbreader.network.*;


public class NetworkBookTree extends NetworkTree {

	public final NetworkBookItem Book;

	NetworkBookTree(NetworkTree parent, NetworkBookItem book, int position) {
		super(parent, position);
		Book = book;
	}

	@Override
	public String getName() {
		return Book.Title;
	}

	@Override
	public String getSummary() {
		StringBuilder builder = new StringBuilder();
		int count = 0;
		for (NetworkBookItem.AuthorData author: Book.Authors) {
			if (count++ > 0) {
				builder.append(",  ");
			}
			builder.append(author.DisplayName);
		}
		String authorsString = builder.toString();

		FBTree parent = this.Parent;
		if (parent.getName().equals(authorsString)) {
			return "";
		}
		return authorsString;
	}

	@Override
	protected ZLImage createCover() {
		return createCover(Book);
	}
}
