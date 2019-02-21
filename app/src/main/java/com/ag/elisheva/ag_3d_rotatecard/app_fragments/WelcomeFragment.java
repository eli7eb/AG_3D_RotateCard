package com.ag.elisheva.ag_3d_rotatecard.app_fragments;

import android.content.Context;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.opengl.GLSurfaceView;

import com.ag.elisheva.ag_3d_rotatecard.MainActivity;
import com.ag.elisheva.ag_3d_rotatecard.demo.SceneLoader;
import com.ag.elisheva.ag_3d_rotatecard.view.LogoGLSurfaceView;

import org.andresoviedo.util.android.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class WelcomeFragment extends Fragment {

    // TODO: Rename and change types of parameters
    private static final String ARG_PARAM1 = "package";
    private static final String ARG_PARAM2 = "file_name";
    private String mParam1;
    private String mParam2;
    private String tag = WelcomeFragment.class.getSimpleName();
    private Button goButton;
    onChoiceSelectedListener mCallback;

    public GLSurfaceView getGlView() {
        return glView;
    }
    public String test;
    private LogoGLSurfaceView glView;
    private SceneLoader scene;
    public WelcomeFragment() {
        super();
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *

     * @return A new instance of fragment WelcomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WelcomeFragment newInstance(String param1, String param2) {

        WelcomeFragment fragment = new WelcomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String [] list = {};
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        InputStream inStream = null;
        Context context = MainActivity.getContext();
        AssetManager am = context.getResources().getAssets();
        try {
            list = am.list("rose");
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (String s:list) {
            String extension = s.substring(s.lastIndexOf("."));
            if (extension.equalsIgnoreCase(".obj")) {
                try {
                    inStream = am.open("rose/" + s);
                    break;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

            //I use this next line as the start of copying a pdf from one location
            //to another. I wanted to give you a real function in use.
        scene = new SceneLoader(this.getActivity(), this.getActivity());
        Uri uri = Uri.parse("assets://" + mParam1 + "/" + mParam2);
        scene.init(uri);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        try {

            glView = new LogoGLSurfaceView(this.getActivity());           // Allocate a GLSurfaceView

        } catch (Error e) {
            Log.d(tag,"Error "+e.getMessage());
        }
        glView.setScene(scene);

        return glView;


    };


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        /*try {
            mCallback = (onChoiceSelectedListener) context;
        }catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnHeadlineSelectedListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }


    // Call back when the activity is going into the background
    @Override
    public void onPause() {
        super.onPause();
        glView.onPause();
    }

    // Call back after onPause()
    @Override
    public void onResume() {
        super.onResume();
        glView.onResume();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface onChoiceSelectedListener {
        // TODO: Update argument type and name
        void onChoiceSelected(String item);
    }
}
