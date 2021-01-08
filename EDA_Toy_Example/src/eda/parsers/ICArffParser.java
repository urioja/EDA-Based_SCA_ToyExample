package eda.parsers;

import eda.core.ICAtt;
import eda.core.ICData;
import eda.core.ICInst;
import eda.core.ICAtt.ICAttType;
import eda.exceptions.ICException;
import eda.exceptions.ICParameterException;
import eda.exceptions.ICParserException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
public class ICArffParser {

	public final static String NAMEID = "@relation";
	public final static String ATTID = "@attribute";
	public final static String DATAID = "@data";
	public final static String ATTNUMERIC = "numeric";
	public final static String ATTSTRING = "string";
	public final static String ATTDATE = "date";
	
	@SuppressWarnings("unchecked")
	public static ICData load (Reader input) throws IOException, ICParserException, ICParameterException
	{
		BufferedReader in = new BufferedReader(input);
		
		String line = in.readLine();
		boolean nextStep = false;
		String datasetName = "No name";
		ArrayList listOfAttributes = new ArrayList();
		ArrayList listOfInstances = new ArrayList();
		
		//First step, search for the name of the dataset
		while (!nextStep)
		{
			while (line.compareTo("")==0) //remove blank lines
				line = in.readLine();
			
			StringTokenizer st = new StringTokenizer (line);
			String firstToken = st.nextToken();
			if (firstToken.compareToIgnoreCase(NAMEID)==0)
			{
				try
				{
					int firstQuote = line.indexOf('"');
					if (firstQuote == -1)
					{
						datasetName = st.nextToken();
					}else{
						int lastQuote = line.lastIndexOf('"');
						datasetName = line.substring(firstQuote+1, lastQuote);
					}//if
					line = in.readLine();
				}catch (NoSuchElementException e){/*If no name is given for the dataset the default value "No name" is used*/}
				
			}else if (firstToken.compareToIgnoreCase(ATTID)==0){
				nextStep = true;
			}else{
				line=in.readLine();
			}//if
		}//while - step 1
		
		nextStep = false;
		
		while (!nextStep)
		{
			while (line.compareTo("")==0) //remove blank lines
				line = in.readLine();
			
			StringTokenizer st = new StringTokenizer (line);
			
			String firstToken = st.nextToken();
		
			if (firstToken.compareToIgnoreCase(ATTID)==0)
			{
				String typeOfAttribute = "";
				ICAttType type;
				String attName = "";
				try
				{

					//Check whether we have a quoted name of the attribute or not
					int firstQuote = line.indexOf('"');
					if (firstQuote != -1 && (firstQuote < line.indexOf('{') || line.indexOf('{')==-1))
					{
						int secondQuote = line.indexOf('"',firstQuote+1);
						attName = line.substring(firstQuote+1, secondQuote);
						st = new StringTokenizer (line.substring(secondQuote+1));
					}else{
						attName = st.nextToken();
					}//if
					
					typeOfAttribute = st.nextToken();
				}catch (NoSuchElementException e) {throw new ICParserException ("All the attributes defined have to have a name and a type");}

				ArrayList listOfValues = null;	
				//Now we check the type of attribute we have
				if (typeOfAttribute.compareToIgnoreCase(ATTNUMERIC)==0)
				{
					type = ICAttType.numeric;
				}else if (typeOfAttribute.compareToIgnoreCase(ATTSTRING)==0){
					type = ICAttType.string;
				}else if (typeOfAttribute.compareToIgnoreCase(ATTDATE)==0){
					type = ICAttType.string;
				}else if (typeOfAttribute.indexOf('{')!=-1){
					type = ICAttType.categorical;
					//If we have a categorical attribute we have to obtain the complete list of values
					String values = line.substring(line.indexOf('{')+1, line.indexOf('}'));
					st = new StringTokenizer (values, ",");
					listOfValues = new ArrayList ();
					while (st.hasMoreTokens()) //Blank spaces are removed before the value is added
						listOfValues.add(removeBlankSpacesAndQuotes(st.nextToken()));
				}else{
					throw new ICParserException ("Unknown type dectected while parsing the arff file: " + typeOfAttribute);
				}//if
			
				//Finally, we create the new attribute and we add it to the list of attributes
				if (type == ICAttType.categorical)
					listOfAttributes.add(new ICAtt(attName, type, listOfValues));
				else
					listOfAttributes.add(new ICAtt(attName,type));	
				
				line = in.readLine();
			}else if (firstToken.toLowerCase().compareTo(DATAID)==0){
				nextStep = true;
			}else{
				line=in.readLine();
			}//if
		}//while - step 2
		
		while (line!=null)
		{
			while (line.compareTo("")==0) //remove blank lines
				line = in.readLine();
			
			StringTokenizer st = new StringTokenizer (line);
			
			//We check whether the line at hand contains values or not (i.e., whether it represents an instance or not)
			if (line.indexOf(',')!=-1)
			{
				st = new StringTokenizer(line,",");
				double [] instanceValues = new double [listOfAttributes.size()];
				ICAtt currentAttribute;
				for (int att=0; att<instanceValues.length; att++)
				{
					currentAttribute = ((ICAtt)listOfAttributes.get(att));
					try
					{
						String currentValue = st.nextToken();
						if (currentValue.compareTo("?")==0)
						{
							instanceValues[att] = ICInst.MISSING;
						}else{
							switch (currentAttribute.getType()){
								case numeric:
									instanceValues [att] = Double.parseDouble(currentValue);
									break;
								case categorical:
									instanceValues [att] = currentAttribute.getIndex(removeBlankSpacesAndQuotes(currentValue));
									break;
								case string:
									instanceValues [att] = currentAttribute.addStringValue(removeBlankSpacesAndQuotes(currentValue));
									break;
							}//switch
						}//if
					}catch(ICException e){e.printStackTrace();}
				}//for
			
				ICInst newInstance = new ICInst(listOfAttributes, instanceValues);
				listOfInstances.add(newInstance);
			}//if
			line = in.readLine();
		}//while - step 3

		//Finally, we create the dataset 
		
		ICData dataset = new ICData(listOfInstances);
		dataset.setName(datasetName);
		
		return dataset;
	}//read
	
	public static void save (ICData dataset, Writer output){
		PrintWriter out = new PrintWriter(output);
		out.print(print(dataset));
		out.flush();
		out.close();
	}//save
	
	
	
	public static String print(ICData dataset)
	{
		String s = "";
		
		//Header of the file
		s += NAMEID + " " + dataset.getName() + "\n\n";
		
		Object [] attributes =  dataset.attributes();
		for (int i=0; i<attributes.length; i++)
			s += ((ICAtt)attributes[i]).toString() + "\n";
		
		s += "\n";
		
		s += DATAID + "\n";
		
		Object [] instances = dataset.instances();
		for (int i=0; i<instances.length; i++)
			s += ((ICInst)instances[i]).toString() + "\n";
		
		return s;
	}//print

	private static String removeBlankSpacesAndQuotes (String s)
	{
		//First we remove the blank spaces at the begining (if there is any)
		int first = 0;
		if (s.charAt(first)==' ')
		{
			//look for the first non-blank space character
			while (s.charAt(first)==' ')
				first++;
			s = s.substring(first);
		}//if
		
		//Idem for the end of the string
		int last = s.length()-1;
		if (s.charAt(last) ==' ')
		{
			while (s.charAt(last) ==' ')
				last--;
			s = s.substring(0, last+1);
		}//if
		
		//Finally, if the value is quoted, remove the quotes
		
		if (s.charAt(0)=='"' && s.charAt(s.length()-1)=='"')
			s=s.substring(1, s.length()-1);
			
		
		return s;
	}
}//class
