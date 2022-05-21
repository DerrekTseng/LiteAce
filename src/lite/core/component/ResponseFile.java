package lite.core.component;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

/**
 * 客戶端下載檔案
 * 
 * @author DerrekTseng
 *
 */
public class ResponseFile {

	private static final int DEFAULT_BUFFER_SIZE = 1024 * 16;
	private static final long DEFAULT_EXPIRE_TIME = 604800000L;
	private static final String MULTIPART_BOUNDARY = "MULTIPART_BYTERANGES";

	/** 副檔名 Content-Type Mapping */
	private static final HashMap<String, String> MimeMappings = new HashMap<>();

	private String saveName = null;
	private File responsefile = null;
	private List<File> responsefiles = null;

	/** sendBytes 專用 */
	private byte[] _bytes = null;

	@Autowired
	private HttpServletRequest request;

	@Autowired
	private HttpServletResponse response;

	private boolean isMutiFile;

	private boolean isDownloading = false;

	private boolean allowDirectory = true;

	/**
	 * 壓縮並傳送資料夾，Header的檔案名稱會放入原始資料夾名稱。
	 * 
	 * @param sourcePath
	 * @throws IOException
	 */
	public static void sendDirectory(String sourcePath) throws IOException {
		ResponseFile responseFile = new ResponseFile(sourcePath);
		responseFile.setAllowDirectory(true);
		responseFile.doDownload();
	}

	/**
	 * 壓縮並傳送資料夾
	 * 
	 * @param sourcePath
	 * @throws IOException
	 */
	public static void sendDirectory(String saveName, String sourcePath) throws IOException {
		ResponseFile responseFile = new ResponseFile(saveName, sourcePath);
		responseFile.setAllowDirectory(true);
		responseFile.doDownload();
	}

	/**
	 * 傳送檔案，Header的檔案名稱會放入原始檔案名稱。
	 * 
	 * @param sourcePath
	 * @throws IOException
	 */
	public static void sendFile(String sourcePath) throws IOException {
		ResponseFile responseFile = new ResponseFile(sourcePath);
		responseFile.setAllowDirectory(false);
		responseFile.doDownload();
	}

	/**
	 * 傳送檔案
	 * 
	 * @param sourcePath
	 * @throws IOException
	 */
	public static void sendFile(String saveName, String sourcePath) throws IOException {
		ResponseFile responseFile = new ResponseFile(saveName, sourcePath);
		responseFile.setAllowDirectory(false);
		responseFile.doDownload();
	}

	/**
	 * 壓縮並傳送多個檔案
	 * 
	 * @param saveName
	 * @param sourcePaths
	 * @param allowDirectory 是否允許傳送資料夾
	 * @throws IOException
	 */
	public static void sendFiles(String saveName, String[] sourcePaths, boolean allowDirectory) throws IOException {
		ResponseFile responseFile = new ResponseFile(saveName, sourcePaths);
		responseFile.setAllowDirectory(allowDirectory);
		responseFile.doDownload();
	}

	/**
	 * 傳送 Bytes 檔案
	 * 
	 * @param saveName
	 * @param bytes
	 * @throws IOException
	 */
	public static void sendBytes(String saveName, byte[] bytes) throws IOException {
		ResponseFile responseFile = new ResponseFile(saveName, bytes);
		responseFile.doSendBytes();
	}

	private ResponseFile() {
		SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
	}

	/**
	 * If the given sourcePath is a directory, will compress as a ZIP file. If
	 * you're not sure if is a directory, but you don't want ZIP it anyway, please
	 * call setAllowDirectory(false).
	 * 
	 * @param sourcePath
	 * @param request
	 * @param response
	 */
	private ResponseFile(String sourcePath) {
		this();
		this.responsefile = new File(sourcePath);
		this.responsefiles = null;
		this.saveName = "";
		isMutiFile = false;
	}

	private ResponseFile(String fileName, byte[] bytes) {
		this();
		this.saveName = fileName;
		this._bytes = bytes;
	}

	/**
	 * If the given sourcePath is a directory, will compress as a ZIP file. If
	 * you're not sure if is a directory, but you don't want ZIP it anyway, please
	 * call setAllowDirectory(false).
	 * 
	 * @param saveName
	 * @param sourcePath
	 * @param request
	 * @param response
	 */
	private ResponseFile(String saveName, String sourcePath) {
		this();
		this.responsefile = new File(sourcePath);
		this.responsefiles = null;
		this.saveName = saveName;
		isMutiFile = false;
	}

	/**
	 * The given sourcePath allow directories and files, but if you don't want
	 * compress directory please call setAllowDirectory(false).
	 * 
	 * @param saveName
	 * @param sourcePaths
	 * @param request
	 * @param response
	 */
	private ResponseFile(String saveName, String[] sourcePaths) {
		this();
		this.responsefiles = new ArrayList<>();
		for (String sourcePath : sourcePaths) {
			responsefiles.add(new File(sourcePath));
		}
		this.responsefile = null;
		this.saveName = saveName;
		isMutiFile = true;
	}

	private void doDownload() throws IOException {
		if (!isDownloading) {
			isDownloading = true;
			if (isMutiFile) {
				downloadFiles();
			} else {
				if (responsefile.isFile() && responsefile.exists()) {
					downloadFile(responsefile.getAbsolutePath());
				} else if (responsefile.isDirectory() && responsefile.exists()) {
					downloadDirectory(responsefile.getAbsolutePath());
				}
			}

		}
	}

	private void doSendBytes() throws IOException {
		String contentType = getMimeType(saveName);
		if (contentType == null) {
			contentType = "application/octet-stream";
		}
		response.setContentType(contentType);
		response.setHeader("Content-Disposition", "attachment;filename=\"" + encodeFileName(saveName) + "\"");
		response.setHeader("Content-Length", String.valueOf(_bytes.length));
		ServletOutputStream servletOutputStream = response.getOutputStream();
		servletOutputStream.write(_bytes);
		servletOutputStream.flush();
		servletOutputStream.close();
		response.setStatus(HttpServletResponse.SC_OK);
	}

	private void setAllowDirectory(boolean b) {
		allowDirectory = b;
	}

	private void downloadFiles() throws IOException {
		String extension = ".zip";
		if (saveName.toLowerCase().endsWith(extension)) {
			extension = "";
		}
		response.setContentType("application/zip");
		response.setHeader("Content-Disposition", "attachment;filename=\"" + encodeFileName(saveName) + extension + "\"");
		ServletOutputStream servletOutputStream = response.getOutputStream();
		try (ZipOutputStream zs = new ZipOutputStream(servletOutputStream)) {
			zs.setLevel(ZipOutputStream.STORED);
			// 判斷 responsefiles 是否有包含資料夾
			if (responsefiles.stream().anyMatch(f -> f.isDirectory())) {
				// 有包含資料夾 照原本流程
				for (File f : responsefiles) {
					if (f.isDirectory()) {
						if (allowDirectory) {
							Path pp = f.toPath();
							for (Object path : Files.walk(pp).filter(path -> !Files.isDirectory(path)).toArray()) {
								ZipEntry zipEntry = new ZipEntry(pp.relativize((Path) path).toString());
								zs.putNextEntry(zipEntry);
								Files.copy((Path) path, zs);
								zs.closeEntry();
							}
						}
					} else {
						ZipEntry zipEntry = new ZipEntry(f.getName());
						zs.putNextEntry(zipEntry);
						Files.copy(f.toPath(), zs);
						zs.closeEntry();
					}

				}
			} else {
				// 只有檔案

				List<File> parents = new ArrayList<>();
				responsefiles.stream().collect(Collectors.groupingBy(File::getParentFile)).forEach((patent, files) -> {
					parents.add(patent);
				});

				// 將相同的 子目錄 歸類在底下

				for (int i = 0; i < parents.size(); i++) {
					final int index = i;
					List<File> sameParents = parents.stream().filter(parent -> parent.getParentFile().compareTo(parents.get(index)) == 0).collect(Collectors.toList());
					if (sameParents.size() > 0) {
						parents.removeAll(sameParents);
						i = 0;
					}
				}

				for (File parent : parents) {
					Path parentPath = parent.toPath();
					List<Path> filePaths = new ArrayList<>();
					Files.walk(parentPath).filter(filePath -> responsefiles.stream().anyMatch(f -> f.compareTo(filePath.toFile()) == 0)).forEach(filePath -> {
						filePaths.add(filePath);
					});

					for (Path filePath : filePaths) {
						ZipEntry zipEntry = new ZipEntry(parentPath.relativize(filePath).toString());
						zs.putNextEntry(zipEntry);
						Files.copy(filePath, zs);
						zs.closeEntry();
					}

				}

			}

			zs.finish();
		}
		servletOutputStream.flush();
		servletOutputStream.close();
		response.setStatus(HttpServletResponse.SC_OK);

	}

	private void downloadDirectory(String directoryPath) throws IOException {

		if (!allowDirectory) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}

		File file = new File(directoryPath);
		if (saveName == null || saveName.equals("")) {
			saveName = file.getName();
		}
		String extension = ".zip";
		if (saveName.toLowerCase().endsWith(extension)) {
			extension = "";
		}
		response.setContentType("application/zip");
		response.setHeader("Content-Disposition", "attachment;filename=\"" + encodeFileName(saveName) + extension + "\"");
		ServletOutputStream servletOutputStream = response.getOutputStream();
		packZip(file.getAbsolutePath(), servletOutputStream);
		servletOutputStream.flush();
		servletOutputStream.close();
		response.setStatus(HttpServletResponse.SC_OK);
	}

	private String encodeFileName(String fileName) throws IOException {
		return java.net.URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
	}

	private void packZip(String sourceDirPath, OutputStream outputStream) throws IOException {
		try (ZipOutputStream zs = new ZipOutputStream(outputStream)) {
			zs.setLevel(ZipOutputStream.STORED);
			Path pp = Paths.get(sourceDirPath);
			for (Object path : Files.walk(pp).filter(path -> !Files.isDirectory(path)).toArray()) {
				ZipEntry zipEntry = new ZipEntry(pp.relativize((Path) path).toString());
				zs.putNextEntry(zipEntry);
				Files.copy((Path) path, zs);
				zs.closeEntry();
			}
			zs.finish();
		}
	}

	private void downloadFile(String filePath) throws IOException {

		boolean content = request.getMethod().toLowerCase().equals("get");

		RandomAccessFile input = null;

		OutputStream output = null;

		try {
			File file = new File(filePath);

			if (saveName == null || saveName.equals("")) {
				saveName = file.getName();
			}

			long length = file.length();
			long lastModified = file.lastModified();
			String eTag = saveName + "_" + length + "_" + lastModified;
			long expires = System.currentTimeMillis() + DEFAULT_EXPIRE_TIME;

			String ifNoneMatch = request.getHeader("If-None-Match");
			if (ifNoneMatch != null && matches(ifNoneMatch, eTag)) {
				response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
				response.setHeader("ETag", eTag);
				response.setDateHeader("Expires", expires);
				return;
			}

			long ifModifiedSince = request.getDateHeader("If-Modified-Since");
			if (ifNoneMatch == null && ifModifiedSince != -1 && ifModifiedSince + 1000 > lastModified) {
				response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
				response.setHeader("ETag", eTag);
				response.setDateHeader("Expires", expires);
				return;
			}

			String ifMatch = request.getHeader("If-Match");
			if (ifMatch != null && !matches(ifMatch, eTag)) {
				response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED);
				return;
			}

			long ifUnmodifiedSince = request.getDateHeader("If-Unmodified-Since");
			if (ifUnmodifiedSince != -1 && ifUnmodifiedSince + 1000 <= lastModified) {
				response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED);
				return;
			}

			MediaRangeBean full = new MediaRangeBean(0, length - 1, length);
			List<MediaRangeBean> ranges = new ArrayList<MediaRangeBean>();

			String range = request.getHeader("Range");
			if (range != null) {
				if (!range.matches("^bytes=\\d*-\\d*(,\\d*-\\d*)*$")) {
					response.setHeader("Content-Range", "bytes */" + length); // Required in 416.
					response.sendError(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
					return;
				}

				String ifRange = request.getHeader("If-Range");
				if (ifRange != null && !ifRange.equals(eTag)) {
					try {
						long ifRangeTime = request.getDateHeader("If-Range"); // Throws IAE if invalid.
						if (ifRangeTime != -1 && ifRangeTime + 1000 < lastModified) {
							ranges.add(full);
						}
					} catch (IllegalArgumentException ignore) {
						ranges.add(full);
					}
				}

				if (ranges.isEmpty()) {
					for (String part : range.substring(6).split(",")) {
						long start = sublong(part, 0, part.indexOf("-"));
						long end = sublong(part, part.indexOf("-") + 1, part.length());
						if (start == -1) {
							start = length - end;
							end = length - 1;
						} else if (end == -1 || end > length - 1) {
							end = length - 1;
						}
						if (start > end) {
							response.setHeader("Content-Range", "bytes */" + length);
							response.sendError(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
							return;
						}
						ranges.add(new MediaRangeBean(start, end, length));
					}
				}
			}

			String contentType = getMimeType(saveName);
			boolean acceptsGzip = false;
			String disposition = "inline";
			if (contentType == null) {
				contentType = "application/octet-stream";
			}

			if (contentType.startsWith("text")) {
				String acceptEncoding = request.getHeader("Accept-Encoding");
				acceptsGzip = acceptEncoding != null && accepts(acceptEncoding, "gzip");
				contentType += ";charset=UTF-8";
			}

			else if (!contentType.startsWith("image")) {
				String accept = request.getHeader("Accept");
				disposition = accept != null && accepts(accept, contentType) ? "inline" : "attachment";
			}

			response.reset();
			response.setBufferSize(DEFAULT_BUFFER_SIZE);
			response.setHeader("Content-Disposition", disposition + ";filename=\"" + encodeFileName(saveName) + "\"");
			response.setHeader("Accept-Ranges", "bytes");
			response.setHeader("ETag", eTag);
			response.setDateHeader("Last-Modified", lastModified);
			response.setDateHeader("Expires", expires);

			input = new RandomAccessFile(file, "r");
			output = response.getOutputStream();

			if (ranges.isEmpty() || ranges.get(0) == full) {
				MediaRangeBean r = full;
				response.setContentType(contentType);
				if (content) {
					if (acceptsGzip) {
						response.setHeader("Content-Encoding", "gzip");
						output = new GZIPOutputStream(output, DEFAULT_BUFFER_SIZE);
					} else {
						response.setHeader("Content-Length", String.valueOf(r.length));
					}
					copy(input, output, r.start, r.length);
				}

			} else if (ranges.size() == 1) {
				MediaRangeBean r = ranges.get(0);
				response.setContentType(contentType);
				response.setHeader("Content-Range", "bytes " + r.start + "-" + r.end + "/" + r.total);
				response.setHeader("Content-Length", String.valueOf(r.length));
				response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);

				if (content) {
					copy(input, output, r.start, r.length);
				}

			} else {
				response.setContentType("multipart/byteranges; boundary=" + MULTIPART_BOUNDARY);
				response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
				if (content) {
					ServletOutputStream sos = (ServletOutputStream) output;
					for (MediaRangeBean r : ranges) {
						sos.println();
						sos.println("--" + MULTIPART_BOUNDARY);
						sos.println("Content-Type: " + contentType);
						sos.println("Content-Range: bytes " + r.start + "-" + r.end + "/" + r.total);
						copy(input, output, r.start, r.length);
					}
					sos.println();
					sos.println("--" + MULTIPART_BOUNDARY + "--");
				}
			}
		} catch (IOException e) {

		} catch (Exception e) {

		} finally {
			close(output);
			close(input);
		}
	}

	private boolean accepts(String acceptHeader, String toAccept) {
		String[] acceptValues = acceptHeader.split("\\s*(,|;)\\s*");
		Arrays.sort(acceptValues);
		return Arrays.binarySearch(acceptValues, toAccept) > -1 || Arrays.binarySearch(acceptValues, toAccept.replaceAll("/.*$", "/*")) > -1 || Arrays.binarySearch(acceptValues, "*/*") > -1;
	}

	private boolean matches(String matchHeader, String toMatch) {
		String[] matchValues = matchHeader.split("\\s*,\\s*");
		Arrays.sort(matchValues);
		return Arrays.binarySearch(matchValues, toMatch) > -1 || Arrays.binarySearch(matchValues, "*") > -1;
	}

	private long sublong(String value, int beginIndex, int endIndex) {
		String substring = value.substring(beginIndex, endIndex);
		return (substring.length() > 0) ? Long.parseLong(substring) : -1;
	}

	private void copy(RandomAccessFile input, OutputStream output, long start, long length) throws IOException {
		byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
		int read;

		if (input.length() == length) {
			while ((read = input.read(buffer)) > 0) {
				output.write(buffer, 0, read);
			}
		} else {
			input.seek(start);
			long toRead = length;

			while ((read = input.read(buffer)) > 0) {
				if ((toRead -= read) > 0) {
					output.write(buffer, 0, read);
				} else {
					output.write(buffer, 0, (int) toRead + read);
					break;
				}
			}
		}
	}

	private void close(Closeable resource) {
		if (resource != null) {
			try {
				resource.close();
			} catch (IOException ignore) {

			}
		}
	}

	private String getMimeType(String file) {
		if (file == null)
			return null;
		int period = file.lastIndexOf('.');
		if (period < 0)
			return null;
		String extension = file.substring(period + 1);
		if (extension.length() < 1)
			return null;

		return MimeMappings.get(extension.toLowerCase());
	}

	private class MediaRangeBean {
		private long start;
		private long end;
		private long length;
		private long total;

		private MediaRangeBean(long start, long end, long total) {
			this.start = start;
			this.end = end;
			this.length = end - start + 1;
			this.total = total;
		}
	}

	/** 副檔名 Content-Type Mapping */
	static {
		MimeMappings.put("123", "application/vnd.lotus-1-2-3");
		MimeMappings.put("3dml", "text/vnd.in3d.3dml");
		MimeMappings.put("3ds", "image/x-3ds");
		MimeMappings.put("3g2", "video/3gpp2");
		MimeMappings.put("3gp", "video/3gpp");
		MimeMappings.put("7z", "application/x-7z-compressed");
		MimeMappings.put("aab", "application/x-authorware-bin");
		MimeMappings.put("aac", "audio/x-aac");
		MimeMappings.put("aam", "application/x-authorware-map");
		MimeMappings.put("aas", "application/x-authorware-seg");
		MimeMappings.put("abs", "audio/x-mpeg");
		MimeMappings.put("abw", "application/x-abiword");
		MimeMappings.put("ac", "application/pkix-attr-cert");
		MimeMappings.put("acc", "application/vnd.americandynamics.acc");
		MimeMappings.put("ace", "application/x-ace-compressed");
		MimeMappings.put("acu", "application/vnd.acucobol");
		MimeMappings.put("acutc", "application/vnd.acucorp");
		MimeMappings.put("adp", "audio/adpcm");
		MimeMappings.put("aep", "application/vnd.audiograph");
		MimeMappings.put("afm", "application/x-font-type1");
		MimeMappings.put("afp", "application/vnd.ibm.modcap");
		MimeMappings.put("ahead", "application/vnd.ahead.space");
		MimeMappings.put("ai", "application/postscript");
		MimeMappings.put("aif", "audio/x-aiff");
		MimeMappings.put("aifc", "audio/x-aiff");
		MimeMappings.put("aiff", "audio/x-aiff");
		MimeMappings.put("aim", "application/x-aim");
		MimeMappings.put("air", "application/vnd.adobe.air-application-installer-package+zip");
		MimeMappings.put("ait", "application/vnd.dvb.ait");
		MimeMappings.put("ami", "application/vnd.amiga.ami");
		MimeMappings.put("anx", "application/annodex");
		MimeMappings.put("apk", "application/vnd.android.package-archive");
		MimeMappings.put("appcache", "text/cache-manifest");
		MimeMappings.put("application", "application/x-ms-application");
		MimeMappings.put("apr", "application/vnd.lotus-approach");
		MimeMappings.put("arc", "application/x-freearc");
		MimeMappings.put("art", "image/x-jg");
		MimeMappings.put("asc", "application/pgp-signature");
		MimeMappings.put("asf", "video/x-ms-asf");
		MimeMappings.put("asm", "text/x-asm");
		MimeMappings.put("aso", "application/vnd.accpac.simply.aso");
		MimeMappings.put("asx", "video/x-ms-asf");
		MimeMappings.put("atc", "application/vnd.acucorp");
		MimeMappings.put("atom", "application/atom+xml");
		MimeMappings.put("atomcat", "application/atomcat+xml");
		MimeMappings.put("atomsvc", "application/atomsvc+xml");
		MimeMappings.put("atx", "application/vnd.antix.game-component");
		MimeMappings.put("au", "audio/basic");
		MimeMappings.put("avi", "video/x-msvideo");
		MimeMappings.put("avx", "video/x-rad-screenplay");
		MimeMappings.put("aw", "application/applixware");
		MimeMappings.put("axa", "audio/annodex");
		MimeMappings.put("axv", "video/annodex");
		MimeMappings.put("azf", "application/vnd.airzip.filesecure.azf");
		MimeMappings.put("azs", "application/vnd.airzip.filesecure.azs");
		MimeMappings.put("azw", "application/vnd.amazon.ebook");
		MimeMappings.put("bat", "application/x-msdownload");
		MimeMappings.put("bcpio", "application/x-bcpio");
		MimeMappings.put("bdf", "application/x-font-bdf");
		MimeMappings.put("bdm", "application/vnd.syncml.dm+wbxml");
		MimeMappings.put("bed", "application/vnd.realvnc.bed");
		MimeMappings.put("bh2", "application/vnd.fujitsu.oasysprs");
		MimeMappings.put("bin", "application/octet-stream");
		MimeMappings.put("blb", "application/x-blorb");
		MimeMappings.put("blorb", "application/x-blorb");
		MimeMappings.put("bmi", "application/vnd.bmi");
		MimeMappings.put("bmp", "image/bmp");
		MimeMappings.put("body", "text/html");
		MimeMappings.put("book", "application/vnd.framemaker");
		MimeMappings.put("box", "application/vnd.previewsystems.box");
		MimeMappings.put("boz", "application/x-bzip2");
		MimeMappings.put("bpk", "application/octet-stream");
		MimeMappings.put("btif", "image/prs.btif");
		MimeMappings.put("bz", "application/x-bzip");
		MimeMappings.put("bz2", "application/x-bzip2");
		MimeMappings.put("c", "text/x-c");
		MimeMappings.put("c11amc", "application/vnd.cluetrust.cartomobile-config");
		MimeMappings.put("c11amz", "application/vnd.cluetrust.cartomobile-config-pkg");
		MimeMappings.put("c4d", "application/vnd.clonk.c4group");
		MimeMappings.put("c4f", "application/vnd.clonk.c4group");
		MimeMappings.put("c4g", "application/vnd.clonk.c4group");
		MimeMappings.put("c4p", "application/vnd.clonk.c4group");
		MimeMappings.put("c4u", "application/vnd.clonk.c4group");
		MimeMappings.put("cab", "application/vnd.ms-cab-compressed");
		MimeMappings.put("caf", "audio/x-caf");
		MimeMappings.put("cap", "application/vnd.tcpdump.pcap");
		MimeMappings.put("car", "application/vnd.curl.car");
		MimeMappings.put("cat", "application/vnd.ms-pki.seccat");
		MimeMappings.put("cb7", "application/x-cbr");
		MimeMappings.put("cba", "application/x-cbr");
		MimeMappings.put("cbr", "application/x-cbr");
		MimeMappings.put("cbt", "application/x-cbr");
		MimeMappings.put("cbz", "application/x-cbr");
		MimeMappings.put("cc", "text/x-c");
		MimeMappings.put("cct", "application/x-director");
		MimeMappings.put("ccxml", "application/ccxml+xml");
		MimeMappings.put("cdbcmsg", "application/vnd.contact.cmsg");
		MimeMappings.put("cdf", "application/x-cdf");
		MimeMappings.put("cdkey", "application/vnd.mediastation.cdkey");
		MimeMappings.put("cdmia", "application/cdmi-capability");
		MimeMappings.put("cdmic", "application/cdmi-container");
		MimeMappings.put("cdmid", "application/cdmi-domain");
		MimeMappings.put("cdmio", "application/cdmi-object");
		MimeMappings.put("cdmiq", "application/cdmi-queue");
		MimeMappings.put("cdx", "chemical/x-cdx");
		MimeMappings.put("cdxml", "application/vnd.chemdraw+xml");
		MimeMappings.put("cdy", "application/vnd.cinderella");
		MimeMappings.put("cer", "application/pkix-cert");
		MimeMappings.put("cfs", "application/x-cfs-compressed");
		MimeMappings.put("cgm", "image/cgm");
		MimeMappings.put("chat", "application/x-chat");
		MimeMappings.put("chm", "application/vnd.ms-htmlhelp");
		MimeMappings.put("chrt", "application/vnd.kde.kchart");
		MimeMappings.put("cif", "chemical/x-cif");
		MimeMappings.put("cii", "application/vnd.anser-web-certificate-issue-initiation");
		MimeMappings.put("cil", "application/vnd.ms-artgalry");
		MimeMappings.put("cla", "application/vnd.claymore");
		MimeMappings.put("class", "application/java");
		MimeMappings.put("clkk", "application/vnd.crick.clicker.keyboard");
		MimeMappings.put("clkp", "application/vnd.crick.clicker.palette");
		MimeMappings.put("clkt", "application/vnd.crick.clicker.template");
		MimeMappings.put("clkw", "application/vnd.crick.clicker.wordbank");
		MimeMappings.put("clkx", "application/vnd.crick.clicker");
		MimeMappings.put("clp", "application/x-msclip");
		MimeMappings.put("cmc", "application/vnd.cosmocaller");
		MimeMappings.put("cmdf", "chemical/x-cmdf");
		MimeMappings.put("cml", "chemical/x-cml");
		MimeMappings.put("cmp", "application/vnd.yellowriver-custom-menu");
		MimeMappings.put("cmx", "image/x-cmx");
		MimeMappings.put("cod", "application/vnd.rim.cod");
		MimeMappings.put("com", "application/x-msdownload");
		MimeMappings.put("conf", "text/plain");
		MimeMappings.put("cpio", "application/x-cpio");
		MimeMappings.put("cpp", "text/x-c");
		MimeMappings.put("cpt", "application/mac-compactpro");
		MimeMappings.put("crd", "application/x-mscardfile");
		MimeMappings.put("crl", "application/pkix-crl");
		MimeMappings.put("crt", "application/x-x509-ca-cert");
		MimeMappings.put("cryptonote", "application/vnd.rig.cryptonote");
		MimeMappings.put("csh", "application/x-csh");
		MimeMappings.put("csml", "chemical/x-csml");
		MimeMappings.put("csp", "application/vnd.commonspace");
		MimeMappings.put("css", "text/css");
		MimeMappings.put("cst", "application/x-director");
		MimeMappings.put("csv", "text/csv");
		MimeMappings.put("cu", "application/cu-seeme");
		MimeMappings.put("curl", "text/vnd.curl");
		MimeMappings.put("cww", "application/prs.cww");
		MimeMappings.put("cxt", "application/x-director");
		MimeMappings.put("cxx", "text/x-c");
		MimeMappings.put("dae", "model/vnd.collada+xml");
		MimeMappings.put("daf", "application/vnd.mobius.daf");
		MimeMappings.put("dart", "application/vnd.dart");
		MimeMappings.put("dataless", "application/vnd.fdsn.seed");
		MimeMappings.put("davmount", "application/davmount+xml");
		MimeMappings.put("dbk", "application/docbook+xml");
		MimeMappings.put("dcr", "application/x-director");
		MimeMappings.put("dcurl", "text/vnd.curl.dcurl");
		MimeMappings.put("dd2", "application/vnd.oma.dd2+xml");
		MimeMappings.put("ddd", "application/vnd.fujixerox.ddd");
		MimeMappings.put("deb", "application/x-debian-package");
		MimeMappings.put("def", "text/plain");
		MimeMappings.put("deploy", "application/octet-stream");
		MimeMappings.put("der", "application/x-x509-ca-cert");
		MimeMappings.put("dfac", "application/vnd.dreamfactory");
		MimeMappings.put("dgc", "application/x-dgc-compressed");
		MimeMappings.put("dib", "image/bmp");
		MimeMappings.put("dic", "text/x-c");
		MimeMappings.put("dir", "application/x-director");
		MimeMappings.put("dis", "application/vnd.mobius.dis");
		MimeMappings.put("dist", "application/octet-stream");
		MimeMappings.put("distz", "application/octet-stream");
		MimeMappings.put("djv", "image/vnd.djvu");
		MimeMappings.put("djvu", "image/vnd.djvu");
		MimeMappings.put("dll", "application/x-msdownload");
		MimeMappings.put("dmg", "application/x-apple-diskimage");
		MimeMappings.put("dmp", "application/vnd.tcpdump.pcap");
		MimeMappings.put("dms", "application/octet-stream");
		MimeMappings.put("dna", "application/vnd.dna");
		MimeMappings.put("doc", "application/msword");
		MimeMappings.put("docm", "application/vnd.ms-word.document.macroenabled.12");
		MimeMappings.put("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
		MimeMappings.put("dot", "application/msword");
		MimeMappings.put("dotm", "application/vnd.ms-word.template.macroenabled.12");
		MimeMappings.put("dotx", "application/vnd.openxmlformats-officedocument.wordprocessingml.template");
		MimeMappings.put("dp", "application/vnd.osgi.dp");
		MimeMappings.put("dpg", "application/vnd.dpgraph");
		MimeMappings.put("dra", "audio/vnd.dra");
		MimeMappings.put("dsc", "text/prs.lines.tag");
		MimeMappings.put("dssc", "application/dssc+der");
		MimeMappings.put("dtb", "application/x-dtbook+xml");
		MimeMappings.put("dtd", "application/xml-dtd");
		MimeMappings.put("dts", "audio/vnd.dts");
		MimeMappings.put("dtshd", "audio/vnd.dts.hd");
		MimeMappings.put("dump", "application/octet-stream");
		MimeMappings.put("dv", "video/x-dv");
		MimeMappings.put("dvb", "video/vnd.dvb.file");
		MimeMappings.put("dvi", "application/x-dvi");
		MimeMappings.put("dwf", "model/vnd.dwf");
		MimeMappings.put("dwg", "image/vnd.dwg");
		MimeMappings.put("dxf", "image/vnd.dxf");
		MimeMappings.put("dxp", "application/vnd.spotfire.dxp");
		MimeMappings.put("dxr", "application/x-director");
		MimeMappings.put("ecelp4800", "audio/vnd.nuera.ecelp4800");
		MimeMappings.put("ecelp7470", "audio/vnd.nuera.ecelp7470");
		MimeMappings.put("ecelp9600", "audio/vnd.nuera.ecelp9600");
		MimeMappings.put("ecma", "application/ecmascript");
		MimeMappings.put("edm", "application/vnd.novadigm.edm");
		MimeMappings.put("edx", "application/vnd.novadigm.edx");
		MimeMappings.put("efif", "application/vnd.picsel");
		MimeMappings.put("ei6", "application/vnd.pg.osasli");
		MimeMappings.put("elc", "application/octet-stream");
		MimeMappings.put("emf", "application/x-msmetafile");
		MimeMappings.put("eml", "message/rfc822");
		MimeMappings.put("emma", "application/emma+xml");
		MimeMappings.put("emz", "application/x-msmetafile");
		MimeMappings.put("eol", "audio/vnd.digital-winds");
		MimeMappings.put("eot", "application/vnd.ms-fontobject");
		MimeMappings.put("eps", "application/postscript");
		MimeMappings.put("epub", "application/epub+zip");
		MimeMappings.put("es3", "application/vnd.eszigno3+xml");
		MimeMappings.put("esa", "application/vnd.osgi.subsystem");
		MimeMappings.put("esf", "application/vnd.epson.esf");
		MimeMappings.put("et3", "application/vnd.eszigno3+xml");
		MimeMappings.put("etx", "text/x-setext");
		MimeMappings.put("eva", "application/x-eva");
		MimeMappings.put("evy", "application/x-envoy");
		MimeMappings.put("exe", "application/octet-stream");
		MimeMappings.put("exi", "application/exi");
		MimeMappings.put("ext", "application/vnd.novadigm.ext");
		MimeMappings.put("ez", "application/andrew-inset");
		MimeMappings.put("ez2", "application/vnd.ezpix-album");
		MimeMappings.put("ez3", "application/vnd.ezpix-package");
		MimeMappings.put("f", "text/x-fortran");
		MimeMappings.put("f4v", "video/x-f4v");
		MimeMappings.put("f77", "text/x-fortran");
		MimeMappings.put("f90", "text/x-fortran");
		MimeMappings.put("fbs", "image/vnd.fastbidsheet");
		MimeMappings.put("fcdt", "application/vnd.adobe.formscentral.fcdt");
		MimeMappings.put("fcs", "application/vnd.isac.fcs");
		MimeMappings.put("fdf", "application/vnd.fdf");
		MimeMappings.put("fe_launch", "application/vnd.denovo.fcselayout-link");
		MimeMappings.put("fg5", "application/vnd.fujitsu.oasysgp");
		MimeMappings.put("fgd", "application/x-director");
		MimeMappings.put("fh", "image/x-freehand");
		MimeMappings.put("fh4", "image/x-freehand");
		MimeMappings.put("fh5", "image/x-freehand");
		MimeMappings.put("fh7", "image/x-freehand");
		MimeMappings.put("fhc", "image/x-freehand");
		MimeMappings.put("fig", "application/x-xfig");
		MimeMappings.put("flac", "audio/flac");
		MimeMappings.put("fli", "video/x-fli");
		MimeMappings.put("flo", "application/vnd.micrografx.flo");
		MimeMappings.put("flv", "video/x-flv");
		MimeMappings.put("flw", "application/vnd.kde.kivio");
		MimeMappings.put("flx", "text/vnd.fmi.flexstor");
		MimeMappings.put("fly", "text/vnd.fly");
		MimeMappings.put("fm", "application/vnd.framemaker");
		MimeMappings.put("fnc", "application/vnd.frogans.fnc");
		MimeMappings.put("for", "text/x-fortran");
		MimeMappings.put("fpx", "image/vnd.fpx");
		MimeMappings.put("frame", "application/vnd.framemaker");
		MimeMappings.put("fsc", "application/vnd.fsc.weblaunch");
		MimeMappings.put("fst", "image/vnd.fst");
		MimeMappings.put("ftc", "application/vnd.fluxtime.clip");
		MimeMappings.put("fti", "application/vnd.anser-web-funds-transfer-initiation");
		MimeMappings.put("fvt", "video/vnd.fvt");
		MimeMappings.put("fxp", "application/vnd.adobe.fxp");
		MimeMappings.put("fxpl", "application/vnd.adobe.fxp");
		MimeMappings.put("fzs", "application/vnd.fuzzysheet");
		MimeMappings.put("g2w", "application/vnd.geoplan");
		MimeMappings.put("g3", "image/g3fax");
		MimeMappings.put("g3w", "application/vnd.geospace");
		MimeMappings.put("gac", "application/vnd.groove-account");
		MimeMappings.put("gam", "application/x-tads");
		MimeMappings.put("gbr", "application/rpki-ghostbusters");
		MimeMappings.put("gca", "application/x-gca-compressed");
		MimeMappings.put("gdl", "model/vnd.gdl");
		MimeMappings.put("geo", "application/vnd.dynageo");
		MimeMappings.put("gex", "application/vnd.geometry-explorer");
		MimeMappings.put("ggb", "application/vnd.geogebra.file");
		MimeMappings.put("ggt", "application/vnd.geogebra.tool");
		MimeMappings.put("ghf", "application/vnd.groove-help");
		MimeMappings.put("gif", "image/gif");
		MimeMappings.put("gim", "application/vnd.groove-identity-message");
		MimeMappings.put("gml", "application/gml+xml");
		MimeMappings.put("gmx", "application/vnd.gmx");
		MimeMappings.put("gnumeric", "application/x-gnumeric");
		MimeMappings.put("gph", "application/vnd.flographit");
		MimeMappings.put("gpx", "application/gpx+xml");
		MimeMappings.put("gqf", "application/vnd.grafeq");
		MimeMappings.put("gqs", "application/vnd.grafeq");
		MimeMappings.put("gram", "application/srgs");
		MimeMappings.put("gramps", "application/x-gramps-xml");
		MimeMappings.put("gre", "application/vnd.geometry-explorer");
		MimeMappings.put("grv", "application/vnd.groove-injector");
		MimeMappings.put("grxml", "application/srgs+xml");
		MimeMappings.put("gsf", "application/x-font-ghostscript");
		MimeMappings.put("gtar", "application/x-gtar");
		MimeMappings.put("gtm", "application/vnd.groove-tool-message");
		MimeMappings.put("gtw", "model/vnd.gtw");
		MimeMappings.put("gv", "text/vnd.graphviz");
		MimeMappings.put("gxf", "application/gxf");
		MimeMappings.put("gxt", "application/vnd.geonext");
		MimeMappings.put("gz", "application/x-gzip");
		MimeMappings.put("h", "text/x-c");
		MimeMappings.put("h261", "video/h261");
		MimeMappings.put("h263", "video/h263");
		MimeMappings.put("h264", "video/h264");
		MimeMappings.put("hal", "application/vnd.hal+xml");
		MimeMappings.put("hbci", "application/vnd.hbci");
		MimeMappings.put("hdf", "application/x-hdf");
		MimeMappings.put("hh", "text/x-c");
		MimeMappings.put("hlp", "application/winhlp");
		MimeMappings.put("hpgl", "application/vnd.hp-hpgl");
		MimeMappings.put("hpid", "application/vnd.hp-hpid");
		MimeMappings.put("hps", "application/vnd.hp-hps");
		MimeMappings.put("hqx", "application/mac-binhex40");
		MimeMappings.put("htc", "text/x-component");
		MimeMappings.put("htke", "application/vnd.kenameaapp");
		MimeMappings.put("htm", "text/html");
		MimeMappings.put("html", "text/html");
		MimeMappings.put("hvd", "application/vnd.yamaha.hv-dic");
		MimeMappings.put("hvp", "application/vnd.yamaha.hv-voice");
		MimeMappings.put("hvs", "application/vnd.yamaha.hv-script");
		MimeMappings.put("i2g", "application/vnd.intergeo");
		MimeMappings.put("icc", "application/vnd.iccprofile");
		MimeMappings.put("ice", "x-conference/x-cooltalk");
		MimeMappings.put("icm", "application/vnd.iccprofile");
		MimeMappings.put("ico", "image/x-icon");
		MimeMappings.put("ics", "text/calendar");
		MimeMappings.put("ief", "image/ief");
		MimeMappings.put("ifb", "text/calendar");
		MimeMappings.put("ifm", "application/vnd.shana.informed.formdata");
		MimeMappings.put("iges", "model/iges");
		MimeMappings.put("igl", "application/vnd.igloader");
		MimeMappings.put("igm", "application/vnd.insors.igm");
		MimeMappings.put("igs", "model/iges");
		MimeMappings.put("igx", "application/vnd.micrografx.igx");
		MimeMappings.put("iif", "application/vnd.shana.informed.interchange");
		MimeMappings.put("imp", "application/vnd.accpac.simply.imp");
		MimeMappings.put("ims", "application/vnd.ms-ims");
		MimeMappings.put("in", "text/plain");
		MimeMappings.put("ink", "application/inkml+xml");
		MimeMappings.put("inkml", "application/inkml+xml");
		MimeMappings.put("install", "application/x-install-instructions");
		MimeMappings.put("iota", "application/vnd.astraea-software.iota");
		MimeMappings.put("ipfix", "application/ipfix");
		MimeMappings.put("ipk", "application/vnd.shana.informed.package");
		MimeMappings.put("irm", "application/vnd.ibm.rights-management");
		MimeMappings.put("irp", "application/vnd.irepository.package+xml");
		MimeMappings.put("iso", "application/x-iso9660-image");
		MimeMappings.put("itp", "application/vnd.shana.informed.formtemplate");
		MimeMappings.put("ivp", "application/vnd.immervision-ivp");
		MimeMappings.put("ivu", "application/vnd.immervision-ivu");
		MimeMappings.put("jad", "text/vnd.sun.j2me.app-descriptor");
		MimeMappings.put("jam", "application/vnd.jam");
		MimeMappings.put("jar", "application/java-archive");
		MimeMappings.put("java", "text/x-java-source");
		MimeMappings.put("jisp", "application/vnd.jisp");
		MimeMappings.put("jlt", "application/vnd.hp-jlyt");
		MimeMappings.put("jnlp", "application/x-java-jnlp-file");
		MimeMappings.put("joda", "application/vnd.joost.joda-archive");
		MimeMappings.put("jpe", "image/jpeg");
		MimeMappings.put("jpeg", "image/jpeg");
		MimeMappings.put("jpg", "image/jpeg");
		MimeMappings.put("jpgm", "video/jpm");
		MimeMappings.put("jpgv", "video/jpeg");
		MimeMappings.put("jpm", "video/jpm");
		MimeMappings.put("js", "application/javascript");
		MimeMappings.put("jsf", "text/plain");
		MimeMappings.put("json", "application/json");
		MimeMappings.put("jsonml", "application/jsonml+json");
		MimeMappings.put("jspf", "text/plain");
		MimeMappings.put("kar", "audio/midi");
		MimeMappings.put("karbon", "application/vnd.kde.karbon");
		MimeMappings.put("kfo", "application/vnd.kde.kformula");
		MimeMappings.put("kia", "application/vnd.kidspiration");
		MimeMappings.put("kml", "application/vnd.google-earth.kml+xml");
		MimeMappings.put("kmz", "application/vnd.google-earth.kmz");
		MimeMappings.put("kne", "application/vnd.kinar");
		MimeMappings.put("knp", "application/vnd.kinar");
		MimeMappings.put("kon", "application/vnd.kde.kontour");
		MimeMappings.put("kpr", "application/vnd.kde.kpresenter");
		MimeMappings.put("kpt", "application/vnd.kde.kpresenter");
		MimeMappings.put("kpxx", "application/vnd.ds-keypoint");
		MimeMappings.put("ksp", "application/vnd.kde.kspread");
		MimeMappings.put("ktr", "application/vnd.kahootz");
		MimeMappings.put("ktx", "image/ktx");
		MimeMappings.put("ktz", "application/vnd.kahootz");
		MimeMappings.put("kwd", "application/vnd.kde.kword");
		MimeMappings.put("kwt", "application/vnd.kde.kword");
		MimeMappings.put("lasxml", "application/vnd.las.las+xml");
		MimeMappings.put("latex", "application/x-latex");
		MimeMappings.put("lbd", "application/vnd.llamagraphics.life-balance.desktop");
		MimeMappings.put("lbe", "application/vnd.llamagraphics.life-balance.exchange+xml");
		MimeMappings.put("les", "application/vnd.hhe.lesson-player");
		MimeMappings.put("lha", "application/x-lzh-compressed");
		MimeMappings.put("link66", "application/vnd.route66.link66+xml");
		MimeMappings.put("list", "text/plain");
		MimeMappings.put("list3820", "application/vnd.ibm.modcap");
		MimeMappings.put("listafp", "application/vnd.ibm.modcap");
		MimeMappings.put("lnk", "application/x-ms-shortcut");
		MimeMappings.put("log", "text/plain");
		MimeMappings.put("lostxml", "application/lost+xml");
		MimeMappings.put("lrf", "application/octet-stream");
		MimeMappings.put("lrm", "application/vnd.ms-lrm");
		MimeMappings.put("ltf", "application/vnd.frogans.ltf");
		MimeMappings.put("lvp", "audio/vnd.lucent.voice");
		MimeMappings.put("lwp", "application/vnd.lotus-wordpro");
		MimeMappings.put("lzh", "application/x-lzh-compressed");
		MimeMappings.put("m13", "application/x-msmediaview");
		MimeMappings.put("m14", "application/x-msmediaview");
		MimeMappings.put("m1v", "video/mpeg");
		MimeMappings.put("m21", "application/mp21");
		MimeMappings.put("m2a", "audio/mpeg");
		MimeMappings.put("m2v", "video/mpeg");
		MimeMappings.put("m3a", "audio/mpeg");
		MimeMappings.put("m3u", "audio/x-mpegurl");
		MimeMappings.put("m3u8", "application/vnd.apple.mpegurl");
		MimeMappings.put("m4a", "audio/mp4");
		MimeMappings.put("m4b", "audio/mp4");
		MimeMappings.put("m4r", "audio/mp4");
		MimeMappings.put("m4u", "video/vnd.mpegurl");
		MimeMappings.put("m4v", "video/mp4");
		MimeMappings.put("ma", "application/mathematica");
		MimeMappings.put("mac", "image/x-macpaint");
		MimeMappings.put("mads", "application/mads+xml");
		MimeMappings.put("mag", "application/vnd.ecowin.chart");
		MimeMappings.put("maker", "application/vnd.framemaker");
		MimeMappings.put("man", "text/troff");
		MimeMappings.put("mar", "application/octet-stream");
		MimeMappings.put("mathml", "application/mathml+xml");
		MimeMappings.put("mb", "application/mathematica");
		MimeMappings.put("mbk", "application/vnd.mobius.mbk");
		MimeMappings.put("mbox", "application/mbox");
		MimeMappings.put("mc1", "application/vnd.medcalcdata");
		MimeMappings.put("mcd", "application/vnd.mcd");
		MimeMappings.put("mcurl", "text/vnd.curl.mcurl");
		MimeMappings.put("mdb", "application/x-msaccess");
		MimeMappings.put("mdi", "image/vnd.ms-modi");
		MimeMappings.put("me", "text/troff");
		MimeMappings.put("mesh", "model/mesh");
		MimeMappings.put("meta4", "application/metalink4+xml");
		MimeMappings.put("metalink", "application/metalink+xml");
		MimeMappings.put("mets", "application/mets+xml");
		MimeMappings.put("mfm", "application/vnd.mfmp");
		MimeMappings.put("mft", "application/rpki-manifest");
		MimeMappings.put("mgp", "application/vnd.osgeo.mapguide.package");
		MimeMappings.put("mgz", "application/vnd.proteus.magazine");
		MimeMappings.put("mid", "audio/midi");
		MimeMappings.put("midi", "audio/midi");
		MimeMappings.put("mie", "application/x-mie");
		MimeMappings.put("mif", "application/x-mif");
		MimeMappings.put("mime", "message/rfc822");
		MimeMappings.put("mj2", "video/mj2");
		MimeMappings.put("mjp2", "video/mj2");
		MimeMappings.put("mk3d", "video/x-matroska");
		MimeMappings.put("mka", "audio/x-matroska");
		MimeMappings.put("mks", "video/x-matroska");
		MimeMappings.put("mkv", "video/x-matroska");
		MimeMappings.put("mlp", "application/vnd.dolby.mlp");
		MimeMappings.put("mmd", "application/vnd.chipnuts.karaoke-mmd");
		MimeMappings.put("mmf", "application/vnd.smaf");
		MimeMappings.put("mmr", "image/vnd.fujixerox.edmics-mmr");
		MimeMappings.put("mng", "video/x-mng");
		MimeMappings.put("mny", "application/x-msmoney");
		MimeMappings.put("mobi", "application/x-mobipocket-ebook");
		MimeMappings.put("mods", "application/mods+xml");
		MimeMappings.put("mov", "video/quicktime");
		MimeMappings.put("movie", "video/x-sgi-movie");
		MimeMappings.put("mp1", "audio/mpeg");
		MimeMappings.put("mp2", "audio/mpeg");
		MimeMappings.put("mp21", "application/mp21");
		MimeMappings.put("mp2a", "audio/mpeg");
		MimeMappings.put("mp3", "audio/mpeg");
		MimeMappings.put("mp4", "video/mp4");
		MimeMappings.put("mp4a", "audio/mp4");
		MimeMappings.put("mp4s", "application/mp4");
		MimeMappings.put("mp4v", "video/mp4");
		MimeMappings.put("mpa", "audio/mpeg");
		MimeMappings.put("mpc", "application/vnd.mophun.certificate");
		MimeMappings.put("mpe", "video/mpeg");
		MimeMappings.put("mpeg", "video/mpeg");
		MimeMappings.put("mpega", "audio/x-mpeg");
		MimeMappings.put("mpg", "video/mpeg");
		MimeMappings.put("mpg4", "video/mp4");
		MimeMappings.put("mpga", "audio/mpeg");
		MimeMappings.put("mpkg", "application/vnd.apple.installer+xml");
		MimeMappings.put("mpm", "application/vnd.blueice.multipass");
		MimeMappings.put("mpn", "application/vnd.mophun.application");
		MimeMappings.put("mpp", "application/vnd.ms-project");
		MimeMappings.put("mpt", "application/vnd.ms-project");
		MimeMappings.put("mpv2", "video/mpeg2");
		MimeMappings.put("mpy", "application/vnd.ibm.minipay");
		MimeMappings.put("mqy", "application/vnd.mobius.mqy");
		MimeMappings.put("mrc", "application/marc");
		MimeMappings.put("mrcx", "application/marcxml+xml");
		MimeMappings.put("ms", "text/troff");
		MimeMappings.put("mscml", "application/mediaservercontrol+xml");
		MimeMappings.put("mseed", "application/vnd.fdsn.mseed");
		MimeMappings.put("mseq", "application/vnd.mseq");
		MimeMappings.put("msf", "application/vnd.epson.msf");
		MimeMappings.put("msh", "model/mesh");
		MimeMappings.put("msi", "application/x-msdownload");
		MimeMappings.put("msl", "application/vnd.mobius.msl");
		MimeMappings.put("msty", "application/vnd.muvee.style");
		MimeMappings.put("mts", "model/vnd.mts");
		MimeMappings.put("mus", "application/vnd.musician");
		MimeMappings.put("musicxml", "application/vnd.recordare.musicxml+xml");
		MimeMappings.put("mvb", "application/x-msmediaview");
		MimeMappings.put("mwf", "application/vnd.mfer");
		MimeMappings.put("mxf", "application/mxf");
		MimeMappings.put("mxl", "application/vnd.recordare.musicxml");
		MimeMappings.put("mxml", "application/xv+xml");
		MimeMappings.put("mxs", "application/vnd.triscape.mxs");
		MimeMappings.put("mxu", "video/vnd.mpegurl");
		MimeMappings.put("n-gage", "application/vnd.nokia.n-gage.symbian.install");
		MimeMappings.put("n3", "text/n3");
		MimeMappings.put("nb", "application/mathematica");
		MimeMappings.put("nbp", "application/vnd.wolfram.player");
		MimeMappings.put("nc", "application/x-netcdf");
		MimeMappings.put("ncx", "application/x-dtbncx+xml");
		MimeMappings.put("nfo", "text/x-nfo");
		MimeMappings.put("ngdat", "application/vnd.nokia.n-gage.data");
		MimeMappings.put("nitf", "application/vnd.nitf");
		MimeMappings.put("nlu", "application/vnd.neurolanguage.nlu");
		MimeMappings.put("nml", "application/vnd.enliven");
		MimeMappings.put("nnd", "application/vnd.noblenet-directory");
		MimeMappings.put("nns", "application/vnd.noblenet-sealer");
		MimeMappings.put("nnw", "application/vnd.noblenet-web");
		MimeMappings.put("npx", "image/vnd.net-fpx");
		MimeMappings.put("nsc", "application/x-conference");
		MimeMappings.put("nsf", "application/vnd.lotus-notes");
		MimeMappings.put("ntf", "application/vnd.nitf");
		MimeMappings.put("nzb", "application/x-nzb");
		MimeMappings.put("oa2", "application/vnd.fujitsu.oasys2");
		MimeMappings.put("oa3", "application/vnd.fujitsu.oasys3");
		MimeMappings.put("oas", "application/vnd.fujitsu.oasys");
		MimeMappings.put("obd", "application/x-msbinder");
		MimeMappings.put("obj", "application/x-tgif");
		MimeMappings.put("oda", "application/oda");
		MimeMappings.put("odb", "application/vnd.oasis.opendocument.database");
		MimeMappings.put("odc", "application/vnd.oasis.opendocument.chart");
		MimeMappings.put("odf", "application/vnd.oasis.opendocument.formula");
		MimeMappings.put("odft", "application/vnd.oasis.opendocument.formula-template");
		MimeMappings.put("odg", "application/vnd.oasis.opendocument.graphics");
		MimeMappings.put("odi", "application/vnd.oasis.opendocument.image");
		MimeMappings.put("odm", "application/vnd.oasis.opendocument.text-master");
		MimeMappings.put("odp", "application/vnd.oasis.opendocument.presentation");
		MimeMappings.put("ods", "application/vnd.oasis.opendocument.spreadsheet");
		MimeMappings.put("odt", "application/vnd.oasis.opendocument.text");
		MimeMappings.put("oga", "audio/ogg");
		MimeMappings.put("ogg", "audio/ogg");
		MimeMappings.put("ogv", "video/ogg");
		MimeMappings.put("ogx", "application/ogg");
		MimeMappings.put("omdoc", "application/omdoc+xml");
		MimeMappings.put("onepkg", "application/onenote");
		MimeMappings.put("onetmp", "application/onenote");
		MimeMappings.put("onetoc", "application/onenote");
		MimeMappings.put("onetoc2", "application/onenote");
		MimeMappings.put("opf", "application/oebps-package+xml");
		MimeMappings.put("opml", "text/x-opml");
		MimeMappings.put("oprc", "application/vnd.palm");
		MimeMappings.put("org", "application/vnd.lotus-organizer");
		MimeMappings.put("osf", "application/vnd.yamaha.openscoreformat");
		MimeMappings.put("osfpvg", "application/vnd.yamaha.openscoreformat.osfpvg+xml");
		MimeMappings.put("otc", "application/vnd.oasis.opendocument.chart-template");
		MimeMappings.put("otf", "font/otf");
		MimeMappings.put("otg", "application/vnd.oasis.opendocument.graphics-template");
		MimeMappings.put("oth", "application/vnd.oasis.opendocument.text-web");
		MimeMappings.put("oti", "application/vnd.oasis.opendocument.image-template");
		MimeMappings.put("otp", "application/vnd.oasis.opendocument.presentation-template");
		MimeMappings.put("ots", "application/vnd.oasis.opendocument.spreadsheet-template");
		MimeMappings.put("ott", "application/vnd.oasis.opendocument.text-template");
		MimeMappings.put("oxps", "application/oxps");
		MimeMappings.put("oxt", "application/vnd.openofficeorg.extension");
		MimeMappings.put("p", "text/x-pascal");
		MimeMappings.put("p10", "application/pkcs10");
		MimeMappings.put("p12", "application/x-pkcs12");
		MimeMappings.put("p7b", "application/x-pkcs7-certificates");
		MimeMappings.put("p7c", "application/pkcs7-mime");
		MimeMappings.put("p7m", "application/pkcs7-mime");
		MimeMappings.put("p7r", "application/x-pkcs7-certreqresp");
		MimeMappings.put("p7s", "application/pkcs7-signature");
		MimeMappings.put("p8", "application/pkcs8");
		MimeMappings.put("pas", "text/x-pascal");
		MimeMappings.put("paw", "application/vnd.pawaafile");
		MimeMappings.put("pbd", "application/vnd.powerbuilder6");
		MimeMappings.put("pbm", "image/x-portable-bitmap");
		MimeMappings.put("pcap", "application/vnd.tcpdump.pcap");
		MimeMappings.put("pcf", "application/x-font-pcf");
		MimeMappings.put("pcl", "application/vnd.hp-pcl");
		MimeMappings.put("pclxl", "application/vnd.hp-pclxl");
		MimeMappings.put("pct", "image/pict");
		MimeMappings.put("pcurl", "application/vnd.curl.pcurl");
		MimeMappings.put("pcx", "image/x-pcx");
		MimeMappings.put("pdb", "application/vnd.palm");
		MimeMappings.put("pdf", "application/pdf");
		MimeMappings.put("pfa", "application/x-font-type1");
		MimeMappings.put("pfb", "application/x-font-type1");
		MimeMappings.put("pfm", "application/x-font-type1");
		MimeMappings.put("pfr", "application/font-tdpfr");
		MimeMappings.put("pfx", "application/x-pkcs12");
		MimeMappings.put("pgm", "image/x-portable-graymap");
		MimeMappings.put("pgn", "application/x-chess-pgn");
		MimeMappings.put("pgp", "application/pgp-encrypted");
		MimeMappings.put("pic", "image/pict");
		MimeMappings.put("pict", "image/pict");
		MimeMappings.put("pkg", "application/octet-stream");
		MimeMappings.put("pki", "application/pkixcmp");
		MimeMappings.put("pkipath", "application/pkix-pkipath");
		MimeMappings.put("plb", "application/vnd.3gpp.pic-bw-large");
		MimeMappings.put("plc", "application/vnd.mobius.plc");
		MimeMappings.put("plf", "application/vnd.pocketlearn");
		MimeMappings.put("pls", "audio/x-scpls");
		MimeMappings.put("pml", "application/vnd.ctc-posml");
		MimeMappings.put("png", "image/png");
		MimeMappings.put("pnm", "image/x-portable-anymap");
		MimeMappings.put("pnt", "image/x-macpaint");
		MimeMappings.put("portpkg", "application/vnd.macports.portpkg");
		MimeMappings.put("pot", "application/vnd.ms-powerpoint");
		MimeMappings.put("potm", "application/vnd.ms-powerpoint.template.macroenabled.12");
		MimeMappings.put("potx", "application/vnd.openxmlformats-officedocument.presentationml.template");
		MimeMappings.put("ppam", "application/vnd.ms-powerpoint.addin.macroenabled.12");
		MimeMappings.put("ppd", "application/vnd.cups-ppd");
		MimeMappings.put("ppm", "image/x-portable-pixmap");
		MimeMappings.put("pps", "application/vnd.ms-powerpoint");
		MimeMappings.put("ppsm", "application/vnd.ms-powerpoint.slideshow.macroenabled.12");
		MimeMappings.put("ppsx", "application/vnd.openxmlformats-officedocument.presentationml.slideshow");
		MimeMappings.put("ppt", "application/vnd.ms-powerpoint");
		MimeMappings.put("pptm", "application/vnd.ms-powerpoint.presentation.macroenabled.12");
		MimeMappings.put("pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation");
		MimeMappings.put("pqa", "application/vnd.palm");
		MimeMappings.put("prc", "application/x-mobipocket-ebook");
		MimeMappings.put("pre", "application/vnd.lotus-freelance");
		MimeMappings.put("prf", "application/pics-rules");
		MimeMappings.put("ps", "application/postscript");
		MimeMappings.put("psb", "application/vnd.3gpp.pic-bw-small");
		MimeMappings.put("psd", "image/vnd.adobe.photoshop");
		MimeMappings.put("psf", "application/x-font-linux-psf");
		MimeMappings.put("pskcxml", "application/pskc+xml");
		MimeMappings.put("ptid", "application/vnd.pvi.ptid1");
		MimeMappings.put("pub", "application/x-mspublisher");
		MimeMappings.put("pvb", "application/vnd.3gpp.pic-bw-var");
		MimeMappings.put("pwn", "application/vnd.3m.post-it-notes");
		MimeMappings.put("pya", "audio/vnd.ms-playready.media.pya");
		MimeMappings.put("pyv", "video/vnd.ms-playready.media.pyv");
		MimeMappings.put("qam", "application/vnd.epson.quickanime");
		MimeMappings.put("qbo", "application/vnd.intu.qbo");
		MimeMappings.put("qfx", "application/vnd.intu.qfx");
		MimeMappings.put("qps", "application/vnd.publishare-delta-tree");
		MimeMappings.put("qt", "video/quicktime");
		MimeMappings.put("qti", "image/x-quicktime");
		MimeMappings.put("qtif", "image/x-quicktime");
		MimeMappings.put("qwd", "application/vnd.quark.quarkxpress");
		MimeMappings.put("qwt", "application/vnd.quark.quarkxpress");
		MimeMappings.put("qxb", "application/vnd.quark.quarkxpress");
		MimeMappings.put("qxd", "application/vnd.quark.quarkxpress");
		MimeMappings.put("qxl", "application/vnd.quark.quarkxpress");
		MimeMappings.put("qxt", "application/vnd.quark.quarkxpress");
		MimeMappings.put("ra", "audio/x-pn-realaudio");
		MimeMappings.put("ram", "audio/x-pn-realaudio");
		MimeMappings.put("rar", "application/x-rar-compressed");
		MimeMappings.put("ras", "image/x-cmu-raster");
		MimeMappings.put("rcprofile", "application/vnd.ipunplugged.rcprofile");
		MimeMappings.put("rdf", "application/rdf+xml");
		MimeMappings.put("rdz", "application/vnd.data-vision.rdz");
		MimeMappings.put("rep", "application/vnd.businessobjects");
		MimeMappings.put("res", "application/x-dtbresource+xml");
		MimeMappings.put("rgb", "image/x-rgb");
		MimeMappings.put("rif", "application/reginfo+xml");
		MimeMappings.put("rip", "audio/vnd.rip");
		MimeMappings.put("ris", "application/x-research-info-systems");
		MimeMappings.put("rl", "application/resource-lists+xml");
		MimeMappings.put("rlc", "image/vnd.fujixerox.edmics-rlc");
		MimeMappings.put("rld", "application/resource-lists-diff+xml");
		MimeMappings.put("rm", "application/vnd.rn-realmedia");
		MimeMappings.put("rmi", "audio/midi");
		MimeMappings.put("rmp", "audio/x-pn-realaudio-plugin");
		MimeMappings.put("rms", "application/vnd.jcp.javame.midlet-rms");
		MimeMappings.put("rmvb", "application/vnd.rn-realmedia-vbr");
		MimeMappings.put("rnc", "application/relax-ng-compact-syntax");
		MimeMappings.put("roa", "application/rpki-roa");
		MimeMappings.put("roff", "text/troff");
		MimeMappings.put("rp9", "application/vnd.cloanto.rp9");
		MimeMappings.put("rpss", "application/vnd.nokia.radio-presets");
		MimeMappings.put("rpst", "application/vnd.nokia.radio-preset");
		MimeMappings.put("rq", "application/sparql-query");
		MimeMappings.put("rs", "application/rls-services+xml");
		MimeMappings.put("rsd", "application/rsd+xml");
		MimeMappings.put("rss", "application/rss+xml");
		MimeMappings.put("rtf", "application/rtf");
		MimeMappings.put("rtx", "text/richtext");
		MimeMappings.put("s", "text/x-asm");
		MimeMappings.put("s3m", "audio/s3m");
		MimeMappings.put("saf", "application/vnd.yamaha.smaf-audio");
		MimeMappings.put("sbml", "application/sbml+xml");
		MimeMappings.put("sc", "application/vnd.ibm.secure-container");
		MimeMappings.put("scd", "application/x-msschedule");
		MimeMappings.put("scm", "application/vnd.lotus-screencam");
		MimeMappings.put("scq", "application/scvp-cv-request");
		MimeMappings.put("scs", "application/scvp-cv-response");
		MimeMappings.put("scurl", "text/vnd.curl.scurl");
		MimeMappings.put("sda", "application/vnd.stardivision.draw");
		MimeMappings.put("sdc", "application/vnd.stardivision.calc");
		MimeMappings.put("sdd", "application/vnd.stardivision.impress");
		MimeMappings.put("sdkd", "application/vnd.solent.sdkm+xml");
		MimeMappings.put("sdkm", "application/vnd.solent.sdkm+xml");
		MimeMappings.put("sdp", "application/sdp");
		MimeMappings.put("sdw", "application/vnd.stardivision.writer");
		MimeMappings.put("see", "application/vnd.seemail");
		MimeMappings.put("seed", "application/vnd.fdsn.seed");
		MimeMappings.put("sema", "application/vnd.sema");
		MimeMappings.put("semd", "application/vnd.semd");
		MimeMappings.put("semf", "application/vnd.semf");
		MimeMappings.put("ser", "application/java-serialized-object");
		MimeMappings.put("setpay", "application/set-payment-initiation");
		MimeMappings.put("setreg", "application/set-registration-initiation");
		MimeMappings.put("sfd-hdstx", "application/vnd.hydrostatix.sof-data");
		MimeMappings.put("sfs", "application/vnd.spotfire.sfs");
		MimeMappings.put("sfv", "text/x-sfv");
		MimeMappings.put("sgi", "image/sgi");
		MimeMappings.put("sgl", "application/vnd.stardivision.writer-global");
		MimeMappings.put("sgm", "text/sgml");
		MimeMappings.put("sgml", "text/sgml");
		MimeMappings.put("sh", "application/x-sh");
		MimeMappings.put("shar", "application/x-shar");
		MimeMappings.put("shf", "application/shf+xml");
		MimeMappings.put("sid", "image/x-mrsid-image");
		MimeMappings.put("sig", "application/pgp-signature");
		MimeMappings.put("sil", "audio/silk");
		MimeMappings.put("silo", "model/mesh");
		MimeMappings.put("sis", "application/vnd.symbian.install");
		MimeMappings.put("sisx", "application/vnd.symbian.install");
		MimeMappings.put("sit", "application/x-stuffit");
		MimeMappings.put("sitx", "application/x-stuffitx");
		MimeMappings.put("skd", "application/vnd.koan");
		MimeMappings.put("skm", "application/vnd.koan");
		MimeMappings.put("skp", "application/vnd.koan");
		MimeMappings.put("skt", "application/vnd.koan");
		MimeMappings.put("sldm", "application/vnd.ms-powerpoint.slide.macroenabled.12");
		MimeMappings.put("sldx", "application/vnd.openxmlformats-officedocument.presentationml.slide");
		MimeMappings.put("slt", "application/vnd.epson.salt");
		MimeMappings.put("sm", "application/vnd.stepmania.stepchart");
		MimeMappings.put("smf", "application/vnd.stardivision.math");
		MimeMappings.put("smi", "application/smil+xml");
		MimeMappings.put("smil", "application/smil+xml");
		MimeMappings.put("smv", "video/x-smv");
		MimeMappings.put("smzip", "application/vnd.stepmania.package");
		MimeMappings.put("snd", "audio/basic");
		MimeMappings.put("snf", "application/x-font-snf");
		MimeMappings.put("so", "application/octet-stream");
		MimeMappings.put("spc", "application/x-pkcs7-certificates");
		MimeMappings.put("spf", "application/vnd.yamaha.smaf-phrase");
		MimeMappings.put("spl", "application/x-futuresplash");
		MimeMappings.put("spot", "text/vnd.in3d.spot");
		MimeMappings.put("spp", "application/scvp-vp-response");
		MimeMappings.put("spq", "application/scvp-vp-request");
		MimeMappings.put("spx", "audio/ogg");
		MimeMappings.put("sql", "application/x-sql");
		MimeMappings.put("src", "application/x-wais-source");
		MimeMappings.put("srt", "application/x-subrip");
		MimeMappings.put("sru", "application/sru+xml");
		MimeMappings.put("srx", "application/sparql-results+xml");
		MimeMappings.put("ssdl", "application/ssdl+xml");
		MimeMappings.put("sse", "application/vnd.kodak-descriptor");
		MimeMappings.put("ssf", "application/vnd.epson.ssf");
		MimeMappings.put("ssml", "application/ssml+xml");
		MimeMappings.put("st", "application/vnd.sailingtracker.track");
		MimeMappings.put("stc", "application/vnd.sun.xml.calc.template");
		MimeMappings.put("std", "application/vnd.sun.xml.draw.template");
		MimeMappings.put("stf", "application/vnd.wt.stf");
		MimeMappings.put("sti", "application/vnd.sun.xml.impress.template");
		MimeMappings.put("stk", "application/hyperstudio");
		MimeMappings.put("stl", "application/vnd.ms-pki.stl");
		MimeMappings.put("str", "application/vnd.pg.format");
		MimeMappings.put("stw", "application/vnd.sun.xml.writer.template");
		MimeMappings.put("sub", "text/vnd.dvb.subtitle");
		MimeMappings.put("sus", "application/vnd.sus-calendar");
		MimeMappings.put("susp", "application/vnd.sus-calendar");
		MimeMappings.put("sv4cpio", "application/x-sv4cpio");
		MimeMappings.put("sv4crc", "application/x-sv4crc");
		MimeMappings.put("svc", "application/vnd.dvb.service");
		MimeMappings.put("svd", "application/vnd.svd");
		MimeMappings.put("svg", "image/svg+xml");
		MimeMappings.put("svgz", "image/svg+xml");
		MimeMappings.put("swa", "application/x-director");
		MimeMappings.put("swf", "application/x-shockwave-flash");
		MimeMappings.put("swi", "application/vnd.aristanetworks.swi");
		MimeMappings.put("sxc", "application/vnd.sun.xml.calc");
		MimeMappings.put("sxd", "application/vnd.sun.xml.draw");
		MimeMappings.put("sxg", "application/vnd.sun.xml.writer.global");
		MimeMappings.put("sxi", "application/vnd.sun.xml.impress");
		MimeMappings.put("sxm", "application/vnd.sun.xml.math");
		MimeMappings.put("sxw", "application/vnd.sun.xml.writer");
		MimeMappings.put("t", "text/troff");
		MimeMappings.put("t3", "application/x-t3vm-image");
		MimeMappings.put("taglet", "application/vnd.mynfc");
		MimeMappings.put("tao", "application/vnd.tao.intent-module-archive");
		MimeMappings.put("tar", "application/x-tar");
		MimeMappings.put("tcap", "application/vnd.3gpp2.tcap");
		MimeMappings.put("tcl", "application/x-tcl");
		MimeMappings.put("teacher", "application/vnd.smart.teacher");
		MimeMappings.put("tei", "application/tei+xml");
		MimeMappings.put("teicorpus", "application/tei+xml");
		MimeMappings.put("tex", "application/x-tex");
		MimeMappings.put("texi", "application/x-texinfo");
		MimeMappings.put("texinfo", "application/x-texinfo");
		MimeMappings.put("text", "text/plain");
		MimeMappings.put("tfi", "application/thraud+xml");
		MimeMappings.put("tfm", "application/x-tex-tfm");
		MimeMappings.put("tga", "image/x-tga");
		MimeMappings.put("thmx", "application/vnd.ms-officetheme");
		MimeMappings.put("tif", "image/tiff");
		MimeMappings.put("tiff", "image/tiff");
		MimeMappings.put("tmo", "application/vnd.tmobile-livetv");
		MimeMappings.put("torrent", "application/x-bittorrent");
		MimeMappings.put("tpl", "application/vnd.groove-tool-template");
		MimeMappings.put("tpt", "application/vnd.trid.tpt");
		MimeMappings.put("tr", "text/troff");
		MimeMappings.put("tra", "application/vnd.trueapp");
		MimeMappings.put("trm", "application/x-msterminal");
		MimeMappings.put("tsd", "application/timestamped-data");
		MimeMappings.put("tsv", "text/tab-separated-values");
		MimeMappings.put("ttc", "font/collection");
		MimeMappings.put("ttf", "font/ttf");
		MimeMappings.put("ttl", "text/turtle");
		MimeMappings.put("twd", "application/vnd.simtech-mindmapper");
		MimeMappings.put("twds", "application/vnd.simtech-mindmapper");
		MimeMappings.put("txd", "application/vnd.genomatix.tuxedo");
		MimeMappings.put("txf", "application/vnd.mobius.txf");
		MimeMappings.put("txt", "text/plain");
		MimeMappings.put("u32", "application/x-authorware-bin");
		MimeMappings.put("udeb", "application/x-debian-package");
		MimeMappings.put("ufd", "application/vnd.ufdl");
		MimeMappings.put("ufdl", "application/vnd.ufdl");
		MimeMappings.put("ulw", "audio/basic");
		MimeMappings.put("ulx", "application/x-glulx");
		MimeMappings.put("umj", "application/vnd.umajin");
		MimeMappings.put("unityweb", "application/vnd.unity");
		MimeMappings.put("uoml", "application/vnd.uoml+xml");
		MimeMappings.put("uri", "text/uri-list");
		MimeMappings.put("uris", "text/uri-list");
		MimeMappings.put("urls", "text/uri-list");
		MimeMappings.put("ustar", "application/x-ustar");
		MimeMappings.put("utz", "application/vnd.uiq.theme");
		MimeMappings.put("uu", "text/x-uuencode");
		MimeMappings.put("uva", "audio/vnd.dece.audio");
		MimeMappings.put("uvd", "application/vnd.dece.data");
		MimeMappings.put("uvf", "application/vnd.dece.data");
		MimeMappings.put("uvg", "image/vnd.dece.graphic");
		MimeMappings.put("uvh", "video/vnd.dece.hd");
		MimeMappings.put("uvi", "image/vnd.dece.graphic");
		MimeMappings.put("uvm", "video/vnd.dece.mobile");
		MimeMappings.put("uvp", "video/vnd.dece.pd");
		MimeMappings.put("uvs", "video/vnd.dece.sd");
		MimeMappings.put("uvt", "application/vnd.dece.ttml+xml");
		MimeMappings.put("uvu", "video/vnd.uvvu.mp4");
		MimeMappings.put("uvv", "video/vnd.dece.video");
		MimeMappings.put("uvva", "audio/vnd.dece.audio");
		MimeMappings.put("uvvd", "application/vnd.dece.data");
		MimeMappings.put("uvvf", "application/vnd.dece.data");
		MimeMappings.put("uvvg", "image/vnd.dece.graphic");
		MimeMappings.put("uvvh", "video/vnd.dece.hd");
		MimeMappings.put("uvvi", "image/vnd.dece.graphic");
		MimeMappings.put("uvvm", "video/vnd.dece.mobile");
		MimeMappings.put("uvvp", "video/vnd.dece.pd");
		MimeMappings.put("uvvs", "video/vnd.dece.sd");
		MimeMappings.put("uvvt", "application/vnd.dece.ttml+xml");
		MimeMappings.put("uvvu", "video/vnd.uvvu.mp4");
		MimeMappings.put("uvvv", "video/vnd.dece.video");
		MimeMappings.put("uvvx", "application/vnd.dece.unspecified");
		MimeMappings.put("uvvz", "application/vnd.dece.zip");
		MimeMappings.put("uvx", "application/vnd.dece.unspecified");
		MimeMappings.put("uvz", "application/vnd.dece.zip");
		MimeMappings.put("vcard", "text/vcard");
		MimeMappings.put("vcd", "application/x-cdlink");
		MimeMappings.put("vcf", "text/x-vcard");
		MimeMappings.put("vcg", "application/vnd.groove-vcard");
		MimeMappings.put("vcs", "text/x-vcalendar");
		MimeMappings.put("vcx", "application/vnd.vcx");
		MimeMappings.put("vis", "application/vnd.visionary");
		MimeMappings.put("viv", "video/vnd.vivo");
		MimeMappings.put("vob", "video/x-ms-vob");
		MimeMappings.put("vor", "application/vnd.stardivision.writer");
		MimeMappings.put("vox", "application/x-authorware-bin");
		MimeMappings.put("vrml", "model/vrml");
		MimeMappings.put("vsd", "application/vnd.visio");
		MimeMappings.put("vsf", "application/vnd.vsf");
		MimeMappings.put("vss", "application/vnd.visio");
		MimeMappings.put("vst", "application/vnd.visio");
		MimeMappings.put("vsw", "application/vnd.visio");
		MimeMappings.put("vtu", "model/vnd.vtu");
		MimeMappings.put("vxml", "application/voicexml+xml");
		MimeMappings.put("w3d", "application/x-director");
		MimeMappings.put("wad", "application/x-doom");
		MimeMappings.put("wasm", "application/wasm");
		MimeMappings.put("wav", "audio/x-wav");
		MimeMappings.put("wax", "audio/x-ms-wax");
		MimeMappings.put("wbmp", "image/vnd.wap.wbmp");
		MimeMappings.put("wbs", "application/vnd.criticaltools.wbs+xml");
		MimeMappings.put("wbxml", "application/vnd.wap.wbxml");
		MimeMappings.put("wcm", "application/vnd.ms-works");
		MimeMappings.put("wdb", "application/vnd.ms-works");
		MimeMappings.put("wdp", "image/vnd.ms-photo");
		MimeMappings.put("weba", "audio/webm");
		MimeMappings.put("webm", "video/webm");
		MimeMappings.put("webp", "image/webp");
		MimeMappings.put("wg", "application/vnd.pmi.widget");
		MimeMappings.put("wgt", "application/widget");
		MimeMappings.put("wks", "application/vnd.ms-works");
		MimeMappings.put("wm", "video/x-ms-wm");
		MimeMappings.put("wma", "audio/x-ms-wma");
		MimeMappings.put("wmd", "application/x-ms-wmd");
		MimeMappings.put("wmf", "application/x-msmetafile");
		MimeMappings.put("wml", "text/vnd.wap.wml");
		MimeMappings.put("wmlc", "application/vnd.wap.wmlc");
		MimeMappings.put("wmls", "text/vnd.wap.wmlscript");
		MimeMappings.put("wmlsc", "application/vnd.wap.wmlscriptc");
		MimeMappings.put("wmv", "video/x-ms-wmv");
		MimeMappings.put("wmx", "video/x-ms-wmx");
		MimeMappings.put("wmz", "application/x-msmetafile");
		MimeMappings.put("woff", "font/woff");
		MimeMappings.put("woff2", "font/woff2");
		MimeMappings.put("wpd", "application/vnd.wordperfect");
		MimeMappings.put("wpl", "application/vnd.ms-wpl");
		MimeMappings.put("wps", "application/vnd.ms-works");
		MimeMappings.put("wqd", "application/vnd.wqd");
		MimeMappings.put("wri", "application/x-mswrite");
		MimeMappings.put("wrl", "model/vrml");
		MimeMappings.put("wsdl", "application/wsdl+xml");
		MimeMappings.put("wspolicy", "application/wspolicy+xml");
		MimeMappings.put("wtb", "application/vnd.webturbo");
		MimeMappings.put("wvx", "video/x-ms-wvx");
		MimeMappings.put("x32", "application/x-authorware-bin");
		MimeMappings.put("x3d", "model/x3d+xml");
		MimeMappings.put("x3db", "model/x3d+binary");
		MimeMappings.put("x3dbz", "model/x3d+binary");
		MimeMappings.put("x3dv", "model/x3d+vrml");
		MimeMappings.put("x3dvz", "model/x3d+vrml");
		MimeMappings.put("x3dz", "model/x3d+xml");
		MimeMappings.put("xaml", "application/xaml+xml");
		MimeMappings.put("xap", "application/x-silverlight-app");
		MimeMappings.put("xar", "application/vnd.xara");
		MimeMappings.put("xbap", "application/x-ms-xbap");
		MimeMappings.put("xbd", "application/vnd.fujixerox.docuworks.binder");
		MimeMappings.put("xbm", "image/x-xbitmap");
		MimeMappings.put("xdf", "application/xcap-diff+xml");
		MimeMappings.put("xdm", "application/vnd.syncml.dm+xml");
		MimeMappings.put("xdp", "application/vnd.adobe.xdp+xml");
		MimeMappings.put("xdssc", "application/dssc+xml");
		MimeMappings.put("xdw", "application/vnd.fujixerox.docuworks");
		MimeMappings.put("xenc", "application/xenc+xml");
		MimeMappings.put("xer", "application/patch-ops-error+xml");
		MimeMappings.put("xfdf", "application/vnd.adobe.xfdf");
		MimeMappings.put("xfdl", "application/vnd.xfdl");
		MimeMappings.put("xht", "application/xhtml+xml");
		MimeMappings.put("xhtml", "application/xhtml+xml");
		MimeMappings.put("xhvml", "application/xv+xml");
		MimeMappings.put("xif", "image/vnd.xiff");
		MimeMappings.put("xla", "application/vnd.ms-excel");
		MimeMappings.put("xlam", "application/vnd.ms-excel.addin.macroenabled.12");
		MimeMappings.put("xlc", "application/vnd.ms-excel");
		MimeMappings.put("xlf", "application/x-xliff+xml");
		MimeMappings.put("xlm", "application/vnd.ms-excel");
		MimeMappings.put("xls", "application/vnd.ms-excel");
		MimeMappings.put("xlsb", "application/vnd.ms-excel.sheet.binary.macroenabled.12");
		MimeMappings.put("xlsm", "application/vnd.ms-excel.sheet.macroenabled.12");
		MimeMappings.put("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		MimeMappings.put("xlt", "application/vnd.ms-excel");
		MimeMappings.put("xltm", "application/vnd.ms-excel.template.macroenabled.12");
		MimeMappings.put("xltx", "application/vnd.openxmlformats-officedocument.spreadsheetml.template");
		MimeMappings.put("xlw", "application/vnd.ms-excel");
		MimeMappings.put("xm", "audio/xm");
		MimeMappings.put("xml", "application/xml");
		MimeMappings.put("xo", "application/vnd.olpc-sugar");
		MimeMappings.put("xop", "application/xop+xml");
		MimeMappings.put("xpi", "application/x-xpinstall");
		MimeMappings.put("xpl", "application/xproc+xml");
		MimeMappings.put("xpm", "image/x-xpixmap");
		MimeMappings.put("xpr", "application/vnd.is-xpr");
		MimeMappings.put("xps", "application/vnd.ms-xpsdocument");
		MimeMappings.put("xpw", "application/vnd.intercon.formnet");
		MimeMappings.put("xpx", "application/vnd.intercon.formnet");
		MimeMappings.put("xsl", "application/xml");
		MimeMappings.put("xslt", "application/xslt+xml");
		MimeMappings.put("xsm", "application/vnd.syncml+xml");
		MimeMappings.put("xspf", "application/xspf+xml");
		MimeMappings.put("xul", "application/vnd.mozilla.xul+xml");
		MimeMappings.put("xvm", "application/xv+xml");
		MimeMappings.put("xvml", "application/xv+xml");
		MimeMappings.put("xwd", "image/x-xwindowdump");
		MimeMappings.put("xyz", "chemical/x-xyz");
		MimeMappings.put("xz", "application/x-xz");
		MimeMappings.put("yang", "application/yang");
		MimeMappings.put("yin", "application/yin+xml");
		MimeMappings.put("z", "application/x-compress");
		MimeMappings.put("z1", "application/x-zmachine");
		MimeMappings.put("z2", "application/x-zmachine");
		MimeMappings.put("z3", "application/x-zmachine");
		MimeMappings.put("z4", "application/x-zmachine");
		MimeMappings.put("z5", "application/x-zmachine");
		MimeMappings.put("z6", "application/x-zmachine");
		MimeMappings.put("z7", "application/x-zmachine");
		MimeMappings.put("z8", "application/x-zmachine");
		MimeMappings.put("zaz", "application/vnd.zzazz.deck+xml");
		MimeMappings.put("zip", "application/zip");
		MimeMappings.put("zir", "application/vnd.zul");
		MimeMappings.put("zirz", "application/vnd.zul");
		MimeMappings.put("zmm", "application/vnd.handheld-entertainment+xml");
	}

}
