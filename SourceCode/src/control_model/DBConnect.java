package control_model;



import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JOptionPane;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Chester
 */
public class DBConnect 
{
    private static Connection connection = null;
    private static Statement statement = null;
    private static ResultSet rs = null;
    
    public Boolean DBConnect()
    {
        try
        {
            /* SQLite JDBC 클래스가 있는지 검사하는 부분 */
            Class.forName("org.sqlite.JDBC");
            /* Program.class와 같은 디렉터리에 있는 DB를 연다 */
            /* 제대로 동작하지 않아 일단 절대경로로 지정해놓음. 할수 있다면 상대경로로 하고 안된다면 절대경로를 사용할 것. */
            /* JAR 파일로 묶어 클라이언트에 제공 시에는 설치 경로를 정확히 해두어야 할 것. */
            connection = DriverManager.getConnection("jdbc:sqlite:EnglishPlay.db3");
            /* 연결 성공했을 때, connection으로부터 statement 인스턴스를 얻는다. statement를 통해 질의를 수행한다. */
            statement = connection.createStatement();
        }
        catch(ClassNotFoundException e)
        {
            System.out.println("org.sqlite.JDBC : Do not find");
            return false;
        }
        
        catch(SQLException e)
        {
            System.out.println("SQL Exception : " + e.getMessage());
            return false;
        }
        
        catch(Exception e)
        {
            return false;
        }
        
        return true;
    }
    
    public static WordInfo query(int key)
    {
        WordInfo word_info = new WordInfo();
        StringBuffer sql_temp = new StringBuffer();
        String sql = "";
        
        sql_temp.append("SELECT * FROM WordInfo WHERE key = ");
        sql_temp.append(key);
        
        sql = sql_temp.toString();
        
        try 
        {
            rs = statement.executeQuery(sql);
            
            word_info.key = key;
            word_info.word = rs.getString("word");
            word_info.def = rs.getString("def");
            word_info.phonics = rs.getString("phonix");
            word_info.sound_path = rs.getString("sound_path");
            word_info.img_path = rs.getString("image_path");
        } 
        catch (SQLException e) 
        {
            System.out.println("SQL Exception : " + e.getMessage());
        }
        
        return word_info;
    }
}
