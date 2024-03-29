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

package org.geometerplus.fbreader.network.authentication.litres;

import java.util.*;

import org.geometerplus.zlibrary.core.options.ZLStringOption;
import org.geometerplus.zlibrary.core.util.ZLNetworkUtil;
import org.geometerplus.zlibrary.core.network.ZLNetworkManager;
import org.geometerplus.zlibrary.core.network.ZLNetworkRequest;

import org.geometerplus.fbreader.network.*;
import org.geometerplus.fbreader.network.authentication.*;


public class LitResAuthenticationManager extends NetworkAuthenticationManager {

	private boolean mySidChecked;

	private ZLStringOption mySidUserNameOption;
	private ZLStringOption mySidOption;

	private String myInitializedDataSid;
	private String myAccount;
	private final HashMap<String, NetworkLibraryItem> myPurchasedBooks = new HashMap<String, NetworkLibraryItem>();


	public LitResAuthenticationManager(NetworkLink link, String sslCertificate) {
		super(link, sslCertificate);
		mySidUserNameOption = new ZLStringOption(link.SiteName, "sidUserName", "");
		mySidOption = new ZLStringOption(link.SiteName, "sid", "");
	}

	public synchronized AuthenticationStatus isAuthorised(boolean useNetwork /* = true */) {
		boolean authState =
			mySidUserNameOption.getValue().length() != 0 &&
			mySidOption.getValue().length() != 0;

		if (mySidChecked || !useNetwork) {
			return new AuthenticationStatus(authState);
		}

		if (!authState) {
			mySidChecked = true;
			mySidUserNameOption.setValue("");
			mySidOption.setValue("");
			return new AuthenticationStatus(false);
		}

		String url = Link.Links.get(NetworkLink.URL_SIGN_IN);
		if (url == null) {
			return new AuthenticationStatus(NetworkErrors.errorMessage(NetworkErrors.ERROR_UNSUPPORTED_OPERATION));
		}
		url = ZLNetworkUtil.appendParameter(url, "sid", mySidOption.getValue());

		final LitResLoginXMLReader xmlReader = new LitResLoginXMLReader(Link.SiteName);
		final String error = ZLNetworkManager.Instance().perform(new LitResNetworkRequest(url, SSLCertificate, xmlReader));

		if (error != null) {
			if (!error.equals(NetworkErrors.errorMessage(NetworkErrors.ERROR_AUTHENTICATION_FAILED))) {
				return new AuthenticationStatus(error);
			}
			mySidChecked = true;
			mySidUserNameOption.setValue("");
			mySidOption.setValue("");
			return new AuthenticationStatus(false);
		}
		mySidChecked = true;
		mySidOption.setValue(xmlReader.Sid);
		return new AuthenticationStatus(true);
	}

	public synchronized String authorise(String password) {
		String url = Link.Links.get(NetworkLink.URL_SIGN_IN);
		if (url == null) {
			return NetworkErrors.errorMessage(NetworkErrors.ERROR_UNSUPPORTED_OPERATION);
		}
		url = ZLNetworkUtil.appendParameter(url, "login", UserNameOption.getValue());
		url = ZLNetworkUtil.appendParameter(url, "pwd", password);

		final LitResLoginXMLReader xmlReader = new LitResLoginXMLReader(Link.SiteName);
		final String error = ZLNetworkManager.Instance().perform(new LitResNetworkRequest(url, SSLCertificate, xmlReader));

		mySidChecked = true;
		if (error != null) {
			mySidUserNameOption.setValue("");
			mySidOption.setValue("");
			return error;
		}
		mySidOption.setValue(xmlReader.Sid);
		mySidUserNameOption.setValue(UserNameOption.getValue());
		return null;
	}

	public synchronized void logOut() {
		mySidChecked = true;
		mySidUserNameOption.setValue("");
		mySidOption.setValue("");
	}

	public synchronized BookReference downloadReference(NetworkBookItem book) {
		final String sid = mySidOption.getValue();
		if (sid.length() == 0) {
			return null;
		}
		BookReference reference = book.reference(BookReference.Type.DOWNLOAD_FULL_CONDITIONAL);
		if (reference == null) {
			return null;
		}
		String url = reference.URL;
		url = ZLNetworkUtil.appendParameter(url, "sid", sid);
		return new DecoratedBookReference(reference, url);
	}

	public synchronized boolean skipIPSupported() {
		return true;
	}

	public synchronized String currentUserName() {
		String value = mySidUserNameOption.getValue();
		if (value.length() == 0) {
			return null;
		}
		return value;
	}


	public synchronized boolean needPurchase(NetworkBookItem book) {
		return !myPurchasedBooks.containsKey(book.Id);
	}

	public synchronized String purchaseBook(NetworkBookItem book) {
		final String sid = mySidOption.getValue();
		if (sid.length() == 0) {
			return NetworkErrors.errorMessage(NetworkErrors.ERROR_AUTHENTICATION_FAILED);
		}

		BookReference reference = book.reference(BookReference.Type.BUY);
		if (reference == null) {
			return NetworkErrors.errorMessage(NetworkErrors.ERROR_BOOK_NOT_PURCHASED); // TODO: more correct error message???
		}
		String query = reference.URL;
		query = ZLNetworkUtil.appendParameter(query, "sid", sid);

		final LitResPurchaseXMLReader xmlReader = new LitResPurchaseXMLReader(Link.SiteName);
		final String error = ZLNetworkManager.Instance().perform(new LitResNetworkRequest(query, SSLCertificate, xmlReader));

		if (xmlReader.Account != null) {
			myAccount = BuyBookReference.price(xmlReader.Account, "RUB");
		}
		final String authenticationError = NetworkErrors.errorMessage(NetworkErrors.ERROR_AUTHENTICATION_FAILED);
		if (authenticationError.equals(error)) {
			mySidChecked = true;
			mySidUserNameOption.setValue("");
			mySidOption.setValue("");
		}
		final String alreadyPurchasedError = NetworkErrors.errorMessage(NetworkErrors.ERROR_PURCHASE_ALREADY_PURCHASED);
		if (!alreadyPurchasedError.equals(error)) {
			if (error != null) {
				return error;
			}
			if (xmlReader.BookId == null || !xmlReader.BookId.equals(book.Id)) {
				return NetworkErrors.errorMessage(NetworkErrors.ERROR_SOMETHING_WRONG, Link.SiteName);
			}
		}
		myPurchasedBooks.put(book.Id, book);
		return error;
	}


	public synchronized String refillAccountLink() {
		final String sid = mySidOption.getValue();
		if (sid.length() == 0) {
			return null;
		}
		final String url = Link.Links.get(NetworkLink.URL_REFILL_ACCOUNT);
		if (url == null) {
			return null;
		}
		return ZLNetworkUtil.appendParameter(url, "sid", sid);
	}

	public synchronized String currentAccount() {
		return myAccount;
	}

	public synchronized String reloadPurchasedBooks() {
		final String sid = mySidOption.getValue();
		if (sid.length() == 0) {
			return NetworkErrors.errorMessage(NetworkErrors.ERROR_AUTHENTICATION_FAILED);
		}
		if (!sid.equals(myInitializedDataSid)) {
			mySidChecked = true;
			mySidUserNameOption.setValue("");
			mySidOption.setValue("");		
			return NetworkErrors.errorMessage(NetworkErrors.ERROR_AUTHENTICATION_FAILED);
		}

		final LitResNetworkRequest networkRequest = loadPurchasedBooks();
		final String error = ZLNetworkManager.Instance().perform(networkRequest);

		if (error != null) {
			//loadPurchasedBooksOnError();
			if (error.equals(NetworkErrors.errorMessage(NetworkErrors.ERROR_AUTHENTICATION_FAILED))) {
				mySidChecked = true;
				mySidUserNameOption.setValue("");
				mySidOption.setValue("");
			}
			return error;
		}
		loadPurchasedBooksOnSuccess(networkRequest);
		return null;
	}

	public synchronized void collectPurchasedBooks(List<NetworkLibraryItem> list) {
		list.addAll(myPurchasedBooks.values());
	}


	public synchronized boolean needsInitialization() {
		final String sid = mySidOption.getValue();
		if (sid.length() == 0) {
			return false;
		}
		return !sid.equals(myInitializedDataSid);
	}

	public synchronized String initialize() {
		final String sid = mySidOption.getValue();
		if (sid.length() == 0) {
			return NetworkErrors.errorMessage(NetworkErrors.ERROR_AUTHENTICATION_FAILED);
		}
		if (sid.equals(myInitializedDataSid)) {
			return null;
		}

		LitResNetworkRequest purchasedBooksRequest = loadPurchasedBooks();
		LitResNetworkRequest accountRequest = loadAccount();

		final LinkedList<ZLNetworkRequest> requests = new LinkedList<ZLNetworkRequest>();
		requests.add(purchasedBooksRequest);
		requests.add(accountRequest);
		final String error = ZLNetworkManager.Instance().perform(requests);

		if (error != null) {
			myInitializedDataSid = null;
			loadPurchasedBooksOnError();
			loadAccountOnError();
			return error;
		}
		myInitializedDataSid = sid;
		loadPurchasedBooksOnSuccess(purchasedBooksRequest);
		loadAccountOnSuccess(accountRequest);
		return null;
	}

	private LitResNetworkRequest loadPurchasedBooks() {
		final String sid = mySidOption.getValue();

		String query = "pages/catalit_browser/";
		query = ZLNetworkUtil.appendParameter(query, "my", "1");
		query = ZLNetworkUtil.appendParameter(query, "sid", sid);

		return new LitResNetworkRequest(
			LitResUtil.url(Link, query),
			SSLCertificate,
			new LitResXMLReader(Link, new LinkedList<NetworkLibraryItem>())
		);
	}

	private void loadPurchasedBooksOnError() {
		myPurchasedBooks.clear();
	}

	private void loadPurchasedBooksOnSuccess(LitResNetworkRequest purchasedBooksRequest) {
		LitResXMLReader reader = (LitResXMLReader) purchasedBooksRequest.Reader;
		myPurchasedBooks.clear();
		for (NetworkLibraryItem item: reader.Books) {
			if (item instanceof NetworkBookItem) {
				NetworkBookItem book = (NetworkBookItem) item;
				myPurchasedBooks.put(book.Id, book);
			}
		}
	}

	private LitResNetworkRequest loadAccount() {
		final String sid = mySidOption.getValue();

		String query = "pages/purchase_book/";
		query = ZLNetworkUtil.appendParameter(query, "sid", sid);
		query = ZLNetworkUtil.appendParameter(query, "art", "0");

		return new LitResNetworkRequest(
			LitResUtil.url(Link, query),
			SSLCertificate,
			new LitResPurchaseXMLReader(Link.SiteName)
		);
	}

	private void loadAccountOnError() {
		myAccount = null;
	}

	private void loadAccountOnSuccess(LitResNetworkRequest accountRequest) {
		LitResPurchaseXMLReader reader = (LitResPurchaseXMLReader) accountRequest.Reader;
		myAccount = BuyBookReference.price(reader.Account, "RUB");
	}


	public synchronized boolean registrationSupported() {
		return true;
	}

	public synchronized String registerUser(String login, String password, String email) {
		String url = Link.Links.get(NetworkLink.URL_SIGN_UP);
		if (url == null) {
			return NetworkErrors.errorMessage(NetworkErrors.ERROR_UNSUPPORTED_OPERATION);
		}
		url = ZLNetworkUtil.appendParameter(url, "new_login", login);
		url = ZLNetworkUtil.appendParameter(url, "new_pwd1", password);
		url = ZLNetworkUtil.appendParameter(url, "mail", email);

		final LitResRegisterUserXMLReader xmlReader = new LitResRegisterUserXMLReader(Link.SiteName);
		final String error = ZLNetworkManager.Instance().perform(new LitResNetworkRequest(url, SSLCertificate, xmlReader));

		mySidChecked = true;
		if (error != null) {
			mySidUserNameOption.setValue("");
			mySidOption.setValue("");
			return error;
		}
		mySidOption.setValue(xmlReader.Sid);
		mySidUserNameOption.setValue(login);
		return null;
	}


	public synchronized boolean passwordRecoverySupported() {
		return true;
	}

	public synchronized String recoverPassword(String email) {
		String url = Link.Links.get(NetworkLink.URL_RECOVER_PASSWORD);
		if (url == null) {
			return NetworkErrors.errorMessage(NetworkErrors.ERROR_UNSUPPORTED_OPERATION);
		}
		url = ZLNetworkUtil.appendParameter(url, "mail", email);
		final LitResPasswordRecoveryXMLReader xmlReader =  new LitResPasswordRecoveryXMLReader(Link.SiteName);
		return ZLNetworkManager.Instance().perform(new LitResNetworkRequest(url, SSLCertificate, xmlReader));
	}
}
