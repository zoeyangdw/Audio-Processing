 package com.bupt.sdmda.yy.process;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JPanel;

public class DrawPanel extends JPanel{
	private float[] data  = null;
	
	public DrawPanel(float[] data) {  
        this.data = data;  
        System.out.println("data");
    }  
      
    @Override  
    protected void paintComponent(Graphics g) { 
    	super.paintComponent(g);
    	System.out.println("paint");
        int ww = getWidth();  
        int hh = getHeight();  
        g.setColor(Color.WHITE);  
        g.fillRect(0, 0, ww, hh);  
          
        int len = data.length;  
        int step = len/ww;  
        if(step==0)  
            step = 1;  
          
        int prex = 0, prey = 0; //��һ������  
        int x = 0, y = 0;  
          
        g.setColor(Color.RED);  
        //double k = hh/2.0/32768.0;
        double k = hh/100.0;
        
        for(int i=0; i<ww; i++){  
            x = i;  

            y = hh-(int)(data[i*step]*k+hh/2);

            //System.out.println(data[i*step]);
            //System.out.println("");
              
            if(i!=0){  
                g.drawLine(x, y, prex, prey);  
            }  
            prex = x;  
            prey = y;  
        }  
    }  
	/*private List<Integer>values;
	private float[] data = null;
	private int MAX_VALUE = 400;
	private int MAX_COUNT_OF_VALUES = 100;
	
 
    public DrawPanel() {
		// TODO Auto-generated constructor stub
    	values = Collections.synchronizedList(new ArrayList<Integer>());
	}
    
    public void Draw(float[] data1){
    	
        data = data1;
        // ʹ��һ���߳�ģ���������.
        new Thread(new Runnable() {
            @Override
            public void run() {
                int i=0;
				while(i<data.length){
					addValue((int)data[i]);
					i++;
					repaint();	
				}
            }
        }).start();
    }

	@Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
 
        int w = getWidth();
        int h = getHeight();
        int xDelta = w / MAX_COUNT_OF_VALUES;
        int length = values.size();
 
        for (int i = 0; i < length - 1; ++i) {
            g2d.drawLine(xDelta * (MAX_COUNT_OF_VALUES - length + i), normalizeValueForYAxis(values.get(i), h),
                    xDelta * (MAX_COUNT_OF_VALUES - length + i + 1), normalizeValueForYAxis(values.get(i + 1), h));
        }
    }*/
 
    /**
     * ���յ������ݷ����ڴ�.
     * @param value
     */
    /*private void addValue(int value) {
        // ѭ����ʹ��һ���������ݵĿռ�.
        // �����ʵ��һ��ѭ�����飬������͵����ʹ��ArrayList.
        if (values.size() > MAX_COUNT_OF_VALUES) {
            values.remove(0);
        }
        values.add(value);
    }
 
    /**
     * ��һ��y�᷽���ֵ. ʹ��value��y���ֵΪ[0, height]֮��.
     *
     * @param value
     * @param height
     * @return
     */
	/*
    private int normalizeValueForYAxis(int value, int height) {
        return (int) ((double) height / MAX_VALUE * value);
    }*/

}

