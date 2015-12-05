package com.sample.userinfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.sample.userinfo.CallLogs.TopFriend;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.widget.TextView;

public class CategoryInfo extends Activity{
	
	String category = "";
	StringBuilder categoryInfo =  new StringBuilder("");
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.listitem);
         
        TextView txtProduct = (TextView) findViewById(R.id.product_label);
         
        Intent i = getIntent();
        // getting attached intent data
        category = i.getStringExtra("product");
        // displaying selected product name
        txtProduct.setText(category +"\n\n");       
         
        if(category.equals(Categories.TRAVEL))
        {
        	 setEmails();  
        	setUpcomingEvents();
        	setInterests();
        }
        
        if(category.equals(Categories.FINANCE))
        {
        	setBestFriends();
        }
        
        if(category.equals(Categories.ENTERTAINMENT))
        {
        	setTopApps();       	
        	setRecentServices();
        }
        
        txtProduct.append(BuildCategoryInfoDisplay());
    }
    
    
    private void setEmails() {
		ArrayList<String> emails = CallLogs.getEmail(CategoryInfo.this);
		for (String email : emails) {
			categoryInfo.append(String.format("Email: %s\n", email));
			//CommonEntitiesForSMS.addInCategory(Categories.USER_PROFILE,
				//	"Email", email);
		}
	}

	private void setBestFriends() {

		TreeMap<Integer, TopFriend> friends = CallLogs
				.getBestFriends(CategoryInfo.this);
		Set<Integer> keys = friends.descendingKeySet();
		int i = 0;
		for (int key : keys) {
			if (i == 5) {
				break;
			}
			TopFriend t = friends.get(key);
			categoryInfo.append(String.format("BestFriend \nName: %s\n", t.Name));
			categoryInfo.append(String.format("Number: %s\n", t.Telno));
			categoryInfo.append(String.format("CallLogs: %s\n\n", t.CallLogs));
			/*CommonEntitiesForSMS.addInCategory(Categories.TOP_FRIENDS, "Name",
					t.Name);
			CommonEntitiesForSMS.addInCategory(Categories.TOP_FRIENDS,
					"Number", t.Telno);
			CommonEntitiesForSMS.addInCategory(Categories.TOP_FRIENDS,
					"CallLogs", t.CallLogs);*/
			i++;
		}
	}

	

	private void setRecentServices() {

		TreeMap<Long, String[]> apps = CallLogs
				.getRecentServices(CategoryInfo.this);
		Set<Long> keys = apps.descendingKeySet();
		int i = 0;
		for (long key : keys) {
			if (i == 5) {
				break;
			}
			String[] t = apps.get(key);
			categoryInfo.append(String.format("ServiceName: %s\n", t[1]));
			categoryInfo.append(String.format("RecentTrafficUsage: %s\n", t[0]));
			categoryInfo.append(String.format("ActiveSince: %s\n\n", key));
			//CommonEntitiesForSMS.addInCategory(Categories.TOP_APPS, "ServiceName", t[1]);
			//CommonEntitiesForSMS.addInCategory(Categories.TOP_APPS, "RecentTrafficUsage", t[0]);
			//CommonEntitiesForSMS.addInCategory(Categories.TOP_APPS, "ActiveSince", key);
			i++;
		}
	}
	
	private void setInterests() {

		HashMap<String, ApplicationInfo> installedApps = CallLogs
				.getInstalledApps(CategoryInfo.this);
		Set<String> keys = installedApps.keySet();
		int i = 0;
		for (String key : keys) {
			if (i == 5) {
				break;
			}
			ApplicationInfo t = installedApps.get(key);
			categoryInfo.append(String.format("Likes: %s\n", key));
			categoryInfo.append(String.format("Class: %s\n\n", t.className));
			//CommonEntitiesForSMS.addInCategory(Categories.USER_PROFILE, "Likes", t);
			//CommonEntitiesForSMS.addInCategory(Categories.TOP_APPS, "Class", t.className);
			//CommonEntitiesForSMS.addInCategory(Categories.TOP_APPS, "Importance", t.name);
			i++;
		}
	}
	
	private void setUpcomingEvents() {

		HashMap<String, String[]> events = CallLogs
				.readCalendarEvent(CategoryInfo.this);
		Set<String> keys = events.keySet();
		int i = 0;
		for (String key : keys) {
			if (i == 5) {
				break;
			}
			String[] t = events.get(key);
			categoryInfo.append(String.format("UpcomingEventName: %s\n", key));
			categoryInfo.append(String.format("StartDate: %s\n", t[0]));
			categoryInfo.append(String.format("EndDate: %s\n", t[1]));
			categoryInfo.append(String.format("Description: %s\n\n", t[2]));
			/*CommonEntitiesForSMS.addInCategory(Categories.USER_PROFILE, "Name",
					key);
			CommonEntitiesForSMS.addInCategory(Categories.USER_PROFILE,
					"StartDate", t[0]);
			CommonEntitiesForSMS.addInCategory(Categories.USER_PROFILE,
					"EndDate", t[1]);
			CommonEntitiesForSMS.addInCategory(Categories.USER_PROFILE,
					"Description", t[2]);*/
			i++;
		}
	}
    
    
    
    private void setTopApps() {

		TreeMap<Integer, String> apps = CallLogs.getRecentProcesses(CategoryInfo.this);
		if(!(apps.size() == 0)){
		Set<Integer> keys = apps.descendingKeySet();
		int i = 0;
		for (int key : keys) {
			if (i == 5) {
				break;
			}
			String t = apps.get(key);
			categoryInfo.append(String.format("TopUsedName: %s\n\n", t));
			//CommonEntitiesForSMS.addInCategory(Categories.TOP_APPS, "Name", t);
			i++;
		}
		}
		else
		{
			TreeMap<Long, String> apps1 = CallLogs
					.getActiveRunningProcesses(CategoryInfo.this);
			Set<Integer> keys = apps.descendingKeySet();
			int i = 0;
			for (long key : keys) {
				if (i == 5) {
					break;
				}
				String t = apps.get(key);
				categoryInfo.append(String.format("TopUsedName: %s\n", t));
				categoryInfo.append(String.format("DataUsage: %s\n\n", key));
				//CommonEntitiesForSMS.addInCategory(Categories.TOP_APPS, "Name", t);
				//CommonEntitiesForSMS.addInCategory(Categories.TOP_APPS, "DataUsage", key);
				i++;
			}
			
		}
		
		
	}
    
    private String BuildCategoryInfoDisplay()
    {
    	Map<String, Set<String>> subCategoryMap = 
                CommonEntitiesForSMS.linkedHashMap.get(category);
    	Set<String> subCategories = subCategoryMap.keySet();
    	for(String subcategory : subCategories){   
    		String data = GetValues(subCategoryMap.get(subcategory));
    		categoryInfo.append(String.format("%s: %s\n\n", subcategory, data )); 
    		
    	}    	
    	return categoryInfo.toString();
    }
    
    private String GetValues(Set<String> values)
    {
    	String val = "";   	
    	for(String v : values)
    	{
    		if(values.size() == 1)
        	{
        		return val;
        	}    		
    		val = val + v.concat(",");
    	}
    	return val;
    }
    
}