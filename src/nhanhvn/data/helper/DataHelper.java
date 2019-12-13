package nhanhvn.data.helper;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;

/**
 * Class to handle strings and json objects like conversion methods, hash generator, etc.
 */
public class DataHelper {
	/**
	 * Convert a map to json object
	 * @param mapData input map type <String, Object>
	 * @return json as String
	 */
	public static String convertMapToJsonString(Map<String, String> mapData) {
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

	public static Map<String, Object> convertJsonStringToMapObject(JsonObject json) throws IOException {
		String jsonString = json.toString();
		HashMap<String, Object> dataMap =
				new ObjectMapper().readValue(jsonString, HashMap.class);
		return dataMap;
	}
}
