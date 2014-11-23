package org.ahuh.flickr.sorter.helper;

import org.joda.time.DateTime;

/**
 * Date Helper
 * @author Julien
 *
 */
public class DateHelper {

	/**
	 * Convert JSON to Java Date
	 * @param jsonDate
	 * @return
	 */
	public static DateTime convertJsonToJava(String jsonDate) {
		return new DateTime(Long.parseLong(jsonDate) * 1000);
	}
}
