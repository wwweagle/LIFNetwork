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
    static final private int[] gluGluPSCs = {3, 3, 3, 4, 5, 5, 5, 5, 5, 5, 5, 6, 6, 6, 7, 7, 8, 8, 8, 8, 9, 9, 10, 10, 10, 10, 10, 11, 11, 11, 11, 11, 12, 12, 13, 13, 13, 14, 14, 14, 14, 15, 15, 16, 16, 17, 17, 17, 17, 17, 18, 18, 18, 18, 19, 19, 20, 20, 20, 21, 22, 23, 24, 25, 25, 25, 26, 27, 27, 30, 32, 32, 35, 38, 38, 43, 44, 47, 47, 48, 48, 49, 50, 50, 51, 52, 54, 60, 64, 65, 68, 76, 77, 96, 99, 110, 120, 177, 205, 220};
    static final private int[] gluGABAPSCs = {3, 3, 3, 5, 5, 5, 5, 6, 6, 7, 7, 8, 8, 9, 9, 10, 10, 11, 11, 12, 15, 15, 16, 17, 17, 18, 19, 20, 20, 20, 21, 21, 22, 24, 25, 25, 25, 25, 26, 26, 27, 27, 29, 29, 29, 29, 30, 30, 30, 31, 32, 32, 32, 32, 32, 32, 32, 33, 33, 33, 34, 34, 34, 35, 35, 36, 36, 36, 36, 37, 38, 38, 38, 38, 39, 41, 41, 42, 42, 44, 45, 45, 45, 47, 47, 47, 47, 48, 50, 50, 50, 50, 50, 50, 51, 52, 53, 53, 53, 53, 53, 53, 54, 54, 55, 55, 55, 55, 55, 56, 58, 58, 59, 60, 61, 62, 62, 64, 64, 64, 64, 64, 64, 65, 66, 66, 67, 69, 70, 70, 70, 73, 74, 75, 75, 75, 76, 77, 77, 79, 80, 82, 82, 82, 83, 83, 86, 86, 86, 87, 87, 88, 89, 89, 91, 91, 93, 94, 96, 97, 97, 98, 99, 100, 100, 100, 106, 107, 110, 110, 110, 111, 111, 114, 115, 115, 120, 121, 121, 123, 123, 123, 124, 128, 131, 131, 131, 133, 136, 138, 145, 148, 150, 151, 152, 155, 156, 159, 166, 168, 170, 170, 170, 175, 177, 178, 181, 186, 186, 192, 193, 200, 204, 209, 215, 226, 229, 231, 240, 248, 250, 255, 263, 264, 273, 279, 289, 295, 299, 303, 304, 311, 313, 317, 330, 335, 338, 345, 347, 351, 370, 374, 383, 388, 400, 410, 411, 414, 430, 446, 457, 508, 521, 560, 579, 605, 609, 640, 688, 722, 730, 732, 851, 898, 981};
    static final private int[] gabaGluPSCs = {3, 3, 4, 4, 4, 4, 4, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 6, 6, 6, 6, 6, 6, 7, 7, 7, 7, 8, 8, 8, 8, 8, 8, 9, 9, 9, 9, 9, 9, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 11, 11, 12, 12, 12, 13, 13, 13, 13, 13, 14, 14, 14, 14, 14, 15, 15, 15, 15, 15, 16, 16, 16, 16, 17, 18, 19, 19, 19, 20, 20, 20, 20, 20, 20, 20, 20, 21, 22, 22, 22, 23, 24, 24, 24, 24, 24, 25, 25, 26, 27, 28, 28, 28, 29, 30, 30, 31, 31, 32, 33, 33, 33, 33, 34, 34, 34, 34, 35, 35, 35, 36, 37, 37, 38, 38, 38, 39, 39, 41, 41, 44, 44, 44, 44, 45, 46, 46, 46, 47, 47, 48, 50, 51, 51, 52, 53, 53, 54, 55, 56, 57, 57, 59, 61, 61, 62, 62, 63, 65, 65, 69, 71, 72, 73, 74, 75, 75, 78, 79, 79, 80, 80, 81, 81, 82, 83, 84, 85, 85, 85, 88, 88, 88, 88, 89, 89, 90, 93, 95, 96, 97, 98, 100, 100, 105, 108, 115, 115, 115, 117, 117, 120, 120, 121, 125, 125, 127, 127, 127, 128, 130, 134, 134, 136, 136, 140, 141, 145, 147, 148, 148, 149, 150, 150, 155, 155, 159, 162, 165, 169, 172, 177, 179, 181, 195, 196, 197, 198, 204, 205, 208, 210, 213, 213, 224, 233, 238, 239, 246, 250, 252, 253, 263, 267, 268, 268, 273, 274, 278, 299, 340, 342, 345, 345, 350, 357, 368, 368, 370, 370, 375, 386, 386, 415, 432, 445, 452, 456, 460, 475, 484, 498, 520, 522, 551, 687, 714};
    static final private int[] gabaGABAPSCs = {3, 3, 3, 3, 4, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 6, 6, 7, 7, 9, 9, 9, 9, 10, 10, 10, 11, 11, 11, 11, 12, 12, 12, 12, 13, 14, 14, 14, 15, 15, 16, 17, 18, 18, 19, 19, 19, 20, 20, 20, 20, 21, 22, 22, 24, 24, 25, 25, 25, 25, 25, 26, 26, 27, 27, 27, 29, 29, 30, 31, 32, 32, 32, 32, 33, 33, 34, 35, 35, 35, 35, 41, 42, 42, 45, 45, 49, 50, 51, 51, 51, 54, 54, 55, 57, 57, 60, 62, 64, 64, 64, 64, 66, 69, 70, 70, 72, 74, 76, 76, 76, 77, 79, 79, 82, 84, 85, 85, 86, 86, 87, 88, 88, 92, 93, 93, 93, 93, 97, 98, 101, 101, 102, 104, 109, 112, 118, 120, 120, 123, 125, 130, 137, 141, 147, 147, 156, 162, 170, 178, 182, 183, 190, 193, 200, 202, 205, 205, 213, 216, 217, 227, 228, 232, 239, 255, 260, 264, 265, 267, 273, 280, 306, 314, 322, 406, 441, 478, 480, 481, 572, 626, 645, 826, 840, 960};

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
                        cells.put(date, new HashSet<Integer>());
                        connected.put(date, new HashSet<Integer>());
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

    static public float getDrivingForce(boolean preIsGlu, boolean postIsGlu, float position) {
        switch ((preIsGlu ? 0 : 1) + (postIsGlu ? 0 : 2)) {
            case 0:
                return gluGluPSCs[(int) (gluGluPSCs.length * position)] / 65f;
            case 1:
                return gabaGluPSCs[(int) (gabaGluPSCs.length * position)] / 20f;
            case 2:
                return gluGABAPSCs[(int) (gluGABAPSCs.length * position)] / 65f;
            case 3:
                return gabaGABAPSCs[(int) (gabaGABAPSCs.length * position)] / 20f;
        }
        return 0;
    }
}
