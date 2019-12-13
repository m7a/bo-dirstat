package ma.dirstat.eval;

import java.util.concurrent.ThreadPoolExecutor;
import java.awt.*;
import javax.swing.*;

class DirStatStatusPanel extends JPanel {

	private final JLabel label;

	private int tasks;

	DirStatStatusPanel() {
		super(new FlowLayout(FlowLayout.LEFT));

		label = new JLabel("...");
		add(label);

		tasks = 0;
		updateLabel();
	}

	void updateTo(ThreadPoolExecutor e) {
		tasks = (int)(e.getTaskCount() - e.getCompletedTaskCount());
		updateLabel();
	}

	void dec() {
		tasks--;
		updateLabel();
	}

	private void updateLabel() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				if(tasks == 0)
					label.setText("idle");
				else
					label.setText("tasks = " + tasks);
				label.repaint();
			}
		});
	}

}
