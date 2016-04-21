# ppp
Simple 2D Path Planning Problem Generator

github.com/slw546/ppp

My version of this project is in PPP/src.
Wei's version is in PPP/Old_Code and is split into multiple versions.

Generator

Open main.Main and run the tournament function, or generate a single PPP via singlePPP.

Simulator
Open sim.Main. All maps in a folder can be tested via testMapsInFolder("path/to/folder"). A UPGMA tree for that folder can be created via runUPGMA("path/to/folder"). Results and UPGMA data structures are written to the folder.

Alternatively, load a single PPP via loadPPP, instantiate a Bot to test, then use singleTest(PPP, Bot, verbose, showSteps)

You can try multi-threaded testing via the testThread class, but I never used it so it might not work. For 60 PPPs the tests take ~30 mins, so it might be useful if you want to test several folders at once.

You can also call these functions from the generator to simulate as soon as the PPPs are generated.

Agents
Agents should inherit from the Bot class so the simulator can run them. Override plan and aprioriPlan to define the new agent's path planning alg. If you give the agent more state remember to override reset and make sure it's properly reset between tests.

UPGMA
Use Sim.RunUPGMA to get the json data structures for UPGMA. Then, copy it next to the tree html documents (found under Evaluation) and open it in a browser to see the tree.
