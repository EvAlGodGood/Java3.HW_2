import javax.swing.*;//подключен только для окна ловящего эксцепшн (чтобы прога не падала =)) можно и умнее обработать)
import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.*;
//Сделать методы для работы с БД (CREATE, UPDATE, DELETE, INSERT, SELECT)
public class Main {
    private static Connection connection;
    private static Statement stmt;
    //private static PreparedStatement pstmt;


    public static void main(String[] args) throws SQLException {

        try {//подключились
            connect();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        dropTable("students"); //удаляем таблицу если она есть

        String str = "CREATE TABLE students (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, score TEXT);";
        creTable(str); //создаем таблицу изменяя str можно создать сколько угодно таблиц с требуемой структурой

        cleanScript(); //очистка

        addScript("Bob1","80"); //вносим тестовые данные
        addScript("Bob2","81");
        addScript("Bob3","47");
        addScript("Bob4","82");
        addScript("Alex","65");
        addScript("Bob5","82");

        System.out.println("Внесенные первичные данные");
        printTabl(); //проверяем таблицу

        delScript("Alex"); //пробуем удалить отдельную строку по name
        updateScript("Bob5","69");

        System.out.println("Данные после удаления записи и изменения Bob5");
        printTabl(); //проверяем таблицу

        readInsert("C:\\java_3\\HW_2\\src\\DZ_update.txt");//читаем файл и вносим изменения в базу

        System.out.println("Данные после обновления данных из файла");
        printTabl(); //проверяем таблицу

        disconnect();//отключились
    }

    public static void connect() throws ClassNotFoundException, SQLException { //подключение базы
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:StudentsTest.db");
        stmt = connection.createStatement();
    }

    public static void disconnect() { //отключение базы
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void printTabl() throws SQLException { //вывод всей таблицы.
        ResultSet rs = stmt.executeQuery("SELECT id, name, score FROM students");

        while (rs.next()){
            System.out.println(rs.getInt(1) + " \t" + rs.getString("name") + " \t"
                    + rs.getString("score"));
        }
    }

    private static void addScript(String name, String score) throws SQLException { //добавление записи
        stmt.executeUpdate(String.format("INSERT INTO students (name, score) VALUES ('%s', %s);", name, score));
    }

    private static void delScript(String name) throws SQLException { //удаление записи по name можно усложнять условие по AND и т.д.
        stmt.executeUpdate(String.format("DELETE FROM students WHERE name = '%s';", name));
    }

    private static void cleanScript() throws SQLException { //удаление всех записей в таблице
        stmt.executeUpdate("DELETE FROM students;");
        //все методы можно сделать более универсальными передавая параметры и создавая перегруженные методы
    }

    private static void updateScript(String name, String score) throws SQLException { //обновление записи
        stmt.executeUpdate(String.format("UPDATE students SET score = %s WHERE name = '%s';",score, name));
    }

    private static void creTable(String strInit) throws SQLException { //создание/добавление таблицы
        stmt.executeUpdate(strInit);
    }

    private static void dropTable(String nameT) throws SQLException {  //удаление таблицы
        stmt.executeUpdate("DROP TABLE IF EXISTS " + nameT + ";");
    }

    private static void readInsert(String path) {  //чтение из файла/добавление в базу (не дробя ¯\_(ツ)_/¯)
        try{
            String st;

            FileReader fr = new FileReader(path);    //читаем
            BufferedReader br = new BufferedReader(fr);         //в буфер

            while ((st = br.readLine())!=null) {//пока в буфере есть строки
                String el[] = st.split("\\s* ");//разбиваем по пробелу с тримом

                if ("0123456789".contains(el[0].substring(0,1))){//если первый символ строки сод. цифру
                    updateScript(el[1],el[2]);//выполняем обновление данных
                }
            }

            fr.close(); //высвобождаем
            br.close(); //ресурсы
        }
        catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Проблема в методе readInsert");
        }
    }
    //можно делать кучи проверок и условий, усложнять не стал общий принцип вполне понятен
}