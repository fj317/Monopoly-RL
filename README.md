# Monopoly-RL
A reinforced learning approach to creating an agent to play the board game Monopoly. The agent uses a Monte Carlo tree search algorithm to decide upon what action to perform in each state. This is the code for my 2021/2022 Dissertation project for my final year at the University of Bath.

Due to implementation difficulties and time restrictions placed upon the project, a simplified Monopoly game was created for the MCTS agent. The SimplifiedMonopoly.java class contains the game logic for this simplified game, whilst Monopoly.java holds the full non-simplified game logic. 

To start, compile and run the SimplifiedMonopoly.java file. The default opponent is a random policy agent however this can be changed by changing the getPlayers() method. Please note that if playing with the MCTS agent, it must always be player one as the game logic assumes this is the case. 
There are four different players that can be used: 
*  RandomPolicyPlayer: selects a random action choice at each input state.
*  BallisPlayer: a fixed-policy agent that is built for SimplifiedMonopoly. It always buys a proeprty if it has more than $350, and always attempts to buy out of jail (unless a get out of jail card is held).
*  HumanPlayer: used by human players to interact with the game and play. The console is used to input the action choice of the user.
*  MonteCarloPlayer: agent that uses a MCTS algorithm to select the best possible action given the current state. Note that the agent has two parameters that can be changed as required: exploration (used to state how much exploration versus exploitation should occur); rollouts (the number of simulations that will be performed). Default values of 0.8 and 50,000 are used respectively.

As stated, there are two Monopoly environments:
* Monopoly: contains a 'full' implementation of Monopoly where trading, mortgaging and houses/hotels are implemented. This environment does have some simplifications: only two players can play, pickup cards are randomly generated, trading only involves 1-to-1 property exchanges. This implementation has several issues that are unresolved and requires a redesign of the tick() method.
* SimplifiedMonopoly: builds upon the Monopoly implementation however removes the following elements: trading, mortgaging, houses/hotels. The step() function mimics the OpenAI gym standard of RL environments.
