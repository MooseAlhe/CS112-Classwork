

public class CardList 
{
	/* Attributes: first, keeps the front of the list
	 * count, keeps track of the number of Cards on the list
	 */
	private Card first;
	private int count;

	public int size() 
	{
		return this.count;
	}
	
	public Card getFirst() 
	{
		return this.first;
	}
	
	/* 
	 * Constructor (I did this one for you)
	 * if all is true, it creates a complete deck of 40 cards,
	 *                 4 colors, numbers from 0-9
	 * if all is false, it just creates an empty list
	 */
	public CardList(boolean all) 
	{
		this.first=null;
		this.count=0;
		if (all) 
		{
			for (Card.Colors color : Card.Colors.values()) 
			{
				for (int number=0;number<=9;number++) 
				{
					add(number,color);
				}	
			}
		}
	}

	/* DO THIS:
	 * traverse: is not actually used in the gui version of the program, 
	 * but it is useful while debugging the program
	 */
	public void traverse() 
	{
		Card current = this.first;
		while (current != null)
		{
			System.out.println(current);
			current = current.getNext();
		}
	}

	/* DO THIS:
	 * add: adds a card to the front of the list
	 * given number and color
	 */
	private void add(int number,Card.Colors color) 
	{
		this.first = new Card(number, color, this.first);
		this.count++;
	}

	/* DO THIS:
	 * add: adds a card to the front of the list
	 * given a reference to the new card
	 */
	private void add(Card card) 
	{
		card.setNext(this.first);
		this.first = card;
		this.count++;
	}
	
	/* DO THIS:
	 * countCards: Traverse the list and return the number of cards.
	 *     When complex operations are done on a list, such as
	 *     concatenation of lists, countCards is used to make sure that the
	 *     number of cards is kept updated (just there because we are lazy
	 *     and do not want to think about how to compute the new number
	 *     of cards based on the original ones).
	 */
	private int countCards() 
	{
		Card current = this.first;
		int counter = 0;
		while (current != null)
		{
			counter++;
			current = current.getNext();
		}
		return counter;
	
	}

	/* DO THIS:
	 * Append a new list of cards "list" at the end of the current list (this)
	 * Notice that it might be possible for this.first to be null
	 */
	public void concatenateWith(CardList list) 
	{

		if (this.first == null)
		{
			this.first = list.first;
		}
		else
		{
			Card current = this.first;
			Card temp = null;
			while (current != null)
			{
				temp = current;
				current = current.getNext();
			}
			temp.setNext(list.first);
			this.count = countCards();
		}
	}

	/* DO THIS:
	 * moveTo: move the front card from this to the front of destination
	 */
	public void moveTo(CardList destination) 
	{
		if (this.first != null)
		{
			Card next = this.first.getNext();
			destination.add(this.first);
			this.first = next;
			this.count--;
		}
	}
	
	/* DO THIS:
	 * moveTo: move the first num cards from this to the front of destination,
	 * it can use the (CardList destination) method repeatedly
	 */	
	public void moveTo(int num,CardList destination) 
	{
		while (num > 0)
		{
			moveTo(destination);
			num--;
		}
	}

	/* DO THIS:
	 * moveTo: Given a Card x, it finds the card on this list and
	 *         moves it to the front of the destination list.
	 * 
	 */
	public boolean moveTo(Card x,CardList destination) 
	{
		Card current = this.first;
		Card temp = null;
		while (current != null)
		{
			if (current == x)
			{
				if (temp == null)
				{
					this.first = this.first.getNext();
				}
				else
				{
					temp.setNext(current.getNext());
				}
				destination.add(current);
				this.count--;
				return true;
			}
			temp = current;
			current = current.getNext();
		}
		return false;
	}
		
	/* DO THIS:
	 * shuffle: Easy way is to create two new empty lists,
	 *          repeat split number of times: move the
	 *          first card of this to the first list, and then 
	 *          the next one to the second list,
	 *          finally, concatenate the two lists to this. 
	 */
	public void shuffle(int split) 
	{	
		CardList list1 = new CardList(false);
		CardList list2 = new CardList(false);
		
		for (int i = 0; i < split; i++) 
		{
			if (size() > 0) 
		    {
				moveTo(list1);
		    }
		    if (size() > 0) 
		    {
		        moveTo(list2);
		    }
			
		 }
		 concatenateWith(list1);
		 concatenateWith(list2);
	}
		

	
	/* DO THIS:
	 * search: return a card that matches either the number or color
	 *         of the given card x.
	 *         You must use the matches(false,x) method that you wrote for the
	 *         Card class.
	 */
	public Card search(Card x) 
	{
		Card current = this.first;
		while (current != null)
		{
			if (current.matches(false, x))
			{
				return current;
			}
			current = current.getNext();
		}
		return null;
	}

	
	/* DO THIS:
	 * getCard: returns a Card in this list that matches exactly
	 *          (use matches(true,card) method in Card) the given card
	 */
	public Card getCard(Card card) 
	{
		Card current = this.first;
		while (current != null)
		{
			if (current.matches(true, card))
			{
				return current;
			}
			current = current.getNext();
		}
		return null;
	}

}
