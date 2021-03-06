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
package org.eclipse.swt.tests.junit;


import junit.framework.*;
import junit.textui.*;

import org.eclipse.swt.events.*;
import org.eclipse.swt.widgets.*;

/**
 * Automated Test Suite for class org.eclipse.swt.events.TraverseEvent
 *
 * @see org.eclipse.swt.events.TraverseEvent
 */
public class Test_org_eclipse_swt_events_TraverseEvent extends Test_org_eclipse_swt_events_KeyEvent {

public Test_org_eclipse_swt_events_TraverseEvent(String name) {
	super(name);
}

public static void main(String[] args) {
	TestRunner.run(suite());
}

@Override
public void test_ConstructorLorg_eclipse_swt_widgets_Event() {
	warnUnimpl("Test test_ConstructorLorg_eclipse_swt_widgets_Event not written");
}

public static Test suite() {
	TestSuite suite = new TestSuite();
	java.util.Vector<String> methodNames = methodNames();
	java.util.Enumeration<String> e = methodNames.elements();
	while (e.hasMoreElements()) {
		suite.addTest(new Test_org_eclipse_swt_events_TraverseEvent(e.nextElement()));
	}
	return suite;
}
public static java.util.Vector<String> methodNames() {
	java.util.Vector<String> methodNames = new java.util.Vector<String>();
	methodNames.addElement("test_ConstructorLorg_eclipse_swt_widgets_Event");
	methodNames.addAll(Test_org_eclipse_swt_events_KeyEvent.methodNames()); // add superclass method names
	return methodNames;
}
@Override
protected void runTest() throws Throwable {
	if (getName().equals("test_ConstructorLorg_eclipse_swt_widgets_Event")) test_ConstructorLorg_eclipse_swt_widgets_Event();
	else super.runTest();
}

/* custom */
@Override
protected TypedEvent newTypedEvent(Event event) {
	return new TraverseEvent(event);
}
}
