package neural;
/** 김종건
 * 패키지의 몸체 
 * main 함수존재
 * XOR example
 * 
 */
import java.text.*;
import java.util.*;

import org.jfree.ui.RefineryUtilities;

public class NeuralNetwork {
	static {
		Locale.setDefault(Locale.ENGLISH);
	}
	final boolean isTrained = false;
	final DecimalFormat df;
	final Random rand = new Random();
	final ArrayList<Neuron> inputLayer = new ArrayList<Neuron>();
	final ArrayList<Neuron> hiddenLayer = new ArrayList<Neuron>();
	final ArrayList<Neuron> outputLayer = new ArrayList<Neuron>();
	final Neuron bias = new Neuron();
	final int[] layers;
	final int randomWeightMultiplier = 1;
    static XYSeriesDemo demo = new XYSeriesDemo("Weight Edge");
	
    private double epsilon = 0.00000000001;

	final double learningRate = 0.9f;
	final double momentum = 0.7f;
	/**김종건
	 * XOR에 대한 인풋과 기대값을 설정해놓는다
	 * 그래서
	 */
	// Inputs for xor problem
	final double inputs[][] = {  { 16, 9 }, { 8, 3 }, { 6, 1 }, { 11, 5 }};
		//{  { 16, 9 }, { 8, 3 }, { 6, 1 }, { 11, 5 },{16,10},{13,18},{7,3},{4,7},{11,14},{0,0}};
		//{{ 164, 91 }, { 89, 38 }, { 64, 15 }, { 114, 56 },{169,107},{132,182},{78,38},{48,72}};


	// Corresponding outputs, xor training data
	final double expectedOutputs[][] = {{1},{0},{0},{1},{1},{1},{0},{0},{1},{0}};	//예상 아웃풋
	double resultOutputs[][] = {{-1},{-1},{-1},{-1},{-1},{-1},{-1},{-1},{-1},{-1}}; // dummy init ??
	double output[];

	// for weight update all
	final HashMap<String, Double> weightUpdate = new HashMap<String, Double>();
	//메인함수
	public static void main(String[] args) {
		NeuralNetwork nn = new NeuralNetwork(2, 4, 1);
		int maxRuns = 500000;
		double minErrorCondition = 0.001;
		nn.run(maxRuns, minErrorCondition);

        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);
	}
	/**김종건
	 * 46줄(main)에서 실행
	 * 
	 * @param input
	 * @param hidden
	 * @param output
	 */
	public NeuralNetwork(int input, int hidden, int output) {
		this.layers = new int[] { input, hidden, output }; //layer 는 array
		df = new DecimalFormat("#.0#");
		/**
		 * Create all neurons and connections Connections are created in the
		 * neuron class
		 * 포문으로 layer 어레이에서 순서대로 in, hidden ,out 각 if문에 맞추어
		 */
		for (int i = 0; i < layers.length; i++) {
			if (i == 0) { // input layer
				//node 생성한다
				for (int j = 0; j < layers[i]; j++) {
					Neuron neuron = new Neuron(); //=>neuron.java 
					inputLayer.add(neuron);	//arraylist에 추가
				}
			} else if (i == 1) { // hidden layer
				for (int j = 0; j < layers[i]; j++) {
					Neuron neuron = new Neuron();
					neuron.addInConnectionsS(inputLayer);//input레이어에서의 노드만큼 할것이다
					neuron.addBiasConnection(bias);//bias설정
					hiddenLayer.add(neuron);  
				}
			}
			else if (i == 2) { // output layer
				for (int j = 0; j < layers[i]; j++) {
					Neuron neuron = new Neuron();
					neuron.addInConnectionsS(hiddenLayer);//이번엔 히든레이어 노드들
					neuron.addBiasConnection(bias);
					outputLayer.add(neuron);
				}
			} else {
				System.out.println("!Error NeuralNetwork init");
			}
		}

		// initialize random weights
		for (Neuron neuron : hiddenLayer) {
			ArrayList<Connection> connections = neuron.getAllInConnections();	//각 히든레이어 노드에서의 선 받아온다
			for (Connection conn : connections) {
				double newWeight = getRandom();	//랜덤으로 -1 ~ 1 의 수 weight에 받는다
				conn.setWeight(newWeight);//커넥션에 weight 저장
			}
		}
		for (Neuron neuron : outputLayer) {
			ArrayList<Connection> connections = neuron.getAllInConnections();
			for (Connection conn : connections) {
				double newWeight = getRandom();
				conn.setWeight(newWeight);
			}
		}

		// reset id counters
		Neuron.counter = 0;
		Connection.counter = 0;
	}

	// random
	double getRandom() {
		double 	re=randomWeightMultiplier * (rand.nextDouble() * 2 - 1);
/*
		if(re>1&&re<-1) {
			re=randomWeightMultiplier * (rand.nextDouble() * 2 - 1);
		}
*/
		return re;
	}

	/**
	 * 
	 * @param inputs
	 *            There is equally many neurons in the input layer as there are
	 *            in input variables
	 */
	public void setInput(double inputs[]) {	//??
		for (int i = 0; i < inputLayer.size(); i++) {
			inputLayer.get(i).setOutput(inputs[i]);	//o return
		}
	}

	public double[] getOutput() {
		double[] outputs = new double[outputLayer.size()];
		for (int i = 0; i < outputLayer.size(); i++)
			outputs[i] = outputLayer.get(i).getOutput();	//각 뉴런의(노드의 ) 아웃풋(시그모이드계산) 저장
		return outputs;
	}

	/**
	 * Calculate the output of the neural network based on the input The forward
	 * operation
	 */
	public void activate() {	//각 노드의 시그모이드 계산 값 저장 각 뉴런에
		for (Neuron n : hiddenLayer)
			n.calculateOutput();
		for (Neuron n : outputLayer)
			n.calculateOutput();
	}

	/**
	 * all output propagate back
	 * 
	 * @param expectedOutput
	 *            first calculate the partial derivative of the error with
	 *            respect to each of the weight leading into the output neurons
	 *            bias is also updated here
	 */
	public void applyBackpropagation(double expectedOutput[]) {

		// error check, normalize value ]0;1[
		for (int i = 0; i < expectedOutput.length; i++) {
			double d = expectedOutput[i];
			if (d < 0 || d > 1) {
				if (d < 0)
					expectedOutput[i] = 0 + epsilon;
				else
					expectedOutput[i] = 1 - epsilon;
			}
		}

		int i = 0;
		for (Neuron n : outputLayer) {
			ArrayList<Connection> connections = n.getAllInConnections();
			for (Connection con : connections) {
				double ak = n.getOutput();
				double ai = con.leftNeuron.getOutput();
				double desiredOutput = expectedOutput[i];

				double partialDerivative = -ak * (1 - ak) * ai
						* (desiredOutput - ak);
				double deltaWeight = -learningRate * partialDerivative;
				double newWeight = con.getWeight() + deltaWeight;
				con.setDeltaWeight(deltaWeight);
				con.setWeight(newWeight + momentum * con.getPrevDeltaWeight());
			}
			i++;
		}

		// update weights for the hidden layer
		for (Neuron n : hiddenLayer) {
			ArrayList<Connection> connections = n.getAllInConnections();
			for (Connection con : connections) {
				double aj = n.getOutput();
				double ai = con.leftNeuron.getOutput();
				double sumKoutputs = 0;
				int j = 0;
				for (Neuron out_neu : outputLayer) {
					double wjk = out_neu.getConnection(n.id).getWeight();
					double desiredOutput = (double) expectedOutput[j];
					double ak = out_neu.getOutput();
					j++;
					sumKoutputs = sumKoutputs
							+ (-(desiredOutput - ak) * ak * (1 - ak) * wjk);
				}

				double partialDerivative = aj * (1 - aj) * ai * sumKoutputs;
				double deltaWeight = -learningRate * partialDerivative;
				double newWeight = con.getWeight() + deltaWeight;
				con.setDeltaWeight(deltaWeight);
				con.setWeight(newWeight + momentum * con.getPrevDeltaWeight());
			}
		}
	}

	void run(int maxSteps, double minError) {
		int i;
		// Train neural network until minError reached or maxSteps exceeded
		double error = 1;
		for (i = 0; i < maxSteps && error > minError; i++) {
			error = 0;
			//System.out.println("3");
			for (int p = 0; p < inputs.length; p++) {
				setInput(inputs[p]);	//인풋값 
				//??????????????????
				activate();

				output = getOutput();
				resultOutputs[p] = output;	//기대값 0 1 1 0 이 아닌 실제 값

				for (int j = 0; j < expectedOutputs[p].length; j++) {
					double err = Math.pow(output[j] - expectedOutputs[p][j], 2);
					error += err;
				}//얼마만큼에러?

				applyBackpropagation(expectedOutputs[p]);	//역전판 알고리즘 기대값 0110이랑 보냠
			}//인풀 노드마다
			showWeights(i);
		}

		printResult();

		System.out.println("Sum of squared errors = " + error);
		System.out.println("##### EPOCH " + i+"\n");
		if (i >= maxSteps) {
			System.out.println("!Error training try again");
		} else {
			//printAllWeights();
			//printWeightUpdate();
		}
	}

	void printResult()
	{
		System.out.println("NN example with xor training");
		for (int p = 0; p < inputs.length; p++) {
			System.out.print("INPUTS: ");
			for (int x = 0; x < layers[0]; x++) {
				System.out.print(inputs[p][x] + " ");
			}

			System.out.print("EXPECTED: ");
			for (int x = 0; x < layers[2]; x++) {
				System.out.print(expectedOutputs[p][x] + " ");
			}

			System.out.print("ACTUAL: ");
			for (int x = 0; x < layers[2]; x++) {
				System.out.print(resultOutputs[p][x] + " ");
			}
			System.out.println();
		}
		System.out.println();
	}
	//
	String weightKey(int neuronId, int conId) {
		return "N" + neuronId + "_C" + conId;
	}

	/**
	 * Take from hash table and put into all weights
	 */
	public void updateAllWeights() {
		// update weights for the output layer
		for (Neuron n : outputLayer) {
			ArrayList<Connection> connections = n.getAllInConnections(); //아웃풋레이어에 연결 되어있는 선들
			for (Connection con : connections) {
				String key = weightKey(n.id, con.id);  //N뉴런아이디_C커넥션아이디
				double newWeight = weightUpdate.get(key);	//아까 트레이닝한 거에서 value얻어온다
				con.setWeight(newWeight);	//커넥션 엣지에 그 값 할당
			}
		}
		// update weights for the hidden layer
		for (Neuron n : hiddenLayer) {
			ArrayList<Connection> connections = n.getAllInConnections();
			for (Connection con : connections) {
				String key = weightKey(n.id, con.id);
				double newWeight = weightUpdate.get(key);
				con.setWeight(newWeight);
			}
		}
	}

	// trained data
	public void showWeights(int cnt) {
		int i=0;
		// weights for the hidden layer
		for (Neuron n : hiddenLayer) {
			ArrayList<Connection> connections = n.getAllInConnections();
			for (Connection con : connections) {
				double w = con.getWeight();
				//System.out.println("n=" + n.id + " c=" + con.id + " w=" + w);
				if(i==0)
					demo.series.add(cnt, w);
				else if(i==1)
					demo.series2.add(cnt, w);
				else if(i==2)
					demo.series3.add(cnt, w);
				else if(i==3)
					demo.series4.add(cnt, w);
				else if(i==4)
					demo.series5.add(cnt, w);
				else if(i==5)
					demo.series6.add(cnt, w);
				else if(i==6)
					demo.series7.add(cnt, w);
				else if(i==7)
					demo.series8.add(cnt, w);
				i++;
			}
		}
		// weights for the output layer
		for (Neuron n : outputLayer) {
			ArrayList<Connection> connections = n.getAllInConnections();
			for (Connection con : connections) {
				double w = con.getWeight();
				//System.out.println("n=" + n.id + " c=" + con.id + " w=" + w);
				if(i==12)
					demo.series9.add(cnt, w);
				else if(i==13)
					demo.series10.add(cnt, w);
				else if(i==14)
					demo.series11.add(cnt, w);
				else if(i==15)
					demo.series12.add(cnt, w);
			}
		}
	}
}
