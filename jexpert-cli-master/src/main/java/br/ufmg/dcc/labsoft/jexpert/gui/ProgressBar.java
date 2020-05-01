package br.ufmg.dcc.labsoft.jexpert.gui;

import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;

public class ProgressBar extends JFrame {
	private static final long serialVersionUID = 1L;
	
	private JProgressBar progressBar = new JProgressBar();
	private JFrame frame = new JFrame("JExpert Version 0.1");

	void updateProgress(final int newValue) {
		progressBar.setValue(newValue);
	}

	public void setValue(final int j) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				updateProgress(j);
			}
		});
	}

	public void desenho(int max) {

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container content = frame.getContentPane();
		// boolean flag=b;

		progressBar.setMinimum(0);
		progressBar.setMaximum(max);
		progressBar.setStringPainted(true);
		Border border = BorderFactory.createTitledBorder("Reading...");
		progressBar.setBorder(border);
		content.add(progressBar, BorderLayout.NORTH);
		frame.setSize(300, 100);
		frame.setVisible(true);
	}

	public void imprimir(int i) {
		int val = i + 1;
		//System.out.println("" + val + "%");
		progressBar.setValue(val);
	}

	public void setProgressBarVisibility(boolean visible) {
		progressBar.setVisible(visible);
		frame.setVisible(visible);
		if (visible == false) {
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		    System.exit(0);
		}
	}
}
