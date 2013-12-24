/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.scify.jthinkfreedom.examples;

import org.scify.jthinkfreedom.reactors.ReactorAdapter;

/**
 *
 * @author eustratiadis-hua
 */
public class MouseLeftReactor extends ReactorAdapter {

    @Override
    public void react() {
        System.out.println("Mouse moved left: " + java.awt.MouseInfo.getPointerInfo().getLocation().toString());
    }

    public MouseLeftReactor() {
        super();
    }
}
