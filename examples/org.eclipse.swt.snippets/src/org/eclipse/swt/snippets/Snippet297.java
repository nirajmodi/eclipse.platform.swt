/*******************************************************************************
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.swt.snippets;

/*
 * Table example snippet: create a table with columns and header images
 *
 * For a list of all SWT example snippets see
 * http://www.eclipse.org/swt/snippets/
 */
import org.eclipse.swt.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

public class Snippet297 {

public static void main (String [] args) {
	Display display = new Display ();
	Image images[] = new Image[] {
		display.getSystemImage(SWT.ICON_INFORMATION),
		display.getSystemImage(SWT.ICON_ERROR),
		display.getSystemImage(SWT.ICON_QUESTION),
		display.getSystemImage(SWT.ICON_WARNING),
	};
	String[] titles = {"Information", "Error", "Question", "Warning"};
	String[] questions = {"who?", "what?", "where?", "when?", "why?"};
	Shell shell = new Shell (display);
	shell.setLayout(new GridLayout());
	Table table = new Table (shell, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION);
	GridData data = new GridData (SWT.FILL, SWT.FILL, true, true);
	data.heightHint = 200;
	table.setLayoutData (data);
	table.setLinesVisible (true);
	table.setHeaderVisible (true);
	for (int i=0; i<titles.length; i++) {
		TableColumn column = new TableColumn (table, SWT.NONE);
		column.setText (titles [i]);
		column.setImage(images [i]);
	}	
	int count = 128;
	for (int i=0; i<count; i++) {
		TableItem item = new TableItem (table, SWT.NONE);
		item.setText (0, "some info");
		item.setText (1, "error #" + i);
		item.setText (2, questions [i % questions.length]);
		item.setText (3, "look out!");
	}
	for (int i=0; i<titles.length; i++) {
		table.getColumn (i).pack ();
	}	
	shell.pack ();
	shell.open ();
	while (!shell.isDisposed ()) {
		if (!display.readAndDispatch ()) display.sleep ();
	}
	display.dispose ();
}
} 
