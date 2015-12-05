package com.sample.userinfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sample.userinfo.CallLogs.TopFriend;
import com.sample.userinfo.Categories.Category;

import android.app.ListActivity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class HikeathonActivity extends ListActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// alldatahere
		processSMS();
		setEmails();
		setBestFriends();
		setUpcomingEvents();
		setTopApps();
		setRecentServices();
		setInterests();

		ArrayList<String> categories = new ArrayList<String>();
		categories.addAll(CommonEntitiesForSMS.linkedHashMap.keySet());

		// Binding Array to ListAdapter
		this.setListAdapter(new ArrayAdapter<String>(this, R.layout.mainlist,
				R.id.label, categories));

		ListView lv = getListView();

		// listening to single list item on click
		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// selected item
				String product = ((TextView) view).getText().toString();

				// Launching new Activity on selecting single List Item
				Intent i = new Intent(getApplicationContext(),
						CategoryInfo.class);
				// sending data to new activity
				i.putExtra("product", product);
				startActivity(i);

			}
		});
	}

	private void processSMS() {
		Uri uriSMSURI = Uri.parse("content://sms/inbox");
		Cursor cur = getContentResolver().query(uriSMSURI, null, null, null,
				null);
		while (cur.moveToNext()) {
			// sms += "From :" + cur.getString(2) + " : " + cur.getString(13) +
			// "\n";
			String from = cur.getString(2);
			String msg = cur.getString(13).toUpperCase();
			Boolean toMask = maskSensitiveMessage(msg);
			if (toMask) {
				continue;
			}
			this.smsAnalytics(from, msg);
		}
	}

	 private void setEmails() {
		ArrayList<String> emails = CallLogs.getEmail(HikeathonActivity.this);
		for (String email : emails) {
			CommonEntitiesForSMS.addInCategory(new Category(Categories.USER_PROFILE),
					"Email", email);
		}
	}

	private void setBestFriends() {

		TreeMap<Integer, TopFriend> friends = CallLogs
				.getBestFriends(HikeathonActivity.this);
		Set<Integer> keys = friends.descendingKeySet();
		int i = 0;
		for (int key : keys) {
			if (i == 5) {
				break;
			}
			TopFriend t = friends.get(key);
			CommonEntitiesForSMS.addInCategory(new Category(Categories.TOP_FRIENDS), "Name",
					t.Name);
			CommonEntitiesForSMS.addInCategory(new Category(Categories.TOP_FRIENDS),
					"Number", t.Telno);
			CommonEntitiesForSMS.addInCategory(new Category(Categories.TOP_FRIENDS),
					"CallLogs", Integer.toString(t.CallLogs));
			i++;
		}
	}

	private void setTopApps() {

		TreeMap<Integer, String> apps = CallLogs
				.getRecentProcesses(HikeathonActivity.this);
		if(!(apps.size() == 0)){
		Set<Integer> keys = apps.descendingKeySet();
		int i = 0;
		for (int key : keys) {
			if (i == 5) {
				break;
			}
			String t = apps.get(key);
			CommonEntitiesForSMS.addInCategory(new Category(Categories.TOP_APPS), "Name", t);
			i++;
		}
		}
		else
		{
			TreeMap<Long, String> apps1 = CallLogs
					.getActiveRunningProcesses(HikeathonActivity.this);
			Set<Integer> keys = apps.descendingKeySet();
			int i = 0;
			for (long key : keys) {
				if (i == 5) {
					break;
				}
				String t = apps.get(key);
				CommonEntitiesForSMS.addInCategory(new Category(Categories.TOP_APPS), "Name", t);
				CommonEntitiesForSMS.addInCategory(new Category(Categories.TOP_APPS), "DataUsage", Long.toString(key));
				i++;
			}
			
		}
		
		
	}

	private void setRecentServices() {

		TreeMap<Long, String[]> apps = CallLogs
				.getRecentServices(HikeathonActivity.this);
		Set<Long> keys = apps.descendingKeySet();
		int i = 0;
		for (long key : keys) {
			String[] t = apps.get(key);
			CommonEntitiesForSMS.addInCategory(new Category(Categories.TOP_APPS), "ServiceName", t[1]);
			CommonEntitiesForSMS.addInCategory(new Category(Categories.TOP_APPS), "RecentTrafficUsage", t[0]);
			CommonEntitiesForSMS.addInCategory(new Category(Categories.TOP_APPS), "ActiveSince", Long.toString(key));
			i++;
		}
	}
	
	private void setInterests() {

		HashMap<String, ApplicationInfo> installedApps = CallLogs
				.getInstalledApps(HikeathonActivity.this);
		Set<String> keys = installedApps.keySet();
		int i = 0;
		for (String key : keys) {
			ApplicationInfo t = installedApps.get(key);
			CommonEntitiesForSMS.addInCategory(new Category(Categories.TOP_APPS), "Likes", t.packageName);
			CommonEntitiesForSMS.addInCategory(new Category(Categories.TOP_APPS), "Class", t.className);
			CommonEntitiesForSMS.addInCategory(new Category(Categories.TOP_APPS), "Importance", t.name);
			i++;
		}
	}
	
	private void setUpcomingEvents() {

		HashMap<String, String[]> events = CallLogs
				.readCalendarEvent(HikeathonActivity.this);
		Set<String> keys = events.keySet();
		int i = 0;
		for (String key : keys) {
			if (i == 5) {
				break;
			}
			String[] t = events.get(key);
			CommonEntitiesForSMS.addInCategory(new Category(Categories.USER_PROFILE), "Name",
					key);
			CommonEntitiesForSMS.addInCategory(new Category(Categories.USER_PROFILE),
					"StartDate", t[0]);
			CommonEntitiesForSMS.addInCategory(new Category(Categories.USER_PROFILE),
					"EndDate", t[1]);
			CommonEntitiesForSMS.addInCategory(new Category(Categories.USER_PROFILE),
					"Description", t[2]);
			i++;
		}
	}

	private void smsAnalytics(String from, String msg) {
		// if the pattern is [XX]-[123456], it is bulk.
		// Get the provider's name
		// Brute-Force logic
		if (from.length() == 9 && from.indexOf('-') == 2) {
			String originator = from.substring(from.indexOf('-') + 1)
					.toUpperCase();
			Set<String> categories = CommonEntitiesForSMS.linkedHashMap
					.keySet();
			// Loop through all categories and check if from belongs to any
			// sub-categories
			Boolean doesExists = false;
			for (String category : categories) {
				Map<String, Set<String>> subCategoryMap = CommonEntitiesForSMS.linkedHashMap
						.get(category);
				if (subCategoryMap.containsKey(originator)) {
					doesExists = true;
					Set<String> values = subCategoryMap.get(originator);
					if (!values.contains(msg)) {
						values.add(msg);
					}
					return;
				}
			}
			if (doesExists == false) {
				String category = guessCategory(originator, msg);
				if (category == null) {
					return;
				}
				Map<String, Set<String>> stringSetMap = CommonEntitiesForSMS.linkedHashMap
						.get(category);
				if (stringSetMap.containsKey(originator)) {
					Set<String> values = stringSetMap.get(originator);
					values.add(msg);
				} else {
					Set<String> value = new TreeSet<String>();
					value.add(msg);
					stringSetMap.put(originator, value);
				}
			}
		} else {
			// Not BULK. Figure out if user is in contact list. Then increase
			// the chances of being
			// favourite.
			String category = guessCategory(from, msg);
			if (category == null) {
				return;
			}
			Map<String, Set<String>> stringSetMap = CommonEntitiesForSMS.linkedHashMap
					.get(category);
			if (stringSetMap.containsKey(from)) {
				Set<String> values = stringSetMap.get(from);
				values.add(msg);
			} else {
				Set<String> value = new TreeSet<String>();
				value.add(msg);
				stringSetMap.put(from, value);
			}
		}
	}

	private String guessCategory(String from, String msg) {
		if (from.contains("OTP")) {
			return Categories.ERETAIL;
		}
		if (from.contains("FACE") || from.contains("TWIT")
				|| from.contains("LINKE")) {
			return Categories.SOCIAL;
		}
		Pattern p = Pattern
				.compile("STAY|HOLIDAY|HOTEL|HOTELS|ROOM|ROOMS|TICKET|TICKETS");
		String s = matchesPattern(p, msg);
		if (s != null) {
			return Categories.TRAVEL;
		}
		p = Pattern.compile("DELIVERY|PURCHASE");
		s = matchesPattern(p, msg);
		if (s != null) {
			return Categories.ERETAIL;
		}
		p = Pattern
				.compile("EDU|EDUCATION|CLASS|CLASSES|COACHING|MAGAZINE|PUBLISHER");
		s = matchesPattern(p, msg);
		if (s != null) {
			return Categories.EDUCATIONAL;
		}
		p = Pattern.compile("HEALTH|HOSPITAL|INSURANCE|LIC|");
		s = matchesPattern(p, msg);
		if (s != null) {
			return Categories.HEALTH;
		}
		return null;
	}

	private Boolean maskSensitiveMessage(String msg) {
		Pattern p = Pattern.compile("PASSWORD|TRANSACTION|RS");
		String s = matchesPattern(p, msg);
		return s != null ? true : false;
	}

	private static String matchesPattern(Pattern p, String sentence) {
		Matcher m = p.matcher(sentence);

		if (m.find()) {
			return m.group();
		}

		return null;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuInflater imf = getMenuInflater();
		imf.inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		if (item.getItemId() == R.id.action_settings) {
			Intent intent = new Intent(HikeathonActivity.this,
					MainActivity.class);
			startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
	}

}
