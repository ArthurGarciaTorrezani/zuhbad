import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * Class Room - a room in an adventure game.
 *
 * This class is part of the "World of Zuul" application.
 * "World of Zuul" is a very simple, text based adventure game.
 *
 * A "Room" represents one location in the scenery of the game. It is
 * connected to other rooms via exits. The exits are labelled north,
 * east, south, west. For each direction, the room stores a reference
 * to the neighboring room, or null if there is no exit in that direction.
 * 
 * @author Michael Kölling and David J. Barnes
 * @version 2016.02.29
 */
public class Room {
    private String description;
    private HashMap<String, Room> exits = new HashMap<>(); // esquerda chave direita objeto
    private HashMap<String,Item> itemsOnRoom = new HashMap<>(); 

    /**
     * Create a room described "description". Initially, it has
     * no exits. "description" is something like "a kitchen" or
     * "an open court yard".
     * 
     * @param description The room's description.
     */
    public Room(String description) {
        this.description = description;
    }


    public void setExit(String direction, Room neighbor) {
        exits.put(direction, neighbor);
    }

    private void setItem(String name, Item item) {
        itemsOnRoom.put(name,item);
    }

    public void createItem(String description, int weight, int amount, String name) {
        Item item = new Item(description, weight, amount);
        setItem(name, item);
    }

    /**
     * @return The description of the room.
     */
    public String getDescription() {
        return description;
    }

    public Room getExit(String direction) {
        return exits.get(direction) != null ? exits.get(direction) : null;
    }

    private String getExitString() {
        Set<String> keys = exits.keySet();
        String information = String.join(" ", keys);
        return information;
    }

    public String getLongDescription() {
        return "You are " + description + "\n" + "Exits: " + getExitString() + "\n" + getItems();
    }

    public String getItems(){
        String information = "";
        Set<String> keys = itemsOnRoom.keySet();
        for(String key: keys){
            information +=    key + ":" +itemsOnRoom.get(key).getAmount() + " ";
        }
        return "Items of room:"+information;
    }

    public void perdeItem(String name){
        itemsOnRoom.get(name).perderItem();
        if(itemsOnRoom.get(name).getAmount() == 0){
            itemsOnRoom.remove(name);
        }
    }

    public void ganhaItem(String name, Item item){
        if(itemsOnRoom.get(name) != null){
            itemsOnRoom.get(name).ganhaItem();
        }else{
            createItem(item.getDescription(), item.getWeight(),1,name);
            int a = itemsOnRoom.get(name).getAmount();
            System.out.println(a);
        }

    }
    public Item getItem(String name){
        return itemsOnRoom.get(name) != null ? itemsOnRoom.get(name) : null;
    }
}
