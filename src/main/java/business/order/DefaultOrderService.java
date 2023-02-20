package business.order;

import api.ApiException;
import business.BookstoreDbException;
import business.JdbcUtils;
import business.book.Book;
import business.book.BookDao;
import business.cart.ShoppingCart;
import business.cart.ShoppingCartItem;
import business.customer.Customer;
import business.customer.CustomerDao;
import business.customer.CustomerForm;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.YearMonth;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class DefaultOrderService implements OrderService {

	private BookDao bookDao;
	private OrderDao orderDao;
	private LineItemDao lineItemDao;
	private CustomerDao customerDao;
	private Random random = new Random();

	public void setBookDao(BookDao bookDao) {
		this.bookDao = bookDao;
	}

	@Override
	public OrderDetails getOrderDetails(long orderId) {
		Order order = orderDao.findByOrderId(orderId);
		Customer customer = customerDao.findByCustomerId(order.getCustomerId());
		List<LineItem> lineItems = lineItemDao.findByOrderId(orderId);
		List<Book> books = lineItems
				.stream()
				.map(lineItem -> bookDao.findByBookId(lineItem.getBookId()))
				.collect(Collectors.toList());
		return new OrderDetails(order, customer, lineItems, books);
	}

	@Override
    public long placeOrder(CustomerForm customerForm, ShoppingCart cart) {

		validateCustomer(customerForm);
		validateCart(cart);

		try (Connection connection = JdbcUtils.getConnection()) {
			Date date = getDate(
					customerForm.getCcExpiryMonth(),
					customerForm.getCcExpiryYear());
			return performPlaceOrderTransaction(
					customerForm.getName(),
					customerForm.getAddress(),
					customerForm.getPhone(),
					customerForm.getEmail(),
					customerForm.getCcNumber(),
					date, cart, connection);
		} catch (SQLException e) {
			throw new BookstoreDbException("Error during close connection for customer order", e);
		}

	}
	private Date getDate(String monthString, String yearString) {
		int month = Integer.parseInt(monthString);
		int year = Integer.parseInt(yearString);
		return new Date(year-1900, month-1, 9);
	}
	private long performPlaceOrderTransaction(
			String name, String address, String phone,
			String email, String ccNumber, Date date,
			ShoppingCart cart, Connection connection) {
		try {
			connection.setAutoCommit(false);
			long customerId = customerDao.create(
					connection, name, address, phone, email,
					ccNumber, date);
			long customerOrderId = orderDao.create(
					connection,
					cart.getComputedSubtotal() + cart.getSurcharge(),
					generateConfirmationNumber(), customerId);
			for (ShoppingCartItem item : cart.getItems()) {
				lineItemDao.create(connection, customerOrderId, item.getBookId(),
						item.getQuantity());
			}
			connection.commit();
			return customerOrderId;
		} catch (Exception e) {
			try {
				connection.rollback();
			} catch (SQLException e1) {
				throw new BookstoreDbException("Failed to roll back transaction", e1);
			}
			return 0;
		}
	}


	private void validateCustomer(CustomerForm customerForm) {

    	String name = customerForm.getName();
		String address = customerForm.getAddress();
		String phone = customerForm.getPhone();
		String email = customerForm.getEmail();
		String ccNumber = customerForm.getCcNumber();
		String ccExpiryMonth = customerForm.getCcExpiryMonth();
		String ccExpiryYear = customerForm.getCcExpiryYear();

		if (name == null || name.equals("") || name.length() > 45) {
			throw new ApiException.InvalidParameter("Invalid name field");
		}

		if (address == null || address.equals("") || address.length() > 45) {
			throw new ApiException.InvalidParameter("Invalid address field");
		}

		String pattern = "^\\s*(?:\\+?(\\d{1,3}))?[-. (]*(\\d{3})[-. )]*(\\d{3})[-. ]*(\\d{4})(?: *x(\\d+))?\\s*$";

		Pattern pat = Pattern.compile(pattern);
		Matcher mat = pat.matcher(phone);
		if (!mat.matches()) {
			throw new ApiException.InvalidParameter("Invalid phone number");
		}

		String emailPattern = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9]))\\.){3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";
		Pattern em_pat = Pattern.compile(emailPattern);
		Matcher em_mat = em_pat.matcher(email);
		if (!em_mat.matches() || email == null) {
			throw new ApiException.InvalidParameter("Invalid email address");
		}

		String ccNumberPattern = "^(?:4[0-9]{12}(?:[0-9]{3})?|[25][1-7][0-9]{14}|6(?:011|5[0-9][0-9])[0-9]{12}|3[47][0-9]{13}|3(?:0[0-5]|[68][0-9])[0-9]{11}|(?:2131|1800|35\\d{3})\\d{11})$";
		Pattern cc_pat = Pattern.compile(ccNumberPattern);
		Matcher cc_mat = cc_pat.matcher(ccNumber);
		if (!cc_mat.matches() || ccNumber == null) {
			throw new ApiException.InvalidParameter("Invalid credit card number");
		}

		if (expiryDateIsInvalid(ccExpiryMonth, ccExpiryYear)) {
			throw new ApiException.InvalidParameter("Invalid expiry date");

		}
	}

	private boolean expiryDateIsInvalid(String ccExpiryMonth, String ccExpiryYear) {

		int expMonth = Integer.parseInt(ccExpiryMonth);
		int exYear = Integer.parseInt(ccExpiryYear);
		int currMonth = YearMonth.now().getMonthValue();
		int currYear = YearMonth.now().getYear();
		if (currYear>exYear) {
			return true;
		}
		else if (currYear==exYear){
			return currMonth > expMonth;
		}
		else {
			return false;
		}

	}

	private void validateCart(ShoppingCart cart) {

		if (cart.getItems().size() <= 0) {
			throw new ApiException.InvalidParameter("Cart is empty.");
		}

		cart.getItems().forEach(item-> {
			if (item.getQuantity() < 0 || item.getQuantity() > 99) {
				throw new ApiException.InvalidParameter("Invalid quantity for book ID: " + item.getBookId());
			}
			Book databaseBook = bookDao.findByBookId(item.getBookId());
			if (item.getBookForm().getPrice() != databaseBook.getPrice()) {
				throw new ApiException.InvalidParameter("Invalid price for book ID: " + item.getBookId());
			}
			if (item.getBookForm().getCategoryId() != databaseBook.getCategoryId()) {
				throw new ApiException.InvalidParameter("Book ID: " + item.getBookId() + " does not belong to Category ID: "+item.getBookForm().getCategoryId());
			}
		});
	}
	private int generateConfirmationNumber() {
		return random.nextInt(999999999);
	}

	public void setOrderDao(OrderDao orderDao) {
		this.orderDao = orderDao;
	}

	public void setLineItemDao(LineItemDao lineItemDao) {
		this.lineItemDao = lineItemDao;
	}

	public void setCustomerDao(CustomerDao customerDao) {
		this.customerDao = customerDao;
	}

}
