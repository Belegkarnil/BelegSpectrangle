/*
 *  Copyright 2025 Belegkarnil
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 *  associated documentation files (the “Software”), to deal in the Software without restriction,
 *  including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 *  and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do
 *  so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all copies or substantial
 *  portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 *  FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS
 *  OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 *  WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 *  CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package be.belegkarnil.game.board.spectrangle.gui;

import be.belegkarnil.game.board.spectrangle.Board;
import be.belegkarnil.game.board.spectrangle.Piece;

import java.awt.*;
import java.awt.geom.Ellipse2D;

/**
 * This class is an utility class for GUI component of the Game. It draw triangle shape (i.e. {@link Piece}).
 *
 * @author Belegkarnil
 */
public class TriangleCellDrawer implements Cloneable{
	public static double EQUILATERAL_TRIANGLE_HEIGHT_WIDTH_FACTOR = Math.sqrt(3.0) / 2.0;
	public static final int NO_VALUE = 0;
	public static final Color BORDER_COLOR = Color.BLACK;
	public static final double CIRCLE_WIDTH = 20;
	public static final double CIRCLE_BORDER_THICKNESS = 4;

	private Point top, left, right, center;
	private int bonus, value;
	private Color horizontalColor, leftColor, rightColor;
	private boolean reversed;
	private Color overlay;

	public TriangleCellDrawer(){
		this(Board.NO_BONUS);
	}

	public TriangleCellDrawer(int bonus){
		this.bonus = bonus;
		this.value = NO_VALUE;
		this.reversed = false;
		this.horizontalColor = null;
		this.leftColor = null;
		this.rightColor = null;
		this.overlay = null;
		this.top = new Point();
		this.left = new Point();
		this.right = new Point();
		this.center = new Point();
	}

	public void setReversed(boolean reversed){
		this.reversed = reversed;
	}

	public boolean isReversed(){
		return this.reversed;
	}

	public void setBonus(int bonus){
		this.bonus = bonus;
	}

	public int getBonus(){
		return this.bonus;
	}

	public void setPiece(Piece piece, int rotation){ // TODO rotation
		if(rotation == 0){
			setColor(piece.colorBottom, piece.colorLeft, piece.colorRight);
		}else if(rotation == 1){
			setColor(piece.colorRight, piece.colorBottom, piece.colorLeft);
		}else{
			setColor(piece.colorLeft, piece.colorRight, piece.colorBottom);
		}
		setValue(piece.value);
	}

	public void setColor(Color horizontalColor, Color leftColor, Color rightColor){
		this.horizontalColor = horizontalColor;
		this.leftColor = leftColor;
		this.rightColor = rightColor;
	}

	public void setOverlay(Color overlay){
		if(overlay == null){
			this.overlay = null;
			return;
		}
		this.overlay = new Color(overlay.getRed(), overlay.getGreen(), overlay.getBlue(), 32);
	}

	public void setValue(int value){
		this.value = value;
	}

	public Shape getBorder(){
		return getPolygon(top, left, right);
	}

	public void centerAt(Point center, int height){
		this.center = center;

		top.setLocation(center);
		left.setLocation(center);
		right.setLocation(center);

		final int width = (int) (height / EQUILATERAL_TRIANGLE_HEIGHT_WIDTH_FACTOR);

		left.translate(-width / 2, 0);
		right.translate(width / 2, 0);

		height /= 2;
		if(reversed){
			top.translate(0, height);
			left.translate(0, -height);
			right.translate(0, -height);
		}else{
			top.translate(0, -height);
			left.translate(0, height);
			right.translate(0, height);
		}
	}

	private void writeText(Graphics2D g2d, String text){
		int width = g2d.getFontMetrics().stringWidth(text);
		g2d.drawString(text, center.x - width / 2, center.y + width / 2);
	}

	private Shape getPolygon(Point p1, Point p2, Point p3){
		int[] x = {p1.x, p2.x, p3.x};
		int[] y = {p1.y, p2.y, p3.y};
		return new Polygon(x, y, 3);
	}

	public void draw(Graphics2D g2d){
		g2d.setColor(Color.WHITE);
		g2d.fill(getPolygon(left, top, right));

		g2d.setColor(BORDER_COLOR);
		g2d.draw(getBorder());


		if(this.bonus != Board.NO_BONUS){
			writeText(g2d, String.valueOf(bonus));
		}

		if(this.horizontalColor != null){
			g2d.setColor(this.horizontalColor);
			g2d.fill(getPolygon(center, left, right));
		}

		if(this.leftColor != null){
			g2d.setColor(this.leftColor);
			g2d.fill(getPolygon(center, top, left));
		}

		if(this.rightColor != null){
			g2d.setColor(this.rightColor);
			g2d.fill(getPolygon(center, top, right));
		}

		if(this.value != NO_VALUE){
			g2d.setColor(Color.WHITE);
			g2d.fill(new Ellipse2D.Double(center.x - (CIRCLE_WIDTH + CIRCLE_BORDER_THICKNESS) / 2, center.y - (CIRCLE_WIDTH + CIRCLE_BORDER_THICKNESS) / 2, CIRCLE_WIDTH + CIRCLE_BORDER_THICKNESS, CIRCLE_WIDTH + CIRCLE_BORDER_THICKNESS));
			g2d.setColor(Color.BLUE.darker());
			g2d.fill(new Ellipse2D.Double(center.x - CIRCLE_WIDTH / 2, center.y - CIRCLE_WIDTH / 2, CIRCLE_WIDTH, CIRCLE_WIDTH));
			g2d.setColor(Color.LIGHT_GRAY);
			writeText(g2d, String.valueOf(value));
		}

		if(this.overlay != null){
			g2d.setComposite(AlphaComposite.SrcOver);
			g2d.setColor(this.overlay);
			g2d.fill(getPolygon(left, top, right));
		}
	}

	public void reset(){
		setColor(null, null, null);
		setOverlay(null);
		setValue(NO_VALUE);
	}

	public boolean contains(Point point){
		return getBorder().contains(point);
	}

	public void resize(int height){
		this.centerAt(center, height);
	}

	@Override
	public Object clone(){
		TriangleCellDrawer clone = new TriangleCellDrawer();

		clone.bonus = this.bonus;
		clone.value = this.value;
		clone.reversed = this.reversed;
		clone.horizontalColor = this.horizontalColor;
		clone.leftColor = this.leftColor;
		clone.rightColor = this.rightColor;
		clone.top = this.top;
		clone.left = this.left;
		clone.right = this.right;
		clone.center = this.center;

		return clone;
	}
}
