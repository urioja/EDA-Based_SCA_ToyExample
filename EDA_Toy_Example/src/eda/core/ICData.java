package eda.core;

import eda.core.ICAtt.ICAttType;
import eda.exceptions.ICException;
import eda.exceptions.ICParameterException;
import eda.exceptions.ICParserException;
import eda.parsers.ICArffParser;
import eda.utils.ICDataUtils;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class ICData {
	
	private String _name = "no.name";
	
	// List of the attributes of the instance
	private ArrayList <ICAtt> _attList;
	
	// List of index(es) of the attribute(s) that act as the class variable(s)
	private ArrayList <Integer> _classIndexes; 
	
	// List of instances in the dataset
	private ArrayList <ICInst> _instances;

	/**
	 * Constructor of the class based on a vector of instances. The compatibility of the instances has to be
	 * checked, increasing the computational time required to construct the object. For a faster method use the
	 * constructor based on an array of doubles
	 * @param listOfInstances - Vector containing objects of type ICInst
	 */
	public ICData (ArrayList <ICInst> listOfInstances) throws ICParameterException
	{
		//Parameter check
		if (!this.checkListOfInstances(listOfInstances))
			throw new ICParameterException ("The instances used to create the object are not compatible");
		
		_attList = listOfInstances.get(0).getAttList();
		_classIndexes = listOfInstances.get(0).getClassIndexes();
		_instances = listOfInstances;
	}//ICData
	
	public ICData (double [][] instance, ArrayList <ICAtt> listOfAttributes, ArrayList <Integer> classIndexes) throws ICParameterException
	{
		_instances = new ArrayList <ICInst> ();
	
		// Check that all the instances have valid values for all the attributes and add them to the list of instances
		double [] currentInstance;
		for(int inst=0; inst<instance.length; inst++)
		{
			currentInstance = instance[inst];
			for (int index=0; index < currentInstance.length; index++)
				if (!(listOfAttributes.get(index).getType() == ICAttType.numeric))
					try
					{	
						if (currentInstance[index] >= listOfAttributes.get(index).getAttCardinality())
							throw new ICParameterException ("The instances used to create the object are not compatible");
					}catch(Exception e){/*An exception is thrown if the attribute is numerical, but we have checked it*/};
			_instances.add(new ICInst(listOfAttributes, classIndexes, currentInstance,"N/A"));
		}//for
	}//ICData

	
	/**
	 * This constructor makes a full copy of the ICData object
	 * @param dataset - ICData to be copied
	 */
	public ICData (ICData dataset)
	{
		// Create a name for the new dataset
		_name = "Copy.of." + dataset.getName();
	
		//Copy the list of attributes
		_attList = new ArrayList <ICAtt> ();
		Object [] attributes = dataset.attributes();
		for (int i=0; i<attributes.length; i++)
			this._attList.add(new ICAtt ((ICAtt)attributes[i]));	
	
		//Copy the list of attributes
		_instances = new ArrayList <ICInst> ();
		Object [] instances = dataset.instances();
		for (int i=0; i<instances.length; i++)
			this._instances.add(new ICInst ((ICInst)instances[i]));	
		
		if (dataset._classIndexes != null)
		{
			_classIndexes = new  ArrayList <Integer> ();
			Object [] classes = dataset.getClassIndexes().toArray();
			for (int i=0; i<classes.length;i++)
				_classIndexes.add(((Integer)classes[i]).intValue());
		}//if
	}//ICData
	
	public ICData (String arffFilePath) throws ICParserException, ICParameterException, IOException
	{
		this(ICArffParser.load(new FileReader(arffFilePath)));
		
	}//ICData
	
	/**
	 * This method returns the number of instances
	 * @return number of instances
	 */
	public int numInstances ()
	{
		return _instances.size();
	}//numInstances
	
	/**
	 * This method returns the number of attributes
	 * @return number of attributes
	 */
	public int numAttributes ()
	{
		return _attList.size();
	}//numAttributes
	
	/**
	 * This method returns the name of the dataset
	 * @return name of the dataset
	 */
	public String getName ()
	{
		return _name;
	}//getName
	
	/**
	 * Method to obtain an enumeration of the attributes
	 * @return Enumeration of the attributes in the list of attributes
	 */
	public Object [] attributes ()
	{
		return _attList.toArray();
	}//enumerateAttributes
	
	/**
	 * Method to obtain an enumeration of the attributes
	 * @return Enumeration of the attributes in the list of attributes
	 */
	public Object [] instances ()
	{
		return _instances.toArray();
	}//enumerateAttributes

	
	/**
	 * Method to obtain the list of indexes of the class
	 * @return Vector containing the list of indexes of the attributes that act as class
	 */
	public ArrayList <Integer> getClassIndexes ()
	{
		return _classIndexes;
	}//getClassIndexes

	
	/**
	 * Method to set the name of the dataset
	 * @param new name of the dataset
	 */
	public void setName (String name)
	{
		_name = name;
	}//getClassIndexes

	
	/**
	 * Method to add a set of instances to the dataset
	 * @param instances - instances to add
	 */
	public void addInstances (ArrayList <ICInst> instances) throws ICParameterException, ICException
	{
		//We have to check the compatibility of each instance and then add it to the dataset
		for (int i=0; i<instances.size(); i++)
		{
			ICInst currentInstance = instances.get(i);
			//PARAMETER CONTROL
			ArrayList <ICAtt> listOfAttributes = ICDataUtils.merge(currentInstance.getAttList(),_attList);
			if (listOfAttributes == null)
			{
				throw new ICParameterException ("The list of attributes in the instances and in " 
						+ _name + " are not compatible");
			}else{
				if (currentInstance.hasStringAttributes())//The indexes of the string attributes has to be revised
				{
					_attList = listOfAttributes;//If there are string attributes the list of attributes may have changed
					for (int j=0;j<_instances.size(); j++)
						_instances.get(j).setAttList(_attList);
						
						
				}else{
					_instances.add(instances.get(i));
				}//if
			}//if
		}//for
	}//addInstances
	
	
	/**
	 * Method for removing instances
	 * @param indexes - List of indexes to keep / remove
	 * @param remove - If true the instances pointed by "indexes" are remove, otherwise they are conserved
	 */
	public void filterInstances (int [] indexes, boolean remove)
	{
		ArrayList <ICInst> instances = new ArrayList <ICInst> (); 
		for (int i=0; i<indexes.length; i++)
			instances.add(_instances.get(indexes[i]));
		
		if (remove)
			_instances.removeAll(instances);
		else
			_instances.retainAll(instances);
	}//filterInstances
	
	/**
	 * Method for combining two datasets. The method checks that the list of attributes are equal in both datasets. If they are not the method returns null
	 * If the list of classes is not the same in both datasets the list is taken from the dataset that invokes the method.
	 * @param dataset - dataset to merge with
	 * @return new dataset (or null if the two datasets are not compatible)
	 */
	public ICData merge (ICData dataset) throws ICException
	{
		
		ICData newData = null;
		try
		{
			newData = new ICData (this);
			newData.addInstances(dataset._instances);
			newData.setName("merge.of." + _name + ".and." + dataset.getName());
		}catch (ICParameterException e){
			/*The exception is thrown when the attributes in the new and current dataset are not compatible */
		}//try
		return newData;
	}
	public String toString ()
	{
		return ICArffParser.print(this);
		
	}
	private boolean checkListOfInstances (ArrayList <ICInst> listOfInstances)
	{
		boolean correct = true;
		
		return correct;
	}
}