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


public class TACreateUserFragment extends Fragment 
{
	public interface TACreateUserDelegate
	{
		public void userWasCreated();
	}
	
	private ProgressDialog progress;
	private TACreateUserDelegate delegate;
	
    protected EditText usernameField;
    protected EditText passwordField;
    protected EditText firstNameField;
    protected EditText lastNameField;
    protected Button createButton;
    
    public void onAttach(Activity activity) 
    {
    	delegate = (TACreateUserDelegate) activity;

    	super.onAttach(activity);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
    	
    	View view = inflater.inflate(R.layout.fragment_create_user, container, false);
    	
        usernameField = (EditText) view.findViewById(R.id.loginField);
        passwordField = (EditText) view.findViewById(R.id.passwordField);
        firstNameField = (EditText) view.findViewById(R.id.firstnameField);
        lastNameField = (EditText) view.findViewById(R.id.lastnameField);

        createButton = (Button) view.findViewById(R.id.createUserButton);
        createButton.setOnClickListener(createClickListener);
        

        return view;

    }

    private View.OnClickListener createClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View view) 
        {
    	    final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
    	    imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
        	
        	String username = usernameField.getText().toString();
            if(validateUsername(username)){
                String password = passwordField.getText().toString();
                if(validatePassword(password)){
                    createUser(username, password, firstNameField.getText().toString(), lastNameField.getText().toString());
                }
            }
//    		TAMapFragment mapFragment = new TAMapFragment();
//    		FragmentTransaction fragmentTransaction =
//    				getFragmentManager().beginTransaction();
//    		fragmentTransaction.replace(R.id.activity_layout, mapFragment);
//    		fragmentTransaction.commit();

        }

		private void createUser(String username, String password, String firstName, String lastName) 
		{
			isLoading(true);
			TAWebService.createUser(username, password, firstName, lastName, getActivity(), new TAWebServiceDelegate() 
			{
				
				@Override
				public void webServiceDidFinishWithResult(Object result) 
				{
					isLoading(false);
					delegate.userWasCreated();
				}
				
				@Override
				public void webServiceDidFailWithError(String errorString) 
				{
					isLoading(false);
					
		        	AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()); 
		        	builder.setMessage(R.string.login_failed).setTitle("Error");
		        	builder.create().show();
					
				}
			});
			
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
