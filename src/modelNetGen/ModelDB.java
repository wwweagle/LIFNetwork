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
import java.util.Set;

/**
 *
 * @author Libra
 */
public class ModelDB {

    final private String pathToFile;
    final private HashSet<Integer> allSlot = new HashSet<>();

    public ModelDB(String pathToFile) {
        this.pathToFile = pathToFile;
    }

    private ArrayList<PotentialSynapse> getSlots() {
        ArrayList<PotentialSynapse> slots = new ArrayList<>(500);
        String jdbcPath = "jdbc:ucanaccess://" + this.pathToFile;
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
        String jdbcPath = "jdbc:ucanaccess://" + this.pathToFile;
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

    protected void clusterCoef() {
        String jdbcPath = "jdbc:ucanaccess://" + this.pathToFile;

        HashMap<java.sql.Date, HashSet<Integer>> connected = new HashMap();
        HashMap<java.sql.Date, HashSet<Integer>> cells = new HashMap<>();
        try (Connection con = DriverManager.getConnection(jdbcPath)) {
            try (Statement stmt = con.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY)) {
                String qryStr = "SELECT tblConnection.PreCellID, tblConnection.PostCellID, tblGADCell.CultureDate \n"
                        + "FROM tblConnection INNER JOIN tblGADCell ON tblConnection.PreCellID = tblGADCell.CellID;";
                ResultSet rs = stmt.executeQuery(qryStr);
                while (rs.next()) {
                    int pre = rs.getInt(1);
                    int post = rs.getInt(2);
                    java.sql.Date date = rs.getDate(3);
                    if (!cells.containsKey(date)) {
                        cells.put(date, new HashSet<>());
                        connected.put(date, new HashSet<>());
                    }
                    cells.get(date).add(pre);
                    cells.get(date).add(post);
                    connected.get(date).add(Com.getSetKey(pre, post));
                }
            }
        } catch (SQLException e) {
            System.out.println(e.toString());
        }

        ////////////////////////////////////////////
        int groupCount = 0;
        int connedTriplets = 0;
        int triangle = 0;
        for (java.sql.Date date : cells.keySet()) {
            if (groupCount % 3 == 0) {
                connedTriplets = 0;
                triangle = 0;
            }
            int connCount;
            ArrayList<Integer> cellList = new ArrayList<>(cells.get(date));
            Set<Integer> conns = connected.get(date);
            for (int i = 0; i < cellList.size(); i++) {
//                System.out.println(i + ",");
                for (int j = i + 1; j < cellList.size(); j++) {
                    for (int k = j + 1; k < cellList.size(); k++) {
                        int ii = cellList.get(i);
                        int jj = cellList.get(j);
                        int kk = cellList.get(k);
                        connCount = 0;
                        connCount += (conns.contains(Com.getSetKey(ii, jj)) || conns.contains(Com.getSetKey(jj, ii))) ? 1 : 0;
                        connCount += (conns.contains(Com.getSetKey(jj, kk)) || conns.contains(Com.getSetKey(kk, jj))) ? 1 : 0;
                        connCount += (conns.contains(Com.getSetKey(ii, kk)) || conns.contains(Com.getSetKey(kk, ii))) ? 1 : 0;

                        switch (connCount) {
                            case 2:
                                connedTriplets++;
                                break;
                            case 3:
                                connedTriplets += 3;
                                triangle += 3;
                                break;
                        }
                    }
                }
            }
            if (groupCount % 3 == 2) {
                System.out.print(date.toString() + ",");
                System.out.println((double) triangle / connedTriplets);
            }
            groupCount++;
        }
    }

    private ArrayList<HashMap<Integer, Integer>> getConnMap() {
        ArrayList<PotentialSynapse> conns = getConns();
        ArrayList<HashMap<Integer, Integer>> connMaps = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            connMaps.add(new HashMap<>());
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
            slotsMaps.add(new HashMap<>());
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
            pBases.add(new HashMap<>());
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
        String jdbcPath = "jdbc:ucanaccess://" + this.pathToFile;
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
