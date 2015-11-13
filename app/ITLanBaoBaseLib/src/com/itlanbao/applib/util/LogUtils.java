package com.itlanbao.applib.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
 

/**
 * 日志输出工具类
 * 
 * @author wjl
 *
 */
public class LogUtils {
	private static final String TAG = "itlanbao.com";
	public static boolean DEBUG = true;

	/**
	 * 保存日志到文件中
	 */
	private final boolean isSaveToFile = false;
	
	/**
	 * 日志保存路径，sdcard/(UMS_APP_NAME设置的名称)/Log.txt
	 */
	private static final String LOG_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + FileConstants.RESOURCE_DIRECTORY;
	private static final String LOG_PATH = LOG_DIR + "Log.txt";
	
	private static LogUtils instance = new LogUtils();

	public static LogUtils getLogger() {
		return instance;
	}

	/**
	 * log.i
	 */
	public void info(String msg) {
		if (DEBUG) {
			final String message = createMessage(msg);
			Log.i(TAG, message);
		}
	}

	public static void i(String msg) {
		instance.info(msg);
	}
	/**
	 * @author HUANGLB
	 * @param msg
	 */
	public static void i(String tag,String msg) {
		instance.info(tag,msg);
	}

 
	
	public void info(String tag,String msg) {
		if (DEBUG) {
			final String message = createMessage(msg);
			Log.i(tag, message);
		}
	}
	
	public static void i(Exception e) {
		instance.info(e != null ? e.toString() : "null");
	}

	/**
	 * log.v
	 */
	public void verbose(String msg) {
		if (DEBUG) {
			final String message = createMessage(msg);
			Log.v(TAG, message);
		}
	}
	
	public void verbose(String tag, String msg) {
		if (DEBUG) {
			final String message = createMessage(msg);
			Log.v(tag, message);
		}
	}

	public static void v(String msg) {
		instance.verbose(msg);
	}
	
	public static void v(String tag, String msg) {
		instance.verbose(tag, msg);
	}
	 
	public static void v(Exception e) {
		instance.verbose(e != null ? e.toString() : "null");
	}

	/**
	 * log.d
	 */
	public void debug(String msg) {
		if (DEBUG) {
			final String message = createMessage(msg);
			Log.d(TAG, message);
		}
	}
	
	public void debug(String tag, String msg) {
		if (DEBUG) {
			final String message = createMessage(msg);
			Log.d(tag, message);
		}
	}

	public static void d(String msg) {
		instance.debug(msg);
	}
	
	public static void d(String tag, String msg) {
		instance.debug(tag, msg);
	}
	
	 

	public static void d(Exception e) {
		instance.debug(e != null ? e.toString() : "null");
	}

	/**
	 * log.warn
	 * 
	 * @param msg
	 */
	public void warn(String tag, String msg) {
	    if (DEBUG) {
            final String message = createMessage(msg);
            Log.w(tag, message);
        }
	}
	public void warn(String msg) {
		if (DEBUG) {
			final String message = createMessage(msg);
			Log.w(TAG, message);
		}
	}
	
	public static void w(String tag, String msg) {
        instance.warn(tag, msg);
    }

	public static void w(String msg) {
		instance.warn(msg);
	}

	public static void w(Exception e) {
		instance.warn(e != null ? e.toString() : "null");
	}
	
	/**
	 * log.e
	 * 
	 * @param msg
	 */
	public void error(String msg) {
		if (DEBUG) {
			final String message = createMessage(msg);
			Log.e(TAG, message);
		}
	}
	
	public void error(String tag, String msg) {
		if (DEBUG) {
			final String message = createMessage(msg);
			Log.e(tag, message);
		}
	}
	
	public static void e(String msg) {
		instance.error(msg);
	}
	
	public static void e(String tag, String msg) {
		instance.error(tag, msg);
	}
	
	 
	
	public static void e(Exception e) {
		instance.error(e);
	}

	/**
	 * log.error
	 * 
	 * @param e
	 */
	public void error(Exception e) {
		if (DEBUG) {
			final StringBuffer sb = new StringBuffer();
			final String name = getFunctionName();
			final StackTraceElement[] sts = e.getStackTrace();

			if (name != null) {
				sb.append(name + " - " + e + "\r\n");
			} else {
				sb.append(e + "\r\n");
			}
			if (sts != null && sts.length > 0) {
				for (final StackTraceElement st : sts) {
					if (st != null) {
						sb.append("[ " + st.getFileName() + ":" + st.getLineNumber() + " ]\r\n");
					}
				}
			}
			Log.e(TAG, sb.toString());
		}
	}

    private static SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.UK);
    
    /**
     * 获取函数名称
     */
    private String getFunctionName() {
        final StackTraceElement[] sts = Thread.currentThread().getStackTrace();

		if (sts == null) {
			return null;
		}

		for (final StackTraceElement st : sts) {
			if (st.isNativeMethod()) {
				continue;
			}

			if (st.getClassName().equals(Thread.class.getName())) {
				continue;
			}

			if (st.getClassName().equals(this.getClass().getName())) {
				continue;
			}

            Calendar c = Calendar.getInstance(); 
            String ctime = formater.format(c.getTime());
            
			return ctime + "[" + st.getFileName()
					+ ":" + st.getLineNumber() + "]"
                    + "[" + st.getMethodName() + "]";
		}

		return null;
	}

	private String createMessage(String msg) {
		if (TextUtils.isEmpty(msg)) {
			return null;
		}
		
		final String functionName = getFunctionName();
		final String message = (functionName == null ? msg : (functionName + " - " + msg));
		
		if (isSaveToFile) {
			FileUtils.writeStringToFile(message + "\r\n", LOG_DIR, LOG_PATH, true);
		}
		
		return message;
	}
	
	/**
	 * 保存信息到文件
	 * 
	 * @param msg
	 */
	public static void saveMessage(String msg) {
		if (LogUtils.DEBUG) {
			FileUtils.writeStringToFile(msg + "\r\n", LOG_PATH, true);
		}
	}
	
}
