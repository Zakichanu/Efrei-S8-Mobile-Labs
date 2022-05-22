package fr.android.calculator;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class CalculatorActivity extends AppCompatActivity {
    private Handler handler;
    private ProgressBar progressBar;
    private TextView loadingText;
    private int errorCode;


    /**
     * @description: Creation of context of the activity
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        handler = new Handler();
        progressBar = findViewById(R.id.progressBar1);
        loadingText = findViewById(R.id.loadingText);
    }
    /**
     *
     * @param operation string of the operation wrote on the operation TextField
     * @return Result of the operation
     */
    public double fromStringToOperation (String operation) throws Exception {
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
                    errorCode = 1;
                    throw new Exception("Could not parse");
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
                        errorCode = 2;
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
     * @description: ResultHandler
     */
    public void resultHandler() {
        try {
            TextView operationText = findViewById(R.id.operationText);
            TextView resultText = findViewById(R.id.resultText);
            if(operationText.length() > 0) {
                try {
                    double result = fromStringToOperation(operationText.getText().toString());
                    resultText.setText(String.valueOf(result));
                } catch (Exception e) {
                    if(e.getMessage().contains("Could not parse")) {
                        resultText.setText("Error");
                        switch (errorCode) {
                            case 2:
                                Toast.makeText(this, "Division by zero", Toast.LENGTH_SHORT).show();
                                break;
                            case 1:
                                Toast.makeText(this, "Invalid operation", Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                }

            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void eraseHandler(View view) {
        TextView operationText = findViewById(R.id.operationText);
        if(operationText.length() > 0) {
            String newOpText = operationText.getText().toString().substring(0, operationText.getText().toString().length() - 1);
            operationText.setText(newOpText);
        }

        if(operationText.length() == 0) {
            Button equalButton = findViewById(R.id.buttonEqual);
            Button eraseButton = findViewById(R.id.buttonClear);
            equalButton.setVisibility(View.INVISIBLE);
            eraseButton.setVisibility(View.INVISIBLE);
        }
        TextView resultText = findViewById(R.id.resultText);
        resultText.setText("");
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void progressBarHandler(View view) {
        try {
            calculateTask dt = new calculateTask();
            dt.execute("0", "1", "2", "3", "4", "5", "6", "7", "8", "9");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class calculateTask extends AsyncTask<String, Integer, Long> {
        protected Long doInBackground(String... percentages) {
            // Imitates a I/O
            int count = percentages.length;
            long totalSize = 0;
            for (int i = 0; i <= count; i++) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                publishProgress((int) ((i / (float) count) * 10));
            }

            return totalSize;
        }
        @RequiresApi(api = Build.VERSION_CODES.N)
        protected void onProgressUpdate(Integer... p) {
            // Update the progress bar every progress
            handler.post(() -> progressBar.setProgress(p[0], true));
        }
        protected void onPostExecute(Long result) {
            // Call the result handler at the end
            resultHandler();
            Toast.makeText(CalculatorActivity.this, "Finished", Toast.LENGTH_LONG).show();
        }
    }
}