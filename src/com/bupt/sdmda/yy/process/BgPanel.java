package com.bupt.sdmda.yy.process;

import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JPanel;

public class BgPanel extends JPanel{
	
	private Image image = null;

	public BgPanel(Image image){
		this.image = image;
	}
	
	protected void paintComponent(Graphics g){
		g.drawImage(image, 0, 0, this.getWidth(), this.getHeight(), this);
	}

}
