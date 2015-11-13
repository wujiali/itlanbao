package com.itlanbao.listener;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.itlanbao.applib.BaseClipImageActivity;
import com.itlanbao.applib.ItLanbaoLibApplication;
import com.itlanbao.applib.R;
import com.itlanbao.applib.util.BaseKeyConstants;
import com.itlanbao.applib.util.FileConstants;
import com.itlanbao.applib.util.FileUtils;
import com.itlanbao.applib.util.ImageUtil;
 

/**
 * 插入图片dialog
 * 
 * @author wjl
 * 
 */
public class InsertPictureDialog extends DialogFragment {
	
  
	
	protected final int TAKE_PICTURE = 0;
	protected final int LOCAL_PICTURE = 1;
	protected final int CROP_PICTURE = 3;
	protected final int CUSTOM_LOCAL_PICTURE = 4;
	protected InsertionPictureOnFinishListener onFinishListener;
	protected InsertionMultiplePictureOnFinishListener onMultipleFinishListener;
	protected InsertPictureOnCancelListener onCancelListener;
	// 图片最大不能超过1m
	private static final int BITMAP_MAX_SIZE = 1 * 1024 * 1024;
	// 图片最小不能低于10k
	private static final int BITMAP_MIN_SIZE = 10 * 1024;
	
	/**
	 * 图片路径
	 */
	private String mCurrentPhotoPath = null;
	
	/**
	 * 图片裁切使用的uri
	 */
	private Uri mCropUri = null;
	private int uploadType = 1 ;
	
	/**
	 * @param uploadType 上传类型，1->修改头像, 2->多图选择, 3->普通图片, 4->需要裁切头像
	 * @param isCrop 是否裁切
	 * @return
	 */
	public static InsertPictureDialog newInstance(int uploadType, boolean isCrop) {
		final InsertPictureDialog insertPictureDialog = new InsertPictureDialog();
		final Bundle args = new Bundle();
        args.putInt(BaseKeyConstants.KEY_UPLOADTYPE, uploadType);
        args.putBoolean(BaseKeyConstants.KEY_ISCROP, isCrop);
        insertPictureDialog.setArguments(args);
        return insertPictureDialog;
    }
	
 
	
 
 
 
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// 上传图片类型
		uploadType = getArguments().getInt(BaseKeyConstants.KEY_UPLOADTYPE, 1);
     
		
		if (savedInstanceState != null && !savedInstanceState.isEmpty()) {
			mCurrentPhotoPath = savedInstanceState.getString(BaseKeyConstants.KEY_CURRENTPHOTOPATH);
    	}
 
		View v = inflater.inflate(R.layout.insert_picture_dialog_layout, container, false);
     
	 
		// 拍照
		final LinearLayout takePicture = (LinearLayout) v.findViewById(R.id.take_picture);
		takePicture.setOnClickListener(new TextView.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				try {
					mCurrentPhotoPath = null;
					
					final Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					if (FileUtils.checkSDCard()) {
						// 设置图片保存图片的路径
						final File file = new File(FileUtils.getDiskCacheDir(getActivity()).getAbsolutePath() 
								+ FileConstants.CACHE_IMAGE_DIR, "" + System.currentTimeMillis() + ".jpg");
						 
						mCurrentPhotoPath = file.getAbsolutePath();
						i.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
					}
					
					startActivityForResult(i, TAKE_PICTURE);
					 
				} catch (Exception e) {
					e.printStackTrace();
//					BaseTools.showToast(R.string.str_cant_insert_album);
				}
			}
		});
		
		// 本地上传
		final LinearLayout localPicture = (LinearLayout) v.findViewById(R.id.local_picture);
		localPicture.setOnClickListener(new TextView.OnClickListener() {
			
			@Override
			public void onClick(View v) throws SecurityException {
			 
		        // 获取系统可用的相册 
                try {
                    final Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                    startActivityForResult(intent, LOCAL_PICTURE);
                } catch (Exception e) {
                    e.printStackTrace();
                    try {
                        final Intent getImage = new Intent(Intent.ACTION_GET_CONTENT);
                        getImage.addCategory(Intent.CATEGORY_OPENABLE);
                        getImage.setType("image/*");
                        startActivityForResult(getImage, LOCAL_PICTURE);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                        try {
                            final Intent getImage = new Intent(Intent.ACTION_GET_CONTENT);
                            getImage.setType("image/*");
                            startActivityForResult(Intent.createChooser(getImage, "请选择相册"), LOCAL_PICTURE);
                        } catch (Exception e2) {
                            e1.printStackTrace();
//                            BaseTools.showToast("抱歉！无法打开相册");
                        }
                    }
                } 
			}
		});
		
		// 触摸内容区域外的需要关闭对话框
		v.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(v instanceof ViewGroup){
					View layoutView = ((ViewGroup)v).getChildAt(0);
					if(layoutView != null){
						float y = event.getY();
						float x = event.getX();
						Rect rect = new Rect(layoutView.getLeft(), layoutView.getTop(), layoutView.getRight(), layoutView.getBottom());
						if(!rect.contains((int)x, (int)y)){
						    if (onCancelListener != null) {
					            onCancelListener.onCancel();
					        }
							dismiss();
						}
						rect.setEmpty();
						rect = null ;
					}
				}
				return false;
			}
		});
		
		return v;
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case LOCAL_PICTURE:// 如果是直接从相册获取
				if (data != null) {
					
					// 是否裁切
					final boolean isCrop = getArguments().getBoolean(BaseKeyConstants.KEY_ISCROP, false);
					
					final Uri uri = data.getData();
					if (uri == null) {
						return;
					}
						
					if (isCrop) {
						startPhotoZoom(uri);
					} else {
						final String path = ImageUtil.getPicPathFromUri(uri, getActivity());
						resizePhoto(path,uploadType);
					}
				}
				break;
			case TAKE_PICTURE:// 如果是调用相机拍照时
				 
				
				// 是否裁切
				final boolean isCrop = getArguments().getBoolean(BaseKeyConstants.KEY_ISCROP, false);
				
				// 添加到图库,这样可以在手机的图库程序中看到程序拍摄的照片
				galleryAddPic(mCurrentPhotoPath);
				
				if (TextUtils.isEmpty(mCurrentPhotoPath)) {
					mCurrentPhotoPath = getImgPath(data);
				}
				
				if (isCrop) {
					final File f = new File(mCurrentPhotoPath);
					startPhotoZoom(Uri.fromFile(f));
				} else {
					resizePhoto(mCurrentPhotoPath,uploadType);
				}
				
				break;
			case CROP_PICTURE:// 取得裁剪后的图片
				if (mCropUri != null) {
					final String path = ImageUtil.getPicPathFromUri(mCropUri, getActivity());
					setPicToView(path, mCropUri,uploadType);
                }
				break;
			case CUSTOM_LOCAL_PICTURE:
                if (data != null) {
                    @SuppressWarnings("unchecked")
                    final ArrayList<String> listImage = (ArrayList<String>) 
                            data.getSerializableExtra(BaseKeyConstants.KEY_LISTIMAGE);
                    if (listImage != null && listImage.size() > 0) {
                        if (onMultipleFinishListener != null) {
                            onMultipleFinishListener.onAddImageFinish(listImage);
                        }
                        dismiss();
                    }
                }
			}
		}
	}
	
	private void resizePhoto(String photoPath,int uploadType) {
		if (!TextUtils.isEmpty(photoPath)) {
			if(uploadType == 3){
				final File file = new File(FileUtils.getDiskCacheDir(getActivity()).getAbsolutePath() + FileConstants.CACHE_IMAGE_DIR);
				String destPath = file.getAbsolutePath() + "/" + photoPath.substring(photoPath.lastIndexOf("/") + 1, photoPath.length());
				File file2 = new File(destPath);
				if(!file2.exists()){// 多次选择同一张图片不需要再次处理
					FileUtils.copyFile(photoPath, destPath);
				}
				
				photoPath = destPath ;
			}
			
			final Bitmap bm = ImageUtil.getSmallBitmap(photoPath);
			if (bm != null) {
			    
			    final File f = new File(photoPath);
			    if (!f.exists()) {
			        try {
                        f.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
			    }
			    
			   
			    
			    // 判断图片大小，如果超过1m就进行压缩
			    if (f.length() > BITMAP_MAX_SIZE) {
			        try {
                        final FileOutputStream fos = new FileOutputStream(f);
                        bm.compress(CompressFormat.JPEG, 80, fos);
                        fos.flush();
                        fos.close();
                        
                       
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
			    } else if (f.length() <= BITMAP_MIN_SIZE) {
			        try {
	                    if (!bm.isRecycled()) {
	                        bm.recycle();
	                    }
	                } catch (Exception e) {
	                    e.printStackTrace();
	                }
//			        BaseTools.showToast("图片大小不能低于10k");
			        return;
			    }
			    
				try {
					if (!bm.isRecycled()) {
						bm.recycle();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				// 通知回调方法设置图片
                setPicToView(photoPath, Uri.fromFile(f), uploadType);
			}
			
		}
	}
	
	/**
	 * 添加到图库
	 */
	private void galleryAddPic(String path) {
		try {
			final Intent mediaScanIntent = new Intent(
					Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
			final File f = new File(path);
			final Uri contentUri = Uri.fromFile(f);
			mediaScanIntent.setData(contentUri);
			getActivity().sendBroadcast(mediaScanIntent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取图片存储路径
	 * @param data
	 * @return
	 */
	private String getImgPath(Intent data) {
		// 获取图片存储路径
		String path = "";
				
		if(data == null) {
			return path;
		}
		
		final Uri uri = data.getData();		
		if (uri != null)
		{
			// 获取SD卡存储路径
			// 某些手机拍照后，照片存储在SD卡上，如：HTC desire HD(a9191):
			path = ImageUtil.getPicPathFromUri(uri, getActivity());
			 

			// 拍照后(某些手机拍照后不存储在SD卡上)，
			// /data/data/com.miui.camera/files/image-temp.jpg
			if (TextUtils.isEmpty(path)) {
				path = uri.getPath();
				 
			}
		} else {
			final Bundle extras = data.getExtras();
			if (extras != null) {
				try{
					// 将返回的图片源存到sd卡中
					final Bitmap bmp = (Bitmap) extras.get("data");				
					final String fileName = "paizhao.jpg";
					if (!FileUtils.checkSDCard() && bmp != null) {
						// 将Bmp存到内部存储卡
						final FileOutputStream outStream = getActivity().openFileOutput(fileName,
								Context.MODE_WORLD_WRITEABLE);
						final BufferedOutputStream bos = new BufferedOutputStream(outStream);
						bmp.compress(Bitmap.CompressFormat.JPEG, 100, bos);
						bos.flush();
						bos.close();
			
						final File file = getActivity().getFileStreamPath(fileName);
						if (file != null) {
							path = file.getPath();				
//							if (LogUtils.DEBUG) {
//								LogUtils.e("PATH3: 返回BMP，将其存入内存卡[data/data/]" + path);
//							}
						}
					}
					
					// 释放Bitmap图片
					if (bmp != null && bmp.isRecycled()) {
						bmp.recycle();
					}
				
				}catch(Exception e){
					e.printStackTrace();
//					if (LogUtils.DEBUG) {
//						LogUtils.e("上传图片获取图片错误");
//					}
				}	
			}
		}
		return path;
	}
	
	/**
	 * 裁剪图片方法实现
	 * 
	 * @param uri
	 */
	public void startPhotoZoom(Uri uri) {
//		if (LogUtils.DEBUG) {
//			LogUtils.e("startPhotoZoom uri= " + uri);
//		}
		final File file = new File(FileUtils.getDiskCacheDir(getActivity()).getAbsolutePath() 
		        + FileConstants.CACHE_IMAGE_DIR, String.valueOf(System.currentTimeMillis()) + ".jpg");
		mCropUri = Uri.fromFile(file);
		
		final Intent intent = new Intent(ItLanbaoLibApplication.getInstance(), BaseClipImageActivity.class);
		intent.putExtra(BaseKeyConstants.KEY_IMAGE_PATH, uri);
		//裁剪图片宽高
		intent.putExtra(BaseKeyConstants.KEY_IMAGE_OUTPUT_X,(int)getResources().getDimension(R.dimen.img_w));
		intent.putExtra(BaseKeyConstants.KEY_IMAGE_OUTPUT_Y, (int)getResources().getDimension(R.dimen.img_h));
        intent.putExtra(BaseKeyConstants.KEY_IMAGE_OUTPUT, mCropUri);
		startActivityForResult(intent, CROP_PICTURE);
	}
	
	/**
	 * 保存裁剪之后的图片数据
	 * 
	 * @param uri
	 */
	private void setPicToView(String imagePath, Uri uri,int uploadType) {
//		if (LogUtils.DEBUG) {
//			LogUtils.d("setPicToView -->imagePath " + imagePath + ", imageFileUri " + uri);
//		}
		
		if(uploadType == 3){
			int height = (int) ImageUtil.applyDimension(getActivity(),TypedValue.COMPLEX_UNIT_DIP, 55);
			if (onFinishListener != null) {
			    onFinishListener.onAddImageFinish(imagePath, ImageUtil.getBitmapFromFile(imagePath,height, height));
			} else if (onMultipleFinishListener != null) {
			    final ArrayList<String> listImage = new ArrayList<String>();
			    listImage.add(imagePath);
			    onMultipleFinishListener.onAddImageFinish(listImage);
			}
		}else {
		    if (onFinishListener != null) {
		        onFinishListener.onAddImageFinish(imagePath, ImageUtil.getBitmapFromUri(getActivity(), uri));
		    } else if (onMultipleFinishListener != null) {
                final ArrayList<String> listImage = new ArrayList<String>();
                listImage.add(imagePath);
                onMultipleFinishListener.onAddImageFinish(listImage);
            }
		}
		
		dismiss();
	}
	
	 @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setStyle(DialogFragment.STYLE_NO_FRAME, android.R.style.Theme_Translucent);
    }

    @Override
    public void dismiss() {
    	try {
			super.dismissAllowingStateLoss();
		} catch (Exception e) {
			e.printStackTrace();
			super.dismiss();
		}
    }

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onCancel(DialogInterface dialog) {
	    if (onCancelListener != null) {
	        onCancelListener.onCancel();
	    }
		super.onCancel(dialog);
	}

	@Override
	public void onDismiss(DialogInterface dialog) {
		super.onDismiss(dialog);
	}

	@Override
	public void show(FragmentManager manager, String tag) {
		try {
			super.show(manager, tag);
		} catch (IllegalStateException e) {
			e.printStackTrace();
			// 解决java.lang.IllegalStateException: Can not perform this action问题
			final FragmentTransaction ft = manager.beginTransaction();
	        ft.add(this, tag);
	        ft.commitAllowingStateLoss();
		}
	}

	@Override
	public int show(FragmentTransaction transaction, String tag) {
		try {
			return super.show(transaction, tag);
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	@Override
	public void onSaveInstanceState(Bundle arg0) {
		super.onSaveInstanceState(arg0);
		if (arg0 != null) {
			arg0.putString(BaseKeyConstants.KEY_CURRENTPHOTOPATH, mCurrentPhotoPath);
		}
	}
	
	public void setOnFinishListener(InsertionPictureOnFinishListener listener) {
		onFinishListener = listener;
	}

	public interface InsertionPictureOnFinishListener {
		public void onAddImageFinish(String imagePath, Bitmap bitmap);
	}
	
	public void setOnMultipleFinishListener(InsertionMultiplePictureOnFinishListener onMultipleFinishListener) {
	    this.onMultipleFinishListener = onMultipleFinishListener;
	}
	
	public void setInsertPictureOnCancelListener(InsertPictureOnCancelListener onCancelListener) {
	    this.onCancelListener = onCancelListener;
	}
	
	public interface InsertionMultiplePictureOnFinishListener {
        /**
         * 多选图片
         * 
         * @param listImage
         */
        public void onAddImageFinish(ArrayList<String> listImage);
        
    }
	
	public interface InsertPictureOnCancelListener {
        /**
         * 提示框去掉
         */
        public void onCancel();
        
    }
}
