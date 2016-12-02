package com.bupt.sdmda.yy.process;


import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JPanel;

import org.knowm.xchart.QuickChart;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;

import jm.audio.io.AudioFileIn;
import jm.audio.io.AudioFileOut;
import jm.audio.math.RealFloatFFT_Radix2;
import jm.util.Play;

public class AudioProcesser {
	public float[] data;
	private int channel;
	private int bitDepth;
	private int sampleRate;

	
	public AudioProcesser(String filePath) {
		AudioFileIn afi = new AudioFileIn(filePath);
		data = afi.getSampleData();
		channel = afi.getChannels();
		bitDepth = afi.getBitResolution();
		sampleRate = afi.getSampleRate();
	}
	
	public AudioProcesser(float[] d, int c, int b, int r) {
		this.data = d;
		this.channel = c;
		this.bitDepth = b;
		this.sampleRate = r;
	}
	
	public float[] getData() {
		return data;
	}

	public int getChannel() {
		return channel;
	}

	public int getBitDepth() {
		return bitDepth;
	}

	public int getSampleRate() {
		return sampleRate;
	}

	public void write(String fileName) {
		new AudioFileOut(data, fileName, channel, sampleRate, bitDepth);
	}
	
	public void write(String fileName, int channel, int sampleRate,
			int bitDepth) {
		new AudioFileOut(data, fileName, channel, sampleRate, bitDepth);
	}
	
	public void play(){
		mPlay play = new mPlay();
		Thread t2 = new Thread(play);
		t2.start();
	}	
	
	public static float[] hanning(int size){
		float[] ret = new float[size];
		for(int i = 0;i<size;i++){
			ret[i] = (float) (0.5 - 0.5*Math.cos(2*Math.PI*i/size));
		}
		return ret;
	}
	
	public float[] resample_linear(float rate){
		if(1==rate){
			return data;
		}
		float[] ret = new float[(int)(Math.ceil((data.length-1)*rate))];
		ret[0] = data[0];
		for(int i=1; i<data.length; i++){
			int newLoc = (int)Math.ceil((i*rate));
			int start = (int)Math.ceil((i-1)*rate)+1;
			for(int j=start; j<newLoc-1; ++j){
				if(newLoc >= data.length){
					newLoc = data.length-1;
				}
				ret[j] = (newLoc-j)/(newLoc-start+1)*data[start-1] +
				         (j-start+1)/(newLoc-start+1)*data[newLoc];
			}
			ret[newLoc-1] = data[i];
		}
		return ret;	
	}
	
	public float[] resample(float rate){
		if(1==rate){
			return data;
		}
		
		float[] ret = new float[(int)(Math.ceil((data.length-1)*rate))];
		System.out.println(ret.length);
		ret[0] = data[0];
		for(int i=1; i<data.length; i++){
			int newLoc = (int)Math.ceil((i*rate));
			int start = (int)Math.ceil((i-1)*rate)+1;
			for(int j=start; j<newLoc; ++j){
				ret[j] = 0;
			}
			ret[newLoc-1] = data[i];
		}
		return ret;	
	}
	
	public float[] ola(float rate) {
		int wndSz = sampleRate/10;
		int anaStep = wndSz / 2;
		int synStep = (int) (anaStep*rate);
		int numWnd = (int) Math.ceil((data.length-wndSz)/anaStep+1);
		float[] hanningWnd = hanning(wndSz);
		float[][] frames = new float[numWnd][];
		for (int i = 0; i < numWnd; i++) {
			frames[i]= new float[wndSz]; 
		}
		for (int i = 0; i < numWnd; i++) {
			for (int j = 0; j < wndSz; j++) {
				try {
					frames[i][j] = data[i*anaStep+j]*hanningWnd[j]; 
				} catch (ArrayIndexOutOfBoundsException e) {
					// TODO: handle exception
					frames[i][j]= 0;
				}
			}
		}
		int N = wndSz+(numWnd-1)*synStep;
		float[] ret = new float[N];
		for (int i = 0; i < N; i++) {
			ret[i] = 0;
		}
		for (int i = 0; i < numWnd; i++) {
			for (int j = 0; j < wndSz; j++) {
				ret[i*synStep+j] += frames[i][j];
			}
		}
		return ret;		
	}
	//»ìÒô
	public float[] mix(float[] d){
		
		float data1[] = resample_linear(1.3f);
		float b[] = d;
		/*float[] longer, shorter;
		if(d.length>data1.length){
			longer = d;
			shorter = data1;
		} else {
			longer = data1;
			shorter = d;
		}
		float[] ret = new float[longer.length];
		for(int i=0; i<longer.length;i++){
			int j = i;
			if(i >= shorter.length){
				j = i/shorter.length;	
			}
			ret[i] = longer[i] + shorter[j];
		}*/
		float[] ret = new float[data1.length];
		for(int i=0; i<ret.length; i++){
			int j = i;
			if(i >= b.length){
				j = i/b.length;
			}
			ret[i] = data1[i]+b[j];			
		}
		return ret;
	}
	
	public float[] filter(float[] b, float[] a){
		LinkedList<Float> qa=new LinkedList<Float>();
		LinkedList<Float> qb=new LinkedList<Float>();
		
		HashMap<Integer, Float> ha = new HashMap<Integer, Float>();
		HashMap<Integer, Float> hb = new HashMap<Integer, Float>();
		
		for(int i=1;i<b.length;i++){
			qb.add(0f);
			if(b[i]!=0){
				hb.put(i, b[i]);	
			}	
		}
		for(int i=1;i<a.length;i++){
			qa.add(0f);
			if(a[i]!=0){
				ha.put(i, a[i]);				
			}
		}
		float[] y = new float[data.length];
		for(int i = 0;i<data.length;i++){
			float cur = data[i];
			float res = cur*b[0];
			for(Integer k:hb.keySet()){
				res += qb.get(b.length-k-1)*hb.get(k);
			}
			for(Integer k:ha.keySet()){
				res -= qa.get(a.length-k-1)*ha.get(k);
			}
			qb.add(cur);
			qb.remove(0);
			qa.add(res);
			qa.remove(0);
			y[i]=res;
		}
		return y;
	}
	
	public float[] lowPassFilter(){ //µÍÍ¨ÂË²¨
		float[] a = new float[]{1};
		float[] b = new float[]{0.2f,0.2f,0.2f,0.2f,0.2f};
		float[] lp = filter(b,a);
		return lp;
	}
	
	public float[] highPassFilter(){ //¸ßÍ¨ÂË²¨
		float[] a = new float[]{1};
		float[] b = new float[]{1,-1};
		float[] hp = filter(b,a);
		return hp;
	}
	
	public float[] removeVoice(){ //È¥³ýÈËÉù
		if (getChannel()<2) {
			return highPassFilter();
		}
		float[] a = getChannelData(1);
		float[] b = getChannelData(2);
		System.out.println(getChannel());
		System.out.println(a.length+","+b.length);
		float[] c = new float[data.length];
		for (int i = 0; i < a.length; i++) {
			c[2*i] = a[i]-b[i]; //×óÉùµÀ¼õÓÒÉùµÀ
			c[2*i+1] = b[i]-a[i];
		}
		System.out.println();
		return c;
	}

	public float[] getChannelData(int n) {
		float data1[] = new float [data.length/2];
		int flag;
		if (n ==1) {
			 flag = 0;
		}else if (n ==2) {
			 flag = 1;
		}else  {
			System.out.println("error input");
			return null;
		}
		for (int i = 0; i < data.length/2; i++) {
			
			data1[i] = data[2*i+flag];//Channel1
		}
		return data1;
	}
	//»ØÉùÂË²¨Æ÷
	public float[] echo(){
		float[] a = new float[]{1};
		float delay = 0.2f;
		float[] b = new float[(int)(2+delay*sampleRate)];
		for(int i=0; i<b.length;i++){
			b[i] = 0;
		}
		b[0] = 1;
		b[b.length-1] = 0.8f;
		float[] echo = filter(b, a);
		return echo;
	}
	//È«Í¨ÂË²¨Æ÷
	public float[] allpass(){
		float delay = 0.2f;
		float[] a = new float[(int)(2+delay*sampleRate)];
		float[] b = new float[(int)(2+delay*sampleRate)];
		for(int i=0; i<b.length;i++){
			b[i] = 0;
			a[i] = 0;
		}
		b[0] = 1;
		b[b.length-1] = 0.5f;
		a[0] = 1;
		a[a.length-1] = -0.5f;
		float[] allpass = filter(b, a);
		return allpass;
	} 
	
	@SuppressWarnings("null")
	public float[] ghost(){
		//AudioProcesser b = new AudioProcesser(resample_linear(),sample)
		//data = resample(1.1f);
		AudioFileIn a = new AudioFileIn("ghost1.wav");
		float[] b = a.getSampleData();
		float[] ghost = null;
		if(a.getChannels()>1){
			float[] b1 = new float[b.length/2];
			for (int i = 0; i < b.length/2; i++) {			
				b1[i] = b[2*i];//Channel1
			}
			ghost = mix(b1);
		}
		else{
			ghost = mix(b);		
		}
		return ghost;
	}
	
	public float[] girl(){
		float[] girl = ola(1.2f);
		return girl;
	}
	
	public float[] boy(){
		float[] boy = ola(0.8f);
		return boy;
	}
	//¾í»ý£¨»ìÏì£©
	public float[] conv(){
		AudioFileIn a = new AudioFileIn("ghost1.wav");
		int M = data.length;
		int N = a.getSampleData().length;
		float[] ret = new float[N+M-1];//Êä³öÐòÁÐ³¤ÎªN+M-1
		for(int n=0; n<M+N-1; ++n){
			for(int i=0; i<M; ++i){
				if(n-i>=0 && n-i<N){
					ret[n] += data[i]*a.getSampleData()[n-i+1];
				}
			}
		}
		return ret;
	}
	
	class mPlay implements Runnable
	{
		//²¥·ÅÉùÒô
		public void run() {
			String tempname = "temp.aiff";
			new AudioFileOut(data, tempname, channel, sampleRate, bitDepth);
			Play.au(tempname,false);
		}		
	}
	
public float[] fft() {
		int n = calcN(data.length);
		int index = 0;
		float[] temp = new float[n];
		float[] mag = new float[n];
		float[] real = new float[n];
		float[] imaginary = new float[n];
			for (int i = 0; i < data.length; i++) {
				temp[i] = data[i];
			}
		RealFloatFFT_Radix2 rf = new  RealFloatFFT_Radix2(n);
		System.out.println("*****************");
		System.out.println(data.length +","+n);
		System.out.println("*****************");
		rf.transform(temp);
		for (int i = 0; i < n/2; i++) {
			if (i == 0) {
				real[index] = temp[i]; 
				imaginary[index++] = 0;
				
			}else{
				real[index] = temp[i]; 
				imaginary[index++] = temp[n-i];
			}
			mag[index-1] = (float) Math.sqrt(real[index-1]*real[index-1]
					+imaginary[index-1]*imaginary[index-1]) ;
		}
		for (int i = n/2; i >= 1 ; i--) {
			 
				if (i == n/2) {
				real[index] = temp[i]; 
				imaginary[index++] = 0;
			}else{
				real[index] = temp[i]; 
				imaginary[index++] = -temp[n-i];
			}
				mag[index-1] = (float) Math.sqrt(real[index-1]*real[index-1]
						+imaginary[index-1]*imaginary[index-1]) ;
			//System.out.println(real[i]+" + "+imaginary[i]+"i;");
			
		}
		System.out.println("FFT END");
		return mag;
	}

	private  int calcN(int length) {//¼ÆËãFFTµÄµãÊý
		// TODO Auto-generated method stub
		{
		    if (0==(length & (length-1))) {
		            return length;
		        }
		        int temp = length;
		        //while (temp >>= 1) {
		        temp = temp>>1;
		        while(temp!=0){
		        	length |= temp;
		            temp = temp>>1;
		        }
		        return length+1;

		}
	}
	
public void drawFFT(){
	fftThread ft = new fftThread();
	Thread t = new Thread(ft);
	t.start();
}
class fftThread implements Runnable
{
	//»æÖÆÆµÆ×Í¼
	public void run() {
		int N = calcN(data.length);
		float[] mag = new float[N];
		int fs = getSampleRate();
		mag = fft();
		double [] xData = new double[N];
		double [] yData = new double[N];
		for (int i = 0; i < N; i++) {
			//xData[i] = i;
			yData[i] = mag[i];
			xData[i] = i*fs/N;
		}
		XYChart chart = QuickChart.getChart("fft","fre", "mag",  "y(x)", xData, yData);
		new SwingWrapper(chart).displayChart();
	}		
}		
}
