package org.scify.jthinkfreedom.stimuli.tools;

import com.googlecode.javacv.cpp.opencv_core.IplImage;
import static com.googlecode.javacv.cpp.opencv_highgui.cvSaveImage;
import java.awt.AWTException;
import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.Robot;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

/**
 *
 * @author bucketcp
 */
public class WorkSpace extends JPanel {

    private BufferedImage image;
    private String path = "";
    private String[] images;
    private int iterator;
    private Graphics g;
    private Rectangle2D rect;
    private int minHeight = 40;
    private int minWidth = 40;
    private boolean dragging = false;
    private int x1 = 0;
    private int y1 = 0;
    private int x2 = 0;
    private int y2 = 0;
    private ArrayList<String> obj;
    private txtInOut file = new txtInOut();

    public WorkSpace() {
        iterator = 0;

        images = null;
        rect = null;

        obj = new ArrayList<>();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    dragging = true;
                    x1 = e.getX();
                    y1 = e.getY();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    dragging = false;

                    PointerInfo a = MouseInfo.getPointerInfo();
                    Point b = a.getLocation();
                    int x = (int) b.getX();
                    int y = (int) b.getY();

                    int value1 = 0;
                    int value2 = 0;

                    if (x1 > x2) {
                        value1 = x1 - x2;
                    } else {
                        value1 = x2 - x1;
                    }

                    if (y1 > y2) {
                        value2 = y1 - y2;
                    } else {
                        value2 = y2 - y1;
                    }

                    setCursor(x - (value1 / 2), y - (value2 / 2));
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    if (rect != null && rect.getHeight() > 0 && rect.getWidth() > 0) {
                        try {
                            int w, h;

                            if (rect.getX() + rect.getWidth() > image.getWidth()) {
                                w = (int) (image.getWidth() - rect.getX());
                            } else {
                                w = (int) rect.getWidth();
                            }

                            if (rect.getY() + rect.getHeight() > image.getHeight()) {
                                h = (int) (image.getHeight() - rect.getY());
                            } else {
                                h = (int) rect.getHeight();
                            }

                            if (w == 0 && h == 0) {
                                return;
                            }

                            BufferedImage subImage = image.getSubimage((int) rect.getX(),
                                    (int) rect.getY(),
                                    w, h);

                            Random gen = new Random();
                            IplImage snapshot = IplImage.createFrom(subImage);

                            cvSaveImage("eyes/snap-" + gen.nextInt(1000000) + "-" + iterator + ".jpg", snapshot);

                            String sentence = path + "/";
                            if (subImage != null) {
                                sentence += images[iterator];

                                int index = -1;
                                for (int i = 0; i < obj.size(); i++) {
                                    String temp = obj.get(i).substring(0, obj.get(i).indexOf(" "));
                                    if (temp.equals(sentence)) {
                                        index = i;
                                        break;
                                    }
                                }

                                if (index != -1) {
                                    int position1 = obj.get(index).indexOf(" ") + 1;
                                    String newSentence = obj.get(index).substring(position1, obj.get(index).length());

                                    String temp = newSentence.substring(0, newSentence.indexOf(" "));
                                    int numb = Integer.parseInt(temp);
                                    numb++;

                                    String description = newSentence.substring(newSentence.indexOf(" ") + 1, newSentence.length());
                                    description += " " + (int) rect.getX() + " " + (int) rect.getY() + " " + w + " " + h;

                                    obj.set(index, sentence + " " + numb + " " + description);
                                } else {

                                    sentence += " 1 " + (int) rect.getX() + " " + (int) rect.getY() + " " + w + " " + h;
                                    obj.add(sentence);
                                }

                            }

                        } catch (Exception ex) {
                            ex.printStackTrace(System.err);
                        }
                    }
                }
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            // If mouse moved then draw a constant size rectangle around it
            public void mouseMoved(MouseEvent e) {

                double x = e.getPoint().getX();
                double y = e.getPoint().getY();

                if (image != null) {

                    if (rect == null) // Create the rectangle
                    {
                        rect = new Rectangle2D.Double(x - (minHeight / 2), y - (minWidth / 2), minHeight, minWidth);
                    } else // If the rectangle exists, change the coordinates
                    {
                        x -= (minHeight / 2);
                        if (x < 0) {
                            x = 0;
                        } else if (x > image.getWidth()) {
                            x = image.getWidth();
                        }
                        y -= (minWidth / 2);
                        if (y < 0) {
                            y = 0;
                        } else if (y > image.getHeight()) {
                            y = image.getHeight();
                        }
                        rect.setRect(x, y, minHeight, minWidth);
                    }

                    repaint();
                }
            }

            // If mouse moved then draw a constant size rectangle around it
            @Override
            public void mouseDragged(MouseEvent e) {
                if (dragging) {
                    x2 = e.getX();
                    y2 = e.getY();

                    if (x1 > x2) {
                        minHeight = x1 - x2;
                    } else {
                        minHeight = x2 - x1;
                    }

                    if (y1 > y2) {
                        minWidth = y1 - y2;
                    } else {
                        minWidth = y2 - y1;
                    }

                    if (x2 < x1 || y2 < y1) {
                        rect.setRect(x2, y2, minHeight, minWidth);
                    } else {
                        rect.setRect(x1, y1, minHeight, minWidth);
                    }

                    repaint();
                }
            }
        });

        addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                int notches = e.getWheelRotation();

                if (notches > 0) {
                    notches += 5;
                } else {
                    notches -= 5;
                }

                double x = e.getPoint().getX();
                double y = e.getPoint().getY();
                minHeight += notches;
                if (minHeight < 0) {
                    minHeight = 0;
                }

                minWidth += notches;
                if (minWidth < 0) {
                    minWidth = 0;
                }

                rect.setRect(x - (minHeight / 2), y - (minWidth / 2), minHeight, minWidth);

                repaint();
            }
        });
    }

    public void save() {
        String o = path.substring(0, path.lastIndexOf("/"));
        file.OpenOutputStream(o + "/description.txt");

        while (obj.size() > 0) {
            file.PrintLine(obj.get(0));
            obj.remove(0);
        }
        file.CloseOutputStream();
    }

    public void createNeg(File directory) {
        String sPath = directory.getPath();
        String[] files = directory.list();

        String o = directory.getAbsolutePath();
        o = o.substring(0, o.lastIndexOf("/"));
        file.OpenOutputStream(o + "/negative.dat");

        for (int i = 0; i < files.length; i++) {
            try {
                file.PrintLine(sPath + "/" + files[i]);
            } catch (Exception e) {
                System.out.println(".Vec file generation | image not found");
            }
        }
        file.CloseOutputStream();
        System.out.println("");

    }

    public void setCursor(int x, int y) {
        try {
            Robot robot = new Robot();
            robot.mouseMove(x, y);
        } catch (AWTException ex) {
            Logger.getLogger(WorkSpace.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void readImage() {
        System.out.println("Image Loaded: " + path + " " + images[iterator]);
        try {
            if (image != null) {
                image.flush();
            }

            File sourceimage = new File(path + "/" + images[iterator]);
            image = ImageIO.read(sourceimage);

        } catch (IOException e) {
            System.out.println("Error: File not found.");
        }

        this.repaint();
    }

    // Set the list of the selected images
    public void setPath(File directory) {
        iterator = 0;
        path = directory.getPath();
        images = directory.list();

        readImage();
    }

    // Jump to the next image
    public void next() {
        if (images != null && iterator < images.length - 1 && !path.equals("")) {
            iterator++;
            readImage();
        }
    }

    // Jump to the previous image
    public void previous() {
        if (images != null && iterator > 0 && !path.equals("")) {
            iterator--;
            readImage();
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        if (image != null) {
            g2d.drawImage(image, 0, 0, null);
        }

        if (rect != null) {
            float epaisseur = 1;

            float[] style = {10, 5};

            g2d.setStroke(new BasicStroke(
                    epaisseur,
                    BasicStroke.CAP_BUTT,
                    BasicStroke.JOIN_MITER,
                    9.0f,
                    style,
                    0));

            if (rect != null) {
                g2d.draw(rect);

            }
        }
    }
}
