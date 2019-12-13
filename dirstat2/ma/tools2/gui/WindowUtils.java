package ma.tools2.gui;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.metal.DefaultMetalTheme;
import javax.swing.plaf.metal.MetalLookAndFeel;

/**
 * @version 1.0.1.0
 * @author Linux-Fan, Ma_Sys.ma
 * @since Tools 2 (WindowUtils in Tools 1)
 */
public class WindowUtils {

	public static final String METAL_LOOK_AND_FEEL = "javax.swing.plaf.metal.MetalLookAndFeel";
	
	public static class Size {
	
		private Size() {
			super(); // static
		}
		
		public static final int BIGGER = 12;
		public static final int BIG = 14;
		public static final int DEFAULT = 18;
		public static final int SMALL = 20;
		public static final int SMALLER = 30;
	
	}
	
	public static enum Place {
		MIDDLE,
		TOP_LEFT,
		TOP_RIGHT,
		BOTTOM_LEFT,
		BOTTOM_RIGHT
	}
	
	private Container wnd;
	private int padding;
	private Dimension screenSize;
	
	public WindowUtils(Container window) throws IllegalArgumentException {
		init(window, 0);
	}

	public WindowUtils(Container window, int padding) throws IllegalArgumentException {
		init(window, padding);
	}
	
	private void init(Container window, int padding) throws IllegalArgumentException {
		if(window == null) {
			throw new NullPointerException("Das Fenster kann nicht null sein.");
		}
		setPadding(padding);
		wnd = window;
	}
	
	public int getPadding() {
		return padding;
	}
	
	public void setPadding(int padding2) throws IllegalArgumentException {
		if(padding2 < 0) {
			throw new IllegalArgumentException("Der Abstand kann nicht kleiner als 0 sein.");
		}
		padding = padding2;
		screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	}

	public void resize(int newSize, int width, int height) {
		wnd.setSize(width * 10 / newSize, height * 9 / newSize);
	}
	
	public void resize(int newSize, Container desktop) {
		resize(newSize, desktop.getWidth(), desktop.getHeight());
	}
	
	public void resize(int newSize) {
		resize(newSize, screenSize.width, screenSize.height);
	}
	
	// TODO USE SOME KIND OF BTLR MATRIX HERE (NOT L3!)
	public void place(int height, int width, int x, int y, Place placement) throws IllegalArgumentException {
		if(height < 0 || width < 0) {
			throw new IllegalArgumentException("Die Koordinaten müssen im Positiven Bereich liegen.");
		}
		int location_x = 0;
		int location_y = 0;
		
		switch(placement) {
			case MIDDLE:
				location_x = ((width - (wnd.getWidth())) / 2) + x;
				location_y = ((height -(wnd.getHeight())) / 2) + y;
				break;
			case TOP_LEFT:
				location_x = padding;
				location_y = padding;
				break;
			case TOP_RIGHT:
				location_x = width - (wnd.getWidth() + padding) + x;
				location_y = padding;
				break;
			case BOTTOM_LEFT:
				location_x = padding;
				location_y = height - (wnd.getHeight() + padding) + y;
				break;
			case BOTTOM_RIGHT:
				location_x = width - (wnd.getWidth() + padding) + x;
				location_y = height - (wnd.getHeight() + padding) + y;
				break;
		}
		wnd.setLocation(location_x, location_y);
	}
	
	public void place(Container window, Place placement) throws NullPointerException, IllegalArgumentException {
		if(window == null) {
			throw new NullPointerException("Die Komponente kann nicht auf null plaziert werden, soll sie relativ zum Bildschirm platziert werden nutzen Sie die Funtkon place() ohne Argumente.");
		}
		place(window.getHeight(), window.getWidth(), window.getX(), window.getY(), placement);
	}
	
	public void place(Container window) throws NullPointerException {
		place(window.getHeight(), window.getWidth(), window.getX(), window.getY(), Place.MIDDLE);
	}
	
	public void place(Place placement) {
		place(screenSize.height, screenSize.width, 0, 0, placement);
	}
	
	public void place() {
		place(Place.MIDDLE);
	}
	
	public void refreshScreenSize() {
		screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	}
	
	public Dimension getSavedScreenSize() {
		return screenSize;
	}
	
	public void restoreOldSwingDesign() 
		throws UnsupportedLookAndFeelException, ClassNotFoundException, 
		InstantiationException, IllegalAccessException 
	{
		MetalLookAndFeel.setCurrentTheme(new DefaultMetalTheme());
		UIManager.setLookAndFeel(METAL_LOOK_AND_FEEL);
		SwingUtilities.updateComponentTreeUI(wnd);
	}
}
