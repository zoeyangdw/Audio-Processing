package com.bupt.sdmda.yy.process;

import java.awt.EventQueue;

import com.bupt.sdmda.yy.view.RecordData;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					RecordData frame = new RecordData();
					frame.setLocationRelativeTo(null);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

	}

}
