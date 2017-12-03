package neural;

public class Connection {
	double weight = 0;
	double prevDeltaWeight = 0; // for momentum
	double deltaWeight = 0;

	final Neuron leftNeuron;
	final Neuron rightNeuron;
	static int counter = 0;
	final public int id; // auto increment, starts at 0

	public Connection(Neuron fromN, Neuron toN) {
		leftNeuron = fromN;	//시작노드==>
		rightNeuron = toN;  //==>도착노드
		id = counter;	//뉴런 클래스에서 주어진 id와 같은 id를 같게될것이다
		counter++;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double w) {
		weight = w;
	}

	public void setDeltaWeight(double w) {
		prevDeltaWeight = deltaWeight;
		deltaWeight = w;
	}

	public double getPrevDeltaWeight() {
		return prevDeltaWeight;
	}

	public Neuron getFromNeuron() {
		return leftNeuron;
	}

	public Neuron getToNeuron() {
		return rightNeuron;
	}
}
