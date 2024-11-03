package com.example.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(urlPatterns = {"/my-servlet"})
public class MyServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(MyServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        logger.info("Metoda doGet została uruchomiona");

        doAction(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        logger.info("Metoda doPost została uruchomiona");

        doAction(request, response);
    }

    private void doAction(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String argument = request.getParameter("name");
        if (argument == null || argument.isEmpty()) {
            argument = "nieznajomy";
        }

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        String jsonResponse = String.format("{\"message\": \"Witaj %s!!!\"}", argument);

        PrintWriter out = response.getWriter();
        out.print(jsonResponse);
        out.flush();
    }
}