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
import be.belegkarnil.game.board.spectrangle.Game;
import be.belegkarnil.game.board.spectrangle.Piece;
import be.belegkarnil.game.board.spectrangle.Player;
import be.belegkarnil.game.board.spectrangle.Constants;
import be.belegkarnil.game.board.spectrangle.event.TurnAdapter;
import be.belegkarnil.game.board.spectrangle.event.TurnEvent;
import be.belegkarnil.game.board.spectrangle.event.RoundAdapter;
import be.belegkarnil.game.board.spectrangle.event.RoundEvent;
import be.belegkarnil.game.board.spectrangle.strategy.HMIStrategy;

import javax.swing.JOptionPane;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/**
 * This class is a GUI component of the Game. It is the board panel ({@link SpectranglePanel}).
 *
 * @author Belegkarnil
 */
public class BoardPanel extends SpectranglePanel implements MouseListener, MouseMotionListener{
	public static final int BORDER_THICKNESS = 2;
	private Board board;
	private TriangleCellDrawer[][] cells;

	private TriangleCellDrawer area;
	private Piece current;
	private int rotation;
	private Player hmi;
	private final Object lock;
	private Color color;

	public BoardPanel(Board board){
		this.rotation = 0;
		this.hmi = null;
		this.board = board;
		this.cells = new TriangleCellDrawer[11][];
		this.lock = new Object();
		this.color = Constants.FIRST_PLAYER_COLOR;

		int i;
		for(i = 0; i < 6; i++){
			cells[i] = new TriangleCellDrawer[i + 1];
			for(int j = 0; j < cells[i].length; j++){
				cells[i][j] = new TriangleCellDrawer(board.getFactor(j, i));
				if((i + j) % 2 != 0) cells[i][j].setReversed(true);
			}
		}
		for(; i < cells.length; i++){
			cells[i] = new TriangleCellDrawer[11 - i];
			for(int j = 0; j < cells[i].length; j++){
				cells[i][j] = new TriangleCellDrawer(board.getFactor(j, i));
				if((i + j) % 2 != 0) cells[i][j].setReversed(true);
			}
		}

		area = null;
		current = null;

		final Dimension size = new Dimension(600, 600);
		setPreferredSize(size);
		setMinimumSize(size);

		addMouseListener(this);
		addMouseMotionListener(this);
	}

	@Override
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.setBackground(this.color);
		g2d.clearRect(0, 0, getWidth(), getHeight());
		g2d.setBackground(Color.WHITE);

		g2d.setStroke(new BasicStroke(BORDER_THICKNESS));
		g2d.setFont(g2d.getFont().deriveFont(Font.BOLD, 16.0f));

		final int height = getHeight();
		final int width = (int) (getWidth() * TriangleCellDrawer.EQUILATERAL_TRIANGLE_HEIGHT_WIDTH_FACTOR);

		// h is height of triangle, size is width of triangle
		final int h = Math.min(height, width) / 6;
		final int size = (int) (h / TriangleCellDrawer.EQUILATERAL_TRIANGLE_HEIGHT_WIDTH_FACTOR);

		// Triangle row i, column j
		// Java Top-Left is y=0, x=0 and Bottom-right is y=height, x=width
		// triangle i=0,j=0 is at bottom-left => y=height, x=0
		// center is y = 6*h  - h/2 = height - h/2, size/2
		// next triangle row 0, column 1 => y, x += size/2


		int x = size / 2;
		for(int i = 0; i < cells.length; i++){
			int y = 6 * h - h / 2;
			for(int j = 0; j < cells[i].length; j++){
				cells[i][j].centerAt(new Point(x, y), h);
				cells[i][j].draw(g2d);
				y -= h;
			}
			x += size / 2;
		}

		if(area != null){
			area.resize(h);
			area.draw(g2d);
		}
	}

	public TriangleCellDrawer getCell(Point panelPoint){
		for(int i = 0; i < cells.length; i++){
			for(int j = 0; j < cells[i].length; j++){
				if(cells[i][j].contains(panelPoint))
					return cells[i][j];
			}
		}
		return null;
	}

	public Point getCellPosition(Point panelPoint){ // x,y => i,j
		for(int i = 0; i < cells.length; i++){
			for(int j = 0; j < cells[i].length; j++){
				if(cells[i][j].contains(panelPoint))
					return new Point(j, i);
			}
		}
		return null;
	}

	public void update(Point boardPositionChanged){
		update(boardPositionChanged.x, boardPositionChanged.y);
	}

	public void update(int x, int y){
		cells[y][x].setColor(board.getBaseColor(x, y), board.getLeftColor(x, y), board.getRightColor(x, y));
		cells[y][x].setValue(board.getValue(x, y));
	}

	public void reset(){
		this.current = null;
		this.area = null;
		for(int y = 0; y < cells.length; y++){
			for(int x = 0; x < cells[y].length; x++){
				cells[y][x].reset();
				if(!board.isFree(x, y)) update(x, y);
			}
		}
		repaint();
	}

	@Override
	public void mouseClicked(MouseEvent e){
		if(e.getButton() == MouseEvent.BUTTON1){
			// Left => put the piece
			if(current == null) return; // no piece to place
			final Point pos = getCellPosition(e.getPoint());
			final TriangleCellDrawer cell = this.cells[pos.y][pos.x];
			if(cell == null) return; // no position to place at
			if(!board.canPlace(this.current, pos, this.rotation)) return; // invalid pos
			synchronized(lock){
				if(this.hmi != null)
					((HMIStrategy) (this.hmi.getStrategy())).setPiece(this.current, pos, this.rotation);
			}
			current = null;
			//this.repaint();
		}else /*if(e.getButton() == MouseEvent.BUTTON2)*/{ // Other mouse button, clockwise rotate
			this.rotation = (this.rotation + 1) % 3;
			if(this.area != null){
				this.area.setPiece(this.current, this.rotation);
				this.repaint();
			}
		}
	}

	@Override
	public void mouseMoved(MouseEvent e){
		Point p = getCellPosition(e.getPoint());
		if(p == null) return;
		TriangleCellDrawer cell = cells[p.y][p.x];

		area = (TriangleCellDrawer) cell.clone();

		if(this.current == null){
			area.setValue(TriangleCellDrawer.NO_VALUE);
			area.setColor(this.color, this.color, this.color);
		}else{
			area.setPiece(current, this.rotation);
			if(!board.canPlace(this.current, p, this.rotation)){
				area.setOverlay(Color.RED);
			}
		}
		this.repaint();
	}

	@Override
	public void mousePressed(MouseEvent e){

	}

	@Override
	public void mouseReleased(MouseEvent e){

	}

	@Override
	public void mouseEntered(MouseEvent e){

	}

	@Override
	public void mouseExited(MouseEvent e){

	}

	@Override
	public void mouseDragged(MouseEvent e){

	}

	@Override
	void register(Game game){
		if(game == null){
			reset();
			return;
		}
		game.addRoundListener(new RoundAdapter(){
			@Override
			public void onRoundBegins(RoundEvent re){
				reset();
			}
		});

		game.addTurnListener(new TurnAdapter(){
			@Override
			public void onTurnBegins(TurnEvent te){
				color = (te.turn % 2 == 0 ? Constants.FIRST_PLAYER_COLOR : Constants.SECOND_PLAYER_COLOR);
				current = null;
				if(te.current.getStrategy() instanceof HMIStrategy){
					synchronized(lock){
						hmi = te.current;
					}
				}
			}

			@Override
			public void onTurnEnds(TurnEvent te){
				reset();
				synchronized(lock){
					hmi = null;
				}
				if(te.action != null && !te.action.isSkip() && !te.action.isReplace()){
					update(te.action.position);
				}
				BoardPanel.this.repaint();
			}
		});
	}

	@Override
	void onPieceSelected(String name){
		boolean repaint = false;
		synchronized(lock){
			if(hmi != null){
				Piece piece = null;
				try{
					piece = Piece.valueOf(name);
				}catch(IllegalArgumentException e){
					// Button (skip/replace)
				}
				if(piece == null){
					if(name == PiecesPanel.REPLACE_ACTION){
						// TODO JOption pane is not selected
						if(this.current == null){
							JOptionPane.showMessageDialog(getParent(),
									  "Please select a piece first.", "Error", JOptionPane.ERROR_MESSAGE);
						}else{
							((HMIStrategy) (this.hmi.getStrategy())).setReplaceAction(this.current);
						}
					}else{
						((HMIStrategy) (this.hmi.getStrategy())).setSkipAction();
					}
					this.current = null;
				}else{
					this.current = piece;
					if(this.area != null){
						this.area.setPiece(this.current, this.rotation);
						repaint = true;
					}
				}
			}
		}
		if(repaint) repaint();
	}
}
