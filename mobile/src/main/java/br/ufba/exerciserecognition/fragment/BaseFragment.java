package br.ufba.exerciserecognition.fragment;

import android.app.Fragment;
import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.List;

import br.ufba.exerciserecognition.BaseActivity;
import br.ufba.exerciserecognition.listener.ActionListener;

/**
 * Created by Igor Lopes de Faria on 08/04/15.
 */
public class BaseFragment extends Fragment implements ActionListener {


    public BaseActivity getBaseActivity(){
        return ((BaseActivity)getActivity());

    }

    @Override
    public void onSynchronizeCompleted(Object object) {
    }

    @Override
    public void onSynchronizeCompleted() {
    }

    @Override
    public void onSynchronizeCompleted(List<? extends Object> list) {
    }

    @Override
    public void onSynchronizeFailed(String message) {
    }

    public void showSnackBar(View view, String message, Integer duration, int colorText, int colorBackground ){
        Snackbar snack = Snackbar.make(view, message, duration);
        View snackbarView = snack.getView();
        TextView tv = (TextView) snack.getView().findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(ContextCompat.getColor(getBaseActivity(), colorText));
        snackbarView.setBackgroundColor(ContextCompat.getColor(getBaseActivity(), colorBackground));
        tv.setGravity(Gravity.CENTER_HORIZONTAL);
        snack.show();
    }

}
