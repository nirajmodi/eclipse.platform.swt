package org.eclipse.swt.widgets;

/*
 * Copyright (c) 2000, 2002 IBM Corp.  All rights reserved.
 * This file is made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 */

import org.eclipse.swt.internal.carbon.*;

import org.eclipse.swt.*;
import org.eclipse.swt.graphics.*;

public class TreeItem extends Item {
	Tree parent;
	TreeItem parentItem;
	int id, index = -1;
	boolean checked;

public TreeItem (Tree parent, int style) {
	super (parent, style);
	this.parent = parent;
	parent.createItem (this, null, -1);
}

public TreeItem (Tree parent, int style, int index) {
	super (parent, style);
	if (index < 0) error (SWT.ERROR_INVALID_RANGE);
	this.parent = parent;
	parent.createItem (this, null, index);
}

public TreeItem (TreeItem parentItem, int style) {
	super (checkNull (parentItem).parent, style);
	parent = parentItem.parent;
	this.parentItem = parentItem;
	parent.createItem (this, parentItem, -1);
}

public TreeItem (TreeItem parentItem, int style, int index) {
	super (checkNull (parentItem).parent, style);
	if (index < 0) error (SWT.ERROR_INVALID_RANGE);
	parent = parentItem.parent;
	this.parentItem = parentItem;
	parent.createItem (this, parentItem, index);
}

static TreeItem checkNull (TreeItem item) {
	if (item == null) SWT.error (SWT.ERROR_NULL_ARGUMENT);
	return item;
}

protected void checkSubclass () {
	if (!isValidSubclass ()) error (SWT.ERROR_INVALID_SUBCLASS);
}

public Color getBackground () {
	checkWidget ();
	//NOT DONE
	return getDisplay ().getSystemColor (SWT.COLOR_WHITE);
}

public Rectangle getBounds () {
	checkWidget ();
	Rect rect = new Rect();
	OS.GetDataBrowserItemPartBounds (parent.handle, id, Tree.COLUMN_ID, OS.kDataBrowserPropertyEnclosingPart, rect);
	int width = rect.right - rect.left;
	int height = rect.bottom - rect.top;
	return new Rectangle (rect.left, rect.top, width, height);
}

public boolean getChecked () {
	checkWidget ();
	if ((parent.style & SWT.CHECK) == 0) return false;
	return checked;
}

public Display getDisplay () {
	Tree parent = this.parent;
	if (parent == null) error (SWT.ERROR_WIDGET_DISPOSED);
	return parent.getDisplay ();
}

public boolean getExpanded () {
	checkWidget ();
	int [] state = new int [1];
	OS.GetDataBrowserItemState (parent.handle, id, state);
	return (state [0] & OS.kDataBrowserContainerIsOpen) != 0;
}

public Color getForeground () {
	checkWidget ();
	//NOT DONE
	return getDisplay ().getSystemColor (SWT.COLOR_BLACK);
}

public boolean getGrayed () {
	checkWidget ();
	if ((parent.style & SWT.CHECK) == 0) return false;
	//NOT DONE
	return false;
}

public int getItemCount () {
	checkWidget ();
	return parent.getItemCount (this);
}

public TreeItem [] getItems () {
	checkWidget ();
	return parent.getItems (this);
}

public Tree getParent () {
	checkWidget ();
	return parent;
}

public TreeItem getParentItem () {
	checkWidget ();
	return parentItem;
}

void releaseChild () {
	super.releaseChild ();
	parent.destroyItem (this);
}

void releaseWidget () {
	super.releaseWidget ();
	parentItem = null;
	parent = null;
	id = 0;
	index = -1;
}

public void setBackground (Color color) {
	checkWidget ();
	if (color != null && color.isDisposed ()) {
		SWT.error (SWT.ERROR_INVALID_ARGUMENT);
	}
	//NOT DONE
}

public void setChecked (boolean checked) {
	checkWidget ();
	if ((parent.style & SWT.CHECK) == 0) return;
	this.checked = checked;
	int parentID = parentItem == null ? OS.kDataBrowserNoItem : parentItem.id;
	OS.UpdateDataBrowserItems (parent.handle, parentID, 1, new int[] {id}, OS.kDataBrowserItemNoProperty, OS.kDataBrowserNoItem);
}

public void setExpanded (boolean expanded) {
	checkWidget ();
	parent.ignoreExpand = true;
	if (expanded) {
		OS.OpenDataBrowserContainer (parent.handle, id);
	} else {
		OS.CloseDataBrowserContainer (parent.handle, id);
	}
	parent.ignoreExpand = false;
}

public void setForeground (Color color) {
	checkWidget ();
	if (color != null && color.isDisposed ()) {
		SWT.error (SWT.ERROR_INVALID_ARGUMENT);
	}
	//NOT DONE
}

public void setGrayed (boolean grayed) {
	checkWidget ();
	if ((parent.style & SWT.CHECK) == 0) return;
	//NOT DONE
}

public void setImage (Image image) {
	checkWidget ();
	super.setImage (image);
	int parentID = parentItem == null ? OS.kDataBrowserNoItem : parentItem.id;
	OS.UpdateDataBrowserItems (parent.handle, parentID, 1, new int[] {id}, OS.kDataBrowserItemNoProperty, OS.kDataBrowserNoItem);
}

public void setText (String string) {
	checkWidget ();
	if (string == null) error (SWT.ERROR_NULL_ARGUMENT);
	super.setText (string);
	int parentID = parentItem == null ? OS.kDataBrowserNoItem : parentItem.id;
	OS.UpdateDataBrowserItems (parent.handle, parentID, 1, new int[] {id}, OS.kDataBrowserItemNoProperty, OS.kDataBrowserNoItem);
}

}
