/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package modelNetGen;

//import iodegree.lib.D;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 *
 * @author Libra
 */
public class ModelDB {

    private String pathToFile;
    private HashSet<Integer> allSlot = new HashSet<>();

    public ModelDB(String pathToFile) {
        this.pathToFile = pathToFile;
    }

    private ArrayList<PotentialSynapse> getSlots() {
        ArrayList<PotentialSynapse> slots = new ArrayList<>(500);
        String jdbcPath = "jdbc:odbc:DRIVER="
                + "Microsoft Access Driver (*.mdb, *.accdb);DBQ=" + this.pathToFile;
        try (Connection con = DriverManager.getConnection(jdbcPath)) {
            try (Statement stmt = con.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY)) {
                String qryStr = "SELECT DISTINCT tblCellwCoord.RecordDate, tblCellwCoord.CultureDate, tblCellwCoord.GroupNo, tblCellwCoord.DIV, tblCellwCoord.CellID AS id1, tblCellwCoord_1.CellID AS id2, tblCellwCoord.GAD, tblCellwCoord_1.GAD AS revGAD, tblCellwCoord.CellX, tblCellwCoord.CellY, tblCellwCoord_1.CellX, tblCellwCoord_1.CellY "
                        + "FROM tblCellwCoord INNER JOIN tblCellwCoord AS tblCellwCoord_1 ON tblCellwCoord.GroupNo = tblCellwCoord_1.GroupNo AND tblCellwCoord.RecordDate = tblCellwCoord_1.RecordDate "
                        + "WHERE tblCellwCoord_1.CellID<>tblCellwCoord.CellID "
                        + "ORDER BY tblCellwCoord.RecordDate, tblCellwCoord.GroupNo, tblCellwCoord.CellID;";
                ResultSet rs = stmt.executeQuery(qryStr);
                while (rs.next()) {
                    PotentialSynapse aSlot = new PotentialSynapse();
                    aSlot.setDate(rs.getDate(1));
                    aSlot.setGroup(rs.getInt(3));
                    aSlot.setDiv(rs.getInt(4));
                    aSlot.setId1(rs.getInt(5));
                    aSlot.setId2(rs.getInt(6));
                    aSlot.setFwdGlu(!rs.getBoolean(7));//GAD
                    aSlot.setRevGlu(!rs.getBoolean(8));//GAD
                    int x1 = rs.getInt(9);
                    int y1 = rs.getInt(10);
                    int deltaX = rs.getInt(11) - x1;
                    int deltaY = rs.getInt(12) - y1;
                    int dist = (int) Math.round(Math.sqrt(deltaX * deltaX + deltaY * deltaY) * 0.638f);
                    aSlot.setDist(dist);
                    slots.add(aSlot);
                }
            }

        } catch (SQLException e) {
            System.out.println(e.toString());
        }
        return slots;
    }

    private ArrayList<PotentialSynapse> getConns() {
        ArrayList<PotentialSynapse> conns = new ArrayList<>(500);
        String jdbcPath = "jdbc:odbc:DRIVER="
                + "Microsoft Access Driver (*.mdb, *.accdb);DBQ=" + this.pathToFile;
        try (Connection con = DriverManager.getConnection(jdbcPath)) {
            try (Statement stmt = con.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY)) {
                String qryStr = "SELECT DISTINCT tblCellwCoord.DIV, tblCellwCoord.GAD, tblCellwCoord_1.GAD, tblCellwCoord.CellX, tblCellwCoord.CellY, tblCellwCoord_1.CellX, tblCellwCoord_1.CellY, tblConnection.PreCellID, tblConnection.PostCellID "
                        + "FROM (tblConnection INNER JOIN tblCellwCoord ON tblConnection.PreCellID = tblCellwCoord.CellID) INNER JOIN tblCellwCoord AS tblCellwCoord_1 ON tblConnection.PostCellID = tblCellwCoord_1.CellID; ";
                ResultSet rs = stmt.executeQuery(qryStr);
                while (rs.next()) {
                    PotentialSynapse aConn = new PotentialSynapse();
                    aConn.setDiv(rs.getInt(1));
                    aConn.setFwdGlu(!rs.getBoolean(2));//GAD
                    aConn.setRevGlu(!rs.getBoolean(3));//GAD
                    int x1 = rs.getInt(4);
                    int y1 = rs.getInt(5);
                    int deltaX = rs.getInt(6) - x1;
                    int deltaY = rs.getInt(7) - y1;
                    int dist = (int) Math.round(Math.sqrt(deltaX * deltaX + deltaY * deltaY) * 0.638f);
                    aConn.setDist(dist);
                    aConn.setId1(rs.getInt(8));
                    aConn.setId2(rs.getInt(9));
                    conns.add(aConn);
                }
            }

        } catch (SQLException e) {
            System.out.println(e.toString());
        }
        return conns;
    }

    private ArrayList<HashMap<Integer, Integer>> getConnMap() {
        ArrayList<PotentialSynapse> conns = getConns();
        ArrayList<HashMap<Integer, Integer>> connMaps = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            connMaps.add(new HashMap<Integer, Integer>());
        }
        for (PotentialSynapse conn : conns) {
            int mapKey = Com.getMapKey(conn.getFwdGlu(), conn.getRevGlu(), conn.getDist());
            for (int div = 8; div >= conn.getDiv(); div--) {
                Com.sAdd(connMaps.get(div - 5), mapKey);
            }
        }
        return connMaps;
    }

    private ArrayList<HashMap<Integer, Integer>> getSlotMap() {
        ArrayList<HashMap<Integer, Integer>> slotsMaps = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            slotsMaps.add(new HashMap<Integer, Integer>());
        }
        ArrayList<PotentialSynapse> slots = getSlots();

        for (PotentialSynapse conn : slots) {
            int mapKey = Com.getMapKey(conn.getFwdGlu(), conn.getRevGlu(), conn.getDist());
            allSlot.add(mapKey);
            for (int div = 8; div >= conn.getDiv(); div--) {
                Com.sAdd(slotsMaps.get(div - 5), mapKey);
            }
        }
        return slotsMaps;
    }

    public ArrayList<HashMap<Integer, Float>> getPBase() {
        ArrayList<HashMap<Integer, Float>> pBases = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            pBases.add(new HashMap<Integer, Float>());
        }

        ArrayList<HashMap<Integer, Integer>> slotsMaps = getSlotMap();
        ArrayList<HashMap<Integer, Integer>> connsMaps = getConnMap();
        for (int div = 0; div < 4; div++) {
            for (Integer i : allSlot) {
                float ratio = (connsMaps.get(div).containsKey(i)
                        ? ((float) connsMaps.get(div).get(i) / slotsMaps.get(div).get(i))
                        : 0);
                pBases.get(div).put(i, ratio);
            }
        }
        return pBases;
    }

    public int[] getGrps() {
        int[] grps = new int[2];
        String jdbcPath = "jdbc:odbc:DRIVER="
                + "Microsoft Access Driver (*.mdb, *.accdb);DBQ=" + this.pathToFile;
        try (Connection con = DriverManager.getConnection(jdbcPath)) {
            try (Statement stmt = con.createStatement(
                    ResultSet.TYPE_FORWARD_ONLY,
                    ResultSet.CONCUR_READ_ONLY)) {
                String qryStr = "SELECT qGroupSizenCell.Size, Count(qGroupSizenCell.GroupNo) AS Sum "
                        + "FROM qGroupSizenCell "
                        + "GROUP BY qGroupSizenCell.Size;";
                ResultSet rs = stmt.executeQuery(qryStr);
                while (rs.next()) {
                    int size = rs.getInt(1);
                    if (size == 3) {
                        grps[0] = rs.getInt(2);
                    } else if (size == 4) {
                        grps[1] = rs.getInt(2);
                    }
                }
            }

        } catch (SQLException e) {
            System.out.println(e.toString());
        }
        return grps;
    }
}
