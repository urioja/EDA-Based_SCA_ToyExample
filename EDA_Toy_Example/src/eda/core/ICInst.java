package eda.core;

//import ICAtt.ICAttType;
import eda.core.ICAtt.ICAttType;
import eda.exceptions.ICException;
import eda.exceptions.ICInvalidMethodException;
import eda.exceptions.ICParameterException;
import eda.utils.ICDataUtils;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ICInst {

	// Code to identify the instance
	private String _id = "N/A";
	// List of the attributes of the instance
	private ArrayList <ICAtt> _attList;
	// List of index(es) of the attribute(s) that act as the class variable(s)
	private ArrayList <Integer> _classIndexes; 
	// Values of the different attributes in the instance
	private double [] _instanceValues;
	
	public static final double MISSING = Double.NaN;
	

	/**
	 * Complete constructor of the ICInst class
	 * @param attList - List of attributes of the class
	 * @param classIndexes - List of indexes of the class
	 * @param instanceValues - List of values of the instance
	 * @param id - Identifier of the class
	 */
	public ICInst (ArrayList <ICAtt> attList, ArrayList <Integer> classIndexes, double [] instanceValues, String id)
	{
		_attList = attList;
		_classIndexes = classIndexes;
		_instanceValues = instanceValues;
		_id = id;
	}//Constructor
	
	/**
	 * Basic constructor of the ICInst class for instances with a single class
	 * @param attList - List of attributes of the class
	 * @param classIndex - Indexes of the class attribute
	 * @param instanceValues - List of values of the instance
	 */
	public ICInst (ArrayList <ICAtt> attList, int classIndex, double [] instanceValues)
	{
		this(attList, null, instanceValues, "N/A");
		ArrayList <Integer> classIndexes = new ArrayList <Integer> ();
		classIndexes.add(classIndex);
		_classIndexes = classIndexes;
	}//Constructor
	
	/**
	 * Basic constructor for non-supervised instances
	 * @param attList - List of attributes of the class
	 * @param instanceValues - List of values of the instance
	 */
	public ICInst (ArrayList <ICAtt> attList, double [] instanceValues)
	{
		this(attList, null, instanceValues, "N/A");
	}//Constructor
	
	/**
	 * This constructor makes a full copy of instance
	 * @param instance - Instance to be copied
	 */
	public ICInst (ICInst instance)
	{
		//Copy the values of the instance
		_instanceValues = instance._instanceValues;
		
		
		//Copy the list of attributes
		_attList = new ArrayList <ICAtt> ();
		Object [] attributes = instance.attributes();
		for (int i=0; i<attributes.length; i++)
			_attList.add(new ICAtt ((ICAtt)attributes[i]));	
		
		
		//Copy the list of indexes of the class
		if (_classIndexes != null)
		{
			_classIndexes = new ArrayList <Integer> ();
			Object [] classList = instance.getClassIndexes().toArray();
			for (int i=0; i<classList.length; i++)
				_classIndexes.add(new Integer (((Integer)classList[i]).intValue()));
		}//if
		_id = "" + instance._id;		
	}//Constructor
	
	
	/**
	 * Method to obtain an enumeration of the attributes
	 * @return Enumeration of the attributes in the list of attributes
	 */
	public Object[] attributes ()
	{
		return _attList.toArray();
	}//enumerateAttributes
	
	/**
	 * Method to obtain the value of a given attribute in the instance. 
	 * @return index of the value of the attribute (if the attribute is categorical or a string, the index can be
	 * converted to the actual value using the method getValue@ICAtt 
	 */	
	public double getAttValue (int index)
	{
		return _instanceValues[index];
	}//getAttValue
	
	/**
	 * Method to obtain the list of indexes of the class
	 * @return Vector containing the list of indexes of the attributes that act as class
	 */
	public ArrayList <Integer> getClassIndexes ()
	{
		return _classIndexes;
	}//getClassIndexes
	
	/**
	 * Method to change the value of an attribute in the instance
	 * @param index - index of the attribute to change
	 * @param value - new value of the attribute
	 */
	public void setAttValue (int index, double value)
	{
		_instanceValues[index] = value;
	}//setAttValue
	
	/**
	 * Method to obtain the list of attributes
	 * @return Vector of ICAtt containing the list of attributes
	 */
	public ArrayList <ICAtt> getAttList ()
	{
		return _attList;
	}//getAttList
	
	/**
	 * Method to get the number of attributes
	 * @return number of attributes
	 */
	public int numAtts ()
	{
		return _attList.size();
	}//numAtts
	
	/**
	 * Method to get the number of classes
	 * @return number of classes
	 */
	public int numClasses ()
	{
		return _classIndexes.size();
	}//numAtts
	
	/**
	 * Method to check whether value at the given index is missing
	 * @param index - index of the value to check
	 * @return true if the value at "index" is missing
	 */
	public boolean isMissing (int index)
	{
		return Double.isNaN(_instanceValues[index]);
	}//isMissing
	
	/**
	 * Method to check whether there is more than one class attribute or not
	 * @return true if there is more than one class attribute
	 */
	public boolean isMulti ()
	{
		return (numClasses()>1);
	}//isMulti
	
	/**
	 * Method to check whether the instance has string attributes or not
	 * @return true if there is at least one string attribute
	 */
	public boolean hasStringAttributes ()
	{
		boolean hasStringAttributes = false;
		int index = 0;
		while (!hasStringAttributes && index<_attList.size())
		{
			if (_attList.get(index).getType()==ICAttType.string)
				hasStringAttributes = true;
			index++;
		}//while
		return hasStringAttributes;
	}//hasStringAttributes

	
	/**
	 * Method to change the list of attributes of the instance
	 * @param listOfAtt - new list of attributes
	 */
	public void setAttList (ArrayList <ICAtt> listOfAtt) throws ICParameterException, ICInvalidMethodException, ICException
	{
		//First we check whether the new list is compatible with the old one
		ArrayList <ICAtt> newList = ICDataUtils.merge(_attList, listOfAtt);
		if (newList == null)
		{
			throw new ICParameterException ("The new list of attributes is not compatible with existing attribute values");
		}else{
			//If there are string attributes we have to check translate the old indexes into new indexes in the new list of values
			if (hasStringAttributes())
			{
				ICAtt oldAttribute, newAttribute;
				for (int i=0; i<_attList.size(); i++)
				{
					oldAttribute = _attList.get(i);
					String value;
					if (oldAttribute.getType() == ICAttType.string)
					{
						newAttribute = newList.get(i);
                                                value = oldAttribute.getValue((int)_instanceValues[i]);
                                                _instanceValues[1] = newAttribute.getIndex(value);
					}
				}//for
				
				_attList = listOfAtt;
			}//if
		}//if
	}//setAttList

	/**
	 * Method to convert print out an instance
	 * @return String describing the instance
	 */
	public String toString()
	{
		String output=null;
		
                ICAtt currentAttribute = _attList.get(0);
                if (isMissing(0))
                {
                    output = "?";
                }else{
                    if (currentAttribute.getType() == ICAttType.numeric)
                    {
                        output = "" + _instanceValues[0];
                    }else{
                        String currentValue = null;
                        try {
                            currentValue = currentAttribute.getValue((int)_instanceValues[0]);
                        } catch (ICInvalidMethodException ex) {
                            Logger.getLogger(ICInst.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        if (currentValue.indexOf(' ')!=-1) //If the value has blank spaces then it has to be quoted
                            currentValue = "\"" + currentValue + "\"";
                        output = "" + currentValue;
                    }//if
                }//if
                for (int i=1; i<_instanceValues.length; i++)
                {
                    currentAttribute = (ICAtt)_attList.get(i);
                    if (isMissing(i))
                    {
                        output += ",?";
                    }else{
                        if (currentAttribute.getType()==ICAttType.numeric)
                        {
                            output += "," + _instanceValues[i];
                        }else{
                            String currentValue = null;
                            try {
                                currentValue = currentAttribute.getValue((int)_instanceValues[i]);
                            } catch (ICInvalidMethodException ex) {
                                Logger.getLogger(ICInst.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            if (currentValue.indexOf(' ')!=-1) //If the value has blank spaces then it has to be quoted
                                currentValue = "\"" + currentValue + "\"";
                            output += "," + currentValue;
                        }//if
                    }//if
                }//for
		return output;
	}//toString
	
}//class
