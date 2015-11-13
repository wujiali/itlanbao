package com.itlanbao.applib.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import android.text.TextUtils;
 

/**
 * 对象缓存到文件工具类
 * 
 * @author wjl
 *
 */
public class FileCacheUtils {

	/**
	 * 写入对象到缓存文件
	 * 
	 * @param filePath
	 * @param fileName
	 * @param mObject
	 */
	public static void writeObject(String filePath, String fileName, Object mObject) {
		writeObject(filePath, fileName, mObject, false);
	}
	
	/**
	 * 追加形式写入对象到缓存文件
	 * 
	 * @param filePath
	 * @param fileName
	 * @param mObject
	 */
	public static void writeObjectAppend(String filePath, String fileName, Object mObject) {
		writeObject(filePath, fileName, mObject, true);
	}

	/**
	 * 写入对象
	 * 
	 * @param path
	 * @param mObject
	 * @param fileStatus
	 * @param append
	 */
	private static void writeObject(String filePath, String fileName, Object mObject, boolean append) {
		final String path = checkFilePath(filePath, fileName);
		if (!TextUtils.isEmpty(path) && mObject != null) {
			
			final int fileStatus = checkFileStatus(filePath, fileName);
			if (fileStatus != FileConstants.FILE_ERROR) {
				try {
					if (LogUtils.DEBUG) {
						LogUtils.i("写::" + path);
					}
					final FileOutputStream fo = new FileOutputStream(path, append);
					final ObjectOutputStream out = new ObjectOutputStream(fo);
					
					if (append && fileStatus == FileConstants.FILE_EXISTS) {
						long pos = 0;
						pos = fo.getChannel().position() - 4;
						fo.getChannel().truncate(pos);
					}
					
					out.writeObject(mObject); 
					out.close();
					fo.close();
				} catch (Exception e) {
					LogUtils.e(e);
				}

			}
		}
	}
	
	/**
	 * 读取缓存文件中的对象
	 * 
	 * @param filePath
	 * @param fileName
	 * @return
	 */
	public static Object readObject(String filePath, String fileName) {
		return readObject(filePath, fileName, false);
	}
	
	/**
	 * 读取追加进去的数据，以ArrayList方式返回
	 * 
	 * @param filePath
	 * @param fileName
	 * @return
	 */
	public static ArrayList<?> readObjectAppend(String filePath, String fileName) {
		return (ArrayList<?>)readObject(filePath, fileName, true);
	}

	/**
	 * 读取对象
	 * 
	 * @param filePath
	 * @param fileName
	 * @return
	 */
	private static Object readObject(String filePath, String fileName, boolean isAppend) {
		final String path = checkFilePath(filePath, fileName);
		if (!TextUtils.isEmpty(path)) {
			final int fileStatus = checkFileStatus(filePath, fileName);
			
			if (fileStatus == FileConstants.FILE_EXISTS) {
				try {
					if (LogUtils.DEBUG) {
						LogUtils.i("读::" + path);
					}
					
					final FileInputStream fin = new FileInputStream(path);
					final ObjectInputStream in = new ObjectInputStream(fin);
					
					if (isAppend) {
						ArrayList<Object> objectList = new ArrayList<Object>();
						while (fin.available() > 0) {
							objectList.add(in.readObject());
						}
						in.close();
						fin.close();
						return objectList;
					} else {
						final Object mObject = in.readObject();
						in.close();
						fin.close();
						return mObject;
					}
					
					
				} catch (Exception e) {
					if (LogUtils.DEBUG) {
						e.printStackTrace();
					}
				}

			}
		}
		return null;
	}
	
	/**
	 * ArrayList追加写入到1个缓存文件中，读取缓存用readObjectAppend
	 * 
	 * @param filePath
	 * @param fileName
	 * @param objectList
	 */
	public static void writeObjectListAppend(String filePath, String fileName, ArrayList<?> objectList) {
		writeObjectListAppend(filePath, fileName, objectList, true);
	}

	/**
	 * ArrayList追加写入到1个缓存文件中，读取缓存用readObjectAppend
	 * 
	 * @param filePath
	 * @param fileName
	 * @param objectList
	 * @param append
	 */
	public static void writeObjectListAppend(String filePath, String fileName, ArrayList<?> objectList, boolean append) {
		final String path = checkFilePath(filePath, fileName);
		if (!TextUtils.isEmpty(path) && objectList != null && objectList.size() > 0) {
			final int fileStatus = checkFileStatus(filePath, fileName);
			
			if (fileStatus != FileConstants.FILE_ERROR) {
				try {
					if (LogUtils.DEBUG) {
						LogUtils.i("写::" + path);
					}
					final FileOutputStream fo = new FileOutputStream(path, append);
					final ObjectOutputStream os = new ObjectOutputStream(fo);
					if (fileStatus == FileConstants.FILE_EXISTS && append) {
						long pos = 0;
						pos = fo.getChannel().position() - 4;
						fo.getChannel().truncate(pos);
					}
					for (final Object object : objectList) {
						os.writeObject(object);
					}
					os.close();
					fo.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 检测创建文件目录
	 * 
	 * @param dirPath
	 * @param fileName
	 * @return
	 */
	public static int checkFileStatus(String dirPath, String fileName) {
		return checkFileStatus(dirPath, fileName, null);
	}
	
	public static int checkFileStatus(String dirPath, String fileName, String suffix) {

		int fileStatus;

		final String filePath = checkFilePath(dirPath, fileName, suffix);

		if (filePath == null) {
			return FileConstants.FILE_ERROR;
		}

		try {
			final File dirFile = new File(dirPath);
			if (!dirFile.exists()) {
				dirFile.mkdirs();
			}
			final File mFile = new File(filePath);
			if (!mFile.exists()) {
				fileStatus = FileConstants.FILE_CREATE;
				mFile.createNewFile();
			} else {
				final long size = mFile.length();
				if (size > 0) {
					fileStatus = FileConstants.FILE_EXISTS;
				} else {
					fileStatus = FileConstants.FILE_EXISTS_NULL;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			return FileConstants.FILE_ERROR;
		}

		return fileStatus;
	}
	
	/**
	 * 检测文件路径是否合法
	 * 
	 * @param dirPath
	 * @param fileName
	 * @return
	 */
	public static String checkFilePath(String dirPath, String fileName) {
		return checkFilePath(dirPath, fileName, null);
	}
	
	public static String checkFilePath(String dirPath, String fileName, String suffix) {
		if (!TextUtils.isEmpty(dirPath) && !TextUtils.isEmpty(fileName)) {
			if (dirPath.endsWith("/")) {
				dirPath = dirPath.substring(0, dirPath.length() - 1);
			}
			if (suffix != null) {
				return dirPath + "/" + fileName + suffix;
			} else {
				return dirPath + "/" + fileName + FileConstants.FILE_SUFFIX;
			}
		}
		return null;
	}
	
	/**
	 * 获取文件大小
	 * 
	 * @param dirPath
	 * @param fileName
	 * @return
	 */
	public static long getFileSize(String dirPath, String fileName) {
		if (!TextUtils.isEmpty(fileName) && !TextUtils.isEmpty(dirPath)) {
			final String filePath = dirPath + "/" + fileName + FileConstants.FILE_SUFFIX;
			final File file = new File(filePath);
			if (file.exists()) {
				return file.length();
			}
		}
		return 0;
	}
	
	/**
	 * 删除文件
	 */
	public static boolean deleteFile(String dirPath, String fileName) {
		if (!TextUtils.isEmpty(fileName) && !TextUtils.isEmpty(dirPath)) {
			final String filePath = dirPath + "/" + fileName + FileConstants.FILE_SUFFIX;
			final File file = new File(filePath);
			if (file.exists()) {
				return file.delete();
			}
		}
		return false;
	}
	
}
