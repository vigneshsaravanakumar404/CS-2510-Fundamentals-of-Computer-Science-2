How to Run:
 - Run with the tester and javalib library
 - Use the Main class as the argument to the tester
 - The program should run and print the results of the tests
 - Make sure the path of the image files are correct if there are errors

 How to Play:
 - Move using the array keys (up, down, left, right)
 - The goal is to eat as much fish as possible and become the biggest fish
 - You can loop around the left and right edges of the screen
 - Fish bigger than you will eat you, so avoid them
 - Collect speed boosts to move faster


 Extra Credit Features:
 - Fish can move in 2 directions at the same time
    - Kept track through many variables for which keys are pressed
 - Fish has inertia and continues moving in the last direction pressed for a short time
    - Coded through some basic physics (Frication, force, acceleration)
 - The bigger the fish it is, the slower it moves
    - Coded through some basic physics (Force, mass, acceleration)
 - Kept track of score
    - Simple text image to keep track of the score
    - Little fish have lower score and bigger fish have higher score
 - Add speed snacks
    - Added speed snacks with a timer that counts down till the speed boost is over
    - Speed boosts have glowing radiating effect
 - Added multiple lives
    - You can die 3 times before the game is over
    - Lives are in the top right corner of the screen
 - Wave Movement for fish
    - Fish move in a wave pattern to make it more realistic
    - USed Math.sin() to create the wave effect
 - Logarithm Game Progression
    - The more you eat the harder it is to grow
    - Mass grows more slowly as you eat more fish
 - FPS
    - The game runs at a fixed frame rate of 144 FPS and optimized to run at that speed or better
    - Change tick rate to get betetr fps