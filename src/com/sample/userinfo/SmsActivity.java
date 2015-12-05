package com.sample.userinfo;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

public class SmsActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        processSMS();
        setContentView(R.layout.activity_main);
     }

    private void processSMS() {
        Uri uriSMSURI = Uri.parse("content://sms/inbox");
        Cursor cur = getContentResolver().query(uriSMSURI, null, null, null,null);
        while (cur.moveToNext()) {
            //sms += "From :" + cur.getString(2) + " : " + cur.getString(13) + "\n";
            String from = cur.getString(2);
            String msg  = cur.getString(13).toUpperCase();
            Boolean toMask = maskSensitiveMessage(msg);
            if (toMask) {
                continue;
            }
            this.smsAnalytics(from, msg);
        }
    }
    private void smsAnalytics(String from, String msg) {
        //if the pattern is [XX]-[123456], it is bulk.
        //Get the provider's name
        //Brute-Force logic
        if (from.length() == 9 && from.indexOf('-') == 2) {
            String originator = from.substring(from.indexOf('-')+1).toUpperCase();
            Set<String> categories = CommonEntitiesForSMS.linkedHashMap.keySet();
            //Loop through all categories and check if from belongs to any sub-categories
            Boolean doesExists = false;
            for (String category : categories) {
                Map<String, Set<String>> subCategoryMap =
                        CommonEntitiesForSMS.linkedHashMap.get(category);
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
                Map<String, Set<String>> stringSetMap =
                        CommonEntitiesForSMS.linkedHashMap.get(category);
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
            //Not BULK. Figure out if user is in contact list. Then increase the chances of being
            //favourite.
            String category = guessCategory(from, msg);
            if (category == null) {
                return;
            }
            Map<String, Set<String>> stringSetMap =
                    CommonEntitiesForSMS.linkedHashMap.get(category);
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
        Pattern p = Pattern.compile("STAY|HOLIDAY|HOTEL|HOTELS|ROOM|ROOMS|TICKET|TICKETS");
        String s = matchesPattern(p, msg);
        if (s != null) {
            return Categories.TRAVEL;
        }
        p = Pattern.compile("DELIVERY|PURCHASE");
        s = matchesPattern(p, msg);
        if (s != null) {
            return Categories.ERETAIL;
        }
        p = Pattern.compile("EDU|EDUCATION|CLASS|CLASSES|COACHING|MAGAZINE|PUBLISHER");
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

    private static String matchesPattern(Pattern p,String sentence) {
        Matcher m = p.matcher(sentence);

        if (m.find()) {
            return m.group();
        }

        return null;
    }

    private Boolean maskSensitiveMessage(String msg) {
        Pattern p = Pattern.compile("PASSWORD|TRANSACTION|RS");
        String s = matchesPattern(p, msg);
        return s != null ? true : false;
    }

}