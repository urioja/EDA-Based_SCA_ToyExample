package eda.utils;

import eda.core.ICAtt;
import eda.exceptions.ICInvalidMethodException;
import java.util.ArrayList;

public class ICDataUtils {
	
	/**
	 * This method merges two sets of attributes. The method checks whether the two lists are
	 * compatible. If there are string attributes the output is a new list with string attributes containing 
	 * all the possible values of the two lists. It the two lists are not compatible the output is null
	 * @param attList1 - List of attributes to merge
	 * @param attList2 - List of attributes to merge
	 * @return new list of attributes (with merged string attributes) or null if the two lists are not compatible
	 */
	public static ArrayList <ICAtt> merge (ArrayList <ICAtt> attList1, ArrayList <ICAtt> attList2)
	{
		boolean compatible = true;
		
		ArrayList <ICAtt> newAttList = new ArrayList <ICAtt> ();
		
		if (attList1.size()!=attList2.size())
		{
			compatible=false;
			newAttList=null;
		}else{
			int index = 0;
			while (compatible && index<attList1.size())
			{
				ICAtt att1 = attList1.get(index);
				ICAtt att2 = attList2.get(index);
				if (att1.getType() != att2.getType())
				{
					compatible=false;
					newAttList = null;
				}else{
					switch (att1.getType())
					{
						case numeric:
							//For numeric attributes we simply copy the attribute from one of the lists
							newAttList.add(att1);
							break;
						case categorical:
							//If categorical then we have to check that in both lists we have the same possible values
							try
							{
								if (att1.getAttCardinality() != att2.getAttCardinality())
								{
									compatible=false;
									newAttList = null;
								}else{//Check that all the values are the same
									ArrayList <String> listOfValues1 = att1.getValues();
									ArrayList <String> listOfValues2 = att2.getValues();
									int val = 0;
									boolean ok = true;
									while (ok && val<att1.getAttCardinality())
									{
										if (listOfValues1.get(val).compareTo(listOfValues2.get(val))!=0)
										{
											ok=false;
											newAttList = null;
										}//if
										val++;
									}//while
									if (ok) //If all the values are the same, then we add the attribute to the list
										newAttList.add(att1);
								}//if
							}catch(ICInvalidMethodException e){/* Thrown when the method is applied to non-categorical attributes */}
							break;
						case string:
							//This is the most delicate step. If we have a string attribute it is possible that the values in each of the lists are not the same
							//The final attribute should have all the values (i.e., we have to merge the values of the attribute)
							ArrayList <String> listOfValues = att2.getValues();
							ICAtt newAttribute = new ICAtt(att1);
							for (int i=0; i<listOfValues.size(); i++)
								try
								{
									newAttribute.addStringValue(listOfValues.get(i));
								}catch(ICInvalidMethodException e){/*The exception is only thrown when the method is applied to a non-string attribute*/}
							newAttList.add(newAttribute);
							
							break;
					}//switch
				}//if
				index++;
			}//while
			return newAttList;
		}//if
		
		return newAttList;
	}//checkListOfInstances

}
