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

import java.util.List;
import java.util.Random;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Collections;

/**
 * The class represents the bag of {@link Piece} linked to a {@link Board} during a {@link Game}.
 *
 * @author Belegkarnil
 */
public class Bag implements Cloneable {
    private final Random random;
    private List<Piece> content;

    /**
     * Construct a bag with random undefined seed
     */
    public Bag(){
        this(new Random((new Random()).nextLong()));
    }

    /**
     * Constructor that clones the bag (see {@link Bag#clone()})
     * @param bag The bag to clone
     */
    protected Bag(Bag bag){
        this(new Random((new Random()).nextLong()));
        for(Piece piece : bag.content){
            this.content.add(piece);
        }
    }
    /**
     * Construct a bag with custom {@link Random} generator
     */
    public Bag(Random random) {
        this.random = random;
        this.content    = new LinkedList<Piece>();
    }

    /**
     * Swap a given {@link Piece} by another from the bag
     * @param piece The Piece to swap
     * @return A new {@link Piece} taken from the bag
     */
    protected Piece swap(Piece piece){
        if(piece == null) throw new NullPointerException("Piece cannot be null");
        final Piece taken = take();
        this.content.add(random.nextInt(size()+1),piece);
        return taken;
    }

    /**
     * Take an amount of Piece from the bag (see {@link Bag#take()}
     * @param amount The amount of {@link Piece} to take
     * @return An array of {@link Piece}s taken from the bag
     */
    protected Piece[] take(int amount){
        Piece[] pieces = new Piece[amount];
        for(int i=0; i<amount; i++){
            pieces[i] = take();
        }
        return pieces;
    }

    /**
     * Reset the bag for another round/game. All Piece are put into the bag.
     */
    protected void reset(){
        this.content = new ArrayList<Piece>(List.of(Piece.values()));
        Collections.shuffle(content,this.random);
    }

    /**
     * Take one {@link Piece} from the bag
     * @return A {@link Piece} extracted from the bag
     */
    protected Piece take(){
        return content.remove(0);
    }

    /**
     * Get the number of {@link Piece}s the bag has.
     * @return The number of {@link Piece}s that are in the bag
     */
    public int size(){
        return content.size();
    }

    /**
     * Know if the bag is empty
     * @return true iff the bag contains no {@link Piece}
     */
    public boolean isEmpty(){
        return content.isEmpty();
    }

    /**
     * Clone the current bag (note that the random number is different)
     * @return a clone of the bag state
     * @throws CloneNotSupportedException
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        return new Bag(this);
    }
}
