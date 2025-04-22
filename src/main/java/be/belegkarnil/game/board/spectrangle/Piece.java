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

import java.awt.Color;

/**
 * This class defines all existing Spectrangle pieces, there are triangle shape with three colors and one value.
 * @author Belegkarnil
 */
public enum Piece {
    PURPLE(Constants.PURPLE_COLOR),
    GREEN(Constants.GREEN_COLOR),
    BLUE(Constants.BLUE_COLOR),
    YELLOW(Constants.YELLOW_COLOR),
    RED(Constants.RED_COLOR),
    WHITE(Constants.WHITE_COLOR,1),

    PURPLE_GREEN(Constants.PURPLE_COLOR,Constants.GREEN_COLOR,5),
    GREEN_RED(Constants.GREEN_COLOR,Constants.RED_COLOR,5),
    BLUE_RED(Constants.BLUE_COLOR,Constants.RED_COLOR,5),
    YELLOW_GREEN(Constants.YELLOW_COLOR,Constants.GREEN_COLOR,5),
    RED_YELLOW(Constants.RED_COLOR,Constants.YELLOW_COLOR,5),
    PURPLE_YELLOW(Constants.PURPLE_COLOR,Constants.YELLOW_COLOR,5),
    GREEN_BLUE(Constants.GREEN_COLOR,Constants.BLUE_COLOR,5),
    BLUE_PURPLE(Constants.BLUE_COLOR,Constants.PURPLE_COLOR,5),
    YELLOW_BLUE(Constants.YELLOW_COLOR,Constants.BLUE_COLOR,5),
    RED_PURPLE(Constants.RED_COLOR,Constants.PURPLE_COLOR,5),

    PURPLE_RED(Constants.PURPLE_COLOR,Constants.RED_COLOR,4),
    GREEN_YELLOW(Constants.GREEN_COLOR,Constants.YELLOW_COLOR,4),
    BLUE_YELLOW(Constants.BLUE_COLOR,Constants.YELLOW_COLOR,4),
    YELLOW_RED(Constants.YELLOW_COLOR,Constants.RED_COLOR,4),
    RED_GREEN(Constants.RED_COLOR,Constants.GREEN_COLOR,4),
    PURPLE_BLUE(Constants.PURPLE_COLOR,Constants.BLUE_COLOR,4),
    GREEN_PURPLE(Constants.GREEN_COLOR,Constants.PURPLE_COLOR,4),
    BLUE_GREEN(Constants.BLUE_COLOR,Constants.GREEN_COLOR,4),
    YELLOW_PURPLE(Constants.YELLOW_COLOR,Constants.PURPLE_COLOR,4),
    RED_BLUE(Constants.RED_COLOR,Constants.BLUE_COLOR,4),

    GREEN_BLUE_PURPLE(Constants.GREEN_COLOR,Constants.BLUE_COLOR,Constants.PURPLE_COLOR,1),
    GREEN_RED_BLUE(Constants.GREEN_COLOR,Constants.RED_COLOR,Constants.BLUE_COLOR,1),
    GREEN_PURPLE_YELLOW(Constants.GREEN_COLOR,Constants.PURPLE_COLOR,Constants.YELLOW_COLOR,1),
    GREEN_PURPLE_BLUE(Constants.GREEN_COLOR,Constants.PURPLE_COLOR,Constants.BLUE_COLOR,1),
    GREEN_BLUE_YELLOW(Constants.GREEN_COLOR,Constants.BLUE_COLOR,Constants.YELLOW_COLOR,1),
    GREEN_PURPLE_RED(Constants.GREEN_COLOR,Constants.PURPLE_COLOR,Constants.RED_COLOR,1),
    BLUE_PURPLE_YELLOW(Constants.BLUE_COLOR,Constants.PURPLE_COLOR,Constants.YELLOW_COLOR,1),
    BLUE_PURPLE_RED(Constants.BLUE_COLOR,Constants.PURPLE_COLOR,Constants.RED_COLOR,1),
    BLUE_RED_YELLOW(Constants.BLUE_COLOR,Constants.RED_COLOR,Constants.YELLOW_COLOR,1),
    PURPLE_YELLOW_RED(Constants.PURPLE_COLOR,Constants.YELLOW_COLOR,Constants.RED_COLOR,1),
    ;

    /**
     * is the left triangle color (consider base at bottom)
     */
    public final Color colorLeft;
    /**
     * is the right triangle color (consider base at bottom)
     */
    public final Color colorRight;
    /**
     * is the bottom triangle color (consider base at bottom)
     */
    public final Color colorBottom;
    /**
     * is the triangle value which is used for scoring purpose
     */
    public final int value;

    private Piece(Color color){
        this(color,color,color,6);
    }
    private Piece(Color color, int value){
        this(color,color,color,value);
    }
    private Piece(Color colorSide,Color colorBottom, int value){
        this(colorSide,colorSide,colorBottom,value);
    }
    private Piece(Color colorLeft,Color colorRight,Color colorBottom,int value){
        this.colorLeft      = colorLeft;
        this.colorRight     = colorRight;
        this.colorBottom    = colorBottom;
        this.value          = value;
    }
}