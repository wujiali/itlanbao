package com.itlanbao.applib;

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

import com.itlanbao.applib.util.BaseKeyConstants;
import com.itlanbao.applib.util.ClipImageLayout;
import com.itlanbao.applib.util.ImageUtil;
 

public class BaseClipImageActivity extends Activity{
	
	private ClipImageLayout mClipImageLayout;
	private final String Tag = "ClipImage";
	private String outPutUrl = "";
	private int width ,height;
	 
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.base_clip_image_layout);

		width = getResources().getDisplayMetrics().widthPixels;
		height = getResources().getDisplayMetrics().heightPixels;
		
		final Uri inputUri = getIntent().getParcelableExtra(BaseKeyConstants.KEY_IMAGE_PATH);
		final Uri outPutUri = getIntent().getParcelableExtra(BaseKeyConstants.KEY_IMAGE_OUTPUT);
		int clipImageWid = getIntent().getIntExtra(BaseKeyConstants.KEY_IMAGE_OUTPUT_X, 330);
		int clipImageHgt = getIntent().getIntExtra(BaseKeyConstants.KEY_IMAGE_OUTPUT_Y, 330);
		if (outPutUri != null) {
			outPutUrl = ImageUtil.getPicPathFromUri(outPutUri, this);
		} 
		if (inputUri != null) {
			final String path = ImageUtil.getPicPathFromUri(inputUri, this); 
			mClipImageLayout = (ClipImageLayout) findViewById(R.id.id_clipImageLayout);
 
			mClipImageLayout.init(clipImageWid, clipImageHgt, path);
		}
		findViewById(R.id.sure).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				new SaveAsyn().execute();
			
			}
		});

	}
	
	class SaveAsyn extends AsyncTask<Void, Void, Bitmap>{

		@Override
		protected Bitmap doInBackground(Void... params) {
			Bitmap bitmap = mClipImageLayout.clip(); 
			ImageUtil.saveImage(bitmap, outPutUrl); 
			return bitmap;
		}
		
		@Override
		protected void onPostExecute(Bitmap result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if(result!=null){
				setResult(RESULT_OK);
				finish();
			}
		}
		
	
	}

	 
 
}
