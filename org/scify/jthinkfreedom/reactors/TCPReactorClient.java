/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.scify.jthinkfreedom.reactors;

import gr.demokritos.iit.jinsect.structs.Pair;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ggianna
 */
public class TCPReactorClient extends LinkedList<Pair<String,Integer>> implements IReactor {
    
    @Override
    public void react() {
        // For each server
        for (Pair<String,Integer> pServerInfo : this) {
            try {
                // Connect to signal reaction
                Socket sTmp = new Socket(pServerInfo.getFirst(), pServerInfo.getSecond());
                while (sTmp.isConnected())
                    try {
                       Thread.sleep(100);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(TCPReactorClient.class.getName()).log(Level.SEVERE, null, ex);
                    }
            } catch (UnknownHostException ex) {
                Logger.getLogger(TCPReactorClient.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(TCPReactorClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
}
