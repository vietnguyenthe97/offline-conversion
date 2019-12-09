package nhanhvn.data.helper;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import org.json.JSONObject;

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

	/**
	 * Convert json string into json object
	 * @param jsonString
	 * @return json object
	 */
	public static JSONObject convertStringToJson(String jsonString) {
		Gson gson = new Gson();
		JSONObject jsonObject = gson.fromJson(jsonString, JSONObject.class);
		return jsonObject;
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
}
