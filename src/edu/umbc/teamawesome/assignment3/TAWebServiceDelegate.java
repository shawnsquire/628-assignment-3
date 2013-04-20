package edu.umbc.teamawesome.assignment3;

public interface TAWebServiceDelegate 
{
	public void webServiceDidFinishWithResult(Object result);
	public void webServiceDidFailWithError(String errorString);

}
