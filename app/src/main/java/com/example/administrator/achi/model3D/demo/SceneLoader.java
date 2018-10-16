package com.example.administrator.achi.model3D.demo;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.SystemClock;
import android.util.Log;

import org.andresoviedo.android_3d_model_engine.model.Camera;
import org.andresoviedo.android_3d_model_engine.model.Object3DData;
import org.andresoviedo.android_3d_model_engine.services.LoaderTask;
import org.andresoviedo.android_3d_model_engine.services.Object3DBuilder;
import com.example.administrator.achi.MainActivity;
import com.example.administrator.achi.fragment.MonitoringFragment;

import org.andresoviedo.util.android.ContentUtils;
import org.andresoviedo.util.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * This class loads a 3D scena as an example of what can be done with the app
 *
 * @author andresoviedo
 */
public class SceneLoader implements LoaderTask.Callback {

    /**
     * Default model color: yellow
     */
    private static float[] DEFAULT_COLOR = {1.0f, 1.0f, 0, 1.0f};
    /**
     * Parent component
     */
    protected final MonitoringFragment parent;
    /**
     * List of data objects containing info for building the opengl objects
     */
    private List<Object3DData> objects = new ArrayList<Object3DData>();
    /**
     * Point of view camera
     */
    private Camera camera;
    /**
     * Whether to draw objects as wireframes
     */
    private boolean drawWireframe = false;
    /**
     * Whether to draw using points
     */
    private boolean drawingPoints = false;
    /**
     * Whether to draw bounding boxes around objects
     */
    private boolean drawBoundingBox = false;
    /**
     * Whether to draw face normals. Normally used to debug models
     */
    private boolean drawNormals = false;
    /**
     * Whether to draw using textures
     */
    private boolean drawTextures = false;
     /**
     * Light toggle feature: we have 3 states: no light, light, light + rotation
     */
    private boolean rotatingLight = true;
    /**
     * Light toggle feature: whether to draw using lights
     */
    private boolean drawLighting = true;
    /**
     * Animate model (dae only) or not
     */
    private boolean animateModel = true;
    /**
     * Draw skeleton or not
     */
    private boolean drawSkeleton = false;
    /**
     * Toggle collision detection
     */
    private boolean isCollision = false;
    /**
     * Toggle 3d anaglyph
     */
    private boolean isAnaglyph = false;
    /**
     * Object selected by the user
     */
    private Object3DData selectedObject = null;
    /**
     * Initial light position
     */
    private final float[] lightPosition = new float[]{0, 0, 6, 1};
    /**
     * Light bulb 3d data
     */
    private final Object3DData lightPoint = Object3DBuilder.buildPoint(lightPosition).setId("light");
    /**
     * Animator
     */
    /**
     * Did the user touched the model for the first time?
     */
    private boolean userHasInteracted;
    /**
     * time when model loading has started (for stats)
     */
    private long startTime;

    private Object3DData[] boxes;

    public SceneLoader(MonitoringFragment main) {
        this.parent = main;
    }

    public void init() {

        // Camera to show a point of view
        camera = new Camera();

        if (parent.getParamUri() == null){
            return;
        }

        boxes = new Object3DData[48];

        startTime = SystemClock.uptimeMillis();
        ProgressDialog dialog = new ProgressDialog((MainActivity)parent.getActivity());
        List<Exception> errors = new ArrayList<>();

        try {
            // Set up ContentUtils so referenced materials and/or textures could be find
            ContentUtils.setThreadActivity((MainActivity)parent.getActivity());
            ContentUtils.provideAssets((MainActivity)parent.getActivity());

            // test loading object
            try {
                String fileName;
                for (int i = 11; i < 47; i++) {
                    fileName = new String("teeth" + i + ".obj");
                    addteethObject(fileName, i);
                }
                addteethObject("gum_and_tongue.obj", -1);
//                addGumAndTongueObjct();
            } catch (Exception ex) {
                errors.add(ex);
            }

        } catch (Exception ex) {
            errors.add(ex);
        } finally{
            ContentUtils.setThreadActivity(null);
            ContentUtils.clearDocumentsProvided();
        }

    }

    private void addteethObject(String name, int i) {
        try {
            Object3DData box = Object3DBuilder.loadV5((MainActivity)parent.getActivity(), Uri.parse("assets://assets/" + name));

            box.setScale(new float[]{3f, 2.5f, 2.5f});

            if (i != -1) {  // teeth
                box.setColor(new float[]{1.0f, 1.0f, 1.0f, 1.0f});
                boxes[i] = box;
            } else {    // gum
                box.setColor(new float[]{1.0f, 0.639f, 0.639f, 1.0f});
                boxes[0] = box;
            }
            addObject(box);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    /**
     * Call this method to color a specific tooth.
     * @param toothNumberString
     * @param colorString
     */
    public void colorTeeth(String toothNumberString, String colorString) {
        int toothNumber = Integer.parseInt(toothNumberString);
        int a = toothNumber%10;     // 10's
        int b = toothNumber/10;     // 1's

        int index = (a-1)*7 + (b-1);
        Log.i("Scene", "index: " + index);
        float[] color;
        if(colorString.equals("YELLOW"))
            color = new float[] {1.0f, 0.909f, 0.0f, 1.0f};
        else if (colorString.equals("BLUE"))
            color = new float[] {0.439f, 0.631f, 1.0f, 1.0f};
        else
            color = new float[] {0.0f, 0.0f, 0.0f, 1.0f};

        objects.get(index).setColor(color);
    }

    public Camera getCamera() {
        return camera;
    }

    public Object3DData getLightBulb() {
        return lightPoint;
    }

    public float[] getLightPosition() {
        return lightPosition;
    }

    /**
     * Hook for animating the objects before the rendering
     */
    public void onDrawFrame() {

//        animateLight();

        // smooth camera transition
        camera.animate();

        // initial camera animation. animate if user didn't touch the screen
        if (!userHasInteracted) {
//            animateCamera();
        }

        if (objects.isEmpty()) return;
    }

    private void animateLight() {
        if (!rotatingLight) return;

        // animate light - Do a complete rotation every 5 seconds.
        long time = SystemClock.uptimeMillis() % 5000L;
        float angleInDegrees = (360.0f / 5000.0f) * ((int) time);
        lightPoint.setRotationY(angleInDegrees);
    }

    private void animateCamera(){
        camera.translateCamera(0.0025f, 0f);
    }

    synchronized void addObject(Object3DData obj) {
        List<Object3DData> newList = new ArrayList<Object3DData>(objects);
        newList.add(obj);
        this.objects = newList;
        requestRender();
    }

    private void requestRender() {
        // request render only if GL view is already initialized
        if (parent.getGLView() != null) {
            parent.getGLView().requestRender();
        }
    }

    public synchronized List<Object3DData> getObjects() {
        return objects;
    }

    public boolean isDrawPoints() {
        return this.drawingPoints;
    }

    public void toggleLighting() {
        if (this.drawLighting && this.rotatingLight) {
            this.rotatingLight = false;
        } else if (this.drawLighting && !this.rotatingLight) {
            this.drawLighting = false;
        } else {
            this.drawLighting = true;
            this.rotatingLight = true;
        }
        requestRender();
    }

    public boolean isDrawAnimation() {
        return animateModel;
    }

    public boolean isDrawTextures() {
        return drawTextures;
    }

    public boolean isDrawLighting() {
        return drawLighting;
    }

    public boolean isAnaglyph() {
        return isAnaglyph;
    }

    @Override
    public void onStart(){
        ContentUtils.setThreadActivity((MainActivity)parent.getActivity());
    }

    @Override
    public void onLoadComplete(List<Object3DData> datas) {
        // TODO: move texture load to LoaderTask
        for (Object3DData data : datas) {
            if (data.getTextureData() == null && data.getTextureFile() != null) {
                Log.i("LoaderTask","Loading texture... "+data.getTextureFile());
                try (InputStream stream = ContentUtils.getInputStream(data.getTextureFile())){
                    if (stream != null) {
                        data.setTextureData(IOUtils.read(stream));
                    }
                } catch (IOException ex) {
                    data.addError("Problem loading texture " + data.getTextureFile());
                }
            }
        }
        // TODO: move error alert to LoaderTask
        List<String> allErrors = new ArrayList<>();
        for (Object3DData data : datas) {
            addObject(data);
            allErrors.addAll(data.getErrors());
        }
        if (!allErrors.isEmpty()){
        }
        final String elapsed = (SystemClock.uptimeMillis() - startTime) / 1000 + " secs";
        ContentUtils.setThreadActivity(null);
    }

    @Override
    public void onLoadError(Exception ex) {
        Log.e("SceneLoader", ex.getMessage(), ex);
        ContentUtils.setThreadActivity(null);
    }
}