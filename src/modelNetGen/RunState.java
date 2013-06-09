/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package modelNetGen;

/**
 *
 * @author Libra
 */
public enum RunState {

    Instantiated,
    ReadyGenCells, GeneratingCells, CellsGenerated,
    ReadyGenMonitor, GeneratingMonitor, MonitorGenerated,
    ReadyGenNet, GeneratingNet, NetGenerated,
    UserRequestStop, StoppedByUser
}
