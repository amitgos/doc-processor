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

package org.geometerplus.fbreader.formats.xhtml;

import org.geometerplus.zlibrary.core.xml.ZLStringMap;
import org.geometerplus.zlibrary.core.image.ZLFileImage;
import org.geometerplus.zlibrary.core.filesystem.ZLFile;

import org.geometerplus.fbreader.bookmodel.BookReader;

class XHTMLTagImageAction extends XHTMLTagAction {
	private final String myNameAttribute;

	XHTMLTagImageAction(String nameAttribute) {
		myNameAttribute = nameAttribute;
	}

	protected void doAtStart(XHTMLReader reader, ZLStringMap xmlattributes) {
		String fileName = xmlattributes.getValue(myNameAttribute);
		if (fileName != null) {
			if (fileName.startsWith("./")) {
				fileName = fileName.substring(2);
			}
			final ZLFile imageFile = ZLFile.createFileByPath(reader.myPathPrefix + fileName);
			if (imageFile != null) {
				final BookReader modelReader = reader.getModelReader();
				boolean flag = modelReader.paragraphIsOpen() && !modelReader.paragraphIsNonEmpty();
				if (flag) {
					modelReader.endParagraph();
				}
				final String imageName = imageFile.getName(false);
				modelReader.addImageReference(imageName, (short)0);
				modelReader.addImage(imageName, new ZLFileImage("image/auto", imageFile));
				if (flag) {
					modelReader.beginParagraph();
				}
			}
		}
	}

	protected void doAtEnd(XHTMLReader reader) {
	}
}
