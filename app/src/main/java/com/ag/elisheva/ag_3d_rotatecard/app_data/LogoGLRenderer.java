package com.ag.elisheva.ag_3d_rotatecard.app_data;

import android.app.Activity;
import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.opengl.Matrix;
import android.util.Log;

import com.ag.elisheva.ag_3d_rotatecard.demo.SceneLoader;
import com.ag.elisheva.ag_3d_rotatecard.view.LogoGLSurfaceView;

import org.andresoviedo.android_3d_model_engine.animation.Animator;
import org.andresoviedo.android_3d_model_engine.drawer.DrawerFactory;
import org.andresoviedo.android_3d_model_engine.drawer.Object3DImpl;
import org.andresoviedo.android_3d_model_engine.model.AnimatedModel;
import org.andresoviedo.android_3d_model_engine.model.Camera;
import org.andresoviedo.android_3d_model_engine.model.Object3D;
import org.andresoviedo.android_3d_model_engine.model.Object3DData;
import org.andresoviedo.android_3d_model_engine.services.Object3DBuilder;
import org.andresoviedo.util.android.GLUtil;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES10.GL_NO_ERROR;
import static android.opengl.GLES10.glGetError;

public class LogoGLRenderer implements GLSurfaceView.Renderer {
    public volatile float mAngle;
    public volatile float scaleXY = .7f;
    public volatile String cardAction;

    private String tag = LogoGLRenderer.class.getSimpleName();
    private Logo3DData logo3DData;     // (NEW)

    private float zInto = -2.5f;
    private static float angleCube = 0;     // rotational angle in degree for cube
    private static float speedCube = 0.5f; // rotational speed for cube
    private static float red_bg = 249.0f;
    private static float green_bg = 241.0f;
    private static float blue_bg = 241.0f;
    private static float alpha_bg = 1.0f;

    // Lighting (NEW)
    boolean lightingEnabled = false;   // Is lighting on? (NEW)
    private float[] lightAmbient = {0.5f, 0.5f, 0.5f, 1.0f};
    private float[] lightDiffuse = {1.0f, 1.0f, 1.0f, 1.0f};
    private float[] lightPosition = {0.0f, 0.0f, 2.0f, 1.0f};
    // width of the screen
    private int width;
    // height of the screen
    private int height;
    // frustrum - nearest pixel
    private static final float near = 1f;
    // frustrum - fartest pixel
    private static final float far = 100f;
    private DrawerFactory drawer;
    // The wireframe associated shape (it should be made of lines only)
    private Map<Object3DData, Object3DData> wireframes = new HashMap<Object3DData, Object3DData>();
    // The loaded textures
    private Map<byte[], Integer> textures = new HashMap<byte[], Integer>();
    // The corresponding opengl bounding boxes and drawer
    private Map<Object3DData, Object3DData> boundingBoxes = new HashMap<Object3DData, Object3DData>();
    // The corresponding opengl bounding boxes
    private Map<Object3DData, Object3DData> normals = new HashMap<Object3DData, Object3DData>();
    private Map<Object3DData, Object3DData> skeleton = new HashMap<>();

    // 3D matrices to project our 3D world
    private final float[] modelProjectionMatrix = new float[16];
    private final float[] modelViewMatrix = new float[16];
    // mvpMatrix is an abbreviation for "Model View Projection Matrix"
    private final float[] mvpMatrix = new float[16];

    // light position required to render with lighting
    private final float[] lightPosInEyeSpace = new float[4];
    /**
     * Whether the info of the model has been written to console log
     */
    private boolean infoLogged = false;

    /**
     * Skeleton Animator
     */
    private Animator animator = new Animator();

    public void setScene(SceneLoader scene) {
        this.scene = scene;
    }

    private SceneLoader scene;

    public float getNear() {
        return near;
    }
    // 3D window (parent component)
    private LogoGLSurfaceView main;
    public float getFar() {
        return far;
    }
    // Constructor
    public LogoGLRenderer(LogoGLSurfaceView logoGLSurfaceView) {
        this.main = logoGLSurfaceView;
    }

    // Call back when the surface is first created or re-created.
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

        gl.glClearColor(red_bg,green_bg,blue_bg, alpha_bg);  // Set color's clear-value to black
        gl.glClearDepthf(1.0f);            // Set depth's clear-value to farthest
        gl.glEnable(GL10.GL_DEPTH_TEST);   // Enables depth-buffer for hidden surface removal
        gl.glDepthFunc(GL10.GL_LEQUAL);    // The type of depth testing to do
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);  // nice perspective view
        gl.glShadeModel(GL10.GL_SMOOTH);   // Enable smooth shading of color
        //gl.glDisable(GL10.GL_DITHER);      // Disable dithering for better performance

        // Setup Texture, each time the surface is created (NEW)
        // logo3DData.loadTexture(gl);             // Load images into textures (NEW)

        int err = glGetError();
        if (err != GL_NO_ERROR) {
            Log.d(tag,"err on loadTexture"+err);
        }
        gl.glEnable(GL10.GL_TEXTURE_2D);  // Enable texture (NEW)

        // add light

        // Setup lighting GL_LIGHT1 with ambient and diffuse lights (NEW)
        gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_AMBIENT, lightAmbient, 0);
        gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_DIFFUSE, lightDiffuse, 0);
        gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_POSITION, lightPosition, 0);
        gl.glEnable(GL10.GL_LIGHT1);   // Enable Light 1 (NEW)
        gl.glEnable(GL10.GL_LIGHT0);   // Enable the default Light 0 (NEW)
        drawer = new DrawerFactory();
    }

    // Call back after onSurfaceCreated() or whenever the window's size changes
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        if (height == 0) height = 1;   // To prevent divide by zero
        float aspect = (float)width / height;

        // Set the viewport (display area) to cover the entire window
        gl.glViewport(0, 0, width, height);

        // Setup perspective projection, with aspect ratio matches viewport
        gl.glMatrixMode(GL10.GL_PROJECTION); // Select projection matrix
        gl.glLoadIdentity();                 // Reset projection matrix
        // Use perspective projection
        GLU.gluPerspective(gl, 45, aspect, 0.1f, 100.f);

        gl.glMatrixMode(GL10.GL_MODELVIEW);  // Select model-view matrix
        gl.glLoadIdentity();              // Reset

        Camera camera = scene.getCamera();
        Matrix.setLookAtM(modelViewMatrix, 0, camera.xPos, camera.yPos, camera.zPos, camera.xView, camera.yView,
                camera.zView, camera.xUp, camera.yUp, camera.zUp);

        // the projection matrix is the 3D virtual space (cube) that we want to project
        float ratio = (float) width / height;
        Log.d(tag, "projection: [" + -ratio + "," + ratio + ",-1,1]-near/far[1,10]");
        Matrix.frustumM(modelProjectionMatrix, 0, -ratio, ratio, -1, 1, getNear(), getFar());

        // Calculate the projection and view transformation
        Matrix.multiplyMM(mvpMatrix, 0, modelProjectionMatrix, 0, modelViewMatrix, 0);
    }

    // Call back to draw the current frame.
    @Override
    public void onDrawFrame(GL10 gl) {
        // Clear color and depth buffers
        // Log.d(tag,"action "+cardAction);
        // Enable lighting? (NEW)
        // Draw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        if (scene == null) {
            // scene not ready
            return;
        }
        // animate scene
        scene.onDrawFrame();
        List<Object3DData> objects = scene.getObjects();
        Camera camera = scene.getCamera();
        if (camera.hasChanged()) {
            Matrix.setLookAtM(modelViewMatrix, 0, camera.xPos, camera.yPos, camera.zPos, camera.xView, camera.yView,
                    camera.zView, camera.xUp, camera.yUp, camera.zUp);
            // Log.d("Camera", "Changed! :"+camera.ToStringVector());
            Matrix.multiplyMM(mvpMatrix, 0, modelProjectionMatrix, 0, modelViewMatrix, 0);
            camera.setChanged(false);
        }

        // draw light
        /*if (scene.isDrawLighting()) {

            Object3DImpl lightBulbDrawer = (Object3DImpl) drawer.getPointDrawer();

            float[] lightModelViewMatrix = lightBulbDrawer.getMvMatrix(lightBulbDrawer.getMMatrix(scene.getLightBulb()),modelViewMatrix);

            // Calculate position of the light in eye space to support lighting
            Matrix.multiplyMV(lightPosInEyeSpace, 0, lightModelViewMatrix, 0, scene.getLightPosition(), 0);

            // Draw a point that represents the light bulb
            lightBulbDrawer.draw(scene.getLightBulb(), modelProjectionMatrix, modelViewMatrix, -1, lightPosInEyeSpace);
        }
*/
        int sz = objects.size();
        for (int i=0; i<objects.size(); i++) {
            Object3DData objData = null;
            try {
                objData = objects.get(i);
                boolean changed = objData.isChanged();

                Object3D drawerObject = drawer.getDrawer(objData, scene.isDrawTextures(), scene.isDrawLighting(),
                        scene.isDrawAnimation());

                if (!infoLogged) {
                    Log.i("ModelRenderer","Using drawer "+drawerObject.getClass());
                    infoLogged = true;
                }

                Integer textureId = textures.get(objData.getTextureData());
                if (textureId == null && objData.getTextureData() != null) {
                    Log.i("ModelRenderer","Loading GL Texture...");
                    ByteArrayInputStream textureIs = new ByteArrayInputStream(objData.getTextureData());
                    textureId = GLUtil.loadTexture(textureIs);
                    textureIs.close();
                    textures.put(objData.getTextureData(), textureId);
                }

                if (objData.getDrawMode() == GLES20.GL_POINTS){
                    Object3DImpl lightBulbDrawer = (Object3DImpl) drawer.getPointDrawer();
                    lightBulbDrawer.draw(objData,modelProjectionMatrix, modelViewMatrix, GLES20.GL_POINTS,lightPosInEyeSpace);
                } else if (scene.isAnaglyph()){
                    // TODO: implement anaglyph
                } else if (scene.isDrawWireframe() && objData.getDrawMode() != GLES20.GL_POINTS
                        && objData.getDrawMode() != GLES20.GL_LINES && objData.getDrawMode() != GLES20.GL_LINE_STRIP
                        && objData.getDrawMode() != GLES20.GL_LINE_LOOP) {
                    // Log.d("ModelRenderer","Drawing wireframe model...");
                    try{
                        // Only draw wireframes for objects having faces (triangles)
                        Object3DData wireframe = wireframes.get(objData);
                        if (wireframe == null || changed) {
                            Log.i("ModelRenderer","Generating wireframe model...");
                            wireframe = Object3DBuilder.buildWireframe(objData);
                            wireframes.put(objData, wireframe);
                        }
                        drawerObject.draw(wireframe,modelProjectionMatrix,modelViewMatrix,wireframe.getDrawMode(),
                                wireframe.getDrawSize(),textureId != null? textureId:-1, lightPosInEyeSpace);
                    }catch(Error e){
                        Log.e("ModelRenderer",e.getMessage(),e);
                    }
                } else if (scene.isDrawPoints() || objData.getFaces() == null || !objData.getFaces().loaded()){
                    drawerObject.draw(objData, modelProjectionMatrix, modelViewMatrix
                            ,GLES20.GL_POINTS, objData.getDrawSize(),
                            textureId != null ? textureId : -1, lightPosInEyeSpace);
                } else if (scene.isDrawSkeleton() && objData instanceof AnimatedModel && ((AnimatedModel) objData)
                        .getAnimation() != null){
                    Object3DData skeleton = this.skeleton.get(objData);
                    if (skeleton == null){
                        skeleton = Object3DBuilder.buildSkeleton((AnimatedModel) objData);
                        this.skeleton.put(objData, skeleton);
                    }
                    animator.update(skeleton);
                    drawerObject = drawer.getDrawer(skeleton, false, scene.isDrawLighting(), scene
                            .isDrawAnimation());
                    drawerObject.draw(skeleton, modelProjectionMatrix, modelViewMatrix,-1, lightPosInEyeSpace);
                } else {
                    drawerObject.draw(objData, modelProjectionMatrix, modelViewMatrix,
                            textureId != null ? textureId : -1, lightPosInEyeSpace);
                }

                // Draw bounding box
                if (scene.isDrawBoundingBox() || scene.getSelectedObject() == objData) {
                    Object3DData boundingBoxData = boundingBoxes.get(objData);
                    if (boundingBoxData == null || changed) {
                        boundingBoxData = Object3DBuilder.buildBoundingBox(objData);
                        boundingBoxes.put(objData, boundingBoxData);
                    }
                    Object3D boundingBoxDrawer = drawer.getBoundingBoxDrawer();
                    boundingBoxDrawer.draw(boundingBoxData, modelProjectionMatrix, modelViewMatrix, -1, null);
                }

                // Draw normals
                if (scene.isDrawNormals()) {
                    Object3DData normalData = normals.get(objData);
                    if (normalData == null || changed) {
                        normalData = Object3DBuilder.buildFaceNormals(objData);
                        if (normalData != null) {
                            // it can be null if object isnt made of triangles
                            normals.put(objData, normalData);
                        }
                    }
                    if (normalData != null) {
                        Object3D normalsDrawer = drawer.getFaceNormalsDrawer();
                        normalsDrawer.draw(normalData, modelProjectionMatrix, modelViewMatrix, -1, null);
                    }
                }
                // TODO: enable this only when user wants it
                // obj3D.drawVectorNormals(result, modelViewMatrix);
            } catch (Exception ex) {
                Log.e("ModelRenderer","There was a problem rendering the object '"+objData.getId()+"':"+ex.getMessage(),ex);
            }
        }


    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public float[] getModelProjectionMatrix() {
        return modelProjectionMatrix;
    }

    public float[] getModelViewMatrix() {
        return modelViewMatrix;
    }
}
