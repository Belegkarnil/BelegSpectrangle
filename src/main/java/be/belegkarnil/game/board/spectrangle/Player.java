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
package be.belegkarnil.game.board.spectrangle;

import be.belegkarnil.game.board.spectrangle.strategy.Strategy;
import be.belegkarnil.game.board.spectrangle.strategy.StrategyAdapter;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * This class represents a Spectrangle player that use a {@link Strategy} and owns some {@link Piece}s.
 * It also maintains state information like the player name, the current (round) score, the number of round won during a {@link Game}, the number of time the player skip his turn (during a round).
 *
 * @author Belegkarnil
 */
public class Player implements Externalizable{
	private String name;
	private int score;
	private int win, skip;
	private Strategy strategy;
	private List<Piece> pieces;

	/**
	 * Construct a new player
	 *
	 * @param name     The player's name
	 * @param strategy The strategy used by the player
	 */
	public Player(String name, Strategy strategy){
		this.name = name;
		this.strategy = strategy;
		this.win = 0;
		this.skip = 0;
		this.score = 0;
		this.pieces = new LinkedList<Piece>();
	}

	/**
	 * Know if the player owns a specific {link @Piece}
	 *
	 * @param piece the piece to know if the player owns
	 * @return true iff the player owns the piece
	 */
	public final boolean hasPiece(Piece piece){
		return pieces.contains(piece);
	}

	/**
	 * Get the player's name
	 *
	 * @return The player's name
	 */
	public String getName(){
		return name;
	}

	/**
	 * Get the current (round) score
	 *
	 * @return the score
	 */
	public int getScore(){
		return score;
	}

	/**
	 * Count the number of times the player won a round during a game
	 *
	 * @return The number of times the player won a round
	 */
	public int countWin(){
		return win;
	}

	/**
	 * Count the number of times that the player skip his turn during a round
	 *
	 * @return the number of times that the player skip his turn
	 */
	public int countSkip(){
		return skip;
	}

	void setScore(int score){
		this.score = score;
	}

	/**
	 * Ask the player to use his {@link Strategy} and select an {@link Action} to play
	 *
	 * @param board    The current board
	 * @param opponent The current opponent
	 * @return The action selection by the strategy
	 */
	Action behaves(Board board, Player opponent){
		return strategy.plays(this, board, opponent);
	}

	/**
	 * Get a copy of the {@link Piece}s owned by the player
	 *
	 * @return An array of {@link Piece}
	 */
	public Piece[] getPieces(){
		return pieces.toArray(new Piece[pieces.size()]);
	}

	/**
	 * Know if the player has at least one {@link Piece}
	 *
	 * @return true iff the player has at least one {@link Piece}
	 */
	public boolean hasPieces(){
		return !pieces.isEmpty();
	}

	/**
	 * Get the strategy used by the player
	 *
	 * @return The strategy
	 */
	public Strategy getStrategy(){
		return strategy;
	}

	void win(){
		win++;
	}

	void skip(){
		skip++;
	}

	void initialize(Piece[] pieces){
		this.skip = 0;
		this.score = 0;
		this.pieces = new LinkedList<Piece>();
		Collections.addAll(this.pieces, pieces);
	}

	/**
	 * Used by {@link Externalizable}
	 *
	 * @param out see {@link Externalizable}
	 * @throws IOException see {@link Externalizable}
	 */
	@Override
	public void writeExternal(ObjectOutput out) throws IOException{
		out.writeUTF(name);
		out.writeInt(win);
		out.writeInt(skip);
		out.writeInt(score);
		out.writeInt(pieces.size());
		for(Piece p : pieces){
			out.writeObject(p);
		}
	}

	/**
	 * Used by {@link Externalizable}
	 *
	 * @param in see {@link Externalizable}
	 * @throws IOException            see {@link Externalizable}
	 * @throws ClassNotFoundException see {@link Externalizable}
	 */
	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException{
		this.name = in.readUTF();
		this.win = in.readInt();
		this.skip = in.readInt();
		this.score = in.readInt();

		int size = in.readInt();
		this.pieces = new LinkedList<Piece>();
		while(size > 0){
			size--;
			this.pieces.add((Piece) in.readObject());
		}

		this.strategy = new StrategyAdapter(){
			@Override
			public Action plays(Player myself, Board board, Player opponent){
				return null;
			}
		};
	}

	void plays(Piece piece){
		this.pieces.remove(piece);
	}

	void draw(Piece piece){
		this.pieces.add(piece);
	}

	/**
	 * Count the number of pieces the player owns
	 *
	 * @return the number of pieces the player has in its hands
	 */
	public int countPieces(){
		return this.pieces.size();
	}
}