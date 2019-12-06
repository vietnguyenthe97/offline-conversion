package nhanhvn.data.helper;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DataHelper {
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
	
	public static String md5Hash(String ...values) {	
		String hashedString = "";
		String concatenatedString = Stream.of(values).collect(Collectors.joining());
			
		MessageDigest md5;
		try {
				md5 = MessageDigest.getInstance(("MD5"));
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
			md5.update(concatenatedString.getBytes(StandardCharsets.UTF_8));
			byte[] hashInBytes = md5.digest();
			
			StringBuilder stringBuilder = new StringBuilder(2*hashInBytes.length);
			for(byte b: hashInBytes) {
				stringBuilder.append(String.format("%02x", b&0xff));
			}
			hashedString = stringBuilder.toString();
		return hashedString;
	}
}
