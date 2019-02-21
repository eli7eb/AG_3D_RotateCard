package org.andresoviedo.android_3d_model_engine.services;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.andresoviedo.android_3d_model_engine.model.Object3DData;

import java.io.InputStream;
import java.util.List;

/**
 * This component allows loading the model without blocking the UI.
 *
 * @author andresoviedo
 */
public abstract class OriginalLoaderTask extends AsyncTask<Void, Uri, List<Object3DData>> {
    private String tag = OriginalLoaderTask.class.getSimpleName();
    /**
     * URL to the 3D model
     */
    //protected final Uri uri;
    protected Uri uri;
    /**
     * Callback to notify of events
     */
    private final Callback callback;
    /**
     * The dialog that will show the progress of the loading
     */
    // private final ProgressDialog dialog;

    /**
     * @param context
     * @param uri
     * @param callback
     */
    public OriginalLoaderTask(Context context, Uri uri, Callback callback) {
        this.uri = uri;
        Log.d(tag, "Loading model data...");

        // this.dialog = ProgressDialog.show(this.parent, "Please wait ...", "Loading model data...", true);
        // this.dialog.setTitle(modelId);
        // this.dialog = new ProgressDialog(context);
        this.callback = callback;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.d(tag, "onPreExecute...");
		/*this.dialog.setMessage("Loading...");
		this.dialog.setCancelable(false);
		this.dialog.show();*/
    }


    @Override
    protected List<Object3DData> doInBackground(Void... params) {
        Log.d(tag,"doInBackground " );
        try {
            callback.onStart();
            List<Object3DData> data = build();
            build(data);
            callback.onLoadComplete(data);
            return data;
        } catch (Exception ex) {
            callback.onLoadError(ex);
            return null;
        }
    }

    protected abstract List<Object3DData> build() throws Exception;

    protected abstract void build(List<Object3DData> data) throws Exception;

    /*@Override
    protected void onProgressUpdate(Integer... values) {
        Log.d(tag, "onProgressUpdate...");
        super.onProgressUpdate(values);
        switch (values[0]) {
            case 0:
                //this.dialog.setMessage("Analyzing model...");
                break;
            case 1:
                //this.dialog.setMessage("Allocating memory...");
                break;
            case 2:
                //this.dialog.setMessage("Loading data...");
                break;
            case 3:
                //this.dialog.setMessage("Scaling object...");
                break;
            case 4:
                //this.dialog.setMessage("Building 3D model...");
                break;
            case 5:
                // Toast.makeText(parent, modelId + " Build!", Toast.LENGTH_LONG).show();
                break;
        }
    }*/

    @Override
    protected void onPostExecute(List<Object3DData> data) {
        super.onPostExecute(data);
		/*if (dialog.isShowing()) {
			dialog.dismiss();
		}*/
    }


    public interface Callback {

        void onStart();

        void onLoadError(Exception ex);

        void onLoadComplete(List<Object3DData> data);
    }
}