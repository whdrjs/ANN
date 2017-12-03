package neural;
/**
 * 뉴런 node생성
 */
import java.util.*;

public class Neuron {	
	static int counter = 0;
	final public int id;  // auto increment, starts at 0
	Connection biasConnection;
	final double bias = -1;
	double output;
	
	ArrayList<Connection> Inconnections = new ArrayList<Connection>();
	HashMap<Integer,Connection> connectionLookup = new HashMap<Integer,Connection>();
	
	public Neuron(){		
		id = counter;
		counter++;	//그다음것을 위해, 각 레이어에서의 노드마다 0~n까지의 번호
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
	/**
	 * 다른 레이어의 노드와 각노드를 연결하는 connection 생성 및 저장 
	 * 인풋과 히든/ 히든과 아웃풋 연결
	 */
	public void addInConnectionsS(ArrayList<Neuron> inNeurons){ //인풋레이어 어레이리스트 받아옴
		for(Neuron n: inNeurons){//for each 문으로 각 인풋레이어의(어레이리스트) 노드 만큼
			Connection con = new Connection(n,this);	//n:인풋|히든  / this:히든|아웃풋(현재이 클래스)
														//노드와 노드를 가리키는 선
			Inconnections.add(con);		//어레이리스트 인커넥션에 선(커넥션) 저장
			connectionLookup.put(n.id, con);	//hashmap key는 노드의 번호, 값은 연결된 connection
		}
	}
	
	public Connection getConnection(int neuronIndex){
		return connectionLookup.get(neuronIndex);
	}

	public void addInConnection(Connection con){
		Inconnections.add(con);
	}
	//바이어스 어렵
	public void addBiasConnection(Neuron n){ //main(뉴럴네트워크 클래스)의 bias받아온다
		Connection con = new Connection(n,this);	//바이어스와 현재 클래스의 연결
		biasConnection = con;		
		Inconnections.add(con);		//??
	}
	public ArrayList<Connection> getAllInConnections(){
		return Inconnections;
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
