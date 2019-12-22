package shared.datahelper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
		ObjectMapper objectMapper = new ObjectMapper();
		String jsonString = "";
		try {
			jsonString = objectMapper.writeValueAsString(mapData);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return jsonString;
	}

	public static String convertMapToJsonStrings(Map<String, Object> mapData) {
		ObjectMapper objectMapper = new ObjectMapper();
		String jsonString = "";
		try {
			jsonString = objectMapper.writeValueAsString(mapData);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return jsonString;
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
		HashMap<String, Object> dataMap = new ObjectMapper().readValue(jsonString, HashMap.class);
		return dataMap;
	}

	/**
	 *
	 * @param dateTimeString input should be yyyy-MM-dd HH:mm:ss
	 * @return
	 */
	public static Date parseDateTimeString(String dateTimeString) {
		SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date localDateTime = null;
		try {
			localDateTime = dateTimeFormatter.parse(dateTimeString);
		} catch (ParseException e) {
			System.err.println("Wrong format input");
			e.printStackTrace();
		}
		return localDateTime;
	}

	/**
	 * https://www.baeldung.com/sha-256-hashing-java
	 * @param rawString
	 * @return
	 */
	public static String SHA256Hash(String rawString) throws NoSuchAlgorithmException {
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		byte[] encodedhash = digest.digest(
				rawString.getBytes(StandardCharsets.UTF_8));
		return bytesToHex(encodedhash);
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

	public static String formatMobileNumber(String phoneNumber) {
		return "84" + phoneNumber
			.replaceFirst("^0+(?!$)", "")
			.replaceAll("[^0-9]", "");
	}

	public static void main(String[] args) throws NoSuchAlgorithmException {
		System.out.println(DataHelper.SHA256Hash("ccacacacac"));
		System.out.println(DataHelper.formatMobileNumber("        0000000000945803932      "));
	}
}
