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

import be.belegkarnil.game.board.spectrangle.Bag;
import be.belegkarnil.game.board.spectrangle.Constants;
import be.belegkarnil.game.board.spectrangle.Game;
import be.belegkarnil.game.board.spectrangle.Player;
import be.belegkarnil.game.board.spectrangle.event.GameAdapter;
import be.belegkarnil.game.board.spectrangle.event.GameEvent;
import be.belegkarnil.game.board.spectrangle.event.RoundListener;
import be.belegkarnil.game.board.spectrangle.event.RoundEvent;
import be.belegkarnil.game.board.spectrangle.event.TurnAdapter;
import be.belegkarnil.game.board.spectrangle.event.TurnEvent;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Color;
import java.awt.GridLayout;

/**
 * This class is a GUI component of the Game. It is the score panel ({@link SpectranglePanel}).
 *
 * @author Belegkarnil
 */
public class ScorePanel extends SpectranglePanel{
	private static final Color SCORE_COLOR = new Color(40, 40, 40);
	private final JLabel FIRST_PLAYER;
	private final JLabel SECOND_PLAYER;
	private final JLabel BAG_INFO;

	public ScorePanel(){
		final Dimension dim = new Dimension(0, 40);
		FIRST_PLAYER = new JLabel(String.valueOf(0), SwingConstants.CENTER);
		SECOND_PLAYER = new JLabel(String.valueOf(0), SwingConstants.CENTER);
		BAG_INFO = new JLabel(String.valueOf(0), SwingConstants.CENTER);

		final Font font = getFont().deriveFont(Font.BOLD, 28);
		FIRST_PLAYER.setFont(font);
		SECOND_PLAYER.setFont(font);
		BAG_INFO.setFont(font);


		FIRST_PLAYER.setOpaque(true);
		FIRST_PLAYER.setBackground(Constants.FIRST_PLAYER_COLOR);
		SECOND_PLAYER.setOpaque(true);
		SECOND_PLAYER.setBackground(Constants.SECOND_PLAYER_COLOR);

		FIRST_PLAYER.setForeground(SCORE_COLOR);
		SECOND_PLAYER.setForeground(SCORE_COLOR);

		setPreferredSize(dim);
		setMinimumSize(dim);
		setMaximumSize(dim);

		setLayout(new GridLayout(1, 3));
		add(FIRST_PLAYER);
		add(BAG_INFO);
		add(SECOND_PLAYER);
	}

	private void resetScore(Player first, Player second, Bag bag){
		FIRST_PLAYER.setText(createPlayerInfo(first));
		SECOND_PLAYER.setText(createPlayerInfo(second));
		setBagInfo(bag);
	}

	private void setBagInfo(int count){
		BAG_INFO.setText("Bag has " + String.valueOf(count) + " pieces");
	}

	private void setBagInfo(Bag bag){
		setBagInfo(bag.size());
	}

	private String createPlayerInfo(Player player){
		return createPlayerInfo(player.getScore(), player.countSkip(), player.countWin());
	}

	private String createPlayerInfo(int score, int skip, int win){
		final StringBuffer data = new StringBuffer();
		data.append("Score: ");
		data.append(score);
		data.append(", Skip: ");
		data.append(skip);
		data.append(", Win: ");
		data.append(win);
		return data.toString();
	}

	@Override
	void register(Game game){
		if(game == null) return;

		game.addGameListener(new GameAdapter() {
			@Override
			public void onGameEnds(GameEvent ge){
				if(ge.winner != null){
					JOptionPane.showMessageDialog(getParent(), "The winner is " + ge.winner.getName(), "Game ended", JOptionPane.INFORMATION_MESSAGE);
				}
			}
		});
		game.addTurnListener(new TurnAdapter(){
			@Override
			public void onTurnBegins(TurnEvent te){
				if(te.turn % 2 == 0){
					SECOND_PLAYER.setText(createPlayerInfo(te.opponent));
				}else{
					FIRST_PLAYER.setText(createPlayerInfo(te.opponent));
				}
				setBagInfo(te.game.getBoard().getBag());
			}
		});
		game.addRoundListener(new RoundListener(){
			@Override
			public void onRoundBegins(RoundEvent re){
				resetScore(re.startPlayer, re.opponent, re.game.getBoard().getBag());
			}

			@Override
			public void onRoundEnds(RoundEvent event){
				FIRST_PLAYER.setText(createPlayerInfo(event.startPlayer));
				SECOND_PLAYER.setText(createPlayerInfo(event.opponent));
				setBagInfo(event.game.getBoard().getBag());
			}
		});
	}

	@Override
	void onPieceSelected(String name){
	}
}

