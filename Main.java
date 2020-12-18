import java.util.*;

class Card{
  String name;
  int value;

  //Card Constructors
  public Card(String n,int v){
    name = n;
    value = v;
  }

  //Get Name
  public String getName(){
    return name;
  }

  //Get Hard Value
  public int getValue(){
    return value;
  }

  //Card Visuals
  public String toString(){
    if(name.equals("10"))
      return ".-----.\n|"+name.substring(0,2)+"   |\n|     |\n|   "+name.substring(0,2)+"|\n\'-----\'";
    return ".-----.\n|"+name.charAt(0)+"    |\n|     |\n|    "+name.charAt(0)+"|\n\'-----\'";
    /*.-----.
      |#    |
      |     |
      |    #|
      '-----'*/
  }
}

class Main {
  //INITIALIZATION
  static Scanner scan = new Scanner(System.in);
  //52 Card Array List
  static ArrayList<Card> deck = new ArrayList<Card>();
  //Value Array List
  static HashMap<String,Integer> values = new HashMap<>();
  //List of Hands at the Table
  static LinkedHashMap<String, ArrayList<Card>> table = new LinkedHashMap<>();

  //Card Dealing
  public static ArrayList<Card> dealCards(ArrayList<Card> hand,int numDealt){
    Random r = new Random();
    int cardDrawn;
    for(int dealt=0;dealt<numDealt;dealt++){
      cardDrawn = r.nextInt(deck.size());
      hand.add(deck.get(cardDrawn));
      deck.remove(cardDrawn);
    }
    return hand;
  }
  
  //Hand Value Calculating
  public static int handValue(ArrayList<Card> hand){
    int value=0;
    boolean ace = false;

    for(Card card : hand){
      value += card.getValue();
      if(card.getName().equals("Ace"))
        ace = true;
    }
    
    //If there's an ace set the ace to 1 if the value otherwise exceeds 21
    if(value > 21 && ace)
      value -= 10;
    return value;
  }

  //Visuals
  public static void displayTable(LinkedHashMap<String, ArrayList<Card>> table,boolean hidden){
    //Clears Console (Buggy on Repl.it)
    System.out.print("\033[H\033[2J");
    System.out.flush();
    //Print Title
    System.out.println("~ ~ ~ B L A C K  J A C K ~ ~ ~\n");
    //Print Each Hand
    for (Map.Entry<String, ArrayList<Card>> hand : table.entrySet()) {
      //Print Who's Hand it is
      System.out.println(hand.getKey().toUpperCase());
      //Print Each Card
      int i=0;
      for(Card card : hand.getValue()){
        i++;
        //Print Hidden
        if(hand.getKey().equals("dealer") && i == 2 && hidden){
          System.out.println(".-----.");
          for(int row=0;row<3;row++)
            System.out.println("|     |");
          System.out.println("\'-----\'");
        }
        //Print Revealed
        else
          System.out.println(card);
      }
    }
  }

  //Checking User Input
  public static boolean validInput(String input){
    return input.substring(0, 1).equalsIgnoreCase("y") || input.substring(0, 1).equalsIgnoreCase("n");
  }

  //Drawing Cards
  public static ArrayList<Card> drawCards(ArrayList<Card> hand, String key){
    //Ask if they want to draw another card
    String draw = " ";
    do{
      if(key.contains("player")){
        draw = " ";
        System.out.print("Would you like to draw another card for your ");
        if(key.equals("player split"))
          System.out.print("split ");
        System.out.println("hand? (y/n)");
        while(!validInput(draw)){
          draw = scan.next();
        }
      }
      //If another random card is drawn, recalculate value of hand and check if it's greater than 21
      if(draw.substring(0, 1).equalsIgnoreCase("y") || (key.equals("dealer") && values.get(key) < 17)){
        hand = dealCards(hand,1);
        if(key.equals("dealer"))
          displayTable(table,false);
        else
          displayTable(table,true);
        values.put(key,handValue(hand));
        if(values.get(key) > 21){
          //If Dealer exceeds 21: Win
          if(key.equals("dealer"))
            System.out.println("You Win! The dealer busted!");
          //If Player Exceeds 21: Lose (Bust)
          else
            System.out.println("You Busted!");
          System.exit(0);
        }
      }
    } while(draw.substring(0, 1).equalsIgnoreCase("y") || (key.equals("dealer") && values.get(key) < 17));
    return hand;
  }

  //Check Player and Dealer for "Black Jack" Hand
  public static void checkBlackJack(String player){
    if(values.get(player) == 21){
      //If Both Black Jack: Tie
      if(values.get("dealer") == 21){
        System.out.println("Tie! Both you and the dealer got a Black Jack!");
        System.exit(0);
      }
      //If Player Black Jack: Win
      System.out.println("You Win! You got a Black Jack!");
      System.exit(0);
    }
  }

  public static void main(String[] args) {
    String dealer="dealer",player="player",playerSplit="";
    //Jack Queen King 10
    //Ace 11 or 1
    //Numbered Cards Equal Their Number
    String[] n = {"Ace","2","3","4","5","6","7","8","9","10","Jack","Queen","King"};
    int[] v = {11,2,3,4,5,6,7,8,9,10,10,10,10};
    //Add Cards to deck
    int index;
    for(int card=0;card<52;card++){
      index = (int)Math.floor(card/4.0);
      deck.add(new Card(n[index],v[index]));
    }

    //Player and Computer Dealer are each Dealt 2 Cards
    table.put(dealer,dealCards(new ArrayList<Card>(),2));
    table.put(player,dealCards(new ArrayList<Card>(),2));
    
    displayTable(table,true);
    
    //Calculate value of Hands
    values.put(dealer,handValue(table.get(dealer)));
    values.put(player,handValue(table.get(player)));

    //If cards have same value, ask if player wants to have 2 at the table.
    String split = " ";
    if(table.get(player).get(0).getValue() == table.get(player).get(1).getValue()){
      System.out.println("Would you like to split your hand? (y/n)");
      while(!validInput(split)){
        split = scan.next();
      }
      if(split.substring(0, 1).equalsIgnoreCase("y")){
        playerSplit="player split";
        table.put(playerSplit,dealCards(new ArrayList<Card>(),1));
        table.get(playerSplit).add(table.get(player).get(0));
        table.get(player).remove(0);
        table.put(player,dealCards(table.get(player),1));
        displayTable(table,true);
        values.put(playerSplit,handValue(table.get(playerSplit)));

        checkBlackJack(player);
        table.put(playerSplit,drawCards(table.get(playerSplit),playerSplit));
      }
    }
    checkBlackJack(player);
    displayTable(table,true);
    table.put(player,drawCards(table.get(player),player));

    //Reveal Dealer's other card.
    displayTable(table,false);
    //Draw random cards until Dealer's hand value is at least 17
    table.put(dealer,drawCards(table.get(dealer),dealer));
    boolean possTie = false;
    if(values.get(dealer) == 21){
    //If Dealer equals 21: Lose
      System.out.println("You Lost! The dealer got a Black Jack!");
      return;
    }
    if(playerSplit.equals("player split")){ 
    //If Player's Hand is Higher: Win
      if(values.get(playerSplit) > values.get(dealer)){
        System.out.println("You Win! Your split hand is higher than the dealer!");
        return;
      }
      if(values.get(playerSplit) == values.get(dealer)){
        possTie = true;
      }
    }
    if(values.get(player) > values.get(dealer)){
      System.out.println("You Win! Your hand is higher than the dealer!");
      return;
    }
    if(values.get(player) == values.get(dealer)){
      possTie = true;
    }
    if(!possTie){
    //If Dealer's Hand is Higher: Lose (Bust)
      System.out.println("You Lost! The dealer's hand is higher than yours!");
    }
    else{
    //If Both hands are equal: Tie
      System.out.println("Tie! You and the dealer have equally valued hands!");
    }
  }
}