package controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import dao.BookDao;
import model.Book;


public class BookController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public static String INSERT_OR_EDIT = "/book.jsp";
	public static String LIST_BOOK = "/dashboard-book.jsp";
	public static String BOOK_DISPLAY= "/display-books.jsp";
	private BookDao dao;
	
	public BookController() {
		super(); 
		dao = new BookDao();
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String forward = "";
		String action = request.getParameter("action");
		
		if (action.equalsIgnoreCase("delete")) {
				int bookId = Integer.parseInt(request.getParameter("bookId"));
				dao.deleteBook(bookId);
				forward = LIST_BOOK;
				request.setAttribute("books", dao.getAllBooks());
		}
		else if (action.equalsIgnoreCase("edit")) {
			forward = INSERT_OR_EDIT;
			int bookId = Integer.parseInt(request.getParameter("bookId"));
			Book book = dao.getBookById(bookId);
			request.setAttribute("book", book);
		}
		else if (action.equalsIgnoreCase("listBook")){
			forward = LIST_BOOK;
			request.setAttribute("books", dao.getAllBooks());
		}
		else if (action.equalsIgnoreCase("bookDisplay")){
			forward = BOOK_DISPLAY;
			request.setAttribute("books", dao.getAllBooks());
		}
		else {
			forward = INSERT_OR_EDIT;
		}
		
		RequestDispatcher view = request.getRequestDispatcher(forward);
		view.forward(request, response);
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Book book = new Book();
		book.setAuthor(request.getParameter("author"));
		book.setCategory(request.getParameter("category"));
		book.setTitle(request.getParameter("title"));
		String inventory = request.getParameter("inventory");
		if (inventory == null || inventory.isEmpty()){
			book.setInventory(0);
		}
		else{
			int inv = Integer.parseInt(inventory);
			book.setInventory(inv);
		}
		String rating = request.getParameter("reviewRating");
		if (rating == null || rating.isEmpty()){
			book.setReviewRating(0);	
		}
		else{
			int rate = Integer.parseInt(rating);
			book.setReviewRating(rate);
		}
		
		book.setPrice(Double.parseDouble(request.getParameter("price"))); //TODO: may need validation
		book.setYearPublished(request.getParameter("publicationYear"));
		book.setPublisher(request.getParameter("publisher"));
		InputStream inputStream = null; // input stream of the upload file
        
        // obtains the upload file part in this multipart request
        Part filePart = request.getPart("photoUrl");
        if (filePart != null) {
            // prints out some information for debugging
            System.out.println(filePart.getName());
            System.out.println(filePart.getSize());
            System.out.println(filePart.getContentType());
             
            // obtains input stream of the upload file
            inputStream = filePart.getInputStream();
        }
         
		System.out.println(filePart);
		if (filePart == null || filePart.toString().isEmpty()){
			//do nothing
		}
		else {
			//File file = new File("/Users/tazzledazzle/Pictures/"+photoUrl);
//			FileInputStream inputStream = new FileInputStream(file);
			book.setPhoto((Blob)inputStream);
		}
		String bookId = request.getParameter("bookId");
		if(bookId == null || bookId.isEmpty()){
			dao.addBook(book);
		}
		else
		{
			book.setBookId(Integer.parseInt(bookId));
			dao.updateBook(book);
		}
		
		RequestDispatcher view = request.getRequestDispatcher(LIST_BOOK);
		request.setAttribute("books", dao.getAllBooks());
		view.forward(request, response);
	}

}
