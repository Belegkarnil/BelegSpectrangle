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

import java.awt.Point;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class represents the board of the Spectrangle game. It includes factor/multiple, the {@link Bag}, the piece (colors) that are already on the board.
 * This class includes some utility methods to compute neighbors positions, check if a piece can be put, and rotation of a piece.
 *
 * @author Belegkarnil
 */
public class Board implements Cloneable{
	/**
	 * An array index to specify the position that contains the base piece color (always base at bottom)
	 */
	public static final int BASE_COLOR = 0;
	/**
	 * An array index to specify the position that contains the left piece color (always base at bottom)
	 */
	public static final int LEFT_COLOR = 1;
	/**
	 * An array index to specify the position that contains the right piece color (always base at bottom)
	 */
	public static final int RIGHT_COLOR = 2;

	/**
	 * The default factor/multiple which means there are no bonus
	 */
	public static final int NO_BONUS = 1;
	/**
	 * The initial value of positions when there are not yet a piece
	 */
	public static final int NO_VALUE = 0;
	/**
	 * The default reference board size
	 */
	public static final int DEFAULT_SIZE = 6;

	private boolean firstMove;
	private Color[][][] grid;
	private int[][] values;
	private int[][] factors;
	private final int SIZE;

	/**
	 * The bag linked to the board initialized during construction
	 */
	protected final Bag bag;

	/**
	 * Construct a board with a reference size of {@link Board#DEFAULT_SIZE}
	 */
	public Board(){
		this(new Bag(), DEFAULT_SIZE);
		reset();
	}

	/**
	 * Construct a board of the given size
	 *
	 * @param size the board size (i.e. reference size)
	 */
	public Board(int size){
		this(new Bag(), size);
		reset();
	}

	/**
	 * Constructor that clones the board (see {@link Board#clone()})
	 *
	 * @param board The board to clone
	 * @throws CloneNotSupportedException
	 */
	protected Board(Board board) throws CloneNotSupportedException{
		this((Bag) board.bag.clone(), board.SIZE);
		this.firstMove = board.firstMove;
		for(int row = 0; row < board.countRows(); row++){
			for(int column = 0; column < board.countColumns(row); column++){
				this.factors[row][column] = board.factors[row][column];
				this.values[row][column] = board.values[row][column];
				for(int color = 0; color < board.grid[row][column].length; color++){
					this.grid[row][column][color] = board.grid[row][column][color];
				}
			}
		}
	}

	private Board(Bag bag, int size){
		this.SIZE = size;
		this.bag = bag;

		grid = new Color[2 * size - 1][][];
		values = new int[2 * size - 1][];
		factors = new int[2 * size - 1][];

		int rowCounter = 0;
		int y = 0;
		for(; y < size; y++){
			rowCounter++;
			grid[y] = new Color[rowCounter][3];
			values[y] = new int[rowCounter];
			factors[y] = new int[rowCounter];
		}
		for(; y < grid.length; y++){
			rowCounter--;
			grid[y] = new Color[rowCounter][3];
			values[y] = new int[rowCounter];
			factors[y] = new int[rowCounter];
		}
	}

	/**
	 * Reset the board (i.e. Piece values, factors/multipliers, bag, and reset the state of the first move)
	 */
	protected void reset(){
		bag.reset();
		for(int y = 0; y < grid.length; y++){
			Arrays.fill(values[y], NO_VALUE);
			Arrays.fill(factors[y], NO_BONUS);
			for(int x = 0; x < grid[y].length; x++){
				Arrays.fill(grid[y][x], null);
			}
		}

		factors[1][0] = 3;
		factors[3][2] = 2;
		factors[4][2] = 4;
		factors[5][0] = 2;
		factors[5][1] = 4;
		factors[5][4] = 3;
		factors[6][2] = 4;
		factors[7][2] = 2;
		factors[9][0] = 3;

		firstMove = true;
	}

	/**
	 * Get the reference size of this board
	 *
	 * @return the reference size
	 */
	public int getSize(){
		return SIZE;
	}

	/**
	 * Know if a position is contained in the bounds of the board
	 *
	 * @param x the x-axis (column) position
	 * @param y the y-axis (row) position
	 * @return true iff the position is on the board
	 */
	public boolean inBounds(int x, int y){
		if(x < 0 || y < 0) return false;
		if(y >= grid.length) return false;
		if(x >= grid[y].length) return false;
		return true;
	}

	/**
	 * Know if a position is contained in the bounds of the board
	 *
	 * @param position the position on the board
	 * @return true iff the position is on the board
	 */
	public boolean inBounds(Point position){
		return inBounds(position.x, position.y);
	}

	/**
	 * Know if a position is free (i.e. there are no {@link Piece} at that position)
	 *
	 * @param x the x-axis (column) position
	 * @param y the y-axis (row) position
	 * @return true iff the position is free
	 */
	public boolean isFree(int x, int y){
		return inBounds(x, y) && values[y][x] == NO_VALUE;
	}

	/**
	 * Know if a position is free (i.e. there are no {@link Piece} at that position)
	 *
	 * @param position the position on the board
	 * @return true iff the position is free
	 */
	public boolean isFree(Point position){
		return isFree(position.x, position.y);
	}

	/**
	 * Know if the position provide a bonus (i.e. factor/multiple)
	 *
	 * @param position the position on the board
	 * @return true iff the position is a bonus position on the board
	 */
	public boolean isBonus(Point position){
		return isBonus(position.x, position.y);
	}

	/**
	 * Know if the position provide a bonus (i.e. factor/multiple)
	 *
	 * @param x the x-axis (column) position
	 * @param y the y-axis (row) position
	 * @return true iff the position is a bonus position on the board
	 */
	public boolean isBonus(int x, int y){
		return inBounds(x, y) && factors[y][x] != NO_BONUS;
	}

	/**
	 * Know if a {@link Piece} with a given rotation can be put at a given position
	 *
	 * @param piece    The piece to place
	 * @param position The position on the board
	 * @param rotation The rotation to apply on the piece
	 * @return true iff the rotated piece can be put at the given position
	 */
	public boolean canPlace(Piece piece, Point position, int rotation){
		// pos must be in bounds and free
		if(!inBounds(position) || !isFree(position)) return false;
		if(firstMove){// fist move can be everywhere except on a bonus cell
			return !isBonus(position);
		}
		if(piece.equals(Piece.WHITE)){
			// No need to check color for white piece
			// Need at least 1 neighbor
			return getLeftValue(position) != NO_VALUE || getRightValue(position) != NO_VALUE || getBaseValue(position) != NO_VALUE;
		}
		// Need at least 1 neighbor and adjacent color for each neighbors
		final Color[] current = applyRotate(piece, rotation);
		int count = 0;
		int valid = 0;
		Point p;

		p = getLeftNeighbour(position);
		if(inBounds(p) && !isFree(p)){
			count++;
			if(grid[p.y][p.x][RIGHT_COLOR] == current[LEFT_COLOR] || grid[p.y][p.x][RIGHT_COLOR] == Constants.WHITE_COLOR)
				valid++;
		}

		p = getRightNeighbour(position);
		if(inBounds(p) && !isFree(p)){
			count++;
			if(grid[p.y][p.x][LEFT_COLOR] == current[RIGHT_COLOR] || grid[p.y][p.x][RIGHT_COLOR] == Constants.WHITE_COLOR)
				valid++;
		}

		p = getBaseNeighbour(position);
		if(inBounds(p) && !isFree(p)){
			count++;
			if(grid[p.y][p.x][BASE_COLOR] == current[BASE_COLOR] || grid[p.y][p.x][BASE_COLOR] == Constants.WHITE_COLOR)
				valid++;
		}
		return (count >= 1) && count == valid;
	}

	/**
	 * Compute the rotation of a {@link Piece}, always consider the triangle base at bottom
	 *
	 * @param piece    The piece to apply the rotation
	 * @param rotation The rotation is an integer: 0 means no rotation, 1 for a single clockwise rotation, and 2 for two clockwise rotations
	 * @return an array of three {@link Color} (see indices: {@link Board#LEFT_COLOR}, {@link Board#RIGHT_COLOR}, and {@link Board#BASE_COLOR})
	 */
	public Color[] applyRotate(Piece piece, int rotation){
		final Color[] colors = new Color[3];
		colors[Board.BASE_COLOR] = piece.colorBottom;
		colors[Board.LEFT_COLOR] = piece.colorLeft;
		colors[Board.RIGHT_COLOR] = piece.colorRight;

		if(rotation == 1){
			colors[Board.BASE_COLOR] = piece.colorRight;
			colors[Board.LEFT_COLOR] = piece.colorBottom;
			colors[Board.RIGHT_COLOR] = piece.colorLeft;
		}else if(rotation == 2){
			colors[Board.BASE_COLOR] = piece.colorLeft;
			colors[Board.LEFT_COLOR] = piece.colorRight;
			colors[Board.RIGHT_COLOR] = piece.colorBottom;
		}else{
			colors[Board.BASE_COLOR] = piece.colorBottom;
			colors[Board.LEFT_COLOR] = piece.colorLeft;
			colors[Board.RIGHT_COLOR] = piece.colorRight;
		}

		return colors;
	}

	/**
	 * Put a rotated {@link Piece} at a given position
	 *
	 * @param piece    The piece to put on the board
	 * @param position The position at which to put the piece
	 * @param rotation The rotation to apply
	 * @return the increment of score but positioning the piece (i.e. factor * max(1,number of adjacents) * piece value)
	 */
	protected int place(Piece piece, Point position, int rotation){
		if(!canPlace(piece, position, rotation)) return 0;
		final Color[] colors = applyRotate(piece, rotation);

		int corners = 0;
		if(!firstMove){
			if(getLeftValue(position) != NO_VALUE) corners++;
			if(getRightValue(position) != NO_VALUE) corners++;
			if(getBaseValue(position) != NO_VALUE) corners++;
		}else{
			firstMove = false;
			corners++;
		}
		this.values[position.y][position.x] = piece.value;
		this.grid[position.y][position.x] = colors;

		return corners * piece.value * this.factors[position.y][position.x];
	}

	/**
	 * Get the neighbors position of a position
	 *
	 * @param position the position on the board
	 * @return A {@link List} of Point that represents valid (i.e. see {@link Board#inBounds(Point)}) position adjacent to the given position
	 */
	public List<Point> getNeighbours(Point position){
		final List<Point> neighbours = new ArrayList<Point>(3);
		Point p;
		p = getLeftNeighbour(position);
		if(inBounds(p)) neighbours.add(p);

		p = getRightNeighbour(position);
		if(inBounds(p)) neighbours.add(p);

		p = getBaseNeighbour(position);
		if(inBounds(p)) neighbours.add(p);

		return neighbours;
	}

	/**
	 * Get the left position of a given position
	 *
	 * @param position the position on the board
	 * @return A Point on the left of the given position (Note: can be an invalid position)
	 */
	public final Point getLeftNeighbour(Point position){
		return new Point(position.x, position.y - 1);
	}

	/**
	 * Get the right position of a given position
	 *
	 * @param position the position on the board
	 * @return A Point on the right of the given position (Note: can be an invalid position)
	 */
	public final Point getRightNeighbour(Point position){
		return new Point(position.x, position.y + 1);
	}

	/**
	 * Get the base position of a given position
	 *
	 * @param position the position on the board
	 * @return A Point on the base of the given position (Note: can be an invalid position)
	 */
	public final Point getBaseNeighbour(Point position){
		if((position.x + position.y) % 2 == 0) return new Point(position.x - 1, position.y);
		return new Point(position.x + 1, position.y);
	}

	/**
	 * Get the piece value at the left neighbor of a given position
	 *
	 * @param position the position on the board
	 * @return The piece value of the base neighbor iff exists (see {@link Board#getBaseNeighbour(Point)}), otherwise {@link Board#NO_VALUE}
	 */
	public final int getLeftValue(Point position){
		position = getLeftNeighbour(position);
		if(!inBounds(position)) return NO_VALUE;
		return values[position.y][position.x];
	}

	/**
	 * Get the piece value at the right neighbor of a given position
	 *
	 * @param position the position on the board
	 * @return The piece value of the base neighbor iff exists (see {@link Board#getBaseNeighbour(Point)}), otherwise {@link Board#NO_VALUE}
	 */
	public final int getRightValue(Point position){
		position = getRightNeighbour(position);
		if(!inBounds(position)) return NO_VALUE;
		return values[position.y][position.x];
	}

	/**
	 * Get the piece value at the base neighbor of a given position
	 *
	 * @param position the position on the board
	 * @return The piece value of the base neighbor iff exists (see {@link Board#getBaseNeighbour(Point)}), otherwise {@link Board#NO_VALUE}
	 */
	public final int getBaseValue(Point position){
		position = getBaseNeighbour(position);
		if(!inBounds(position)) return NO_VALUE;
		return values[position.y][position.x];
	}

	/**
	 * Count the number of rows that the board has
	 *
	 * @return the number of rows
	 */
	public int countRows(){
		return this.grid.length;
	}

	/**
	 * Count the number of column that the board has at a given row
	 *
	 * @param row the row at which to count the number of columns
	 * @return the number of columns
	 */
	public int countColumns(int row){
		return this.grid[row].length;
	}

	/**
	 * Get the factor/multiplier value at a given position
	 *
	 * @param x the x-axis (column) position
	 * @param y the y-axis (row) position
	 * @return The factor (multiplier of the board) value iff exists, otherwise {@link Board#NO_BONUS}
	 */
	public int getFactor(int x, int y){
		return factors[y][x];
	}

	/**
	 * Get the factor/multiplier value at a given position
	 *
	 * @param position the position on the board
	 * @return The factor (multiplier of the board) value iff exists, otherwise {@link Board#NO_BONUS}
	 */
	public int getFactor(Point position){
		return getFactor(position.x, position.y);
	}

	/**
	 * Get value at a given position
	 *
	 * @param x the x-axis (column) position
	 * @param y the y-axis (row) position
	 * @return The piece value iff exists, otherwise {@link Board#NO_VALUE}
	 */
	public int getValue(int x, int y){
		return values[y][x];
	}

	/**
	 * Get value at a given position
	 *
	 * @param position the position on the board
	 * @return The piece value iff exists, otherwise {@link Board#NO_VALUE}
	 */
	public int getValue(Point position){
		return getValue(position.x, position.y);
	}

	/**
	 * Get the base color (always consider base triangle at bottom) at a position
	 *
	 * @param x the x-axis (column) position
	 * @param y the y-axis (row) position
	 * @return The base/bottom color
	 */
	public Color getBaseColor(int x, int y){
		return grid[y][x][BASE_COLOR];
	}

	/**
	 * Get the left color (always consider base triangle at bottom) at a position
	 *
	 * @param x the x-axis (column) position
	 * @param y the y-axis (row) position
	 * @return The left color
	 */
	public Color getLeftColor(int x, int y){
		return grid[y][x][LEFT_COLOR];
	}

	/**
	 * Get the right color (always consider base triangle at bottom) at a position
	 *
	 * @param x the x-axis (column) position
	 * @param y the y-axis (row) position
	 * @return The right color
	 */
	public Color getRightColor(int x, int y){
		return grid[y][x][RIGHT_COLOR];
	}

	/**
	 * Get the base color (always consider base triangle at bottom) at a position
	 *
	 * @param position the position on the board
	 * @return The base/bottom color
	 */
	public Color getBaseColor(Point position){
		return getBaseColor(position.x, position.y);
	}

	/**
	 * Get the left color (always consider base triangle at bottom) at a position
	 *
	 * @param position the position on the board
	 * @return The left color
	 */
	public Color getLeftColor(Point position){
		return getLeftColor(position.x, position.y);
	}

	/**
	 * Get the right color (always consider base triangle at bottom) at a position
	 *
	 * @param position the position on the board
	 * @return The right color
	 */
	public Color getRightColor(Point position){
		return getRightColor(position.x, position.y);
	}

	/**
	 * Get the current {@link Bag} linked to the board and used by a {@link Game}
	 *
	 * @return the bag
	 */
	public Bag getBag(){
		return bag;
	}

	/**
	 * Know if the board has not yet a {@link Piece} on it
	 *
	 * @return true iff there are no {@link Player} that has already put a {@link Piece}
	 */
	public boolean isFirstMove(){
		return firstMove;
	}

	/**
	 * Clone the current board (use clone on the bag, see {@link Bag#clone()})
	 *
	 * @return a clone of the board state
	 * @throws CloneNotSupportedException
	 */
	@Override
	public Object clone() throws CloneNotSupportedException{
		return new Board(this);
	}
}
