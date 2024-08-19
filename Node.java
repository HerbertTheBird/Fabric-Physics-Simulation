import java.awt.Color;
import java.awt.Graphics2D;
import java.util.*;
public class Node {
	double x, y;
	//points to nodes connected by spring
	ArrayList<Node> neighbors;
	//length to each node
	ArrayList<Double> length;
	//stress of each spring
	ArrayList<Double> stress;
	double vx = 0, vy = 0;
	//max force before it breaks
	double maxForce = 80;
	//if exist is set to false, the cloth will effectively be removed
	boolean exist = true;
	//this if for dragging cloth
	boolean selected;
	double selectedX, selectedY;
	//air resistance
	double airResistance = .003;
	public Node(double x, double y) {
		this.x = x;
		this.y = y;
	}
	public void setNeighbors(ArrayList<Node> neighbors) {
		//cant setup the neighbors in contructer, since some of them arent initialized yet
		this.neighbors = neighbors;
		stress = new ArrayList<Double>();
		length = new ArrayList<Double>();
		for(int i = 0; i  < neighbors.size(); i++) {
			stress.add(0.0);
			length.add(dist(this, neighbors.get(i)));
		}
		
	}
	public void updateVelocity() {
		if(!exist) {
			return;
		}
		//apply gravity
		vy += Main.g*Main.dt;
		for(int i = 0; i < neighbors.size(); i++) {
			Node other = neighbors.get(i);
			if(!other.exist) {
				continue;
			}
			double dist = dist(this, other);
			if(dist < .1) {
				dist = .1;
			}
			//this is the force from the spring
			double force = -(dist-length.get(i))*Main.strength;
			//break if force is too big
			if(force < -maxForce*Main.drag) {
				exist = false;
			}
			//dont apply force in the other direction, so springs dont push, only pull
			if(force > 0) {
				force = 0;
			}
			//add this value to stress arraylist, used while painting
			stress.set(i, Math.min(-force, 20));
			//update velocity
			vx += (this.x-other.x)/dist * force / Main.mass * Main.dt;
			vy += (this.y-other.y)/dist * force / Main.mass * Main.dt;
		}
		
		//air resistance stuff
		double speed = dist(0, 0, vx, vy);
		double c = Main.dt * airResistance;
		double delta_v = speed*speed*c;
		
		double new_speed = speed;
		if (delta_v < speed) {
			new_speed -= delta_v;
		}
		if(speed > 0.01) {
			vx = vx/speed*new_speed;
			vy = vy/speed*new_speed;
		}
		
		//makes it move toward mouse if selected
		if(selected) {
			vx += (Main.mouseX+selectedX-x)*Main.dt*5;
			vy += (Main.mouseY+selectedY-y)*Main.dt*5;
			
		}
	}
	public void updatePosition() {
		if(!exist) {
			return;
		}
		//updates position
		x += vx*Main.dt;
		y += vy*Main.dt;
		//dont let it fall too far, ground is at y=1000
		if(y > 1000) {
			y = 1000;
			if(vy > 0) {
				vy = 0;
			}
		}
	}
	public void paint(Graphics2D g2d) {
		if(!exist) {
			return;
		}
		//draw line to each neighbor
		for(int i = 0; i < neighbors.size(); i++) {
			Node n = neighbors.get(i);
			if(n.exist) {
				//makes the saturation go up with more stress, so it makes it more red
				g2d.setColor(new Color(Color.HSBtoRGB(0, (float)(stress.get(i)/60), 1)));
				g2d.drawLine((int)x, (int)y, (int)n.x, (int)n.y);
			}
		}
	}
	public double dist(Node n1, Node n2) {
		return dist(n1.x, n1.y, n2.x, n2.y);
	}
	public double dist(double x1, double y1, double x2, double y2) {
		return Math.sqrt((x1-x2)*(x1-x2)+(y1-y2)*(y1-y2));
	}
}
