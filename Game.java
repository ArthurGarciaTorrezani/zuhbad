import java.util.Stack;

import javax.sound.midi.Soundbank;

/**
 * This class is the main class of the "World of Zuul" application.
 * "World of Zuul" is a very simple, text based adventure game. Users
 * can walk around some scenery. That's all. It should really be extended
 * to make it more interesting!
 * 
 * To play this game, create an instance of this class and call the "play"
 * method.
 * 
 * This main class creates and initialises all the others: it creates all
 * rooms, creates the parser and starts the game. It also evaluates and
 * executes the commands that the parser returns.
 * 
 * @author Michael Kölling and David J. Barnes
 * @version 2016.02.29
 */

public class Game {
    private Parser parser;
    private Room currentRoom;
    private Stack<Room> oldRooms = new Stack<>();
    private Player player = new Player();


    /**
     * Create the game and initialise its internal map.
     */
    public Game() {
        createRooms();
        parser = new Parser();
    }

    /**
     * Create all the rooms and link their exits together.
     */
    private void createRooms() {
        Room outside, theater, pub, lab, office;

        // create the rooms
        outside = new Room("outside the main entrance of the university");
        theater = new Room("in a lecture theater");
        pub = new Room("in the campus pub");
        lab = new Room("in a computing lab");
        office = new Room("in the computing admin office");

        // initialise room exits
        outside.setExit("east", theater);
        outside.setExit("south", lab);
        outside.setExit("west", pub);
        outside.createItem("ficar bebado", 2, 5,"saque");
        outside.createItem("ficar bebado", 2, 5,"leite");
        outside.createItem("ficar bebado", 2, 5,"suco");
        outside.createItem("ficar bebado", 2, 5,"ovo");

        theater.setExit("west", outside);

        pub.setExit("east", outside);

        lab.setExit("north", outside);
        lab.setExit("east", office);

        office.setExit("west", lab);

        currentRoom = outside; // start game outside
    }

    /**
     * Main play routine. Loops until end of play.
     */
    public void play() {
        printWelcome();

        // Enter the main command loop. Here we repeatedly read commands and
        // execute them until the game is over.

        boolean finished = false;
        while (!finished) {
            Command command = parser.getCommand();
            finished = processCommand(command);
        }
        System.out.println("Thank you for playing.  Good bye.");
    }

    /**
     * Print out the opening message for the player.
     */
    private void printWelcome() {
        System.out.println();
        System.out.println("Welcome to the World of Zuul!");
        System.out.println("World of Zuul is a new, incredibly boring adventure game.");
        System.out.println("Type 'help' if you need help.");
        System.out.println();
        printLocationInfo();
    }

    /**
     * Given a command, process (that is: execute) the command.
     * 
     * @param command The command to be processed.
     * @return true If the command ends the game, false otherwise.
     */
    private boolean processCommand(Command command) {
        boolean wantToQuit = false;

        if (command.isUnknown()) {
            System.out.println("I don't know what you mean...");
            return false;
        }

        String commandWord = command.getCommandWord();
        String secondWord = command.getSecondWord();
        if (commandWord.equals("help")) {
            printHelp();
        } else if (commandWord.equals("go")) {
            goRoom(command);
        } else if (commandWord.equals("quit")) {
            wantToQuit = quit(command);
        }else if(commandWord.equals("look")){
            printLocationInfo();
        }else if(commandWord.equals("back")){
            goRoom(command);
        }else if(commandWord.equals("take")){
           takeItem(command);
        }else if(commandWord.equals("drop")){

            dropItem(command);
        }else if(commandWord.equals("items")){
            String items = player.getItems();
            System.out.println(items);
        }

        return wantToQuit;
    }

    // implementations of user commands:

    /**
     * Print out some help information.
     * Here we print some stupid, cryptic message and a list of the
     * command words.
     */
    private void printHelp() {
        System.out.println("You are lost. You are alone. You wander");
        System.out.println("around at the university.");
        System.out.println();
        System.out.println("Your command words are:");
        parser.showCommands();
    }

    /**
     * Try to go in one direction. If there is an exit, enter
     * the new room, otherwise print an error message.
     */
    private void goRoom(Command command) {
        if(command.getCommandWord().equals("back")){
            if(oldRooms.size() == 0){
                System.out.println("não tem mais salas"); 
                return; 
            }else{      
                currentRoom = oldRooms.pop();
                printLocationInfo();
                return;
            }

        }else if (!command.hasSecondWord()) {
            // if there is no second word, we don't know where to go...
            System.out.println("Go where?");
            return;
        }

        String direction = command.getSecondWord();

        // Try to leave current room.
        Room nextRoom = currentRoom.getExit(direction);

        if (nextRoom == null) {
            System.out.println("There is no door!");
        }else {
            oldRooms.push(currentRoom); 
            currentRoom = nextRoom;     
            printLocationInfo();
        }
    }

    /**
     * "Quit" was entered. Check the rest of the command to see
     * whether we really quit the game.
     * 
     * @return true, if this command quits the game, false otherwise.
     */
    private boolean quit(Command command) {
        if (command.hasSecondWord()) {
            System.out.println("Quit what?");
            return false;
        } else {
            return true; // signal that we want to quit
        }
    }

    private void printLocationInfo() {
        System.out.println(currentRoom.getLongDescription());
    }

    private void takeItem(Command command){
        if (!command.hasSecondWord()) {
            // if there is no second word, we don't know where to go...
            System.out.println("qual item?");
            return;
        }

        String secondWord = command.getSecondWord();
        Item itemRoom = currentRoom.getItem(secondWord);

        if (itemRoom == null) {
            System.out.println("item nao existe na sala!");
            return;
        }

        currentRoom.perdeItem(secondWord);

        if(player.getItem(secondWord) != null){
            player.takeItem(secondWord);
        }else{
            player.createItem(itemRoom.getDescription(), itemRoom.getWeight(), 1, secondWord);
        }

        String itemsOfPlayer = player.getItems();
        String itemsOfRoom = currentRoom.getItems();
        System.out.println(itemsOfPlayer + "\n" + itemsOfRoom);

    }

    private void dropItem(Command command){
        if (!command.hasSecondWord()) {
            // if there is no second word, we don't know where to go...
            System.out.println("qual item?");
            return;
        }

        String secondWord = command.getSecondWord();
        Item itemPlayer = player.getItem(secondWord);

        if(itemPlayer != null){
            currentRoom.ganhaItem(secondWord, itemPlayer);
            player.dropItem(secondWord);
        }else{
            System.out.println("o jogador nao possui este item");
        }

        String itemsOfPlayer = player.getItems();
        String itemsOfRoom = currentRoom.getItems();
        System.out.println(itemsOfPlayer + "\n" + itemsOfRoom);

    }

}
    