
public class Config {
	public int mapwidth, mapheight;
	public float capture_radius;
	public float vision_radius;
	public float friction;
	public float brake_friction;
	public float bomb_place_radius;
	public float bomb_effect_radius;
	public int bomb_delay;
	public float bomb_power;
	public float scan_radius;
	public int scan_delay;
	
	public boolean Parse(String raw){
		String[] raw_parts = raw.split("\\s+");
		if (raw_parts.length == 25){
			int delta = 2;
			this.mapwidth = Integer.parseInt(raw_parts[delta++]);
			
			delta++;
			this.mapheight = Integer.parseInt(raw_parts[delta++]);
			
			delta++;
			this.capture_radius = Float.parseFloat(raw_parts[delta++]);
			
			delta++;
			this.vision_radius = Float.parseFloat(raw_parts[delta++]);
			
			delta++;
			this.friction = Float.parseFloat(raw_parts[delta++]);
			
			delta++;
			this.brake_friction = Float.parseFloat(raw_parts[delta++]);
			
			delta++;
			this.bomb_place_radius = Float.parseFloat(raw_parts[delta++]);
			
			delta++;
			this.bomb_effect_radius = Float.parseFloat(raw_parts[delta++]);
			
			delta++;
			this.bomb_delay = Integer.parseInt(raw_parts[delta++]);
			
			delta++;
			this.bomb_power = Float.parseFloat(raw_parts[delta++]);
			
			delta++;
			this.scan_radius = Float.parseFloat(raw_parts[delta++]);
			
			delta++;
			this.scan_delay = Integer.parseInt(raw_parts[delta++]);
			return true;
		}
		return false;
	}
}
