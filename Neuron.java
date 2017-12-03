package neural;

import java.util.*;
//신경망 : 입력층, 1층의 은닉층, 출력층으로 이루어진 1-1-1 MLP 
//학습방법 : 역전파 알고리즘(Back Propagation)
public class Neuron {	
	static int counter = 0;
	final public int id;  // auto increment, starts at 0
	Connection biasConnection;
	final double bias = -1;// hidden layer 의 뉴런의 값은 가중치와 bias를 반영하여 아래와 같이 표현된다.

	double output;
	
	ArrayList<Connection> Inconnections = new ArrayList<Connection>(); //모든 커낵션 리스트 
	HashMap<Integer,Connection> connectionLookup = new HashMap<Integer,Connection>(); //integer 는 key connection 은 실제 데이타
	
	public Neuron(){		
		id = counter;
		counter++;
	}
	
	/**
	 * Compute Sj = Wij*Aij + w0j*bias
	 */
	public void calculateOutput(){
		double s = 0;
		for(Connection con : Inconnections){
			Neuron leftNeuron = con.getFromNeuron();
			double weight = con.getWeight();
			double a = leftNeuron.getOutput(); //output from previous layer
			
			s = s + (weight*a);
		}
		s = s + (biasConnection.getWeight()*bias);
		
		output = g(s);
	}
	
	
	double g(double x) {
		return sigmoid(x);
	}

	double sigmoid(double x) {
		return 1.0 / (1.0 +  (Math.exp(-x)));
	}
	
	public void addInConnectionsS(ArrayList<Neuron> inNeurons){
		for(Neuron n: inNeurons){
			Connection con = new Connection(n,this);//n: Neuron fromN, this : Neuron toN
			Inconnections.add(con); //connection 의 arraylist 인푸 0 인풋 1 2개
			connectionLookup.put(n.id, con); //connectionLookup hashmap 에 뉴런 n 의 id (key 값) con(data 값)
		}
	}
	
	public Connection getConnection(int neuronIndex){
		return connectionLookup.get(neuronIndex);
	}

	public void addInConnection(Connection con){
		Inconnections.add(con);
	}
	public void addBiasConnection(Neuron n){ // n 에 bias 가 들어옴
		Connection con = new Connection(n,this); //n: Neuron fromN, this : Neuron toN
		biasConnection = con; // 지금만들어진 connection 이 biasconnection 이야 
		Inconnections.add(con); //arraylist connection 에 
	}
	public ArrayList<Connection> getAllInConnections(){
		return Inconnections; //arraylist 에 있는 모든 커낵션을 리턴
	}
	
	public double getBias() {
		return bias;
	}
	public double getOutput() {
		return output;
	}
	public void setOutput(double o){
		output = o;
	}
}