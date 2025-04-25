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
 * This class contains global colors information of the original Spectrangle game
 *
 * @author Belegkarnil
 */
public final class Constants{
	public static final Color PURPLE_COLOR = new Color(102, 0, 153);
	public static final Color RED_COLOR = new Color(139, 15, 25);
	public static final Color GREEN_COLOR = new Color(0x05, 0x60, 0x27);
	public static final Color BLUE_COLOR = new Color(63, 144, 199);
	public static final Color YELLOW_COLOR = new Color(210, 182, 39);
	public static final Color WHITE_COLOR = new Color(173, 161, 147);

	public static final Color FIST_PLAYER_COLOR = PURPLE_COLOR.brighter();
	public static final Color SECOND_PLAYER_COLOR = YELLOW_COLOR.brighter();
};
