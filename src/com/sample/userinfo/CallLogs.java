package com.sample.userinfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ActivityManager;
import android.app.ActivityManager.RecentTaskInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.TrafficStats;
import android.net.Uri;
import android.provider.CallLog;
import android.util.Log;
import android.util.Patterns;

public class CallLogs {

	static long send = 0;

	static long received = 0;

	public static ArrayList<String> Festivals = new ArrayList<String> (Arrays.asList("Bhai Duj", "Rama Navami",
			"Good Friday", "Birthday of Ravindranath", "Independence Day",
			"Dussehra (Maha Navami)", "Christmas Eve", "Makar Sankranti",
			"Republic Day", "Vasant Panchami", "Parsi New Year",
			"Maha Saptami", "Diwali/Deepavali",
			"Guru Tegh Bahadur's Martyrdom Day", "Vaisakhi",
			"Hazarat Ali's Birthday", "Jamat Ul-Vida", "Muharram/Ashura",
			"Chaitra Sukhladi", "Mesadi/Vaisakhadi", "Easter Day",
			"Janmashtami", "Naraka Chaturdasi", "Christmas", "New Year's Day",
			"Dolyatra", "Rath Yatra", "Onam", "Pongal",
			"Milad un-Nabi/Id-e-Milad", "Holika Dahana", "Buddha Purnima",
			"Maha Ashtami", "Bakri Id/Eid ul-Adha",
			"Maha Shivaratri/Shivaratri", "Ganesh Chaturthi/Vinayaka Chaturthi","Ganesh Chaturthi",
			"Ramzan Id/Eid-ul-Fitar", "Karaka Chaturthi (Karva Chauth)",
			"Raksha Bandhan (Rakhi)", "Milad un-Nabi/Id-e-Milad","Buddha Purnima/Vesak" ));


	public static String MAX_CURSOR_LIMIT = "200";

	public static TreeMap<Integer, TopFriend> getBestFriends(Context ctx) {
		ArrayList<String> allNumbers = new ArrayList<String>();
		Map<String, String> callLogMap = new HashMap<String, String>();

		String strOrder = android.provider.CallLog.Calls.DATE + " DESC";
		String[] projection = new String[] { CallLog.Calls.NUMBER,
				CallLog.Calls.CACHED_NAME, CallLog.Calls.DATE,
				CallLog.Calls.DURATION };
		String selection = CallLog.Calls.DATE + " > ?";
		String[] selectors = new String[] { String.valueOf(System
				.currentTimeMillis() - (1000 * 60 * 60 * 24 * 30)) };
		Uri callUri = CallLog.Calls.CONTENT_URI;
		Uri callUriLimited = callUri
				.buildUpon()
				.appendQueryParameter(CallLog.Calls.LIMIT_PARAM_KEY,
						String.valueOf(MAX_CURSOR_LIMIT)).build();

		ContentResolver cr = ctx.getContentResolver();
		Cursor cur = cr.query(callUriLimited, null, null, null, strOrder);

		// int i =0;
		if (cur != null) {
			try {
				while (cur.moveToNext()) {

					String callNumber = cur
							.getString(cur
									.getColumnIndex(android.provider.CallLog.Calls.NUMBER));
					String callName = cur
							.getString(cur
									.getColumnIndex(android.provider.CallLog.Calls.CACHED_NAME));
					if (callNumber == null || callNumber.length() < 10) {
						continue;
					}
					Log.d("anshu1", callName + callNumber);
					allNumbers.add(callNumber.substring(
							callNumber.length() - 10, callNumber.length()));
					callLogMap.put(callNumber.substring(
							callNumber.length() - 10, callNumber.length()),
							callName);
				}

			} catch (Exception e) {

				Log.d("anshu2", e.toString());

			} finally {
				cur.close();
			}

		}
		TreeMap<Integer, TopFriend> topXFriends = FindTopXFriends(allNumbers,
				callLogMap);
		return topXFriends;
	}
	
	private static TreeMap<Integer, TopFriend> FindTopXFriends(ArrayList<String> arr,
			Map<String, String> map) {
		Log.d("anshu3", "" + arr.size());
		TreeMap<Integer, TopFriend> topFriends = new TreeMap<Integer, TopFriend>();
		ArrayList<String> list1 = new ArrayList<String>();
		ArrayList<Integer> list2 = new ArrayList<Integer>();
		for (int i = 0; i < arr.size(); i++) {
			int index = list1.indexOf(arr.get(i));
			if (index != -1) {
				int newCount = list2.get(index) + 1;
				list2.set(index, newCount);
			} else {
				list1.add(arr.get(i));
				list2.add(1);
			}
		}
		for (int i = 0; i < list1.size(); i++) {
			Log.d("anshu4",
					"Number " + list1.get(i) + " occurs " + list2.get(i)
							+ " times.");
			topFriends.put(list2.get(i), new TopFriend(map.get(list1.get(i)),
					list1.get(i), list2.get(i)));
		}
		return topFriends;
	}

	public static HashMap<String,String[]> readCalendarEvent(Context context) {
		HashMap<String,String[]> events = new HashMap<String,String[]>();
		
		Cursor cursor = context.getContentResolver()
				.query(Uri.parse("content://com.android.calendar/events"),
						new String[] { "calendar_id", "title", "description",
								"dtstart", "dtend", "eventLocation" }, null,
						null, null);		
		cursor.moveToFirst();		
		while(cursor.moveToNext()){
		
			if (events.containsKey(cursor.getString(1))|| Festivals.contains(cursor.getString(1))
					|| cursor.getString(1).contains("Puja")
					|| cursor.getString(1).contains("Jayanti")) {
            	continue;
            }
           events.put(cursor.getString(1), new String[]{""+ Long.parseLong(cursor.getString(3)) , ""+ Long.parseLong(cursor.getString(4)),
							 "" + cursor.getString(2)});
			Log.d("anshu2", cursor.getString(1));
		}		
		return events;
	}
    public static ArrayList<String> getEmail(Context ct)
    {
    	ArrayList<String> emails = new ArrayList<String>();
    	Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
    	Account[] accounts = AccountManager.get(ct).getAccounts();
    	for (Account account : accounts) {
    	    if (emailPattern.matcher(account.name).matches()) {
    	        emails.add(account.name);
    	        Log.d("anshu2",account.name);
    	    }
    	}
    	return emails;
    }
	public static TreeMap<Long,String> getActiveRunningProcesses(Context ct) {
		TreeMap<Long, String> mostUsedApps = new TreeMap<Long, String>();
		// Get running processes
		ActivityManager manager = (ActivityManager) ct.getSystemService(ct.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> runningProcesses = manager
				.getRunningAppProcesses();
		if (runningProcesses != null && runningProcesses.size() > 0) {
			// Set data to the list adapter
			Iterator<RunningAppProcessInfo> i = runningProcesses.iterator();
			PackageManager pm = ct.getPackageManager();
			while (i.hasNext()) {
				ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i
						.next());
				try {
					CharSequence c = pm.getApplicationLabel(pm
							.getApplicationInfo(info.processName,
									PackageManager.GET_META_DATA));
					int uid = info.uid;
					// Get traffic data
					received = TrafficStats.getUidRxBytes(uid);
					send = TrafficStats.getUidTxBytes(uid);
					mostUsedApps.put((received / 1000) + (send / 1000), c.toString());
					Log.d("LABEL", c.toString() + (received / 1000)
							+ (send / 1000) );
				} catch (Exception e) {
					// Name Not FOund Exception
					Log.e("anshu", e.toString());
				}
			}
		} else {
			// In case there are no processes running (not a chance :))
			Log.d("anshu9", "No application is running");
		}
     return mostUsedApps;
	}

	public static TreeMap<Integer,String> getRecentProcesses(Context ct) {
		TreeMap<Integer, String> mostFequentApps = new TreeMap<Integer, String>();
		ArrayList<String> tasks = new ArrayList<String>();
		// Get running processes
		ActivityManager manager = (ActivityManager)ct.getSystemService(ct.ACTIVITY_SERVICE);
		List<RecentTaskInfo> runningProcesses = manager.getRecentTasks(50,
				ActivityManager.RECENT_WITH_EXCLUDED);
		if (runningProcesses != null && runningProcesses.size() > 0) {
			// Set data to the list adapter
			Iterator<RecentTaskInfo> i = runningProcesses.iterator();
			PackageManager pm = ct.getPackageManager();
			while (i.hasNext()) {
				ActivityManager.RecentTaskInfo info = (ActivityManager.RecentTaskInfo) (i
						.next());
				try {
					final String packagename = info.baseIntent.getComponent()
							.getPackageName();
					CharSequence c = pm.getApplicationLabel(pm
							.getApplicationInfo(packagename,
									PackageManager.GET_META_DATA));
					if(c != null || c != "" || !c.toString().contains("System") || !c.toString().contains("Settings"))
					{
					tasks.add(c.toString());
					}
					Log.w("LABEL", c.toString());
				} catch (Exception e) {
					// Name Not FOund Exception
					Log.e("anshu", e.toString());
				}
			}
		} else {
			// In case there are no processes running (not a chance :))
			Log.d("anshu9", "No application is running");
		}
		
		ArrayList<String> list1 = new ArrayList<String>();
		ArrayList<Integer> list2 = new ArrayList<Integer>();
		for (int i = 0; i < tasks.size(); i++) {
			int index = list1.indexOf(tasks.get(i));
			if (index != -1) {
				int newCount = list2.get(index) + 1;
				list2.set(index, newCount);
			} else {
				list1.add(tasks.get(i));
				list2.add(1);
			}
		}
		for (int i = 0; i < list1.size(); i++) {
			Log.d("anshu5",
					"Process " + list1.get(i) + " occurs " + list2.get(i)
							+ " times.");
			mostFequentApps.put(list2.get(i), list1.get(i));
		}		
		return mostFequentApps;
	}

	public static TreeMap<Long, String[]> getRecentServices(Context ct) {
		TreeMap<Long, String[]> serviceUsage = new TreeMap<Long, String[]>();
		// Get running processes
		ActivityManager manager = (ActivityManager) ct.getSystemService(ct.ACTIVITY_SERVICE);
		List<RunningServiceInfo> runningProcesses = manager
				.getRunningServices(50);
		if (runningProcesses != null && runningProcesses.size() > 0) {
			// Set data to the list adapter
			Iterator<RunningServiceInfo> i = runningProcesses.iterator();
			// long currentMillis = Calendar.getInstance().getTimeInMillis();
			Calendar cal = Calendar.getInstance();

			while (i.hasNext()) {
				ActivityManager.RunningServiceInfo info = (ActivityManager.RunningServiceInfo) (i
						.next());
				try {
					int uid = info.uid;
					// Get traffic data
					received = TrafficStats.getUidRxBytes(uid);
					send = TrafficStats.getUidTxBytes(uid);
					serviceUsage.put(info.activeSince,
							new String[] { ""+(received / 1000) + (send / 1000),
									info.service.getClassName() });
					Log.d("LABEL",
							String.format(
									"Process %s with component %s has been running since %s (%d milliseconds)",
									info.service.getPackageName(), info.service.getClassName(),
									cal.getTime().toString(), info.activeSince));
				} catch (Exception e) {
					// Name Not FOund Exception
					Log.e("anshu", e.toString());
				}
			}
		} else {
			// In case there are no processes running (not a chance :))
			Log.d("anshu9", "No application is running");
		}
		return serviceUsage;
	}

	public static HashMap<String, ApplicationInfo> getInstalledApps(Context ct) {
		HashMap<String, ApplicationInfo> installedApps = new HashMap<String, ApplicationInfo>();
		final PackageManager pm = ct.getPackageManager();
		List<PackageInfo> packages = pm
				.getInstalledPackages(PackageManager.GET_META_DATA);

		for (PackageInfo p : packages) {
			if (isSystemPackage(p) || p.versionName == null) {
				continue;
			}
			installedApps.put(p.applicationInfo.loadLabel(pm).toString(),
					p.applicationInfo);
			Log.d("apps", p.applicationInfo.loadLabel(pm).toString());

		}
		return installedApps;
	}

	private static boolean isSystemPackage(PackageInfo pkgInfo) {
		return ((pkgInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) ? true
				: false;
	}

	public static class TopFriend {
		String Name;
		String Telno;
		int CallLogs;

		public TopFriend(String name, String telno, int calls) {
			this.Name = name;
			this.CallLogs = calls;
			this.Telno = telno;
		}

		public void setPhone(String telno) {
			this.Telno = telno;
		}

		public void setCalls(int calls) {
			this.CallLogs = calls;
		}

		public void setName(String name) {
			this.Name = name;
		}

		public String getPhone() {
			return this.Telno;
		}

		public int getCalls() {
			return this.CallLogs;
		}

		public String getName() {
			return this.Name;
		}
	}
}
