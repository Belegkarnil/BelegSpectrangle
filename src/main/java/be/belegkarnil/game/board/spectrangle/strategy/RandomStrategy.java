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
package be.belegkarnil.game.board.spectrangle.strategy;

import be.belegkarnil.game.board.spectrangle.Action;
import be.belegkarnil.game.board.spectrangle.Board;
import be.belegkarnil.game.board.spectrangle.Piece;
import be.belegkarnil.game.board.spectrangle.Player;

import java.awt.Point;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

/**
 * This {@link Strategy} represents a strategy that randomly plays.
 * Pieces are randomly sorted, position are randomly sorted, rotation are randomly sorted.
 * Then for each combination, if the move is valid ({@link Board#canPlace(Piece, Point, int)}) then play.
 * Otherwise, ask to replace/swap if the bag if not empty.
 * In other cases, return the skip action.
 *
 * @author Belegkarnil
 */
public class RandomStrategy extends StrategyAdapter{
	/**
	 * This constant defines an {@link Action} object which means to skip
	 */
	private static final Action SKIP_ACTION = new Action();
	private Random random;

	/**
	 * Initialize the RandomStrategy (i.e. a random generator)
	 */
	public RandomStrategy(){
		random = new Random();
	}

	/**
	 * Override the {@link Strategy#plays(Player, Board, Player)} and try to play randomy a valid action.
	 *
	 * @param myself   see {@link Strategy#plays}
	 * @param board    see {@link Strategy#plays}
	 * @param opponent see {@link Strategy#plays}
	 * @return a valid {@link Action#Action(Piece, Point, int)} (Piece, Position and rotation), if not possible a random Replace {@link Action#Action(Piece)}, otherwise  {@link RandomStrategy#SKIP_ACTION}
	 */
	@Override
	public Action plays(Player myself, Board board, Player opponent){
		if(!myself.hasPieces()) return SKIP_ACTION;

		// List available pieces
		Piece[] pieces = myself.getPieces();
		shuffle(pieces);

		// List available positions
		List<Point> points = new ArrayList<>();
		for(int row = 0; row < board.countRows(); row++){
			for(int column = 0; column < board.countColumns(row); column++){
				final Point currentPosition = new Point(column, row);
				final List<Point> neighbors = board.getNeighbours(currentPosition);
				int counter = 0;
				for(Point neighbor : neighbors){
					if(!board.isFree(neighbor)) counter++;
				}
				if(board.isFirstMove()) counter++;
				if(counter > 0){
					points.add(currentPosition);
				}
			}
		}
		Collections.shuffle(points, random);

		// List available rotation
		Integer[] rotations = {Integer.valueOf(0), Integer.valueOf(1), Integer.valueOf(2)};
		shuffle(rotations);

		// Then iterate
		while(!points.isEmpty()){
			Point position = points.remove(0);
			for(Piece piece : pieces){
				for(Integer rotation : rotations){
					if(board.canPlace(piece, position, rotation.intValue())){
						return new Action(piece, position, rotation.intValue());
					}
				}
			}
		}

		// Then no available move, SKIP or REPLACE/SWAP
		if(board.getBag().isEmpty()) return SKIP_ACTION;
		return createSwapAction(pieces[0]);
	}

	private <T> void shuffle(T[] data){
		T temp;
		int swap;
		for(int i = 0; i < data.length; i++){
			swap = random.nextInt(data.length);
			temp = data[i];
			data[i] = data[swap];
			data[swap] = temp;
		}
	}

	private Action createSwapAction(Piece pieceToSwap){
		return new Action(pieceToSwap);
	}
}
