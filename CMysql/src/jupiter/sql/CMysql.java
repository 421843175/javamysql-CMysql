package jupiter.sql;

import java.sql.*;
import java.util.*;

public class CMysql {
    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        CMysql c=new CMysql("127.0.0.1","3306","root","0000","sx");
        LinkedHashMap<String, LinkedList<String>> select = c.select("select * from sx_use where id<10");
        System.out.println(select);
        c.upInDe("delete from sx_use where id=2");
        c.flush("sx_use");
        c.selectAll("select * from sx_use where id<10");
        System.out.println(c.existRow(c.isExist("yh","200001")));;
    }
    private Connection con=null;


    private Statement st=null;
   private  ResultSet rs=null;
   private ResultSetMetaData rsmd=null;
    private int colum;
    private LinkedHashMap<String, LinkedList<String>> lh=null;

    public Connection getCon() {
        return con;
    }

    public void setCon(Connection con) {
        this.con = con;
    }

    public Statement getSt() {
        return st;
    }

    public ResultSet getRs() {
        return rs;
    }

    public ResultSetMetaData getRsmd() {
        return rsmd;
    }

    public int getColum() {
        return colum;
    }

    public CMysql(String adress, String port, String username, String password, String database) throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        con=DriverManager.getConnection("jdbc:mysql://"+adress+":"+port+"/"+database+"?serverTimezone=GMT",username,password);
    }
    public CMysql(String adress,String port,String username,String password,String database,String time) throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        con=DriverManager.getConnection("jdbc:mysql://"+adress+":"+port+"/"+database+"?serverTimezone="+time,username,password);
    }

    public LinkedHashMap<String, LinkedList<String>> select(String sql) throws SQLException {
        LinkedHashMap<String, LinkedList<String>>  lh=new LinkedHashMap();

        st=con.createStatement();
        rs=st.executeQuery(sql);
        rsmd = rs.getMetaData();

        colum= rsmd.getColumnCount();
        LinkedList<String> ll=new LinkedList<>();
        rs=st.executeQuery(sql);
        for(int i=1;i<=colum;i++) {
            for (int j = 1; rs.next(); j++) {
                ll.add(rs.getString(i));
                lh.put(rsmd.getColumnName(i),ll);
            }
            ll=new LinkedList<>();
            rs=st.executeQuery(sql);
        }
        this.lh=lh;
        return lh;

        }

    /**
     *
     * @param lh
     * @param row:多少行
     * @param col:多少列
     * @return
     */
        public String selectOne(LinkedHashMap<String, LinkedList<String>> lh,String row,int col)
        {
         Set bl=lh.entrySet();
            LinkedList<String> s = lh.get(row);
            return s.get(col);
        }

    /**
     *插入修改删除
     * @param  sql：语句
     * @return 插入成功数据的行数
     * @throws SQLException
     */
        public int upInDe(String sql) throws SQLException {
            Statement st=con.createStatement();
            int result=st.executeUpdate(sql);
            return result;
        }

        //刷新表
        public void flush(String table) throws SQLException {
            select("select * from "+table);
        }

        public void selectAll(String sql) throws SQLException {
            rs=st.executeQuery(sql);
            for(int i=1;i<=colum;i++)
                System.out.printf("%15s",rsmd.getColumnName(i));
            System.out.println();
            while(rs.next())
            {
                for(int i=1;i<=colum;i++)
                    System.out.printf("%15s",rs.getString(i));
                System.out.println();
            }
        }

        public int isExist(String head,String str){
            LinkedList<String> s = lh.get(head);
            if(s==null)return -1;
            int i = s.indexOf(str);
            return i;

        }

        public LinkedHashMap<String,String> existRow(int i) throws SQLException {
            LinkedHashMap<String,String> als=new LinkedHashMap<String,String>();
            for(int j=1;j<=colum;j++) {
                LinkedList<String> li = lh.get(rsmd.getColumnName(j));
                als.put(rsmd.getColumnName(j),li.get(i));
            }
            return als;
        }

    protected void finalize() {
        if(rs!=null) {
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if(st!=null) {
            try {
                st.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
       if(con!=null){
           try {
               con.close();
           } catch (SQLException e) {
               e.printStackTrace();
           }
       }
    }

    }
