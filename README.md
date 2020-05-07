## RCSS2D Sim Code
Java code to communicate with the RCSS server

[Github with all the resources](https://rcsoccersim.github.io/)  
[Server Download](https://github.com/rcsoccersim/rcssserver/releases)  
[Moniter Download](https://github.com/rcsoccersim/rcssmonitor/releases)  
[Server Manual](https://rcsoccersim.github.io/rcssserver-manual-20030211.pdf)  

## Dependencies  
* g++  
* make  
* boost  
    `    sudo apt-get boost`
* flex  
    `    sudo apt-get flex`
* bison  
    `    sudo apt-get bison`
  

## Installation  
*  After dependencies installed, download the latest release of server and monitor
*  Extract the files
*  Run `./configure` and `make` in the respective directories  
  *  This will create env variables to run the server and monitor. Potential issues here might be that make might not have proper permissions and can be fixed by adding `sudo` permissions

## Using the program
* Start the server by running `rcssserver`
* (optional) start monitor by running `monitor`
* Run the project (main currently in tester.java)

## Notes  
* The program will create a player that can be controlled by command line input in format of `command parameter_1 parameter_2 ...`  
  Also a JPanel for the player's view is created to see what the player is seeing  
  
* Installation of the RoboCup Server also installs an `rcssclient` which is also a command line parser for the robocup server.  
  This takes in direct commands in the form shown on the Server Manual (i.e. `(init team1 (version 15))` or `(move 0 0)`)  
  
* Installation instructions based on use on Ubuntu and can differ based on OS. Doesn't seem to be a version of the RCSS server for Windows at the moment  
