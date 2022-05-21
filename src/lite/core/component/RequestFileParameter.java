package lite.core.component;

import java.util.HashMap;
import java.util.Map;

public class RequestFileParameter extends HashMap<String, Object> {

	private static final long serialVersionUID = -7763545034894068673L;

	public RequestFileParameter() {

	}

	public RequestFileParameter(Map<String, Object> map) {
		this.putAll(map);
	}

}
