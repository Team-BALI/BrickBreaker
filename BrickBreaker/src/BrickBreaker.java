import java.awt.*;//image
import java.io.*;//audio
import java.net.URL;//audio
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.sound.sampled.*;//audio
import javax.swing.*;//audio

public class BrickBreaker extends JPanel implements KeyListener,
		ActionListener, Runnable {

	// movement keys..
	static boolean right = false;
	static boolean left = false;

	// variables declaration for ball.................................
	int ballx = 160;
	int bally = 218;
	// variables declaration for bat..................................
	int batx = 160;
	int baty = 245 + 35;
	// variables declaration for brick...............................
	int brickx = 70;
	int bricky = 50;
	// declaring ball, paddle,bricks
	Rectangle Ball = new Rectangle(ballx, bally, 6, 6);
	Rectangle Bat = new Rectangle(batx, baty, 50, 5);
	// Rectangle Brick;// = new Rectangle(brickx, bricky, 30, 10);
	Rectangle[] Brick = new Rectangle[12];
	Thread t;

	BrickBreaker() {
		addKeyListener(this);
		setFocusable(true);
		t = new Thread(this);
		t.start();

		try {
			// Open an audio input stream.
			URL url = this.getClass().getClassLoader()
					.getResource("Kitaro.wav");
			AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
			// Get a sound clip resource.
			Clip clip = AudioSystem.getClip();
			// Open audio clip and load samples from the audio input stream.
			clip.open(audioIn);
			clip.loop(Clip.LOOP_CONTINUOUSLY);// Audio Clip won't loop
												// continuously
			// clip.start();
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame();
		BrickBreaker game = new BrickBreaker();
		JButton button = new JButton("restart");
		frame.setSize(344, 315);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.add(game);
		frame.add(button, BorderLayout.SOUTH);
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		frame.setVisible(true);
		button.addActionListener(game);
	}

	// declaring ball, paddle,bricks
	public void paint(Graphics g) {
		Image golf = Toolkit.getDefaultToolkit().getImage("src//golf.jpg"); // add
																			// image
																			// as
																			// background
		g.drawImage(golf, 0, 50, this);
		g.setColor(Color.blue);
		g.fillOval(Ball.x, Ball.y, Ball.width, Ball.height);
		g.setColor(Color.green);
		g.fill3DRect(Bat.x, Bat.y, Bat.width, Bat.height, true);
		g.setColor(Color.white);
		g.fillRect(0, 0, 450, 50);
		g.setColor(Color.gray);
		g.drawRect(0, 50, 343, 250);
		for (int i = 0; i < Brick.length; i++) {
			g.setColor(Color.red);
			if (Brick[i] != null) {
				// g.drawImage(golf, 0, 0, this);
				g.fill3DRect(Brick[i].x, Brick[i].y, Brick[i].width,
						Brick[i].height, true);
			}
			// ===================================set scorre
			Font score = new Font("Arial", Font.BOLD, 20);// SoftUni
			g.setColor(Color.darkGray);
			g.setFont(score);// SoftUni
			g.drawString(statusScore, 10, 30);// SoftUni

			// ===================================set lives
			Font life = new Font("Arial", Font.BOLD, 20);// SoftUni
			g.setColor(Color.green);
			g.setFont(life);// SoftUni
			g.drawString(lives, 260, 30);// SoftUni
			// ===================================
		}

		if (ballFallDown == true || bricksOver == true) {
			Font f = new Font("Arial", Font.BOLD, 20);
			g.setFont(f);
			g.drawString(status, 70, 120);
			ballFallDown = false;
			bricksOver = false;
		}
	}

	// /...Game Loop...................

	// /////////////////// When ball strikes borders......... it
	// reverses......==>
	int movex = -1;
	int movey = -1;
	int pause = 0;
	boolean ballFallDown = false;
	boolean bricksOver = false;
	int count = 0;
	int playLives = 3;
	int playState = 1;
	String status;
	String lives;
	String statusScore; // SoftUni

	public void run() {

		// //////////// =====Creating bricks for the game===>.....
		for (int i = 0; i < Brick.length; i++) {
			Brick[i] = new Rectangle(brickx, bricky + 40, 30, 10);
			if (i == 5) {
				brickx = 70;
				bricky = 62;
			}
			if (i == 9) {
				brickx = 100;
				bricky = 74;
			}
			brickx += 31;
		}
		// ===========BRICKS created for the game new ready to use===

		// == ball reverses when touches the brick=======

		while (ballFallDown == false && bricksOver == false) {
			// if(gameOver == true){return;}
			for (int i = 0; i < Brick.length; i++) {
				if (Brick[i] != null) {
					if (Brick[i].intersects(Ball)) {
						Brick[i] = null;
						// movex = -movex;
						movey = -movey;
						count++;
						brickStrike();
					}// end of 2nd if..
				}// end of 1st if..
			}// end of for loop..
				// =========================draw score
			if (count >= 0 || count < 0) { // SoftUni
				statusScore = "Score " + count;
				lives = "Lives " + playLives;
			}

			if (count == Brick.length) {// check if ball hits all bricks
				bricksOver = true;
				status = "YOU WON THE GAME";
				repaint();
//				playState = 0;
			}

			repaint();
			if (playState == 1) {
				Ball.x += movex;
				Ball.y += movey;
			}

			if (left == true) {

				Bat.x -= 5;
				right = false;
			}
			if (right == true) {
				Bat.x += 5;
				left = false;
			}
			if (Bat.x <= 4) {
				Bat.x = 4;
			} else if (Bat.x >= 298) {
				Bat.x = 298;
			}
			// ..... Ball reverses when strikes the bat
			if (Ball.intersects(Bat)) {
				movey = -movey;
				// if(Ball.y + Ball.width >=Bat.y)
				bataStrike();
			}
			// ....Ball reverses when touches left and right boundary
			if (Ball.x <= 0 || Ball.x + Ball.height >= 343) {
				movex = -movex;
				strike();
			}// if ends here
			if (Ball.y <= 45) {// ////////////////|| bally + Ball.height >= 250
				movey = -movey;
				strike();
			}// if ends here.....

			if (Ball.y >= 340) {// when ball falls below bat game is over...
				ballFallDown = true;
				status = "YOU LOST THE GAME";
				playState = 0;
				repaint();
				
			}
			try {
				Thread.sleep(8);
			} catch (Exception ex) {
			}// try catch ends here

		}// while loop ends here
	}

	// loop ends here

	// ///////..... HANDLING KEY EVENTS................//
	@Override
	public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();
		if ((keyCode == KeyEvent.VK_LEFT) && (pause != 1)) {
			left = true;
		}

		if ((keyCode == KeyEvent.VK_RIGHT) && (pause != 1)) {
			right = true;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		int keyCode = e.getKeyCode();
		if (keyCode == KeyEvent.VK_LEFT) {
			left = false;
		}

		if (keyCode == KeyEvent.VK_RIGHT) {
			right = false;
		}

		if (keyCode == KeyEvent.VK_SPACE) {
			if (pause == 0) {
				playState = 0;
				pause = 1;
			} else {
				playState = 1;
				pause = 0;
			}
		}
	}

	@Override
	public void keyTyped(KeyEvent arg0) {

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String str = e.getActionCommand();
		if (str.equals("restart")) {
			playState = 1;
			this.restart();
			// this.paint0();
		}
	}

	public void bataStrike() { // add audio when strike brick

		try {
			// Open an audio input stream.
			URL url = this.getClass().getClassLoader()
					.getResource("bataStrike.wav");
			AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
			// Get a sound clip resource.
			Clip clip = AudioSystem.getClip();
			// Open audio clip and load samples from the audio input stream.
			clip.open(audioIn);
			clip.start();
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
	}

	public void brickStrike() { // add audio when trike brik

		try {
			// Open an audio input stream.
			URL url = this.getClass().getClassLoader()
					.getResource("brickStrike.wav");
			AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
			// Get a sound clip resource.
			Clip clip = AudioSystem.getClip();
			// Open audio clip and load samples from the audio input stream.
			clip.open(audioIn);
			clip.start();
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
	}

	public void strike() { // add audio when trike wall

		try {
			// Open an audio input stream.
			URL url = this.getClass().getClassLoader()
					.getResource("strike.wav");
			AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
			// Get a sound clip resource.
			Clip clip = AudioSystem.getClip();
			// Open audio clip and load samples from the audio input stream.
			clip.open(audioIn);
			clip.start();
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
	}

	public void restart() {
		playLives--;
		if (playLives == 0) {
			count = 0;
			playLives = 3;
		}
		requestFocus(true);
		// variables declaration for ball.................................
		ballx = 160;
		bally = 218;
		// variables declaration for bat..................................
		batx = 160;
		baty = 245 + 35;
		// variables declaration for brick...............................
		brickx = 70;
		bricky = 50;
		// declaring ball, paddle,bricks
		Ball = new Rectangle(ballx, bally, 6, 6);
		Bat = new Rectangle(batx, baty, 50, 5);
		// Rectangle Brick;// = new Rectangle(brickx, bricky, 30, 10);
		Brick = new Rectangle[12];

		movex = -1;
		movey = -1;
		ballFallDown = false;
		bricksOver = false;
		status = null;
		// //////////// =====Creating bricks for the game===>.....
		/*
		 * creating bricks again because this for loop is out of while loop in
		 * run method
		 */
		for (int i = 0; i < Brick.length; i++) {
			Brick[i] = new Rectangle(brickx, bricky + 40, 30, 10);
			if (i == 5) {
				brickx = 70;
				bricky = 62;
			}
			if (i == 9) {
				brickx = 100;
				bricky = 74;
			}
			brickx += 31;
		}
		// repaint();
	}
}