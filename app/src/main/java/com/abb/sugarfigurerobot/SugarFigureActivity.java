package com.abb.sugarfigurerobot;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.abb.robot.SocketAsyncTask;
import com.abb.robot.SocketMessageData;
import com.abb.robot.SocketMessageType;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;

public class SugarFigureActivity extends AppCompatActivity implements SocketAsyncTask.OnSocketListener {
    private static final String TAG = "SugarFigureActivity";
    private SugarFigureView sugarFigureView;
    private Context mContext;
    private View view_settings;
    private AlertDialog.Builder builder = null;
    private AlertDialog alertDialog = null;
    private EditText editText;
    private String HOST = "192.168.2.52";
    private int PORT = 3003;

    private SocketAsyncTask socketAsyncTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sugar_figure);

        SharedPreferences pref = getSharedPreferences("sugarfigure", MODE_PRIVATE);

        mContext = SugarFigureActivity.this;
        this.sugarFigureView = findViewById(R.id.sugarFigureView);
        this.sugarFigureView.setStrokeWidth(pref.getInt("sugarWidth", 5));

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Sugar Figure Robot");
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SugarFigureView sugarFigureView = findViewById(R.id.sugarFigureView);
                sugarFigureView.clearPath();
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }
        });

        view_settings = SugarFigureActivity.this.getLayoutInflater().inflate(R.layout.view_sugar_figure_settings, null, false);
        builder = new AlertDialog.Builder(SugarFigureActivity.this);
        builder.setView(view_settings);
        builder.setTitle("Settings");

        DialogInterface.OnClickListener setListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        EditText editText = (EditText) SugarFigureActivity.this.view_settings.findViewById(R.id.editText_sugarWidth);
                        SugarFigureActivity.this.sugarFigureView.setStrokeWidth(Integer.parseInt(editText.getText().toString()));
                        dialog.dismiss();
                        SharedPreferences.Editor editor = getSharedPreferences("sugarfigure", MODE_PRIVATE).edit();
                        editor.putInt("sugarWidth", SugarFigureActivity.this.sugarFigureView.getStrokeWidth());
                        editor.commit();
                        break;
                    case DialogInterface.BUTTON_NEUTRAL:
                        dialog.dismiss();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        dialog.dismiss();
                        break;
                }
            }
        };
        builder.setPositiveButton("OK", setListener);
        builder.setNegativeButton("Cancel", setListener);

//        builder.setCancelable(false);
        alertDialog = builder.create();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sugar_figure_robot, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                Log.d(TAG, "action_save");
                return true;

            case R.id.action_fit:
                sugarFigureView.drawCompressedPath();
                Log.d(TAG, "action_fit");
                return true;

            case R.id.action_send:

                this.socketAsyncTask = new SocketAsyncTask(HOST, PORT, this);
                ArrayList<SocketMessageData> arrayListSocketMessageData = new ArrayList<SocketMessageData>();
                SocketMessageData socketMessageData;
                socketMessageData = new SocketMessageData(SocketMessageType.OpenDataBinFile);
                socketMessageData.setFileName("sugarfigure.bin");
                arrayListSocketMessageData.add(socketMessageData);

                byte[] bytes = this.sugarFigureView.getCompressedPathBytes();
                if (bytes.length == 0) {
                    return true;
                }
                int intReadRawBytesCount = bytes.length / 1021;
                int intReadRawBytesModulo = bytes.length % 1021;
                Log.d(TAG,"intReadRawBytesCount=" + intReadRawBytesCount + ", intReadRawBytesModulo=" + intReadRawBytesModulo);
                for (int i = 0; i < intReadRawBytesCount; i++) {
                    socketMessageData = new SocketMessageData(SocketMessageType.WriteDatatoFile);
                    socketMessageData.setFileData(Arrays.copyOfRange(bytes, i * 1021, i * 1021+1021));
                    socketMessageData.setRequestDataLength(1021);
                    arrayListSocketMessageData.add(socketMessageData);
                    Log.d(TAG,"WriteDatatoFile:" + i );
                }
                if (intReadRawBytesModulo > 0) {
                    socketMessageData = new SocketMessageData(SocketMessageType.WriteDatatoFile);
                    socketMessageData.setFileData(Arrays.copyOfRange(bytes, intReadRawBytesCount * 1021,intReadRawBytesCount * 1021+ intReadRawBytesModulo));
                    socketMessageData.setRequestDataLength(intReadRawBytesModulo);
                    arrayListSocketMessageData.add(socketMessageData);
                    Log.d(TAG,"WriteDatatoFile:" + intReadRawBytesModulo);
                }

                socketMessageData = new SocketMessageData(SocketMessageType.CloseDataFile);
                arrayListSocketMessageData.add(socketMessageData);

                socketMessageData = new SocketMessageData(SocketMessageType.DecodeDataFile);
                socketMessageData.setFileName("sugarfigure.bin");
                arrayListSocketMessageData.add(socketMessageData);

                socketMessageData = new SocketMessageData(SocketMessageType.SetSignalGo);
                socketMessageData.setSignalName("sgoOrderCode");
                socketMessageData.setSignalValue(101);
                arrayListSocketMessageData.add(socketMessageData);

                socketMessageData = new SocketMessageData(SocketMessageType.PulseSignalDO);
                socketMessageData.setSignalName("sdoRunPart");
                socketMessageData.setSignalValue(1);
                arrayListSocketMessageData.add(socketMessageData);

                socketMessageData = new SocketMessageData(SocketMessageType.CloseConnection);
                arrayListSocketMessageData.add(socketMessageData);

                socketAsyncTask.execute(arrayListSocketMessageData.toArray(new SocketMessageData[arrayListSocketMessageData.size()]));
                return true;

            case R.id.action_settings:
                editText = (EditText) SugarFigureActivity.this.view_settings.findViewById(R.id.editText_sugarWidth);
                editText.setText(String.valueOf(this.sugarFigureView.getStrokeWidth()));
                alertDialog.show();
                Log.d(TAG, "action_settings");
                return true;

            case R.id.action_about:
                Toast toast = Toast.makeText(this, "Hello Michael!", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);
                TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                v.setTextColor(Color.RED);
                toast.show();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public void refreshUI(SocketMessageData[] socketMessageDatas) {
        Log.d(TAG, "refreshUI");
        if (this.socketAsyncTask.isIoExceptionRaised()) {
            Toast toast = Toast.makeText(this, "The connetion may be closed, please check it!" + HOST, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);
            TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
            v.setTextColor(Color.RED);
            toast.show();
        } else {
            for (SocketMessageData socketMessageData : socketMessageDatas) {
                if (socketMessageData != null) {
                    Log.d(TAG, "refreshUI: " + socketMessageData.getSymbolValue());
                } else {
                    Log.d(TAG, "refreshUI: null");
                }
            }
        }
    }
}
