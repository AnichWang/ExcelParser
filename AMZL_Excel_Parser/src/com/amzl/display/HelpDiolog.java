package com.amzl.display;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;

public class HelpDiolog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static HelpDiolog helpDiolog = null;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			new HelpDiolog();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static HelpDiolog getHelpDiolog() {
		if(helpDiolog == null) {
			helpDiolog = new HelpDiolog();
			return helpDiolog;
		} else {
			helpDiolog.setVisible(true);
			return helpDiolog;
		}
	}
	
	/**
	 * Create the dialog in singleton.
	 */
	private HelpDiolog() {
		
		//���ô���λ�����С
		int screenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;  
        int screenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;
        setLocation((screenWidth - 400)/2, (screenHeight-540)/2);
        setSize(400, 540);
        //diolog���ڹر�
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				helpDiolog = null;
			}
		});
		setVisible(true);
		setResizable(false);
        
		getContentPane().setLayout(new BorderLayout());
		
		JPanel textPanel = new JPanel();
		getContentPane().add(textPanel, BorderLayout.CENTER);
		textPanel.setLayout(new GridLayout(1, 0, 0, 0));
		
		//��ʾ�������ı����棬�����Զ����JTextPane
		JTextPane textPane = new JTextPane();
		Document document = textPane.getDocument();
		SimpleAttributeSet attribut = new SimpleAttributeSet();
		textPane.setEditable(false);
		textPane.setText("1, �����ļ���λ���ڣ�C:\\Users\\'Login'\\temp������'Login'Ϊ�û�Login"
				+ " "+"CK_ExtractorΪ��ȡCK���������ļ���"
				+ ""
				+ ""
				+ ""
				+ ""
				+ "");
		textPanel.add(textPane);
		
		
		
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setBackground(Color.WHITE);
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			JButton okButton = new JButton("ȷ��");
			//���ȷ�ϼ����¼����������ȷ�ϹرնԻ���
			okButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					//setVisible(false);
					helpDiolog = null;
					dispose();
				}
			});
			okButton.setActionCommand("OK");
			buttonPane.add(okButton);
			getRootPane().setDefaultButton(okButton);
		}
	}

}
