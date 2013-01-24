package org.geometerplus.android.fbreader;

import org.geometerplus.fbreader.fbreader.FBReader;
import org.geometerplus.zlibrary.core.options.ZLIntegerRangeOption;
import org.geometerplus.zlibrary.text.view.ZLTextElement;
import org.geometerplus.zlibrary.text.view.ZLTextView;
import org.geometerplus.zlibrary.text.view.ZLTextWord;
import org.geometerplus.zlibrary.text.view.ZLTextWordCursor;
import org.geometerplus.zlibrary.text.view.style.ZLTextStyleCollection;
import org.geometerplus.zlibrary.ui.android.R;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class TestActivity extends Activity {

	ZLTextWordCursor endCursor;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.text);
		
		ZLTextWordCursor startCursor = ((FBReader)FBReader.Instance()).getTextView().getStartCursor();
		
//		((FBReader)FBReader.Instance()).
		endCursor = ((FBReader)FBReader.Instance()).getTextView().getEndCursor();
		String textPage = ZLTextView.TEXT;
		ZLIntegerRangeOption option =
			ZLTextStyleCollection.Instance().getBaseStyle().FontSizeOption;
	
		TextView textView = (TextView) findViewById(R.id.TextView);
		textView.setText(textPage);
		textView.setTextSize(option.getValue() );
	}
	
public String getTextPage(ZLTextWordCursor startCursor,ZLTextWordCursor endCursor){
		
		int startCharIndex = startCursor.getElementIndex();
		int endCharIndex = endCursor.getElementIndex();
		StringBuilder builder = new StringBuilder();
		while(startCursor.isEndOfParagraph()){
			Log.i("WhileLoop","testingggg");
			final ZLTextElement element = startCursor.getElement();
			if(element instanceof ZLTextWord){
			final ZLTextWord word = (ZLTextWord)element ;
			builder.append(word.Data, word.Offset, word.Length);
			
			}
			
			startCursor.nextWord();
		}
		Log.i("BUILDER",builder.toString());
		return builder.toString();
	}

private String createBookmarkText(ZLTextWordCursor cursor) {
	
	cursor = new ZLTextWordCursor(cursor);

	final StringBuilder builder = new StringBuilder();
	final StringBuilder sentenceBuilder = new StringBuilder();
	final StringBuilder phraseBuilder = new StringBuilder();
	int curElementIndex = cursor.getElementIndex();
	int wordCounter = 0;
	int sentenceCounter = 0;
	int storedWordCounter = 0;
	boolean lineIsNonEmpty = false;
	boolean appendLineBreak = false;
	
mainLoop:
	while ( ( cursor.getParagraphIndex()!= endCursor.getParagraphIndex())  
			|| ( cursor.getElementIndex()!= endCursor.getElementIndex())
			|| cursor.isEndOfParagraph()) {
		
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
	
	if (appendLineBreak) {
		builder.append("\n");
		appendLineBreak = false;
	}
	
	sentenceBuilder.append(phraseBuilder);
	phraseBuilder.delete(0, phraseBuilder.length());
	builder.append(sentenceBuilder);
	sentenceBuilder.delete(0, sentenceBuilder.length());
	storedWordCounter = wordCounter;
	
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
