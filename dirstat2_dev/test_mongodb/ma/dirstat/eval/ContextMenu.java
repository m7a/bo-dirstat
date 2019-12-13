package ma.dirstat.eval;

import java.awt.Component;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;
import javax.swing.JMenu;

class ContextMenu extends JPopupMenu implements MouseListener {

	private final ActionListener l;

	ContextMenu(ActionListener l) {
		super();
		this.l = l;
		createDefaultMenuEntries();
	}

	private void createDefaultMenuEntries() {
		addSimpleItem("DirStat Menu").setEnabled(false);
		addSeparator();

		addAttachSubMenu();
		addSimpleItem("Delete");
		addSeparator();

		addExportSubMenu();
	}

	private JMenuItem addSimpleItem(String title) {
		return addSimpleItem(this, title);
	}

	private JMenuItem addSimpleItem(Object men, String title) {
		JMenuItem item = new JMenuItem(title);
		item.addActionListener(l);
		if(men instanceof JPopupMenu)
			((JPopupMenu)men).add(item);
		else
			((JMenu)men).add(item);
		return item;
	}

	private void addAttachSubMenu() {
		JMenu attach = new JMenu("Attach");
		addSimpleItem(attach, "File...");

		JMenu def = new JMenu("Default");
		for(String i: DirStatTree.DEFAULT_VIEWS)
			addSimpleItem(def, i).setActionCommand("jarcnt");
		attach.add(def);

		add(attach);
	}

	private void addExportSubMenu() {
		JMenu export = new JMenu("Export");
		addSimpleItem(export, "Text");
		addSimpleItem(export, "HTML");
		addSimpleItem(export, "CSV");
		add(export);
	}

	@Override
	public void mousePressed(MouseEvent event) {
		if(event.getButton() == MouseEvent.BUTTON3) {
			show((Component)event.getSource(), event.getX(),
								event.getY());
		}
	}

	// Not used
	public void mouseReleased(MouseEvent event) {}
	public void mouseClicked(MouseEvent event)  {}
	public void mouseEntered(MouseEvent event)  {}
	public void mouseExited(MouseEvent event)   {}
	
}
