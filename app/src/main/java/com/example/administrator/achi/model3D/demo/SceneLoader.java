package com.example.administrator.achi.model3D.demo;

import android.app.ProgressDialog;
import android.net.Uri;
import android.nfc.Tag;
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

import static com.example.administrator.achi.model3D.demo.SceneLoader.Color.BLUE;

/**
 * This class loads a 3D scena as an example of what can be done with the app
 *
 * @author andresoviedo
 */
public class SceneLoader implements LoaderTask.Callback {

    static final String TAG = "SceneLoader";
    public enum Color{
        WHITE, YELLOW, BLUE, LIGHTBLUE
    }

    /**
     * Default shades for highlighting purpose.
     */
    final float[] COLOR_WHITE   = new float[] {1.0f, 1.0f, 1.0f, 1.0f};
    final float[] COLOR_YELLOW  = new float[] {1.0f, 0.909f, 0.0f, 1.0f};
    final float[] COLOR_BLUE    = new float[] {0.439f, 0.631f, 1.0f, 1.0f};
    final float[] COLOR_PINK    = new float[]{1.0f, 0.639f, 0.639f, 1.0f};

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
     * Whether to draw using points
     */
    private boolean drawingPoints = false;
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
     * Toggle 3d anaglyph
     */
    private boolean isAnaglyph = false;
    /**
     * Initial light position
     */
    private final float[] lightPosition = new float[]{0, 0, 8, 1};
    /**
     * Light bulb 3d data
     */
    private final Object3DData lightPoint = Object3DBuilder.buildPoint(lightPosition).setId("light");
    /**
     * time when model loading has started (for stats)
     */
    private long startTime;

    public SceneLoader(MonitoringFragment main) {
        this.parent = main;
    }

    public void init() {

        // Camera to show a point of view
        camera = new Camera();

        if (parent.getParamUri() == null){
            return;
        }

        startTime = SystemClock.uptimeMillis();
        List<Exception> errors = new ArrayList<>();

        try {
            // Set up ContentUtils so referenced materials and/or textures could be find
            ContentUtils.setThreadActivity((MainActivity)parent.getActivity());
            ContentUtils.provideAssets((MainActivity)parent.getActivity());

            // test loading object
            try {
                String fileName;
                for (int i = 11; i < 48; i++) {
                    fileName = new String("teeth" + i + ".obj");
                    addteethObject(fileName, i);
                }
                addteethObject("gum_and_tongue.obj", -1);
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

            box.setScale(new float[] {3.0f, 2.5f, 2.5f});

            if (i != -1)   // teeth
                box.setColor(COLOR_BLUE);
            else           // gum
                box.setColor(COLOR_PINK);

            addObject(box);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    /**
     * Colors a specific tooth
     * @param toothNumber
     * @param color
     */
    public void colorTeeth(int toothNumber, Color color) {
        if (toothNumber < 11 || toothNumber > 47) {
            Log.i(TAG, "Tooth index out of bound.");
            return;
        }

        int num = toothNumber;
        int a = num / 10;     // 10's
        int b = num % 10;     // 1's

        if (b==8 || b==9 || b==0) {
            Log.i(TAG, "Tooth index out of bound.");
        }

        /**
         *      tooth number
         * { 17, 16, 15, 14, 13, 12, 11, 21, 22, 23, 24, 25, 26, 27,         // upper teeth
         *      47, 46, 45, 44, 43, 42, 41, 31, 32, 33, 34, 35, 36, 37 }     // lower teeth
         *
         *      converts to
         * {  6,  5,  4,  3,  2,  1,  0,  7,  8,  9, 10, 11, 12, 13,
         *      27, 26, 25, 24, 23, 22, 21, 14, 15, 16, 17, 18, 19, 20 }
         */
        int toothIndex = (a - 1) * 7 + (b - 1);
        Log.i("Scene", "index: " + toothIndex);

        // Color tooth
        float[] colorValues;
        switch (color) {
            case YELLOW:
                colorValues = COLOR_YELLOW;
                break;
            case WHITE:
                colorValues = COLOR_WHITE;
                break;
            case LIGHTBLUE:
                colorValues = COLOR_PINK;
                break;
            default:
                colorValues = COLOR_BLUE;
        }
        Log.i("SceneLoader", "Tooth index: " + toothIndex);
        objects.get(toothIndex).setColor(colorValues);

    }

    /**
     * Colors a specific tooth.
     * @param numString
     * @param color
     */
    public void colorTeeth(String numString, Color color) {
        // Find out tooth's number
        int num = 0;
        try {
            num = Integer.parseInt(numString);
        } catch (Exception e) {
            Log.d(TAG, "Cannot parse numString: " + numString);
        }
        colorTeeth(num, color);

    }

    /**
     * Colors a specific tooth and roates the camera to its angle.
     * @param toothNumber
     * @param color
     */
    public void colorTeethAndRotate(int toothNumber, Color color) {
        int num = toothNumber;
        int a = num / 10;     // 10's
        int b = num % 10;     // 1's

        colorTeeth(toothNumber, color);
        rotateModel(b, num);
    }

    public void rotateModel(int b, int num) {
        final float FRONT_TEETH_ANGLE = 0f;
        final float MIDDLE_TEETH_ANGLE = 0.3f;
        final float BACK_TEETH_ANGLE = 0.5f;

        // Rotate camera from range -0.5 ~ 0.5
        if (b == 1 || b == 2) {                                     // front teeth
            camera.setHorizontalRotation(FRONT_TEETH_ANGLE);
        } else if (b == 3 || b == 4) {                              // 3, 4th teeth
            if (num > 20 && num < 40) {                             // left
                camera.setHorizontalRotation(-MIDDLE_TEETH_ANGLE);
            } else {
                camera.setHorizontalRotation(MIDDLE_TEETH_ANGLE);   // right
            }
        } else {                                                    // 5, 6, 7th teeth
            if (num > 20 && num < 40) {                             // left
                camera.setHorizontalRotation(-BACK_TEETH_ANGLE);
            } else {                                                // right
                camera.setHorizontalRotation(BACK_TEETH_ANGLE);
            }
        }
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
        // smooth camera transition
        camera.animate();

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
