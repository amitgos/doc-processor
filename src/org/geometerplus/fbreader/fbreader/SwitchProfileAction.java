/*
 * Copyright (C) 2007-2010 Geometer Plus <contact@geometerplus.com>
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

package org.geometerplus.fbreader.fbreader;

import org.geometerplus.zlibrary.core.dialogs.ZLDialogManager;

class SwitchProfileAction extends FBAction {
	private String myProfileName;

	SwitchProfileAction(FBReader fbreader, String profileName) {
		super(fbreader);
		myProfileName = profileName;
	}

	public boolean isVisible() {
		return !myProfileName.equals(Reader.getColorProfileName());
	}

	
	public void run() {
		Reader.setColorProfileName(myProfileName);
		Reader.repaintView();
	}
}
