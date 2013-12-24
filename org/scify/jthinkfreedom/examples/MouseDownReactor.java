package org.scify.jthinkfreedom.examples;

import org.scify.jthinkfreedom.reactors.ReactorAdapter;

/**
 *
 * @author eustratiadis-hua
 */
public class MouseDownReactor extends ReactorAdapter {

    @Override
    public void react() {
        System.out.println("Mouse moved down: " + java.awt.MouseInfo.getPointerInfo().getLocation().toString());
    }

    public MouseDownReactor() {
        super();
    }
}
