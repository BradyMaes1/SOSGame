# SOSGame

Summary:
This project aims to create a fully function SOS game in Java accompanied by a GUI. There will be two game modes: simple and general. A "simple" game will end as soon as a user creates an SOS. A "general" game will end only once the board is full or no new SOSs can possibly be created. At the end of a general game, the number of SOSs formed by each player is tallied and the winner is the player with the most SOSs. There will be a two-player "human versus human" option as well as a single-player "human versus machine" mode where a player may play against the computer on varying difficulties. Players will also be given the option to select a board size within certain bounds. The program is replayable and players may select to play a new game once they have completed their prior game.

|Design Choices| |
|--------------------------------------|------|
| Object-oriented programming language | Java |
| GUI library (strongly encouraged)    | JavaFX (Subject to change if I decide it does not meet my needs) |
| IDE (Integrated Development Environment) | IntelliJ IDEA Community Edition |
| xUnit framework (e.g., JUnit for Java) | JUnit version 5 (I also might use Mockito for testing small game functionalities). In the case of JUnit 5 with IntelliJ Community Edition, I will be using Maven. |
| Programming style guide (must read it carefully) | Google Java Style Guide (I am a big fan of Camel and Pascal casing as well as limiting characters on each line). <br> *Also worth noting that IntelliJ, like many IDEs, has the option to be configured to a specific style guide. This can be done for my project with the Google Java Format Plugin.* |
| Project hosting site | GitHub (github.com) |
| Other decisions if applicable | I make some other decisions found in my brainstorming section below. |

Sprints 0 and 1 set up these design choices and define User Stories and Acceptance Criteria. Documentation of this process can be found in the attached Sprint1 and Sprint2 PDF files.

# Sprint 2
In this sprint, we set up the core logic and UI for the game. A user can select between a "simple" or "general" game, but this is purely cosmetic at this point. The user can select their board size and to start the game. Once they click "start game", the board will update according to the size they selected. A user can choose between placing an "S" or "O" and their moves will be reflected on the board in their color (red or blue). No win conditions are implemented in this sprint.

Demo Link: https://youtu.be/0Cz57L1ESTM
