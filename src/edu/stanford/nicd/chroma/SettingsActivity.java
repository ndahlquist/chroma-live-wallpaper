package edu.stanford.nicd.chroma;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class SettingsActivity extends Activity  {

	//implements SharedPreferences.OnSharedPreferenceChangeListener TODO

	private Button chooseImageButton;
	private Button showLicenseButton;
	//private ImageView imageDisplay;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//View tempView = getLayoutInflater().inflate(R.layout.settings_layout, null);
		//setContentView(tempView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
		setContentView(R.layout.settings_layout);

		chooseImageButton = (Button) findViewById(R.id.chooseImageButton);
		showLicenseButton = (Button) findViewById(R.id.licenseButton);
		//imageDisplay = (ImageView) findViewById(R.id.imageDisplay);

		chooseImageButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				intent.setType("image/*");
				startActivityForResult(intent, 0);
			}
		});
		
		showLicenseButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO
			}
		});
	}

	/*public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		// TODO Auto-generated method stub

	}*/

	/*@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected (MenuItem item) {
		Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		intent.setType("image/*");
		startActivityForResult(intent, 0);
		return true;
	}

	// http://stackoverflow.com/questions/2507898/how-to-pick-a-image-from-gallery-sd-card-for-my-app-in-android
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);

		if(resultCode != RESULT_OK) return;

		MyRenderer.myBitmap.recycle();
		MyRenderer.myBitmap = null;

		Uri imageUri = data.getData();

		// Decode image size
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        try {
			BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri), null, options);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        // Find the correct scale value. It should be the power of 2.
        int width_tmp = options.outWidth;
        int height_tmp = options.outHeight;
        int scale = 1;
        while (width_tmp / 2 >= 1.2 * screenDimensions[0] && height_tmp / 2 >= 1.2 * screenDimensions[1]) {
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        // Decode with inSampleSize
        options = new BitmapFactory.Options();
        options.inSampleSize = scale;
        try {
        	_renderer.loadBitmap(BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri), null, options));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}*/
}
