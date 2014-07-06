public class GetMove
{
	double maxWin = 2.1;
	int m1, m2, c;
	
	public GetMove () {}
	
	public int getMove1 () { return m1; }
	public int getMove2 () { return m2; }
	public int getCollapse () { return c; }
	
	public double computeMove ( Board B, double max ) //throws java.lang.Exception
	{
		double winStatus = -maxWin;
		double t1, t2;
		int tm1 = -1, tm2 = -1, tc = -1;
		
		if ( B.gameOver() ) winStatus = -B.getWinner();
		
		else
		{
			if ( B.isEntangled() ) //Scan both collapse choices
			{
				B.collapse(0,true);
				t1 = -computeMove(B,max);
				B.undoCollapse();
				if ( t1 >= max ) { tc = 0; winStatus = t1; } //alpha-beta pruning
				
				else
				{
					B.collapse(1,true);
					t2 = -computeMove(B,max);
					B.undoCollapse();
					
					if ( t2 > t1 ) { tc = 1; winStatus = t2; }
					else { tc = 0; winStatus = t1; }
				}
			}
			
			else
			{
				int i, j;
				
				for ( i = 0; i < B.numSquares; i++ )
				{
					if ( winStatus >= max ) { winStatus = maxWin; break; } //alpha beta pruning
					
					if ( B.howManyPieces() == 1 && B.getTurn() == B.numSquares-1 )
					{
						if ( B.addPiece(i) )
						{
							t1 = computeMove(B,-winStatus);
							if ( t1 > winStatus ) { winStatus = t1; tm1 = i; tm2 = -1; } //Do not change to >= or alpha-beta pruning shall fail
							B.undoCollapse();
							B.undoAdd();
						}
					}
					
					else
					{
						for ( j = i+1; j < B.numSquares; j++ )
						{
							if ( winStatus >= max ) { winStatus = maxWin; break; } //alpha beta pruning
							
							if ( B.addPiece(i) )
							{
								if ( B.addPiece(j) )
								{
									t1 = computeMove(B,-winStatus);
									if ( t1 > winStatus ) { winStatus = t1; tm1 = i; tm2 = j; } //Do not change to >= or alpha-beta pruning shall fail
									B.undoAdd();
								}
								
								B.undoAdd();
							}
						}
					}
				}
			}
		}
		
		c = tc;
		m1 = tm1;
		m2 = tm2;
		return -winStatus;
	}
}