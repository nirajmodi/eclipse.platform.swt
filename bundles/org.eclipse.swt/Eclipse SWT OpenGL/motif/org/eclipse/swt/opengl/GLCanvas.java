/*******************************************************************************
 * Copyright (c) 2000, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.swt.opengl;

import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.internal.motif.*;
import org.eclipse.swt.internal.opengl.glx.*;

/**
 * GLCanvas is a widget capable of displaying OpenGL content.
 * 
 * @see GLData
 * @see <a href="http://www.eclipse.org/swt/snippets/#opengl">OpenGL snippets</a>
 * @see <a href="http://www.eclipse.org/swt/">Sample code and further information</a>
 *
 * @since 3.2
 */

public class GLCanvas extends Canvas {
	int xWindow;
	int context;
	int colormap;
	XVisualInfo vinfo;
	static final int MAX_ATTRIBUTES = 32;

/**
 * Create a GLCanvas widget using the attributes described in the GLData
 * object provided.
 *
 * @param parent a composite widget
 * @param style the bitwise OR'ing of widget styles
 * @param data the requested attributes of the GLCanvas
 *
 * @exception IllegalArgumentException
 * <ul><li>ERROR_NULL_ARGUMENT when the data is null
 *     <li>ERROR_UNSUPPORTED_DEPTH when the requested attributes cannot be provided</ul> 
 * </ul>
 */
public GLCanvas (Composite parent, int style, GLData data) {
	super (parent, style);	
	if (data == null) SWT.error (SWT.ERROR_NULL_ARGUMENT);
	int glxAttrib [] = new int [MAX_ATTRIBUTES];
	int pos = 0;
	glxAttrib [pos++] = GLX.GLX_RGBA;
	if (data.doubleBuffer) glxAttrib [pos++] = GLX.GLX_DOUBLEBUFFER;
	if (data.stereo) glxAttrib [pos++] = GLX.GLX_STEREO;
	if (data.redSize > 0) {
		glxAttrib [pos++] = GLX.GLX_RED_SIZE;
		glxAttrib [pos++] = data.redSize;
	}
	if (data.greenSize > 0) {
		glxAttrib [pos++] = GLX.GLX_GREEN_SIZE;
		glxAttrib [pos++] = data.greenSize;
	}
	if (data.blueSize > 0) {
		glxAttrib [pos++] = GLX.GLX_BLUE_SIZE;
		glxAttrib [pos++] = data.blueSize;
	}
	if (data.alphaSize > 0) {
		glxAttrib [pos++] = GLX.GLX_ALPHA_SIZE;
		glxAttrib [pos++] = data.alphaSize;
	}
	if (data.depthSize > 0) {
		glxAttrib [pos++] = GLX.GLX_DEPTH_SIZE;
		glxAttrib [pos++] = data.depthSize;
	}
	if (data.stencilSize > 0) {
		glxAttrib [pos++] = GLX.GLX_STENCIL_SIZE;
		glxAttrib [pos++] = data.stencilSize;
	}
	if (data.accumRedSize > 0) {
		glxAttrib [pos++] = GLX.GLX_ACCUM_RED_SIZE;
		glxAttrib [pos++] = data.accumRedSize;
	}
	if (data.accumGreenSize > 0) {
		glxAttrib [pos++] = GLX.GLX_ACCUM_GREEN_SIZE;
		glxAttrib [pos++] = data.accumGreenSize;
	}
	if (data.accumBlueSize > 0) {
		glxAttrib [pos++] = GLX.GLX_ACCUM_BLUE_SIZE;
		glxAttrib [pos++] = data.accumBlueSize;
	}
	if (data.accumAlphaSize > 0) {
		glxAttrib [pos++] = GLX.GLX_ACCUM_ALPHA_SIZE;
		glxAttrib [pos++] = data.accumAlphaSize;
	}
	if (data.sampleBuffers > 0) {
		glxAttrib [pos++] = GLX.GLX_SAMPLE_BUFFERS;
		glxAttrib [pos++] = data.sampleBuffers;
	}
	if (data.samples > 0) {
		glxAttrib [pos++] = GLX.GLX_SAMPLES;
		glxAttrib [pos++] = data.samples;
	}
	glxAttrib [pos++] = 0;

	//FIXME - need to ensure the widget is realized at this point
	new GC(this).dispose();

	int xDisplay = OS.XtDisplay (handle);
	int xScreen = OS.XDefaultScreen (xDisplay);
	int infoPtr = GLX.glXChooseVisual (xDisplay, xScreen, glxAttrib);
	if (infoPtr == 0) {
		dispose ();
		SWT.error (SWT.ERROR_UNSUPPORTED_DEPTH);
	}
	vinfo = new XVisualInfo ();
	GLX.memmove (vinfo, infoPtr, XVisualInfo.sizeof);
	OS.XFree (infoPtr);
	long /*int*/ share = data.shareContext != null ? data.shareContext.context : 0;
	context = GLX.glXCreateContext (xDisplay, vinfo, share, true);
	if (context == 0) SWT.error (SWT.ERROR_NO_HANDLES);

	int xParent = OS.XtWindow (handle);
	XSetWindowAttributes attributes = new XSetWindowAttributes ();
	int mask = OS.CWDontPropagate | OS.CWEventMask | OS.CWColormap;
	colormap = OS.XCreateColormap(xDisplay, xParent, vinfo.visual, OS.AllocNone);
	attributes.colormap = colormap;
	xWindow = OS.XCreateWindow (xDisplay, xParent, 0, 0, 1, 1, 0,
			vinfo.depth, 1, vinfo.visual, mask, attributes);
	int event_mask = OS.XtBuildEventMask (handle);
	/*
	* Attempting to propogate EnterWindowMask and LeaveWindowMask
	* causes an X error so these must be specially cleared out from
	* the event mask, not included in the propogate mask.
	*/
	//FIXME - check this
	//event_mask = event_mask | ~(OS.EnterWindowMask | OS.LeaveWindowMask);
	OS.XSelectInput (xDisplay, xWindow, event_mask);
	OS.XMapWindow (xDisplay, xWindow);
	OS.XtRegisterDrawable (xDisplay, xWindow, handle);

	Listener listener = new Listener () {
		public void handleEvent (Event event) {
			int xDisplay = OS.XtDisplay (handle);
			switch (event.type) {
			case SWT.Paint:
				/**
				* Bug in MESA.  MESA does some nasty sort of polling to try
				* and ensure that their buffer sizes match the current X state.
				* This state can be updated using glViewport().
				* FIXME: There has to be a better way of doing this.
				*/
				int [] viewport = new int [4];
				GLX.glGetIntegerv (GLX.GL_VIEWPORT, viewport);
				GLX.glViewport (viewport [0],viewport [1],viewport [2],viewport [3]);
				break;
			case SWT.KeyDown:
				break;
			case SWT.Resize:
				Rectangle clientArea = getClientArea();
				OS.XMoveResizeWindow (xDisplay, xWindow, clientArea.x, clientArea.y, clientArea.width, clientArea.height);
				break;
			case SWT.Dispose:
				if (context != 0) {
					if (GLX.glXGetCurrentContext () == context) {
						GLX.glXMakeCurrent (xDisplay, 0, 0);
					}
					GLX.glXDestroyContext (xDisplay, context);
					context = 0;
				}
				if (xWindow != 0) {
					OS.XtUnregisterDrawable (xDisplay, xWindow);
					OS.XDestroyWindow (xDisplay, xWindow);
					xWindow = 0;
				}
				if (colormap != 0) {
					OS.XFreeColormap (xDisplay, colormap);
					colormap = 0;
				}
				break;
			}
		}
	};
	addListener (SWT.KeyDown, listener);
	addListener (SWT.Resize, listener);
	addListener (SWT.Paint, listener);
	addListener (SWT.Dispose, listener);
}

/**
 * Returns a GLData object describing the created context.
 *  
 * @return GLData description of the OpenGL context attributes
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public GLData getGLData () {
	checkWidget ();
	int xDisplay = OS.XtDisplay (handle);
	GLData data = new GLData ();
	int [] value = new int [1];
	GLX.glXGetConfig (xDisplay, vinfo, GLX.GLX_DOUBLEBUFFER, value);
	data.doubleBuffer = value [0] != 0;
	GLX.glXGetConfig (xDisplay, vinfo, GLX.GLX_STEREO, value);
	data.stereo = value [0] != 0;
	GLX.glXGetConfig (xDisplay, vinfo, GLX.GLX_RED_SIZE, value);
	data.redSize = value [0];
	GLX.glXGetConfig (xDisplay, vinfo, GLX.GLX_GREEN_SIZE, value);
	data.greenSize = value [0];
	GLX.glXGetConfig (xDisplay, vinfo, GLX.GLX_BLUE_SIZE, value);
	data.blueSize = value [0];
	GLX.glXGetConfig (xDisplay, vinfo, GLX.GLX_ALPHA_SIZE, value);
	data.alphaSize = value [0];
	GLX.glXGetConfig (xDisplay, vinfo, GLX.GLX_DEPTH_SIZE, value);
	data.depthSize = value [0];
	GLX.glXGetConfig (xDisplay, vinfo, GLX.GLX_STENCIL_SIZE, value);
	data.stencilSize = value [0];
	GLX.glXGetConfig (xDisplay, vinfo, GLX.GLX_ACCUM_RED_SIZE, value);
	data.accumRedSize = value [0];
	GLX.glXGetConfig (xDisplay, vinfo, GLX.GLX_ACCUM_GREEN_SIZE, value);
	data.accumGreenSize = value [0];
	GLX.glXGetConfig (xDisplay, vinfo, GLX.GLX_ACCUM_BLUE_SIZE, value);
	data.accumBlueSize = value [0];
	GLX.glXGetConfig (xDisplay, vinfo, GLX.GLX_ACCUM_ALPHA_SIZE, value);
	data.accumAlphaSize = value [0];
	GLX.glXGetConfig (xDisplay, vinfo, GLX.GLX_SAMPLE_BUFFERS, value);
	data.sampleBuffers = value [0];
	GLX.glXGetConfig (xDisplay, vinfo, GLX.GLX_SAMPLES, value);
	data.samples = value [0];
	return data;
}

/**
 * Returns a boolean indicating whether the receiver's OpenGL context
 * is the current context.
 *  
 * @return true if the receiver holds the current OpenGL context,
 * false otherwise
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public boolean isCurrent () {
	checkWidget ();
	return GLX.glXGetCurrentContext () == context;
}

/**
 * Sets the OpenGL context associated with this GLCanvas to be the
 * current GL context.
 * 
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public void setCurrent () {
	checkWidget ();
	if (GLX.glXGetCurrentContext () == context) return;
	int xDisplay = OS.XtDisplay (handle);
	GLX.glXMakeCurrent (xDisplay, xWindow, context);
}

/**
 * Swaps the front and back color buffers.
 * 
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public void swapBuffers () {
	checkWidget ();
	int xDisplay = OS.XtDisplay (handle);
	GLX.glXSwapBuffers (xDisplay, xWindow);
}
}
