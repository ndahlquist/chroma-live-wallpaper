package edu.stanford.nicd.chroma;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Intent;
import android.os.Bundle;

// Adapted from www.yougli.net/android/live-wallpaper-binding-an-activity-to-the-open-button-of-the-market/
public class InfoActivity extends Activity {
    
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        
        //Toast toast = Toast.makeText(this, R.string.choose_this_wallpaper, Toast.LENGTH_LONG);
        //toast.show();
        
        Intent intent = new Intent(); // TODO: test compatibility with pre-4.1
        //intent.setAction(WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER);
        intent.setAction(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
        startActivityForResult(intent, 0);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	finish();
    }
}
