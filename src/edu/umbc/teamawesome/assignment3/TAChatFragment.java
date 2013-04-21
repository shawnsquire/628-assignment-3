package edu.umbc.teamawesome.assignment3;
import edu.umbc.teamawesome.assignment3.TALoginFragment.TALoginDelegate;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;


public class TAChatFragment extends Fragment 
{
    
    public void onAttach(Activity activity) 
    {
    	super.onAttach(activity);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
    	
    	View view = inflater.inflate(R.layout.fragment_userlist, container, false);

        return view;

    }    
}
