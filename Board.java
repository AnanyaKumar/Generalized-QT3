/**
 *
 * Author: Ananya Kumar
 * Institution: Carnegie Mellon University
 *
 * The Board class represents the Board data structure, representing the core of this project. Includes
 * methods to add pieces, find entanglement, collapse entanglements, evaluate the winner.
 *
 * Note all data structures are 0-based. For displaying purposes you (probably) need to transform them.
 * Eg. Player 1s pieces are 0, 2, 4, ...
 *
 **/

import java.util.Collections;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;

public class Board
{
	ArrayList<Integer>[] b; //b[i] stores the list of pieces currently on the i^th square
	boolean[] isClassical; //isCLassical[i] = is square i is classical?
	ArrayList<Integer>[] p; //p[i] stores the list of squares the i^th piece was played on
	Stack<ArrayList<Integer>> strace; //Top of stack stores latest pieces that have been collapsed
	
	int numSquares; //Number of squares in the game board
	int piecesPerTurn; //Pieces played per turn at start of game
	int turn; //The current turn number (0-based)
	int squaresLeft; //Number of non-classical squares on the board
	boolean cycle; //Is there an entanglement
	boolean justCounting; //True if this board is used only for game tree enumeration
	
	public Board ( int m, int n ) //Initialize the board to have m squares and n pieces played per turn
	{
		b = new ArrayList[m];
		for ( int i = 0; i < m; i++ ) b[i] = new ArrayList<Integer>();
		isClassical = new boolean[m];
		Arrays.fill(isClassical,false);
		p = new ArrayList[m];
		for ( int i = 0; i < m; i++ ) p[i] = new ArrayList<Integer>();
		strace = new Stack<ArrayList<Integer>>();
		
		numSquares = m;
		piecesPerTurn = n;
		turn = 0;
		squaresLeft = numSquares;
	}
	
	public Board ( int m, int n, boolean countop ) //Additional option, true if board is being used only for counting
	{
		this(m,n);
		justCounting = countop;
	}
	
	public Board ( int m ) { this(m,2); } //Initialize the board to have m squares and 2 pieces played per turn
	public Board () { this(9,2); } //Initialize the board to have 9 squares and 2 pieces played per turn
	
	/******************************************
	 * Methods for adding pieces onto the board
	 ******************************************/
	
	public int howManyPieces () //How many pieces should a player play per turn
	{
		if ( squaresLeft < piecesPerTurn ) return 1;
		else return piecesPerTurn;
	}
	
	public int howManyPiecesLeft () //How many pieces are yet to be played in the current turn
	{
		return howManyPieces() - p[turn].size();
	}
	
	public boolean addPiece ( int square ) //Add quantum piece to specified square, if move is valid
	{
		if ( isClassical[square] ) return false;
		for ( int i = 0; i < p[turn].size(); i++ ) if ( square == p[turn].get(i) ) return false;
		p[turn].add(square);
		b[square].add(turn);
		if ( p[turn].size() == howManyPieces() ) finalizeMove();
		return true;
	}
	
	private void finalizeMove () //When all quantum moves this turn have been played, the move will be finalized
	{
		strace.push(new ArrayList<Integer>());
		if ( p[turn].size() == 1 ) collapseTo(turn,p[turn].get(0));
		turn++;
		checkEntanglement();
	}
	
	public void undoAdd () //Undo the addition of one of the moves (without undoing collapse)
	{
		if ( turn == 0 && p[turn].size() == 0 ) return;
		
		if ( turn >= numSquares || p[turn].size() == 0 )
		{
			turn--;
			strace.pop();
		}
		
		ArrayList<Integer> cursqr = b[p[turn].get(p[turn].size()-1)];
		cursqr.remove(cursqr.size()-1);
		p[turn].remove(p[turn].size()-1);
		cycle = false;
	}
	
	/**
	 * Methods for checking whether there is an entanglement. Consider moving these to separate class.
	 **/
	
	public boolean isEntangled () //Return true if and only if there is an entanglement in the board
	{
		return cycle;
	}
	
	private void checkEntanglement () //Checks if there is an entanglement in the board
	{
		if ( piecesPerTurn == 2 ) checkCycleEntanglement();
		else if ( justCounting ) checkCountingEntanglement();
        else checkMatchingEntanglement();
	}
	
	private void checkCycleEntanglement () //Checks if there is a cyclic entanglement in the board (piecesPerTurn must be 2)
	{
		cycle = false;
		if ( turn == 0 ) return;
		if ( isClassical[p[turn-1].get(0)] ) return; //If it has been collapsed then there isn't a cycle
		cycleEntanglement(turn-1,p[turn-1].get(0),new boolean[numSquares]);
	}
	
	private void cycleEntanglement ( int cpiece, int csquare, boolean[] visited ) //Recursive depth-first search function
	{
		if ( visited[csquare] ) { cycle = true; return; }
		ArrayList<Integer> sqr = b[csquare];
		int npiece, nsquare;
		visited[csquare] = true;
		
		for ( int i = 0; i < sqr.size(); i++ )
		{
			npiece = sqr.get(i);
			if ( npiece == cpiece ) continue;
			nsquare = p[npiece].get(0);
			if ( nsquare == csquare ) nsquare = p[npiece].get(1);
			cycleEntanglement(npiece,nsquare,visited);
			if ( cycle ) break;
		}
	}
    
  private void checkCountingEntanglement ()
  {
      
  }
	
	private void checkMatchingEntanglement () //Custom algorithm to find if there is an entanglement (got any piecesPerTurn value)
	{
		
	}
	
	/**
	 * Methods for collapsing an entanglement. Only works for 2-pieces per turn at the moment
	 **/
	
	public boolean collapse ( int sqrnum ) //Main collapse function: to collapse the latest piece to sqrnum
	{
        boolean validCollapse = false;
        
        for (int i = 0; i < p[turn-1].size(); i++) 
        {
            if (p[turn-1].get(i) == sqrnum) validCollapse = true;
        }
        
        if (validCollapse) {            
            cycleCollapse(turn-1,sqrnum);
            cycle = false;
        }
        
        return validCollapse;
	}
	
	public void collapse ( int piecenum, boolean extra ) //This collapse method allows you to scan through all possible collapse options for the latest piece by specifying piecenum = 1...n
	{
		cycleCollapse(turn-1,p[turn-1].get(piecenum));
		cycle = false;
	}
	
	private void cycleCollapse ( int cpiece, int csquare ) //Recursive method for collapse
	{
		if ( isClassical[csquare] ) return;
		(strace.peek()).add(cpiece);
		isClassical[csquare] = true;
		squaresLeft--;
		ArrayList<Integer> sqr = b[csquare];
		int newpiece, newsqr;
		
		for ( int i = 0; i < sqr.size(); i++ )
		{
			newpiece = sqr.get(i);
			if ( newpiece == cpiece ) continue;
			else newsqr = p[newpiece].get(0);
			if ( newsqr == csquare ) newsqr = p[newpiece].get(1);
			cycleCollapse(newpiece,newsqr);
		}	
		
		b[csquare] = new ArrayList<Integer>();
		b[csquare].add(cpiece);
	}
	
	private void collapseTo ( int pnum, int sqrnum ) //Collapse piece pnum to square sqrnum (depracated)
	{
		if ( isClassical[sqrnum] ) return;
		b[sqrnum] = new ArrayList<Integer>();
		b[sqrnum].add(pnum);
		(strace.peek()).add(pnum);
		isClassical[sqrnum] = true;
		squaresLeft--;
	}
	
	public boolean hasCollapsed () //Has a collapse just occured?
	{
		if ( strace.empty() ) return false;
		else return (turn == numSquares || (p[turn].size() == 0)) && ((strace.peek()).size() > 0) && isClassical[p[strace.peek().get(0)].get(0)];
	}
	
	public void undoCollapse () //Undo the collapse that occured at start of the current turn (if there was no collapse it does nothing)
	{
		ArrayList<Integer> collapsed = strace.peek();
		if ( collapsed.size() > 0 ) cycle = true;
		Collections.sort(collapsed);
		int i, j, m, curp;
		
		for ( i = 0; i < collapsed.size(); i++ ) //For each piece
		{
			curp = collapsed.get(i); //Get the piece index
			
			for ( j = 0; j < p[curp].size(); j++ ) //For each square piece was placed on
			{
				m = p[curp].get(j); //Find the square index
				
				if ( isClassical[m] ) //Restore the square if not done so before
				{
					isClassical[m] = false;
					b[m].remove(b[m].size()-1);
					squaresLeft++;
				}
				
				b[m].add(curp); //Restore the piece to the square
			}
		}
		
		(strace.peek()).clear();
	}
	
	/*******************
	 * Utility methods
	 *******************/
	
	public int getNumSquares ()
	{
		return numSquares;
	}
	
	public int numSquarePieces ( int sqrnum ) //Returns the number of pieces on the square
	{
		if ( sqrnum < 0 || sqrnum >= numSquares ) return 0;
		else return b[sqrnum].size();
	}
	
	public boolean isSquareClassical ( int sqrnum )
	{
		if ( sqrnum < 0 || sqrnum >= numSquares ) return false;
		return isClassical[sqrnum];
	}
	
	public int pieceAt ( int sqrnum, int subsqrnum )
	{
		if ( sqrnum < 0 || sqrnum >= numSquares ) return -1;
		if ( subsqrnum >= (b[sqrnum]).size() ) return -1;
		else return (b[sqrnum]).get(subsqrnum);
	}
	
	public int getTurn ()
	{
		return turn;
	}
	
	public boolean gameOver () //Returns true iff the game is over
	{
		return squaresLeft == 0;
	}
	
	public double getWinner () //Returns player 1's score - player 2's score
	{
		ArrayList<Integer> p1lines = new ArrayList<Integer>();
		ArrayList<Integer> p2lines = new ArrayList<Integer>();
		int pline;
		int maxTurn;
		int curpiece;
		int i, j;
		double winMargin = 0;
		int n = (int)(Math.sqrt(numSquares));
		
		for ( i = 0; i < n; i++ ) //There are actually more elegant ways to do this, but they are slower
		{
			pline = 0;
			maxTurn = 0;
			
			for ( j = 0; j < n; j++ )
			{
				curpiece = (b[i*n+j]).get(0);
				maxTurn = Math.max(curpiece,maxTurn);
				pline += curpiece%2;
				if ( 0 < pline && pline <= j ) break;
			}
			
			if ( pline == 0 ) p1lines.add(maxTurn);
			else if ( pline == n ) p2lines.add(maxTurn);
			
			pline = 0;
			maxTurn = 0;
			
			for ( j = 0; j < n; j++ )
			{
				curpiece = (b[i+j*n]).get(0);
				maxTurn = Math.max(curpiece,maxTurn);
				pline += curpiece%2;
				if ( 0 < pline && pline <= j ) break;
			}
			
			if ( pline == 0 ) p1lines.add(maxTurn);
			else if ( pline == n ) p2lines.add(maxTurn);
		}
		
		pline = 0;
		maxTurn = 0;
		
		for ( j = 0; j < n; j++ )
		{
			curpiece = (b[j*n+j]).get(0);
			maxTurn = Math.max(curpiece,maxTurn);
			pline += curpiece%2;
			if ( 0 < pline && pline <= j ) break;
		}
		
		if ( pline == 0 ) p1lines.add(maxTurn);
		else if ( pline == n ) p2lines.add(maxTurn);
		
		pline = 0;
		maxTurn = 0;
		
		for ( j = 0; j < n; j++ )
		{
			curpiece = (b[j*n+n-j-1]).get(0);
			maxTurn = Math.max(curpiece,maxTurn);
			pline += curpiece%2;
			if ( 0 < pline && pline <= j ) break;
		}
		
		if ( pline == 0 ) p1lines.add(maxTurn);
		else if ( pline == n ) p2lines.add(maxTurn);
		p1lines.add(1000000);
		p2lines.add(1000000);
		Collections.sort(p1lines);
		Collections.sort(p2lines);
		i = j = pline = 0;
		maxTurn = -1;
		
		while ( i+j < p1lines.size() + p2lines.size() - 2 )
		{
			if ( p1lines.get(i) <= p2lines.get(j) )
			{
				if ( p1lines.get(i) > maxTurn ) pline++;
				winMargin += 1.0/pline;
				maxTurn = p1lines.get(i);
				i++;
			}
			
			else if ( p2lines.get(j) < p1lines.get(i) )
			{
				if ( p2lines.get(j) > maxTurn ) pline++;
				winMargin -= 1.0/pline;
				maxTurn = p2lines.get(j);
				j++;
			}
		}
		
		return winMargin;
	}	
}
