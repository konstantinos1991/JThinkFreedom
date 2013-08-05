package org.scify.jthinkfreedom.stimuli.tools;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

/**
 * Class to facilitate reading and writing from files
 *
 */
public class txtInOut {

    private String FileName;
    private FileInputStream inStream;
    private PrintStream outStream;

    public String getFileName() {
        return FileName;
    }

    // Open a reader
    public void OpenInputStream(String file) {
        FileName = file;
        try {
            inStream = new FileInputStream(FileName);
        } catch (FileNotFoundException e) {
            System.out.println("Warning: the document " + file
                    + " has not be found.");
        }
    }

    // Close reader
    public void CloseInputStream() {
        try {
            inStream.close();
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
    }

    // Open a writer
    public void OpenOutputStream(String file) {
        try {
            outStream = new PrintStream(new FileOutputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace(System.err);
        }
    }

    // Close writer
    public void CloseOutputStream() {
        outStream.close();
    }

    // Save a word into the file
    public void PrintWord(String word) {
        outStream.print(word);
    }

    // Write a line
    public void PrintLine(String Line) {
        outStream.println(Line);
    }

    // Get a line from the file
    public String nextLine() {
        StringBuilder buf = new StringBuilder();
        try {
            int c;
            do {
                c = inStream.read();
                if ((char) c == 10) {
                    return buf.toString();
                } else {
                    buf.append((char) c);
                }
            } while (c != -1);
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
        return buf.toString();
    }

    // Get a word
    public String nextWord() {
        int c;
        StringBuilder buf = new StringBuilder();
        try {
            do {
                c = inStream.read();
                if ((char) c == ' ') {
                    return buf.toString();
                } else {
                    buf.append((char) c);
                }
            } while (c != -1);
            return buf.toString();
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
        return null;
    }

    public int available() {
        try {
            return inStream.available();
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
        return 0;
    }
}
