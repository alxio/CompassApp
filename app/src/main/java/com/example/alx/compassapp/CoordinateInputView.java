package com.example.alx.compassapp;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CoordinateInputView extends LinearLayout {
    public interface Model {
        void setCoordinate(double coordinate, Compass.CoordinateType coordinateType);
        double getCoordinate(Compass.CoordinateType coordinateType);
    }

    public CoordinateInputView(Context context) {
        super(context);
        init(context);
    }

    public CoordinateInputView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CoordinateInputView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private Model mModel;
    private Compass.CoordinateType mCoordinateType;

    private EditText mUserInput;
    private View mError;
    private TextView mTitle;
    private Button mSave;
    private Button mCancel;

    public void init(Model listener, Compass.CoordinateType coordinateType){
        mModel = listener;
        mCoordinateType = coordinateType;
        mTitle.setText(mCoordinateType.name());
    }

    public void show(){
        double coord = mModel.getCoordinate(mCoordinateType);
        mUserInput.setText(coordToString(coord));
        setVisibility(View.VISIBLE);
    }

    private void init(Context ctx){
        inflate(ctx, R.layout.coordinate, this);
        mUserInput = (EditText) findViewById(R.id.input);
        mError = findViewById(R.id.error);
        mSave = (Button) findViewById(R.id.save);
        mCancel = (Button) findViewById(R.id.cancel);
        mTitle = (TextView) findViewById(R.id.title);

        mSave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Double coord = stringToCoord(mUserInput.getText().toString());
                if (coord != null) {
                    mModel.setCoordinate(coord, mCoordinateType);
                    mError.setVisibility(View.GONE);
                    setVisibility(View.GONE);
                } else {
                    mError.setVisibility(View.VISIBLE);
                }
            }
        });
        mCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mUserInput.setText("");
                mError.setVisibility(View.GONE);
                setVisibility(View.GONE);
            }
        });
    }

    private Double stringToCoord(String coord){
        boolean ok = false;
        int mult = 1;
        double value = 0;
        String last = coord.substring(coord.length() - 1).toUpperCase();
        if(mCoordinateType == Compass.CoordinateType.LATITUDE){
            switch (last){
                case "S":
                    mult = -1;
                    //no break
                case "N":
                    coord = coord.substring(0, coord.length() - 1);
                    break;
            }
        }else{
            switch (last) {
                case "W":
                    mult = -1;
                    //no break
                case "E":
                    coord = coord.substring(0, coord.length() - 1);
                    break;
            }
        }
        try {
            value = Double.parseDouble(coord) * mult;
            double abs = Math.abs(value);
            if (Math.abs(value) <= 90 || (mCoordinateType == Compass.CoordinateType.LONGITUDE && abs <= 180)) {
                ok = true;
            }
        } catch (NumberFormatException e) {
            //pass, ok is false anyway
        }
        return ok ? value : null;
    }

    private String coordToString(double coord){
        String direction;
        if(mCoordinateType == Compass.CoordinateType.LONGITUDE){
            if(coord < 0) direction = "W";
            else direction = "E";
        }else{
            if(coord < 0) direction = "S";
            else direction = "N";
        }
        return Math.abs(coord) + direction;
    }
}
