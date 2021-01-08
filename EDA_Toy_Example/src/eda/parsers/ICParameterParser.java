package eda.parsers;

import eda.exceptions.ICParserException;

public class ICParameterParser {

// ATTRIBUTES
	
	private String [] _parameter;
	private String [][] _nonOptionalParameterDescription;
	private String [][] _optionalParameterDescription;
	private String _description;
	
// END OF ATTRIBUTES
	
// CONSTRUCTOR
	
	/**
	 * Constructor of the parser. 
	 * @param parameter - String containing the parameters to be parsed (the "args" parameter in the main function)
	 * @param nonOptionalParameterDescription - Array of size [n][2] where n is the number of non optional parameters of the program. 
	 * For each non optional parameter (i) the name ([i][0]) and the description ([i][1]) of the parameter have to be provided.
	 * @param optionalParameterDescription - Same as nonOptionalParameterDescription but for the optional parameters.
	 * @param description - String containing the description of the program 
	 */
	public ICParameterParser (String [] parameter, String [][] nonOptionalParameterDescription, String [][] optionalParameterDescription, String description)
	{
		_parameter = parameter;
		_description = description;
		_nonOptionalParameterDescription = nonOptionalParameterDescription;
		_optionalParameterDescription = optionalParameterDescription;
		
		if (getFlag ("-help"))
			System.out.println(getHelp());
		
	}//Constructor

// END OF CONSTRUCTOR
	
// PUBLIC METHODS
	/**
	 * Method to check flags in the parameters
	 * @param flag - Name of the flag checked
	 * @return true if the flag is in the list of parameters, false in other case
	 */
	public boolean getFlag (String flag)
	{
		boolean found = false;
		int i=0;
		while (!found && i<_parameter.length)
		{
			if (_parameter[i].compareTo("-" + flag)==0)
			{
				found = true;
			}
			else
			{
				i++;
			}//if
		}//while
		return found;
	}//getFlag
	
	/**
	 * Method to retrieve the value of a given parameter
	 * @param name - name of the parameter to be checked
	 * @return String with the value associated to the parameter checked
	 * @throws ICParserException - Exception cast when the parameter is not in the list
	 */
	public String getParameter (String name) throws ICParserException
	{
		boolean found = false;
		String paramValue = "";
		int i=0;
		try
		{
			while (!found && i<_parameter.length)
			{
				if (_parameter[i].compareTo("-" + name)==0)
				{
					found = true;
					paramValue = _parameter[i+1];
				}
				else
				{
					i++;
				}//if
			}//while
		}catch (ArrayIndexOutOfBoundsException e){throw new ICParserException ("The " + name + 
				" parameter is not a flag, it requires a value (for more help use the --help option)");}

		return paramValue;
	}//getParameter
	
	/**
	 * Method to get a String with the description of the program and the list of parameters
	 * @return String with the help of the program
	 */
	public String getHelp ()
	{
		String help = "";
		
		help += "PROGRAM DESCRIPTION:\n";
		help += _description + "\n\n";
		
		help += "REQUIRED PARAMETERS:\n";
		for (int i=0; i<_nonOptionalParameterDescription.length; i++)
			help += "-" + _nonOptionalParameterDescription[i][0] + ": " 
						+ _nonOptionalParameterDescription[i][1] + "\n";
		help += "\n";
		
		if (_nonOptionalParameterDescription != null)
		{
			help += "OPTIONAL PARAMETERS:\n";
			for (int i=0; i<_optionalParameterDescription.length; i++)
				help += "-" + _optionalParameterDescription[i][0] + ": " 
					+ _optionalParameterDescription[i][1] + "\n";
			help += "--help: Prints this help\n";
		
			help += "\n";
			help += "\n";
		}//if
		
		return help;
	}//getHelp()
	
// END OF PUBLIC METHODS

}//class