package fr.android.calculator;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class CalculatorActivity extends AppCompatActivity {


    /**
     * @description: Creation of context of the activity
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * @description: Creation of the buttons
     * @param view : The button of the calculator
     */
    public void operationHandler(View view) {
        TextView operationText = (TextView) findViewById(R.id.operationText);
        Button equalButton = (Button) findViewById(R.id.buttonEqual);
        Button eraseButton = (Button) findViewById(R.id.buttonClear);
        switch (view.getId()) {
            case R.id.buttonOne:
            case R.id.buttonTwo:
            case R.id.buttonThree:
            case R.id.buttonFour:
            case R.id.buttonFive:
            case R.id.buttonSix:
            case R.id.buttonSeven:
            case R.id.buttonEight:
            case R.id.buttonNine:
            case R.id.buttonZero:
            case R.id.buttonDiv:
            case R.id.buttonPlus:
            case R.id.buttonMinus:
            case R.id.buttonMultiply:
                operationText.append(((TextView) view).getText());
                break;
        }

        if(operationText.length() > 0) {
           equalButton.setVisibility(View.VISIBLE);
           eraseButton.setVisibility(View.VISIBLE);
        }
    }

    /**
     * @description: Cliking on the equal button event
     * @param view
     */
    public void resultHandler(View view) {
        TextView operationText = (TextView) findViewById(R.id.operationText);
        TextView resultText = (TextView) findViewById(R.id.resultText);
        if(operationText.length() > 0) {
            double result = fromStringToOperation(operationText.getText().toString());
            if(result != 0) {
                resultText.setText(String.valueOf(result));
            }else{
                resultText.setText("ERROR");
            }
        }
    }

    /**
     * @description: Cliking on the erase button event
     * @param view
     */
    public void eraseHandler(View view) {
        TextView operationText = (TextView) findViewById(R.id.operationText);
        if(operationText.length() > 0) {
            String newOpText = operationText.getText().toString().substring(0, operationText.getText().toString().length() - 1);
            operationText.setText(newOpText);
        }

        if(operationText.length() == 0) {
            Button equalButton = (Button) findViewById(R.id.buttonEqual);
            Button eraseButton = (Button) findViewById(R.id.buttonClear);
            equalButton.setVisibility(View.INVISIBLE);
            eraseButton.setVisibility(View.INVISIBLE);
        }
        TextView resultText = (TextView) findViewById(R.id.resultText);
        resultText.setText("");
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
                try{
                    operand.add(Double.parseDouble(operationParsed[i]));
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(),"Vous ne pouvez pas mettre deux opérateurs côte à côte",Toast.LENGTH_SHORT).show();
                    return 0;
                }


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
                        Toast.makeText(getApplicationContext(),"Division par 0 impossible",Toast.LENGTH_SHORT).show();
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