package org.scify.jthinkfreedom.stimuli.tools;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

public final class Main extends JFrame {

    private static final int SCR_WIDTH = Toolkit.getDefaultToolkit().getScreenSize().width;
    private static final int SCR_HEIGHT = Toolkit.getDefaultToolkit().getScreenSize().height;
    
    private static Main window;
    private WorkSpace space;
    private JMenuBar menubar;

    public Main() {

        setTitle("SubImage Selection");
        setSize(SCR_WIDTH, SCR_HEIGHT);
        setLayout(new GridBagLayout());
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        GridBagConstraints gbc = new GridBagConstraints();

        space = new WorkSpace();

        // For alignment
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.ipadx = 640;
        gbc.ipady = 460;
        this.add(space, gbc);

        createMenuBar();
    }

    public static void main(String[] args) {
        window = new Main();
        window.setVisible(true);
    }

    // createVec all the menu bar
    void createMenuBar() {
        menubar = new JMenuBar();

        // <BEGIN> file menu
        JMenu file = new JMenu("File");
        file.setMnemonic(KeyEvent.VK_F);
        // <END> file menu

        // <BEGIN> open directory button
        JMenuItem openDirectory = new JMenuItem("Open");
        openDirectory.setToolTipText("Select the directory of the images");
        openDirectory.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));

        openDirectory.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                // If the button is clicked then open the file chooser <fileopen>
                final JFileChooser fileopen = new JFileChooser();
                fileopen.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

                // If the button is clicked then open the file chooser <fileopen>
                int returnVal = fileopen.showOpenDialog(space);

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File directory = fileopen.getSelectedFile();
                    System.out.println("Opening the directory: " + directory.getPath());
                    String[] files = directory.list();
                    System.out.println("> " + files.length + " images selected");

                    if (files.length > 0) {
                        space.setPath(directory);
                    }

                } else {
                    System.out.println("No directory has been opened");
                }
            }
        });

        JMenuItem saveIn = new JMenuItem("Save");
        saveIn.setToolTipText("Save Description File");
        saveIn.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));

        saveIn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                space.save();
            }
        });

        JMenuItem openNeg = new JMenuItem("Negative Images");
        openNeg.setToolTipText("Select the directory of negative images");
        openNeg.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));

        openNeg.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                // If the button is clicked then open the file chooser <fileopen>
                final JFileChooser fileopen = new JFileChooser();
                fileopen.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                // Fileopen.setAcceptAllFileFilterUsed(false);

                // If the button is clicked then open the file chooser <fileopen>
                int returnVal = fileopen.showOpenDialog(null);

                if (returnVal == JFileChooser.APPROVE_OPTION) {

                    File directory = fileopen.getSelectedFile();
                    String[] files = directory.list();

                    if (files.length > 0) {
                        space.createNeg(directory);
                    }

                } else {
                    System.out.println("No directory has been opened");
                }
            }
        });
        // <END> open directory button

        // <BEGIN> next previous image button
        JMenuItem nextImage = new JMenuItem("Next Image");
        nextImage.setMnemonic(KeyEvent.VK_V);
        nextImage.setToolTipText("select the next image in the queue");
        nextImage.setAccelerator(KeyStroke.getKeyStroke("V"));
        nextImage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                // If clicked then show the next image in the list
                space.next();
            }
        });

        JMenuItem previousImage = new JMenuItem("Previous Image");
        previousImage.setMnemonic(KeyEvent.VK_C);
        previousImage.setToolTipText("select the previous image in the queue");
        previousImage.setAccelerator(KeyStroke.getKeyStroke("C"));
        previousImage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                // If clicked then show the next image in the list
                space.previous();
            }
        });
        // <END> open directory button

        // <BEGIN> closer button
        JMenuItem fileClose = new JMenuItem("Close");
        fileClose.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, ActionEvent.ALT_MASK));
        fileClose.setToolTipText("Exit application");
        fileClose.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                System.exit(0);
            }
        });
        // <END> closer button

        // Adding buttons to the menu
        file.add(openDirectory);
        file.add(saveIn);
        file.add(openNeg);

        file.addSeparator();

        file.add(nextImage);
        file.add(previousImage);

        file.addSeparator();

        file.add(fileClose);

        // Add the menu to the menubar
        menubar.add(file);

        // Add the menubar to the window
        setJMenuBar(menubar);
    }
}
