import java.util.Scanner;

public class CountGameTree
{	
	static int numSquares;
	
	public static int getCount ( int n )
	{
		numSquares = n;
		return count(0,new Board(numSquares));
	}
	
	public static int count ( int turn, Board b )
	{
		if ( turn == numSquares ) return 1;
		if ( b.isEntangled() ) b.collapse(turn-1);
		int curcount = 0;
		
		if ( b.howManyPieces() == 1 )
		{
			b.addPiece(turn);
			curcount += count(turn+1,b);
			b.undoCollapse();
			b.undoAdd();
			//b.clearMove();
		}
		
		else
		{
			for ( int i = 0; i < numSquares; i++ )
			{
				//if ( i == turn ) continue;
				b.addPiece(turn);
					
				if ( b.addPiece(i) )
				{
					curcount += count(turn+1,b);
					b.undoCollapse();
					b.undoAdd();
				}	
				
				b.undoAdd();
			}
		}
		
		return curcount;
	}
	
	public static void main ( String[] args )
	{
		Scanner sc = new Scanner(System.in);
		int slength = sc.nextInt();
		System.out.println(getCount(slength) + " * " + slength + "!");
	}
	
	public static void printBoard ( Board b )
	{
		int i, j, k, l;
		int tmppiece;
		int tmpchar;
		int blength = (int)(Math.ceil(Math.sqrt(b.numSquares)));
		
		System.out.println("Board at turn " + b.getTurn() + ":");
		
		for ( i = 0; i < blength; i++ )
		{
			for ( j = 0; j < blength; j++ )
			{
				for ( k = 0; k < blength; k++ )
				{
					for ( l = 0; l < blength; l++ )
					{
						tmppiece = b.pieceAt( blength*i+k, blength*j+l ) + 1;
						if ( tmppiece != 0 )
						{
							if ( tmppiece % 2 == 0 ) System.out.print("O");
							else if ( tmppiece % 2 == 1 ) System.out.print("X");
							if ( tmppiece >= 10 ) System.out.print( tmppiece + " " );
							else System.out.print( tmppiece + "  " );
						}
						else System.out.print("    ");
					}
					
					if ( k != blength-1 ) System.out.print("|");
				}
				System.out.println();
			}
			
			if ( i != blength-1 )
			{
				for ( j = 0; j < 4*blength*blength + blength - 1; j++ )
					System.out.print("-");
				System.out.println();
			}
		}
	}
	
}