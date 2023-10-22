import java.sql.*;
import java.util.Properties;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws SQLException {
        Properties properties = new Properties();

        properties.setProperty("user", "test_user");
        properties.setProperty("password", "test_password");

        String connectionUrl = "jdbc:mysql://localhost:3306/cafeup";

        try(Connection connection = DriverManager.getConnection(connectionUrl, properties);
        Statement statement = connection.createStatement()) {
            String insertSQL = "INSERT INTO dishes (name) VALUES (?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {
                Scanner scanner = new Scanner(System.in);
                System.out.println("Введіть назву страви:");
                String dishName = scanner.nextLine();
                preparedStatement.setString(1, dishName);
                preparedStatement.executeUpdate();
                System.out.println("Дані успішно вставлені");
            }

            ResultSet resultSet = statement.executeQuery("SELECT * FROM dishes");

            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            System.out.println("Column count: " + columnCount);
            for (int i = 1; i <= columnCount; i++) {
                System.out.println("Column name: " + metaData.getColumnName(i));
                System.out.println("Column type: " + metaData.getColumnTypeName(i));
                System.out.println("Is nullable: " + (metaData.isNullable(i) == ResultSetMetaData.columnNullable));
                System.out.println("Is auto increment: " + metaData.isAutoIncrement(i));
                System.out.println("Is case sensitive: " + metaData.isCaseSensitive(i));
                System.out.println("Is writable: " + metaData.isWritable(i));
                System.out.println("Is searchable: " + metaData.isSearchable(i));
                System.out.println("Column display size: " + metaData.getColumnDisplaySize(i));
                System.out.println("Catalog name: " + metaData.getCatalogName(i));
                System.out.println("Schema name: " + metaData.getSchemaName(i));
                System.out.println("------------------------");
            }

            //INSERT

            statement.executeUpdate("insert into customers (name, date_of_birth, favorite_dish_id) values ('Микола Васильович', '1990-01-01', '2')");
            statement.executeUpdate("insert into employers (name, date_of_birth) values ('Петро Бариста', '1990-01-01')");
            statement.executeUpdate("insert into order_history (customer_id, dish_id, employee_id, date, price) values ('1', '1', '1', '2023-01-01', '588')");

            String queryAboutCustomer = "SELECT c.name, c.date_of_birth, d.name as favorite_dish " +
                    "FROM customers c " +
                    "JOIN dishes d ON c.favorite_dish_id = d.id";
            ResultSet rs = statement.executeQuery(queryAboutCustomer);

            while (rs.next()) {
                String customerName = rs.getString("name");
                String customerDod = rs.getString("date_of_birth");
                String favoriteDish = rs.getString("favorite_dish");
                System.out.println("Customer: " + customerName + " , Date of Birth: " + customerDod + ", Favorite Dish: " + favoriteDish);
            }

            String selectOrdersSql = "SELECT oh.id, c.name AS customer_name, d.name AS dish_name, e.name AS employee_name, oh.date, oh.price " +
                    "FROM order_history oh " +
                    "JOIN customers c ON oh.customer_id = c.id " +
                    "JOIN dishes d ON oh.dish_id = d.id " +
                    "JOIN employers e ON oh.employee_id = e.id";

            //UPDATE
            String upQuery = "UPDATE employers SET name = 'Ivan' WHERE id = 1";
            statement.executeUpdate(upQuery);

            ResultSet resOrder = statement.executeQuery(selectOrdersSql);

            System.out.println();
            // Display data
            while (resOrder.next()) {
                int orderId = resOrder.getInt("id");
                String customerName =  resOrder.getString("customer_name");
                String dishName = resOrder.getString("dish_name");
                String employeeName = resOrder.getString("employee_name");
                String date = resOrder.getString("date");
                float price = resOrder.getFloat("price");

                System.out.println("Order ID: " + orderId +
                        ", Customer Name: " + customerName +
                        ", Dish Name: " + dishName +
                        ", Employee Name: " + employeeName +
                        ", Date: " + date +
                        ", Price: " + price);
            }

            System.out.println();
            System.out.println("Пошук замовлення за стравою");
            int dishIdToSearch = 5; // Replace with the actual dish ID you want to search for

            String searchOrdersByDishSql = "SELECT oh.id, c.name AS customer_name, d.name AS dish_name, e.name AS employee_name, oh.date, oh.price " +
                    "FROM order_history oh " +
                    "JOIN customers c ON oh.customer_id = c.id " +
                    "JOIN dishes d ON oh.dish_id = d.id " +
                    "JOIN employers e ON oh.employee_id = e.id " +
                    "WHERE oh.dish_id = ?";

            PreparedStatement pstmtDish = connection.prepareStatement(searchOrdersByDishSql);
            pstmtDish.setInt(1, dishIdToSearch);
            ResultSet OrderByDish = pstmtDish.executeQuery();

            // Display data
            while (OrderByDish.next()) {
                int orderId = OrderByDish.getInt("id");
                String customerName = OrderByDish.getString("customer_name");
                String dishName = OrderByDish.getString("dish_name");
                String employeeName = OrderByDish.getString("employee_name");
                String date = OrderByDish.getString("date");
                float price = OrderByDish.getFloat("price");

                System.out.println("Order ID: " + orderId +
                        ", Customer Name: " + customerName +
                        ", Dish Name: " + dishName +
                        ", Employee Name: " + employeeName +
                        ", Date: " + date +
                        ", Price: " + price);
            }

            //DELETE

            int orderIdToDelete = 1;

            String deleteOrderSql = "DELETE FROM order_history WHERE id = ?";

            PreparedStatement pstmt = connection.prepareStatement(deleteOrderSql);
            pstmt.setInt(1, orderIdToDelete);

            int rowsDeleted = pstmt.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Order with ID " + orderIdToDelete + " was deleted successfully!");
            } else {
                System.out.println("No order found with ID " + orderIdToDelete + ". No changes were made.");
            }

            }
        }
    }