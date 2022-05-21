package lite.core.component;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public class RequestFile {
	
	List<MultipartFile> files;
	RequestFileParameter data;

	public List<MultipartFile> getFiles() {
		return files;
	}

	public void setFiles(List<MultipartFile> files) {
		this.files = files;
	}

	public RequestFileParameter getData() {
		return data;
	}

	public void setData(RequestFileParameter data) {
		this.data = data;
	}

}
