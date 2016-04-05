public class CircularArrayQueue<T>{
	
	private T[] queueArray;
	private int front;
	private int rear;
	private int n;
	
	public CircularArrayQueue(){
		n = 5;
		queueArray = (T[])new Object[n];
		front = rear = 0;
	}
	
	public void enqueue(Object element) {
		int s = size();
		if (s == n - 1){
			resize();
		}
		queueArray[rear++] = (T)element;
		if(rear == n){
			rear = 0;
		}
	}
	
	private void resize(){
		n *= 2;
		int s = size();
		int lastIndex = s +1;
		T[]array = (T[])new Object[n];
		int i = 0;
		while(s > 0){
			s--;
			array[i++] = queueArray[front++];
			if(front == lastIndex){
				front = 0;
			}
		}
		rear = i++;
		front = 0;
		queueArray = array;
	}
	
	public T dequeue() {
		if (isEmpty()) return null;
		T x = queueArray[front];
		queueArray[front] = null;
		front++;
		if (front == n)front = 0;
		return x;
	}

	public Object first() {
		return queueArray[front];
	}

	public boolean isEmpty() {
		return front == rear;
	}

	public int size() {
		return (n - front + rear) % n;
	}
	
	public String toString(){
		String retString = "";
		for (int i = 0; i < n; i++ ){
			retString += "Index " + i + ": " +  queueArray[i] + "\n";
		}
		return retString;
	}

}