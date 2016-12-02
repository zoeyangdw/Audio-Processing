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
	//����¼����ʽ
	AudioFormat af = null;
	//����Ŀ��������,���Դ��ж�ȡ��Ƶ����,�� TargetDataLine �ӿ��ṩ��Ŀ�������еĻ�������ȡ���������ݵķ�����
	TargetDataLine td = null;
	//����Դ������,Դ�������ǿ���д�����ݵ������С����䵱���Ƶ����Դ��Ӧ�ó�����Ƶ�ֽ�д��Դ�����У������ɴ����ֽڻ��岢�����Ǵ��ݸ���Ƶ����
	SourceDataLine sd = null;
	//�����ֽ��������������
	ByteArrayInputStream bais = null;
	ByteArrayOutputStream baos = null;
	//������Ƶ������
	AudioInputStream ais = null;
	//����ֹͣ¼���ı�־��������¼���̵߳�����
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
		
		File directory = new File("");//�趨Ϊ��ǰ�ļ��� 
		String path = new String();
		try{ 
			path = directory.getAbsolutePath();//��ȡ�������·�� 
		    //System.out.println(directory.getCanonicalPath());//��ȡ��׼��·�� 
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
				//����¼���ķ���
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
				dispose();//���ٵ�ǰ����
				mainFrame.setVisible(true);//��ʾ������
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
	
	//��ʼ¼��
	public void capture()
	{
		try {
			//afΪAudioFormatҲ������Ƶ��ʽ
			af = getAudioFormat();
			DataLine.Info info = new DataLine.Info(TargetDataLine.class,af);
			td = (TargetDataLine)(AudioSystem.getLine(info));
			//�򿪾���ָ����ʽ���У�������ʹ�л�����������ϵͳ��Դ����ÿɲ�����
			td.open(af);
			//����ĳһ������ִ������ I/O
			td.start();	
			//��������¼�����߳�
			Record record = new Record();
			Thread t1 = new Thread(record);
			t1.start();
			
		} catch (LineUnavailableException ex) {
			ex.printStackTrace();
			return;
		}
	}
	//ֹͣ¼��
	public void stop()
	{
		stopflag = true;			
	}
	//����¼��
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
	
	//����¼��
	public void save()
	{
		File getPath;
		JFileChooser fChooser = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter( "audio file","wav");
		fChooser.setFileFilter(filter);
		int value = fChooser.showSaveDialog(null);
		if(value==JFileChooser.APPROVE_OPTION){    //�жϴ����Ƿ����Ǵ򿪻򱣴� 
			 getPath=fChooser.getSelectedFile();       //ȡ��·��

			}else{
			  // û��ѡ�񣬼����˴��ڵ�ȡ��
				return;
			}
		if (audioProcesser != null) {
				audioProcesser.write(getPath.getAbsolutePath()+".wav");
		}else {
			 //ȡ��¼��������
	        af = getAudioFormat();

	        byte audioData[] = baos.toByteArray();
	        bais = new ByteArrayInputStream(audioData);
	        ais = new AudioInputStream(bais,af, audioData.length / af.getFrameSize());
	        //�������ձ�����ļ���
	        File file = null;
	        //д���ļ�
	        try {	
	        	
	        	file = new File(getPath.getAbsolutePath()+".wav");
	            AudioSystem.write(ais, AudioFileFormat.Type.WAVE, file);
	            audioProcesser =  new AudioProcesser(file.getAbsolutePath());
	        } catch (Exception e) {
	            e.printStackTrace();
	        }finally{
	        	//�ر���
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

	//����AudioFormat�Ĳ���
	public AudioFormat getAudioFormat() 
	{
		//����ע�Ͳ���������һ����Ƶ��ʽ�����߶�����
		AudioFormat.Encoding encoding = AudioFormat.Encoding.
        PCM_SIGNED ;
		float rate = 8000f;
		int sampleSize = 16;
		boolean bigEndian = true;
		int channels = 1;
		return new AudioFormat(encoding, rate, sampleSize, channels,
				(sampleSize / 8) * channels, rate, bigEndian);
	}
	//¼���࣬��ΪҪ�õ�MyRecord���еı��������Խ��������ڲ���
	class Record implements Runnable
	{
		//������¼�����ֽ�����,��Ϊ������
		byte bts[] = new byte[10000];
		//���ֽ������װ��������մ��뵽baos��
		//��дrun����
		public void run() {	
			baos = new ByteArrayOutputStream();		
			try {
				System.out.println("ok3");
				stopflag = false;
				while(stopflag != true)
				{
					//��ֹͣ¼��û����ʱ�����߳�һֱִ��	
					//�������е����뻺������ȡ��Ƶ���ݡ�
					//Ҫ��ȡbts.length���ȵ��ֽ�,cnt ��ʵ�ʶ�ȡ���ֽ���
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
					//�رմ򿪵��ֽ�������
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
	
	//������,ͬ��Ҳ�����ڲ���
	class Play implements Runnable
	{
		//����baos�е����ݼ���
		public void run() {
			audioProcesser.play();
		}		
	}

}

