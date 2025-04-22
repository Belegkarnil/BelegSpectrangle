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

/**
 * This class represents all possible actions during a {@link Game}.
 * @author Belegkarnil
 */
public class Action {
    /**
     * The piece played during the action
     */
    public final Piece piece;
    /**
     * The replace/swap request during the action
     */
    public final boolean replace;
    /**
     * The rotation applied to the piece played during the action
     */
    public final int rotation;
    /**
     * The position at which to put the piece during the action
     */
    public final Point position;

    /**
     * The maximum number of rotation that can be applied to a {@link Piece}
     */
    public static final int MAX_ROTATION = 3;

    private Action(Piece piece, boolean replace, Point position,int rotation) {
        this.piece      = piece;
        this.replace    = replace;
        this.position   = position;
        this.rotation   = (MAX_ROTATION + (rotation % MAX_ROTATION)) % MAX_ROTATION;
    }

    /**
     * Construct an action to replace/swap action (i.e. put a piece in the bag and ask another)
     * @param piece the piece to put back in the bag
     */
    public Action(Piece piece) {
        this(piece, true, null,0);
    }
    /**
     * Construct an action to skip turn, no action
     */
    public Action() {
        this(null, false, null,0);
    }

    /**
     * Construct an action to play a piece on the board
     * @param piece the piece to put on the board at pos position
     * @param position the position to put the piece on
     * @param rotation the number of clockwise rotation to apply on the piece
     */
    public Action(Piece piece, Point position,int rotation) {
        this(piece, false, position,rotation);
    }

    /**
     * Know if the action is to replace/swap a {@link Piece}
     * @return true iff the action is to replace/swap a {@link Piece}
     */
    public boolean isReplace() {
        return replace;
    }

    /**
     * Know if the action is to skip the turn
     * @return true iff the action is to skip the turn
     */
    public boolean isSkip(){
        return !replace && this.piece == null;
    }
}
