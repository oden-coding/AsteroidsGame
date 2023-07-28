package spaceGame;

import java.awt.Rectangle;

public class Asteroids {
	//declares the size of the asteroid
	int size;
	//declares the x, y and the change in x and y for the asteroid
	int x, y, dx, dy;
	//declares the angle in which the asteroid is moving
	double angle;
	//the hitbox of the asteroid
	Rectangle asteroidHitbox;
	public Asteroids(int s, int x1, int y1, double a) {
		//sets the value for size, x, y and angle
		size = s;
		x = x1;
		y = y1;
		angle = a;
		//calculates dx and dy given the angle
		dx = (int)(Math.sin(angle)*3);
		dy = (int)(Math.cos(angle)*3);
		//creates the hitbox of the asteroid
		asteroidHitbox = new Rectangle(x-size/2, y, size, size);
	}
	//method to move the asteroid
	public void move() {
		//moves the x and y using dx and dy
		x+=dx;
		y+=dy;
		//if the asteroid goes off the screen, make it appear on the other side
		if(x+size/2 < 0) x = 800;
		if(y+size/2 < 0) y = 800;
		if(y-size/2 > 800) y = 0;
		if(x-size/2 > 800) x = 0;
		//moves the asteroid hitbox
		asteroidHitbox = new Rectangle(x-size/2, y, size, size);
	}
}