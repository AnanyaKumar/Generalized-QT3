import java.util.*;

public class TextGame
{
	static int blength;
	
	public static void main ( String[] args )
	{
		Scanner sc = new Scanner(System.in);
		blength = sc.nextInt();
		int nump = sc.nextInt();
		Board b = new Board(blength*blength,nump);
		
		while ( !b.gameOver() )
		{
			printBoard(b);
			//System.out.println(CountGameTree.getCount(4));
			
			if ( true )
			{
				System.out.println("Enter your move: ");
				if ( b.addPiece(sc.nextInt()-1) ) System.out.println("Move added!\n");
				else System.out.println("Invalid move. Please try again!\n");
			}
			
			else 
			{
				System.out.println("Enter collapse square: ");
				b.collapse(sc.nextInt()-1);
			}
		}
		
		printBoard(b);
		System.out.println("Winner: " + b.getWinner());
	}
	
	public static void printBoard ( Board b )
	{
		int i, j, k, l;
		int tmppiece;
		int tmpchar;
		
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