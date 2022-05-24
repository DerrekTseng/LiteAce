package lite.tools;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class FileTools {

	/** 中文檔名排序用 */
	public static final Comparator<Object> traditionalChineseComparator = Collator.getInstance(Locale.TRADITIONAL_CHINESE);

	public static byte[] readRandomBytes(RandomAccessFile raf, File file, int offset, int bufferSize) throws IOException {
		int readSize = (int) (file.length() - offset);
		if (readSize > bufferSize) {
			readSize = bufferSize;
		}
		byte[] buffer = new byte[readSize];
		raf.seek(offset);
		raf.readFully(buffer);
		return buffer;
	}

	public static byte[] readBytes(File file) {
		try (ByteArrayOutputStream ous = new ByteArrayOutputStream()) {
			readBytesStream(file, 4096, (bytes, read) -> ous.write(bytes, 0, read));
			ous.flush();
			return ous.toByteArray();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static byte[] readBytes(String file) {
		return readBytes(new File(file));
	}

	public static String readString(byte[] bytes, String charset) {
		ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
		return readString(bis, charset);
	}

	public static String readString(InputStream inputStream) {
		return readString(inputStream, Charset.defaultCharset().name());
	}

	public static String readString(InputStream inputStream, String charset) {
		try (InputStreamReader isr = new InputStreamReader(inputStream, charset); BufferedReader br = new BufferedReader(isr)) {
			CharBuffer cb = CharBuffer.allocate(inputStream.available());
			br.read(cb);
			cb.flip();
			return cb.toString();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static String readString(File file, String charset) {
		return readString(readBytes(file), charset);
	}

	public static String readString(File file) {
		return readString(file, Charset.defaultCharset().name());
	}

	public static String readString(String file, String charset) {
		return readString(new File(file), charset);
	}

	public static String readString(String file) {
		return readString(new File(file));
	}

	public static String[] readLines(File file, String charset) {
		ArrayList<String> result = new ArrayList<>();
		readLinesStream(file, charset, line -> result.add(line));
		return result.toArray(new String[result.size()]);
	}

	public static String[] readLines(File file) {
		return readLines(file, Charset.defaultCharset().name());
	}

	public static String[] readLines(String file, String charset) {
		return readLines(new File(file), charset);
	}

	public static String[] readLines(String file) {
		return readLines(new File(file));
	}

	@FunctionalInterface
	public interface BytesStreamConsumer {
		void accept(byte[] buffer, int read);
	}

	public static void readBytesStream(File file, int bufferSize, BytesStreamConsumer consumer) {
		byte[] buffer = new byte[bufferSize];
		try (FileInputStream ios = new FileInputStream(file)) {
			int read = 0;
			while ((read = ios.read(buffer)) != -1) {
				consumer.accept(buffer, read);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static void readCharsStream(File file, String charset, Consumer<Character> consumer) {
		try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), charset))) {
			int r;
			while ((r = br.read()) != -1) {
				char ch = (char) r;
				consumer.accept(ch);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static void readLinesStream(File file, String charset, Consumer<String> consumer) {
		final StringBuilder stringBuilder = new StringBuilder();
		readCharsStream(file, charset, c -> {
			if (c == '\r') {
				// do nothing
			} else if (c == '\n') {
				consumer.accept(stringBuilder.toString());
				stringBuilder.setLength(0);
			} else {
				stringBuilder.append(c);
			}
		});
		consumer.accept(stringBuilder.toString());
	}

	private static String[] autoEncodeCharsets = { "utf-8", "big5", "ms950", "gbk" };

	public static String readStringAutoEncode(String filePath) {
		byte[] bytes = readBytes(filePath);

		for (String charset : autoEncodeCharsets) {
			String temp = getStringFromByte(bytes, charset);
			if (getStringByte(temp, charset).length == bytes.length) {
				return temp;
			}
		}

		return new String(bytes);
	}

	// -------------------------------------------------------------------------
	private static byte[] getStringByte(String str, String charset) {
		try {
			return str.getBytes(charset);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	private static String getStringFromByte(byte[] bytes, String charset) {
		try {
			return new String(bytes, charset);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	// ---------------------------------------------------------------------------
	public static void writeBytes(File file, byte[] bytes, boolean append) {
		try (FileOutputStream fos = new FileOutputStream(file, append); BufferedOutputStream bos = new BufferedOutputStream(fos);) {
			bos.write(bytes);
			bos.flush();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static void writeBytes(String file, byte[] bytes, boolean append) {
		writeBytes(new File(file), bytes, append);
	}

	public static void writeString(File file, String text, boolean append, String charset) {
		writeBytes(file, getStringByte(text, charset), append);
	}

	public static void writeString(String file, String text, boolean append, String charset) {
		writeString(new File(file), text, append, charset);
	}

	public static void writeString(File file, String text, boolean append) {
		writeString(file, text, append, Charset.defaultCharset().name());
	}

	public static void writeString(String file, String text, boolean append) {
		writeString(new File(file), text, append);
	}

	public static void writeStringLines(File file, String[] lines, boolean append, String charset) {
		String lineSeparator = System.getProperty("line.separator");
		writeString(file, Arrays.asList(lines).stream().collect(Collectors.joining(lineSeparator)), append, charset);
	}

	public static void writeStringLines(String file, String[] lines, boolean append, String charset) {
		writeStringLines(new File(file), lines, append, charset);
	}

	public static void writeStringLines(File file, String[] lines, boolean append) {
		writeStringLines(file, lines, append, Charset.defaultCharset().name());
	}

	public static void writeStringLines(String file, String[] lines, boolean append) {
		writeStringLines(new File(file), lines, append);
	}

	public static void writeStringLines(File file, List<String> lines, boolean append, String charset) {
		writeStringLines(file, lines.toArray(new String[lines.size()]), append, charset);
	}

	public static void writeStringLines(String file, List<String> lines, String charset, boolean append) {
		writeStringLines(new File(file), lines, append, charset);
	}

	public static void writeStringLines(File file, List<String> lines, boolean append) {
		writeStringLines(file, lines, append, Charset.defaultCharset().name());
	}

	public static void writeStringLines(String file, List<String> lines, boolean append) {
		writeStringLines(new File(file), lines, append);
	}

	// -----------------------------------------------------------------------------------------------------------

	/**
	 * Combin file path. No need to deal with any File.pathSeparator<br>
	 * <br>
	 * Example: <br>
	 * 
	 * combinPath("C://", "//a", "\b", "c", "//test.txt"); <br>
	 * <br>
	 * Linux: <br>
	 * C:/a/b/c/test.txt <br>
	 * <br>
	 * Windows:<br>
	 * C:\a\b\c\test.txt
	 * 
	 * @param paths
	 * @return
	 */
	public static String combinPath(String... paths) {
		File file = null;
		for (String path : paths) {
			if (file == null) {
				file = new File(path);
			} else {
				file = new File(file, path);
			}
		}
		return file.getPath();
	}

	/**
	 * Tests the file denoted by its abstract pathname exists.
	 * 
	 * @param path
	 * @return
	 */
	public static boolean fileExists(String path) {
		return exists(path) && isFile(path) && !"".equals(path);
	}

	/**
	 * Tests the directory denoted by its abstract pathname exists.
	 * 
	 * @param path
	 * @return
	 */
	public static boolean directoryExists(String path) {
		return exists(path) && isDirectory(path) && !"".equals(path);
	}

	/**
	 * Tests whether the file or directory denoted by its abstract pathname exists.
	 * 
	 * @param path
	 * @return
	 */
	public static boolean exists(String path) {
		return new File(path).exists();
	}

	public static boolean isFileLocked(File f) {
		try (RandomAccessFile file = new RandomAccessFile(f, "rw");) {
			file.getChannel().tryLock();
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	/**
	 * Tests whether the file denoted by its abstract pathname is a normal file.
	 * 
	 * @param path
	 * @return
	 */
	public static boolean isFile(String path) {
		return new File(path).isFile();
	}

	/**
	 * Return the file length
	 * 
	 * @param path
	 * @return
	 */
	public static long getFileSize(String path) {
		if (fileExists(path)) {
			return getFileObject(path).length();
		} else {
			return 0L;
		}
	}

	/**
	 * Tests whether the file denoted by its abstract pathname is a directory.
	 * 
	 * @param path
	 * @return
	 */
	public static boolean isDirectory(String path) {
		return new File(path).isDirectory();
	}

	/**
	 * Returns the extension without '.' by its abstract pathname. <br/>
	 * <br/>
	 * Examples: <br/>
	 * "C:\example.txt" returns "txt" <br/>
	 * "C:\example.DOc" returns "DOc" <br/>
	 * <br/>
	 * If its abstract pathname does not have extension, will return a empty String.
	 * 
	 * @param path
	 * @return
	 */
	public static String getExtension(String path) {
		String[] separated = new File(path).getName().split("[.]");
		if (separated.length < 2) {
			return "";
		} else {
			return separated[separated.length - 1];
		}
	}

	/**
	 * Returns the extension without '.' by its abstract pathname. <br/>
	 * <br/>
	 * Examples: <br/>
	 * "C:\example.txt" returns "txt" <br/>
	 * "C:\example.DOc" returns "DOc" <br/>
	 * <br/>
	 * If its abstract pathname does not have extension, will return a empty String.
	 * 
	 * @param path
	 * @return
	 */
	public static String getExtension(File file) {
		return getExtension(file.getAbsolutePath());
	}

	/**
	 * Create a File object without new File().
	 * 
	 * @param path
	 * @return
	 */
	public static File getFileObject(String path) {
		return new File(path);
	}

	public static List<File> getAllFiles(String dir) {
		List<File> result = new ArrayList<>();
		File f = new File(dir);
		if (f.exists()) {
			if (f.isFile()) {
				result.add(f);
			} else if (f.isDirectory()) {
				for (File cf : f.listFiles()) {
					result.addAll(getAllFiles(cf.getAbsolutePath()));
				}
			}
		}
		sortFiles(result, true);
		return result;
	}

	public static List<File> getAllDirectories(String dir) {
		List<File> result = new ArrayList<>();
		File f = new File(dir);
		if (f.exists()) {
			if (f.isDirectory()) {
				result.add(f);
				for (File cf : f.listFiles()) {
					result.addAll(getAllDirectories(cf.getAbsolutePath()));
				}
			}
		}
		sortFiles(result, true);
		return result;
	}

	public static List<File> getDirectories(String dir) {
		List<File> result = new ArrayList<>();
		if (directoryExists(dir)) {
			for (File d : new File(dir).listFiles()) {
				if (directoryExists(d.getAbsolutePath())) {
					result.add(d);
				}
			}
		}
		sortFiles(result, true);
		return result;
	}

	/**
	 * 取得目錄底下的檔案+資料夾 不包含子目錄
	 * 
	 * @param dir
	 * @return
	 */
	public static ArrayList<File> getContents(String dir) {
		ArrayList<File> result = new ArrayList<>();
		if (directoryExists(dir)) {
			for (File d : new File(dir).listFiles()) {
				result.add(d);
			}
		}
		sortFiles(result, true);
		return result;
	}

	/**
	 * 取得目錄底下的檔案+資料夾 包含子目錄、包含帶入的 dir
	 * 
	 * @param dir
	 * @return
	 * @throws IOException
	 */
	public static List<File> getAllContents(String dir, boolean sort) {
		List<File> result = new ArrayList<>();
		if (directoryExists(dir)) {
			File dirFile = new File(dir);
			result.add(dirFile);
			for (File f : dirFile.listFiles()) {
				if (f.isDirectory()) {
					result.addAll(getAllContents(f.getAbsolutePath(), false));
				} else {
					result.add(f);
				}
			}
		}
		if (sort) {
			sortFiles(result, true);
		}
		return result;
	}

	public static Set<String> searchFileNames(File folder, String keyword) {
		Set<String> result = new HashSet<>();
		getAllContents(folder.getAbsolutePath(), false).forEach(f -> {
			if (f.getName().contains(keyword)) {
				result.add(f.getAbsolutePath());
			}
		});
		return result;
	}

	public static List<File> sortFiles(List<File> files, boolean asc) {
		if (asc) {
			Collections.sort(files, (Comparator<? super File>) (File file1, File file2) -> {
				return traditionalChineseComparator.compare(file1.getAbsolutePath(), file2.getAbsolutePath());
			});
		} else {
			Collections.sort(files, (Comparator<? super File>) (File file1, File file2) -> {
				return traditionalChineseComparator.compare(file2.getAbsolutePath(), file1.getAbsolutePath());
			});
		}
		return files;
	}

	/**
	 * Copy a file
	 * 
	 * @param source
	 * @param destination
	 * @throws IOException
	 */
	public static void copyFile(File source, File destination) throws IOException {
		if (source.isFile() && source.exists()) {
			byte[] buffer = new byte[4096];
			try (FileInputStream ios = new FileInputStream(source); FileOutputStream fos = new FileOutputStream(destination, false);) {
				int read = 0;
				while ((read = ios.read(buffer)) != -1) {
					fos.write(buffer, 0, read);
				}
			}
			destination.setLastModified(source.lastModified());
		} else {
			throw new IOException("'" + source.getAbsolutePath() + "' is not a file.");
		}

	}

	public static String[] separatePath(String str) {
		return str.split("\\" + File.separator);
	}

	public static long getDirectorySize(File dir) {
		long length = 0;
		File[] files = dir.listFiles();
		if (files != null) {
			for (File file : files) {
				if (file.isFile())
					length += file.length();
				else
					length += getDirectorySize(file);
			}
		}
		return length;
	}

	public static String toSizeString(long size) {

		long KB = 1024;
		long MB = KB * 1024;
		long GB = MB * 1024;
		long TB = GB * 1024;

		long Size_TB = size / TB;
		size -= Size_TB * TB;

		long Size_GB = size / GB;
		size -= Size_GB * GB;

		long Size_MB = size / MB;
		size -= Size_MB * MB;

		long Size_KB = size / KB;
		size -= Size_KB * KB;

		StringBuilder result = new StringBuilder();
		if (Size_TB > 0) {
			result.append(Long.toString(Size_TB) + " TB ");
		}
		if (Size_GB > 0) {
			result.append(Long.toString(Size_GB) + " GB ");
		}
		if (Size_MB > 0) {
			result.append(Long.toString(Size_MB) + " MB ");
		}
		if (Size_KB > 0) {
			result.append(Long.toString(Size_KB) + " KB ");
		}
		if (size > 0) {
			result.append(Long.toString(size) + " Byte ");
		}
		if (result.length() == 0) {
			result.append("0 Byte ");
		}
		return result.toString().trim();

	}

	/**
	 * Copy directories with files
	 * 
	 * @param source
	 * @param destination
	 * @throws IOException If directory or file cannot create.
	 */
	public static void copyDirectory(File source, File destination) throws IOException {
		if (source.isDirectory() && source.exists()) {
			copyDirectoryStructure(source, destination);
			List<File> files = getAllFiles(source.getAbsolutePath());
			File newFile = null;
			for (File f : files) {
				newFile = new File(combinPath(destination.getAbsolutePath(), straightReplaceFirst(f.getAbsolutePath(), source.getAbsolutePath(), "")));
				copyFile(f, newFile);
			}
		} else {
			throw new IOException("Directory '" + source.getAbsolutePath() + "' does not exist.");
		}
	}

	/**
	 * Copy directories with files. Fast but none-safe.
	 * 
	 * @param source
	 * @param destination
	 * @throws IOException If directory or file cannot create.
	 */
	public static void copyDirectoryWithMultithread(File source, File destination) throws IOException {
		if (source.isDirectory() && source.exists()) {
			copyDirectoryStructure(source, destination);
			List<File> files = getAllFiles(source.getAbsolutePath());
			CountDownLatch latch = new CountDownLatch(files.size());
			AtomicLong errorCount = new AtomicLong(0L);
			ExecutorService threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
			for (File f : files) {
				if (errorCount.get() > 0L) {
					latch.countDown();
					continue;
				}
				File newFile = new File(combinPath(destination.getAbsolutePath(), straightReplaceFirst(f.getAbsolutePath(), source.getAbsolutePath(), "")));
				threadPool.execute(() -> {
					try {
						copyFile(f, newFile);
					} catch (IOException e) {
						e.printStackTrace();
						errorCount.addAndGet(1L);
					} finally {
						latch.countDown();
					}
				});
			}
			try {
				latch.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
				errorCount.addAndGet(1L);
			}
			threadPool.shutdown();
			if (errorCount.get() > 0L) {
				deleteDirectory(destination);
				throw new IOException("Copying directory threaded failed...");
			}
		} else {
			throw new IOException("Directory '" + source.getAbsolutePath() + "' does not exist.");
		}
	}

	/**
	 * Only copy directories not files
	 * 
	 * @param source
	 * @param destination
	 * @throws IOException If directory cannot create.
	 */
	public static void copyDirectoryStructure(File source, File destination) throws IOException {
		List<File> dirs = getAllDirectories(source.getAbsolutePath());
		File newDirFile = null;
		for (File f : dirs) {
			newDirFile = new File(combinPath(destination.getAbsolutePath(), straightReplaceFirst(f.getAbsolutePath(), source.getAbsolutePath(), "")));
			boolean mkdir = newDirFile.mkdirs();
			if (!mkdir) {
				if (!directoryExists(newDirFile.getAbsolutePath())) {
					throw new IOException("Cannot create directory '" + newDirFile.getAbsolutePath() + "'");
				}
			}
		}
	}

	/**
	 * Replace string without regular expression.
	 * 
	 * @param content
	 * @param toReplace
	 * @param replacement
	 * @return
	 */
	public static String straightReplaceFirst(String content, String toReplace, String replacement) {
		if (content.contains(toReplace)) {
			int index = content.indexOf(toReplace);
			int toReplaceLength = toReplace.length();
			String head = content.substring(0, index);
			String tail = content.substring(index + toReplaceLength);
			return head + replacement + tail;
		} else {
			return content;
		}
	}

	/**
	 * Delete a file
	 * 
	 * @param file
	 * @throws IOException
	 */
	public static void deleteFile(File file) {
		if (!isFile(file.getAbsolutePath())) {
			throw new RuntimeException(new IOException(file.getAbsolutePath() + " is not a file."));
		}
		file.delete();
		if (fileExists(file.getAbsolutePath())) {
			throw new RuntimeException(new IOException(file.getAbsolutePath() + " cannot delete."));
		}
	}

	/**
	 * Delete whole directories and files
	 * 
	 * @param dir
	 * @throws IOException
	 */
	public static void deleteDirectory(File dir) {
		for (File childFile : getAllFiles(dir.getAbsolutePath())) {
			deleteFile(childFile);
		}

		for (File childDir : sortFiles(getAllDirectories(dir.getAbsolutePath()), false)) {
			childDir.delete();
		}
		dir.delete();
	}

	/**
	 * Compare a inputStream and a filePath. If the binary of two are the same
	 * return true, otherwise false.
	 * 
	 * @param inputStream
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public static boolean compare(InputStream inputStream, String path) throws IOException {
		InputStream ios = new FileInputStream(path);
		return compare(inputStream, ios, true);
	}

	/**
	 * Compare two inputStream, if the binary of two are the same return true,
	 * otherwise false.
	 * 
	 * @param inputStream
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public static boolean compare(InputStream input1, InputStream input2, boolean close) throws IOException {
		if (input1.available() != input2.available()) {
			if (close) {
				input1.close();
				input2.close();
			}
			return false;
		}
		byte[] buffer1 = new byte[4096];
		byte[] buffer2 = new byte[4096];
		while (input1.read(buffer1) != -1 && input2.read(buffer2) != -1) {
			if (!Arrays.equals(buffer1, buffer2)) {
				if (close) {
					input1.close();
					input2.close();
				}
				return false;
			}
		}
		if (close) {
			input1.close();
			input2.close();
		}
		return true;
	}

	public static void moveFile(File sourceFile, File destFile) throws IOException {
		if (directoryExists(sourceFile.getAbsolutePath())) {
			for (File file : sourceFile.listFiles()) {
				moveFile(file, new File(combinPath(destFile.getAbsolutePath(), file.getName())));
			}
			sourceFile.renameTo(destFile);
			if (getAllFiles(sourceFile.getAbsolutePath()).size() == 0 && getAllDirectories(sourceFile.getAbsolutePath()).size() == 1) {
				deleteDirectory(sourceFile);
			}
		} else if (fileExists(sourceFile.getAbsolutePath())) {
			destFile.getParentFile().mkdirs();
			Files.move(Paths.get(sourceFile.getPath()), Paths.get(destFile.getPath()), StandardCopyOption.REPLACE_EXISTING);
		} else {
			throw new IOException(sourceFile.getAbsolutePath() + " not found.");
		}
	}

	/**
	 * Compare two file, if the binary of two are the same return true, otherwise
	 * false.
	 * 
	 * @param file1
	 * @param file2
	 * @return
	 * @throws IOException
	 */
	public static boolean compare(File file1, File file2) throws IOException {
		return compare(new FileInputStream(file1), new FileInputStream(file2), true);
	}

	/**
	 * Compare two file path, if the binary of two are the same return true,
	 * otherwise false.
	 * 
	 * @param path1
	 * @param path2
	 * @return
	 * @throws IOException
	 */
	public static boolean compare(String path1, String path2) throws IOException {
		return compare(new File(path1), new File(path2));
	}

	public static long getCreationTime(String filePath) {
		try {
			File file = new File(filePath);
			BasicFileAttributes attr = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
			return attr.creationTime().toMillis();
		} catch (Exception e) {
			return -1L;
		}

	}

	public static long getLastModifiedTime(String filePath) {
		try {
			File file = new File(filePath);
			BasicFileAttributes attr = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
			return attr.lastModifiedTime().toMillis();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static long lastAccessTime(String filePath) {
		try {
			File file = new File(filePath);
			BasicFileAttributes attr = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
			return attr.lastAccessTime().toMillis();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static boolean isValidFilePath(String path) {
		File f = new File(path);
		try {
			f.getCanonicalPath();
			return true;
		} catch (IOException e) {
			return false;
		}
	}

}
