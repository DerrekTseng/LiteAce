package lite.core.component;

import org.springframework.core.convert.converter.Converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RequestFileParameterConverter implements Converter<String, RequestFileParameter> {

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public RequestFileParameter convert(String source) {
		try {
			return new RequestFileParameter(objectMapper.readValue(source.toString(), new MapTypeReference()));
		} catch (JsonProcessingException e) {
			return new RequestFileParameter();	
		}
	}

}
