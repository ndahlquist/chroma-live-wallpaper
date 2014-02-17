package edu.stanford.nicd.chroma;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class InfoActivity extends Activity {

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        Toast toast = Toast.makeText(this, R.string.choose_this_wallpaper, Toast.LENGTH_LONG);
        toast.show();

        Intent intent = new Intent();
        intent.setAction(WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER);
        //intent.setAction(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER); // TODO
        //String pkg = Service.class.getPackage().getName();
        //String cls = MyWallpaperService.class.getCanonicalName();
        //intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT, new ComponentName(pkg, cls));
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        finish();
    }
}
