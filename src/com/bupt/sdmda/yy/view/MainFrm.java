package com.bupt.sdmda.yy.view;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Image;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JButton;

import com.bupt.sdmda.yy.process.AudioProcesser;


import com.bupt.sdmda.yy.process.BgPanel;
import com.bupt.sdmda.yy.process.DrawPanel;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.ImageIcon;

import java.awt.Color;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JFileChooser;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import java.awt.SystemColor;
import java.awt.Font;
import java.awt.Toolkit;

public class MainFrm extends JFrame {

	private BgPanel contentPane;
	AudioProcesser audioProcesser;
	AudioProcesser opened;
	AudioProcesser proced;
	private JTextField tfb;
	private JTextField tfa;
	private  JTextArea logArea;
	
	private StringBuffer logString;
	private DrawPanel drawPanel;
	float[] data;

	/**
	 * Launch the application.
	 */
	/*public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainFrm frame = new MainFrm();
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
	public MainFrm() {
		
		File directory = new File("");//设定为当前文件夹 
		String path = new String();
		try{ 
			path = directory.getAbsolutePath();//获取程序绝对路径 
		    //System.out.println(directory.getCanonicalPath());//获取标准的路径 
		    System.out.println(path);
		    
		}catch(Exception e){} 

		
		setIconImage(Toolkit.getDefaultToolkit().getImage(path+"\\res\\recorder_icon_s.png"));
		setResizable(false);
		this.setLocationRelativeTo(null);
		logString = new StringBuffer("");
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 644, 459);
		Image bg = new ImageIcon(path+"\\res\\bg1_icon2.png").getImage();
		contentPane = new BgPanel(bg);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JButton btnEcho = new JButton("");
		btnEcho.setToolTipText("echo");
		btnEcho.setIcon(new ImageIcon(path+"\\res\\echo_icon2.png"));
		btnEcho.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				proced = new AudioProcesser(audioProcesser.echo(),audioProcesser.getChannel(),audioProcesser.getBitDepth(),audioProcesser.getSampleRate());
				play();
				plot();
			}
		});
		btnEcho.setBounds(384, 126, 75, 75);
		btnEcho.setBorderPainted(false);
		btnEcho.setContentAreaFilled(false);
		contentPane.add(btnEcho);
		
		JButton btnPervoice = new JButton("");
		btnPervoice.setToolTipText("removeVoice");
		btnPervoice.setIcon(new ImageIcon(path+"\\res\\removeVoice_icon.png"));
		btnPervoice.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				proced = new AudioProcesser(audioProcesser.removeVoice(),audioProcesser.getChannel(),audioProcesser.getBitDepth(),audioProcesser.getSampleRate());
				play();
				plot();
				String str = "removed voice ";
				addToLog(str);
			}
		});
		btnPervoice.setBounds(40, 278, 70, 70);
		btnPervoice.setBorderPainted(false);
		btnPervoice.setContentAreaFilled(false);
		contentPane.add(btnPervoice);
		
		JButton btnGhost = new JButton("");
		btnGhost.setToolTipText("ghost");
		btnGhost.setIcon(new ImageIcon(path+"\\res\\ghost_icon2.png"));
		btnGhost.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				proced = new AudioProcesser(audioProcesser.ghost(),audioProcesser.getChannel(),audioProcesser.getBitDepth(),audioProcesser.getSampleRate());
				play();
				plot();
				String str = "like ghost ";
				addToLog(str);
			}
		});
		btnGhost.setBounds(484, 126, 75, 75);
		btnGhost.setBorderPainted(false);
		btnGhost.setContentAreaFilled(false);
		contentPane.add(btnGhost);
		
		JButton btnGirl = new JButton("");
		btnGirl.setToolTipText("girl");
		btnGirl.setIcon(new ImageIcon(path+"\\res\\girl_icon2.png"));
		btnGirl.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				float rate = (float)audioProcesser.girl().length/(float)audioProcesser.getData().length;
				int sampleRate = (int)((float)audioProcesser.getSampleRate()*rate);
				proced = new AudioProcesser(audioProcesser.girl(),audioProcesser.getChannel(),audioProcesser.getBitDepth(),sampleRate);
				play();
				plot();
				String str = "like girl ";
				addToLog(str);
			}
		});
		btnGirl.setBounds(384, 25, 75, 75);
		btnGirl.setBorderPainted(false);
		btnGirl.setContentAreaFilled(false);
		contentPane.add(btnGirl);
		
		
		JButton btnBoy = new JButton("");
		btnBoy.setToolTipText("man");
		btnBoy.setIcon(new ImageIcon(path+"\\res\\boy_icon2.png"));
		btnBoy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				float rate = (float)audioProcesser.boy().length/(float)audioProcesser.getData().length;
				int sampleRate = (int)((float)audioProcesser.getSampleRate()*rate);
				proced = new AudioProcesser(audioProcesser.boy(),audioProcesser.getChannel(),audioProcesser.getBitDepth(),sampleRate);
				play();
				plot();
				String str = "like boy ";
				addToLog(str);
			}
		});
		btnBoy.setBounds(484, 25, 75, 75);
		btnBoy.setBorderPainted(false);
		btnBoy.setContentAreaFilled(false);
		contentPane.add(btnBoy);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(14, 364, 598, 37);
		contentPane.add(scrollPane);
		
		logArea = new JTextArea();
		logArea.setBackground(SystemColor.activeCaption);
		scrollPane.setViewportView(logArea);
		
		JLabel lblB = new JLabel("b");
		lblB.setFont(new Font("宋体", Font.PLAIN, 20));
		lblB.setForeground(SystemColor.text);
		lblB.setHorizontalAlignment(SwingConstants.CENTER);
		lblB.setBounds(394, 214, 20, 18);
		contentPane.add(lblB);
		
		JLabel lblA = new JLabel("a");
		lblA.setFont(new Font("宋体", Font.PLAIN, 20));
		lblA.setForeground(SystemColor.text);
		lblA.setHorizontalAlignment(SwingConstants.CENTER);
		lblA.setBounds(394, 247, 20, 18);
		contentPane.add(lblA);
		
		tfb = new JTextField();
		tfb.setBackground(SystemColor.activeCaption);
		tfb.setBounds(428, 214, 131, 24);
		contentPane.add(tfb);
		tfb.setColumns(10);
		
		tfa = new JTextField();
		tfa.setBackground(SystemColor.activeCaption);
		tfa.setBounds(428, 244, 131, 24);
		contentPane.add(tfa);
		tfa.setColumns(10);
		
		JButton btnFilter = new JButton("");
		btnFilter.setToolTipText("filter");
		btnFilter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				float [] a = getParaA();
				float [] b = getParaB();
				proced = new AudioProcesser(audioProcesser.filter(b,a),audioProcesser.getChannel(),audioProcesser.getBitDepth(),audioProcesser.getSampleRate());
				proced.play();
				String str = " filter " +"a="+ tfa.getText() +"  b="+tfb.getText();
				addToLog(str);
			}
		});
		btnFilter.setIcon(new ImageIcon(path+"\\res\\filter_icon.png"));
		btnFilter.setBounds(422, 278, 70, 70);
		btnFilter.setBorderPainted(false);
		btnFilter.setContentAreaFilled(false);
		contentPane.add(btnFilter);
		
		JButton btnFFT = new JButton("");
		btnFFT.setToolTipText("drawFFT");
		btnFFT.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				proced.drawFFT();
				String str = "draw fft";
				addToLog(str);
			}
		});
		btnFFT.setIcon(new ImageIcon(path+"\\res\\drawfft_icon.png"));
		btnFFT.setBounds(132, 278, 70, 70);
		btnFFT.setBorderPainted(false);
		btnFFT.setContentAreaFilled(false);
		contentPane.add(btnFFT);
		
		JButton btnPlay = new JButton("");
		btnPlay.setToolTipText("play");
		btnPlay.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				play();
			}
		});
		btnPlay.setIcon(new ImageIcon(path+"\\res\\play_icon2.png"));
		btnPlay.setBounds(234, 278, 70, 70);
		btnPlay.setBorderPainted(false);
		btnPlay.setContentAreaFilled(false);
		contentPane.add(btnPlay);
		
		JButton btnSave = new JButton("");
		btnSave.setToolTipText("save");
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				save(proced);
				String str = "save audio";
				addToLog(str);
			}
		});
		btnSave.setIcon(new ImageIcon(path+"\\res\\save_icon.png"));
		btnSave.setBounds(332, 278, 70, 70);
		btnSave.setBorderPainted(false);
		btnSave.setContentAreaFilled(false);
		contentPane.add(btnSave);
		
		JButton btnReturn = new JButton("");
		btnReturn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
				RecordData recordFrm = new RecordData();
				recordFrm.setLocationRelativeTo(null);
				recordFrm.setVisible(true);//显示录音窗口
			}
		});
		btnReturn.setIcon(new ImageIcon(path+"\\res\\go_icon_s.png"));
		btnReturn.setToolTipText("return");
		btnReturn.setBounds(506, 278, 70, 70);
		btnReturn.setBorderPainted(false);
		btnReturn.setContentAreaFilled(false);
		contentPane.add(btnReturn);
		
		/*JPanel panel = new JPanel();
		panel.setBounds(36, 35, 334, 224);
		contentPane.add(panel);*/
	}
	
	public void setAudio( AudioProcesser audioProcesser ) {
		this.audioProcesser = audioProcesser;
	}
	public void save( AudioProcesser audioProcesser ) {
		
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
			audioProcesser.write(getPath.getAbsolutePath()+".wav");
			
		}
	
	public void play(){
		/*Play play = new Play();
		Thread t1 = new Thread(play);
		t1.start();*/
		proced.play();
	}
	
	/*class Play implements Runnable
	{
		//播放处理过后的音频
		public void run() {
			proced.play();
		}		
	}*/
	private float[] getParaA(){
		String stringa = this.tfa.getText();
		System.out.println(stringa);
		String s[] = stringa.split(",");
		float[] a = new float[s.length];
		for (int i = 0; i < s.length; i++) {
			a[i] = Float.parseFloat(s[i]);
			System.out.print(s[i]+" ");
			}
		return a;

	}
	
	private float[] getParaB(){
		String stringb = this.tfb.getText();
		System.out.println(stringb);
		String s[] = stringb.split(",");
		float[] b = new float[s.length];
		for (int i = 0; i < s.length; i++) {
			b[i] = Float.parseFloat(s[i]);
			System.out.print(s[i]+" ");
			}
		return b;

	}
	private void addToLog(String str){
		Date date=new Date();
		DateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time=format.format(date);
		String thisLog = time+": "+str;
		logString.append(thisLog);
		logString.append("\n");
		logArea.setText(logString.toString());
	}
	
	public void plot()
	{
		data = proced.getData();
		float[] data1 = new float[data.length];
		for(int i=0; i<data.length;i++){
			data1[i] = 200*data[i];
		}
		if(drawPanel != null)
		{
			contentPane.remove(drawPanel);
		}
		drawPanel = new DrawPanel(data1);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));		
		drawPanel.setBounds(36, 35, 334, 224);
		contentPane.add(drawPanel);
	}
}
