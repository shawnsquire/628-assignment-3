package edu.umbc.teamawesome.assignment3;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class TALoginFragment extends Fragment 
{
	public interface TALoginDelegate
	{
		public void userDidLogin();
	}

	
	private ProgressDialog progress;
	private TALoginDelegate delegate;
	
    protected EditText usernameField;
    protected EditText passwordField;
    protected Button loginButton;
    protected Button createUserButton;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
    	super.onCreate(savedInstanceState);
    	    	
    }
    
    @Override
    public void onAttach(Activity activity) 
    {
    	delegate = (TALoginDelegate) activity;

    	super.onAttach(activity);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
    	
    	View view = inflater.inflate(R.layout.fragment_login, container, false);
    	
        usernameField = (EditText) view.findViewById(R.id.loginField);
        passwordField = (EditText) view.findViewById(R.id.passwordField);
        
        loginButton = (Button) view.findViewById(R.id.loginButton);
        loginButton.setOnClickListener(loginClickListener);
        
        createUserButton = (Button) view.findViewById(R.id.createUserButton);
        createUserButton.setOnClickListener(createClickListener);

        return view;

    }

    private View.OnClickListener loginClickListener = new View.OnClickListener() 
    {
        @Override
        public void onClick(View view) {
            String username = usernameField.getText().toString();
            if(validateUsername(username)){
                String password = passwordField.getText().toString();
                if(validatePassword(password)){
                    login(username, password);
                }
            }
        }
    };
    
    private View.OnClickListener createClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View view) 
        {
        	delegate.userDidLogin();
        	
//    		TAMapFragment mapFragment = new TAMapFragment();
//    		FragmentTransaction fragmentTransaction =
//    				getFragmentManager().beginTransaction();
//    		fragmentTransaction.replace(R.id.activity_layout, mapFragment);
//    		fragmentTransaction.commit();

        }
    };
    
    public boolean validateUsername(String username)
    {
        if(username == null || username.length() < 1)
        {
        	AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()); 
        	builder.setMessage("Please enter a username.").setTitle("Error");
        	builder.create().show();
            return false;
        }
        return true;

    }

    public boolean validatePassword(String password)
    {
        if(password == null || password.length() < 1){
        	AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()); 
        	builder.setMessage("Please enter a password.").setTitle("Error");
        	builder.create().show();
            return false;
        }
        return true;
    }

    private void login(String username, String password)
    {
    	isLoading(true);
    	
    	TAWebService.authenticate(username, password, getActivity(), new TAWebServiceDelegate() 
    	{
			
			@Override
			public void webServiceDidFinishWithResult(Object result) 
			{
				isLoading(false);
				delegate.userDidLogin();
				Log.i("WEBSERVICE", "success");
			}
			
			@Override
			public void webServiceDidFailWithError(String errorString) 
			{
				isLoading(false);
				passwordField.setText("");
	        	AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()); 
	        	builder.setMessage(R.string.login_failed).setTitle("Error");
	        	builder.create().show();
				Log.i("WEBSERVICE", errorString);
			}
		});
    	
    }
    
    protected void isLoading(boolean isLoading)
    {
    	if(isLoading)
    	{
    		if(progress == null)
    		{
    			progress = new ProgressDialog(this.getActivity());
    			progress.setCancelable(false);
    			progress.setMessage("Loading ...");
    			progress.setIndeterminate(true);
    		}
			progress.show();
    	}
    	else
    	{
    		if(progress != null)
    			progress.dismiss();
    	}
    }

}
