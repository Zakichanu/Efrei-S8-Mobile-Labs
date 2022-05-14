package fr.android.calculator;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

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
                    errorCode = 1;
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
                double result = fromStringToOperation(operationText.getText().toString());
                if(result != 0) {
                    resultText.setText(String.valueOf(result));
                }else{
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
        }catch (Exception e) {
            e.getMessage();
        }



    }

    /**
     * @description: Cliking on the erase button event
     * @param view
     */
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



    /**
     * Function to progress the progressBar when click on result button
     * @param view
     * */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void progressBarHandler(View view) {
        try {
            downloadTask dt = new downloadTask();
            /*
            * This code is here to make us able to understand handlers. In this case, we are using a handler to progress the progressBar,
            * this one will add a new value of the progressBar to the MessageQueue. Then, a thread will loop into this messageQueue
            * and will change the progressBar value.
            * */
            Runnable runnable = () -> {
                for (int i = 0; i <= 10; i++) {
                    final int value = i;
                    // simulate a slow network !
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e){
                        e.printStackTrace(); }
                    handler.post(() -> progressBar.setProgress(value, true));
                    if(value == 10) {
                        resultHandler();
                        Thread.currentThread().interrupt();
                    }
                }
            };
            // We create a new thread to run the progressBar
            new Thread(runnable).start();

            // Call asyncTask to change the loading textfield
            dt.execute("0", "1", "2", "3", "4", "5", "6", "7", "8", "9");

        }catch (Exception e) {
            e.getMessage();
        }
    }

    /**
     * @description: This function will be called when the user click on the equal button
     *
     * To Explain:
     *  - This task is mainly called when the user click on the equal button.
     *  - We put in it some string params to pass a count to loop into the task.
     *  - Then, in the loop, for each count, we will add a new value to a Queue (in publishProgress method) that we are going to work with.
     *  - After in the onProgressUpdate, we will change the text of the textfield with the value of the first element of the queue and then after
     *  the task will pop it from it.
     *  - After all the process, when the task is finished, we will call the onPostExecute function -> to trigger after the resultHandler function.
     * */
    private class downloadTask extends AsyncTask<String, Integer, Long> {
        protected Long doInBackground(String... purcentage) {
            int count = purcentage.length;
            long totalSize = 0;
            for (int i = 0; i <= count; i++) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // Publish progress will send to onProgressUpdate method the new value to print on the UI
                publishProgress((int) ((i / (float) count) * 10));
            }

            return totalSize;
        }
        protected void onProgressUpdate(Integer... p) {
            loadingText.setText((p[0] * 10) + "%");
        }
        protected void onPostExecute(Long result) {
            Toast.makeText(CalculatorActivity.this, "Finished",
                    Toast.LENGTH_LONG).show();
        }
    }
}