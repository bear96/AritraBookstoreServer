
package business;

import business.book.BookDao;
import business.book.BookDaoJdbc;
import business.category.CategoryDao;
import business.category.CategoryDaoJdbc;
import business.customer.CustomerDao;
import business.customer.CustomerDaoJdbc;
import business.order.*;

public class ApplicationContext {

    // TODO Add field and complete the getter for bookDao

    private CategoryDao categoryDao;
    private BookDao bookDao;
    private LineItemDao lineItemDao;
    private OrderDao orderDao;
    private CustomerDao customerDao;

    private OrderService orderService;

    public static ApplicationContext INSTANCE = new ApplicationContext();

    private ApplicationContext() {
        categoryDao = new CategoryDaoJdbc();
        bookDao = new BookDaoJdbc();
        customerDao = new CustomerDaoJdbc();
        orderDao = new OrderDaoJdbc();
        lineItemDao = new LineItemDaoJdbc();
        orderService = new DefaultOrderService();
        ((DefaultOrderService) orderService).setBookDao(bookDao);
        ((DefaultOrderService) orderService).setOrderDao(orderDao);
        ((DefaultOrderService) orderService).setCustomerDao(customerDao);
        ((DefaultOrderService) orderService).setLineItemDao(lineItemDao);
    }

    public CategoryDao getCategoryDao() {
        return categoryDao;
    }

    public BookDao getBookDao() {
        return this.bookDao;
    }

    public OrderService getOrderService() { return this.orderService;}
}
