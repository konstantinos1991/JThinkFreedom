/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.scify.jthinkfreedom;

import gr.demokritos.iit.jinsect.utils;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.scify.jthinkfreedom.reactors.DoubleClickReactor;
import org.scify.jthinkfreedom.reactors.IReactor;
import org.scify.jthinkfreedom.reactors.RightClickReactor;
import org.scify.jthinkfreedom.reactors.TCPReactorServer;

/**
 *
 * @author ggianna
 */
public class ReactorServer {
    public static void main(String[] saArgs) {
        Hashtable hSwitches = utils.parseCommandLineSwitches(saArgs);
        // Port number for server
        int iPortNo = 25100;
        try {
            iPortNo = Integer.valueOf(utils.getSwitch(hSwitches, "port", "25100"));
        }
        catch (NumberFormatException nfe) {
            // Use default
            System.err.println("Malformed port number: " + utils.getSwitch(hSwitches, "port", "25100"));
            
        }
        System.err.println("Using port number: " + iPortNo);
        // Class for reactor
        String sReactorClass = utils.getSwitch(hSwitches, "reactor", 
                DoubleClickReactor.class.getCanonicalName());
        IReactor rReactor;
        try {
            try {
                rReactor = (IReactor)Class.forName(sReactorClass).newInstance();
            } catch (InstantiationException ex) {
                Logger.getLogger(ReactorServer.class.getName()).log(Level.SEVERE, null, ex);
                System.err.println("Could not instantiate. Aborting.");
                return;

            } catch (IllegalAccessException ex) {
                Logger.getLogger(ReactorServer.class.getName()).log(Level.SEVERE, null, ex);
                return;
            }
        }
        catch (ClassNotFoundException cnfe) {
            Logger.getLogger(ReactorServer.class.getName()).log(Level.SEVERE, null, cnfe);
            System.err.println("Class " + sReactorClass + " not found. Aborting.");
            return;
        }
                
        // Init server
        TCPReactorServer trsListener = new TCPReactorServer(iPortNo);
        // Add actual reactor to TCP reactor server
        trsListener.addReactor(rReactor);
        trsListener.start();
        while (trsListener.isRunning()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(ReactorServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }
    
}
