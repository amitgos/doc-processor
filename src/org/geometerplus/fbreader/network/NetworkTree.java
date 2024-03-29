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

package org.geometerplus.fbreader.network;

import org.geometerplus.fbreader.tree.FBTree;

import org.geometerplus.zlibrary.core.image.ZLImage;

import android.util.Log;

public abstract class NetworkTree extends FBTree {

	protected NetworkTree() {
		super();
	}

	protected NetworkTree(NetworkTree parent) {
		super(parent);
	}

	protected NetworkTree(NetworkTree parent, int position) {
		super(parent, position);
	}

	public static ZLImage createCover(NetworkLibraryItem item) {
		if (item.Cover == null) {
			return null;
		}
		return createCover(item.Cover, null);
	}

	public static ZLImage createCover(String url, String mimeType) {
		
		Log.i("Url",url);
		
		if (url == null) {
			return null;
		}
		if (mimeType == null) {
			mimeType = "image/auto";
		}
		if (url.startsWith("http://") || url.startsWith("https://") || url.startsWith("ftp://")) {
			return new NetworkImage(url, mimeType);
		} else if (url.startsWith("data:")) {
			int commaIndex = url.indexOf(',');
			if (commaIndex == -1) {
				return null;
			}
			if (mimeType == "image/auto") {
				int index = url.indexOf(';');
				if (index == -1 || index > commaIndex) {
					index = commaIndex;
				}
				if (url.startsWith("image/", 5)) { // 11 -- length of "data:image/"; 5 -- length of "data:"
					mimeType = url.substring(5, index);
				}
			}
			int key = url.indexOf("base64");
			if (key != -1 && key < commaIndex) {
				Base64EncodedImage img = new Base64EncodedImage(mimeType);
				img.setData(url.substring(commaIndex + 1));
				return img;
			}
		}
		return null;
	}
}
