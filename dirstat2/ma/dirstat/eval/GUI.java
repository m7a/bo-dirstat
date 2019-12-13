package ma.dirstat.eval;

import java.lang.reflect.Field;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;

import org.netbeans.swing.outline.Outline;
import org.netbeans.swing.outline.OutlineModel;
import org.netbeans.swing.outline.DefaultOutlineModel;

import ma.tools2.MTC;
import ma.tools2.gui.WindowUtils;

import ma.dirstat.Args;

class GUI extends JFrame {

	/** https://de.wikipedia.org/wiki/Goldener_Schnitt */
	private final double DIVIDER_POSITION = 0.618d;

	GUI(final DirStatTree tree, UserController ctrl,
						DirStatDetailPanel detail) {
		super(((ViewNode)(tree.getRoot())).id + " – " + Args.NAME +
							" " + Args.VERSION);
		Container cp = getContentPane();
		cp.setLayout(new BorderLayout());

		final JSplitPane horiz =
				new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		horiz.setOneTouchExpandable(true);
		horiz.setTopComponent(createNavigationTreeTable(tree, ctrl));
		horiz.setBottomComponent(detail);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				horiz.setDividerLocation(DIVIDER_POSITION);
			}
		});
		cp.add(BorderLayout.CENTER, horiz);

		position(this);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	/** https://blogs.oracle.com/geertjan/entry/swing_outline_component */
	private JComponent createNavigationTreeTable(DirStatTree tree,
							UserController ctrl) {
		Outline treeTable = new Outline();
		OutlineModel cont = DefaultOutlineModel.createOutlineModel(
						tree, tree, true, "Path");
		treeTable.setModel(cont);
		treeTable.setFont(MTC.TERMINAL_FONT);
		treeTable.setShowGrid(false);
		treeTable.setRowMargin(0);
		final TreeTableRenderer n = new TreeTableRenderer(
				treeTable.getDefaultRenderer(Object.class));
		treeTable.setDefaultRenderer(Object.class, n);
		//treeTable.setRowHeight(treeTable.getFontMetrics(
		//				MTC.TERMINAL_FONT).getHeight());
	
		tree.setGUI(treeTable);
		treeTable.setRowSorter(null);

		ctrl.setTable(treeTable);
		ContextMenu cntxt = new ContextMenu(ctrl);
		treeTable.addMouseListener(cntxt);

		return new JScrollPane(treeTable);
	}

	private static void position(final JFrame frm) {
		WindowUtils u = new WindowUtils(frm, 64);
		u.resize(WindowUtils.Size.BIG);
		u.place(WindowUtils.Place.TOP_LEFT);
	}

}
