package edu.umbc.teamawesome.assignment3;

import java.io.InputStream;

public interface TAWebRequestDelegate 
{
	public void webRequestDidFinishWithResult(InputStream result);
	public void webRequestDidFailWithError(String errorString);

}
