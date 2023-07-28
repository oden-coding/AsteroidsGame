package spaceGame;

import java.awt.Rectangle;

public class Bullet {
	//declares the x, y and dx, dy of the asteroid
	int x, y;
	double dx;
	double dy;
	//creates a rectangle for the hitbox of the bullet and a time in which the bullet was fired
	Rectangle rect;
	long lastShotTime;
	public Bullet(Ship s) {
		//given ship s, set the x and y to be the tip of the ship
		x = s.x;
		y = s.y;
		//get the dx and dy by using the dx and dy of the ship
		dx = s.getDX()*12;
		dy = s.getDY()*12;
		//sets the lastshottime to the time the bullet was fired
		lastShotTime = System.currentTimeMillis();
		//creates the rectangular hitbox of the bullet
		rect = new Rectangle(x-1, y, 2, 5);
	}
	public void move() {
		//moves the bullet and resets the rectangular hitbox
		x += (int)dx;
		y -= (int)dy;
		rect = new Rectangle(x-1, y, 2, 5);
	}
}