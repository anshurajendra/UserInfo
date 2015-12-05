package com.sample.userinfo;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class CommonEntitiesForSMS {

	public static final LinkedHashMap<String, Map<String, Set<String>>> linkedHashMap = new LinkedHashMap<String, Map<String, Set<String>>>();

	public static void addInCategory(Categories.Category broadCategory,
			String specificCategory, String value) {
		Map<String, Set<String>> stringListMap = linkedHashMap
				.get(broadCategory.toString());

		if (stringListMap.containsKey(specificCategory)) {
			Set<String> set = stringListMap.get(specificCategory);
			if (set == null) {
				Set<String> tempSet = new TreeSet<String>();
				tempSet.add(value);
				stringListMap.put(specificCategory, tempSet);
				return;
			}
			if (!stringListMap.get(specificCategory).contains(value)) {
				Set<String> tempSet = stringListMap.get(specificCategory);
				tempSet.add(value);
				return;
			}
			return;
		}
		Set<String> set = new TreeSet<String>();
		set.add(value);
		stringListMap.put(specificCategory, set);
		return;
	}

	static {
		// MainCategory : Finance : Some banks related
		Map<String, Set<String>> banks = new TreeMap<String, Set<String>>();
		banks.put("MYAMEX", new TreeSet<String>());
		banks.put("ICICIB", new TreeSet<String>());
		banks.put("BAJAJF", new TreeSet<String>());
		banks.put("HDFCBK", new TreeSet<String>());
		banks.put("FROMSC", new TreeSet<String>());
		linkedHashMap.put(Categories.FINANCE, banks);
		// <TODO> all numbers are notifications
		// EMI word , Password, show / pvr / cinema / mp3
		// pleasant stay - travel
		// insurance MAXBUP for health. Diet etc...

		Map<String, Set<String>> travel = new TreeMap<String, Set<String>>();
		travel.put("AIROAM", new TreeSet<String>());
		travel.put("MMTRIP", new TreeSet<String>());
		linkedHashMap.put(Categories.TRAVEL, travel);

		Map<String, Set<String>> entertainment = new TreeMap<String, Set<String>>();
		entertainment.put("OTP", new TreeSet<String>());
		entertainment.put("BMSHOW", new TreeSet<String>());
		linkedHashMap.put(Categories.ENTERTAINMENT, entertainment);

		Map<String, Set<String>> eretail = new TreeMap<String, Set<String>>();
		eretail.put("MYNTRA", new TreeSet<String>());
		eretail.put("FLPKRT", new TreeSet<String>());
		eretail.put("SNAPDL", new TreeSet<String>());
		eretail.put("YDEALS", new TreeSet<String>());
		eretail.put("FCHRGE", new TreeSet<String>());
		eretail.put("IPAYTM", new TreeSet<String>());
		eretail.put("YEBHI", new TreeSet<String>());
		linkedHashMap.put(Categories.ERETAIL, eretail);

		Map<String, Set<String>> health = new TreeMap<String, Set<String>>();
		health.put("MAXBUP", new TreeSet<String>());
		linkedHashMap.put(Categories.HEALTH, health);

		Map<String, Set<String>> social = new TreeMap<String, Set<String>>();
		social.put("ONLRTI", new TreeSet<String>());
		social.put("FACEBOOK", new TreeSet<String>());
		social.put("TWITTER", new TreeSet<String>());
		social.put("LINKEDIN", new TreeSet<String>());
		social.put("GOOGLE", new TreeSet<String>());
		linkedHashMap.put(Categories.SOCIAL, social);

	}
}
