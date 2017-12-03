package neural;

public class Connection {
	double weight = 0;
	double prevDeltaWeight = 0; // for momentum (가속도, 운동량)
	double deltaWeight = 0;

	final Neuron leftNeuron;
	final Neuron rightNeuron;
	static int counter = 0;
	final public int id; // auto increment, starts at 0

	public Connection(Neuron fromN, Neuron toN) {
		leftNeuron = fromN;
		rightNeuron = toN;
		id = counter; //id 가 0 부터 시작 
		counter++;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double w) {
		weight = w;
	}

	public void setDeltaWeight(double w) { //가중치 weight 변화시키기
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