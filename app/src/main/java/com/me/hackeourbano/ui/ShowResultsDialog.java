package com.me.hackeourbano.ui;

import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.google.android.gms.maps.model.MarkerOptions;
import com.me.hackeourbano.R;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import butterknife.ButterKnife;

/**
 * @author Angel Gladin
 * @version 1.0
 * @since 06/03/2016
 */
public class ShowResultsDialog extends DialogFragment {
    @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_show_results, null);
        builder.setView(view);
        ButterKnife.bind(this, view);
        return builder.create();
    }

    public void onEvent(ArrayList<MarkerOptions> a){
        Log.e("XYZ", a.toString());

    }

}
