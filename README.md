# Taiko-no-Tatsujin
Fall 2022 ECE 188 Interactive System Bakeoff Project
## Group memebers:
Hongchang Kuang, Jiahe Yan, Ruichen Zheng
## Score:
96% in demo day accuracy test
## Introduction:
A reaction-time-based rythem game. Player has to move the piezo sensor according to the moving instruction on the screen, including hit and shake, when it enters the target box. Player will gain point if he generates the correct reaction.
## Install:
Pull the repository. Add all the related library in Eclipse Java IDE. Assemble the sensor and connect it to the audio jack. Run ClassifyVibration.
## Relevent techniques:
Signal sampling (nyquist rate, reconstruction, fast fourier transform(FFT)), Machine learning (Weka library, vector support machine, polynomial fitting, classification), Java Multi-threading, Java visualization, piezo sensor (soldering, breadboard circuit design)
