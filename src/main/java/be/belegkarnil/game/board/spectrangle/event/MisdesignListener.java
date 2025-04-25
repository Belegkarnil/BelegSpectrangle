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
package be.belegkarnil.game.board.spectrangle.event;

import be.belegkarnil.game.board.spectrangle.Game;

import java.util.EventListener;

/**
 * The listener interface for receiving "interesting" misdesign events (begin, and end) during a game.
 * The class that is interested in processing a misdesign event either implements this interface (and all the methods it contains) or extends the abstract {@link MisdesignAdapter} class (overriding only the methods of interest).
 * <p>
 * The listener object created from that class is then registered in a {@link be.belegkarnil.game.board.spectrangle.strategy.Strategy}'s {@link be.belegkarnil.game.board.spectrangle.strategy.Strategy#register(Game)} on the {@link Game} object using {@link Game#addMisdesignListener(MisdesignListener)}.
 * Please, do not forget to unregister the listener when the {@link be.belegkarnil.game.board.spectrangle.strategy.Strategy}'s {@link be.belegkarnil.game.board.spectrangle.strategy.Strategy#unregister(Game)} method is called by using {@link Game#removeMisdesignListener(MisdesignListener)} on the {@link Game} object.
 * A misdesign event is generated when the game ask a player to use its {@link be.belegkarnil.game.board.spectrangle.strategy.Strategy} and an error (implementation, design, misconception) occurs. When a misdesign event occurs, the relevant method in the listener object is invoked, and the {@link MisdesignEvent} is passed to it.
 *
 * @author Belegkarnil
 */
public interface MisdesignListener extends EventListener{
	/**
	 * Invoked when a {@link be.belegkarnil.game.board.spectrangle.strategy.Strategy} try to play an invalid {@link be.belegkarnil.game.board.spectrangle.Piece} (not in the {@link be.belegkarnil.game.board.spectrangle.Player}'hands).
	 *
	 * @param event The event that contains all information about the invalid {@link be.belegkarnil.game.board.spectrangle.Piece}
	 */
	public void onInvalidPiece(MisdesignEvent event);

	/**
	 * Invoked when a {@link be.belegkarnil.game.board.spectrangle.strategy.Strategy} try to play at an invalid {@link java.awt.Point} position (out of the board, a busy place, no adjacent place, or no matching color place)
	 *
	 * @param event The event that contains all information about the invalid {@link be.belegkarnil.game.board.spectrangle.Piece} and {@link java.awt.Point} position
	 */
	public void onInvalidPosition(MisdesignEvent event);

	/**
	 * Invoked when a {@link be.belegkarnil.game.board.spectrangle.strategy.Strategy} hangs and does not select an {@link be.belegkarnil.game.board.spectrangle.Action} Within the given time frame.
	 *
	 * @param event The event that contains all information about the current {@link Game} status and the current {@link be.belegkarnil.game.board.spectrangle.Player}
	 */
	public void onTimeout(MisdesignEvent event);

	/**
	 * Invoked when a {@link be.belegkarnil.game.board.spectrangle.strategy.Strategy} has badly designed and an {@link Exception} occurs.
	 *
	 * @param event The event that contains all information about the current {@link Game} status, the current {@link be.belegkarnil.game.board.spectrangle.Player} and the {@link Exception}
	 */
	public void onException(MisdesignEvent event);
}
