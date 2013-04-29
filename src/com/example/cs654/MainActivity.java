package com.example.cs654;
import  java.io.File;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import android.widget.ImageView;
public class MainActivity extends Activity {
	private Uri mImageCaptureUri;
	private ImageView mImageView;
	
	private static final int PICK_FROM_CAMERA = 1;
	private static final int CROP_FROM_CAMERA = 2;
	private static final int PICK_FROM_FILE = 3;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        final String [] items			= new String [] {"Take from camera", "Select from gallery"};				
		ArrayAdapter<String> adapter	= new ArrayAdapter<String> (this, android.R.layout.select_dialog_item,items);
		AlertDialog.Builder builder		= new AlertDialog.Builder(this);
		
		builder.setTitle("Select Image");
		builder.setAdapter( adapter, new DialogInterface.OnClickListener() {
			public void onClick( DialogInterface dialog, int item ) { //pick from camera
				if (item == 0) {
					Intent intent 	 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					
					mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(),
									   "tmp_avatar_" + String.valueOf(System.currentTimeMillis()) + ".jpg"));

					intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);

					try {
						intent.putExtra("return-data", true);
						
						startActivityForResult(intent, PICK_FROM_CAMERA);
					} catch (ActivityNotFoundException e) {
						e.printStackTrace();
					}
				} else { //pick from file
					Intent intent = new Intent();
					intent.setType("image/*");
	                intent.setAction(Intent.ACTION_GET_CONTENT);
	                startActivityForResult(Intent.createChooser(intent, "Complete action using"), PICK_FROM_FILE);
				}
			}
		} );
		final AlertDialog dialog = builder.create();
		Button button 	= (Button) findViewById(R.id.btn_crop);
		mImageView		= (ImageView) findViewById(R.id.iv_photo);
		button.setOnClickListener(new View.OnClickListener() {	
			@Override
			public void onClick(View v) {
				dialog.show();
			}
		});
    }
    
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (resultCode != RESULT_OK) return;
	   
	    switch (requestCode) {
		    case PICK_FROM_CAMERA:
		    	doCrop();
		    	
		    	break;
		    	
		    case PICK_FROM_FILE: 
		    	mImageCaptureUri = data.getData();
		    	
		    	doCrop();
	    
		    	break;	    	
	    
		    case CROP_FROM_CAMERA:	    	
		        Bundle extras = data.getExtras();
	
		        if (extras != null) {	        	
		            Bitmap photo = extras.getParcelable("data");
		            
		            mImageView.setImageBitmap(photo);
		            int height= photo.getHeight();
		            int width= photo.getHeight();
		            String str = Integer.toString(height);
		            String str2 = Integer.toString(width);
		            int x = (int)height/2;
		            int y = (int)width/2;
		            int mcolor= photo.getPixel(x, y);
		            int r1 = Color.red(mcolor);
		            int g1 = Color.green(mcolor);
		            int b1 = Color.blue(mcolor);
		            String r = Integer.toString(r1);
		            String g = Integer.toString(g1);
		            String b = Integer.toString(b1);
		            String col= "";
		            if(mcolor ==Color.WHITE)
		            	col = "WHITE";
		            if(mcolor ==Color.BLACK)
		            	col = "BLACK";
		            if(mcolor ==Color.BLUE)
		            	col = "BLUE";
		            if(mcolor ==Color.CYAN)
		            	col = "CYAN";
		            if(mcolor ==Color.DKGRAY)
		            	col = "DKGRAY";
		            if(mcolor ==Color.GRAY)
		            	col = "GRAY";
		            if(mcolor ==Color.GREEN)
		            	col = "GREEN";
		            if(mcolor ==Color.LTGRAY)
		            	col = "LTGRAY";
		            if(mcolor ==Color.GREEN)
		            	col = "GREEN";
		            if(mcolor ==Color.MAGENTA)
		            	col = "MAGENTA";
		            if(mcolor ==Color.RED)
		            	col = "RED";
		            if(mcolor ==Color.TRANSPARENT)
		            	col = "TRANSPARENT";
		            if(mcolor ==Color.YELLOW)
		            	col = "YELLOW";
		            
		            Log.d("colors"," "+mcolor);
		            
//
		            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		            alertDialog.setTitle("Color");
		            alertDialog.setMessage("HEX:  "+ Integer.toHexString(mcolor) + "\n" + "RGB:  " +r+" "+" "+g+"  "+b+"\n"+col);
		            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog, int which) {
		                Log.d("colorssssssss"," hello");
		            }
		            });
		            alertDialog.setIcon(R.drawable.ic_launcher);
		            alertDialog.show();		            
//
		         }
		        File f = new File(mImageCaptureUri.getPath());            
		        if (f.exists()) f.delete();
		        break;

	    }
	}
    private void doCrop() {
		final ArrayList<CropOption> cropOptions = new ArrayList<CropOption>();
    	
    	Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setType("image/*");
        
        List<ResolveInfo> list = getPackageManager().queryIntentActivities( intent, 0 );
        
        int size = list.size();
        
        if (size == 0) {	        
        	Toast.makeText(this, "Can not find image crop app", Toast.LENGTH_SHORT).show();
        	
            return;
        } else {
        	intent.setData(mImageCaptureUri);
            
            intent.putExtra("outputX", 200);
            intent.putExtra("outputY", 200);
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("scale", true);
            intent.putExtra("return-data", true);
            
        	if (size == 1) {
        		Intent i 		= new Intent(intent);
	        	ResolveInfo res	= list.get(0);
	        	
	        	i.setComponent( new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
	        	
	        	startActivityForResult(i, CROP_FROM_CAMERA);
        	} else {
		        for (ResolveInfo res : list) {
		        	final CropOption co = new CropOption();
		        	
		        	co.title 	= getPackageManager().getApplicationLabel(res.activityInfo.applicationInfo);
		        	co.icon		= getPackageManager().getApplicationIcon(res.activityInfo.applicationInfo);
		        	co.appIntent= new Intent(intent);
		        	
		        	co.appIntent.setComponent( new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
		        	
		            cropOptions.add(co);
		        }
	        
		        CropOptionAdapter adapter = new CropOptionAdapter(getApplicationContext(), cropOptions);
		        
		        AlertDialog.Builder builder = new AlertDialog.Builder(this);
		        builder.setTitle("Choose Crop App");
		        builder.setAdapter( adapter, new DialogInterface.OnClickListener() {
		            public void onClick( DialogInterface dialog, int item ) {
		                startActivityForResult( cropOptions.get(item).appIntent, CROP_FROM_CAMERA);
		            }
		        });
	        
		        builder.setOnCancelListener( new DialogInterface.OnCancelListener() {
		            @Override
		            public void onCancel( DialogInterface dialog ) {
		               
		                if (mImageCaptureUri != null ) {
		                    getContentResolver().delete(mImageCaptureUri, null, null );
		                    mImageCaptureUri = null;
		                }
		            }
		        } );
		        
		        AlertDialog alert = builder.create();
		        
		        alert.show();
        	}
        }
	}
}