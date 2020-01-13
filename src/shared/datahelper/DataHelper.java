package shared.datahelper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Class to handle strings and json objects like conversion methods, hash generator, etc.
 */
public class DataHelper {
	/**
	 * Convert a map to json object
	 * @param mapData input map type <String, Object>
	 * @return json as String
	 */
	public static String convertMapToJsonString(Map<String, Object> mapData) {
		Gson gson = new Gson();
		return gson.toJson(mapData);
	}

	/**
	 * Generate checksum using md5
	 * @param secretKey api key
	 * @param dataString input data
	 * @return hashed md5 
	 */
	public static String generateChecksum(String secretKey, String dataString) {
		String concatenatedString = secretKey + dataString;
		String hashedString = md5Hash(concatenatedString);
		return md5Hash(hashedString + dataString);
	}

	/**
	 * md5 hashing function
	 * @param rawString
	 * @return md5 hashed string
	 */
	private static String md5Hash(String rawString) {
		String hashedString = "";
		try {
			MessageDigest messageDigest = MessageDigest.getInstance("MD5");
			messageDigest.update(rawString.getBytes());
			byte[] bytes = messageDigest.digest();
			StringBuilder stringBuilder = new StringBuilder();
			for (int i = 0; i < bytes.length; i++) {
				stringBuilder.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
			}
			hashedString = stringBuilder.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return hashedString;
	}

	/**
	 * Convert json string to java map object
	 * @param json
	 * @return
	 * @throws IOException
	 */
		public static Map<String, Object> convertJsonStringToMapObject(JsonObject json) throws IOException {
		String jsonString = json.toString();
		return new ObjectMapper().readValue(jsonString, HashMap.class);
	}

	/**
	 *
	 * @param dateTimeString input should be yyyy-MM-dd HH:mm:ss
	 * @return
	 */
	public static Date parseDateTimeString(String dateTimeString) {
		SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date dateTime = null;
		try {
			dateTime = dateTimeFormatter.parse(dateTimeString);
		} catch (ParseException e) {
			System.err.println("Wrong format input");
			e.printStackTrace();
		}
		return dateTime;
	}

	/**
	 * Check if the input date is within the range between current date and the date of 62 days before
	 * @param inputDate
	 * @return true if it is in the range, false otherwise
	 */
	public static boolean isDateWithin62DaysUntilToday(Date inputDate) {
		Date currentDate = Calendar.getInstance().getTime();
		Calendar calendarFrom62DaysBefore = Calendar.getInstance();
		calendarFrom62DaysBefore.add(Calendar.DAY_OF_MONTH, -62);
		calendarFrom62DaysBefore.set(Calendar.HOUR_OF_DAY, 0);
		calendarFrom62DaysBefore.set(Calendar.MINUTE, 0);
		calendarFrom62DaysBefore.set(Calendar.SECOND, 0);
		calendarFrom62DaysBefore.set(Calendar.MILLISECOND, 0);
		Date dateFrom62DaysBefore = calendarFrom62DaysBefore.getTime();
		return (inputDate.compareTo(dateFrom62DaysBefore) >= 0 && inputDate.compareTo(currentDate) <= 0);
	}

	public static String convertDateToUnixTimeStampString(Date date) {
		long unixTimeStamp = date.getTime()/1000;
		return String.valueOf(unixTimeStamp);
	}

	/**
	 * https://www.baeldung.com/sha-256-hashing-java
	 * @param rawString
	 * @return hash 256
	 */
	public static String sha256Hash(String rawString) throws NoSuchAlgorithmException {
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		byte[] encodedHash = digest.digest(
				rawString.getBytes(StandardCharsets.UTF_8));
		return bytesToHex(encodedHash);
	}

	private static String bytesToHex(byte[] hash) {
		StringBuffer hexString = new StringBuffer();
		for (int i = 0; i < hash.length; i++) {
			String hex = Integer.toHexString(0xff & hash[i]);
			if(hex.length() == 1) hexString.append('0');
			hexString.append(hex);
		}
		return hexString.toString();
	}

	/**
	 * Remove all leading zeros, all special characters and add prefix 84 for the phone number
	 * @param phoneNumber mobile phone number in string format, i.e +84945xxxx
	 * @return formatted phone number as description
	 */
	public static String formatMobileNumber(String phoneNumber) {
		return "84" + phoneNumber
			.replaceFirst("^0+(?!$)", "")
			.replaceAll("[^0-9]", "");
	}


	public static void main(String[] args) throws NoSuchAlgorithmException {
		Calendar currentDateCalendar = Calendar.getInstance();
		currentDateCalendar.add(Calendar.MONTH, 0);
		System.out.println(Calendar.getInstance().getTime());

		String date = "2019-11-11 19:37:59";
		String date1 = "2019-12-21 19:02:00";
		String date2 = "2020-01-01 00:00:00";
		String date3 = "2020-01-12 18:49:55";
		Date converted = DataHelper.parseDateTimeString(date);
		Date converted1 = DataHelper.parseDateTimeString(date1);
		Date converted2 = DataHelper.parseDateTimeString(date2);
		Date converted3 = DataHelper.parseDateTimeString(date3);
		System.out.println("?: " + converted);

		System.out.println("Date is: " + DataHelper.isDateWithin62DaysUntilToday(converted));
		System.out.println(DataHelper.isDateWithin62DaysUntilToday(converted1));
		System.out.println(DataHelper.isDateWithin62DaysUntilToday(converted2));
		System.out.println(DataHelper.isDateWithin62DaysUntilToday(converted3));

		System.out.println(converted.getTime()/1000);

		System.out.println(DataHelper.sha256Hash("mary@example.com"));
		System.out.println("0903637792");
		System.out.println(DataHelper.formatMobileNumber("0903637792"));
		System.out.println(DataHelper.sha256Hash("0903637792"));

		String dateToUnix = "2019-11-06 08:22:06";
		Date convertedDateToUnix = DataHelper.parseDateTimeString(dateToUnix);
		System.out.println(DataHelper.convertDateToUnixTimeStampString(convertedDateToUnix));

		Gson gson = new Gson();

		try (Reader reader = new FileReader("d:\\events.json")) {

			// Convert JSON File to Java Object
			JsonParser parser = new JsonParser();
			JsonArray array = parser.parse(reader).getAsJsonObject().get("data").getAsJsonArray();
			List<TemporaryFix> events = new ArrayList<>();
			Type listType = new TypeToken<List<TemporaryFix>>() {}.getType();
			events = gson.fromJson(array, listType);
			events = events.stream().filter(event -> event.getCount() > 0 && event.getEvent().equals("Purchase")).collect(Collectors.toList());
			System.out.println(events.size());
			TemporaryFix max = new TemporaryFix();

			int count = 0;

			for (int i=0; i<events.size(); i++) {
				System.out.println("=====================================");
				System.out.println("Event counts: " + events.get(i).getCount());
				System.out.println("Event name: " + events.get(i).getEvent());
				System.out.println("Event time: " + events.get(i).getTime());
				System.out.println("=====================================");
				count += events.get(i).getCount();
			}

			max = events.get(0);
			for (int i=1; i<events.size(); i++) {
				if (events.get(i).getTime() > max.getTime()) {
					max = events.get(i);
				}
			}

			System.out.println("MAX: " + max.getTime());
			System.out.println("Total bills: " + count);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
