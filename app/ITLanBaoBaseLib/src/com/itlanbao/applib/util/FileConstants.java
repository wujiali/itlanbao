package com.itlanbao.applib.util;
 
/**
 * File相关的常量值
 * 
 * @author wjl
 *
 */
public class FileConstants
{
	// app主文件夹名称
	public static final String RESOURCE_DIRECTORY = "/itlanbao/";
	
    // 图片缓存文件夹
    public static final String CACHE_IMAGE_DIR = "/image/";
    
    // 语音缓存文件夹
    public static final String CACHE_AUDIO_DIR = "/audio/";
   
    // 消息缓存文件夹
    public static final String CACHE_MESSAGE_DIR = "/message/";
    
    // 缓存文件后缀
	public static final String FILE_SUFFIX = /*".cache"*/"";
	
	// 写入失败
	public static final int FILE_ERROR_WRITE = -2;
	
	// 文件错误
	public static final int FILE_ERROR = -1;
	
	// 此次操作执行建立
	public static final int FILE_CREATE = 0;
	
	// 文件已经建立不为空
	public static final int FILE_EXISTS = 1;
	
	// 文件已经建立为空
	public static final int FILE_EXISTS_NULL = 2;
}
