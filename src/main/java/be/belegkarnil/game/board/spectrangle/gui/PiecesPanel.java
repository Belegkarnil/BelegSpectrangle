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
import be.belegkarnil.game.board.spectrangle.Player;
import be.belegkarnil.game.board.spectrangle.event.TurnAdapter;
import be.belegkarnil.game.board.spectrangle.event.TurnEvent;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JSeparator;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;

/**
 * This class is a GUI component of the Game. It is the pieces panel ({@link SpectranglePanel}) that enables GUI interaction with a player (see {@link be.belegkarnil.game.board.spectrangle.strategy.HMIStrategy}).
 *
 * @author Belegkarnil
 */
public class PiecesPanel extends SpectranglePanel implements MouseListener, MouseWheelListener{
	public static final String REPLACE_ACTION = "Replace";
	public static final String SKIP_ACTION = "Skip";

	private static class PiecePanel extends JPanel{
		public static final Color UNSELECTED_COLOR = Color.LIGHT_GRAY;
		public static final Color SELECTED_COLOR = Color.GRAY;
		static final int SIZE = 40;
		public final String name;
		private boolean selected;
		private final Object lock;
		private final TriangleCellDrawer drawer;

		public PiecePanel(String buttonText){
			final Dimension size = new Dimension(SIZE << 2, SIZE);
			setMinimumSize(size);

			this.drawer = null;
			this.name = buttonText;
			final JButton button = new JButton(buttonText);
			setMaximumSize(size);
			setPreferredSize(size);
			setSize(size);
			setLayout(new BorderLayout());
			add(button, BorderLayout.CENTER);
			button.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e){
					firePieceSelected(buttonText);
				}
			});

			this.selected = false;
			this.lock = new Object();
		}

		public PiecePanel(Piece piece){
			final Dimension size = new Dimension(SIZE << 1, SIZE);
			setMinimumSize(size);

			this.drawer = new TriangleCellDrawer();
			this.drawer.setPiece(piece, 0);
			this.name = piece.name();

			this.selected = false;
			this.lock = new Object();
			//setFont(getFont().deriveFont(Font.BOLD,28));
		}

		public void setSelect(boolean selected){
			synchronized(lock){
				this.selected = selected;
			}
		}

		public boolean contains(Piece piece){
			if(piece == null) return contains((String) null);
			return contains(piece.name());
		}

		public boolean contains(String pieceName){
			if(this.name == null){
				return pieceName == null;
			}
			return pieceName.equals(this.name);
		}

		@Override
		public void paintComponent(Graphics g){
			super.paintComponent(g);
			Color ground = UNSELECTED_COLOR;
			synchronized(lock){
				if(selected) ground = SELECTED_COLOR;
			}
			if(this.drawer != null){
				final int width = getWidth();
				final int height = getHeight();
				g.setColor(ground);
				g.fillRect(0, 0, getWidth(), getHeight());
				this.drawer.centerAt(new Point(width >> 1, height >> 1), height);
				this.drawer.draw((Graphics2D) g);
			}
		}
	}

	private final JPanel content;
	private java.util.List<PiecePanel> pieces;
	private int position;
	private JScrollPane scroll;

	public PiecesPanel(){
		this.position = 0;
		pieces = new LinkedList<>();
		setLayout(new BorderLayout());

		content = new JPanel();
		content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
		final Dimension dimension = new Dimension(6 * PiecePanel.SIZE, 0);// 5*SIZE = max piece length + 1 for spacing
		content.setMinimumSize(dimension);
		content.setPreferredSize(dimension);
		setBackground(Color.red);
		add(scroll = new JScrollPane(content, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER), BorderLayout.CENTER);
	}

	@Override
	void register(Game game){
		if(game == null){
			reset();
			return;
		}
		game.addTurnListener(new TurnAdapter(){
			@Override
			public void onTurnBegins(TurnEvent te){
				update(te.current);
			}
		});

	}

	@Override
	void onPieceSelected(String name){

	}

	public void reset(){
		update((Player) null);
	}

	public void update(Player player){
		content.removeAll();
		pieces.clear();
		position = -1;

		int height = 0;
		if(player != null){
			List<Piece> pieces = List.of(player.getPieces());
			JComponent comp;

			content.add(comp = new PiecePanel(SKIP_ACTION));
			content.add(comp = new PiecePanel(REPLACE_ACTION));
			comp.addMouseListener(this);
			this.pieces.add((PiecePanel) comp);
			height += comp.getHeight();

			for(Piece piece : pieces){
				content.add(comp = new JSeparator());
				height += comp.getHeight();
				content.add(comp = new PiecePanel(piece));
				comp.addMouseListener(this);
				this.pieces.add((PiecePanel) comp);
				height += comp.getHeight();
			}
		}
		final Dimension dimension = new Dimension(content.getWidth(), height + PiecePanel.SIZE);
		content.setMinimumSize(dimension);
		content.setPreferredSize(dimension);
		content.revalidate();
		if(this.pieces.size() > 1) select(1);
	}

	@Override
	public void mouseClicked(MouseEvent e){
		if(e.getSource() instanceof PiecePanel){
			final PiecePanel panel = (PiecePanel) e.getSource();
			select(getPosition(panel.name));
		}
	}

	public int getPosition(String pieceName){
		for(int i = 0; i < pieces.size(); i++)
			if(pieces.get(i).contains(pieceName))
				return i;
		return -1;
	}

	public void select(int position){
		if(position >= pieces.size() || position < 1) return;
		this.position = position;

		for(PiecePanel panel : pieces){
			panel.setSelect(false);
			panel.repaint();
		}

		final PiecePanel panel = pieces.get(position);
		panel.setSelect(true);
		panel.repaint();
		firePieceSelected(panel.name);
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e){
		e.consume();
		final int length = pieces.size();
		if(this.position == -1 || length == 0) return;
		final int incr = e.getWheelRotation() > 0 ? 1 : -1;
		final int position = (this.position + incr) % length;
		select(position);
		final PiecePanel panel = pieces.get(this.position);
		int yPos = 0;
		yPos += panel.getLocation().y;
		yPos -= panel.getHeight() >> 1;
		scroll.getVerticalScrollBar().setValue(yPos);
	}

	@Override
	public void mousePressed(MouseEvent e){
	}

	@Override
	public void mouseReleased(MouseEvent e){
	}

	@Override
	public void mouseEntered(MouseEvent e){
	}

	@Override
	public void mouseExited(MouseEvent e){
	}
}