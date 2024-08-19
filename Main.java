import java.util.*;

import javax.swing.JFrame;

import java.awt.*;
public class Main {
	//gravity
	static double g = 0.1;
	//default length for springs
	static double length = 5; 
	//spring strength
	static double strength = 10;
	//delta time (time increment)
	static double dt = .1;
	//number of sub steps
	static int substeps = 50;
	//size of cloth
	static int width = 80;
	static int height = 80;
	static Node[][] cloth = new Node[height][width];
	//mouse info for cutting, moving
	static int mouseX, mouseY;
	static boolean mouseDown = false;
	//top left position of cloth, can be moved
	static double x = 650, y = 350;
	//break force multiplier
	static double drag = 1;
	static boolean up = false, down = false, left = false, right = false;
	//speed you move the cloth with wasd
	static double speed = 3;
	//mass of node, doesnt really matter
	static double mass = 1;
	//previous mouse x and y, for cutting
	static int prevX = -1, prevY = -1;
	public static void main(String[] args) {
		setupCloth();
		JFrame f = new JFrame();
		f.setVisible(true);
		f.setSize(1700, 1100);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Display display = new Display();
		f.add(display);
		display.addMouseListener(display);
		display.addMouseMotionListener(display);
		f.addKeyListener(display);
	}
	public static void update() {
		for(int s = 0; s < substeps; s++) {
			//update nodes velocity
			for(int i = 1; i < height; i++) {
				for(int j = 0; j < width; j++) {
					cloth[i][j].updateVelocity();
				}
			}
			//update nodes position
			for(int i = 1; i < height; i++) {
				for(int j = 0; j < width; j++) {
					cloth[i][j].updatePosition();
				}
			}
			//locks top nodes
			for(int i = 0; i < width; i++) {
				cloth[0][i].x = x + i*length;
				cloth[0][i].y = y;
			}
			//moves top nodes with wasd
			if(up)
				y -= speed*dt;
			if(down)
				y += speed*dt;
			if(left)
				x -= speed*dt;
			if(right)
				x += speed*dt;
		}
		//makes the break strength higher when dragging cloth
		if(mouseDown) {
			drag = 10;
		}
		else {
			drag -= .5;
			if(drag < 1)
				drag = 1;
		}
	}
	public static void paint(Graphics g) {
		Graphics2D g2d = (Graphics2D)g;
		g2d.setColor(Color.black);
		g2d.fillRect(0, 0, 1700, 1100);
		g2d.setColor(Color.white);

		g2d.setStroke(new BasicStroke(1));
		for(int i = 0; i < height; i++) {
			for(int j = 0; j < width; j++) {
				cloth[i][j].paint(g2d);
			}
		}
	}
	public static void mousePressed(int x, int y) {
		mouseX = x;
		mouseY = y;
		mouseDown = true;
		for(int i = 1; i < height; i++) {
			for(int j = 0; j < width; j++) {
				//finds a node close to where clicked
				if(cloth[i][j].dist(x, y, cloth[i][j].x, cloth[i][j].y) < length) {
					cloth[i][j].selected = true;
					cloth[i][j].selectedX = cloth[i][j].x-x;
					cloth[i][j].selectedY = cloth[i][j].y-y;
					return;
				}
			}
		}
	}
	public static void cut(int mx, int my) {
		if(prevX == -1) {
			prevX = mx;
			prevY = my;
		}
		double dist = dist(prevX, prevY, mouseX, mouseY);
		for(int d = 0; d <= dist; d++) {
			//if the mouse moves too quickly, delete in between the previous mouse position, and current mouse position
			double x = prevX + (mx-prevX)*d/dist;
			double y = prevY + (my-prevY)*d/dist;
			for(int i = 1; i < height; i++) {
				for(int j = 0; j < width; j++) {
					if(cloth[i][j].dist(x, y, cloth[i][j].x, cloth[i][j].y) < length) {
						//'deletes' close nodes
						cloth[i][j].exist = false;
					}
				}
			}
		}
		prevX = mx;
		prevY = my;
	}
	public static void mouseReleased() {
		for(int i = 1; i < height; i++) {
			for(int j = 0; j < width; j++) {
				cloth[i][j].selected = false;
			}
		}
		mouseDown = false;
		prevX = -1;
		prevY = -1;
	}
	public static void mouseMove(int x, int y) {
		mouseX = x;
		mouseY = y;
	}
	public static void keyPressed(char c) {
		if(c == 'w') {
			up = true;
		}
		if(c == 's') {
			down = true;
		}
		if(c == 'a') {
			left = true;
		}
		if(c == 'd') {
			right = true;
		}
		if(c == ' ') {
			setupCloth();
		}
	}
	public static void keyReleased(char c) {
		if(c == 'w') {
			up = false;
		}
		if(c == 's') {
			down = false;
		}
		if(c == 'a') {
			left = false;
		}
		if(c == 'd') {
			right = false;
		}
	}
	public static void setupCloth() {
		//resets cloth, called at the beginning, and if user presses space
		for(int i = 0; i < height; i++) {
			for(int j = 0; j < width; j++) {
				cloth[i][j] = new Node(x+j*length, y+i*length);
			}
		}
		for(int i = 0; i < height; i++) {
			for(int j = 0; j < width; j++) {
				ArrayList<Node> neighbors = new ArrayList<Node>();
				if(i > 0) {
					neighbors.add(cloth[i-1][j]);
				}
				if(i < height-1) {
					neighbors.add(cloth[i+1][j]);
				}
				if(j > 0) {
					neighbors.add(cloth[i][j-1]);
				}
				if(j < width-1) {
					neighbors.add(cloth[i][j+1]);
				}
				cloth[i][j].setNeighbors(neighbors);
			}
		}
	}
	public static double dist(double x1, double y1, double x2, double y2) {
		return Math.sqrt((x1-x2)*(x1-x2)+(y1-y2)*(y1-y2));
	}
}
