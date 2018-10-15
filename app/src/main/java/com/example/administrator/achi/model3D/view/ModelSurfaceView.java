package com.example.administrator.achi.model3D.view;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;


import com.example.administrator.achi.MainActivity;
import com.example.administrator.achi.fragment.MonitoringFragment;
import com.example.administrator.achi.model3D.view.ModelRenderer;


/**
 * This is the actual opengl view. From here we can detect touch gestures for example
 *
 * @author andresoviedo
 *
 */
public class ModelSurfaceView extends GLSurfaceView {

	private MonitoringFragment parent;
	private ModelRenderer mRenderer;

	public ModelSurfaceView(Context context, AttributeSet attributeSet) {
		super(context);

		// Create an OpenGL ES 2.0 context.
		setEGLContextClientVersion(2);

		// This is the actual renderer of the 3D space
		mRenderer = new ModelRenderer(this);
		setRenderer(mRenderer);

		// Render the view only when there is a change in the drawing data
		// TODO: enable this?
		// setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

	}


	public ModelSurfaceView(Context context, MonitoringFragment parent) {
		super(context);

		// parent component
		this.parent = parent;

		// Create an OpenGL ES 2.0 context.
		setEGLContextClientVersion(2);

		// This is the actual renderer of the 3D space
		mRenderer = new ModelRenderer(this);
		setRenderer(mRenderer);

		// Render the view only when there is a change in the drawing data
		// TODO: enable this?
		// setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
	}

	public void setParent(MonitoringFragment parent) {
		this.parent = parent;
	}

//	@Override
//	public boolean onTouchEvent(MotionEvent event) {
//		return touchHandler.onTouchEvent(event);
//	}

	public MonitoringFragment getModelActivity() {
		return parent;
	}

	public ModelRenderer getModelRenderer(){
		return mRenderer;
	}

}