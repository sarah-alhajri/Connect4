
import java.io.*;
import java.util.Arrays;
import java.util.ArrayList;
import java.awt.Point;
import java.util.*;
import java.util.Random;

class State implements Cloneable
{

	int rows, cols;
	char[][] board;
	static final int COLUMN_IS_FULL = -1;

	/* basic methods for constructing and proper hashing of State objects */
	public State(int n_rows, int n_cols){
		this.rows=n_rows;
		this.cols=n_cols;
		this.board=new char[n_rows][n_cols];
		
		//fill the board up with blanks
		for(int i=0; i<n_rows; i++)
			for(int j=0; j<n_cols; j++)
				this.board[i][j]='.';
	}
	
	public boolean equals(Object obj){
		
		State other=(State)obj;
		return Arrays.deepEquals(this.board, other.board);
	}
	
	public int hashCode(){
		String b="";
		for(int i=0; i<board.length; i++)
			b+=String.valueOf(board[0]);
		return b.hashCode();
	}

	public Object clone() throws CloneNotSupportedException {
        State new_state=new State(this.rows, this.cols);
		for (int i=0; i<this.rows; i++)
			new_state.board[i] = (char[]) this.board[i].clone();
		return new_state;
	}
	
	
	
	/* returns a list of actions that can be taken from the current state
	actions are integers representing the column where a coin can be dropped */
	public ArrayList<Integer> getLegalActions(){
		ArrayList<Integer> actions=new ArrayList<Integer>();
		for(int j=0; j<this.cols; j++)
			if(this.board[0][j]=='.')
				actions.add(j);
		return actions;
	}
	
	/* returns a State object that is obtained by the agent (parameter)
	performing an action (parameter) on the current state */
	public State generateSuccessor(char agent, int action) throws CloneNotSupportedException{
		
		int row;
		for(row=0; row<this.rows && this.board[row][action]!='X' && this.board[row][action]!='O'; row++);
		State new_state=(State)this.clone();
		new_state.board[row-1][action]=agent;
		
		return new_state;
	}
	
	/* Print's the current state's board in a nice pretty way */
	public void printBoard(){
		System.out.println(new String(new char[this.cols*2]).replace('\0', '-'));
		for(int i=0; i<this.rows; i++){
			for(int j=0; j<this.cols; j++){
				System.out.print(this.board[i][j]+" ");
			}
			System.out.println();
		}	
		System.out.println(new String(new char[this.cols*2]).replace('\0', '-'));
	}
	
	/* returns True/False if the agent(parameter) has won the game
	by checking all rows/columns/diagonals for a sequence of >=4 */
	public boolean isGoal(char agent){
	
		String find=""+agent+""+agent+""+agent+""+agent;
		
		//check rows
		for(int i=0; i<this.rows; i++)
			if(String.valueOf(this.board[i]).contains(find))
				return true;
		
		//check cols
		for(int j=0; j<this.cols; j++){
			String col="";
			for(int i=0; i<this.rows; i++)
				col+=this.board[i][j];
				
			if(col.contains(find))
				return true;
		}
		
		//check diags
		ArrayList<Point> pos_right=new ArrayList<Point>();
		ArrayList<Point> pos_left=new ArrayList<Point>();
		
		for(int j=0; j<this.cols-4+1; j++)
			pos_right.add(new Point(0,j));
		for(int j=4-1; j<this.cols; j++)
			pos_left.add(new Point(0,j));	
		for(int i=1; i<this.rows-4+1; i++){
			pos_right.add(new Point(i,0));
			pos_left.add(new Point(i,this.cols-1));
		}
	
		//check right diags
		for (Point p : pos_right) {
			String d="";
			int x=p.x, y=p.y;
			while(true){				
				if (x>=this.rows||y>=this.cols)
					break;
				d+=this.board[x][y];
				x+=1; y+=1;
			}
			if(d.contains(find))
				return true;
		}
		
		//check left diags
		for (Point p : pos_left) {
			String d="";
			int x=p.x, y=p.y;
			while(true){
				if(y<0||x>=this.rows||y>=this.cols)
					break;
				d+=this.board[x][y];
				x+=1; y-=1;
			}
			if(d.contains(find))
				return true;
		}
		
		return false;
		
	}
	
	

	/* returns the value of each state for minimax to min/max over at
	zero depth.  */
	public double evaluationFunction(){
	
		if (this.isGoal('O'))
			return 1000.0;
		if (this.isGoal('X'))
			return -1000.0;
		
		return 0.0;
	}
	
	
	
	
	
	
	
	


}
// MINIMAX 
class minimaxAgent {
	/* Variable that holds the maximum depth the MiniMax algorithm will reach for this player. */ 
	int depth;
    /* Variable that holds the action that the MiniMax agent will prform. */ 
	int action=0;

	/* Initialize the minimax agent at the given depth. */ 
	public minimaxAgent(int d)
	{
		depth = d;
	}	
    /* Returns an action to the minimax agent. */
	public int Action(State s) throws CloneNotSupportedException
	{
		
		max(s, depth);
		return action;
		
	}
	/* The max and min functions are called interchangingly, one after another until the depth 0 is reached. */
	public double max(State s, int d) throws CloneNotSupportedException
	{
		ArrayList<Integer> children = new ArrayList<Integer>();
		/* If max is called after depth zero is reached,
	     then a heuristic is calculated on the state and the action returned. */
		if(d == 0 )
		return s.evaluationFunction();
		
			/* The children-actions of the state are calculated. */ 
		children = s.getLegalActions();
		double value = Integer.MIN_VALUE;
		
		double max;
	
		for(int i = 0; i<children.size();i++)
		{
			/* And for each child min is called. */
			max= min(s.generateSuccessor('O',children.get(i)),d);
			/* The child-action with the greatest value is selected and returned by max. */
			if(max >= value)
			{
				this.action = i;		
					value = max;	
			} 
	
	}
		

		return value; 
		
	}
	/* min works as max. */
	public double min(State s, int d) throws CloneNotSupportedException
	{

		ArrayList<Integer> children = new ArrayList<Integer>();
		if(d == 0)
		return s.evaluationFunction();
		
		
		children = s.getLegalActions();
		
		double value = Integer.MAX_VALUE;
		
		double min;
		for(int i =0; i<children.size();i++)
		{
			
			min= max(s.generateSuccessor('X',children.get(i)),d-1);
			if(min <= value)
				value = min;	
	

		
		
		}
		return value;
		
	}
	
	
	
}

// ALPHA-BETA
class alphabetaAgent{
	
	int depth;
	int action=0;
	double alpha, beta; 
	public alphabetaAgent(int d)
	{
		depth = d;
		alpha= Integer.MIN_VALUE;
		beta = Integer.MAX_VALUE;
	}	
	
	public int Action(State s) throws CloneNotSupportedException
	{
		 max(s, depth, alpha, beta);
        
		return action;
		
	}
	
	public double max(State st, int d, double alpha, double beta) throws CloneNotSupportedException
	{

		ArrayList<Integer> children = new ArrayList<Integer>();
		if(d ==0)
		return st.evaluationFunction();
	
		children = st.getLegalActions();
		double value = Integer.MIN_VALUE;
		
		double max;
	
		for(int i =0; i<children.size();i++)
		{
			max = min(st.generateSuccessor('O',children.get(i)),d, alpha, beta);
			
			if(max >= value)
			{
				this.action = i;       
					value = max;

		

		}
	     

		if (value>= beta) 
		return value;  
	   // prune
alpha = Math.max(alpha, value);

		
		
		
			
}
			
		

		return value;
		
	}
	
	public double min(State s, int d, double alpha, double beta) throws CloneNotSupportedException
	{
	
		ArrayList<Integer> children = new ArrayList<Integer>();
		if(d == 0)
		return s.evaluationFunction();
		
		children = s.getLegalActions();
		
		double value = Integer.MAX_VALUE;
		
		double min;
		for(int i =0; i<children.size();i++)
		{
			
			min = max(s.generateSuccessor('X',children.get(i)),d-1, alpha, beta);
			if(min <= value) {
		
			
		
			
				value = min; 
	
		

		
	
			}
		
			if (value <= alpha)
			return value; 
			beta = Math.min(beta, value);
		
		
		}
		return value;
		
	}
	
	
	
}



public class connect4AI{

	public static void main(String[] args) throws CloneNotSupportedException{
		Scanner in = new Scanner(System.in); 
		int choice=0; 
		long start, end; 
		do { 
			System.out.println("Choose Your Fighter: "); 
			System.out.println("1. Minimax Agent VS Random Agent");
			System.out.println("2. Minimax Agent VS You");
			System.out.println("3. Alpha-beta Agent VS Random Agent"); 
			System.out.println("4. Alpha-beta Agent VS You");
			System.out.println("5. Exit"); 
			choice = in.nextInt();
switch(choice) {
	case(1):
	start = System.currentTimeMillis(); 
	MinmaxPlay();
	end = System.currentTimeMillis();
	System.out.println("Execution time: " + (end - start) + " miliseconds.");
	break;
	case(2):
	start = System.currentTimeMillis();
	MinmaxPlayVSu(); 
	end = System.currentTimeMillis();
	System.out.println("Execution time: " + (end - start) + " miliseconds.");
	break; 
	case(3):
	start = System.currentTimeMillis();
	AlphaBetaPlay();
	end = System.currentTimeMillis(); 
	System.out.println("Execution time: " + (end - start) + " miliseconds.");
	break; 
	case(4):
	start = System.currentTimeMillis(); 
	AlphaBetaPlayVSu(); 
	end = System.currentTimeMillis(); 
	System.out.println("Execution time: " + (end - start) + " miliseconds.");
	break; 
	case(5):
	System.out.println("Thank You!");
	break; 

default:
System.out.println("Invalid Choice!");

}

		} while(choice != 5);
	 }


		static void MinmaxPlay() throws CloneNotSupportedException { 
			int depth;
			Scanner in = new Scanner(System.in); 
		
			System.out.println("Enter the depth. NOTE: (depth<4) for large depth the time would be too high:");
			depth = in.nextInt(); 
				
					
				minimaxAgent MiniMax = new minimaxAgent(depth);
				State s=new State(6,7);

				while(true){
					int action = MiniMax.Action(s);
		
					s = s.generateSuccessor('O', action);
					s.printBoard();
					//check if O won?
					if(s.isGoal('O'))
					break;
					Random ran = new Random();
					int random_move = ran.nextInt(7);
					  s = s.generateSuccessor('X', random_move);
					  s.printBoard();
					  //check if x won?
					  if(s.isGoal('X'))
					  break;
					//pause
		
		}
	}
	static void MinmaxPlayVSu() throws CloneNotSupportedException { 
		int depth;
		Scanner in = new Scanner(System.in); 
	
		System.out.println("Enter the depth. NOTE: (depth<4) for large depth the time would be too high:");
		depth = in.nextInt(); 
			
				
			minimaxAgent MiniMax = new minimaxAgent(depth);
			State s=new State(6,7);
			while(true){
				int action = MiniMax.Action(s);
	
				s = s.generateSuccessor('O', action);
				s.printBoard();
				//check if O won?
				if(s.isGoal('O'))
				break;
				System.out.println("choose your move: ");
		int enemy_move = in.nextInt();
		s = s.generateSuccessor('X', enemy_move);
				  s.printBoard();
				  //check if x won?
				  if(s.isGoal('X'))
				  break;
				//pause
	
	}
}
	static void AlphaBetaPlay() throws CloneNotSupportedException { 
		int depth;
		Scanner in = new Scanner(System.in); 
		
		System.out.println("Enter the depth. NOTE: (depth<4) for large depth the time would be too high: ");
		depth = in.nextInt(); 
			
				
			alphabetaAgent ALPHABETA = new alphabetaAgent(depth);
			State s=new State(6,7);
			while(true){
				int action = ALPHABETA.Action(s);
	
				s = s.generateSuccessor('O', action);
				s.printBoard();
				//check if O won?
				if(s.isGoal('O'))
				break;
				Random ran = new Random();
				int random_move = ran.nextInt(7);
				  s = s.generateSuccessor('X', random_move);
				  s.printBoard();
				  //check if x won?
				  if(s.isGoal('X'))
				  break;
				//pause
	
	}
	}
	static void AlphaBetaPlayVSu() throws CloneNotSupportedException { 
		int depth;
		Scanner in = new Scanner(System.in); 
		
		System.out.println("Enter the depth. NOTE: (depth<4) for large depth the time would be too high: ");
		depth = in.nextInt(); 
			
				
			alphabetaAgent ALPHABETA = new alphabetaAgent(depth);
			State s=new State(6,7);
			while(true){
				int action = ALPHABETA.Action(s);
	
				s = s.generateSuccessor('O', action);
				s.printBoard();
				//check if O won?
				if(s.isGoal('O'))
				break;
				System.out.println("choose your move: ");
		int enemy_move = in.nextInt();
		s = s.generateSuccessor('X', enemy_move);
				  s.printBoard();
				  //check if x won?
				  if(s.isGoal('X'))
				  break;
				//pause
	
	}
	}
} 
