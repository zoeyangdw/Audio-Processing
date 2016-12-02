package com.bupt.sdmda.yy.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Image;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JButton;

import com.bupt.sdmda.yy.process.AudioProcesser;
import com.bupt.sdmda.yy.process.BgPanel;
import com.bupt.sdmda.yy.process.DrawPanel;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.awt.Panel;

import javax.swing.ImageIcon;

import java.awt.Toolkit;

public class RecordData extends JFrame {	
	//定义录音格式
	AudioFormat af = null;
	//定义目标数据行,可以从中读取音频数据,该 TargetDataLine 接口提供从目标数据行的缓冲区读取所捕获数据的方法。
	TargetDataLine td = null;
	//定义源数据行,源数据行是可以写入数据的数据行。它充当其混频器的源。应用程序将音频字节写入源数据行，这样可处理字节缓冲并将它们传递给混频器。
	SourceDataLine sd = null;
	//定义字节数组输入输出流
	ByteArrayInputStream bais = null;
	ByteArrayOutputStream baos = null;
	//定义音频输入流
	AudioInputStream ais = null;
	//定义停止录音的标志，来控制录音线程的运行
	Boolean stopflag = false;
	AudioProcesser audioProcesser;
	AudioProcesser opened;
	AudioProcesser proced;
	
	float[] data;
	private BgPanel contentPane;
	private DrawPanel drawPanel;
	public  JButton btnSave = new JButton("Save");
	
	/**
	 * Launch the application.
	 */
	/*public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					RecordData frame = new RecordData();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}*/

	/**
	 * Create the frame.
	 */
	public RecordData() {
		
		File directory = new File("");//设定为当前文件夹 
		String path = new String();
		try{ 
			path = directory.getAbsolutePath();//获取程序绝对路径 
		    //System.out.println(directory.getCanonicalPath());//获取标准的路径 
		    System.out.println(path);
		    
		}catch(Exception e){} 
		
		setResizable(false);
		setIconImage(Toolkit.getDefaultToolkit().getImage(path+"\\res\\recorder_icon_s.png"));
		setTitle("Record");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 632, 457);
		
		Image bg = new ImageIcon(path+"\\res\\bg2_icon.png").getImage();
		contentPane = new BgPanel(bg);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		final JButton btnOpen = new JButton("");
		btnOpen.setToolTipText("open");
		btnOpen.setIcon(new ImageIcon(path+"\\res\\open_icon_s.png"));
		btnOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//open an audio file
				Open(btnOpen);
			}
		});
		btnOpen.setBounds(496, 23, 60, 60);
		btnOpen.setBorderPainted(false);
		btnOpen.setContentAreaFilled(false);
		contentPane.add(btnOpen);
		
		JButton btnRecord = new JButton("");
		btnRecord.setToolTipText("record");
		btnRecord.setIcon(new ImageIcon(path+"\\res\\start_icon_s.png"));
		btnRecord.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//调用录音的方法
	            capture();
			}
		});
		btnRecord.setBounds(496, 95, 60, 60);
		btnRecord.setBorderPainted(false);
		btnRecord.setContentAreaFilled(false);
		contentPane.add(btnRecord);
		
		JButton btnOk = new JButton("");
		btnOk.setToolTipText("ok");
		btnOk.setIcon(new ImageIcon(path+"\\res\\go_icon_s.png"));
		btnOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){ 
				MainFrm mainFrame = new MainFrm();
				mainFrame.setLocationRelativeTo(null);
				mainFrame.setAudio(audioProcesser);
				dispose();//销毁当前窗口
				mainFrame.setVisible(true);//显示主窗口
			}
		});
		btnOk.setBounds(496, 314, 60, 60);
		btnOk.setBorderPainted(false);
		btnOk.setContentAreaFilled(false);
		contentPane.add(btnOk);
		
		JButton btnStop = new JButton("");
		btnStop.setToolTipText("stop");
		btnStop.setIcon(new ImageIcon(path+"\\res\\recorder_icon_s.png"));
		btnStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				stop();
				save();
			}
		});
		btnStop.setBounds(496, 168, 60, 60);
		btnStop.setBorderPainted(false);
		btnStop.setContentAreaFilled(false);
		contentPane.add(btnStop);
		
		JButton btnPlay = new JButton("");
		btnPlay.setToolTipText("play&plot");
		btnPlay.setIcon(new ImageIcon(path+"\\res\\play_icon_s.png"));
		btnPlay.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				play();
				plot();
			}
		});
		btnPlay.setBounds(496, 241, 60, 60);
		btnPlay.setBorderPainted(false);
		btnPlay.setContentAreaFilled(false);
		contentPane.add(btnPlay);

	}
	
	public void Open(Component parent){
		//open an audio file
		JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		String extt[] = { "mp3","wav","aiff" };
		FileNameExtensionFilter filter = new FileNameExtensionFilter( "audio file",extt);
	    fc.setFileFilter(filter);
		if (JFileChooser.APPROVE_OPTION == fc.showOpenDialog(parent)) {
		//tfPath.setText(fc.getSelectedFile().getAbsolutePath());
		audioProcesser = new AudioProcesser(fc.getSelectedFile().getAbsolutePath());
		}
	}
	
	//开始录音
	public void capture()
	{
		try {
			//af为AudioFormat也就是音频格式
			af = getAudioFormat();
			DataLine.Info info = new DataLine.Info(TargetDataLine.class,af);
			td = (TargetDataLine)(AudioSystem.getLine(info));
			//打开具有指定格式的行，这样可使行获得所有所需的系统资源并变得可操作。
			td.open(af);
			//允许某一数据行执行数据 I/O
			td.start();	
			//创建播放录音的线程
			Record record = new Record();
			Thread t1 = new Thread(record);
			t1.start();
			
		} catch (LineUnavailableException ex) {
			ex.printStackTrace();
			return;
		}
	}
	//停止录音
	public void stop()
	{
		stopflag = true;			
	}
	//播放录音
	public void play()
	{
		if (audioProcesser == null) {
			save();
		}
		audioProcesser.play();	
	}
	public void plot()
	{
		data = audioProcesser.getData();
		float[] data1 = new float[data.length];
		for(int i=0; i<data.length;i++){
			data1[i] = 150*data[i];
		}
		drawPanel = new DrawPanel(data1);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));		
		drawPanel.setBounds(59, 57, 378, 288);
		contentPane.add(drawPanel);
	}
	
	//保存录音
	public void save()
	{
		File getPath;
		JFileChooser fChooser = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter( "audio file","wav");
		fChooser.setFileFilter(filter);
		int value = fChooser.showSaveDialog(null);
		if(value==JFileChooser.APPROVE_OPTION){    //判断窗口是否点的是打开或保存 
			 getPath=fChooser.getSelectedFile();       //取得路径

			}else{
			  // 没有选择，即点了窗口的取消
				return;
			}
		if (audioProcesser != null) {
				audioProcesser.write(getPath.getAbsolutePath()+".wav");
		}else {
			 //取得录音输入流
	        af = getAudioFormat();

	        byte audioData[] = baos.toByteArray();
	        bais = new ByteArrayInputStream(audioData);
	        ais = new AudioInputStream(bais,af, audioData.length / af.getFrameSize());
	        //定义最终保存的文件名
	        File file = null;
	        //写入文件
	        try {	
	        	
	        	file = new File(getPath.getAbsolutePath()+".wav");
	            AudioSystem.write(ais, AudioFileFormat.Type.WAVE, file);
	            audioProcesser =  new AudioProcesser(file.getAbsolutePath());
	        } catch (Exception e) {
	            e.printStackTrace();
	        }finally{
	        	//关闭流
	        	try {
	        		if(bais != null)
	        		{
	        			bais.close();
	        		} 
	        		if(ais != null)
	        		{
	        			ais.close();		
	        		}
				} catch (Exception e) {
					e.printStackTrace();
				}   	
	        }
		}
	}

	//设置AudioFormat的参数
	public AudioFormat getAudioFormat() 
	{
		//下面注释部分是另外一种音频格式，两者都可以
		AudioFormat.Encoding encoding = AudioFormat.Encoding.
        PCM_SIGNED ;
		float rate = 8000f;
		int sampleSize = 16;
		boolean bigEndian = true;
		int channels = 1;
		return new AudioFormat(encoding, rate, sampleSize, channels,
				(sampleSize / 8) * channels, rate, bigEndian);
	}
	//录音类，因为要用到MyRecord类中的变量，所以将其做成内部类
	class Record implements Runnable
	{
		//定义存放录音的字节数组,作为缓冲区
		byte bts[] = new byte[10000];
		//将字节数组包装到流里，最终存入到baos中
		//重写run函数
		public void run() {	
			baos = new ByteArrayOutputStream();		
			try {
				System.out.println("ok3");
				stopflag = false;
				while(stopflag != true)
				{
					//当停止录音没按下时，该线程一直执行	
					//从数据行的输入缓冲区读取音频数据。
					//要读取bts.length长度的字节,cnt 是实际读取的字节数
					int cnt = td.read(bts, 0, bts.length);
					if(cnt > 0)
					{
						baos.write(bts, 0, cnt);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				try {
					//关闭打开的字节数组流
					if(baos != null)
					{
						baos.close();
					}	
				} catch (IOException e) {
					e.printStackTrace();
				}finally{
					td.drain();
					td.close();
				}
			}
		}		
	}
	
	//播放类,同样也做成内部类
	class Play implements Runnable
	{
		//播放baos中的数据即可
		public void run() {
			audioProcesser.play();
		}		
	}

}

