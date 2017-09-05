package com.parsin.bletool.View.Fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.parsin.bletool.Controller.OnFragmentInteractionListener;
import com.parsin.bletool.R;
import com.parsin.bletool.Utils.StaticObjects;
import com.parsin.bletool.internal.Constants;


public class SettingsFragment extends Fragment {

    private static final String TAG = SettingsFragment.class.getSimpleName();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;

    //private variables
    private TextView labelUserName;
    private TextView labelGroupName;
    private TextView labelServerName;
    private TextView learnInterval;
    private TextView trackInterval;
    private TextView trackCounter;
    private TextView learnPeriod;
    private TextView labelLearnInterval;
    private TextView labelTrackInterval;
    private TextView labelLearnPeriod;
    private TextView labelTrackCounter;
    private TextView labelOneScanPeriod;
    private TextView labelHowManyScan;
    private TextView fieldHowManyScan;
    private TextView fieldOneScanPeriod;
    private TextView labelParsinServerName;
    private TextView labelHowManyLearning;
    private TextView fieldHowManyLearning;


    private SharedPreferences sharedPreferences;
    private String prefUsername;
    private String prefServerName;
    private String prefGroupName;
    private int prefTrackInterval;
    private int prefLearnInterval;
    private int prefLearnPeriod;
    private String prefParsinServerName;
    private int prefOneScanPeriod;
    private int prefHowManyScan;
    private int prefHowManyLearning;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    // Required empty public constructor
    public SettingsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        sharedPreferences = getActivity().getSharedPreferences(Constants.PREFS_NAME, 0);
        prefUsername = sharedPreferences.getString(Constants.USER_NAME, Constants.DEFAULT_USERNAME);
        prefServerName = sharedPreferences.getString(Constants.SERVER_NAME, Constants.DEFAULT_SERVER);
        prefParsinServerName = sharedPreferences.getString(StaticObjects.PARSIN_SERVER_NAME, StaticObjects.ParsinServerIp);
        prefGroupName = sharedPreferences.getString(Constants.GROUP_NAME, Constants.DEFAULT_GROUP);
        prefTrackInterval = sharedPreferences.getInt(Constants.TRACK_INTERVAL, Constants.DEFAULT_TRACKING_INTERVAL);
        prefLearnInterval = sharedPreferences.getInt(Constants.LEARN_INTERVAL, Constants.DEFAULT_LEARNING_INTERVAL);
        prefLearnPeriod = sharedPreferences.getInt(Constants.LEARN_PERIOD, Constants.DEFAULT_LEARNING_PERIOD);
        prefOneScanPeriod = sharedPreferences.getInt(Constants.ONE_SCAN_PERIOD_NAME, Constants.ONE_SCAN_PERIOD);
        prefHowManyScan = sharedPreferences.getInt(Constants.LEARN_PERIOD, Constants.HOW_MANY_SCAN);
        prefHowManyLearning = sharedPreferences.getInt(Constants.HOW_MANY_LEARNING_NAME, Constants.HOW_MANY_LEARNING_DEFAULT);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);

        // Getting all the UI components
        labelUserName = (TextView)rootView.findViewById(R.id.fieldUserName);
        labelGroupName = (TextView)rootView.findViewById(R.id.fieldGroupName);
        labelServerName = (TextView)rootView.findViewById(R.id.fieldServerName);
        labelParsinServerName = (TextView)rootView.findViewById(R.id.fieldParsinServerName);
        labelLearnInterval = (TextView)rootView.findViewById(R.id.labelLearnInterval);
        labelTrackInterval = (TextView)rootView.findViewById(R.id.labelTrackInterval);
        labelLearnPeriod = (TextView)rootView.findViewById(R.id.labelLearnPeriod);
        labelTrackCounter = (TextView) rootView.findViewById(R.id.labelTrackCounter);
        learnInterval = (TextView)rootView.findViewById(R.id.fieldLearnInterval);
        learnPeriod = (TextView)rootView.findViewById(R.id.fieldLearnPeriod);
        trackCounter = (TextView) rootView.findViewById(R.id.fieldTrackCounter);
        trackInterval = (TextView)rootView.findViewById(R.id.fieldTrackInterval);
        labelOneScanPeriod = (TextView)rootView.findViewById(R.id.labelOneScanPeriod);
        labelHowManyScan = (TextView)rootView.findViewById(R.id.labelHowManyScan);
        fieldOneScanPeriod = (TextView)rootView.findViewById(R.id.fieldOneScanPeriod);
        fieldHowManyScan = (TextView)rootView.findViewById(R.id.fieldHowManyScan);
        labelHowManyLearning = (TextView) rootView.findViewById(R.id.labelHowManyLearning);
        fieldHowManyLearning = (TextView) rootView.findViewById(R.id.fieldHowManyLearning);

        // Rendering the setting page
        drawUi();

        labelHowManyLearning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder =
                        new AlertDialog.Builder(getActivity()).setTitle("How Many Learning?");
                final EditText editText = new EditText(getActivity());
                editText.setText(fieldHowManyLearning.getText());
                builder.setView(editText);
                builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String strUserName = editText.getText().toString();
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt(Constants.HOW_MANY_LEARNING_NAME, Integer.valueOf(strUserName));
                        fieldHowManyLearning.setText(strUserName);
                        Constants.HOW_MANY_LEARNING_DEFAULT = Integer.valueOf(strUserName);
                        editor.apply();
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        });

        labelHowManyScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder =
                        new AlertDialog.Builder(getActivity()).setTitle("How Many Scan?");
                final EditText editText = new EditText(getActivity());
                editText.setText(fieldHowManyScan.getText());
                builder.setView(editText);
                builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String strUserName = editText.getText().toString();
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt(Constants.HOW_MANY_SCAN_NAME, Integer.valueOf(strUserName));
                        fieldHowManyScan.setText(strUserName);
                        Constants.HOW_MANY_SCAN = Integer.valueOf(strUserName);
                        editor.apply();
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        });

        labelOneScanPeriod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder =
                        new AlertDialog.Builder(getActivity()).setTitle("Edit One Scan Period");
                final EditText editText = new EditText(getActivity());
                editText.setText(fieldOneScanPeriod.getText());
                builder.setView(editText);
                builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String strUserName = editText.getText().toString();
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt(Constants.ONE_SCAN_PERIOD_NAME, Integer.valueOf(strUserName));
                        fieldOneScanPeriod.setText(strUserName);
                        Constants.ONE_SCAN_PERIOD = Integer.valueOf(strUserName);
                        editor.apply();
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        });

        labelTrackCounter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder =
                        new AlertDialog.Builder(getActivity()).setTitle("Edit Track Counter");
                final EditText editText = new EditText(getActivity());
                editText.setText(trackCounter.getText());
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                builder.setView(editText);
                builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Integer intTracking = Integer.valueOf(editText.getText().toString());
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt(Constants.TrackCounterName, intTracking);
                        trackCounter.setText(intTracking);
                        Constants.DEFAULT_TRACKING_COUNTER = intTracking;
                        editor.apply();
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        });

        // UserName click listener
        labelUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder =
                        new AlertDialog.Builder(getActivity()).setTitle("Edit User name");
                final EditText editText = new EditText(getActivity());
                editText.setText(labelUserName.getText());
                builder.setView(editText);
                builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String strUserName = editText.getText().toString();
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(Constants.USER_NAME, strUserName);
                        labelUserName.setText(strUserName);
                        editor.apply();
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        });

        // GroupName click listener
        labelGroupName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder =
                        new AlertDialog.Builder(getActivity()).setTitle("Edit Group name");
                final EditText editText = new EditText(getActivity());
                editText.setText(labelGroupName.getText());
                builder.setView(editText);
                builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String strGrpName = editText.getText().toString();
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(Constants.GROUP_NAME, strGrpName);
                        labelGroupName.setText(strGrpName);
                        editor.apply();
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        });

        // ServerName click listener
        labelServerName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder =
                        new AlertDialog.Builder(getActivity()).setTitle("Edit Server address");
                final EditText editText = new EditText(getActivity());
                editText.setText(labelServerName.getText());
                builder.setView(editText);
                builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String strServerName = editText.getText().toString();
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(Constants.SERVER_NAME, strServerName);
                        Constants.DEFAULT_SERVER = strServerName;
                        labelServerName.setText(strServerName);
                        editor.apply();
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        });

        labelParsinServerName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder =
                        new AlertDialog.Builder(getActivity()).setTitle("Edit Server address");
                final EditText editText = new EditText(getActivity());
                editText.setText(labelParsinServerName.getText());
                builder.setView(editText);
                builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String strServerName = editText.getText().toString();
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(StaticObjects.PARSIN_SERVER_NAME, strServerName);
                        StaticObjects.ParsinServerIp = strServerName;
                        labelParsinServerName.setText(strServerName);
                        editor.apply();
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        });

        // Learning Interval click listener
        labelLearnInterval.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder =
                        new AlertDialog.Builder(getActivity()).setTitle("Edit Learning Period");

                final NumberPicker numberPicker = new NumberPicker(getActivity());
                builder.setView(numberPicker);
                String[] nums = new String[11];
                for(int i = 0; i < nums.length; i++)
                    nums[i] = Integer.toString(i);

                numberPicker.setMinValue(0);
                numberPicker.setMaxValue(nums.length-1);
                numberPicker.setWrapSelectorWheel(false);
                numberPicker.setDisplayedValues(nums);
                numberPicker.setValue(5);

                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        int learnIntervalVal = numberPicker.getValue();
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt(Constants.LEARN_INTERVAL, learnIntervalVal);
                        learnInterval.setText(String.valueOf(learnIntervalVal));
                        editor.apply();
                        dialog.dismiss();
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        });

        // Learn Period click listener
        labelLearnPeriod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder =
                        new AlertDialog.Builder(getActivity()).setTitle("Edit Learning Peri");

                final NumberPicker numberPicker = new NumberPicker(getActivity());
                builder.setView(numberPicker);
                String[] nums = new String[11];
                for(int i = 0; i < nums.length; i++)
                    nums[i] = Integer.toString(i);

                numberPicker.setMinValue(0);
                numberPicker.setMaxValue(nums.length-1);
                numberPicker.setWrapSelectorWheel(false);
                numberPicker.setDisplayedValues(nums);
                numberPicker.setValue(5);

                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        int learnPeriodlVal = numberPicker.getValue();
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt(Constants.LEARN_PERIOD, learnPeriodlVal);
                        learnPeriod.setText(String.valueOf(learnPeriodlVal));
                        editor.apply();
                        dialog.dismiss();
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        });

        // Track Interval click listener
        labelTrackInterval.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder =
                        new AlertDialog.Builder(getActivity()).setTitle("Edit Tracking Period");

                final NumberPicker numberPicker = new NumberPicker(getActivity());
                builder.setView(numberPicker);
                String[] nums = new String[16];
                for(int i = 0; i < nums.length; i++)
                    nums[i] = Integer.toString(i);

                numberPicker.setMinValue(0);
                numberPicker.setMaxValue(nums.length-1);
                numberPicker.setWrapSelectorWheel(false);
                numberPicker.setDisplayedValues(nums);
                numberPicker.setValue(5);

                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        int trackIntervalVal = numberPicker.getValue();
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt(Constants.TRACK_INTERVAL, trackIntervalVal);
                        trackInterval.setText(String.valueOf(trackIntervalVal));
                        Constants.ALT_BEACON_TRACK_INTERVAL_AMOUNT = trackIntervalVal;
                        editor.apply();
                        dialog.dismiss();
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        });

        return rootView;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    // Draw setting page UI
    private void drawUi() {
        // User name
        if(prefUsername != null && !prefUsername.isEmpty()) {
            labelUserName.setText(prefUsername);

        } else {
            labelUserName.setText(Constants.DEFAULT_USERNAME);
        }

        // Group name
        if(prefGroupName != null && !prefGroupName.isEmpty()) {
            labelGroupName.setText(prefGroupName);
        } else {
            labelGroupName.setText(Constants.DEFAULT_GROUP);
        }

        // Server name
        if(prefServerName != null && !prefServerName.isEmpty()) {
            labelServerName.setText(prefServerName);
        } else {
            labelServerName.setText(Constants.DEFAULT_SERVER);
        }
        // Parsin Server name
        if(prefParsinServerName != null && !prefParsinServerName.isEmpty()) {
            labelParsinServerName.setText(prefParsinServerName);
        } else {
            labelParsinServerName.setText(StaticObjects.ParsinServerIp);
        }

        // Track Interval
        trackInterval.setText(String.valueOf(prefTrackInterval));

        // Learn Interval
        learnInterval.setText(String.valueOf(prefLearnInterval));

        // Learn period
        learnPeriod.setText(String.valueOf(prefLearnPeriod));

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (OnFragmentInteractionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}