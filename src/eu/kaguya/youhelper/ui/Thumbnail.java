package eu.kaguya.youhelper.ui;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.EtchedBorder;

public class Thumbnail extends JPanel {

	private static final long serialVersionUID = -7271940129761551558L;
	private BufferedImage bg, bgEmpty, play;
	private float ratio = 1.33f;
	private boolean loading;

	private boolean expand = true;
	private boolean clicked = false;

	private Timer t;
	private float progress = 0f;

	private int duration = -1;
	private int likes = -1;
	private int dislikes = -1;

	// constructor

	public Thumbnail() {
		setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
		setOpaque(false);
		setFocusable(true);

		// simple animation
		// TODO: framework?
		t = new Timer(10, a -> {
			progress = Math.min(1f, Math.max(0, progress + (expand ? 0.05f : -0.05f)));
			if (expand && progress >= 1f) {
				t.stop();
			}
			if (!expand && progress <= 0f) {
				t.stop();
			}
			repaint();
		});
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseExited(MouseEvent e) {
				expand = false;
				t.start();
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				expand = true;
				t.start();
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				clicked = false;
				t.start();
			}

			@Override
			public void mousePressed(MouseEvent e) {
				grabFocus();
				if (e.getButton() == MouseEvent.BUTTON1) {
					clicked = true;
					t.start();
				}
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					thumbnailAction();
				}
			}
		});
		addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent e) {
				setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
				expand = false;
				t.start();
			}

			@Override
			public void focusGained(FocusEvent e) {
				setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
				expand = true;
				t.start();
			}
		});
		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (KeyEvent.VK_ENTER == e.getKeyCode()) {
					clicked = true;
					t.start();
					thumbnailAction();
					new Timer(50, (a) -> {
						clicked = false;
						t.start();
						((Timer) a.getSource()).stop();
					}).start();
				}
			}
		});

		setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	}

	// action

	private void thumbnailAction() {
		//TODO
		getToolkit().beep();
	}

	// correct dimensions

	@Override
	public Dimension getMinimumSize() {
		int biggestAffordableHeight = Math.max(50, getHeight());
		return new Dimension((int) (biggestAffordableHeight * ratio), biggestAffordableHeight);
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(200, 150);
	}

	// paint logic

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (bgEmpty == null) {
			bgEmpty = loadImage("/no_thumbnail.jpg");
		}
		if (play == null) {
			play = loadImage("/play-button.png");
		}

		if (g instanceof Graphics2D) {
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
			g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
			g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		}

		// draw background image
		if (bg == null) {
			g.setColor(new Color(204, 204, 204));
			g.fillRect(0, 0, getWidth(), getHeight());
			g.drawImage(bgEmpty, (getWidth() - bgEmpty.getWidth()) / 2, (getHeight() - bgEmpty.getHeight()) / 2, null);
		} else {
			g.drawImage(bg, 0, 0, getWidth(), getHeight(), null);
		}

		// draw focused box
		g.setColor(clicked ? Color.DARK_GRAY : Color.BLACK);
		if (g instanceof Graphics2D) {
			Graphics2D g2 = (Graphics2D) g;
			Composite c = g2.getComposite();
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f));

			int playBgWidth = (int) (progress * getWidth());
			int playBgHeight = (int) (progress * getHeight());

			g2.fillRect((getWidth() - playBgWidth) / 2, (getHeight() - playBgHeight) / 2, playBgWidth, playBgHeight);
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f * Math.min(1f, progress * 1.5f)));
			
			// draw play button
			int clickedMargin = clicked ? -10 : 0;
			g2.drawImage(play, (getWidth() - play.getWidth()) / 2 + clickedMargin / 2, (getHeight() - play.getHeight()) / 2 + clickedMargin / 2,
					play.getWidth() - clickedMargin, play.getHeight() - clickedMargin, null);
			g2.setComposite(c);
		}
		
		// draw duration box
		if (this.duration > -1) {
			String duration = formattedDuration(this.duration);

			Font font = new Font("SansSerif", Font.PLAIN, 11);
			FontMetrics fm = g.getFontMetrics(font);
			int sWidth = fm.stringWidth(duration) + 4;
			int sHeight = fm.getAscent();

			g.setColor(Color.black);
			if (g instanceof Graphics2D) {
				Graphics2D g2 = (Graphics2D) g;
				Composite c = g2.getComposite();
				g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, Math.max(0.3f, Math.min(1f, progress * 1.5f))));
				g2.fillRoundRect(getWidth() - sWidth - 3, getHeight() - sHeight * 1 - 4 - 2, sWidth + 4, sHeight + 4, 7, 20);
				g2.setComposite(c);
			}

			// draw duration string
			g.setColor(Color.white);
			g.setFont(font);
			g.drawString(duration, getWidth() - sWidth, getHeight() - 5 - 2);
		}
		
		// draw like/dislike bar
		if (this.likes > -1 && this.dislikes > -1) {
			float likes = this.likes;
			float dislikes = this.dislikes;
			int barHeight = 3;

			g.setColor(Color.GREEN);
			int totalLikeBarWidth = getWidth();
			int likeWidth = (int) (totalLikeBarWidth * (likes / (likes + dislikes)));
			g.fillRect(0, getHeight() - barHeight, likeWidth, barHeight);
			g.setColor(Color.RED);
			int dislikeWidth = (int) (totalLikeBarWidth * (dislikes / (likes + dislikes)));
			g.fillRect(likeWidth, getHeight() - barHeight, dislikeWidth, barHeight);
			g.setColor(Color.DARK_GRAY);
			g.fillRect(likeWidth + dislikeWidth, getHeight() - barHeight, totalLikeBarWidth - likeWidth - dislikeWidth, barHeight);
		}

		// draw loading box
		if (this.loading) {
			String loading = "loading";

			Font font = new Font("SansSerif", Font.PLAIN, 11);
			FontMetrics fm = g.getFontMetrics(font);
			int sWidth = fm.stringWidth(loading) + 4;
			int sHeight = fm.getAscent();

			g.setColor(Color.black);
			if (g instanceof Graphics2D) {
				Graphics2D g2 = (Graphics2D) g;
				Composite c = g2.getComposite();
				g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f));
				g2.fillRoundRect(3, 3, sWidth + 4, sHeight + 4, 7, 20);
				g2.setComposite(c);
			}

			// draw loading string
			g.setColor(Color.white);
			g.setFont(font);
			g.drawString(loading, 3 + 4, sHeight + 3);
		}
	}

	// helper methods

	private String formattedDuration(int totalSecs) {
		int hours = totalSecs / 3600;
		int minutes = (totalSecs % 3600) / 60;
		int seconds = totalSecs % 60;

		return String.format("%02d:%02d:%02d", hours, minutes, seconds);
	}

	private BufferedImage loadImage(String url) {
		try {
			BufferedImage img = ImageIO.read(Thumbnail.class.getResource(url));
			if (img != null) {
				BufferedImage newImage = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
				Graphics2D g = newImage.createGraphics();
				g.drawImage(img, 0, 0, null);
				g.dispose();
				return newImage;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	// public API

	public void fetchingStarted() {
		this.loading = true;
		repaint();
	}

	public void fetchingEnded() {
		this.loading = false;
		repaint();
	}

	public void load(BufferedImage thumbnailImage) {
		if (SwingUtilities.isEventDispatchThread()){
			this.bg = thumbnailImage;
			repaint();
		}
		else SwingUtilities.invokeLater(() -> { this.bg = thumbnailImage; repaint(); });
	}

	public boolean isThumbnailLoaded() {
		return this.bg != null;
	}

	public boolean isThumbnailLoadedOrLoading() {
		return this.isThumbnailLoaded() || this.loading;
	}

	public void setDuration(Integer duration, TimeUnit unit) {
		this.duration = duration == null ? -1 : (int) unit.toSeconds(duration);
		repaint();
	}

	public void removeDuration() {
		this.duration = -1;
	}

	public void setLikesDislikes(Integer likes, Integer dislikes) {
		this.likes = likes == null ? -1 : likes;
		this.dislikes = dislikes == null ? -1 : dislikes;
		repaint();
	}

	public void removeLikesDislikes() {
		this.likes = -1;
		this.dislikes = -1;
		repaint();
	}
}
