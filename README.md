# Bookstore Inventory Management System
Web service that implements CRUD operations on Books using gRPC


## Setup:
1. Clone project.
2. Define datasource in application.properties (PostgreSQL database).
3. Build and start application.

## Run instructions and info:

Proto file path: src/main/proto

Default port: 9090

#### Available methods: 

- GetAllBooks()
- GetBookById(string id)
- AddBook(string title, string author, string isbn, int32 quantity)
- UpdateBook(string id, string title, string author, string isbn, int32 quantity)
- DeleteBook(string id)

