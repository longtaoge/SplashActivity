package com.itau.jingdong.zxing;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.itau.jingdong.R;

/** 
 * @author xiang
    E-mail: xiangzhihong8@sina.com
 * 创建时间：2013-9-5 下午4:35:55 
 * 类说明 
 */

public class CaptureResultActivity extends Activity implements OnClickListener {

	private static final String TAG="CaptureResultActivity";
	 private static int RESULT_LOAD_IMAGE = 1;

	private TextView scan_result;
	private ImageView image_result,back;
	private Button listmore,saveBarcode,btn_camera,btn_openPic;
	private Bitmap bitmap;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.capture_result);
		
		initView();
		Bundle bundle=new Bundle();
		
    	scan_result.setText(getIntent().getExtras().getString("result"));
        image_result.setImageBitmap((Bitmap) getIntent().getParcelableExtra("bitmap"));
    	
		
		
	}

	private void initView() {
		scan_result=(TextView)this.findViewById(R.id.scan_result);
		image_result=(ImageView)this.findViewById(R.id.qrcode_bitmap);
	
		back=(ImageView)this.findViewById(R.id.barcode_back);
		saveBarcode=(Button)this.findViewById(R.id.save);
		btn_camera=(Button)this.findViewById(R.id.camera);
		btn_openPic=(Button)this.findViewById(R.id.openPic);
		saveBarcode.setOnClickListener(this);
		btn_camera.setOnClickListener(this);
		btn_openPic.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.barcode_back:
			
			finish();
			
			break;
			
			//保存二维码
		case R.id.save:
			saveBarcode();
			break;
			
		case R.id.camera:
			openCamera();
			break;
			
		case R.id.openPic:
			openPic();
			break;

		default:
			break;
		}
		
		
		
	}
	
	//打开保存的二维码
	private void openPic() {
		Intent i = new Intent(
		Intent.ACTION_PICK,
		android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivity(i);

	}

	
	
		//保存生成的二维码图片
		private void saveBarcode() {
			bitmap=getIntent().getParcelableExtra("bitmap");
			
			 ByteArrayOutputStream baos = new ByteArrayOutputStream();
			 bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
			 try {
				Writer(baos.toByteArray());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	//将图片通过流的方式写入到文件中去
		private void Writer(byte[] byteArray) throws IOException {
			File cacheFile =null;
			if (Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED)) {
				File sdCardDir = Environment.getExternalStorageDirectory();
				
				long time=Calendar.getInstance().getTimeInMillis();
				String fileName =time+".png";
				File dir = new File(sdCardDir.getCanonicalPath()
						+"/querysys/BarCode/");
				if (!dir.exists()) {
					dir.mkdirs();
				}
				cacheFile = new File(dir, fileName);
			
			}  
			
			BufferedOutputStream bos = null;
			try {
				bos = new BufferedOutputStream(new FileOutputStream(cacheFile));
				
					bos.write(byteArray,0,byteArray.length);
					bos.close();
					Toast toast = Toast.makeText(getApplicationContext(),
							"图片保存在了内存卡下BarCode文件夹下", Toast.LENGTH_LONG);
				      	   toast.setGravity(Gravity.CENTER, 0, 0); 
				      	   toast.show();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	//打开camera，实现拍照操作
	private void openCamera() {
		Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(intent, 1);
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		
		if (resultCode == Activity.RESULT_OK){
			String sdStatus=Environment.getExternalStorageState();
			if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
                Log.v("TestFile",
                        "SD card is not avaiable/writeable right now.");
                return;
            }
			
			Bundle bundle=data.getExtras();
			//获得相机的返回的数据
			Bitmap bitmap=(Bitmap) bundle.get("data");
			//获得sdcard路径
			File file=null;
			File saveDir = null;
			String pic = null;
			FileOutputStream fileoutputStream=null;
			try {
			File sdcardDir=Environment.getExternalStorageDirectory();
			long time=Calendar.getInstance().getTimeInMillis();
			 pic=time+".png";
             saveDir = new File(sdcardDir.getCanonicalPath()
						+"/query/saveCamer/");
            if (!saveDir.exists()) {
    			saveDir.mkdirs();
    		}
    		file = new File(saveDir, pic);
    		//保存文件的名称
			String picName=saveDir+"/"+pic;
		   fileoutputStream=new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileoutputStream);// 把数据写入文件
			
			Toast.makeText(CaptureResultActivity.this, "图片已成功保存", 1500).show();
			fileoutputStream.flush();
			fileoutputStream.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	
	/*@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    
    if(resultCode==RESULT_OK){
    	Bundle bundle=data.getExtras();
    	scan_result.setText(bundle.getShort("result"));
    	image_result.setImageBitmap((Bitmap)data.getParcelableExtra("bitmap"));
    	
    }
	}*/
}
