package com.myproject.bookwebservice;


import com.myproject.bookwebservice.service.BookServiceImpl;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
@TestPropertySource(properties = {
        "grpc.server.port=6565",
        "spring.datasource.url=jdbc:tc:postgresql:///testdb",
        "spring.datasource.driver-class-name=org.testcontainers.jdbc.ContainerDatabaseDriver"
})
public class GrpcIntegrationTest {

    @Container
    private static final PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:latest");

    @Autowired
    private BookServiceImpl bookService;

    private static BookServiceGrpc.BookServiceBlockingStub bookServiceStub;

    private ManagedChannel channel;

    @BeforeEach
    public void setUp() {
        channel = ManagedChannelBuilder.forAddress("localhost", 6565)
                .usePlaintext()
                .build();
        bookServiceStub = BookServiceGrpc.newBlockingStub(channel);
    }

    @AfterEach
    public void tearDown() {
        channel.shutdownNow();
    }


}
