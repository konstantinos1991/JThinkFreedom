package org.scify.jthinkfreedom.examples;

import java.awt.PointerInfo;

/**
 *
 * @author eustratiadis-hua
 */
public class MouseDownStimulus extends MouseStimulus {

    public MouseDownStimulus() {
        super();
    }

    @Override
    protected boolean shouldReact(PointerInfo piOld, PointerInfo piNew) {
        return piOld.getLocation().getY() < piNew.getLocation().getY();
    }

}
