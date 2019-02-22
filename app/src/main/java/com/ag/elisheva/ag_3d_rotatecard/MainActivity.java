package com.ag.elisheva.ag_3d_rotatecard;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import org.intellij.lang.annotations.Language;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import android.os.Bundle;
import android.widget.ProgressBar;

import com.ag.elisheva.ag_3d_rotatecard.app_fragments.WelcomeFragment;

import static android.opengl.GLUtils.getEGLErrorString;
import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity  implements WelcomeFragment.onChoiceSelectedListener {

    private static MainActivity instance;
    private String tag = MainActivity.class.getSimpleName();
    WelcomeFragment welcomeFragment;



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    /**
     * constructor
     */
    public MainActivity() {
        instance = this;
    }

    /**
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        initApp(true);


    }

    /**
     *
     * @param debuggable
     */
    private void initApp(boolean debuggable) {

        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        try {

            welcomeFragment = new WelcomeFragment();
            Bundle bundle = new Bundle();

            String asset_package = getPackageName();

            bundle.putString("package",asset_package);
            bundle.putString("file_name_obj","rose/roselogo2.obj");
            bundle.putString("file_name_mtl","rose/roselogo2.mtl");
            bundle.putString("file_name_bmp","rose/logo_color.bmp");
            welcomeFragment.setArguments(bundle);
            fragmentTransaction.add (R.id.fragment_container, welcomeFragment);
            fragmentTransaction.commit();

        }catch(Exception ex) {
            if (debuggable) {
                // This is printed only when debuggable is true
                ex.printStackTrace();
            }
        }
    }

    /*
     * Back from choice
     * close choice fragment
     * calculate the data for the puzzle
     */
    public void onChoiceSelected(String item) {
        // The user selected the headline of an article from the HeadlinesFragment
        // Do something here to display that article

        // TODO remember where I left and go back to that rack
        /*String title = value.get_title();

        bundle.putString("id",value.get_id());

        bundle.putString("title",title);
        bundle.putString("long_title",value.get_long_title());
        bundle.putString("name_on_disk",value.get_name_on_disk());
        bundle.putString("folder_name",value.get_folder_name());
        tour_fragment.setArguments(bundle);*/




    }

    /**
     * for getting the context
     * @return
     */
    public static Context getContext() {
        return instance;
    }

}
