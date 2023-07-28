package spaceGame;

//Andy and Steven
//June 19, 2023
//Asteroids game where you control a spaceship and try to destroy the asteroids without being hit
//Arrows are used to move
//Space is used shoot a bullet

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import hsa2.GraphicsConsole;


public class Game {

	//loads the images into the game
	BufferedImage backImg = LoadImage("background.jpeg");
	Image shipImgFire = LoadImage("Rocket_Fire.png").getScaledInstance(50, 120, Image.SCALE_DEFAULT);
	Image shipImg = LoadImage("Rocket_Ship.png").getScaledInstance(50, 75, Image.SCALE_DEFAULT);
	Image shipLifeImg = LoadImage("Rocket_Ship.png").getScaledInstance(15, 30, Image.SCALE_DEFAULT);

	//the time when the player last moved
	//used to control the rocket fire
	long lastMove = System.currentTimeMillis();

	//the time when the player started
	//used to give invincibility to the player
	long start = System.currentTimeMillis();

	//defines the width and height of the screen
	int winW = 800;
	int winH = 800;

	//gives the player 3 lives
	int lives = 3;

	//boolean to store if player has quit
	//used to close the program after the player has quit
	boolean quit = false;

	//defines the graphics console
	GraphicsConsole gc = new GraphicsConsole(winW,winH);

	//creates a arraylist that stores the asteroids and bullets
	ArrayList<Asteroids> asteroids = new ArrayList();
	ArrayList<Bullet> bullets = new ArrayList();

	//creates the ship object
	Ship s = new Ship(400, 400, 0);

	//defines the score font and sets the score to 0
	Font scoreFont = new Font ("Arial", Font.BOLD, 30);
	int score = 0;

	//this is the font, need to change to arcade font. (download from internet)
	Font headerFonts = new Font("Arial", Font.BOLD, 70);
	Font textFont = new Font("Arial", Font.BOLD, 15);

	//method used to load the images
	static BufferedImage LoadImage(String filename) {
		BufferedImage img = null;			
		try {
			img = ImageIO.read(new File(filename));
		} catch (IOException e) {
			System.out.println(e.toString());
			JOptionPane.showMessageDialog(null, "An image failed to load: " + filename , "ERROR", JOptionPane.ERROR_MESSAGE);
		}
		//DEBUG
		//if (img == null) System.out.println("null");
		//else System.out.printf("w=%d, h=%d%n",img.getWidth(),img.getHeight());

		return img;
	}

	public static void main(String[] args) {
		new Game();
	}

	Game(){
		setup();
	}

	//the setup of the game
	void setup() {
		//sets antialias to true, centers the screen, sets the title to "asteroids", enables mouse movements
		gc.setAntiAlias(true);
		gc.setLocationRelativeTo(null); //centers the screen
		gc.setTitle("Asteroids");
		gc.enableMouse();
		gc.setFont(headerFonts);

		//runs the starting screen
		startingScreen();
		while (true) {
			//respawns the asteroids at the start of the game
			respawnAsteroids();
			//runs the game
			run();
			//if the player as quit, exit the game
			if(quit) 
				break;
			//resets the score, clears the bullets and asteroids, resets the ship and lives
			score = 0;
			bullets.clear();
			asteroids.clear();
			s = new Ship(400, 400, 0);
			lives=3;
		}
	}

	//the method used the run the game
	void run() {
		while(true) {
			//draw the graphics
			draw();
			//add inertia to the game
			inertia();
			//move the ship using method from ship
			s.move();
			//moves the asteorids and bullets
			moveAsteroids();
			moveBullets();
			//checks if a bullet has hit an asteroid
			checkAsteroidBreak();
			//readjusts the center location of the ship
			readjustCenter();
			//gets and handles user key press
			handleKeyPress();
			//respawns the asteroids if the asteroids are all broken
			if(asteroids.size()==0) {
				respawnAsteroids();
			}
			//checks if the player has hit an asteroid and the user is not invincible
			if (System.currentTimeMillis()-start >= 3000 && shipCollision()){
				//if there are no more lives lost
				//ask the user to see if they want to play again
				if(lives==1) {
					int n = JOptionPane.showConfirmDialog(null, "Do you want to play again?", "Play Again?", JOptionPane.YES_NO_OPTION);
					//quits the game if the user has said yes
					if (n==1) {
						quit = true;
						gc.close();
					}
					break;
				}
				//if the user still has lives, respawn the ship
				else {
					respawnShip();
				}
			}
			gc.sleep(15);
		}
	}

	//method to respawn the ship
	void respawnShip() {
		//reduces lives by 1 and sets start time to current time to give user invincibility again
		lives--;
		start = System.currentTimeMillis();
	}

	//method for the start screen
	void startingScreen() {
		//sets the title to "Asteroids" and sets the background colour to black
		gc.setTitle("Asteroids");
		gc.setBackgroundColor(Color.BLACK);
		while(true) {
			//if the user has clicked space, the starting screen is exited
			if (gc.isKeyDown(32)) {
				break;			
			}
			synchronized(gc) {
				//draws the title and tells the instructions to start the game
				gc.clear();
				gc.setFont(headerFonts);
				gc.setColor(Color.WHITE);
				gc.drawString("ASTEROIDS", 200, 350);
				gc.setFont(textFont);
				gc.setColor(Color.WHITE);
				gc.drawString("PRESS 'SPACE' TO START...", 303, 400);
			}
			gc.sleep(15);
		}
	}

	//method to respawn the asteroids
	void respawnAsteroids() {
		//adds 4 asteroids with random positions to the asteroids list
		//also sets the direction of the asteroid to a random radian from 0 to pi
		for(int i = 0; i < 4; i++) {
			asteroids.add(new Asteroids(80, (int)(Math.random()*800), (int)(Math.random()*800), Math.random()*3.14));
		}
		//resets the start time to give the player invincibility
		start = System.currentTimeMillis();
	}

	//method to check if an asteroid has broke
	void checkAsteroidBreak() {
		//temporary list of asteroids to store the asteroids that are created from the broken asteroids
		ArrayList<Asteroids> tempAsteroids = new ArrayList();
		for(int i = bullets.size()-1; i >= 0; i--) {
			for(int j = asteroids.size()-1; j>=0; j--) {
				//iterates through all bullets and asteroids to check if a bullet has hit an asteroid
				if(bullets.get(i).rect.intersects(asteroids.get(j).asteroidHitbox)){
					//gets the asteroid that was hit
					Asteroids brokenAsteroid = asteroids.get(j);
					//removes the asteroid from the asteroids list
					asteroids.remove(j);
					//removes the bullet that hit the asteroid
					bullets.remove(i);
					//if the size of the asteroid is 40 or bigger, then it creates 2 smaller asteroids
					if(brokenAsteroid.size>=40) {
						//creates the smaller asteroids and gives it a random direction
						tempAsteroids.add(new Asteroids(brokenAsteroid.size/2, brokenAsteroid.x, brokenAsteroid.y, Math.random()*3.14));
						tempAsteroids.add(new Asteroids(brokenAsteroid.size/2, brokenAsteroid.x, brokenAsteroid.y, Math.random()*3.14));
					}
					//adds 20 to the score
					score+=20;
					//breaks out of the for loop because the bullet has been removed
					break;
				}
			}
		}
		//adds the smaller asteroids back into the asteroid list
		for(Asteroids A : tempAsteroids) {
			asteroids.add(A);
		}
	}

	//method to implement inertia in the game
	void inertia() {
		//if the time between the current time and the last moved time is greater than 45 milliseconds,
		//the rocket velocity will slowly decrease
		if(System.currentTimeMillis()-lastMove>45) {
			s.isAccelerating=false;
			if(s.vel>0) {
				s.vel-=0.07;
			}
			if(s.vel<0.1) {
				s.vel=0;
			}
		}
		//if the rocket is accelerating and the velocity is less than 8
		//the velocity will slowly increase
		if(s.isAccelerating&&s.vel<8) {
			s.vel+=0.05;
		}
	}

	//method to move the asteroids
	void moveAsteroids() {
		//loops through all the asteroids and moves them
		for(Asteroids A : asteroids) {
			A.move();
		}
	}

	//moves the bullets
	void moveBullets() {
		//loops through the bullets and moves them
		for(int i = bullets.size()-1; i >= 0; i--) {
			Bullet B = bullets.get(i);
			B.move();

			//if the bullet goes off the edge, remove it from the bullet list
			if(B.x < 0) {
				bullets.remove(i);
				continue;
			}
			if(B.y < 0) {
				bullets.remove(i);
				continue;
			}
			if(B.x >= 800) {
				bullets.remove(i);
				continue;
			}
			if(B.y >= 800) {
				bullets.remove(i);
				continue;
			}
		}
	}
	//code to readjust the center coordinate of the ship
	void readjustCenter() {
		//sets the cx and cy to a point 30 pixels away from the tip of the ship in the center of the ship
		double dx = 30*Math.sin(s.angle);
		double dy = 30*Math.cos(s.angle);
		s.cx = s.xx-dx;
		s.cy = s.yy+dy;
	}
	//method to handle the key press
	void handleKeyPress() {

		//if space is pressed
		if(gc.isKeyDown(32)) {
			//adds bullets to the bullet list
			if(bullets.size()==0) {
				bullets.add(new Bullet(s));
			}
			else {
				//if the time between the last bullet shot and the current time is greater than 200,
				//add the bullet to the bullet list
				long lastShot = bullets.get(bullets.size()-1).lastShotTime;
				if(System.currentTimeMillis()-lastShot>200) {
					bullets.add(new Bullet(s));
				}
			}
		}

		//if left is pressed subtract 0.07 radians from the angle
		if(gc.isKeyDown(37)) {
			s.angle-=0.07;
		}
		//if right is pressed add 0.07 radians to the angle
		if(gc.isKeyDown(39)) {
			s.angle+=0.07;
		}
		//if the up arrow is pressed
		if(gc.isKeyDown(38)) {
			//sets accelerating to true, moves the ship and resets the last move time
			s.isAccelerating = true;
			s.move();
			lastMove = System.currentTimeMillis();
		}
	}

	//checks if the ship has collided with an asteroid
	boolean shipCollision() {
		for(Asteroids A : asteroids) {
			//iterates through the asteroids and checks if they collide using
			//the distance between the center of the asteroids and the ship
			double distanceT = Math.sqrt(Math.pow(s.cx - A.x, 2) + Math.pow(s.cy - A.y, 2));
			if(distanceT < A.size/2 + 10) {
				return true;
			}
		}
		return false;
	}

	//method to draw the game
	void draw() {
		synchronized(gc) {
			gc.clear();
			//sets the background image
			gc.drawImage(backImg, 0, 0, 800, 800, 400, 0, 1200, 800, null);
			//rotates the screen at the point of the ship by the rotation angle of the ship
			gc.setRotation((int)(180*s.angle/3.14159), s.x, s.y);
			//if the time from the ship and start is greater than 3 seconds
			if(System.currentTimeMillis()-start >= 3000) {
				//draws the ship image if the time between the last move time and now is greater than 50 milliseconds
				if(System.currentTimeMillis() - lastMove>50) {
					gc.drawImage(shipImg, s.x-25, s.y);
				}
				//flickers between the ship image and the ship with the fire if the ship is currently moving
				else if((System.currentTimeMillis() - lastMove)%5<2){
					gc.drawImage(shipImg, s.x-25, s.y);
				}
				else {
					gc.drawImage(shipImgFire, s.x-25, s.y);
				}
			}
			//if the ship is invincible
			else {
				//flickers the image every 100 milliseconds
				if(System.currentTimeMillis()%200 < 100) {
					//draws the ship image if the ship is not moving
					if(System.currentTimeMillis() - lastMove>50) {
						gc.drawImage(shipImg, s.x-25, s.y);
					}
					//flickers between the ship image and the ship with the fire if the ship is currently moving
					else if((System.currentTimeMillis() - lastMove)%5<2){
						gc.drawImage(shipImg, s.x-25, s.y);
					}
					else {
						gc.drawImage(shipImgFire, s.x-25, s.y);
					}
				}
			}
			//resets the rotation degree
			gc.setRotation(0, s.x, s.y);
			//draws the asteroids and bullets
			gc.setColor(Color.WHITE);
			for(Asteroids A : asteroids) {
				gc.setStroke(3);
				gc.drawOval(A.x-A.size/2, A.y-A.size/2, A.size, A.size);
			}
			for(Bullet B : bullets) {
				gc.fillRect(B.rect.x, B.rect.y, B.rect.width, B.rect.height);
			}
			//draws the score and lives
			gc.setColor(Color.WHITE);
			gc.setFont(scoreFont);
			gc.drawString(Integer.toString(score), 50, 75);
			for(int i = 0; i < lives; i++) {
				gc.drawImage(shipLifeImg, 20*i + 52	, 90);
			}
		}
	}
}
