/*
 * Copyright (C) 2009-2010 Geometer Plus <contact@geometerplus.com>
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

package org.geometerplus.android.fbreader.preferences;

import java.util.Map;
import java.util.TreeMap;

import android.content.Context;
import android.util.Log;
import android.view.WindowManager;

import org.geometerplus.zlibrary.core.resources.ZLResource;
import org.geometerplus.zlibrary.core.language.ZLLanguageList;

import org.geometerplus.zlibrary.text.hyphenation.ZLTextHyphenator;
import org.geometerplus.zlibrary.text.model.ZLTextParagraph;
import org.geometerplus.zlibrary.text.view.ZLTextElement;
import org.geometerplus.zlibrary.text.view.ZLTextWord;
import org.geometerplus.zlibrary.text.view.ZLTextWordCursor;
import org.geometerplus.zlibrary.ui.android.R.string;

import org.geometerplus.fbreader.fbreader.FBReader;
import org.geometerplus.fbreader.library.Book;

class BookTitlePreference extends ZLStringPreference {
	private final Book myBook;

	BookTitlePreference(Context context, ZLResource rootResource, String resourceKey, Book book) {
		super(context, rootResource, resourceKey);
		myBook = book;
		setValue(book.getTitle());
	}

	public void onAccept() {
		myBook.setTitle(getValue());
	}
}

class LanguagePreference extends ZLStringListPreference {
	private final Book myBook;

	LanguagePreference(Context context, ZLResource rootResource, String resourceKey, Book book) {
		super(context, rootResource, resourceKey);
		myBook = book;
		final TreeMap<String,String> map = new TreeMap<String,String>();
		for (String code : ZLLanguageList.languageCodes()) {
			map.put(ZLLanguageList.languageName(code), code);
		}
		final int size = map.size();
		String[] codes = new String[size + 1];
		String[] names = new String[size + 1];
		int index = 0;
		for (Map.Entry<String,String> entry : map.entrySet()) {
			codes[index] = entry.getValue();
			names[index] = entry.getKey();
			++index;
		}
		codes[size] = "other";
		names[size] = ZLLanguageList.languageName(codes[size]);
		setLists(codes, names);
		String language = myBook.getLanguage();
		if (language == null) {
			language = "other";
		}
		if (!setInitialValue(language)) {
			setInitialValue("other");
		}
	}

	public void onAccept() {
		final String value = getValue();
		myBook.setLanguage((value.length() != 0) ? value : null);
	}
}
	

public class BookInfoActivity extends ZLPreferenceActivity {
	
	private Book myBook;

	public BookInfoActivity() {
		super("BookInfo");
	}

	@Override
	protected void init() {
		
//		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setTitle("Book Info");
		
		final Category commonCategory = createCategory(null);
		myBook = ((FBReader)FBReader.Instance()).Model.Book;
		Log.i("TEXT DATA", "STARRTED");
		ZLTextWordCursor startCursor = ((FBReader)FBReader.Instance()).getTextView().getStartCursor();
		ZLTextWordCursor endCursor = ((FBReader)FBReader.Instance()).getTextView().getEndCursor();
		startCursor.getElementIndex();
		Log.i("Text Data", createBookmarkText(startCursor));
		Log.i("Text Data", getTextPage(startCursor, endCursor));
		
		
//		for (ZLTextParagraph.EntryIterator it = tp.iterator(); it.hasNext(); ) {
//			it.next();
//			
//			Log.i("TEXT DATA", "STARRTED");
//			if(it.getType()== ZLTextParagraph.Entry.TEXT){
//			Log.i("TEXT DATA", String.valueOf(it.getTextData()));
//			}
//		}
	
		if (myBook.File.getPhysicalFile() != null) {
			commonCategory.addPreference(new InfoPreference(
				this,
				commonCategory.Resource.getResource("fileName").getValue(),
				myBook.File.getPath())
			);
		}
		commonCategory.addPreference(new BookTitlePreference(this, commonCategory.Resource, "title", myBook));
		commonCategory.addPreference(new LanguagePreference(this, commonCategory.Resource, "language", myBook));
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (myBook.save()) {
			((FBReader)FBReader.Instance()).clearTextCaches();
			ZLTextHyphenator.Instance().load(myBook.getLanguage());
		}
	}
	
	public String getTextPage(ZLTextWordCursor startCursor,ZLTextWordCursor endCursor){
		
		int startCharIndex = startCursor.getElementIndex();
		int endCharIndex = endCursor.getElementIndex();
		StringBuilder builder = new StringBuilder();
		while(startCursor.getElementIndex()< endCharIndex){
			Log.i("WhileLoop","testingggg");
			final ZLTextElement element = startCursor.getElement();
			if(element instanceof ZLTextWord){
			final ZLTextWord word = (ZLTextWord)element ;
			builder.append(word.Data, word.Offset, word.Length);
			Log.i("BUILDER",builder.toString());
			}
			
			startCursor.nextWord();
		}
		return builder.toString();
	}
	private String createBookmarkText(ZLTextWordCursor cursor) {
		cursor = new ZLTextWordCursor(cursor);

		final StringBuilder builder = new StringBuilder();
		final StringBuilder sentenceBuilder = new StringBuilder();
		final StringBuilder phraseBuilder = new StringBuilder();

		int wordCounter = 0;
		int sentenceCounter = 0;
		int storedWordCounter = 0;
		boolean lineIsNonEmpty = false;
		boolean appendLineBreak = false;
	mainLoop:
		while ((wordCounter < 20) && (sentenceCounter < 3)) {
			while (cursor.isEndOfParagraph()) {
				if (!cursor.nextParagraph()) {
					break mainLoop;
				}
				if ((builder.length() > 0) && cursor.getParagraphCursor().isEndOfSection()) {
					break mainLoop;
				}
				if (phraseBuilder.length() > 0) {
					sentenceBuilder.append(phraseBuilder);
					phraseBuilder.delete(0, phraseBuilder.length());
				}
				if (sentenceBuilder.length() > 0) {
					if (appendLineBreak) {
						builder.append("\n");
					}
					builder.append(sentenceBuilder);
					sentenceBuilder.delete(0, sentenceBuilder.length());
					++sentenceCounter;
					storedWordCounter = wordCounter;
				}
				lineIsNonEmpty = false;
				if (builder.length() > 0) {
					appendLineBreak = true;
				}
			}
			final ZLTextElement element = cursor.getElement();
			if (element instanceof ZLTextWord) {
				final ZLTextWord word = (ZLTextWord)element;
				if (lineIsNonEmpty) {
					phraseBuilder.append(" ");
				}
				phraseBuilder.append(word.Data, word.Offset, word.Length);
				++wordCounter;
				lineIsNonEmpty = true;
				switch (word.Data[word.Offset + word.Length - 1]) {
					case ',':
					case ':':
					case ';':
					case ')':
						sentenceBuilder.append(phraseBuilder);
						phraseBuilder.delete(0, phraseBuilder.length());
						break;
					case '.':
					case '!':
					case '?':
						++sentenceCounter;
						if (appendLineBreak) {
							builder.append("\n");
							appendLineBreak = false;
						}
						sentenceBuilder.append(phraseBuilder);
						phraseBuilder.delete(0, phraseBuilder.length());
						builder.append(sentenceBuilder);
						sentenceBuilder.delete(0, sentenceBuilder.length());
						storedWordCounter = wordCounter;
						break;
				}
			}
			cursor.nextWord();
		}
		if (storedWordCounter < 4) {
			if (sentenceBuilder.length() == 0) {
				sentenceBuilder.append(phraseBuilder);
			}
			if (appendLineBreak) {
				builder.append("\n");
			}
			builder.append(sentenceBuilder);
		}
		return builder.toString();
	}

	
}
