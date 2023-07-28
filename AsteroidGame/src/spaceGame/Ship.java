package spaceGame;

import java.awt.Rectangle;

public class Ship {
	//declares the tip of the ship using a set of double and integers
	double xx, yy;
	int x, y;
	//the angle of the ship
	double angle;
	//the velocity of the ship
	double vel=0.0;
	//boolean to see if the ship is accelerating
	boolean isAccelerating = false;
	//the center coordinates of the ship
	double cx, cy;
	public Ship(int x1, int y1, double d) {
		//sets the tip x and y of the ship as well as the angle
		xx = x1;
		yy = y1;
		x = (int)xx;
		y = (int)yy;
		angle = d;
		//calculates the center x and y of the ship
		cx = (int)xx;
		cy = (int) yy+30;

	}
	//method used to calculate dx of the ship
	public double getDX() {
		double dx = Math.sin(angle);
		return dx;
	}
	//method used to calculate dy of the ship
	public double getDY() {
		double dy = Math.cos(angle);
		return dy;
	}
	//method used to move the ship
	public void move() {
		//gets dx and dy and multiplies it by the velocity
		double dx = getDX()*vel;
		double dy = getDY()*vel;
		//changes the location of the tip of the ship using dx and dy
		xx += dx;
		yy -= dy;
		//if the rocket if off the screen, make it appear on the opposite side
		if(xx+25 < 0) xx = 800;
		if(yy+75 < 0) yy = 800;
		if(yy > 800) yy = 0;
		if(xx-25 > 800) xx = 0;
		//converts the double to integer and stores it in the integer set of x and y
		x = (int)xx;
		y = (int)yy;
	}

}