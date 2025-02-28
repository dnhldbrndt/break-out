package game;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.Timer;
import javax.swing.JPanel;

public class Board extends JPanel{

	private Timer timer;
	private String message = "Game Over";
	private Ball ball;
	private Paddle paddle;
	private Brick[] bricks;
	private boolean inGame = true;

	public Board() {
		initBoard();
	}
	private void initBoard() {
		addKeyListener(new TAdapter());
		setFocusable(true);
		setPreferredSize(new Dimension(Commons.WIDTH, Commons.HEIGHT));
		
		gameInit();
	}
	private void gameInit() {
		bricks = new Brick[Commons.N_OF_BRICKS];
		
		ball = new Ball();
		paddle = new Paddle();
		int k = 0;
		for (int i = 0; i<5; i++) {
			for (int j = 0; j<6; j++) {
				bricks[k] = new Brick(j*45+30, i*15+50);
				System.out.println("k = " + k);
				System.out.println(bricks[k].x + " " + bricks[k].y);
				System.out.println(bricks[k].isDestroyed());
				k++;

			}
		}
		timer = new Timer(Commons.PERIOD, new GameCycle());
		timer.start();
	}
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		var g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		
		if (inGame) {
			drawObjects(g2d);
			
		}else {
			gameFinished(g2d);
		}
		
		Toolkit.getDefaultToolkit().sync();
	}
	private void drawObjects(Graphics2D g2d) {
		g2d.drawImage(ball.getImage(), ball.getX(), ball.getY(), ball.getImageWidth(), ball.getImageHeight(),this);
		g2d.drawImage(paddle.getImage(), paddle.getX(), paddle.getY(), paddle.getImageWidth(), paddle.getImageHeight(),this);
		
		for (int i = 0; i < Commons.N_OF_BRICKS; i++) {
			if (!bricks[i].isDestroyed()) {
				g2d.drawImage(bricks[i].getImage(), bricks[i].getX(), bricks[i].getY(), bricks[i].getImageWidth(), bricks[i].getImageHeight(), this);
			}
		}
	}
	private void gameFinished(Graphics2D g2d) {
		var font = new Font("Verdana", Font.BOLD, 18);
		FontMetrics fontMetrics = this.getFontMetrics(font);
		
		g2d.setColor(Color.black);
		g2d.setFont(font);
		g2d.drawString(message, (Commons.WIDTH - fontMetrics.stringWidth(message)) / 2, Commons.WIDTH /2);
	}

	private class TAdapter extends KeyAdapter {
		public void keyReleased(KeyEvent e) {
			paddle.keyReleased(e);
		}
		public void keyPressed(KeyEvent e) {
			paddle.keyPressed(e);
		}
	}
	
	private class GameCycle implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			doGameCycle();
		}
	}
	
	private void doGameCycle() {
		ball.move();
		paddle.move();
		checkCollision();
		repaint();
	}
	public void stopGame() {
		inGame = false;
		timer.stop();
	}
	public void checkCollision() {
		if (ball.getRect().getMaxY()>Commons.BOTTOM_EDGE) {
			stopGame();
		}
		for (int i = 0, j = 0; i < Commons.N_OF_BRICKS; i++) {
			if (bricks[i].isDestroyed()) {
				j++;
			}
			if (j == Commons.N_OF_BRICKS) {
				message = "congratulations!";
				stopGame();
			}
		}
		if ((ball.getRect()).intersects(paddle.getRect())) {
			int paddlePos = (int) paddle.getRect().getMinX();
			int ballPos = (int) ball.getRect().getMinX();
			
			int first = paddlePos + 8;
			int second = paddlePos + 16;
			int third = paddlePos + 24;
			int fourth = paddlePos + 32;
			
			if (ballPos < first) {
				ball.setXDir(-1);
				ball.setYDir(-1);
				
			}
			if (ballPos >= first && ballPos < second) {
				ball.setXDir(-1);
				ball.setYDir(-1*ball.getYDir());
			}
			if (ballPos >= second && ballPos < third) {
				ball.setXDir(0);
				ball.setYDir(-1);
			}
			if (ballPos >= third && ballPos < fourth) {
				ball.setXDir(-1);
				ball.setYDir(-1 * ball.getYDir()) ;
			}
			if (ballPos > fourth) {
				ball.setXDir(1);
				ball.setYDir(-1);
			}
		}
		
		for (int i = 0; i < Commons.N_OF_BRICKS; i++) {
			if ((ball.getRect().intersects(bricks[i].getRect()))) {
				int ballLeft = (int) ball.getRect().getMinX();
				int ballHeight = (int) ball.getRect().getHeight();
				int ballWidth = (int) ball.getRect().getWidth();
				int ballTop = (int) ball.getRect().getMinY();
				
				var pointRight = new Point(ballLeft + ballWidth + 1, ballTop);
				var pointLeft = new Point (ballLeft -1, ballTop);
				var pointTop = new Point(ballLeft, ballTop-1);
				var pointBottom = new Point(ballLeft, ballTop+ballHeight +1);
				
				if (!bricks[i].isDestroyed()) {
					if(bricks[i].getRect().contains(pointRight)) {
						ball.setXDir(-1);
					}else if (bricks[i].getRect().contains(pointLeft)) {
						ball.setXDir(1);
					}
					if(bricks[i].getRect().contains(pointTop)) {
						ball.setYDir(1);
					} else if (bricks[i].getRect().contains(pointBottom)) {
						ball.setYDir(-1);
					}
					bricks[i].setDestroyed(true);
				}
			}
		}
	}
}

