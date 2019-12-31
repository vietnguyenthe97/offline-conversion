package nhanhvn.security.apistorage;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class ApiHelper {
	public static ApiCredentials getApiCredentials() {
		Yaml yamlLoader = new Yaml(new Constructor(ApiCredentials.class));
		InputStream input = null;
		try {
			input = new FileInputStream(new File("resources/ApiConfigurations/api_information.yaml"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return yamlLoader.load(input);
	}

	public static void main(String[] args) {
		ApiHelper a = new ApiHelper();
		ApiCredentials b = a.getApiCredentials();
		System.out.println(b.getFacebookDetails().getOfflineEventSetId());
		System.out.println(b.getFacebookDetails().getUserAccessToken());
		System.out.println(b.getFacebookDetails().getVersion());
		System.out.println(b.getApiDetails().getApiSecretKey());
		System.out.println(b.getApiDetails().getApiUserName());
		System.out.println(b.getApiDetails().getVersion());
	}
}
