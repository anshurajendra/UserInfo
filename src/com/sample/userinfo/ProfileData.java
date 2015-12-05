package com.sample.userinfo;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.util.Patterns;

public class ProfileData {

	public static class UserProfile {

		public void addPossibleEmail(String email) {
			addPossibleEmail(email, false);
		}

		public void addPossibleEmail(String email, boolean is_primary) {
			if (email == null)
				return;
			if (is_primary) {
				_primary_email = email;
				_possible_emails.add(email);
			} else
				_possible_emails.add(email);
		}

		public void addPossibleName(String name) {
			if (name != null)
				_possible_names.add(name);
		}
		
		public void addPossiblePhoneNumber(String phone_number) {
			if (phone_number != null)
				_possible_phone_numbers.add(phone_number);
		}

		public void addPossiblePhoneNumber(String phone_number,
				boolean is_primary) {
			if (phone_number == null)
				return;
			if (is_primary) {
				_primary_phone_number = phone_number;
				_possible_phone_numbers.add(phone_number);
			} else
				_possible_phone_numbers.add(phone_number);
		}

		public List<String> possibleEmails() {
			return _possible_emails;
		}

		public List<String> possibleNames() {
			return _possible_names;
		}

		public List<String> possiblePhoneNumbers() {
			return _possible_phone_numbers;
		}

		public String primaryEmail() {
			return _primary_email;
		}

		public String primaryPhoneNumber() {
			return _primary_phone_number;
		}

		private String _primary_email;
		private String _primary_name;
		private String _primary_phone_number;
		private ArrayList<String> _possible_emails = new ArrayList<String>();
		private ArrayList<String> _possible_names = new ArrayList<String>();
		private ArrayList<String> _possible_phone_numbers = new ArrayList<String>();
	}

	public static UserProfile getUserProfile(Context context) {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH ? getUserProfileOnIcsDevice(context)
				: getUserProfileOnGingerbreadDevice(context);
	}

	private static UserProfile getUserProfileOnGingerbreadDevice(Context context) {
		// Other that using Patterns (API level 8) this works on devices down to
		// API level 5
		final Matcher valid_email_address = Patterns.EMAIL_ADDRESS.matcher("");
		final Account[] accounts = AccountManager.get(context).getAccounts();
		final UserProfile user_profile = new UserProfile();
		// As far as I can tell, there is no way to get the real name or phone
		// number from the Google account
		for (Account account : accounts) {
			if (valid_email_address.reset(account.name).matches())
				user_profile.addPossibleEmail(account.name);
		}
		// Gets the phone number of the device is the device has one
		if (context.getPackageManager().hasSystemFeature(
				context.TELEPHONY_SERVICE)) {
			final TelephonyManager telephony = (TelephonyManager) context
					.getSystemService(context.TELEPHONY_SERVICE);
			user_profile.addPossiblePhoneNumber(telephony.getLine1Number());
		}

		return user_profile;
	}

	private static UserProfile getUserProfileOnIcsDevice(Context context) {
		final ContentResolver content = context.getContentResolver();
		final Cursor cursor = content
				.query(
				// Retrieves data rows for the device user's 'profile' contact
				Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
						ContactsContract.Contacts.Data.CONTENT_DIRECTORY),
						ProfileQuery.PROJECTION,

						// Selects only email addresses or names
						ContactsContract.Contacts.Data.MIMETYPE + "=? OR "
								+ ContactsContract.Contacts.Data.MIMETYPE
								+ "=? OR "
								+ ContactsContract.Contacts.Data.MIMETYPE
								+ "=? OR "
								+ ContactsContract.Contacts.Data.MIMETYPE
								+ "=?",
						new String[] {
								ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE,
								ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE,
								ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE,
								ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE },

						// Show primary rows first. Note that there won't be a
						// primary email address if the
						// user hasn't specified one.
						ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");

		final UserProfile user_profile = new UserProfile();
		String mime_type;
		while (cursor.moveToNext()) {
			mime_type = cursor.getString(ProfileQuery.MIME_TYPE);
			if (mime_type
					.equals(ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE))
				user_profile.addPossibleEmail(
						cursor.getString(ProfileQuery.EMAIL),
						cursor.getInt(ProfileQuery.IS_PRIMARY_EMAIL) > 0);
			else if (mime_type
					.equals(ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE))
				user_profile.addPossibleName(cursor
						.getString(ProfileQuery.GIVEN_NAME)
						+ " "
						+ cursor.getString(ProfileQuery.FAMILY_NAME));
			else if (mime_type
					.equals(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE))
				user_profile
						.addPossiblePhoneNumber(
								cursor.getString(ProfileQuery.PHONE_NUMBER),
								cursor.getInt(ProfileQuery.IS_PRIMARY_PHONE_NUMBER) > 0);
		}
		cursor.close();
		return user_profile;
	}

	private interface ProfileQuery {
		/** The set of columns to extract from the profile query results */
		String[] PROJECTION = { ContactsContract.CommonDataKinds.Email.ADDRESS,
				ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
				ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME,
				ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME,
				ContactsContract.CommonDataKinds.Phone.NUMBER,
				ContactsContract.CommonDataKinds.Phone.IS_PRIMARY,
				ContactsContract.CommonDataKinds.Photo.PHOTO_URI,
				ContactsContract.Contacts.Data.MIMETYPE };

		/** Column index for the email address in the profile query results */
		int EMAIL = 0;
		/**
		 * Column index for the primary email address indicator in the profile
		 * query results
		 */
		int IS_PRIMARY_EMAIL = 1;
		/** Column index for the family name in the profile query results */
		int FAMILY_NAME = 2;
		/** Column index for the given name in the profile query results */
		int GIVEN_NAME = 3;
		/** Column index for the phone number in the profile query results */
		int PHONE_NUMBER = 4;
		/**
		 * Column index for the primary phone number in the profile query
		 * results
		 */
		int IS_PRIMARY_PHONE_NUMBER = 5;
		/** Column index for the photo in the profile query results */
		int PHOTO = 6;
		/** Column index for the MIME type in the profile query results */
		int MIME_TYPE = 7;
	}
}
