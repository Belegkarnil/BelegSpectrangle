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

import be.belegkarnil.game.board.spectrangle.Game;
import be.belegkarnil.game.board.spectrangle.Piece;
import be.belegkarnil.game.board.spectrangle.event.TurnEvent;
import be.belegkarnil.game.board.spectrangle.event.TurnListener;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;

/**
 * This class is a GUI component of the Game. It allows to see opponent pieces.
 *
 * @author Belegkarnil
 */
public class OpponentPiecesPanel extends SpectranglePanel implements TurnListener{
	public final int SIZE = 60;
	public final int SPACER = 20;
	private final TriangleCellDrawer[] drawer;

	public OpponentPiecesPanel(){
		int rows = Game.INITIAL_PIECES >> 1;
		if((rows << 1) < Game.INITIAL_PIECES) rows++;
		setLayout(new GridLayout(rows, 2));
		drawer = new TriangleCellDrawer[Game.INITIAL_PIECES];
		for(int i = 0; i < drawer.length; i++){
			int x = SPACER+ (i % 2) * (SIZE+SPACER);
			final int y = (i >> 1) * SIZE;
			drawer[i] = new TriangleCellDrawer();
			drawer[i].centerAt(new Point(x + (SIZE>>1), y + (SIZE>>1)), SIZE);
		}
		final Dimension size = new Dimension(SIZE * 2, SIZE * rows);
		setMinimumSize(size);
		setMaximumSize(size);
		setPreferredSize(size);
	}

	@Override
	protected void paintComponent(Graphics g){
		super.paintComponent(g);
		final Graphics2D g2d = (Graphics2D) g;
		for(TriangleCellDrawer drawer : drawer){
			drawer.draw(g2d);
		}
	}

	@Override
	void register(Game game){
		if(game == null){
			reset();
			this.invalidate();
			this.repaint();
			return;
		}
		game.addTurnListener(this);
	}

	private void reset(){
		for(TriangleCellDrawer drawer : drawer)
			drawer.reset();
	}

	@Override
	void onPieceSelected(String name){

	}

	@Override
	public void onTurnBegins(TurnEvent event){
		reset();
		Piece[] pieces = event.opponent.getPieces();
		for(int i = 0; i < pieces.length; i++){
			drawer[i].setPiece(pieces[i], 0);
		}
		this.invalidate();
		this.repaint();
	}

	@Override
	public void onTurnEnds(TurnEvent event){

	}
}
