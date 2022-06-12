package modules;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class MyCalculusRunnable implements Runnable {

    private Socket sock;

    public MyCalculusRunnable(Socket s) {
        sock = s;
    }

    @Override
    public void run() {

        try {
            DataInputStream dis = new DataInputStream(sock.getInputStream());
            DataOutputStream dos = new DataOutputStream(sock.getOutputStream());

            // read op1, op2 and the opreation to make
            // Double op1 = dis.readDouble();
            // char op = dis.readChar();
            // Double op2 = dis.readDouble();
            String op = dis.readUTF();
            System.out.println(op);
            Double result = fromStringToOperation(op);
            // Double res = CalculusServer.doOp(op1, op2, op);

            // send back result
            dos.writeDouble(result);

            dis.close();
            dos.close();
            sock.close();

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     *
     * @param operation string of the operation wrote on the operation TextField
     * @return Result of the operation
     */
    public double fromStringToOperation (String operation) {
        String[] operationParsed = operation.split("(?<=[-+*/])|(?=[-+*/])");
        // AL stores operators
        ArrayList<String> operator = new ArrayList<>();

        // AL stores numbers
        ArrayList<Double> operand = new ArrayList<>();


        // Parse the operation
        for (int i = 0; i < operationParsed.length; i++) {
            if(i % 2 == 0) {
                operand.add(Double.parseDouble(operationParsed[i]));
            }else{
                operator.add(operationParsed[i]);
            }
        }

        // Loop to calculate multiplication and division (for priority of operation)
        for (int i = 0; i < operator.size(); i++) {

            // Priority of operation
            if(operator.get(i).equals("*") || operator.get(i).equals("/")) {
                if (operator.get(i).equals("*")) {
                    operand.set(i, operand.get(i) * operand.get(i + 1));
                } else {
                    if(operand.get(i + 1) == 0) {
                        return 0;
                    }else {
                        operand.set(i, operand.get(i) / operand.get(i + 1));
                    }

                }
                operand.remove(i + 1);
                operator.remove(i);
                i--;
            }
        }

        // Making the operation
        double result = operand.get(0);
        for (int i = 0; i < operator.size(); i++) {
            switch (operator.get(i)) {
                case "+":
                    result += operand.get(i + 1);
                    break;
                case "-":
                    result -= operand.get(i + 1);
                    break;
            }
        }
        return result;
    }

}