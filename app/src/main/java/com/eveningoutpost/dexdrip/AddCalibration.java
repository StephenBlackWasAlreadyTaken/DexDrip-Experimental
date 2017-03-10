package com.eveningoutpost.dexdrip;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.eveningoutpost.dexdrip.Models.Calibration;
import com.eveningoutpost.dexdrip.Models.Sensor;
import com.eveningoutpost.dexdrip.Models.UserError.Log;
import com.eveningoutpost.dexdrip.UtilityModels.CollectionServiceStarter;
import com.eveningoutpost.dexdrip.UtilityModels.Constants;


public class AddCalibration extends Activity implements NavigationDrawerFragment.NavigationDrawerCallbacks {
    Button button;
    public static String menu_name = "Add Calibration";
    private NavigationDrawerFragment mNavigationDrawerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(CollectionServiceStarter.isBTShare(getApplicationContext())) {
            Intent intent = new Intent(this, Home.class);
            startActivity(intent);
            finish();
        }
        setContentView(R.layout.activity_add_calibration);
        addListenerOnButton();
    }

    protected void onResume(){
        super.onResume();
        mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), menu_name, this);
    }
    @Override
    public void onNavigationDrawerItemSelected(int position) {
        mNavigationDrawerFragment.swapContext(position);
    }

    public void addListenerOnButton() {

        button = (Button) findViewById(R.id.save_calibration_button);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (Sensor.isActive()) {
                    EditText value = (EditText) findViewById(R.id.bg_value);
                    String string_value = value.getText().toString();
                    if (!TextUtils.isEmpty(string_value)){
                        double calValue = Double.parseDouble(string_value);


                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(AddCalibration.this);
                        String unit = prefs.getString("units", "mgdl");
                        double calValueMGDL = ("mgdl".equals(unit))?calValue:calValue*Constants.MMOLL_TO_MGDL;

                        if(calValueMGDL >=40 && calValueMGDL <=400){

                            Calibration calibration = Calibration.create(calValue, getApplicationContext());

                            Intent tableIntent = new Intent(v.getContext(), Home.class);
                            startActivity(tableIntent);
                            finish();
                        } else {
                            value.setError(getString(R.string.out_of_range));
                        }
                    } else {
                        value.setError(getString(R.string.calibration_cannot_be_blank));
                    }
                } else {
                    Log.w("CALERROR", "Sensor is not active, cannot calibrate");
                }
            }
        });

    }
}
