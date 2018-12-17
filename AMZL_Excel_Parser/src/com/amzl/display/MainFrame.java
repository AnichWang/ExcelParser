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
		// ��������в����ô�СΪ400��600
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

		JMenu mnNewMenu = new JMenu("��ʼ");
		menuBar.add(mnNewMenu);

		JMenuItem menuItem_ExtractCK = new JMenuItem("��ȡCK����");
		// ��ȡCK�ֶ�
		menuItem_ExtractCK.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//���а�ťΪunable
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						mnNewMenu.setEnabled(false);
						textPane.setText("");
					}
				});
				//����������
				textPane.setText("");
				
				CK_Extractor CK_extractor = new CK_Extractor(textPane);
				new Thread(CK_extractor).start();
				
				//���а�ť�ָ�����
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						mnNewMenu.setEnabled(true);
					}
				});
			}
		});
		mnNewMenu.add(menuItem_ExtractCK);

		// ��ȡͶ����Ϣ
		JMenuItem menuItem_ExtractComplaint = new JMenuItem("��ȡͶ����Ϣ");
		menuItem_ExtractComplaint.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//���а�ťΪunable
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						mnNewMenu.setEnabled(false);
						textPane.setText("");
					}
				});
				//����������
				textPane.setText("");
				
				Complaint_Extractor complaint_Extractor = new Complaint_Extractor(textPane);
				// ��ȡ�����ڵ������߳���
				new Thread(complaint_Extractor).start();
				
				//���а�ť�ָ�����
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						mnNewMenu.setEnabled(true);
					}
				});
			}
		});
		mnNewMenu.add(menuItem_ExtractComplaint);

		// ����ʱ������
		JMenuItem menuItem_TimeParse = new JMenuItem("����ʱ������");
		menuItem_TimeParse.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//���а�ťΪunable
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						mnNewMenu.setEnabled(false);
						textPane.setText("");
					}
				});
				//����������
				textPane.setText("");
				
				Time_Extractor time_Extractor = new Time_Extractor(textPane);
				new Thread(time_Extractor).start();
				
				//���а�ť�ָ�����
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						mnNewMenu.setEnabled(true);
					}
				});
			}
		});
		mnNewMenu.add(menuItem_TimeParse);

		// �����˳�
		JMenuItem menuItem_exit = new JMenuItem("�˳�");
		menuItem_exit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				frame.dispose();
				System.exit(0);
			}
		});
		mnNewMenu.add(menuItem_exit);
		// �����˵�
		JMenu menu = new JMenu("����");
		menuBar.add(menu);
		// ������ť
		JMenuItem menuItem_help = new JMenuItem("����");
		menuItem_help.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				HelpDiolog.getHelpDiolog();
			}
		});
		menu.add(menuItem_help);

		// ��ӹرմ��ڵ��¼�����
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				frame.dispose();
				System.exit(0);
			}
		});
	}

}
