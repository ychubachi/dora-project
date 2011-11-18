DoraDroid 

This is Android program as step 1 of DORA project.
It does the same tasks as RobotSample and RobotSampleAndroid do.

How to build:
===========
The project is built by itself, no additional project is required. All necessary libs are already included. There might be some unnecessary, I haven't checked yet.

How to run:
============
1. run SampleService
2. run DoraDroid. it will connect to SampleService via Internet (IP address is hardcoded) and ask user to confirm robot address before connecting to robot via Bluetooth (should it be hardcoded? I'm not sure).
3. Use robotcamera, contentsupload, robotcontrol pages from a browser to control the robot (the same way we deal with RobotSample and RobotSampleAndroid)

To-do's
========
- new logo to replace MINDDroid's logo
- change SplashMenu name to some Dora-typed name
- remove unused resources
- make the back button work