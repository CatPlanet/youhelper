package eu.kaguya.youhelper.ui;

import java.awt.Font;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.swing.JLabel;

public class AnimatedLabel extends JLabel {

	private static final long serialVersionUID = 1763333861507761595L;

	private String text;
	private int dots;
//	private int i;
	private String symbol;
//	private boolean direction = true;
	
	public AnimatedLabel(){
		this(null, 4, "\u00B7");
	}

	public AnimatedLabel(String text, int dots, String symbol) {
		super();
		setFont(Font.decode("Monospaced 12"));
		
		this.text = text;
		this.dots = dots;
//		this.i = this.dots;
		this.symbol = symbol;
		
		setText(getPreDots(this.dots) + text + getAfterDots(this.dots));

//		Timer t = new Timer(500, new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				if(text != null){
//					synchronized (AnimatedLabel.class) {
//						setText(getPreDots(i) + getAnimatedText() + getAfterDots(i));
//						if (direction && i++ >= dots) direction = false;
//						if (!direction && i-- <= -1) {
//							direction = true;
//							i++;
//						}
//					}
//				}
//			}
//		});
		//TODO
		//t.start();
	}
	
	public void animateText(String string){
		synchronized (AnimatedLabel.class) {
			this.text = string;
//			this.i = this.dots;
//			this.direction = true;
			
			setText(getPreDots(this.dots) + text);
		}
	}
	
//	private String getAnimatedText(){
//		return this.text;
//	}

	private String getPreDots(int i) {
		i = i % (dots + 1) + 1;
		return IntStream.range(0, i).mapToObj(a -> symbol).collect(Collectors.joining());
	}

	private String getAfterDots(int i) {
		i = i % (dots + 1);
		i = dots - i;
		return IntStream.range(0, i).mapToObj(a -> symbol).collect(Collectors.joining());
	}
	
	@Override
	public boolean isVisible(){
		return this.text != null && !this.text.isEmpty() && super.isVisible();
	}
}
