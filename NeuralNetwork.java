package neural;

import java.text.*;
import java.util.*;

public class NeuralNetwork {
	static {
		Locale.setDefault(Locale.ENGLISH);
	}

	final boolean isTrained = false;
	final DecimalFormat df; //10진수의 값을 원하는 포멧으로 변형해주는 클래스
	final Random rand = new Random();
	final ArrayList<Neuron> inputLayer = new ArrayList<Neuron>();
	final ArrayList<Neuron> hiddenLayer = new ArrayList<Neuron>();
	final ArrayList<Neuron> outputLayer = new ArrayList<Neuron>();
	final Neuron bias = new Neuron();
	final int[] layers;
	final int randomWeightMultiplier = 1;

	final double epsilon = 0.00000000001;

	final double learningRate = 0.9f;
	final double momentum = 0.7f; //가중치

	// Inputs for xor problem
	final double inputs[][] = { { 1, 1 }, { 1, 0 }, { 0, 1 }, { 0, 0 } };

	// Corresponding outputs, xor training data
	final double expectedOutputs[][] = { { 0 }, { 1 }, { 1 }, { 0 } };
	double resultOutputs[][] = { { -1 }, { -1 }, { -1 }, { -1 } }; // dummy init
	double output[];

	// for weight update all
	final HashMap<String, Double> weightUpdate = new HashMap<String, Double>();

	public static void main(String[] args) {
		NeuralNetwork nn = new NeuralNetwork(2, 4, 1); // 두개의 인풋 뉴런, 4개의 뉴런 in hidden layer, 하나의 뉴런 인 output
		int maxRuns = 50000;//max runs
		double minErrorCondition = 0.001;
		nn.run(maxRuns, minErrorCondition);
	}

	public NeuralNetwork(int input, int hidden, int output) {
		this.layers = new int[] { input, hidden, output };
		df = new DecimalFormat("#.0#"); // 0은 표시한 자리수 만큼의 값을 최소한으로 표시, #는 표시한 소수점자리수만큼 반올림

		/**
		 * Create all neurons and connections Connections are created in the
		 * neuron class
		 */
		for (int i = 0; i < layers.length; i++) {
			if (i == 0) { // input layer
				for (int j = 0; j < layers[i]; j++) { //2번 돌겠네
					Neuron neuron = new Neuron(); // id는 counter ++ 값. neuron 의 id 는 0에서 부터 시작 
					inputLayer.add(neuron);
				}
			} else if (i == 1) { // hidden layer
				for (int j = 0; j < layers[i]; j++) {
					Neuron neuron = new Neuron();
					neuron.addInConnectionsS(inputLayer); //inputlayer 0, 1, 에 각각 connection 이 생섬 그 관계는 connectionLookup hashmap 에 저장됨.
					neuron.addBiasConnection(bias);
					hiddenLayer.add(neuron);
				}
			}

			else if (i == 2) { // output layer
				for (int j = 0; j < layers[i]; j++) {
					Neuron neuron = new Neuron();
					neuron.addInConnectionsS(hiddenLayer);
					neuron.addBiasConnection(bias);
					outputLayer.add(neuron);
				}
			} else {
				System.out.println("!Error NeuralNetwork init");
			}
		}

		// initialize random weights
		for (Neuron neuron : hiddenLayer) {
			ArrayList<Connection> connections = neuron.getAllInConnections();
			for (Connection conn : connections) {
				double newWeight = getRandom(); //bias , hidden layer 커낵션 모두를 가져와서 랜덤
				conn.setWeight(newWeight);
			}
		}
		for (Neuron neuron : outputLayer) {
			ArrayList<Connection> connections = neuron.getAllInConnections();
			for (Connection conn : connections) {
				double newWeight = getRandom();
				conn.setWeight(newWeight);
			}
		}

		// reset id counters -> 왜?!!!
		Neuron.counter = 0;
		Connection.counter = 0;

		if (isTrained) {
			trainedWeights();
			updateAllWeights();
		}
	}

	// random
	double getRandom() {
		return randomWeightMultiplier * (rand.nextDouble() * 2 - 1); // [-1;1[
	}

	/**
	 * 
	 * @param inputs
	 *            There is equally many neurons in the input layer as there are
	 *            in input variables
	 */
	public void setInput(double inputs[]) {
		for (int i = 0; i < inputLayer.size(); i++) {
			inputLayer.get(i).setOutput(inputs[i]);
		}
	}

	public double[] getOutput() {
		double[] outputs = new double[outputLayer.size()];
		for (int i = 0; i < outputLayer.size(); i++)
			outputs[i] = outputLayer.get(i).getOutput();
		return outputs;
	}

	/**
	 * Calculate the output of the neural network based on the input The forward
	 * operation
	 */
	public void activate() {
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
			for (int p = 0; p < inputs.length; p++) {
				setInput(inputs[p]);

				activate();

				output = getOutput();
				resultOutputs[p] = output;

				for (int j = 0; j < expectedOutputs[p].length; j++) {
					double err = Math.pow(output[j] - expectedOutputs[p][j], 2);
					error += err;
				}

				applyBackpropagation(expectedOutputs[p]);
			}
		}

		printResult();
		
		System.out.println("Sum of squared errors = " + error);
		System.out.println("##### EPOCH " + i+"\n"); //Epoch 프린트!!!!!!!!!!!!!!!!!!!!!!!!
		if (i == maxSteps) {
			System.out.println("!Error training try again");
		} else {
			printAllWeights();
			printWeightUpdate();
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

	String weightKey(int neuronId, int conId) {
		return "N" + neuronId + "_C" + conId;
	}

	/**
	 * Take from hash table and put into all weights
	 */
	public void updateAllWeights() {
		// update weights for the output layer
		for (Neuron n : outputLayer) {
			ArrayList<Connection> connections = n.getAllInConnections();
			for (Connection con : connections) {
				String key = weightKey(n.id, con.id);
				double newWeight = weightUpdate.get(key);
				con.setWeight(newWeight);
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
	void trainedWeights() {
		weightUpdate.clear();
		
		weightUpdate.put(weightKey(3, 0), 1.03); // 웨잇 키가 왜 n에대가 3을 넣을까?!
		weightUpdate.put(weightKey(3, 1), 1.13); // 첫번째 제너릭이 key 값이고 두번째 제너릭이 data 값
		weightUpdate.put(weightKey(3, 2), -.97);
		weightUpdate.put(weightKey(4, 3), 7.24);
		weightUpdate.put(weightKey(4, 4), -3.71);
		weightUpdate.put(weightKey(4, 5), -.51);
		weightUpdate.put(weightKey(5, 6), -3.28);
		weightUpdate.put(weightKey(5, 7), 7.29);
		weightUpdate.put(weightKey(5, 8), -.05);
		weightUpdate.put(weightKey(6, 9), 5.86);
		weightUpdate.put(weightKey(6, 10), 6.03);
		weightUpdate.put(weightKey(6, 11), .71);
		weightUpdate.put(weightKey(7, 12), 2.19);
		weightUpdate.put(weightKey(7, 13), -8.82);
		weightUpdate.put(weightKey(7, 14), -8.84);
		weightUpdate.put(weightKey(7, 15), 11.81);
		weightUpdate.put(weightKey(7, 16), .44);
	}

	public void printWeightUpdate() {
		System.out.println("printWeightUpdate, put this i trainedWeights() and set isTrained to true");
		// weights for the hidden layer
		for (Neuron n : hiddenLayer) {
			ArrayList<Connection> connections = n.getAllInConnections();
			for (Connection con : connections) {
				String w = df.format(con.getWeight());
				System.out.println("weightUpdate.put(weightKey(" + n.id + ", "
						+ con.id + "), " + w + ");");
			}
		}
		// weights for the output layer
		for (Neuron n : outputLayer) {
			ArrayList<Connection> connections = n.getAllInConnections();
			for (Connection con : connections) {
				String w = df.format(con.getWeight());
				System.out.println("weightUpdate.put(weightKey(" + n.id + ", "
						+ con.id + "), " + w + ");");
			}
		}
		System.out.println();
	}

	public void printAllWeights() {
		System.out.println("printAllWeights");
		// weights for the hidden layer
		for (Neuron n : hiddenLayer) {
			ArrayList<Connection> connections = n.getAllInConnections();
			for (Connection con : connections) {
				double w = con.getWeight();
				System.out.println("n=" + n.id + " c=" + con.id + " w=" + w);
			}
		}
		// weights for the output layer
		// n 이 나온다!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		for (Neuron n : outputLayer) {
			ArrayList<Connection> connections = n.getAllInConnections();
			for (Connection con : connections) {
				double w = con.getWeight();
				System.out.println("n=" + n.id + " c=" + con.id + " w=" + w);
			}
		}
		System.out.println();
	}
}
