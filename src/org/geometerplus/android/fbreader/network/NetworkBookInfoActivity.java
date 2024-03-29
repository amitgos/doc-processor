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

package org.geometerplus.android.fbreader.network;

import java.util.Set;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;
import android.net.Uri;
import android.content.Intent;
import android.graphics.Bitmap;

import org.geometerplus.zlibrary.ui.android.R;

import org.geometerplus.zlibrary.core.resources.ZLResource;
import org.geometerplus.zlibrary.core.image.ZLImage;

import org.geometerplus.zlibrary.ui.android.image.ZLAndroidImageManager;
import org.geometerplus.zlibrary.ui.android.image.ZLAndroidImageData;

import org.geometerplus.fbreader.network.*;


public class NetworkBookInfoActivity extends Activity implements NetworkLibraryActivity.EventListener {

	private NetworkBookItem myBook;

	private final ZLResource myResource = ZLResource.resource("networkBookView");

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (NetworkLibraryActivity.Instance == null) {
			finish();
			return;
		}

		myBook = NetworkLibraryActivity.Instance.getBookItem();
		if (myBook == null) {
			finish();
			return;
		}

		setTitle(myBook.Title);
		setContentView(R.layout.network_book);

		setupDescription();
		setupInfo();
		setupCover();
		setupButtons();
	}

	private final void setupDescription() {
		((TextView) findViewById(R.id.network_book_description_title)).setText(myResource.getResource("description").getValue());

		final TextView descriptionView = (TextView) findViewById(R.id.network_book_description);
		final String description;
		if (myBook.Summary != null) {
			description = myBook.Summary;
		} else {
			description = myResource.getResource("noDescription").getValue();
		}
		descriptionView.setText(description);
	}

	private void setupInfo() {
		((TextView) findViewById(R.id.network_book_info_title)).setText(myResource.getResource("bookInfo").getValue());

		((TextView) findViewById(R.id.network_book_title_key)).setText(myResource.getResource("title").getValue());
		((TextView) findViewById(R.id.network_book_authors_key)).setText(myResource.getResource("authors").getValue());
		((TextView) findViewById(R.id.network_book_series_key)).setText(myResource.getResource("series").getValue());
		((TextView) findViewById(R.id.network_book_series_index_key)).setText(myResource.getResource("indexInSeries").getValue());
		((TextView) findViewById(R.id.network_book_tags_key)).setText(myResource.getResource("tags").getValue());


		((TextView) findViewById(R.id.network_book_title_value)).setText(myBook.Title);

		if (myBook.Authors.size() > 0) {
			findViewById(R.id.network_book_authors).setVisibility(View.VISIBLE);
			final StringBuilder authorsText = new StringBuilder();
			for (NetworkBookItem.AuthorData author: myBook.Authors) {
				if (authorsText.length() > 0) {
					authorsText.append(", ");
				}
				authorsText.append(author.DisplayName);
			}
			((TextView) findViewById(R.id.network_book_authors_value)).setText(authorsText);
		} else {
			findViewById(R.id.network_book_authors).setVisibility(View.GONE);
		}

		if (myBook.SeriesTitle != null) {
			findViewById(R.id.network_book_series).setVisibility(View.VISIBLE);
			((TextView) findViewById(R.id.network_book_series_value)).setText(myBook.SeriesTitle);
			if (myBook.IndexInSeries > 0) {
				((TextView) findViewById(R.id.network_book_series_index_value)).setText(String.valueOf(myBook.IndexInSeries));
				findViewById(R.id.network_book_series_index).setVisibility(View.VISIBLE);
			} else {
				findViewById(R.id.network_book_series_index).setVisibility(View.GONE);
			}
		} else {
			findViewById(R.id.network_book_series).setVisibility(View.GONE);
			findViewById(R.id.network_book_series_index).setVisibility(View.GONE);
		}

		if (myBook.Tags.size() > 0) {
			findViewById(R.id.network_book_tags).setVisibility(View.VISIBLE);
			final StringBuilder tagsText = new StringBuilder();
			for (String tag: myBook.Tags) {
				if (tagsText.length() > 0) {
					tagsText.append(", ");
				}
				tagsText.append(tag);
			}
			((TextView) findViewById(R.id.network_book_tags_value)).setText(tagsText);
		} else {
			findViewById(R.id.network_book_tags).setVisibility(View.GONE);
		}
	}

	private final void setupCover() {
		final View rootView = findViewById(R.id.network_book_root);
		final ImageView coverView = (ImageView) findViewById(R.id.network_book_cover);

		final int maxHeight = 300; // FIXME: hardcoded constant
		final int maxWidth = maxHeight * 2 / 3;
		Bitmap coverBitmap = null;
		final ZLImage cover = NetworkTree.createCover(myBook);
		if (cover != null) {
			ZLAndroidImageData data = null;
			final ZLAndroidImageManager mgr = (ZLAndroidImageManager) ZLAndroidImageManager.Instance();
			if (cover instanceof NetworkImage) {
				final NetworkImage img = (NetworkImage) cover;
				if (NetworkLibraryActivity.Instance != null
						&& NetworkLibraryActivity.Instance.isCoverLoading(img.Url)) {
					NetworkLibraryActivity.Instance.setOnCoverSyncRunnable(img.Url, new Runnable() {
						public void run() {
							img.synchronizeFast();
							final ZLAndroidImageData data = mgr.getImageData(img);
							if (data != null) {
								final Bitmap coverBitmap = data.getBitmap(maxWidth, maxHeight);
								if (coverBitmap != null) {
									coverView.setImageBitmap(coverBitmap);
									coverView.setVisibility(View.VISIBLE);
									rootView.invalidate();
									rootView.requestLayout();
								}
							}
						}
					});
				} else {
					img.synchronizeFast();
					data = mgr.getImageData(img);
				}
			} else {
				data = mgr.getImageData(cover);
			}
			if (data != null) {
				coverBitmap = data.getBitmap(maxWidth, maxHeight);
			}
		}
		if (coverBitmap != null) {
			coverView.setImageBitmap(coverBitmap);
			coverView.setVisibility(View.VISIBLE);
		} else {
			coverView.setVisibility(View.GONE);
		}
	}

	private final void setupButtons() {
		final ZLResource resource = ZLResource.resource("networkView");
		final int buttons[] = new int[] {
				R.id.network_book_button0,
				R.id.network_book_button1,
				R.id.network_book_button2,
				R.id.network_book_button3,
		};
		final Set<NetworkBookActions.Action> actions = NetworkBookActions.getContextMenuActions(myBook);
// debug code:
/*if (actions.size() == 2) {
	actions.clear();
}*/
/*actions.add(new NetworkBookActions.Action(NetworkBookActions.TREE_NO_ACTION, "buy", "0z"));
actions.add(new NetworkBookActions.Action(NetworkBookActions.TREE_NO_ACTION, "buy", "1z"));
actions.add(new NetworkBookActions.Action(NetworkBookActions.TREE_NO_ACTION, "buy", "2z"));*/
		final boolean skipSecondButton = actions.size() < buttons.length && (actions.size() % 2) == 1;
		int buttonNumber = 0;
		for (final NetworkBookActions.Action a: actions) {
			if (skipSecondButton && buttonNumber == 1) {
				++buttonNumber;
			}
			if (buttonNumber >= buttons.length) {
				break;
			}

			final String text;
			if (a.Arg == null) {
				text = resource.getResource(a.Key).getValue();
			} else {
				text = resource.getResource(a.Key).getValue().replace("%s", a.Arg);
			}

			final int buttonId = buttons[buttonNumber++];
			Button button = (Button) findViewById(buttonId);
			button.setText(text);
			button.setVisibility(View.VISIBLE);
			button.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					NetworkBookActions.runAction(myBook, a.Id);
					NetworkBookInfoActivity.this.updateView();
				}
			});
			button.setEnabled(a.Id != NetworkTreeActions.TREE_NO_ACTION);
		}
		findViewById(R.id.network_book_left_spacer).setVisibility(skipSecondButton ? View.VISIBLE : View.GONE);
		findViewById(R.id.network_book_right_spacer).setVisibility(skipSecondButton ? View.VISIBLE : View.GONE);
		if (skipSecondButton) {
			final int buttonId = buttons[1];
			View button = findViewById(buttonId);
			button.setVisibility(View.GONE);
			button.setOnClickListener(null);
		}
		while (buttonNumber < buttons.length) {
			final int buttonId = buttons[buttonNumber++];
			View button = findViewById(buttonId);
			button.setVisibility(View.GONE);
			button.setOnClickListener(null);
		}
	}

	private void updateView() {
		setupButtons();
		final View rootView = findViewById(R.id.network_book_root);
		rootView.invalidate();
		rootView.requestLayout();
	}

	protected void onStart() {
		super.onStart();
		if (NetworkLibraryActivity.Instance != null) {
			NetworkLibraryActivity.Instance.setTopLevelActivity(this);
			NetworkLibraryActivity.Instance.addEventListener(this);
		}
	}

	protected void onStop() {
		if (NetworkLibraryActivity.Instance != null) {
			NetworkLibraryActivity.Instance.setTopLevelActivity(null);
			NetworkLibraryActivity.Instance.removeEventListener(this);
		}
		super.onStop();
	}

	public void onModelChanged() {
		updateView();
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		switch (id) {
		case NetworkLibraryActivity.DIALOG_AUTHENTICATION:
			dialog = AuthenticationDialog.Instance().createDialog(this);
			break;
		}
		return dialog;
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		super.onPrepareDialog(id, dialog);
		switch (id) {
		case NetworkLibraryActivity.DIALOG_AUTHENTICATION:
			AuthenticationDialog.Instance().prepareDialog(dialog);
			break;
		}		
	}
}
