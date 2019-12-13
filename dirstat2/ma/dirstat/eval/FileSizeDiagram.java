package ma.dirstat.eval;

import java.util.ArrayList;

import java.awt.*;
import java.awt.geom.Point2D;

import javax.swing.JComponent;

import ma.tools2.MTC;

import ma.dirstat.FileSize;

class FileSizeDiagram extends JComponent {

	private static final int PADDING = 10;

	private static final int PADDING2  = 2 * PADDING;
	private static final int PADDINGD2 = PADDING / 2;

	private static final long[] UNITS = {
		32,
		FileSize.ONE_KIB, 32 * FileSize.ONE_KIB,
		FileSize.ONE_MIB, 32 * FileSize.ONE_MIB,
		FileSize.ONE_GIB, 32 * FileSize.ONE_GIB,
		FileSize.ONE_TIB, 32 * FileSize.ONE_TIB,
		FileSize.ONE_PIB, 32 * FileSize.ONE_PIB,
		FileSize.ONE_EIB, 32 * FileSize.ONE_EIB
	};

	private final ArrayList<Point2D> points;
	private final Point2D.Double largest;

	private int height;
	private int width;
	private int minHeight;

	FileSizeDiagram() {
		super();
		points    = new ArrayList<Point2D>();
		largest   = new Point2D.Double(1, 1);
		height    = 0;
		width     = 0;
		minHeight = 0;
	}

	void put(double lnSize, double count) {
		Point2D.Double toAdd = new Point2D.Double(lnSize, count);
		if(toAdd.x > largest.x)
			largest.x = toAdd.x;
		if(toAdd.y > largest.y)
			largest.y = toAdd.y;
		synchronized(points) {
			points.add(toAdd);
		}
	}

	void clear() {
		largest.x = 1;
		largest.y = 1;
		synchronized(points) {
			points.clear();
		}
		repaint();
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setFont(MTC.TERMINAL_FONT);

		int useH = height - PADDING2;

		// x-Axis
		g.drawLine(PADDINGD2, useH, width - PADDINGD2, useH);

		// units
		double cval = 0;
		for(int i = 0; i < UNITS.length && cval < largest.x; i++) {
			cval = Math.log(UNITS[i]);
			int x = calcX(cval);
			g.drawLine(x, useH, x, useH + PADDINGD2);
			String label = FileSize.format(UNITS[i]);
			int delta = g.getFontMetrics().stringWidth(label) / 2;
			g.drawString(label, x - delta, height);
		}

		// y-Axis
		int yY = height - PADDING - PADDINGD2;
		g.drawLine(PADDING, yY, PADDING, PADDINGD2);

		// values
		Point last = new Point(-1, -1);
		int largestY = (int)largest.y;
		ArrayList<Point> ap = new ArrayList<Point>();
		synchronized(points) {
			for(Point2D i: points) {
				int x = calcX(i.getX());
				if(x == last.x)
					last.y += i.getY();
				else
					ap.add(last = new Point(x,
								(int)i.getY()));
				if(last.y > largestY)
					largestY = last.y;
			}
		}
		for(Point i: ap) {
			int y = i.y * useH / largestY;
			g.drawLine(i.x, useH, i.x, useH - y);
		}
	}

	private int calcX(double val) {
		return (int)(PADDING + val * (width - PADDING2) / largest.x);
	}

	//-------------------[ Derived from ma.tools.gui.diagram.LineDiagram ]--
	
	public void setBounds(int x, int y, int width, int height) {
		super.setBounds(x, y, width, height);
		if(height > (minHeight + 5))
			this.height = height;
		else
			this.height = minHeight;
		this.width = width;
		repaint();
	}
	
	public void setBounds(Rectangle r) {
		setBounds(r.x, r.y, r.height, r.width);
	}
	
	public void setMinHeight(int minHeight) {
		this.minHeight = minHeight;
		repaint();
	}

	public Dimension getPreferredSize() {
		Dimension dim = super.getPreferredSize();
		if(dim.height < minHeight) {
			dim.height = minHeight;
			height = minHeight;
		} else {
			height = dim.height;
		}
		return dim;
	}

}
