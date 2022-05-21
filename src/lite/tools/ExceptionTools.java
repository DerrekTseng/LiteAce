package lite.tools;

public class ExceptionTools {

	/**
	 * 取得 Exception 全部的 Stack String
	 * 
	 * @param throwable
	 * @return
	 */
	public static String getAllStackTrace(Throwable throwable) {
		if (throwable == null) {
			return "";
		}
		StringBuilder message = new StringBuilder();

		message.append(throwable.toString());
		message.append(" : ");
		message.append(throwable.getLocalizedMessage()).append("\n");
		for (StackTraceElement stackTraceElement : throwable.getStackTrace()) {
			message.append("\tat " + stackTraceElement).append("\n");
		}
		Throwable cause = throwable.getCause();
		if (cause != null) {
			message.append(getAllStackTrace(cause));
		}
		return message.toString();
	}

	/**
	 * 印出 Exception 全部的 StackTrace
	 * 
	 * @param throwable
	 */
	public static void printAllStackTrace(Throwable throwable) {
		System.err.println(getAllStackTrace(throwable));
	}

	/**
	 * 判斷 Exception 是否源自某個 Exception
	 * 
	 * @param throwable
	 * @param causes
	 * @return
	 */
	@SafeVarargs
	public static boolean matchCauseType(Throwable throwable, Class<? extends Throwable>... causes) {
		if (throwable == null) {
			return false;
		}

		for (Class<?> clazz : causes) {
			if (throwable.getClass() == clazz) {
				return true;
			}
		}
		return matchCauseType(throwable.getCause(), causes);
	}

}
