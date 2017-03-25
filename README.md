# HackBloomberg2K17Movement
A solution to a variation of the HackBloomberg Spaceship Movement problem

Basic Gist:
-We only have control of the ship's thrusters, which determines acceleration (which is a vector)
-In the same tick that we set acceleration, the ship moves according to prior velocity (which was affected by previous acceleration function call)

Bloomberg's Version: Bloomberg has friction for velocity, thereby determining terminal velocity.
Potential Solution: One extra stage would have to be implemented to completely stop the ship when velocity is smaller than acceleration value before the "Stage 3" jump

Bugs:
  -ship does not calibrate correctly when initial velocity is set towards certain directions
