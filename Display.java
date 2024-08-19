import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
public class Display extends JPanel implements ActionListener, MouseListener, MouseMotionListener, KeyListener{
	//this class just forwards everything to Main
	public Display() {
		//calls actionPerformed every 40 ms
		Timer t = new Timer(40, this);
		t.start();
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		Main.update();
		repaint();
	}
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Main.paint(g);
	}
	@Override
	public void mousePressed(MouseEvent e) {
		if(e.isPopupTrigger()) {
			Main.cut(e.getX(), e.getY());
		}
		else {
			Main.mousePressed(e.getX(), e.getY());
		}
	}
	@Override
	public void mouseReleased(MouseEvent e) {
		Main.mouseReleased();
	}
	@Override
	public void mouseDragged(MouseEvent e) {
		if(e.isPopupTrigger()) {
			Main.cut(e.getX(), e.getY());
		}
		else {
			Main.mouseMove(e.getX(), e.getY());
		}
	}
	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		Main.keyPressed(e.getKeyChar());
	}
	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		Main.keyReleased(e.getKeyChar());
	}

	@Override
	public void mouseClicked(MouseEvent e) {}
	@Override
	public void mouseEntered(MouseEvent e) {}
	@Override
	public void mouseExited(MouseEvent e) {}
	@Override
	public void mouseMoved(MouseEvent e) {}
	@Override
	public void keyTyped(KeyEvent e) {}
}
