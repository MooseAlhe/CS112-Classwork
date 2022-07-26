public class Assignment3 {
	
	public static Queue<BTNode<dataNode>> makeQueue(double[] a)
	{
		// Each element of the given array a must be inserted into a BTNode, 
		// this method returns a queue of BTNodes, each node will contain a dataNode
		// the dataNode will have the value equal to the element of the array
		// count equal to the number of times that the element repeats
		// min and max must be equal to value.
		// the BTNode created must have its parent, right, and left references set to null
		Queue<BTNode<dataNode>> q = new Queue<>();
		dataNode last = null;
		
		for(int i = 0; i < a.length; i++)
		{
			if (last != null && last.value == a[i])
			{
				last.count++;
			}
			else
			{
				last = new dataNode();
				last.count = 1;
				last.value = a[i];
				last.min = a[i];
				last.max = a[i];
				q.enqueue(new BTNode<>(last, null, null, null));
			}
		}
		return q;
	}
	
	public static Queue<BTNode<dataNode>> join(Queue<BTNode<dataNode>> myQueue)
	{
		// For every two elements dequeued from myQueue create a new root element and
		// make the two dequeued elements be the left and right child of that root.
		// Each new root must contain the min value, obtained from the left subtree,
		// the max value, obtained from the right subtree, and the value should be
		// equal to the average of the maximum value of the left subtree and the
		// minimum value of the right subtree, count should be set equal to 0 (internal node)
		// Enqueue each new root node into another queue and return that queue.
		// In case that only one element is left in myQueue, just enqueue it 
		// in the queue that will be returned.
		Queue<BTNode<dataNode>> q = new Queue<BTNode<dataNode>>();
		
		while(!myQueue.isEmpty())
		{
			if (myQueue.size() > 1)
			{
				BTNode<dataNode> left = myQueue.dequeue();
				BTNode<dataNode> right = myQueue.dequeue();
				
				double avg = (left.getData().max + right.getData().min) / 2.0;
				dataNode root = new dataNode();
				root.value = avg;
				root.max = right.getData().max;
				root.min = left.getData().min;
				root.count = 0;
				
				BTNode<dataNode> btroot = new BTNode<dataNode>(root, left, right, null);
				left.setParent(btroot);
				right.setParent(btroot);
				q.enqueue(btroot);
			}
			else
			{
				q.enqueue(myQueue.dequeue());
			}
		}
		return q;
	}
	
	public static int search(BTNode<dataNode> root,double target) 
	{
		// given a target value recursively search on the left or the right subtrees
		// by comparing the value in root to the target. You know that you got to a 
		// leaf when the value count of the root is not equal to 0.
		if (root != null && root.getData().count == 0)
		{
			if (root.getData().value > target)
				return search(root.getLeft(), target);
			else
				return search(root.getRight(), target);	
		}
		if (root != null && root.getData().value == target)
		{
			return root.getData().count;
		}
		return 0;
	}
	
	public static void main(String[] args) {
		// this is given to you and should work with your methods.
		// The expected output is:
//		(13.0,1) (12.5,1) (12.3,1) (12.1,1) (11.9,1) (10.3,2) (10.0,1) (9.2,1) (9.1,1) (8.0,1) (7.2,3) (5.8,2) (2.3,1) (1.0,1) 
//		(10.15,0)  7.2   0
//		(7.6,0)  7.2   0
//		(4.05,0)  7.2   0
//		(6.5,0)  7.2   0
//		(7.2,3)  7.2   3
//		3
		double[] a = {1,2.3,5.8,5.8,7.2,7.2,7.2,8,9.1,9.2,10,10.3,10.3,11.9,12.1,12.3,12.5,13};
		Queue<BTNode<dataNode>> myQueue=makeQueue(a);
		myQueue.traverse();
		System.out.println();
		while (myQueue.size()>1) {
			myQueue=join(myQueue);
		}
		BTNode<dataNode> root=myQueue.dequeue();
		System.out.println(search(root,7.2));
	}
}
