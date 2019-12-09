package nhanhvn.security.apistorage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

public class ApiHelper {
	public static ApiCredentials retrieveApiCredentials() {
		Yaml yamlLoader = new Yaml(new Constructor(ApiCredentials.class));
		InputStream input = null;
		try {
			input = new FileInputStream(new File("YmlFolder/api_information.yaml"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return yamlLoader.loadAs(input, ApiCredentials.class);
	}
}
