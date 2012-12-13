package com.sis.memcachedviewer;

import javax.swing.JFrame;
import java.awt.Toolkit;
import javax.swing.JTable;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.table.DefaultTableModel;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;

import net.spy.memcached.MemcachedClient;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ValueList extends JFrame {
	private static final long serialVersionUID = 94713104710420262L;
	private JTable table;
	private DefaultTableModel tableDataModel;
	
	private MemcachedClient memcachedClient;
	
	private void refreshValues() {
		for (int i = 0, j = tableDataModel.getRowCount(); i != j; i++) {
			String key = (String) tableDataModel.getValueAt(i, 0);
			
			tableDataModel.setValueAt(memcachedClient.get(key), i, 1);
		}
	}
	
	private ScheduledFuture<?> refreshInterval = null;
	
	public ValueList(MemcachedClient mc) {
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		memcachedClient = mc;
		
		setIconImage(Toolkit.getDefaultToolkit().getImage(ValueList.class.getResource("/com/sis/memcachedviewer/memcachedviewer.png")));
		setTitle("Value List (" + memcachedClient.getVersions() + ")");
		
		refreshInterval = MemcachedViewer.getScheduledExecutorService().scheduleAtFixedRate(new Runnable() {
			public void run() {
				refreshValues();
			}
		}, 5, 5, TimeUnit.SECONDS);
		
		tableDataModel = new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
				"Key", "Value" 
			}
		) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			Class<?>[] columnTypes = new Class[] {
				String.class, String.class
			};
			public Class<?> getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}
			boolean[] columnEditables = new boolean[] {
				true, true
			};
			public boolean isCellEditable(int row, int column) {
				return columnEditables[column];
			}
		};
		
		table = new JTable();
		table.setColumnSelectionAllowed(true);
		table.setCellSelectionEnabled(true);
		table.setFillsViewportHeight(true);
		table.setModel(tableDataModel);
		getContentPane().add(table, BorderLayout.CENTER);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnMemcached = new JMenu("memcached");
		menuBar.add(mnMemcached);
		
		JMenuItem mntmAddKey = new JMenuItem("Add Key");
		mnMemcached.add(mntmAddKey);
		
		mntmAddKey.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String key = JOptionPane.showInputDialog("Enter the key.");
				
				tableDataModel.addRow(new Object[] {key, ""});
				
				refreshValues();
			}
		});
		
		JMenuItem mntmRefreshValues = new JMenuItem("Refresh Values");
		mnMemcached.add(mntmRefreshValues);
		
		mntmRefreshValues.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				refreshValues();
			}
		});
		
		JMenuItem mntmFlushServer = new JMenuItem("Flush Server");
		mnMemcached.add(mntmFlushServer);
		
		mntmFlushServer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				memcachedClient.flush();
			}
		});
		
		JSeparator separator = new JSeparator();
		mnMemcached.add(separator);
		
		JMenuItem mntmClose = new JMenuItem("Close");
		mnMemcached.add(mntmClose);

		mntmClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				refreshInterval.cancel(false);
				MemcachedViewer.exit();
			}
		});
	}
}
