package org.scify.jthinkfreedom.reactors;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ggianna
 */
public class TCPReactorServer implements Runnable {

    protected ServerSocket ssListener;
    Thread tListener;
    boolean bStop = false;
    List<IReactor> lReactors;

    public TCPReactorServer(int iPort) {
        try {
            ssListener = new ServerSocket(iPort);
            lReactors = new LinkedList<>();
        } catch (IOException ex) {
            Logger.getLogger(TCPReactorServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void start() {
        tListener = new Thread(this);
        tListener.start();

    }

    @Override
    public void run() {
        while (!bStop) {
            try {
                ssListener.setSoTimeout(1000);
                Socket sRecv = ssListener.accept();

                // Connection is enough to signal reaction
                if (sRecv.isConnected()) {
                    // thus, react 
                    triggerReactors();
                    // and close connection
                    sRecv.close();
                }

            } catch (SocketTimeoutException ste) {
                // Ignore
            } catch (IOException ex) {
                Logger.getLogger(TCPReactorServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    public void stop() {
        bStop = true;
    }

    public void addReactor(IReactor rToAdd) {
        lReactors.add(rToAdd);
    }

    public void removeReactor(IReactor rToTRemove) {
        lReactors.remove(rToTRemove);
    }

    public void clearReactors() {
        lReactors.clear();
    }

    private void triggerReactors() {
        for (IReactor iCur : lReactors) {
            iCur.react();
        }
    }

    public boolean isRunning() {
        return !bStop;
    }

}
