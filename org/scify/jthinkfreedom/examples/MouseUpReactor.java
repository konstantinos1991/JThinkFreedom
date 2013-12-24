package org.scify.jthinkfreedom.examples;

import org.scify.jthinkfreedom.reactors.ReactorAdapter;

/**
 *
 * @author eustratiadis-hua
 */
public class MouseUpReactor extends ReactorAdapter {

    @Override
    public void react() {
        System.out.println("Mouse moved up: " + java.awt.MouseInfo.getPointerInfo().getLocation().toString());
    }

    public MouseUpReactor() {
        super();
    }
}
