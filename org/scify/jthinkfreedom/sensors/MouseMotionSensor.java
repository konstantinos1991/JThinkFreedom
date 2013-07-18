package org.scify.jthinkfreedom.sensors;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class MouseMotionSensor extends SensorAdapter<MouseEvent> implements
		MouseListener, MouseMotionListener {
	protected Exception exception = null;
	MouseEvent e = null;
	// To display the (x, y) coordinates of the mouse-clicked
	private TextField tfMouseClickX;
	private TextField tfMouseClickY;
	private TextField tfMousePositionX;
	private TextField tfMousePositionY;
	Frame f = null;

	public MouseMotionSensor() {
		f = new Frame();
		f.setLayout(new FlowLayout());

		f.add(new Label("X-Click: "));
		tfMouseClickX = new TextField(10);
		tfMouseClickX.setEditable(false);
		f.add(tfMouseClickX);
		f.add(new Label("Y-Click: "));
		tfMouseClickY = new TextField(10);
		tfMouseClickY.setEditable(false);
		f.add(tfMouseClickY);

		f.add(new Label("X-Position: "));
		tfMousePositionX = new TextField(10);
		tfMousePositionX.setEditable(false);
		f.add(tfMousePositionX);
		f.add(new Label("Y-Position: "));
		tfMousePositionY = new TextField(10);
		tfMousePositionY.setEditable(false);
		f.add(tfMousePositionY);

		f.addMouseListener(this);
		f.addMouseMotionListener(this);

		f.setTitle("MouseMotion Frame");
		f.setSize(1365, 767);
		f.setVisible(true);
	}

	@Override
	public MouseEvent getData(long timestamp) {
		return e;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		this.e = e;
		updateStimuli(this, System.currentTimeMillis());
	}

	@Override
	public void mousePressed(MouseEvent e) {
		this.e = e;
		updateStimuli(this, System.currentTimeMillis());
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		this.e = e;
		updateStimuli(this, System.currentTimeMillis());
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		this.e = e;
		updateStimuli(this, System.currentTimeMillis());
	}

	@Override
	public void mouseExited(MouseEvent e) {
		this.e = e;
		updateStimuli(this, System.currentTimeMillis());
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		this.e = e;
		updateStimuli(this, System.currentTimeMillis());
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		this.e = e;
		updateStimuli(this, System.currentTimeMillis());
	}
}