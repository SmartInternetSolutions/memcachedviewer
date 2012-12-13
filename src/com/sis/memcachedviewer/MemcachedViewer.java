package com.sis.memcachedviewer;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import javax.swing.JFrame;
import javax.swing.UIManager;

import net.spy.memcached.MemcachedClient;

public class MemcachedViewer {
	private static MemcachedClient memcachedClient;
	
	private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(8);
	
	public static void main(String args[]) throws Exception {

		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		JFrame.setDefaultLookAndFeelDecorated(true);
		
		memcachedClient = new MemcachedClient(new InetSocketAddress("127.0.0.1", 11211));
		
		ValueList valueList = new ValueList(memcachedClient);
		valueList.setVisible(true);
	}

	public static void exit() {
		System.exit(0);
	}
	
	public static ScheduledExecutorService getScheduledExecutorService() {
		return scheduler;
	}
}
