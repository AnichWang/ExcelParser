package com.amzl.display;

import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;

import com.amzl.Parser.CK_Extractor;
import com.amzl.Parser.Complaint_Extractor;
import com.amzl.Parser.Time_Extractor;
import com.amzl.util.ObtainInput;

public class MainFrame {

	private JFrame frame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainFrame window = new MainFrame();
					window.frame.setVisible(true);
				} catch (Exception e) {
					System.exit(0);
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainFrame() {
		initialize();
		ObtainInput.createDir("CK_Extractor");
		ObtainInput.createDir("Complaint_Extractor");
		ObtainInput.createDir("Time_Extractor");
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		frame.setResizable(false);
		frame.getContentPane().setLayout(new GridLayout(1, 0, 0, 0));
		// 主窗体居中并设置大小为400×600
		int screenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
		int screenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;
		frame.setLocation((screenWidth - 400) / 2, (screenHeight - 600) / 2);
		frame.setSize(400, 600);

		JScrollPane scrollPane = new JScrollPane();
		frame.getContentPane().add(scrollPane);

		JTextPane textPane = new JTextPane();
		scrollPane.setViewportView(textPane);
		textPane.setEditable(false);

		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);

		JMenu mnNewMenu = new JMenu("开始");
		menuBar.add(mnNewMenu);

		JMenuItem menuItem_ExtractCK = new JMenuItem("提取CK订单");
		// 提取CK字段
		menuItem_ExtractCK.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//所有按钮为unable
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						mnNewMenu.setEnabled(false);
						textPane.setText("");
					}
				});
				//清空文字面板
				textPane.setText("");
				
				CK_Extractor CK_extractor = new CK_Extractor(textPane);
				new Thread(CK_extractor).start();
				
				//所有按钮恢复正常
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						mnNewMenu.setEnabled(true);
					}
				});
			}
		});
		mnNewMenu.add(menuItem_ExtractCK);

		// 提取投诉信息
		JMenuItem menuItem_ExtractComplaint = new JMenuItem("提取投诉信息");
		menuItem_ExtractComplaint.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//所有按钮为unable
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						mnNewMenu.setEnabled(false);
						textPane.setText("");
					}
				});
				//清空文字面板
				textPane.setText("");
				
				Complaint_Extractor complaint_Extractor = new Complaint_Extractor(textPane);
				// 提取过程在单独的线程中
				new Thread(complaint_Extractor).start();
				
				//所有按钮恢复正常
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						mnNewMenu.setEnabled(true);
					}
				});
			}
		});
		mnNewMenu.add(menuItem_ExtractComplaint);

		// 处理时长分析
		JMenuItem menuItem_TimeParse = new JMenuItem("处理时长分析");
		menuItem_TimeParse.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//所有按钮为unable
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						mnNewMenu.setEnabled(false);
						textPane.setText("");
					}
				});
				//清空文字面板
				textPane.setText("");
				
				Time_Extractor time_Extractor = new Time_Extractor(textPane);
				new Thread(time_Extractor).start();
				
				//所有按钮恢复正常
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						mnNewMenu.setEnabled(true);
					}
				});
			}
		});
		mnNewMenu.add(menuItem_TimeParse);

		// 程序退出
		JMenuItem menuItem_exit = new JMenuItem("退出");
		menuItem_exit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				frame.dispose();
				System.exit(0);
			}
		});
		mnNewMenu.add(menuItem_exit);
		// 帮助菜单
		JMenu menu = new JMenu("帮助");
		menuBar.add(menu);
		// 帮助按钮
		JMenuItem menuItem_help = new JMenuItem("帮助");
		menuItem_help.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				HelpDiolog.getHelpDiolog();
			}
		});
		menu.add(menuItem_help);

		// 添加关闭窗口的事件监听
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				frame.dispose();
				System.exit(0);
			}
		});
	}

}
