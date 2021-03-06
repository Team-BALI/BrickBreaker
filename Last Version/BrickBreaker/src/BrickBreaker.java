import java.awt.*; //image
import java.io.*; //audio
import java.net.URL; //audio
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.sound.sampled.*; //audio
import javax.swing.*; //audio

public class BrickBreaker extends JPanel implements KeyListener, ActionListener, Runnable {
	public static JFrame frame;

	// movement keys..
	static boolean right = false;
	static boolean left = false;

	// variables declaration for ball
	int ballx = 160;
	int bally = 218;

	// variables declaration for bat
	int batx = 160;
	int baty = 245 + 35;
	
	// variables declaration for brick
	int brickx = 70;
	int bricky = 50;
	
	// declaring ball, paddle, bricks
	Rectangle Ball = new Rectangle(ballx, bally, 5, 5);
	Rectangle Bat = new Rectangle(batx, baty, 50, 5);
	Rectangle[] Brick = new Rectangle[12];
	Thread t;

	BrickBreaker() {
		addKeyListener(this);
		setFocusable(true);
		t = new Thread(this);
		t.start();

		try {
			// Open an audio input stream.
			URL url = this.getClass().getClassLoader().getResource("Kitaro.wav");
			AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);

			// Get a sound clip resource.
			Clip clip = AudioSystem.getClip();

			// Open audio clip and load samples from the audio input stream.
			clip.open(audioIn);
			clip.loop(Clip.LOOP_CONTINUOUSLY); // Audio Clip won't loop continuously

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
		frame =  new JFrame();
		BrickBreaker game = new BrickBreaker();
		JButton button = new JButton("Restart");
		frame.setSize(350, 350);
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

		// background image
		File imgfile = new File("src//bricksbg.png");
		try {
			BufferedImage img = ImageIO.read(imgfile);
			g.drawImage(img, 0, 0, 350, 450, null);
		} catch (IOException e) {
			e.printStackTrace();
		}

		imgfile = new File("src//ball.png");
		try {
			BufferedImage ball = ImageIO.read(imgfile);
			g.drawImage(ball, Ball.x, Ball.y, Ball.width, Ball.height, null);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// bat image:
		imgfile = new File("src//bat.png");

		try {
			BufferedImage bat = ImageIO.read(imgfile);
			g.drawImage(bat, Bat.x, Bat.y, Bat.width, Bat.height, null);
		} catch (IOException e) {
			e.printStackTrace();
		}

		g.setColor(Color.white);
		g.fillRect(0, 0, 450, 50);
		g.setColor(Color.gray);
		g.drawRect(0, 50, 343, 250);

		for (int i = 0; i < Brick.length; i++) {
			if (Brick[i] != null) {
				imgfile = new File("src//brick.png");
				try {
					BufferedImage brick = ImageIO.read(imgfile);
					g.drawImage(brick, Brick[i].x, Brick[i].y, Brick[i].width,
							Brick[i].height, null);
				} catch (IOException e) {
					e.printStackTrace();
				}

			}

			// set scorre
			Font score = new Font("Arial", Font.BOLD, 20);
			g.setColor(Color.darkGray);
			g.setFont(score);
			g.drawString(statusScore, 10, 30);
		}

		if (ballFallDown == true || bricksOver == true) {

			Font f = new Font("Arial", Font.BOLD, 20);
			g.setColor(Color.orange);
			g.setFont(f);
			g.drawString(status, 70, 120);
			ballFallDown = false;
			bricksOver = false;
		}
	}

	//Game Loop:

	//When ball strikes borders, it reverses ==>
	int movex = -1;
	int movey = -1;
	int pause = 0;
	boolean ballFallDown = false;
	boolean bricksOver = false;
	int count = 0;
	int playState = 1;
	String status;

	// String lives;
	String statusScore;

	public void run() {

		//Creating bricks for the game:
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

		//BRICKS created for the game new ready to use

		//ball reverses when touches the brick

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
					}
				}
			}
			
			// draw score
			if (count >= 0 || count < 0) {
				statusScore = "Score: " + count;
				// lives = "Lives: " + playLives;
			}

			// check if ball hits all bricks Brick.length
			if (count == Brick.length * 3) {
				bricksOver = true;
				status = "YOU WON THE GAME";
				playState = 0;
				repaint();
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

			// Ball reverses when strikes the bat
			if (Ball.intersects(Bat)) {
				movey = -movey;
				bataStrike();
			}

			// Ball reverses when touches left and right boundary
			if (Ball.x <= 0 || Ball.x + Ball.height >= 343) {
				movex = -movex;
				strike();
			}

			if (Ball.y <= 45) {
				movey = -movey;
				strike();
			}

			// when ball falls below bat game is over
			if (Ball.y > 340) {
				ballFallDown = true;
				status = "YOU LOST THE GAME";
				playState = 0;
				repaint();
			}

			try {
				Thread.sleep(8);
			} catch (Exception ex) {
			}

		}
	}

	// HANDLING KEY EVENTS
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
		frame.dispose();
		main(null);
		if (str.equals("Restart")) {
			playState = 1;
			restart();
		}
	}

	// add audio when strike brick:
	public void bataStrike() { 

		try {
			// Open an audio input stream.
			URL url = this.getClass().getClassLoader().getResource("bataStrike.wav");
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

	// add audio when trike brik:
	public void brickStrike() { 

		try {
			// Open an audio input stream.
			URL url = this.getClass().getClassLoader().getResource("brickStrike.wav");
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

	// add audio when trike wall:
	public void strike() { 

		try {
			// Open an audio input stream.
			URL url = this.getClass().getClassLoader().getResource("strike.wav");
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

		requestFocus(true);

		// variables declaration for ball
		ballx = 160;
		bally = 218;

		// variables declaration for bat
		batx = 160;
		baty = 245 + 35;

		// variables declaration for brick
		brickx = 70;
		bricky = 50;

		// declaring ball, paddle,bricks
		Ball = new Rectangle(ballx, bally, 5, 5);
		Bat = new Rectangle(batx, baty, 50, 5);
		Brick = new Rectangle[12];

		movex = -1;
		movey = -1;
		ballFallDown = false;
		bricksOver = false;
		status = null;

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
	}
}