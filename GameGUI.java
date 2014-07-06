/**
 *
 * Ananya Kumar, 2011
 *
 * To-do:
 * - Check if collapse is valid; only allow valid collapses
 * - When mousing over a piece during entanglement, show collapse
 * 
 **/

import java.lang.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

public class GameGUI extends JFrame
{
	Board B; //Stores the information about the board
	CustomPanel mainPanel; //The main display panel
	int size; //The length of the game board
	JLabel infoLbl; //Information on the game
	JPanel mainBox[]; //One panel for each game square
	boolean panelsadded = false;
	
	public GameGUI ( int m, int n )
	{
		super();
		B = new Board(m,n);
		size = (int)(Math.ceil(Math.sqrt(m)));
		
		setSize(630,630);
		setLayout(new BorderLayout());
		
		mainPanel = new CustomPanel();
		mainPanel.setSize(new Dimension(600,600));
		mainPanel.setLayout(new GridLayout(size,size));
		mainPanel.addMouseListener(new mL());
		add(mainPanel, BorderLayout.CENTER);
		
		mainBox = new JPanel[m];
		
		infoLbl = new JLabel( "A new game has started of size " + size + ". It is now player 1's turn");
		add(infoLbl, BorderLayout.SOUTH);
		
		addKeyListener(new UndoListener());
		
		setTitle("GQT3 game with s = " + m + ", n = " + n);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}
		
	private class CustomPanel extends JPanel
	{
		public void paint ( Graphics g ) //Paints the lines for the grid
		{
			super.paint(g);
			Graphics2D g2 = (Graphics2D)g;
			int boxWidth = (int)((double)(mainPanel.getSize().getWidth())/(size));
			int boxHeight = (int)((double)(mainPanel.getSize().getHeight())/(size));
			int i;
			
			for ( i = 1; i < size; i++ )
			{
				g2.drawLine( i * boxWidth, 0, i * boxWidth, (int)mainPanel.getSize().getHeight() );
				g2.drawLine( 0, i * boxHeight, (int)mainPanel.getSize().getWidth(), i * boxHeight );
			}
		}
	}
	
	private class mL implements MouseListener //Listens for move placement through mouse clicking
	{
		int boxWidth;
		int boxHeight;
		int m;
		
		public void mouseClicked ( MouseEvent e )
		{
			boxWidth = (int)((double)(mainPanel.getSize().getWidth())/((double)(size)));
			boxHeight = (int)((double)(mainPanel.getSize().getHeight())/(size));
			m = (int)(e.getY()/boxHeight) * size + (int)(e.getX()/boxWidth);
			
			if ( B.isEntangled() ) B.collapse(m);
			else B.addPiece(m);
			printBoard();
		}
		
		public void	mouseEntered ( MouseEvent e ) {}
		public void	mouseExited ( MouseEvent e ) {}
		public void	mousePressed ( MouseEvent e ) {}
		public void	mouseReleased(MouseEvent e) {}
	}
	
	private class UndoListener extends KeyAdapter
	{
		public void keyPressed ( KeyEvent ke )
		{
			if ( ke.getKeyCode() == KeyEvent.VK_Z ) //Undo the previous move
			{
				if ( B.hasCollapsed() ) B.undoCollapse();
				else B.undoAdd();
				printBoard();
			}
			
			if ( ke.getKeyCode() == KeyEvent.VK_N ) //Ask the AI to play the next move
			{
				GetMove g = new GetMove();
				g.computeMove(B,2.1);
				//System.out.println(g.computeMove(B,2.1));
				if ( B.gameOver() ) {}
				else if ( B.isEntangled() ) B.collapse(g.getCollapse(),true);
				else if ( B.howManyPieces() == 1 ) B.addPiece(g.getMove1());
				else { B.addPiece(g.getMove1()); B.addPiece(g.getMove2()); }
				printBoard();
			}
		}
	} 
	
	public void printBoard ()
	{
		int i, j, k;
		char[] tempChar = new char[2];
		tempChar[0] = 'O';
		tempChar[1] = 'X';
		Font f;
		JLabel pieceLbl;
		
		for ( i = 0; i < size*size; i++ )
		{
			if ( panelsadded ) mainPanel.remove(mainBox[i]);
			
			if ( B.isSquareClassical(i) )
			{
				mainBox[i] = new JPanel(new BorderLayout());
				f = new Font("Times New Roman", Font.BOLD, (int)(getSize().getWidth() / ( 2*size) ));
				pieceLbl = new JLabel("<HTML>" + (tempChar[(B.pieceAt(i,0)+1)%2]) + "<sub>" + (B.pieceAt(i,0)+1) + "</sub></HTML>");
				pieceLbl.setHorizontalAlignment( SwingConstants.CENTER );
				pieceLbl.setFont(f);
				mainBox[i].add(pieceLbl,BorderLayout.CENTER);				
			}
			
			else
			{
				int nump = B.numSquarePieces(i);
				
				if ( nump != 0 )
				{
					int dim = (int)(Math.ceil(Math.sqrt(nump)));
					mainBox[i] = new JPanel(new GridLayout( dim, dim ));
					
					for ( j = 0; j < nump; j++ )
					{	
						pieceLbl = new JLabel("<HTML>" + (tempChar[(B.pieceAt(i,j)+1)%2]) + "<sub>" + (B.pieceAt(i,j)+1) + "</sub></HTML>");
						pieceLbl.setHorizontalAlignment( SwingConstants.CENTER );
						mainBox[i].add(pieceLbl);
					}
				}
				
				else mainBox[i] = new JPanel(new FlowLayout());
			}
			
			mainPanel.add(mainBox[i]);
		}
		
		if ( B.gameOver() )
		{
			double score = B.getWinner();
			if ( score == 0 ) infoLbl.setText("The game has finished and has ended in a draw.");
			else if ( score > 0 ) infoLbl.setText("The game is finished. Congratulations to Player 1 who won by " + score + " points!");
			else infoLbl.setText("The game is finished. Congratulations to Player 2 who won by " + (-score) + " points!");
		}
		
		else if ( B.isEntangled() ) infoLbl.setText("The board is entangled. Player " + (B.getTurn()%2+1) + " please choose which square to collapse the latest piece into.");
		else if ( B.howManyPiecesLeft() > 1 ) infoLbl.setText("Player " + (B.getTurn()%2+1) + ": Your turn to play " + B.howManyPiecesLeft() + " more quantum pieces.");
		else infoLbl.setText("Player " + (B.getTurn()%2+1) + ": Your turn to play 1 more quantum piece.");
		
		panelsadded = true;
		validate();
		mainPanel.repaint();
	}
	
	public static void main ( String[] args )
	{
		new GameGUI(9,2);
	}
}