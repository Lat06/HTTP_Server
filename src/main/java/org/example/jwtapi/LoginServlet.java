package org.example.jwtapi;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        JsonObject json = JsonParser.parseReader(req.getReader()).getAsJsonObject();
        String login = json.get("login").getAsString();
        String password = json.get("password").getAsString();

        if ("admin".equals(login) && "1234".equals(password)) {
            String token = JwtUtil.createToken(login);
            res.setContentType("application/json");
            res.getWriter().write(new Gson().toJson(java.util.Map.of("token", token)));
        } else {
            res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
        }
    }
}
