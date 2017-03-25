
public class Status {
	public class Mine{
		String owner;
		float x, y;	
		public float distSquared(float cx, float cy){
			return (float)(Math.pow(cx - x, 2) + Math.pow(cy - y, 2));
		}
	}
	public class Player{
		float x, y, dx, dy;
	}
	public class Bomb{
		float x, y;
	}
	public float x, y;
	public float dx, dy;
	public int num_mines;
	public Mine[] mines;
	public int num_players;
	public Player[] players;
	public int num_bombs;
	public Bomb[] bombs;
	
	public Mine closestMine(float x, float y){
		if (mines.length > 0){
			Mine ret = mines[0];
			float dist = ret.distSquared(x, y);
			for (int i = 0; i < mines.length; ++i){
				if (mines[i].distSquared(x,y) < dist){
					ret = mines[i];
					dist = mines[i].distSquared(x, y);
				}
			}
			return ret;
		}
		return null;
	}
	public boolean Parse(String raw, boolean statusPull){
		String[] raw_parts = raw.split("\\s+");
		if (raw_parts.length > 7){
			int delta = 1;
			if (statusPull){
				this.x = Float.parseFloat(raw_parts[delta++]);
				this.y = Float.parseFloat(raw_parts[delta++]);
			
				this.dx = Float.parseFloat(raw_parts[delta++]);
				this.dy = Float.parseFloat(raw_parts[delta++]);
			}
			delta++;
			this.num_mines = Integer.parseInt(raw_parts[delta++]);
			this.mines = new Mine[this.num_mines];
			for (int i = 0; i < this.num_mines; ++i){
				this.mines[i] = new Mine();
				this.mines[i].owner = raw_parts[delta++];
				this.mines[i].x = Float.parseFloat(raw_parts[delta++]);
				this.mines[i].y = Float.parseFloat(raw_parts[delta++]);
			}
			
			delta++;
			this.num_players = Integer.parseInt(raw_parts[delta++]);
			this.players = new Player[this.num_players];
			for (int i = 0; i < this.num_players; ++i){
				this.players[i] = new Player();
				this.players[i].x = Float.parseFloat(raw_parts[delta++]);
				this.players[i].y = Float.parseFloat(raw_parts[delta++]);
				
				this.players[i].dx = Float.parseFloat(raw_parts[delta++]);
				this.players[i].dy = Float.parseFloat(raw_parts[delta++]);
			}
			
			delta++;
			this.num_bombs = Integer.parseInt(raw_parts[delta++]);
			this.bombs = new Bomb[this.num_bombs];
			for (int i = 0; i < this.num_bombs; ++i){
				this.bombs[i] = new Bomb();
				this.bombs[i].x = Float.parseFloat(raw_parts[delta++]);
				this.bombs[i].y = Float.parseFloat(raw_parts[delta++]);
			}
			return true;
		}
		return false;
	}
}
