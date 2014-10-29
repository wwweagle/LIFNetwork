/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package commonLibs;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Libra
 */
final public class GroupFireSaves implements Serializable {

    final private Integer simulateTime;
    final private Integer cellNumber;
//    final private ModelType type;
    final private Float connProb;
    final private Float weightScale;
    final private String hashString;
    final private List<int[]> fireList;

    public GroupFireSaves(Integer simulateTime, Integer cellNumber, /* ModelType type,*/ Float connProb, Float weightScale, String hashString, List<int[]> fireList) {
        this.simulateTime = simulateTime;
        this.cellNumber = cellNumber;
//        this.type = type;
        this.connProb = connProb;
        this.weightScale = weightScale;
        this.hashString = hashString;
        this.fireList = new ArrayList<>();
        this.fireList.addAll(fireList);
    }

    public float getPopulationFireFreq(int timePeriod, int proportion) {
        if (fireList.size() < 1) {
            return 0;
        }
        int eventsCount = 1;

        int grpFireCount = 0;
        for (int currentEventPtr = 1, currentStartTimePtr = 0; currentEventPtr < fireList.size(); currentEventPtr++) {
            if (fireList.get(currentEventPtr)[0] - fireList.get(currentStartTimePtr)[0] < timePeriod * 1000) {
                eventsCount++;
                if (eventsCount > (proportion * cellNumber / 100)) {
                    currentStartTimePtr = currentEventPtr + 1;
                    currentEventPtr++;
                    eventsCount = 1;
                    grpFireCount++;
                }
            } else {
                currentStartTimePtr++;
            }
        }
        return (float) grpFireCount / (simulateTime / (1000f * 1000f));
    }

    public int getMaxFirePopulation(int timePeriod) {
        if (fireList.size() < 1) {
            return 0;
        }
        int eventsCount = 1;
        int maxFreq = 0;
        for (int currentEventIndex = 1, currentStartTimeIndex = 0; currentEventIndex < fireList.size(); currentEventIndex++) {
            if (fireList.get(currentEventIndex)[0] - fireList.get(currentStartTimeIndex)[0] < timePeriod * 1000) { //microseconds, uS
                //less than 1ms
                eventsCount++;
            } else {
                if (eventsCount > maxFreq) {
                    maxFreq = eventsCount;
                }
                currentStartTimeIndex++;
            }
        }
        return maxFreq;
    }

    public Float getConnProb() {
        return connProb;
    }

    public Float getWeightScale() {
        return weightScale;
    }

    public String getHashString() {
        return hashString;
    }

//    public ModelType getType() {
//        return type;
//    }
    
    
}
