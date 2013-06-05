/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.scify.jthinkfreedom.stimuli;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.scify.jthinkfreedom.reactions.IReactor;

/**
 *
 * @author nikos
 */

//The object must be something that will help us to identify the mouse click? Timepressed, time released, location, etc
public class MouseLeftClickStimulus extends StimulusAdapter<Object>{

    
    List<Integer> rangeMaximums = new ArrayList<Integer>() {};
    HashMap<Integer, List<IReactor>> reactorsMap = new HashMap<Integer,List<IReactor>>();
    
    
    
    @Override
    public void onDataReceived() {
        
        
        //at some point we will call
        //getReactors(Integer clickDuration)  and we will assign it to lReactors that are inherited 
        //in order to call the callReactors() without problem
        
        
        
    }
    
    
    /**Returns the milliseconds that the 
     * users was holding down the left click.
     * 
     * 
     * @return The period that left click was pressed in milliseconds
     */
    private int getDurationUntilRelease(){
        
        return 0;
    }    
    
    /**This method returns the list of the reactors that has to be 
     * called according to the duration of the click.
     * 
     * @param clickDuration The time that the click was pressed
     * @return The list of the reactors associated with the specific duration (Classification mapping)
     */
    public List<IReactor> getReactors(Integer clickDuration){
        return null;
    }
    
    public void setRangeMaximum(Integer rangeMaximum){
        rangeMaximums.add(rangeMaximum);
    }
    
    //This overloads the addReactor of the super. Is this ok?
    public void addReactor(Integer rangeMaximum, IReactor reactor){
        
        if(rangeMaximums.contains(rangeMaximum)){
            List<IReactor> reactors = reactorsMap.get(rangeMaximum);
            if(reactors!=null){
                reactors.add(reactor);
                reactorsMap.put(rangeMaximum, reactors);
            }
        }
        
    }
    
}
