package eda.core;

import java.util.ArrayList;
import eda.parsers.ICArffParser;
import eda.exceptions.ICException;
import eda.exceptions.ICInvalidMethodException;
import eda.exceptions.ICParameterException;

public class ICAtt 
{
	
	private String _name;
	private ICAttType _type; 
	private ArrayList <String> _values;
	public enum ICAttType {numeric, categorical, string}
	public ICAtt (String name, ICAttType type) throws ICParameterException {
		_name = name;
		_type = type;
		if (_type!=ICAttType.numeric)
			_values = new ArrayList <String> ();
	}
        public ICAtt (String name, ICAttType type, ArrayList <String> listOfValues) throws ICParameterException  {
            this(name,type);
            if (type == ICAttType.numeric)
                    throw new ICParameterException("Continuous attributes cannot be constructed with a list of values");
            Object [] valueList = listOfValues.toArray();
            int index = 0;
            for (int i=0; i<valueList.length; i++)
            {
                String value = (String) valueList[i];
                if (_values.contains(value))
                {
                        if (_type == ICAttType.categorical) //No repeated values are allowed in categorical attributes (only in string attributes)
                                throw new ICParameterException ("There cannot be duplicated values (" + value + 
                                                ") in continuous attributes (" + name + ")");

                } else {
                        _values.add(value);
                }

                index++;
            }
	}
	public ICAtt (ICAtt attribute)  {
            _type = attribute._type;
            _name = ""+attribute._name;

            if (attribute._values!=null)
            {
                _values =  new ArrayList <String> ();

                Object [] listOfValues = attribute.getValues().toArray();
                for (int i=0; i<listOfValues.length; i++)
                    _values.add((String)listOfValues[i]);
            }		
	}
	public String getValue (int index) throws ICInvalidMethodException
	{
            if (this._type == ICAttType.numeric)
                return ""+index;
            else
                return (String) _values.get(index);
	}
	public ICAttType getType ()
	{
		return _type;
	}
	public String getName ()
	{
		return _name;
	}
	public ArrayList <String> getValues ()
	{
		return _values;
	}
	public int getIndex (String value) throws ICException
	{
		int index=-1;
		//PARAMETER CONTROL
		if (this._type == ICAttType.numeric)
		{
			throw new ICInvalidMethodException ("getIndex()@ICAtt is not applicable to numerical attributes, and " + _name + " is a numerical attribute");
		}else{
			index = _values.indexOf(value); 
			if (index==-1)
				throw new ICException ("Value " + value + " cannot be found in attribute " + _name);
		}//if
		return index;
	}
	public void setName (String pNewName)
	{
		_name = pNewName;
	}
	public int getAttCardinality () throws ICInvalidMethodException
	{
		//PARAMETER CONTROL
		if (_type != ICAttType.categorical)
			throw new ICInvalidMethodException ("getAttCardinality()@ICAtt is only applicable for categorical attributes, and " + _name + " is not");
		
		return _values.size();
	}
	public int addStringValue (String value) throws ICInvalidMethodException
	{
		//PARAMETER CONTROL
		if (_type != ICAttType.string)
			throw new ICInvalidMethodException ("AddStringValue(String value)@ICAtt is only applicable to string attributes, and " + _name + " is not a string attribute");
		
		int index = _values.indexOf(value);
		if (index==-1)
		{
			_values.add(value);
			index = _values.indexOf(value);
		}//if
		return index;
	}
	public String toString()
	{
		String output = ICArffParser.ATTID + " ";
		if (_name.indexOf(' ')!=-1) 
			output += "\"" + _name + "\"";
		else
			output += _name;
		
		switch (_type) {
		case numeric:
			output += " " + ICArffParser.ATTNUMERIC ;
			break;
		case string:
			output += " " + ICArffParser.ATTSTRING;
			break;
		case categorical: 
			output += " {";
			String currentValue = (String)_values.get(0);
			if (currentValue.indexOf(' ')!=-1) 
				currentValue = "\"" + currentValue + "\"";
			output += currentValue;
			for (int i=1; i< _values.size(); i++)
			{
				currentValue = (String)_values.get(i);
				if (currentValue.indexOf(' ')!=-1) 
					currentValue = "\"" + currentValue + "\"";
				output += "," + currentValue;
			}//for
			output += "}";
			break;
		default:
			output += " unknown type!!";
		} 
		
		return output;
	}	
}
