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

// TODO STATUS PANE TO DISPLAY CURRENT ACTION / QUEUE
class GUI extends JFrame {

	/** https://de.wikipedia.org/wiki/Goldener_Schnitt */
	private final double DIVIDER_POSITION = 0.618d;

	private final DirStatTree tree;
	private final UserController ctrl;

	GUI(final DirStatTree tree, UserController ctrl) {
		super(tree.getRoot().toString() + " – " + Args.NAME + " " +
								Args.VERSION);
		this.tree = tree;
		this.ctrl = ctrl;

		Container cp = getContentPane();
		cp.setLayout(new BorderLayout());
		cp.add(BorderLayout.CENTER, createMainPanel());

		position(this);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	private JComponent createMainPanel() {
		final JSplitPane horiz =
				new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		horiz.setOneTouchExpandable(true);
		horiz.setTopComponent(createNavigationTreeTable());
		horiz.setBottomComponent(createDetailPanel());
		addWindowListener(new WindowAdapter() {
			// TODO POSSIBLY ALSO ON RESIZE IF NOTHING BETTER
			@Override
			public void windowOpened(WindowEvent e) {
				horiz.setDividerLocation(DIVIDER_POSITION);
			}
		});
		return horiz;
	}

	/** https://blogs.oracle.com/geertjan/entry/swing_outline_component */
	private JComponent createNavigationTreeTable() {
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

		ctrl.setTable(treeTable);
		ContextMenu cntxt = new ContextMenu(ctrl);
		treeTable.addMouseListener(cntxt);

		return new JScrollPane(treeTable);
	}

	private JComponent createDetailPanel() {
		return new JPanel();
	}

	private static void position(final JFrame frm) {
		WindowUtils u = new WindowUtils(frm, 64);
		u.resize(WindowUtils.Size.BIG);
		u.place(WindowUtils.Place.TOP_LEFT);
	}

}
