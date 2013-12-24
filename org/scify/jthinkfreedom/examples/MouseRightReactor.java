package org.scify.jthinkfreedom.examples;

import org.scify.jthinkfreedom.reactors.ReactorAdapter;

/**
 *
 * @author eustratiadis-hua
 */
public class MouseRightReactor extends ReactorAdapter {

    @Override
    public void react() {
        System.out.println("Mouse moved right: " + java.awt.MouseInfo.getPointerInfo().getLocation().toString());
    }

    public MouseRightReactor() {
        super();
    }
}
