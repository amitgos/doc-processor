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

package org.geometerplus.fbreader.network.opds;

import java.util.*;

import org.geometerplus.zlibrary.core.xml.*;

import org.geometerplus.fbreader.constants.XMLNamespace;

import org.geometerplus.fbreader.network.atom.*;


class OPDSXMLReader extends ZLXMLReaderAdapter {

	public static final String KEY_PRICE = "price";
	public static final String KEY_CURRENCY = "currency";
	public static final String KEY_FORMAT = "format";


	private final OPDSFeedReader myFeedReader;

	private OPDSFeedMetadata myFeed;
	private OPDSEntry myEntry;

	private ATOMAuthor myAuthor;
	private ATOMId myId;
	private ATOMLink myLink;
	private ATOMCategory myCategory;
	private ATOMUpdated myUpdated;
	private ATOMPublished myPublished;
	private DCDate myDCIssued;

	//private ATOMTitle myTitle;      // TODO: implement ATOMTextConstruct & ATOMTitle
	//private ATOMSummary mySummary;  // TODO: implement ATOMTextConstruct & ATOMSummary


	public OPDSXMLReader(OPDSFeedReader feedReader) {
		myFeedReader = feedReader;
	}


	private String myDublinCoreNamespaceId;
	private String myAtomNamespaceId;
	private String myOpenSearchNamespaceId;
	private String myCalibreNamespaceId;
	private String myOpdsNamespaceId;

	@Override
	public boolean processNamespaces() {
		return true;
	}

	private static String intern(String str) {
		if (str == null || str.length() == 0) {
			return null;
		}
		return str.intern();
	}

	@Override
	public void namespaceListChangedHandler(HashMap<String,String> namespaces) {
		myDublinCoreNamespaceId = null;
		myAtomNamespaceId = null;
		myOpenSearchNamespaceId = null;
		myCalibreNamespaceId = null;
		myOpdsNamespaceId = null;

		for (String key: namespaces.keySet()) {
			if (key == XMLNamespace.DublinCoreTerms) {
				myDublinCoreNamespaceId = intern(namespaces.get(key));
			} else if (key == XMLNamespace.Atom) {
				myAtomNamespaceId = intern(namespaces.get(key));
			} else if (key == XMLNamespace.OpenSearch) {
				myOpenSearchNamespaceId = intern(namespaces.get(key));
			} else if (key == XMLNamespace.CalibreMetadata) {
				myCalibreNamespaceId = intern(namespaces.get(key));
			} else if (key == XMLNamespace.Opds) {
				myOpdsNamespaceId = intern(namespaces.get(key));
			}
		}
	}


	private static final int START = 0;
	private static final int FEED = 1;
	private static final int F_ENTRY = 2;
	private static final int F_ID = 3;
	private static final int F_LINK = 4;
	private static final int F_CATEGORY = 5;
	private static final int F_TITLE = 6;
	private static final int F_UPDATED = 7;
	private static final int F_AUTHOR = 8;
	private static final int FA_NAME = 9;
	private static final int FA_URI = 10;
	private static final int FA_EMAIL = 11;
	private static final int FE_AUTHOR = 12;
	private static final int FE_ID = 13;
	private static final int FE_CATEGORY = 14;
	private static final int FE_LINK = 15;
	private static final int FE_PUBLISHED = 16;
	private static final int FE_SUMMARY = 17;
	private static final int FE_CONTENT = 18;
	private static final int FE_TITLE = 19;
	private static final int FE_UPDATED = 20;
	private static final int FE_DC_LANGUAGE = 21;
	private static final int FE_DC_ISSUED = 22;
	private static final int FE_DC_PUBLISHER = 23;
	private static final int FE_CALIBRE_SERIES = 24;
	private static final int FE_CALIBRE_SERIES_INDEX = 25;
	private static final int FEL_PRICE = 26;
	private static final int FEL_FORMAT = 27;
	private static final int FEA_NAME = 28;
	private static final int FEA_URI = 29;
	private static final int FEA_EMAIL = 30;
	private static final int OPENSEARCH_TOTALRESULTS = 31;
	private static final int OPENSEARCH_ITEMSPERPAGE = 32;
	private static final int OPENSEARCH_STARTINDEX = 33;
	private static final int FEC_HACK_SPAN = 34;


	private static final String TAG_FEED = "feed";
	private static final String TAG_ENTRY = "entry";
	private static final String TAG_AUTHOR = "author";
	private static final String TAG_NAME = "name";
	private static final String TAG_URI = "uri";
	private static final String TAG_EMAIL = "email";
	private static final String TAG_ID = "id";
	private static final String TAG_CATEGORY = "category";
	private static final String TAG_LINK = "link";
	private static final String TAG_PUBLISHED = "published";
	private static final String TAG_SUMMARY = "summary";
	private static final String TAG_CONTENT = "content";
	private static final String TAG_TITLE = "title";
	private static final String TAG_UPDATED = "updated";
	private static final String TAG_PRICE = "price";

	private static final String TAG_HACK_SPAN = "span";

	private static final String DC_TAG_LANGUAGE = "language";
	private static final String DC_TAG_ISSUED = "issued";
	private static final String DC_TAG_PUBLISHER = "publisher";
	private static final String DC_TAG_FORMAT = "format";

	private static final String CALIBRE_TAG_SERIES = "series";
	private static final String CALIBRE_TAG_SERIES_INDEX = "series_index";

	private static final String OPENSEARCH_TAG_TOTALRESULTS = "totalResults";
	private static final String OPENSEARCH_TAG_ITEMSPERPAGE = "itemsPerPage";
	private static final String OPENSEARCH_TAG_STARTINDEX = "startIndex";


	private int myState = START;

	private final StringBuffer myBuffer = new StringBuffer();
	private HtmlToString myHtmlToString = new HtmlToString();


	@Override
	public boolean startElementHandler(String tag, ZLStringMap attributes) {
		final int index = tag.indexOf(':');
		final String tagPrefix;
		if (index != -1) {
			tagPrefix = tag.substring(0, index).intern();
			tag = tag.substring(index + 1).intern();
		} else {
			tagPrefix = null;
			tag = tag.intern();
		}

		final char[] bufferContentArray = myBuffer.toString().trim().toCharArray();
		final String bufferContent;
		if (bufferContentArray.length == 0) {
			bufferContent = null;
		} else {
			bufferContent = new String(bufferContentArray);
		}
		myBuffer.delete(0, myBuffer.length());

		switch (myState) {
			case START:
				if (tagPrefix == myAtomNamespaceId && tag == TAG_FEED) {
					myFeedReader.processFeedStart();
					myFeed = new OPDSFeedMetadata();
					myFeed.readAttributes(attributes);
					myState = FEED;
				}
				break;
			case FEED:
				if (tagPrefix == myAtomNamespaceId) {
					if (tag == TAG_AUTHOR) {
						myAuthor = new ATOMAuthor();
						myAuthor.readAttributes(attributes);
						myState = F_AUTHOR;
					} else if (tag == TAG_ID) {
						myId = new ATOMId();
						myId.readAttributes(attributes);
						myState = F_ID;
					} else if (tag == TAG_LINK) {
						myLink = new ATOMLink();
						myLink.readAttributes(attributes);
						myState = F_LINK;
					} else if (tag == TAG_CATEGORY) {
						myCategory = new ATOMCategory();
						myCategory.readAttributes(attributes);
						myState = F_CATEGORY;
					} else if (tag == TAG_TITLE) {
						//myTitle = new ATOMTitle(); // TODO:implement ATOMTextConstruct & ATOMTitle
						//myTitle.readAttributes(attributes);
						myHtmlToString.setupTextContent(attributes.getValue("type"));
						myState = F_TITLE;
					} else if (tag == TAG_UPDATED) {
						myUpdated = new ATOMUpdated();
						myUpdated.readAttributes(attributes);
						myState = F_UPDATED;
					} else if (tag == TAG_ENTRY) {
						myEntry = new OPDSEntry();
						myEntry.readAttributes(attributes);
						myState = F_ENTRY;
						// Process feed metadata just before first feed entry
						if (myFeed != null && myFeed.Id != null) {
							myFeedReader.processFeedMetadata(myFeed, true);
						}
					} 
				} else if (tagPrefix == myOpenSearchNamespaceId) {
					if (tag == OPENSEARCH_TAG_TOTALRESULTS) {
						myState = OPENSEARCH_TOTALRESULTS;
					} else if (tag == OPENSEARCH_TAG_ITEMSPERPAGE) {
						myState = OPENSEARCH_ITEMSPERPAGE;
					} else if (tag == OPENSEARCH_TAG_STARTINDEX) {
						myState = OPENSEARCH_STARTINDEX;
					} 
				} 
				break;
			case F_ENTRY:
				if (tagPrefix == myAtomNamespaceId) {
					if (tag == TAG_AUTHOR) {
						myAuthor = new ATOMAuthor();
						myAuthor.readAttributes(attributes);
						myState = FE_AUTHOR;
					} else if (tag == TAG_ID) {
						myId = new ATOMId();
						myId.readAttributes(attributes);
						myState = FE_ID;
					} else if (tag == TAG_CATEGORY) {
						myCategory = new ATOMCategory();
						myCategory.readAttributes(attributes);
						myState = FE_CATEGORY;
					} else if (tag == TAG_LINK) {
						myLink = new ATOMLink();
						myLink.readAttributes(attributes);
						myState = FE_LINK;
					} else if (tag == TAG_PUBLISHED) {
						myPublished = new ATOMPublished();
						myPublished.readAttributes(attributes);
						myState = FE_PUBLISHED;
					} else if (tag == TAG_SUMMARY) {
						//mySummary = new ATOMSummary(); // TODO:implement ATOMTextConstruct & ATOMSummary
						//mySummary.readAttributes(attributes);
						myHtmlToString.setupTextContent(attributes.getValue("type"));
						myState = FE_SUMMARY;
					} else if (tag == TAG_CONTENT) {
						//myConent = new ATOMContent(); // TODO:implement ATOMContent
						//myConent.readAttributes(attributes);
						myHtmlToString.setupTextContent(attributes.getValue("type"));
						myState = FE_CONTENT;
					} else if (tag == TAG_TITLE) {
						//myTitle = new ATOMTitle(); // TODO:implement ATOMTextConstruct & ATOMTitle
						//myTitle.readAttributes(attributes);
						myHtmlToString.setupTextContent(attributes.getValue("type"));
						myState = FE_TITLE;
					} else if (tag == TAG_UPDATED) {
						myUpdated = new ATOMUpdated();
						myUpdated.readAttributes(attributes);
						myState = FE_UPDATED;
					}
				} else if (tagPrefix == myDublinCoreNamespaceId) {
					if (tag == DC_TAG_LANGUAGE) {
						myState = FE_DC_LANGUAGE;
					} else if (tag == DC_TAG_ISSUED) {
						myDCIssued = new DCDate();
						myDCIssued.readAttributes(attributes);
						myState = FE_DC_ISSUED;
					} else if (tag == DC_TAG_PUBLISHER) {
						myState = FE_DC_PUBLISHER;
					} 
				} else if (tagPrefix == myCalibreNamespaceId) {
					if (tag == CALIBRE_TAG_SERIES) {
						myState = FE_CALIBRE_SERIES;
					} else if (tag == CALIBRE_TAG_SERIES_INDEX) {
						myState = FE_CALIBRE_SERIES_INDEX;
					}
				}
				break;
			case F_AUTHOR:
				if (tagPrefix == myAtomNamespaceId) {
					if (tag == TAG_NAME) {
						myState = FA_NAME;
					} else if (tag == TAG_URI) {
						myState = FA_URI;
					} else if (tag == TAG_EMAIL) {
						myState = FA_EMAIL;
					} 
				} 
				break;
			case FE_AUTHOR:
				if (tagPrefix == myAtomNamespaceId) {
					if (tag == TAG_NAME) {
						myState = FEA_NAME;
					} else if (tag == TAG_URI) {
						myState = FEA_URI;
					} else if (tag == TAG_EMAIL) {
						myState = FEA_EMAIL;
					} 
				}
				break;
			case FE_LINK:
				if (tagPrefix == myOpdsNamespaceId && tag == TAG_PRICE) {
					// FIXME: HACK: price handling must be implemented not through attributes!!!
					myLink.addAttribute(KEY_CURRENCY, attributes.getValue("currencycode"));
					myState = FEL_PRICE;
				} if (tagPrefix == myDublinCoreNamespaceId && tag == DC_TAG_FORMAT) {
					myState = FEL_FORMAT;
				}
				break;
			case FE_CONTENT:
				myHtmlToString.processTextContent(false, tag, attributes, bufferContent);
				// FIXME: HACK: html handling must be implemeted neatly
				if (tag == TAG_HACK_SPAN || attributes.getValue("class") == "price") {
					myState = FEC_HACK_SPAN;
				}
				break;
			case FE_SUMMARY:
			case FE_TITLE:
			case F_TITLE:
				myHtmlToString.processTextContent(false, tag, attributes, bufferContent);
				break;
			default:
				break;
		}

		return false;
	}

	@Override
	public boolean endElementHandler(String tag) {
		final int index = tag.indexOf(':');
		final String tagPrefix;
		if (index >= 0) {
			tagPrefix = tag.substring(0, index).intern();
			tag = tag.substring(index + 1).intern();
		} else {
			tagPrefix = null;
			tag = tag.intern();
		}

		final char[] bufferContentArray = myBuffer.toString().trim().toCharArray();
		final String bufferContent;
		if (bufferContentArray.length == 0) {
			bufferContent = null;
		} else {
			bufferContent = new String(bufferContentArray);
		}
		myBuffer.delete(0, myBuffer.length());

		switch (myState) {
			case START:
				break;
			case FEED:
				if (tagPrefix == myAtomNamespaceId && tag == TAG_FEED) {
					if (myFeed != null && myFeed.Id != null) {
						myFeedReader.processFeedMetadata(myFeed, false);
					}
					myFeed = null;
					myFeedReader.processFeedEnd();
					myState = START;
				} 
				break;
			case F_ENTRY:
				if (tagPrefix == myAtomNamespaceId && tag == TAG_ENTRY) {
					if (myEntry != null && myEntry.Id != null) {
						myFeedReader.processFeedEntry(myEntry);
					}
					myEntry = null;
					myState = FEED;
				}
				break;
			case F_ID:
				if (tagPrefix == myAtomNamespaceId && tag == TAG_ID) {
					// FIXME:uri can be lost:buffer will be truncated, if there are extension tags inside the <id> tag
					if (bufferContent != null && myFeed != null) {
						myId.Uri = bufferContent;
						myFeed.Id = myId;
					}
					myId = null;
					myState = FEED;
				} 
				break;
			case F_LINK:
				if (tagPrefix == myAtomNamespaceId && tag == TAG_LINK) {
					if (myFeed != null) {
						myFeed.Links.add(myLink);
					}
					myLink = null;
					myState = FEED;
				} 
				break;
			case F_CATEGORY:
				if (tagPrefix == myAtomNamespaceId && tag == TAG_CATEGORY) {
					if (myFeed != null) {
						myFeed.Categories.add(myCategory);
					}
					myCategory = null;
					myState = FEED;
				} 
				break;
			case F_TITLE:
				if (tagPrefix == myAtomNamespaceId && tag == TAG_TITLE) {
					// TODO:implement ATOMTextConstruct & ATOMTitle
					final String title = myHtmlToString.finishTextContent(bufferContent);
					if (myFeed != null) {
						myFeed.Title = title;
					}
					myState = FEED;
				} else {
					myHtmlToString.processTextContent(true, tag, null, bufferContent);
				} 
				break;
			case F_UPDATED:
				if (tagPrefix == myAtomNamespaceId && tag == TAG_UPDATED) {
					// FIXME:uri can be lost:buffer will be truncated, if there are extension tags inside the <id> tag
					if (ATOMDateConstruct.parse(bufferContent, myUpdated) && myFeed != null) {
						myFeed.Updated = myUpdated;
					}
					myUpdated = null;
					myState = FEED;
				} 
				break;
			case F_AUTHOR:
				if (tagPrefix == myAtomNamespaceId && tag == TAG_AUTHOR) {
					if (myFeed != null && myAuthor.Name != null) {
						myFeed.Authors.add(myAuthor);
					}
					myAuthor = null;
					myState = FEED;
				} 
				break;
			case FA_NAME:
				if (tagPrefix == myAtomNamespaceId && tag == TAG_NAME) {
					myAuthor.Name = bufferContent;
					myState = F_AUTHOR;
				}
				break;
			case FEA_NAME:
				if (tagPrefix == myAtomNamespaceId && tag == TAG_NAME) {
					myAuthor.Name = bufferContent;
					myState = FE_AUTHOR;
				}
				break;
			case FA_URI:
				if (tagPrefix == myAtomNamespaceId && tag == TAG_URI) {
					myAuthor.Uri = bufferContent;
					myState = F_AUTHOR;
				}
				break;
			case FEA_URI:
				if (tagPrefix == myAtomNamespaceId && tag == TAG_URI) {
					myAuthor.Uri = bufferContent;
					myState = FE_AUTHOR;
				}
				break;
			case FA_EMAIL:
				if (tagPrefix == myAtomNamespaceId && tag == TAG_EMAIL) {
					myAuthor.Email = bufferContent;
					myState = F_AUTHOR;
				}
				break;
			case FEA_EMAIL:
				if (tagPrefix == myAtomNamespaceId && tag == TAG_EMAIL) {
					myAuthor.Email = bufferContent;
					myState = FE_AUTHOR;
				}
				break;
			case FEL_PRICE:
				if (tagPrefix == myOpdsNamespaceId && tag == TAG_PRICE) {
					if (bufferContent != null) {
						myLink.addAttribute(KEY_PRICE, bufferContent.intern());
					}
					myState = FE_LINK;
				}
				break;
			case FEL_FORMAT:
				if (tagPrefix == myDublinCoreNamespaceId && tag == DC_TAG_FORMAT) {
					if (bufferContent != null) {
						myLink.addAttribute(KEY_FORMAT, bufferContent.intern());
					}
					myState = FE_LINK;
				}
				break;
			case FE_AUTHOR:
				if (tagPrefix == myAtomNamespaceId && tag == TAG_AUTHOR) {
					if (myAuthor.Name != null) {
						myEntry.Authors.add(myAuthor);
					}
					myAuthor = null;
					myState = F_ENTRY;
				} 
				break;
			case FE_ID:
				if (tagPrefix == myAtomNamespaceId && tag == TAG_ID) {
					// FIXME:uri can be lost:buffer will be truncated, if there are extension tags inside the <id> tag
					if (bufferContent != null) {
						myId.Uri = bufferContent;
						myEntry.Id = myId;
					}
					myId = null;
					myState = F_ENTRY;
				}
				break;
			case FE_CATEGORY:
				if (tagPrefix == myAtomNamespaceId && tag == TAG_CATEGORY) {
					myEntry.Categories.add(myCategory);
					myCategory = null;
					myState = F_ENTRY;
				}
				break;
			case FE_LINK:
				if (tagPrefix == myAtomNamespaceId && tag == TAG_LINK) {
					myEntry.Links.add(myLink);
					myLink = null;
					myState = F_ENTRY;
				}
				break;
			case FE_PUBLISHED:
				if (tagPrefix == myAtomNamespaceId && tag == TAG_PUBLISHED) {
					// FIXME:uri can be lost:buffer will be truncated, if there are extension tags inside the <id> tag
					if (ATOMDateConstruct.parse(bufferContent, myPublished)) {
						myEntry.Published = myPublished;
					}
					myPublished = null;
					myState = F_ENTRY;
				}
				break;
			case FE_SUMMARY:
				if (tagPrefix == myAtomNamespaceId && tag == TAG_SUMMARY) {
					// TODO:implement ATOMTextConstruct & ATOMSummary
					myEntry.Summary = myHtmlToString.finishTextContent(bufferContent);
					myState = F_ENTRY;
				} else {
					myHtmlToString.processTextContent(true, tag, null, bufferContent);
				}
				break;
			case FE_CONTENT:
				if (tagPrefix == myAtomNamespaceId && tag == TAG_CONTENT) {
					// TODO:implement ATOMContent
					myEntry.Content = myHtmlToString.finishTextContent(bufferContent);
					myState = F_ENTRY;
				} else {
					myHtmlToString.processTextContent(true, tag, null, bufferContent);
				}
				break;
			case FE_TITLE:
				if (tagPrefix == myAtomNamespaceId && tag == TAG_TITLE) {
					// TODO:implement ATOMTextConstruct & ATOMTitle
					myEntry.Title = myHtmlToString.finishTextContent(bufferContent);
					myState = F_ENTRY;
				} else {
					myHtmlToString.processTextContent(true, tag, null, bufferContent);
				}
				break;
			case FE_UPDATED:
				if (tagPrefix == myAtomNamespaceId && tag == TAG_UPDATED) {
					// FIXME:uri can be lost:buffer will be truncated, if there are extension tags inside the <id> tag
					if (ATOMDateConstruct.parse(bufferContent, myUpdated)) {
						myEntry.Updated = myUpdated;
					}
					myUpdated = null;
					myState = F_ENTRY;
				}
				break;
			case FEC_HACK_SPAN:
				// FIXME: HACK
				myHtmlToString.processTextContent(true, tag, null, bufferContent);
				if (bufferContent != null) {
					myEntry.addAttribute(KEY_PRICE, bufferContent.intern());
				}
				myState = FE_CONTENT;
				break;
			case FE_DC_LANGUAGE:
				if (tagPrefix == myDublinCoreNamespaceId && tag == DC_TAG_LANGUAGE) {
					// FIXME:language can be lost:buffer will be truncated, if there are extension tags inside the <dc:language> tag
					myEntry.DCLanguage = bufferContent;
					myState = F_ENTRY;
				}
				break;
			case FE_DC_ISSUED:
				if (tagPrefix == myDublinCoreNamespaceId && tag == DC_TAG_ISSUED) {
					// FIXME:issued can be lost:buffer will be truncated, if there are extension tags inside the <dc:issued> tag
					if (ATOMDateConstruct.parse(bufferContent, myDCIssued)) {
						myEntry.DCIssued = myDCIssued;
					}
					myDCIssued = null;
					myState = F_ENTRY;
				}
				break;
			case FE_DC_PUBLISHER:
				if (tagPrefix == myDublinCoreNamespaceId && tag == DC_TAG_PUBLISHER) {
					// FIXME:publisher can be lost:buffer will be truncated, if there are extension tags inside the <dc:publisher> tag
					myEntry.DCPublisher = bufferContent;
					myState = F_ENTRY;
				}
				break;
			case FE_CALIBRE_SERIES:
				if (tagPrefix == myCalibreNamespaceId && tag == CALIBRE_TAG_SERIES) {
					myEntry.SeriesTitle = bufferContent;
					myState = F_ENTRY;
				}
				break;
			case FE_CALIBRE_SERIES_INDEX:
				if (tagPrefix == myCalibreNamespaceId && tag == CALIBRE_TAG_SERIES_INDEX) {
					if (bufferContent != null) {
						try {
							myEntry.SeriesIndex = Integer.parseInt(bufferContent);
						} catch (NumberFormatException ex) {
						}
					}
					myState = F_ENTRY;
				}
				break;
			case OPENSEARCH_TOTALRESULTS:
				if (tagPrefix == myOpenSearchNamespaceId &&
						tag == OPENSEARCH_TAG_TOTALRESULTS) {
					if (myFeed != null && bufferContent != null) {
						try {
							myFeed.OpensearchTotalResults = Integer.parseInt(bufferContent);
						} catch (NumberFormatException ex) {
						}
					}
					myState = FEED;
				}
				break;
			case OPENSEARCH_ITEMSPERPAGE:
				if (tagPrefix == myOpenSearchNamespaceId &&
						tag == OPENSEARCH_TAG_ITEMSPERPAGE) {
					if (myFeed != null && bufferContent != null) {
						try {
							myFeed.OpensearchItemsPerPage = Integer.parseInt(bufferContent);
						} catch (NumberFormatException ex) {
						}
					}
					myState = FEED;
				}
				break;
			case OPENSEARCH_STARTINDEX:
				if (tagPrefix == myOpenSearchNamespaceId &&
						tag == OPENSEARCH_TAG_STARTINDEX) {
					if (myFeed != null && bufferContent != null) {
						try {
							myFeed.OpensearchStartIndex = Integer.parseInt(bufferContent);
						} catch (NumberFormatException ex) {
						}
					}
					myState = FEED;
				}
				break;
		}

		return false;
	}

	@Override
	public void characterDataHandler(char[] data, int start, int length) {
		final int startIndex = myBuffer.length();
		myBuffer.append(data, start, length);
		int index = startIndex;
		while ((index = myBuffer.indexOf("\r\n", index)) != -1) {
			myBuffer.replace(index, index + 2, "\n");
		}
		index = startIndex;
		while ((index = myBuffer.indexOf("\r", index)) != -1) {
			myBuffer.setCharAt(index, '\n');
		}
	}
}
